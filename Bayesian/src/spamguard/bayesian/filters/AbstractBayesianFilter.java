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
	
	public void train(String line) {
		
		line = SmsFormatter.format(line);
		String type = line.split("\\W")[0];	//get type, first word of the line
		String message = line.substring(type.length() + 1);	//get message, rest of the line
		
		String[] tokenList = this.returnTokenList(message);
		
		for(int i = 1; i < tokenList.length; i++) {
			
			if( tokenList[i].length() <= 3)
				continue;
			
			tokenList[i] = tokenList[i].toLowerCase();
			
			if(!tokens.containsKey(tokenList[i])) {
				Token t = new Token(tokenList[i]);
				tokens.put(tokenList[i], t);
			}
			
			Token temp = tokens.get(tokenList[i]);
			
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
	public void trainBulk(String filePath) {
		int count = 0;
		try
		{
			Scanner scanner = new Scanner(new FileInputStream(filePath), "ISO-8859-9").useDelimiter("\n");
			while(scanner.hasNext())
			{
				String line = scanner.next();
				this.train(line);
				count++;
			}
			//System.out.println("[trainBulk] " + "Filter is trained by " + count + " " + type + "s found in " + filePath);
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
			//t.calculateSpammicity();
		}
	}
	
//	public double analyze(String message) {
//
//		message = SmsFormatter.format(message);
//		
//		String[] messageTokenList = this.returnTokenList(message);
//
//		int limit = 150;
//		
//		PriorityQueue<Token> matchingTokens = new PriorityQueue<Token>(limit, new Comparator<Token>() {
//			@Override
//			public int compare(Token t1, Token t2) {
//				if( t1.getInterestingRate() > t2.getInterestingRate() )
//					return -1;
//				else if( t1.getInterestingRate() < t2.getInterestingRate() )
//					return +1;
//				else
//					return 0;
//			}
//		});
//		
//		for(String str : messageTokenList) {
//			
//			if( str.length() < 3 ) {
//				continue;
//			}
//			
//			if( tokens.containsKey(str) ) {
//				
//				Token t = tokens.get(str);				
//				
//				if( matchingTokens.isEmpty() ) {
//					matchingTokens.add(t);
//				}
//				else {
//				
//					boolean contains = false;
//					for(Token temp : matchingTokens ) {
//						if( temp.getText().equals(str) )
//							contains = true;
//					}
//					
//					if( !contains && t.getInterestingRate() >= matchingTokens.peek().getInterestingRate()) {
//							matchingTokens.add(t);
//					}
//					
//					while( matchingTokens.size() > limit ) {
//						matchingTokens.poll();
//					}
//				}
//			}
//		}		
//
//		// Bayes' rule for computing overall spamicity of the message
//		double spamicityProduct = 1;
//		double minusOneSpamicityProduct = 1;
//
//		while( !matchingTokens.isEmpty() ){
//			Token t = matchingTokens.poll();
//			spamicityProduct *= t.getSpamicity();
//			minusOneSpamicityProduct *= (1.0 - t.getSpamicity());
//		}
//		
//		double spamProbability = spamicityProduct / (spamicityProduct + minusOneSpamicityProduct);
//		
//		return spamProbability;
//	}
//
//	public void printTokens() {		
////		Set<String> keys = tokens.keySet();
////		System.out.println("----------------- All Tokens Start --------------------------------------------");
////		for(String key : keys) {
////			Token t = tokens.get(key);
////			System.out.println(key + " " + t.getSpamCount() + " " + t.getSpamRatio() + " " + t.getNonSpamCount() + " " + t.getNonSpamRatio() + " " + t.getSpamicity() + " " + t.getInterestingRate());
////		}
////		System.out.println("----------------- All Tokens Finish --------------------------------------------");
//
//	}
//
//	public HashMap<String, Token> getTokens() {
//		return tokens;
//	}
//
//	public void setTokens(HashMap<String, Token> tokens) {
//		this.tokens = tokens;
//	}
	
	public Token findToken(String tokenKey) {
		Set<String> keys = tokens.keySet();
		for(String key : keys){
			if( key.equals(tokenKey) )
				return tokens.get(key);
		}
		return null;
	}
}
