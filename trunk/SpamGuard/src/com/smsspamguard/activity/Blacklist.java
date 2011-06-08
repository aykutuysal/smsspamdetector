package com.smsspamguard.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.smsspamguard.R;

public class Blacklist extends TabActivity {
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tab_list);

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Reusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    intent = new Intent().setClass(this, BlacklistSender.class);
	    spec = tabHost.newTabSpec("blacklistSender").setIndicator("Sender",
	                      res.getDrawable(R.drawable.tab_blacklist_sender))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, BlacklistText.class);
	    spec = tabHost.newTabSpec("blacklistText").setIndicator("Content",
	                      res.getDrawable(R.drawable.tab_blacklist_text))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    
	    tabHost.setCurrentTab(0);
	}
}