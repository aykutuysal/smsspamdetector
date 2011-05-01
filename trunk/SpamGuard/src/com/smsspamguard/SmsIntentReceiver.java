package com.smsspamguard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsIntentReceiver extends BroadcastReceiver {
	
	private Database db;
	
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
	
	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(intent);
		if (Preferences.regexString == null) {
			Preferences.regexString = "";
			Log.i("nullmis", "a");
			Log.i("regexString", Preferences.regexString);
		}
		Log.i("toggleApp", String.valueOf(Preferences.toggleApp));
		Log.i("regexString", Preferences.regexString);
		Log.i("blockNonnumeric", String.valueOf(Preferences.blockNonnumeric));
		Log.i("blockAllcapital", String.valueOf(Preferences.blockAllcapital));
		if (Preferences.toggleApp) {
			if (intent.getAction().equals(
					"android.provider.Telephony.SMS_RECEIVED")) {

				SmsMessage msg[] = getMessagesFromIntent(intent);

				boolean regexMatch = false;
				if (!Preferences.regexString.equals("")) {
					Pattern p = Pattern.compile(Preferences.regexString); // Android
																	// default
																	// takes it
																	// unicode
																	// case
																	// insensitive
					Matcher m = p.matcher("");
					for (int i = 0; i < msg.length; i++) {
						m = p.matcher(msg[i].getDisplayMessageBody());
						if (m.find()) {
							regexMatch = true;
							break;
						}
					}
				}

				boolean nonNumeric = false;
				if (Preferences.blockNonnumeric) {
					String sender = msg[0].getDisplayOriginatingAddress();
					Log.i("senderAddress", sender);
					Pattern p = Pattern.compile("[^+\\d]");
					Matcher m = p.matcher(sender);
					if (m.find()) {
						nonNumeric = true;
					}
				}

				boolean allCapital = false;
				if (Preferences.blockAllcapital) {
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

				Log.i("regexMatch", String.valueOf(regexMatch));
				Log.i("nonNumeric", String.valueOf(nonNumeric));
				Log.i("allCapital", String.valueOf(allCapital));
				if (regexMatch || nonNumeric || allCapital) {
					this.abortBroadcast();

					for (int i = 0; i < msg.length; i++) {
						String message = msg[i].getDisplayMessageBody();
						if (message != null && message.length() > 0) {
							Toast.makeText(context, "SPAM: " + message,
									Toast.LENGTH_LONG).show();
							
							// writing spam sms to db
							db = new Database(context);
							db.insert("spam", message);
						}
					}
				}
			}
		}
	}
}
