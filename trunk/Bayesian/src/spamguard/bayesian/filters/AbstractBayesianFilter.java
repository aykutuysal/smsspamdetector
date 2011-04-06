package spamguard.bayesian.filters;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

import spamguard.bayesian.common.SmsFormatter;
import spamguard.bayesian.common.Token;

public abstract class AbstractBayesianFilter {
	
	private HashMap<String, Token> tokens;

	public abstract String[] returnTokenList(String message);
	
	public AbstractBayesianFilter() {
		this.tokens = new HashMap<String, Token>();
	}
	
	public void train(String message, String type) {
		
		message = SmsFormatter.format(message);
		
		String[] tokenList = this.returnTokenList(message);
		
		for(String str : tokenList) {
			
			if( str.length() <= 3)
				continue;
			
			str = str.toLowerCase();
			
			if(!tokens.containsKey(str)) {
				Token t = new Token(str);
				tokens.put(str, t);
			}
			
			Token temp = tokens.get(str);
			
			if( type.equals("spam") ) 
				temp.markSpam();
			else
				temp.markNonSpam();
		}	
	}
	
	/**
	 * trains the filter by reading each entry in the specified file
	 * type -> "spam" or "clean"
	 * 
	 * @param filePath
	 * @param type
	 */
	public void trainBulk(String filePath, String type) {
		int count = 0;
		try
		{
			Scanner scanner = new Scanner(new FileInputStream(filePath), "ISO-8859-9").useDelimiter("\n###SpamGuardDelimiter###\n");
			while(scanner.hasNext())
			{
				String temp = scanner.next();
				this.train(temp, type);
				count++;
			}
			System.out.println("[trainBulk] " + "Filter is trained by " + count + " " + type + "s found in " + filePath);
		}
		catch(IOException e)
		{
			System.out.println("[trainBulk] " + filePath + " can not be found!");
		}
	}
	
	public void finalizeTraining() {
		
		Set<String> keys = tokens.keySet();
		
		// find total number of spam and nonspam tokens
		// count tokens that don't have zero in spamCount field as spam tokens
		// count tokens that don't have zero in nonSpamCount field as nonspam tokens
		int spamTokenCount=0, nonSpamTokenCount=0;
		for(String key : keys) {
			if( tokens.get(key).getSpamCount() != 0 )
				spamTokenCount++;
			if( tokens.get(key).getNonSpamCount() != 0 )
				nonSpamTokenCount++;
		}
		
		for(String key : keys) {
			Token t = tokens.get(key);
			t.calculateSpamRatio(keys.size());
			t.calculateNonSpamRatio(keys.size());
			t.calculateSpammicity();
		}
	}
	
	public double analyze(String message) {

		message = SmsFormatter.format(message);
		
		String[] messageTokenList = this.returnTokenList(message);

		int limit = 150;
		
		PriorityQueue<Token> matchingTokens = new PriorityQueue<Token>(limit, new Comparator<Token>() {
			@Override
			public int compare(Token t1, Token t2) {
				if( t1.getInterestingRate() > t2.getInterestingRate() )
					return -1;
				else if( t1.getInterestingRate() < t2.getInterestingRate() )
					return +1;
				else
					return 0;
			}
		});
		
		for(String str : messageTokenList) {
			
			if( str.length() < 3 ) {
				continue;
			}
			
			if( tokens.containsKey(str) ) {
				
				Token t = tokens.get(str);				
				
				if( matchingTokens.isEmpty() ) {
					matchingTokens.add(t);
				}
				else {
				
					boolean contains = false;
					for(Token temp : matchingTokens ) {
						if( temp.getText().equals(str) )
							contains = true;
					}
					
					if( !contains && t.getInterestingRate() >= matchingTokens.peek().getInterestingRate()) {
							matchingTokens.add(t);
					}
					
					while( matchingTokens.size() > limit ) {
						matchingTokens.poll();
					}
				}
			}
		}		

		// Bayes' rule for computing overall spamicity of the message
		double spamicityProduct = 1;
		double minusOneSpamicityProduct = 1;

		while( !matchingTokens.isEmpty() ){
			Token t = matchingTokens.poll();
			spamicityProduct *= t.getSpamicity();
			minusOneSpamicityProduct *= (1.0 - t.getSpamicity());
		}
		
		double spamProbability = spamicityProduct / (spamicityProduct + minusOneSpamicityProduct);
		
		return spamProbability;
	}

	public void printTokens() {		
		Set<String> keys = tokens.keySet();
		System.out.println("----------------- All Tokens Start --------------------------------------------");
		for(String key : keys) {
			Token t = tokens.get(key);
			System.out.println(key + " " + t.getSpamCount() + " " + t.getSpamRatio() + " " + t.getNonSpamCount() + " " + t.getNonSpamRatio() + " " + t.getSpamicity() + " " + t.getInterestingRate());
		}
		System.out.println("----------------- All Tokens Finish --------------------------------------------");

	}

	public HashMap<String, Token> getTokens() {
		return tokens;
	}

	public void setTokens(HashMap<String, Token> tokens) {
		this.tokens = tokens;
	}
	
	public Token findToken(String tokenKey) {
		Set<String> keys = tokens.keySet();
		for(String key : keys){
			if( key.equalsIgnoreCase(tokenKey) )
				return tokens.get(key);
		}
		return null;
	}
}
