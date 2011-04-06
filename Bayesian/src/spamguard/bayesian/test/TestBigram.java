package spamguard.bayesian.test;

import spamguard.bayesian.filters.BayesianFilterBigram;

public class TestBigram {
	
	public static void main(String[] args) {

		BayesianFilterBigram filter = new BayesianFilterBigram();

		filter.trainBulk("spams.txt", "spam");
		filter.trainBulk("cleans.txt", "clean");
		filter.finalizeTraining();

		double spamProb = filter.analyze("test ediyorum ben bunu. selam canım");

		System.out.println("spam prob: " + spamProb);

		if (spamProb > 0.9)
			System.out.println("spam found");
		else
			System.out.println("it's clean");

	}
}
