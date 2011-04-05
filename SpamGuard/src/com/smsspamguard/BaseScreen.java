package com.smsspamguard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

public class BaseScreen extends PreferenceActivity {
	public static boolean toggleApp;
	public static boolean allowContacts;
	public static boolean blockNonnumeric;
	public static boolean blockAllcapital;
	public static String regexString;
	
	private void getPreferences(){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		toggleApp = sp.getBoolean("toggle_spamguard", true);
		allowContacts = sp.getBoolean("allow_contacts", true);
		regexString = sp.getString("regex_string", "");
		blockNonnumeric = sp.getBoolean("block_nonnumeric", true);
		blockAllcapital = sp.getBoolean("block_allcapital", false);
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferences();
        addPreferencesFromResource(R.xml.preferences);
        
        Preference blacklist = (Preference) findPreference("blacklist");
        blacklist.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(BaseScreen.this, Blacklist.class);
                startActivity(intent);
                return true;
			}
		});
        
        Preference whitelist = (Preference) findPreference("whitelist");
        whitelist.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(BaseScreen.this, Whitelist.class);
                startActivity(intent);
                return true;
			}
		});
    }

    public void onStart()
    {
    	super.onStart();
    	getPreferences();
		Log.i("regexString", regexString);
		Log.i("blockNonnumeric", String.valueOf(blockNonnumeric));
		Log.i("blockAllcapital", String.valueOf(blockAllcapital));
    }
    public void onResume()
    {
    	super.onResume();
    }
    public void onPause()
    {
    	super.onPause();
    }
    public void onDestroy()
    {
    	super.onDestroy();
    }
}