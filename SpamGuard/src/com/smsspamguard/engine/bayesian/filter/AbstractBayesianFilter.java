//package com.smsspamguard.engine.bayesian.filter;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Scanner;
//import java.util.Set;
//
//import android.content.Context;
//
//import com.smsspamguard.engine.bayesian.common.SmsFormatter;
//import com.smsspamguard.engine.bayesian.common.Token;
//
//public abstract class AbstractBayesianFilter {
//	
//	private HashMap<String, Token> tokens;
//
//	public abstract String[] returnTokenList(String message);
//	
//	public AbstractBayesianFilter() {
//		this.tokens = new HashMap<String, Token>();
//	}
//	
//	public void train(String line) {
//		
//		line = SmsFormatter.format(line);
//		String type = line.split("\\W")[0];	//get type, first word of the line
//		String message = line.substring(type.length() + 1);	//get message, rest of the line
//		String[] tokenList = this.returnTokenList(message);
//		
//		for(String str : tokenList) {
//			
//			if( str.length() <= 3)
//				continue;
//			
//			str = str.toLowerCase();
//			
//			if(!tokens.containsKey(str)) {
//				Token t = new Token(str);
//				tokens.put(str, t);
//			}
//			
//			Token temp = tokens.get(str);
//			
//			if( type.equals("spam") ) 
//				temp.markSpam();
//			else
//				temp.markNonSpam();
//		}	
//	}
//	
//	/**
//	 * trains the filter by reading each entry in the specified file
//	 * type -> "spam" or "clean"
//	 * 
//	 * @param filePath
//	 */
//	public void trainBulk(String filePath, Context context) {
//		try
//		{
//			FileInputStream fis = context.openFileInput(filePath);
//			Scanner scanner = new Scanner(fis, "UTF-8").useDelimiter("\n");
//			while(scanner.hasNext())
//			{
//				String line = scanner.next();
//				this.train(line);
//			}
//			//System.out.println("[trainBulk] " + "Filter is trained by " + count + " " + type + "s found in " + filePath);
//		}
//		catch(IOException e)
//		{
//			System.out.println("[trainBulk] " + filePath + " can not be found!");
//		}
//	}
//	
//	public void finalizeTraining() {
//		
//		Set<String> keys = tokens.keySet();
//		for(String key : keys) {
//			Token t = tokens.get(key);
//			t.calculateSpamRatio(keys.size());
//			t.calculateNonSpamRatio(keys.size());
//		}
//	}
//	
//	public Token findToken(String tokenKey) {
//
//		return tokens.get(tokenKey);
//	}
//}
