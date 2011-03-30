package com.smsspamguard;

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
		Log.i("en bas", "yo");
		if(!intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
		{
			return;
		}
		SmsMessage msg[] = getMessagesFromIntent(intent);
		this.abortBroadcast();
		for(int i=0; i < msg.length; i++)
		{
			Log.i("yo", "yo");
			String message = msg[i].getDisplayMessageBody();
			if(message != null && message.length() > 0)
			{
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
				Log.i("trigged", "yo");
			}
		}
	}
}
