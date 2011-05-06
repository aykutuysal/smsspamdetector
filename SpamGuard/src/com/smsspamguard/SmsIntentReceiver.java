package com.smsspamguard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsIntentReceiver extends BroadcastReceiver {

	private Database db;

	private boolean toggleApp;
	private boolean allowContacts;
	private boolean blockNonnumeric;
	private boolean blockAllcapital;
	private String regexString;

	private boolean isBlacklisted = false;
	private boolean isWhitelisted = false;
	private boolean notContact = false;
	private boolean regexMatch = false;
	private boolean nonNumeric = false;
	private boolean allCapital = false;

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

	private void checkLists(String sender) {
		Cursor c = db.searchList(sender);
		if (c.getCount() > 0) {
			c.moveToFirst();
			String type = c.getString(0);
			if (type.equals("bn") || type.equals("bt")) {
				isBlacklisted = true;
			} else {
				isWhitelisted = true;
			}
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		toggleApp = sp.getBoolean("toggle_spamguard", true);
		allowContacts = sp.getBoolean("allow_contacts", true);
		regexString = sp.getString("regex_string", "");
		blockNonnumeric = sp.getBoolean("block_nonnumeric", true);
		blockAllcapital = sp.getBoolean("block_allcapital", false);

		if (regexString == null) {
			regexString = "";
		}

		if (toggleApp) {
			if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {

				db = new Database(context);
				SmsMessage msg[] = getMessagesFromIntent(intent);
				String sender = msg[0].getDisplayOriginatingAddress();
				String body = "";
				for (int i = 0; i < msg.length; i++) {
					body = body + msg[i].getDisplayMessageBody();
				}

				if (sender.matches("[^+\\d]") && sender.length() >= 7) // if number is conventional, look for matches without country/region code
				{
					String tmp = sender;
					for (int i = 0; i <= tmp.length() - 7; i++) {
						tmp = sender.substring(i, sender.length());
						checkLists(tmp);
						if (isBlacklisted || isWhitelisted) {
							break;
						}
					}
				} else // if sender is unconventional, like service number or alphanumeric, look for exact match
				{
					checkLists(sender);
				}

				if (isWhitelisted) {
					Log.i("whitelist", "true");
					db.close();
					return; // do not run spam filter for whitelist sender
				} else {
					// Allow Contacts
					if (allowContacts && sender.matches("[^+\\d]")) {
						Cursor phones = null;
						if (sender.length() >= 7) {
							String tmp = sender;
							for (int i = 0; i <= tmp.length() - 7; i++) {
								tmp = sender.substring(i, sender.length());
								phones = context.getContentResolver().query(Phone.CONTENT_URI, null, Phone.NUMBER + " = '" + tmp + "'", null, null);
								if (phones.getCount() == 0) {
									notContact = true;
								} else {
									db.close();
									return;
								}
							}
						} else {
							phones = context.getContentResolver().query(Phone.CONTENT_URI, null, Phone.NUMBER + " = '" + sender + "'", null, null);
							if (phones.getCount() == 0) {
								notContact = true;
							} else {
								db.close();
								return;
							}
						}
					}

					if (notContact) // this if is not necessary
					{
						// Regex Filtering
						if (!regexString.equals("")) {
							Pattern p = Pattern.compile(regexString); // Android default takes it unicode case insensitive
							Matcher m = p.matcher("");
							for (int i = 0; i < msg.length; i++) {
								m = p.matcher(msg[i].getDisplayMessageBody());
								if (m.find()) {
									regexMatch = true;
									break;
								}
							}
						}

						// Block Non-Numeric Sender
						if (blockNonnumeric) {
							Log.i("senderAddress", sender);
							Pattern p = Pattern.compile("[^+\\d]");
							Matcher m = p.matcher(sender);
							if (m.find()) {
								nonNumeric = true;
							}
						}

						// Block All-Capital Message
						if (blockAllcapital) {
							allCapital = true;
							Pattern p = Pattern.compile("[a-z]");
							Matcher m = p.matcher("");
							for (int i = 0; i < msg.length; i++) {
								m = p.matcher(msg[i].getDisplayMessageBody());
								if (m.find()) {
									allCapital = false;
									break;
								}
							}
						}
					}
				}

				Log.i("regexMatch", String.valueOf(regexMatch));
				Log.i("nonNumeric", String.valueOf(nonNumeric));
				Log.i("allCapital", String.valueOf(allCapital));
				Log.i("isBlacklisted", String.valueOf(isBlacklisted));
				Log.i("allowContacts", String.valueOf(allowContacts));

				// deduce spam or not
				if (isBlacklisted || regexMatch || nonNumeric || allCapital) {
					this.abortBroadcast();

					Toast.makeText(context, "SPAM: " + body, Toast.LENGTH_LONG).show();

					for (int i = 0; i < msg.length; i++) {
						// writing spam sms to db
						db.insertSpam(msg[i]);
					}
				}
			}
			db.close();
		}
	}
}