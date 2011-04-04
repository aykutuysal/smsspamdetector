package spamguard.bayesian.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import spamguard.bayesian.filters.BayesianFilterBigram;

public class TestBigram {
public static void main(String[] args) {
		
		BayesianFilterBigram filter = new BayesianFilterBigram();
		int spamCount = 0, cleanCount = 0;
		
		try
		{
			//check for GSM 7-bit encoding
			Scanner scanner = new Scanner(new FileInputStream("spams.txt"), "ISO-8859-9").useDelimiter("\n###Spam Filter Delimiter###\n");
			String hede = "";
			while(scanner.hasNext())
			{
				hede = scanner.next();
				filter.train(hede, "spam");
				spamCount++;
				//System.out.println(hede);
			}
		}
		catch(IOException e)
		{
			System.out.println("spams.txt not found!");
		}
		
		try
		{
			//check for GSM 7-bit encoding
			Scanner scanner = new Scanner(new FileInputStream("cleans.txt"), "ISO-8859-9").useDelimiter("\r\n###Clean Set Delimiter###\r\n");
			String hede = "";
			while(scanner.hasNext())
			{
				hede = scanner.next();
				filter.train(hede, "clean");
				cleanCount++;
				//System.out.println(hede);
			}
		}
		catch(IOException e)
		{
			System.out.println("cleans.txt not found!");
		}
		
		filter.finalizeTraining();
		
		filter.printTokens();
		
		double spamProb = filter.analyze("");
		
		System.out.println("Spam DB count: " + spamCount);
		System.out.println("Clean DB count: " + cleanCount);
		
		System.out.println("spam prob: " + spamProb);
		//filter.printTokens();
		
		if( spamProb > 0.9 )
			System.out.println("spam found");
		else
			System.out.println("it's clean");
		
	}
}
