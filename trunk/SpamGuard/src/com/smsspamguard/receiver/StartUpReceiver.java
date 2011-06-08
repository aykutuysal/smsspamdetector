package com.smsspamguard.receiver;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
		Intent alarmIntent = new Intent(context, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, StartUpReceiver.requestCode, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Calendar cal = Calendar.getInstance();
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(), AlarmManager.INTERVAL_HOUR, sender);
		Log.i("StartUpReceiver", "Alarm is set by StartUpReceiver");
	}
}