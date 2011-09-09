package com.smsspamguard.engine.bayesian.filter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.smsspamguard.engine.bayesian.common.SmsFormatter;

public class BayesianFilterTrigram extends AbstractBayesianFilter2 {

	@Override
	public String[] returnTokenList(String message) {
		
		message = SmsFormatter.format(message);
		
		ArrayList<String> strTokens = new ArrayList<String>();
		
		Pattern trigramPattern = Pattern.compile("[^\\s]+?\\s+(?=([^\\s]+?\\s+[^\\s]+))");
		Matcher trigramMatcher = trigramPattern.matcher(message);
		
		while(trigramMatcher.find()) {
			strTokens.add( trigramMatcher.group() + trigramMatcher.group(1) );
		}
		
		String[] tokenList = (String[]) strTokens.toArray(new String[strTokens.size()]);
		
		return tokenList;
	}

}
