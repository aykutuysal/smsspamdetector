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
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		toggleApp = sp.getBoolean("toggle_spamguard", true);
		allowContacts = sp.getBoolean("allow_contacts", true);
		regexString = sp.getString("regex_string", "");
		blockNonnumeric = sp.getBoolean("block_nonnumeric", true);
		blockAllcapital = sp.getBoolean("block_allcapital", false);
		
		if (regexString == null) {
			regexString = "";
			Log.i("nullmis", "a");
			Log.i("regexString", regexString);
		}
		
		Log.i("toggleApp", String.valueOf(toggleApp));
		Log.i("regexString", regexString);
		Log.i("blockNonnumeric", String.valueOf(blockNonnumeric));
		Log.i("blockAllcapital", String.valueOf(blockAllcapital));
		
		if (toggleApp) {
			if (intent.getAction().equals(
					"android.provider.Telephony.SMS_RECEIVED")) {

				SmsMessage msg[] = getMessagesFromIntent(intent);

				boolean notContact = false;
				if(allowContacts)
				{
					Cursor phones = context.getContentResolver().query(Phone.CONTENT_URI, null,
				            Phone.NUMBER + " = '" + msg[0].getDisplayOriginatingAddress() + "'", null, null);
					if(phones.getCount() == 0)
					{
						notContact = true;
						Log.i("phoneNo", "bole bi contact yok");
					}
					else
					{
						Log.i("phoneNo", "contact buldu");
					}
				}
				
				boolean regexMatch = false;
				if (!regexString.equals("")) {
					Pattern p = Pattern.compile(regexString); // Android
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
				if (blockNonnumeric) {
					String sender = msg[0].getDisplayOriginatingAddress();
					Log.i("senderAddress", sender);
					Pattern p = Pattern.compile("[^+\\d]");
					Matcher m = p.matcher(sender);
					if (m.find()) {
						nonNumeric = true;
					}
				}

				boolean allCapital = false;
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

				Log.i("regexMatch", String.valueOf(regexMatch));
				Log.i("nonNumeric", String.valueOf(nonNumeric));
				Log.i("allCapital", String.valueOf(allCapital));
				if (notContact || (regexMatch || nonNumeric || allCapital)) {
					this.abortBroadcast();

					for (int i = 0; i < msg.length; i++) {
						
						String displayMessageBody = msg[i].getDisplayMessageBody();
						
						if (displayMessageBody != null && displayMessageBody.length() > 0) {
							Toast.makeText(context, "SPAM: " + displayMessageBody,
									Toast.LENGTH_LONG).show();
							// writing spam sms to db
							db = new Database(context);
							db.insertSpam(msg[i]);
						}
					}
				}
			}
		}
	}
}
