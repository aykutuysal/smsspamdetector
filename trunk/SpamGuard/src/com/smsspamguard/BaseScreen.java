package com.smsspamguard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class BaseScreen extends PreferenceActivity {
	public static boolean toggleApp;
	public static boolean allowContacts;
	public static boolean blockNonnumeric;
	public static String regexString;
	
	private void getPreferences(){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		toggleApp = sp.getBoolean("toggle_spamguard", true);
		allowContacts = sp.getBoolean("allow_contacts", true);
		regexString = sp.getString("regex_string", "");
		blockNonnumeric = sp.getBoolean("block_nonnumeric", true);
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferences();
        addPreferencesFromResource(R.xml.preferences);
        
       /* Preference toggleApp = (Preference) findPreference("regex_string");
        toggleApp.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				SharedPreferences mySharedPref = getSharedPreferences("preferencesFile", 0);
				SharedPreferences.Editor editor = mySharedPref.edit();
				editor.putString("regex_string", "");
				editor.commit();
				return true;
			}
		});*/
    }
    public void onStart()
    {
    	super.onStart();
    	getPreferences();
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