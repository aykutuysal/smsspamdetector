package aykut.bayesianspamfilter;

import java.util.Scanner;
import java.io.FileInputStream;
import java.io.IOException;

public class Test {

	public static void main(String[] args) {
		
		BayesianFilter filter = new BayesianFilter();
		int spamCount = 0, cleanCount = 0;
		
		try
		{
			//check for GSM 7-bit encoding
			Scanner scanner = new Scanner(new FileInputStream("spams.txt"), "UTF-8").useDelimiter("\n###Spam Filter Delimiter###\n");
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
			Scanner scanner = new Scanner(new FileInputStream("cleans.txt"), "UTF-8").useDelimiter("\r\n###Clean Set Delimiter###\r\n");
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
		
		double spamProb = filter.analyze("hadi olm indirim bitecek");
		
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
