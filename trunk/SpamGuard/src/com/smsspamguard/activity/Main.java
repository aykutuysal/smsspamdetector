package com.smsspamguard.activity;

import java.io.IOException;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TabHost;

import com.smsspamguard.R;
import com.smsspamguard.constant.Constants;
import com.smsspamguard.db.Database;

public class Main extends TabActivity {
	
	Database db;
	ProgressDialog progress;

	private class TrainThread implements Runnable {

		private Context context;
		
		public TrainThread(Context context) {
			this.context = context;
		}

		@Override
		public void run() {
//			InputFileCreator ifc = new InputFileCreator(context);
//			ifc.createSvmInputs();
//
//			SVMSpam svmSpam = new SVMSpam(context);
//			svmSpam.createSvmModel();
//
//			try {
//				FileCopier.backupFiles();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			
			/********************************/
			
			Database db = Database.getInstance(context);
			db.close();

			try {
				FileCopier.copyFiles(context);
			} catch (IOException e) {
				e.printStackTrace();
			}

			handler.sendEmptyMessage(0);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Reusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, Spams.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("spams").setIndicator("Spams", res.getDrawable(R.drawable.tab_spams)).setContent(intent);
		tabHost.addTab(spec);
		
		// Do the same for the other tabs
		intent = new Intent().setClass(this, Whitelist.class);
		spec = tabHost.newTabSpec("whitelist").setIndicator("Whitelist", res.getDrawable(R.drawable.tab_whitelist)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, Blacklist.class);
		spec = tabHost.newTabSpec("blacklist").setIndicator("Blacklist", res.getDrawable(R.drawable.tab_blacklist)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, Preferences.class);
		spec = tabHost.newTabSpec("preferences").setIndicator("Preferences", res.getDrawable(R.drawable.tab_preferences)).setContent(intent);
		tabHost.addTab(spec);

		Intent incomingIntent = getIntent();
		if (incomingIntent.hasExtra("defaultTab")) {
			tabHost.setCurrentTab(incomingIntent.getIntExtra("defaultTab", 0));
		} else {
			tabHost.setCurrentTab(0);
		}

		SharedPreferences extraPrefs = getSharedPreferences(Constants.PREFS_NAME, 0);
		boolean assetsCopied = extraPrefs.getBoolean("assetsCopied", false);
		if (!assetsCopied) {
			progress = ProgressDialog.show(this, "", "Initializing...", false);
			Runnable r = new TrainThread(this);
			new Thread(r).start();
			
			SharedPreferences.Editor editor = extraPrefs.edit();
			editor.putBoolean("assetsCopied", true);
			editor.commit();
		}
		
//		progress = ProgressDialog.show(this, "", "Initializing...", false);
//		 Runnable r = new TrainThread(this);
//		 new Thread(r).start();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			progress.dismiss();
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (db != null) {
			db.close();
			Log.i(Constants.DEBUG_TAG, "DB closed by Main");
		}
	}
}