package com.smsspamguard.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.smsspamguard.constant.Constants;

public class FileCopier {

	public static void backupFiles() throws IOException {
	    
		ArrayList<String> filenames = new ArrayList<String>();
		
//		filenames.add(Constants.CORPUS_FILENAME);
		filenames.add(Constants.SVM_INPUT_FILENAME);
		filenames.add(Constants.SVM_INPUT_FILENAME + ".scaled");
		filenames.add(Constants.SVM_RANGE_SAVE_PATH);
		filenames.add(Constants.SVM_MODEL);
//		filenames.add(Constants.SVM_SINGLE_MSG_FEATURE_FILE);
//		filenames.add(Constants.SVM_SINGLE_MSG_FEATURE_FILE_SCALED);

			//Open your local db as the input stream
		    //String inFileName = "/data/data/com.smsspamguard/files/" + filename;
		    String inFileName = "/data/data/com.smsspamguard/databases/" + "spamguard.db";
		    File dbFile = new File(inFileName);
		    FileInputStream fis = new FileInputStream(dbFile);
	
		    String outFileName = Environment.getExternalStorageDirectory()+"/" + "spamguard.db";
		    //Open the empty db as the output stream
		    OutputStream output = new FileOutputStream(outFileName);
		    //transfer bytes from the inputfile to the outputfile
		    byte[] buffer = new byte[1024];
		    int length;
		    while ((length = fis.read(buffer))>0){
		        output.write(buffer, 0, length);
		    }
		    //Close the streams
		    output.flush();
		    output.close();
		    fis.close();
		    Log.i(Constants.DEBUG_TAG, "DB backuped");
	    
		for(String filename : filenames ) {
			//Open your local db as the input stream
		    inFileName = "/data/data/com.smsspamguard/files/" + filename;
		    dbFile = new File(inFileName);
		    fis = new FileInputStream(dbFile);
	
		    outFileName = Environment.getExternalStorageDirectory()+"/" + filename;
		    //Open the empty db as the output stream
		    output = new FileOutputStream(outFileName);
		    //transfer bytes from the inputfile to the outputfile
		    //byte[] buffer = new byte[1024];
		    //int length;
		    while ((length = fis.read(buffer))>0){
		        output.write(buffer, 0, length);
		    }
		    //Close the streams
		    output.flush();
		    output.close();
		    fis.close();
		    Log.i(Constants.DEBUG_TAG, filename + " backuped");
		}
	    
	}
	
	public static void copyFiles(Context context) throws IOException {

		ArrayList<String> filenames = new ArrayList<String>();

		// filenames.add(Constants.CORPUS_FILENAME);
		filenames.add(Constants.SVM_INPUT_FILENAME);
		filenames.add(Constants.SVM_INPUT_FILENAME + ".scaled");
		filenames.add(Constants.SVM_RANGE_SAVE_PATH);
		filenames.add(Constants.SVM_MODEL);
		// filenames.add(Constants.SVM_SINGLE_MSG_FEATURE_FILE);
		// filenames.add(Constants.SVM_SINGLE_MSG_FEATURE_FILE_SCALED);

		// Open your local db as the input stream
		// String inFileName = "/data/data/com.smsspamguard/files/" + filename;
		// String inFileName = "/data/data/com.smsspamguard/databases/" + "spamguard.db";
		// File dbFile = new File(inFileName);
		// FileInputStream fis = new FileInputStream(dbFile);
		InputStream is = context.getAssets().open("spamguard.db");

//		String outFileName = "/data/data/com.smsspamguard/databases/spamguard.db";
		File dbPath = context.getDatabasePath("spamguard.db");
		// Open the empty db as the output stream
		Log.i(Constants.DEBUG_TAG, dbPath.getAbsolutePath());
		OutputStream output = new FileOutputStream(dbPath.getAbsolutePath());
		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = is.read(buffer)) > 0) {
			output.write(buffer, 0, length);
		}
		// Close the streams
		output.flush();
		output.close();
		is.close();
		Log.i(Constants.DEBUG_TAG, "DB copied");

		for (String filename : filenames) {
			// Open your local db as the input stream
//			inFileName = "/data/data/com.smsspamguard/files/" + filename;
//			dbFile = new File(inFileName);
//			fis = new FileInputStream(dbFile);
			is = context.getAssets().open(filename);

//			outFileName = "/data/data/com.smsspamguard/files/" + filename;
//			// Open the empty db as the output stream
//			output = new FileOutputStream(outFileName);
			FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			// transfer bytes from the inputfile to the outputfile
			// byte[] buffer = new byte[1024];
			// int length;
			while ((length = is.read(buffer)) > 0) {
				fos.write(buffer, 0, length);
			}
			// Close the streams
			fos.flush();
			fos.close();
			is.close();
			Log.i(Constants.DEBUG_TAG, filename + " copied");
//			File f = context.getFilesDir();
//			Log.i(Constants.DEBUG_TAG, "hello " + f.getAbsolutePath());
//			File f2 = context.getFileStreamPath(filename);
//			Log.i(Constants.DEBUG_TAG, "yo " + f2.getAbsolutePath());
		}

	}
}
