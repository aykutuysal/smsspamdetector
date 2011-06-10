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
		
		monogramFilter.trainBulk(Constants.SPAMS_FILENAME, "spam", context);
		monogramFilter.trainBulk(Constants.CLEANS_FILENAME, "clean", context);
		monogramFilter.finalizeTraining();
		
		bigramFilter.trainBulk(Constants.SPAMS_FILENAME, "spam", context);
		bigramFilter.trainBulk(Constants.CLEANS_FILENAME, "clean", context);
		bigramFilter.finalizeTraining();
		
		trigramFilter.trainBulk(Constants.SPAMS_FILENAME, "spam", context);
		trigramFilter.trainBulk(Constants.CLEANS_FILENAME, "clean", context);
		trigramFilter.finalizeTraining();
		
		createInputDataFromFile(Constants.SPAMS_FILENAME, Constants.CLEANS_FILENAME, Constants.SVM_INPUT_FILENAME, 1);
	}
	
	private void createInputDataFromFile(String spamSourcePath, String cleanSourcePath, String destPath, int classNo) {
		
		try
		{
			FileInputStream fisSpam = context.openFileInput(spamSourcePath);
			Scanner scannerSpam = new Scanner(fisSpam, "ISO-8859-9").useDelimiter("\n###SpamGuardDelimiter###\n");
			
			FileOutputStream fos = context.openFileOutput(destPath, Context.MODE_PRIVATE);
			while(scannerSpam.hasNext())
			{
				String sms = scannerSpam.next();
				
				// calculate monogram features monoSpamFeature, monoCleanFeauture
				String[] monogramTokens = monogramFilter.returnTokenList(sms);
				double monoSpamFeature = 0;
				double monoCleanFeature = 0;
				int count = 0;
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
				
				// calculate trigram features triSpamFeature, triCleanFeauture
				String[] bigramTokens = bigramFilter.returnTokenList(sms);
				double biSpamFeature = 0;
				double biCleanFeature = 0;
				count = 0;
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
				
				// calculate bigram features biSpamFeature, biCleanFeauture
				String[] trigramTokens = trigramFilter.returnTokenList(sms);
				double triSpamFeature = 0;
				double triCleanFeature = 0;
				count = 0;
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

				String line = classNo + " 1:" + monoSpamFeature + " 2:" + monoCleanFeature +
							" 3:" + biSpamFeature + " 4:" + biCleanFeature + 
							" 5:" + triSpamFeature + " 6:" + triCleanFeature + "\n";
				fos.write(line.getBytes());
			}
			System.out.println("Finished reading " + spamSourcePath);
			fisSpam.close();

			FileInputStream fisClean = context.openFileInput(cleanSourcePath);
			Scanner scannerClean = new Scanner(fisClean, "ISO-8859-9").useDelimiter("\n###SpamGuardDelimiter###\n");
			
			while(scannerClean.hasNext())
			{
				String sms = scannerClean.next();
				
				// calculate monogram features monoSpamFeature, monoCleanFeauture
				String[] monogramTokens = monogramFilter.returnTokenList(sms);
				double monoSpamFeature = 0;
				double monoCleanFeature = 0;
				int count = 0;
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
				
				// calculate trigram features triSpamFeature, triCleanFeauture
				String[] bigramTokens = bigramFilter.returnTokenList(sms);
				double biSpamFeature = 0;
				double biCleanFeature = 0;
				count = 0;
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
				
				// calculate bigram features biSpamFeature, biCleanFeauture
				String[] trigramTokens = trigramFilter.returnTokenList(sms);
				double triSpamFeature = 0;
				double triCleanFeature = 0;
				count = 0;
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

				String line = classNo + " 0:" + monoSpamFeature + " 2:" + monoCleanFeature +
							" 3:" + biSpamFeature + " 4:" + biCleanFeature + 
							" 5:" + triSpamFeature + " 6:" + triCleanFeature + "\n";
				fos.write(line.getBytes());
			}
			System.out.println("Finished reading " + cleanSourcePath);
			Log.i(Constants.DEBUG_TAG,"SVM Input File is created(" + destPath + ")");
			fos.close();
			fisClean.close();
			
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
