package com.smsspamguard.receiver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.util.Log;

import com.smsspamguard.constant.Constants;
import com.smsspamguard.db.Database;
import com.smsspamguard.engine.svm.SvmManager;
import com.smsspamguard.engine.svm.core.SVMSpam;
import com.smsspamguard.engine.svm.input.InputFileCreator;

public class AlarmReceiver extends BroadcastReceiver {
	
	private class TrainThread implements Runnable {

		private Context context;
		private ArrayList<String> messageBodies;
		
		public TrainThread(Context context, ArrayList<String> messageBodies) {
			this.context = context;
			this.messageBodies = messageBodies;
		}
		
		@Override
		public void run() {
			createSpamsFile(messageBodies, context);
			InputFileCreator ifc = new InputFileCreator(context);
			ifc.createSvmInputs();
			
			SVMSpam svmSpam = SvmManager.getSvm(context);
			svmSpam.start();
		}
		
		/**
		 * Reads default spams file and merges it with new spams
		 * Creates a new internal file
		 * @param spams
		 * @param context
		 */
		public void createSpamsFile(ArrayList<String> spams, Context context) {
			
			Log.i(Constants.DEBUG_TAG,"Creating internal spams file");
			try {
				//read default spams file and add it to the internal file
				InputStream defaultCorpusIs = context.getAssets().open("defaultCorpus", AssetManager.ACCESS_UNKNOWN);
				
				FileOutputStream fos = context.openFileOutput(Constants.CORPUS_FILENAME, Context.MODE_PRIVATE);
				
				// copying default spams file to new internal spams file
				byte[] b = new byte[8*1024];  
				int read;  
				while ((read = defaultCorpusIs.read(b)) != -1) {  
					fos.write(b, 0, read);  
				}
	            defaultCorpusIs.close();
	            
	            String delimiter = "\n";
	            
	            // writing new spams to the internal file
//				if( spams.size() > 0 ) {
//		            fos.write(delimiter.getBytes());
//				}
				
	            for(int i=0;i<spams.size();i++) {
	            	fos.write(delimiter.getBytes());
	            	String line = "spam " + spams.get(i);
	            	fos.write(line.getBytes());
//	            	if( i < spams.size()-1 )
//	            		fos.write(delimiter.getBytes());
	            }
	            fos.close();
	            
	            Log.i(Constants.DEBUG_TAG,"Internal spams file is created (" + Constants.CORPUS_FILENAME + ")");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(Constants.DEBUG_TAG, "AlarmReceiver started");
		
		Database db = new Database(context);
		Cursor cursor = db.getBodies();
		cursor.moveToFirst();
		db.close();

		ArrayList<String> messageBodies = new ArrayList<String>();
		if(cursor.getCount() != 0)
		{
			while(!cursor.isAfterLast()) {
				messageBodies.add(cursor.getString(0));
				cursor.moveToNext();
			}
		}
		cursor.close();
		
		Runnable r = new TrainThread(context, messageBodies);
		new Thread(r).start();
	}
	
	
}
