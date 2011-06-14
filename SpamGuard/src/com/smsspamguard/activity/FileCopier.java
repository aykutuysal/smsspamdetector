package com.smsspamguard.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import com.smsspamguard.constant.Constants;

import android.os.Environment;

public class FileCopier {

	public static void backupFiles() throws IOException {
	    
		ArrayList<String> filenames = new ArrayList<String>();
		
		filenames.add(Constants.CORPUS_FILENAME);
//		filenames.add(Constants.SVM_INPUT_FILENAME);
//		filenames.add(Constants.SVM_INPUT_FILENAME + ".scaled");
//		filenames.add(Constants.SVM_RANGE_SAVE_PATH);
//		filenames.add(Constants.SVM_SINGLE_MSG_FEATURE_FILE);
//		filenames.add(Constants.SVM_SINGLE_MSG_FEATURE_FILE_SCALED);


		for(String filename : filenames ) {
			//Open your local db as the input stream
		    String inFileName = "/data/data/com.smsspamguard/files/" + filename;
		    File dbFile = new File(inFileName);
		    FileInputStream fis = new FileInputStream(dbFile);
	
		    String outFileName = Environment.getExternalStorageDirectory()+"/" + filename;
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
		}
	    
	    
	    
	}
}
