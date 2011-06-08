package com.smsspamguard.receiver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.util.Log;

import com.smsspamguard.constant.Constants;
import com.smsspamguard.db.Database;
import com.smsspamguard.engine.svm.input.InputFileCreator;
import com.smsspamguard.model.Message;

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
			createCleansFile(context);
			InputFileCreator ifc = new InputFileCreator(context);
			ifc.createSvmInputs();
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
				InputStream defaultSpamsIs = context.getAssets().open("defaultSpams.txt", AssetManager.ACCESS_UNKNOWN);
				
				FileOutputStream fos = context.openFileOutput(Constants.SPAMS_FILENAME, Context.MODE_PRIVATE);
				
				// copying default spams file to new internal spams file
				byte[] b = new byte[1024];  
				int read;  
				while ((read = defaultSpamsIs.read(b)) != -1) {  
					fos.write(b, 0, read);  
				}  
	            
	            String delimiter = "\n###SpamGuardDelimiter###\n";
	            
	            // writing new spams to the internal file
				if( spams.size() > 0 ) {
		            fos.write(delimiter.getBytes());
				}
				
	            for(int i=0;i<spams.size();i++) {
	            	fos.write(spams.get(i).getBytes());
	            	if( i < spams.size()-1 )
	            		fos.write(delimiter.getBytes());
	            }
	            
	            fos.close();
	            defaultSpamsIs.close();
	            
	            Log.i(Constants.DEBUG_TAG,"Internal spams file is created (" + Constants.SPAMS_FILENAME + ")");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

		/**
		 * Reads default clean file and merges it with new spams
		 * Creates a new internal file
		 * @param spams
		 * @param context
		 */
		public void createCleansFile(Context context) {
			
			Log.i(Constants.DEBUG_TAG,"Creating internal cleans file");
			try {
				//read default spams file and add it to the internal file
				InputStream defaultCleansIs = context.getAssets().open("defaultCleans.txt", AssetManager.ACCESS_UNKNOWN);
				
				FileOutputStream fos = context.openFileOutput(Constants.CLEANS_FILENAME, Context.MODE_PRIVATE);
				
				// copying default spams file to new internal spams file
				byte[] b = new byte[1024];  
				int read;  
				while ((read = defaultCleansIs.read(b)) != -1) {  
					fos.write(b, 0, read);  
				}  
	            
	            fos.close();
	            defaultCleansIs.close();
	            
	            Log.i(Constants.DEBUG_TAG,"Internal spams file is created (" + Constants.CLEANS_FILENAME + ")");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("AlarmReceiver", "AlarmReceiver started");
		
		Database db = new Database(context);
		List<Message> spams = db.selectAllSpam();
		db.close();

		ArrayList<String> messageBodies = new ArrayList<String>();
		for (Message message : spams) {
			messageBodies.add(message.getBody());
		}
		
		Runnable r = new TrainThread(context, messageBodies);
		new Thread(r).start();
	}
	
	
}
