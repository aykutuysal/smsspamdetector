package com.smsspamguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Starts at system boot
 * Receives boot completed intent
 */
public class StartUpReceiver extends BroadcastReceiver{

	public static int requestCode = 10000;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("StartUpReceiver", "StartUpReceiver working");
//		Intent alarmIntent = new Intent(context, AlarmReceiver.class);
//		intent.putExtra("alarm_message", "Job Working");
//		PendingIntent sender = PendingIntent.getBroadcast(context, StartUpReceiver.requestCode, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//		Calendar cal = Calendar.getInstance();
//		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//		am.setInexactRepeating(AlarmManager.RTC, cal.getTimeInMillis(), 30*1000, sender);
//		Log.i("StartUpReceiver", "Alarm is set");
	}
}