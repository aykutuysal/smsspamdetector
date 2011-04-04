package spamguard.bayesian.filters;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BayesianFilterBigram extends AbstractBayesianFilter{

	@Override
	protected String[] returnTokenList(String message) {

		ArrayList<String> strTokens = new ArrayList<String>();
		
		Pattern trigramPattern = Pattern.compile("\\w+ (?=(\\w+))");
		Matcher trigramMatcher = trigramPattern.matcher(message);
		
		while(trigramMatcher.find()) {
			strTokens.add( trigramMatcher.group() + trigramMatcher.group(1) );
		}
		
		String[] tokenList = (String[]) strTokens.toArray(new String[strTokens.size()]);
		
		return tokenList;
	}

}
