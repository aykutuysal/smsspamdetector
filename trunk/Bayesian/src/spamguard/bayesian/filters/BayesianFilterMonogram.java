package spamguard.bayesian.filters;


public class BayesianFilterMonogram extends AbstractBayesianFilter{

	@Override
	protected String[] returnTokenList(String message) {
		String[] tokenList = message.split("\\W");
		return tokenList;
	}
	
}
