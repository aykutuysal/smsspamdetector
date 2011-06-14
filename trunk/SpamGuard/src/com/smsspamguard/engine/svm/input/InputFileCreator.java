package com.smsspamguard.engine.svm.input;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import android.content.Context;
import android.util.Log;

import com.smsspamguard.constant.Constants;
import com.smsspamguard.engine.bayesian.common.Token;
import com.smsspamguard.engine.bayesian.filter.BayesianFilterBigram;
import com.smsspamguard.engine.bayesian.filter.BayesianFilterMonogram;
import com.smsspamguard.engine.bayesian.filter.BayesianFilterTrigram;

public class InputFileCreator {

	private BayesianFilterMonogram monogramFilter;
	private BayesianFilterBigram bigramFilter;
	private BayesianFilterTrigram trigramFilter;
	private Context context;
	
	public InputFileCreator(Context context) {
		this.monogramFilter = new BayesianFilterMonogram();
		this.bigramFilter = new BayesianFilterBigram();
		this.trigramFilter = new BayesianFilterTrigram();
		this.context = context;
	}
	
	public void createSvmInputs() {
		
		monogramFilter.trainBulk(Constants.CORPUS_FILENAME, context);
		monogramFilter.finalizeTraining();
		
		bigramFilter.trainBulk(Constants.CORPUS_FILENAME, context);
		bigramFilter.finalizeTraining();
		
		trigramFilter.trainBulk(Constants.CORPUS_FILENAME, context);
		trigramFilter.finalizeTraining();
		
		createInputDataFromFile(Constants.CORPUS_FILENAME, Constants.SVM_INPUT_FILENAME);
	}
	
	private void createInputDataFromFile(String sourcePath, String destPath) {
		
		try
		{
			FileInputStream fis = context.openFileInput(sourcePath);
			Scanner scanner = new Scanner(new FileInputStream(sourcePath), "UTF-8").useDelimiter("\n");
			
			FileOutputStream fos = context.openFileOutput(destPath, Context.MODE_PRIVATE);
			while(scanner.hasNext())
			{
				String line = scanner.next();
				String type = line.split("\\W")[0];	//get type, first word of the line
				String sms = line.substring(type.length() + 1);	//get message, rest of the line
				int classNo = type.equals("spam") ? 1: 0;
				
				// calculate monogram features monoSpamFeature, monoCleanFeauture
				String[] monogramTokens = monogramFilter.returnTokenList(sms);
				double monoSpamFeature = 0.0;
				double monoCleanFeature = 0.0;
				double count = 0.0;
				for(String tokenKey : monogramTokens) {
					
					Token monogramToken = monogramFilter.findToken(tokenKey);

					if( monogramToken != null ) {
						monoSpamFeature += monogramToken.getSpamRatio();
						monoCleanFeature += monogramToken.getNonSpamRatio();
						count++;
					}
				}
				if( count > 0) {
					monoSpamFeature /= count;
					monoCleanFeature /= count;
				}
				
				// calculate bigram features biSpamFeature, biCleanFeauture
				String[] bigramTokens = bigramFilter.returnTokenList(sms);
				double biSpamFeature = 0.0;
				double biCleanFeature = 0.0;
				count = 0.0;
				for(String tokenKey : bigramTokens) {
					
					Token bigramToken = bigramFilter.findToken(tokenKey);

					if( bigramToken != null ) {
						biSpamFeature += bigramToken.getSpamRatio();
						biCleanFeature += bigramToken.getNonSpamRatio();
						count++;
					}
				}
				if( count > 0) {
					biSpamFeature /= count;
					biCleanFeature /= count;
				}
				
				// calculate trigram features triSpamFeature, triCleanFeauture
				String[] trigramTokens = trigramFilter.returnTokenList(sms);
				double triSpamFeature = 0.0;
				double triCleanFeature = 0.0;
				count = 0.0;
				for(String tokenKey : trigramTokens) {
					
					Token trigramToken = trigramFilter.findToken(tokenKey);

					if( trigramToken != null ) {
						triSpamFeature += trigramToken.getSpamRatio();
						triCleanFeature += trigramToken.getNonSpamRatio();
						count++;
					}
				}
				if( count > 0) {
					triSpamFeature /= count;
					triCleanFeature /= count;
				}				

				String writeLine = classNo + " 1:" + monoSpamFeature + " 2:" + monoCleanFeature +
							" 3:" + biSpamFeature + " 4:" + biCleanFeature + 
							" 5:" + triSpamFeature + " 6:" + triCleanFeature + "\n";
				fos.write(writeLine.getBytes());
			}
			System.out.println("Finished reading " + sourcePath);
			Log.i(Constants.DEBUG_TAG,"SVM Input File is created(" + destPath + ")");
			fis.close();
			fos.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
