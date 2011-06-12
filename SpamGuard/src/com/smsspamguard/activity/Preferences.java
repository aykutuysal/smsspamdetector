package com.smsspamguard.activity;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.smsspamguard.R;
import com.smsspamguard.receiver.AlarmReceiver;

public class Preferences extends PreferenceActivity {
	
	public static final String PREFS_NAME = "AlarmPrefs";
	private long previousPeriod;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        previousPeriod = sp.getLong("svm_period", 7);

    }
    
	public void onPause() {
		super.onPause();
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		boolean toggleSvm = sp.getBoolean("toggle_svm", true);
		long period = sp.getLong("svm_period", 7);
		
		SharedPreferences alarmPref = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = alarmPref.edit();
		boolean alarmSet = alarmPref.getBoolean("alarm_set", false);
		
		Intent alarmIntent = new Intent(this, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		
		if(toggleSvm)
		{
			Calendar cal = Calendar.getInstance();
			//initialize alarm for training if not set before
			if(!alarmSet)
			{
				am.setInexactRepeating(AlarmManager.RTC, cal.getTimeInMillis(), 1*60*1000, sender);
				editor.putBoolean("alarm_set", true);
				editor.commit();
			    Log.i("SpamGuard", "Alarm is set by Preferences.java");
			}
			
			//change period
			if(period != previousPeriod)
			{
				am.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(), 5*60*1000, sender);
				Log.i("SpamGuard", "Period changed from " + String.valueOf(previousPeriod) + " to " + String.valueOf(period));
			}
		}
		else
		{
			am.cancel(sender);
			Log.i("SpamGuard", "Alarm is stopped by Preferences.java");
			editor.putBoolean("alarm_set", false);
			editor.commit();
		}
	}
	
}