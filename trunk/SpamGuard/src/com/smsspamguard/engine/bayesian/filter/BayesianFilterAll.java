package com.smsspamguard.engine.bayesian.filter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.smsspamguard.engine.bayesian.common.SmsFormatter;

public class BayesianFilterAll extends AbstractBayesianFilter2 {

	@Override
	public String[] returnTokenList(String message) {
		message = SmsFormatter.format(message);
		ArrayList<String> strTokens = new ArrayList<String>();

		Pattern bigramPattern = Pattern.compile("\\w+ (?=(\\w+))");
		Matcher bigramMatcher = bigramPattern.matcher(message);

		while (bigramMatcher.find()) {
			strTokens.add(bigramMatcher.group());
			strTokens.add(bigramMatcher.group() + bigramMatcher.group(1));
		}

		Pattern trigramPattern = Pattern.compile("[^\\s]+?\\s+(?=([^\\s]+?\\s+[^\\s]+))");
		Matcher trigramMatcher = trigramPattern.matcher(message);
		
		while(trigramMatcher.find()) {
			strTokens.add( trigramMatcher.group() + trigramMatcher.group(1) );
		}
		
		String[] tokenList = (String[]) strTokens.toArray(new String[strTokens.size()]);

		return tokenList;

	}

}
