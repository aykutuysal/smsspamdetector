package com.smsspamguard.receiver;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.smsspamguard.constant.Constants;

/**
 * Starts at system boot
 * Receives boot completed intent
 */
public class StartUpReceiver extends BroadcastReceiver{

	public static int requestCode = 10000;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(Constants.DEBUG_TAG, "StartUpReceiver working");
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		boolean toggleApp = sp.getBoolean("toggle_spamguard", true);
		boolean toggleSvm = sp.getBoolean("toggle_svm", true);
		long period = Long.parseLong(sp.getString("update_interval", "86400000"));
		
		SharedPreferences extraPrefs = context.getSharedPreferences(Constants.PREFS_NAME, 0);
		long alarmStart = extraPrefs.getLong("alarm_start", 0);
		Calendar cal = Calendar.getInstance();
		long now = cal.getTimeInMillis();
		long left = now - alarmStart;
		left = left % period;
		left = period - left;
		if(left < 0)
		{
			left = 0;
		}
		Log.i(Constants.DEBUG_TAG, String.valueOf(left));
		
		if(toggleApp && toggleSvm)
		{
			Intent alarmIntent = new Intent(context, AlarmReceiver.class);
			PendingIntent sender = PendingIntent.getBroadcast(context, StartUpReceiver.requestCode, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			am.setInexactRepeating(AlarmManager.RTC, cal.getTimeInMillis() + left, period, sender);
			Log.i(Constants.DEBUG_TAG, "Alarm is set by StartUpReceiver");
		}
	}
}