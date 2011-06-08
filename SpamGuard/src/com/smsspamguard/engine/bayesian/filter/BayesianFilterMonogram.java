package com.smsspamguard.engine.bayesian.filter;

import com.smsspamguard.engine.bayesian.common.SmsFormatter;


public class BayesianFilterMonogram extends AbstractBayesianFilter{

	@Override
	public String[] returnTokenList(String message) {
		message = SmsFormatter.format(message);
		String[] tokenList = message.split("\\W");
		return tokenList;
	}
	
}
