package com.smsspamguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.smsspamguard.constant.Constants;
import com.smsspamguard.engine.svm.core.SVMSpam;
import com.smsspamguard.engine.svm.input.InputFileCreator;

public class AlarmReceiver extends BroadcastReceiver {
	
	private class TrainThread implements Runnable {

		private Context context;
		//private ArrayList<String> messageBodies;
		
		public TrainThread(Context context) {
			this.context = context;
			//this.messageBodies = messageBodies;
		}
		
		@Override
		public void run() {
			//createSpamsFile(context);
			InputFileCreator ifc = new InputFileCreator(context);
			ifc.createSvmInputs();
			
			SVMSpam svmSpam = new SVMSpam(context);
			svmSpam.createSvmModel();
			
//			try {
//				FileCopier.backupFiles();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
		
//		/**
//		 * Reads default spams file and merges it with new spams
//		 * Creates a new internal file
//		 * @param spams
//		 * @param context
//		 */
//		public void createSpamsFile(Context context) {
//			
//			Log.i(Constants.DEBUG_TAG,"Creating internal spams file");
//			try {
//				//read default spams file and add it to the internal file
//				InputStream defaultCorpusIs = context.getAssets().open("defaultCorpus", AssetManager.ACCESS_UNKNOWN);
//				
//				FileOutputStream fos = context.openFileOutput(Constants.CORPUS_FILENAME, Context.MODE_PRIVATE);
//				
//				// copying default spams file to new internal spams file
//				byte[] b = new byte[8*1024];  
//				int read;  
//				while ((read = defaultCorpusIs.read(b)) != -1) {  
//					fos.write(b, 0, read);
//				}
//	            defaultCorpusIs.close();
//	            
//	            String delimiter = "\n";
//	            
//	            // writing new spams to the internal file
//	            Database db = Database.getInstance();
//	    		Cursor cursor = db.getSmses();
//	    		cursor.moveToFirst();
//	    		db.close();
//				
//	            for(int i=0;i<cursor.getCount();i++) {
//	            	fos.write(delimiter.getBytes());
//	            	String type;
//	            	if(cursor.getInt(1) == 0)
//	            	{
//	            		type = "ham";
//	            	}
//	            	else
//	            	{
//	            		type = "spam";
//	            	}
//	            	String line = type + " " + cursor.getString(0);
//	            	fos.write(line.getBytes());
//	            	cursor.moveToNext();
//	            }
//	            fos.close();
//	            
//	            Log.i(Constants.DEBUG_TAG,"Internal corpus file is created (" + Constants.CORPUS_FILENAME + ")");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(Constants.DEBUG_TAG, "AlarmReceiver started");
		
//		Database db = Database.getInstance();
//		Cursor cursor = db.getSmses();
//		cursor.moveToFirst();
//		db.close();
//
//		ArrayList<String> messageBodies = new ArrayList<String>();
//		if(cursor.getCount() != 0)
//		{
//			while(!cursor.isAfterLast()) {
//				messageBodies.add(cursor.getString(0));
//				cursor.moveToNext();
//			}
//		}
//		cursor.close();
//		context.startService(new Intent(context, CreateDB.class));
//		InputFileCreator ifc = new InputFileCreator(context);
//		ifc.createSvmInputs();
//		Log.i(Constants.DEBUG_TAG, "Inputfilecreator done!");
//		try {
//			FileCopier.backupFiles();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		SVMSpam svmSpam = new SVMSpam(context);
//		svmSpam.createSvmModel();
		Runnable r = new TrainThread(context);
		new Thread(r).start();
	}
	
	
}
