package com.smsspamguard.receiver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import libsvm.svm_node;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsMessage;
import android.util.Log;

import com.smsspamguard.R;
import com.smsspamguard.activity.Spams;
import com.smsspamguard.constant.Constants;
import com.smsspamguard.db.Database;
import com.smsspamguard.engine.svm.SvmManager;
import com.smsspamguard.engine.svm.core.SVMSpam;
import com.smsspamguard.model.Message;

public class SmsIntentReceiver extends BroadcastReceiver {

	private Database db;

	private boolean toggleApp;
	private boolean allowContacts;
	private boolean blockNonnumeric;
	private boolean blockAllcapital;
	private boolean toggleSvm;

	private boolean isBlacklisted = false;
	private boolean isWhitelisted = false;
	private boolean regexMatch = false;
	private boolean nonNumeric = false;
	private boolean allCapital = false;
	private boolean svmResult = false;
	
	private NotificationManager mNotificationManager;
	private int SIMPLE_NOTFICATION_ID = 1;
	private static ExecutorService executor = Executors.newSingleThreadExecutor();
	
	private class SpamThread implements Runnable {

		Context ctx;
		Database db;

		public SpamThread(Context ctx) {
			this.ctx = ctx;
		}

		@Override
		public void run() {

			db = new Database(ctx);
			Uri uri = Uri.parse("content://sms/inbox");
			Cursor cursor = ctx.getContentResolver().query(uri, new String[] { "_id" }, null, null, null);

			int before = cursor.getCount();
			Log.i("before", String.valueOf(before));

			while (before == cursor.getCount()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				cursor.requery();
			}
			Log.i("after", String.valueOf(cursor.getCount()));

			boolean unreadOnly = false;
			String SMS_READ_COLUMN = "read";
			String WHERE_CONDITION = unreadOnly ? SMS_READ_COLUMN + " = 0" : null;
			String SORT_ORDER = "date DESC";

			cursor = ctx.getContentResolver().query(uri, new String[] { "_id", "thread_id", "address", "person", "date", "body", "read" },
					WHERE_CONDITION, null, SORT_ORDER);

			cursor.moveToFirst();

			long messageId = cursor.getLong(0);
			long threadId = cursor.getLong(1);
			String address = cursor.getString(2);
			long contactId = cursor.getLong(3);
			long date = cursor.getLong(4);
			String messageBody = cursor.getString(5);

			Message message = new Message(messageId, threadId, address, contactId, date, messageBody);
			db.insertSpam(message);

			//System.out.println(messageId + " " + threadId + " " + address + " " + contactId + " " + date + " " + messageBody);

			ContentValues values = new ContentValues();
			values.put("read", 1);
			ctx.getContentResolver().update(Uri.parse("content://sms/conversations/" + threadId), values, "_id=?",
					new String[] { String.valueOf(messageId) });

			ctx.getContentResolver()
					.delete(Uri.parse("content://sms/conversations/" + threadId), "_id=?", new String[] { String.valueOf(messageId) });

			cursor.close();
			db.close();

			// display a notification for caught spam
			mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notifySpam = new Notification(R.drawable.ic_menu_add, "SpamGuarded!", System.currentTimeMillis());
			PendingIntent myIntent = PendingIntent.getActivity(ctx, 0, new Intent(ctx, Spams.class), 0);
			notifySpam.flags |= Notification.FLAG_AUTO_CANCEL;
			notifySpam.setLatestEventInfo(ctx, "SpamGuard", "Click to view spams", myIntent);
			mNotificationManager.notify(SIMPLE_NOTFICATION_ID, notifySpam);
		}
	}

	private SmsMessage[] getMessagesFromIntent(Intent intent) {
		SmsMessage retMsgs[] = null;
		Bundle bdl = intent.getExtras();
		try {
			Object pdus[] = (Object[]) bdl.get("pdus");
			retMsgs = new SmsMessage[pdus.length];
			for (int n = 0; n < pdus.length; n++) {
				byte[] byteData = (byte[]) pdus[n];
				retMsgs[n] = SmsMessage.createFromPdu(byteData);
			}

		} catch (Exception e) {
			Log.e("GetMessages", "fail", e);
		}
		return retMsgs;
	}

