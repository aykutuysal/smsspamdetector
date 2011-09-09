package com.smsspamguard.receiver;

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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsMessage;
import android.util.Log;

import com.smsspamguard.R;
import com.smsspamguard.activity.Main;
import com.smsspamguard.constant.Constants;
import com.smsspamguard.db.Database;
import com.smsspamguard.engine.bayesian.common.Token;
import com.smsspamguard.engine.bayesian.filter.BayesianFilterBigram;
import com.smsspamguard.engine.bayesian.filter.BayesianFilterTrigram;
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
	private boolean nonNumeric = false;
	private boolean allCapital = false;
	private boolean svmResult = false;

	private String body = "";
	private long date = 0;

	private NotificationManager mNotificationManager;
	private int SIMPLE_NOTFICATION_ID = 1;

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
	
	public void insertTokens(String[] ngrams, int type, String feature)
	{
		for (String str : ngrams) {
			long id = db.findToken(str, feature);
			if (id == -1) {
				Token t = new Token(str);
				if (type == 1) {
					t.markSpam();
				} else {
					t.markClean();
				}
				db.insertToken(t, feature);
			} else {
				ContentValues values = new ContentValues();
				if (type == 1) {
					values.put("spamCount", db.getSpamCount(str, feature) + 1);
				} else {
					values.put("cleanCount", db.getCleanCount(str, feature) + 1);
				}
				db.updateToken(id, values, feature);
			}
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		toggleApp = sp.getBoolean("toggle_spamguard", true);
		allowContacts = sp.getBoolean("allow_contacts", true);
		blockNonnumeric = sp.getBoolean("block_nonnumeric", false);
		blockAllcapital = sp.getBoolean("block_allcapital", false);
		toggleSvm = sp.getBoolean("toggle_svm", true);

		if (toggleApp) {
			if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {

				db = Database.getInstance(context);
				SmsMessage msg[] = getMessagesFromIntent(intent);
				String sender = msg[0].getDisplayOriginatingAddress();
				body = "";
				for (int i = 0; i < msg.length; i++) {
					body = body + msg[i].getDisplayMessageBody();
				}
				date = msg[0].getTimestampMillis();
				String address = msg[0].getOriginatingAddress();
				Message message = new Message(address, date, body, 0); // at start, incoming sms marked as clean

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
							break;
						}
						cursor.moveToNext();
					}
					cursor.close();
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
							break;
						}
						cursor.moveToNext();
					}
					cursor.close();
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
					if (!isBlacklisted && blockNonnumeric) {
						Log.i("senderAddress", sender);
						m = p.matcher(sender);
						if (m.find()) {
							nonNumeric = true;
						}
					}

					// Block All-Capital Message
					if (!isBlacklisted && !nonNumeric && blockAllcapital) {
						allCapital = true;
						Pattern p3 = Pattern.compile("[a-z]");
						m = p3.matcher(body);
						if (m.find()) {
							allCapital = false;
						}
					}

					if (!isBlacklisted && !nonNumeric && !allCapital && toggleSvm) {
						Log.i(Constants.DEBUG_TAG, "Starting SVM check for: " + body);
						SVMSpam svmSpam = new SVMSpam(context);
						svmSpam.loadSvmModel();

						svm_node[] scaledNodes = SvmManager.getSvmNodeFromMessage(body, context);
						//svm_node[] scaledNodes = SvmManager.scaleSingleMessage(nodes, context);
						double result = svmSpam.predictSingle(scaledNodes);

						// if result is 1.0, spam found
						if (result == 1.0) {
							svmResult = true;
							Log.i(Constants.DEBUG_TAG, "SVM Result : Spam  (" + body + ")");
						} else {
							svmResult = false;
							Log.i(Constants.DEBUG_TAG, "SVM Result : Clean (" + body + ")");
						}
						Log.i(Constants.DEBUG_TAG, "Finished SVM check");
					}
				}

				Log.i("nonNumeric", String.valueOf(nonNumeric));
				Log.i("allCapital", String.valueOf(allCapital));
				Log.i("isBlacklisted", String.valueOf(isBlacklisted));
				Log.i("svmResult", String.valueOf(svmResult));

				// deduce spam or not
				if (isBlacklisted || nonNumeric || allCapital || svmResult) {

					Log.i(Constants.DEBUG_TAG, "Marking message as spam. (" + body + ")");
					this.abortBroadcast();
					Log.i(Constants.DEBUG_TAG, "abort sonrasi");

					message.setType(1); // mark entry as spam

					// context.startService(new Intent(context, HandleSpam.class));
					mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
					Notification notifySpam = new Notification(R.drawable.ic_menu_add, "SpamGuarded!", System.currentTimeMillis());
					Intent notifyIntent = new Intent(context, Main.class);
					notifyIntent.putExtra("defaultTab", 0);
					PendingIntent myIntent = PendingIntent.getActivity(context, 0, notifyIntent, 0);
					notifySpam.flags |= Notification.FLAG_AUTO_CANCEL;
					notifySpam.setLatestEventInfo(context, "SpamGuard", "Click to view spams", myIntent);
					mNotificationManager.notify(SIMPLE_NOTFICATION_ID, notifySpam);
				}
				BayesianFilterBigram bigramFilter = new BayesianFilterBigram();
				BayesianFilterTrigram trigramFilter = new BayesianFilterTrigram();
				String[] bigrams = bigramFilter.returnTokenList(body);
				String[] trigrams = trigramFilter.returnTokenList(body);
				insertTokens(bigrams, message.getType(), "bi");
				insertTokens(trigrams, message.getType(), "tri");
				
				db.insertSms(message);
				db.close();
			}
		}
	}
}