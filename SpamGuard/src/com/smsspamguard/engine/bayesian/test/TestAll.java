package com.smsspamguard.engine.bayesian.test;

import com.smsspamguard.engine.bayesian.filter.BayesianFilterAll;

public class TestAll {

	public static void main(String[] args) {

		BayesianFilterAll filter = new BayesianFilterAll();

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
