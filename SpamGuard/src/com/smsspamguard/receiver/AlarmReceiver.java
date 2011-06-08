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
import com.smsspamguard.model.Message;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("AlarmReceiver", "AlarmReceiver started");
		
		Database db = new Database(context);
		List<Message> spams = db.selectAllSpam();

		ArrayList<String> messageBodies = new ArrayList<String>();
		for (Message message : spams) {
			messageBodies.add(message.getBody());
		}
		
		createSpamsFile(messageBodies, context);
		
//		InputFileCreator ifc = new InputFileCreator(context);
//		ifc.createSvmInputs();
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
            
            // writing new spams to the internal file
            String delimiter = "\n###SpamGuardDelimiter###\n";
            fos.write(delimiter.getBytes());
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

}
