package spamguard.svm.input;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import spamguard.bayesian.common.Token;
import spamguard.bayesian.filters.BayesianFilterBigram;
import spamguard.bayesian.filters.BayesianFilterMonogram;
import spamguard.bayesian.filters.BayesianFilterTrigram;

public class InputFileCreator {

	private static BayesianFilterMonogram monogramFilter;
	private static BayesianFilterBigram bigramFilter;
	private static BayesianFilterTrigram trigramFilter;
	
	public static void start()
	{
		monogramFilter = new BayesianFilterMonogram();
		bigramFilter = new BayesianFilterBigram();
		trigramFilter = new BayesianFilterTrigram();
		
		monogramFilter.trainBulk("data/bayesian/test/train.txt");
		monogramFilter.finalizeTraining();
		
		bigramFilter.trainBulk("data/bayesian/test/train.txt");
		bigramFilter.finalizeTraining();
		
		trigramFilter.trainBulk("data/bayesian/test/train.txt");
		trigramFilter.finalizeTraining();
		
		createInputDataFromFile("data/bayesian/test/train.txt", "data/inputset/train");
		
		createInputDataFromFile("data/bayesian/test/test.txt", "data/inputset/test");
	}
	
	public static void main(String[] args) {
		
		start();
	}
	
	private static void createInputDataFromFile(String sourcePath, String destPath) {
		
		try
		{
			Scanner scanner = new Scanner(new FileInputStream(sourcePath), "UTF-8").useDelimiter("\n");
			
			FileWriter fw = new FileWriter(new File(destPath),false);
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
					else {
//						System.out.println("Token " + tokenKey + " not found in monogramFilter tokens");
					}
				}
				if( count > 0) {
					monoSpamFeature /= count;
					monoCleanFeature /= count;
				}
//				System.out.println("monoSpamFeature : " + monoSpamFeature);
//				System.out.println("monoCleanFeature : " + monoCleanFeature);
				
				// calculate trigram features triSpamFeature, triCleanFeauture
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
					else {
//						System.out.println("Token " + tokenKey + " not found in bigramFilter tokens");
					}
				}
				if( count > 0) {
					biSpamFeature /= count;
					biCleanFeature /= count;
				}
//				System.out.println("biSpamFeature : " + biSpamFeature);
//				System.out.println("biCleanFeature : " + biCleanFeature);
				
				
				// calculate bigram features biSpamFeature, biCleanFeauture
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
					else {
//						System.out.println("Token " + tokenKey + " not found in trigramFilter tokens");
					}
				}
				if( count > 0) {
					triSpamFeature /= count;
					triCleanFeature /= count;
				}
//				System.out.println("triSpamFeature : " + triSpamFeature);
//				System.out.println("triCleanFeature : " + triCleanFeature);
				

				try{
				fw.write(classNo + " 1:" + monoSpamFeature + " 2:" + monoCleanFeature +
									" 3:" + biSpamFeature + " 4:" + biCleanFeature + 
									" 5:" + triSpamFeature + " 6:" + triCleanFeature + "\n");
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
//				System.out.println("Finished for sms : " + sms);
			}
			System.out.println("Finished reading " + sourcePath);
			fw.close();

		}
		catch(IOException e){
			e.printStackTrace();
		}
		

		
	}
	
}