	private void checkLists(String type, String value) {
		Cursor c = db.searchList(value);
		if (c.getCount() > 0) {
			c.moveToFirst();
			if (c.getString(0).equals("b" + type + "t")) {
				isBlacklisted = true;
			} else if (c.getString(0).equals("w" + type + "t")) {
				isWhitelisted = true;
			}
		}
		if (c != null && !c.isClosed()) {
			c.close();
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		toggleApp = sp.getBoolean("toggle_spamguard", true);
		allowContacts = sp.getBoolean("allow_contacts", true);
		blockNonnumeric = sp.getBoolean("block_nonnumeric", false);
		blockAllcapital = sp.getBoolean("block_allcapital", false);
		toggleSvm = sp.getBoolean("toggle_svm", false);
		
		if (toggleApp) {
			if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {

				db = new Database(context);
				SmsMessage msg[] = getMessagesFromIntent(intent);
				String sender = msg[0].getDisplayOriginatingAddress();
				String body = "";
				for (int i = 0; i < msg.length; i++) {
					body = body + msg[i].getDisplayMessageBody();
				}

				// check sender against white/blacklist texts
				if (!sender.matches("[^+\\d]") && sender.length() >= 7) // if number is conventional, look for matches without country/region code
				{
					String tmp = sender;
					for (int i = 0; i <= tmp.length() - 7; i++) {
						tmp = sender.substring(i, sender.length());
						checkLists("s", tmp);
						if (isBlacklisted || isWhitelisted) {
							break;
						}
					}
				} else // if sender is unconventional, like service number or
				// alphanumeric, look for exact match
				{
					checkLists("s", sender);
				}

				// check sender against white/blacklist regex
				if (!isBlacklisted && !isWhitelisted) {
					Cursor cursor = db.getRegex("_sr");
					cursor.moveToFirst();
					while (!cursor.isAfterLast()) {
						Pattern p = Pattern.compile(cursor.getString(1));
						Matcher m = p.matcher(sender);
						if (m.find()) {
							if (cursor.getString(0).equals("wsr")) {
								isWhitelisted = true;
							} else {
								isBlacklisted = true;
							}
							cursor.close();
							break;
						}
						cursor.moveToNext();
					}
				}

				// check content against white/blacklist texts
				if (!isBlacklisted && !isWhitelisted) {
					checkLists("c", body);
				}

				// check content against white/blacklist regex
				if (!isBlacklisted && !isWhitelisted) {
					Cursor cursor = db.getRegex("_cr");
					cursor.moveToFirst();
					while (!cursor.isAfterLast()) {
						Pattern p = Pattern.compile(cursor.getString(1));
						Matcher m = p.matcher(body);
						if (m.find()) {
							if (cursor.getString(0).equals("wcr")) {
								isWhitelisted = true;
							} else {
								isBlacklisted = true;
							}
							cursor.close();
							break;
						}
						cursor.moveToNext();
					}
				}

				if (isWhitelisted) {
					Log.i("whitelist", "whitelisted");
					db.close();
					return; // do not run spam filter for whitelist sender
				} else {
					// Allow Contacts
					Pattern p = Pattern.compile("[^+\\d]");
					Matcher m = p.matcher(sender);
					if (allowContacts && !m.find()) {
						Cursor phones = null;
						if (sender.length() >= 7) {
							String tmp = sender;
							for (int i = 0; i <= tmp.length() - 7; i++) {
								tmp = sender.substring(i, sender.length());
								Log.i("tmp", tmp);
								phones = context.getContentResolver().query(Phone.CONTENT_URI, null, Phone.NUMBER + " = '" + tmp + "'", null, null);
								if (phones.getCount() > 0) {
									Log.i("contact", "found");
									phones.close();
									db.close();
									return;
								}
							}
						} else {
							phones = context.getContentResolver().query(Phone.CONTENT_URI, null, Phone.NUMBER + " = '" + sender + "'", null, null);
							if (phones.getCount() > 0) {
								phones.close();
								db.close();
								return;
							}
						}
					}

					// Block Non-Numeric Sender
					if (blockNonnumeric) {
						Log.i("senderAddress", sender);
						m = p.matcher(sender);
						if (m.find()) {
							nonNumeric = true;
						}
					}

					// Block All-Capital Message
					if (blockAllcapital) {
						allCapital = true;
						Pattern p3 = Pattern.compile("[a-z]");
						m = p3.matcher("");
						for (int i = 0; i < msg.length; i++) {
							m = p3.matcher(msg[i].getDisplayMessageBody());
							if (m.find()) {
								allCapital = false;
								break;
							}
						}
					}
					
					if( toggleSvm ) {
						Log.i(Constants.DEBUG_TAG, "Starting SVM check for: " + body);
						SVMSpam svmSpam = SvmManager.getSvm(context);
						svm_node[] nodes = SvmManager.getSvmNodeFromMessage(body, context);
						svm_node[] scaledNodes = SvmManager.scaleSingleMessage(nodes, context);
						double result = svmSpam.predictSingle(scaledNodes);
						
						// if result is 1.0, spam found
						if(result == 1.0) {
							svmResult = true;
							Log.i(Constants.DEBUG_TAG, "Spam found! (" + body + ")");

						}
						else{
							svmResult = false;
							Log.i(Constants.DEBUG_TAG, "It's clean (" + body + ")");
						}
						Log.i(Constants.DEBUG_TAG, "Finished SVM check");
					}
 				}

				Log.i("nonNumeric", String.valueOf(nonNumeric));
				Log.i("allCapital", String.valueOf(allCapital));
				Log.i("isBlacklisted", String.valueOf(isBlacklisted));

				// deduce spam or not
				if (isBlacklisted || regexMatch || nonNumeric || allCapital || svmResult) {
					this.abortBroadcast();
					Runnable r = new SpamThread(context);
					executor.execute(r);
				}
			}
			db.close();
		}
	}
}