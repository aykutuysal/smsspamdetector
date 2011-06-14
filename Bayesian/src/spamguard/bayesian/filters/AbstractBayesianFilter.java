package spamguard.bayesian.filters;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
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
	 */
	public void trainBulk(String filePath) {
		try
		{
			Scanner scanner = new Scanner(new FileInputStream(filePath), "UTF-8").useDelimiter("\n");
			while(scanner.hasNext())
			{
				String line = scanner.next();
				this.train(line);
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
		for(String key : keys) {
			Token t = tokens.get(key);
			t.calculateSpamRatio(keys.size());
			t.calculateNonSpamRatio(keys.size());
		}
	}
	
	public Token findToken(String tokenKey) {

		return tokens.get(tokenKey);
	}
}
