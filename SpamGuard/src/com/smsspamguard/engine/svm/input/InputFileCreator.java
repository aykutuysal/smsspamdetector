package com.smsspamguard.engine.svm.input;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import com.smsspamguard.engine.bayesian.common.Token;
import com.smsspamguard.engine.bayesian.filter.BayesianFilterBigram;
import com.smsspamguard.engine.bayesian.filter.BayesianFilterMonogram;
import com.smsspamguard.engine.bayesian.filter.BayesianFilterTrigram;

public class InputFileCreator {

	private static BayesianFilterMonogram monogramFilter;
	private static BayesianFilterBigram bigramFilter;
	private static BayesianFilterTrigram trigramFilter;
	
	public static void main(String[] args) {
		
		monogramFilter = new BayesianFilterMonogram();
		bigramFilter = new BayesianFilterBigram();
		trigramFilter = new BayesianFilterTrigram();
		
		monogramFilter.trainBulk("data/bayesian/3/spamsTrain.txt", "spam");
		monogramFilter.trainBulk("data/bayesian/3/cleansTrain.txt", "clean");
		monogramFilter.finalizeTraining();
		
		bigramFilter.trainBulk("data/bayesian/3/spamsTrain.txt", "spam");
		bigramFilter.trainBulk("data/bayesian/3/cleansTrain.txt", "clean");
		bigramFilter.finalizeTraining();
		
		trigramFilter.trainBulk("data/bayesian/3/spamsTrain.txt", "spam");
		trigramFilter.trainBulk("data/bayesian/3/cleansTrain.txt", "clean");
		trigramFilter.finalizeTraining();
		
		createInputDataFromFile("data/bayesian/3/spamsTrain.txt", "data/inputset3/train.3", 1);
		createInputDataFromFile("data/bayesian/3/cleansTrain.txt", "data/inputset3/train.3", 0);
		
		createInputDataFromFile("data/bayesian/3/spamsTest.txt", "data/inputset3/test.3", 1);
		createInputDataFromFile("data/bayesian/3/cleansTest.txt", "data/inputset3/test.3", 0);

	}
	
	private static void createInputDataFromFile(String sourcePath, String destPath, int classNo) {
		
		try
		{
			Scanner scanner = new Scanner(new FileInputStream(sourcePath), "ISO-8859-9").useDelimiter("\n###SpamGuardDelimiter###\n");
			
			FileWriter fw = new FileWriter(new File(destPath),true);
			while(scanner.hasNext())
			{
				String sms = scanner.next();
				
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
					else {
//						System.out.println("Token " + tokenKey + " not found in monogramFilter tokens");
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
					else {
//						System.out.println("Token " + tokenKey + " not found in bigramFilter tokens");
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
					else {
//						System.out.println("Token " + tokenKey + " not found in trigramFilter tokens");
					}
				}
				if( count > 0) {
					triSpamFeature /= count;
					triCleanFeature /= count;
				}				

				fw.append(classNo + " 1:" + monoSpamFeature + " 2:" + monoCleanFeature +
									" 3:" + biSpamFeature + " 4:" + biCleanFeature + 
									" 5:" + triSpamFeature + " 6:" + triCleanFeature + "\n");

			}
			System.out.println("Finished reading " + sourcePath);
			fw.close();

		}
		catch(IOException e){
			e.printStackTrace();
		}
		

		
	}
	
}
