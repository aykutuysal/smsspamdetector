package com.smsspamguard.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.smsspamguard.R;

public class Main extends TabActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Reusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, Preferences.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost
				.newTabSpec("preferences")
				.setIndicator("Preferences",
						res.getDrawable(R.drawable.tab_preferences))
				.setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, Whitelist.class);
		spec = tabHost
				.newTabSpec("whitelist")
				.setIndicator("Whitelist",
						res.getDrawable(R.drawable.tab_whitelist))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, Blacklist.class);
		spec = tabHost
				.newTabSpec("blacklist")
				.setIndicator("Blacklist",
						res.getDrawable(R.drawable.tab_blacklist))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, Spams.class);
		spec = tabHost.newTabSpec("spams")
				.setIndicator("Spams", res.getDrawable(R.drawable.tab_spams))
				.setContent(intent);
		tabHost.addTab(spec);

		Intent incomingIntent = getIntent();
		if (incomingIntent.hasExtra("defaultTab")) {
			tabHost.setCurrentTab(incomingIntent.getIntExtra("defaultTab", 0));
		} else {
			tabHost.setCurrentTab(0);
		}
	}
}