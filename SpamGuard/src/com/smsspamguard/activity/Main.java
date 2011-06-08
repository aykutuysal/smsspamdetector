package com.smsspamguard.activity;

import java.util.Calendar;

import com.smsspamguard.R;
import com.smsspamguard.R.drawable;
import com.smsspamguard.R.layout;
import com.smsspamguard.receiver.AlarmReceiver;
import com.smsspamguard.receiver.StartUpReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class Main extends TabActivity {
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Reusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, Preferences.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("preferences").setIndicator("Preferences",
	                      res.getDrawable(R.drawable.tab_preferences))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, Whitelist.class);
	    spec = tabHost.newTabSpec("whitelist").setIndicator("Whitelist",
	                      res.getDrawable(R.drawable.tab_whitelist))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, Blacklist.class);
	    spec = tabHost.newTabSpec("blacklist").setIndicator("Blacklist",
	                      res.getDrawable(R.drawable.tab_blacklist))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, Spams.class);
	    spec = tabHost.newTabSpec("spams").setIndicator("Spams",
	                      res.getDrawable(R.drawable.tab_spams))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    
	    tabHost.setCurrentTab(0);
	
	    //start alarm for training
//	    Intent alarmIntent = new Intent(this, AlarmReceiver.class);
//		intent.putExtra("alarm_message", "Job Working");
//		PendingIntent sender = PendingIntent.getBroadcast(this, StartUpReceiver.requestCode, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//		Calendar cal = Calendar.getInstance();
//		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//		am.setInexactRepeating(AlarmManager.RTC, cal.getTimeInMillis(), 15*1000, sender);
	    Log.i("SpamGuard", "Alarm is set");
	}
}