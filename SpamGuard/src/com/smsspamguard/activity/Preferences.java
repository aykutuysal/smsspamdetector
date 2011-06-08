package com.smsspamguard.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.smsspamguard.R;

public class Preferences extends PreferenceActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}