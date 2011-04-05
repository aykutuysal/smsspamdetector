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


public class SmsIntentReceiver extends BroadcastReceiver 
{
	private SmsMessage[] getMessagesFromIntent(Intent intent)
	{
		SmsMessage retMsgs[] = null;
		Bundle bdl = intent.getExtras();
		try{
			Object pdus[] = (Object [])bdl.get("pdus");
			retMsgs = new SmsMessage[pdus.length];
			for(int n=0; n < pdus.length; n++)
			{
				byte[] byteData = (byte[])pdus[n];
				retMsgs[n] = SmsMessage.createFromPdu(byteData);
			}
			
		}catch(Exception e)
		{
			Log.e("GetMessages", "fail", e);
		}
		return retMsgs;
	}
	
	public void onReceive(Context context, Intent intent) 
	{
		Log.i("toggleApp", String.valueOf(BaseScreen.toggleApp));
		Log.i("regexString", BaseScreen.regexString);
		Log.i("blockNonnumeric", String.valueOf(BaseScreen.blockNonnumeric));
		Log.i("blockAllcapital", String.valueOf(BaseScreen.blockAllcapital));
		if(BaseScreen.toggleApp)
		{
			if(!intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
			{
				return;
			}
			SmsMessage msg[] = getMessagesFromIntent(intent);
			
			boolean regexMatch = false;
			if(!BaseScreen.regexString.equals(""))
			{
				Pattern p = Pattern.compile(BaseScreen.regexString);	//Android default takes it unicode case insensitive
				Matcher m = p.matcher("");
				for(int i=0; i < msg.length; i++)
				{
					m = p.matcher(msg[i].getDisplayMessageBody());
					if(m.find())
					{
						regexMatch = true;
						break;
					}
				}
			}
			
			boolean nonNumeric = false;
			if(BaseScreen.blockNonnumeric)
			{
				String sender = msg[0].getDisplayOriginatingAddress();
				Log.i("senderAddress", sender);
				Pattern p = Pattern.compile("[^+\\d]");
				Matcher m = p.matcher(sender);
				if(m.find())
				{
					nonNumeric = true;
				}
			}
			
			boolean allCapital = false;
			if(BaseScreen.blockAllcapital)
			{
				allCapital = true;
				Pattern p = Pattern.compile("[a-z]");
				Matcher m = p.matcher("");
				for(int i=0; i < msg.length; i++)
				{
					m = p.matcher(msg[i].getDisplayMessageBody());
					if(m.find())
					{
						allCapital = false;
						break;
					}
				}
			}
			
			Log.i("regexMatch", String.valueOf(regexMatch));
			Log.i("nonNumeric", String.valueOf(nonNumeric));
			Log.i("allCapital", String.valueOf(allCapital));
			if(regexMatch || nonNumeric || allCapital)
			{
				this.abortBroadcast();
			}
			for(int i=0; i < msg.length; i++)
			{
				String message = msg[i].getDisplayMessageBody();
				if(message != null && message.length() > 0)
				{
					Toast.makeText(context, message, Toast.LENGTH_LONG).show();
				}
			}
		}
	}
}
