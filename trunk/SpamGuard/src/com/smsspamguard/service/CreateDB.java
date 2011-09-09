package com.smsspamguard.service;

import java.io.IOException;

import com.smsspamguard.activity.FileCopier;
import com.smsspamguard.constant.Constants;
import com.smsspamguard.engine.svm.core.SVMSpam;
import com.smsspamguard.engine.svm.input.InputFileCreator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CreateDB extends Service {

	
  @Override
  public void onCreate() {
      super.onCreate();

      Log.i(Constants.DEBUG_TAG,"onCreate'te");
      

  }
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(Constants.DEBUG_TAG,"onStartCommand'de");
		
	      InputFileCreator ifc = new InputFileCreator(this);
			ifc.createSvmInputs();
			Log.i(Constants.DEBUG_TAG, "Inputfilecreator done!");
			try {
				FileCopier.backupFiles();
			} catch (IOException e) {
				e.printStackTrace();
			}

			SVMSpam svmSpam = new SVMSpam(this);
			svmSpam.createSvmModel();
			
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
