package com.smsspamguard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class BaseScreen extends PreferenceActivity {
	public static boolean toggleApp;
	
	private void getPreferences(){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		toggleApp = sp.getBoolean("toggle_spamguard", true);
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        /*Preference toggleApp = (Preference) findPreference("toggle_spamguard");
        toggleApp.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				SharedPreferences mySharedPref = getSharedPreferences("preferencesFile", 0);
				SharedPreferences.Editor editor = mySharedPref.edit();
				editor.putBoolean("toggle_spamguard", preference.isEnabled());
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