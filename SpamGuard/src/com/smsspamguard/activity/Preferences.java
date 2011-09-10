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
import com.smsspamguard.constant.Constants;
import com.smsspamguard.receiver.AlarmReceiver;

public class Preferences extends PreferenceActivity {
	
	
	private long previousPeriod;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	 SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
         previousPeriod = Long.parseLong(sp.getString("update_interval", "86400000"));
    }
    
    @Override
	public void onPause() {
		super.onPause();
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		SharedPreferences extraPrefs = getSharedPreferences(Constants.PREFS_NAME, 0);
		SharedPreferences.Editor editor = extraPrefs.edit();
		
//		PackageInfo pInfo;
//	    try {
//	        pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
//	        if ( extraPrefs.getLong( "lastRunVersionCode", 0) < pInfo.versionCode ) {
//	        	editor.putBoolean("alarm_set", false);
//	            editor.putLong("lastRunVersionCode", pInfo.versionCode);
//	            editor.commit();
//	        }
//	    } catch (NameNotFoundException e) {
//	        e.printStackTrace();
//	    }
	    
		boolean toggleApp = sp.getBoolean("toggle_spamguard", true);
		boolean toggleSvm = sp.getBoolean("toggle_svm", true);
		long period = Long.parseLong(sp.getString("update_interval", "86400000"));
	    boolean alarmSet = extraPrefs.getBoolean("alarm_set", false);
		
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		if(toggleApp && toggleSvm)
		{
			Calendar cal = Calendar.getInstance();
			long alarmStart = extraPrefs.getLong("alarm_start", 0);
			long left = 0;
			if(alarmStart > 0)
			{
				left = cal.getTimeInMillis() + previousPeriod - alarmStart;
				left = left % previousPeriod;
				left = period - left;
				if(left < 0)
				{
					left = 0;
				}
			}
			Log.i(Constants.DEBUG_TAG, String.valueOf(left));
			
			Intent alarmIntent = new Intent(this, AlarmReceiver.class);
			PendingIntent sender = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			//initialize alarm for training if not set before
			if(!alarmSet)
			{
				am.setInexactRepeating(AlarmManager.RTC, cal.getTimeInMillis() + period, period, sender);
				editor.putLong("alarm_start", cal.getTimeInMillis());
				editor.putBoolean("alarm_set", true);
				editor.commit();
			    Log.i("SpamGuard", "Alarm is started by Preferences.java");
			}
			
			//change period
			if(period != previousPeriod)
			{
				am.setInexactRepeating(AlarmManager.RTC, cal.getTimeInMillis() + left, period, sender);
				Log.i("SpamGuard", "Alarm is modified by Preferences.java");
			}
		}
		else
		{
			Intent alarmIntent = new Intent(this, AlarmReceiver.class);
			PendingIntent sender = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			am.cancel(sender);
			Log.i("SpamGuard", "Alarm is stopped by Preferences.java");
			editor.putLong("alarm_start", 0);
			editor.putBoolean("alarm_set", false);
			editor.commit();
		}
	}
	
}