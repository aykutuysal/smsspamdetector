package com.smsspamguard;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

public class Whitelist extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.blackwhitelistmenu, menu);
        return true;
    }
    public void onStart()
    {
    	super.onStart();
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
