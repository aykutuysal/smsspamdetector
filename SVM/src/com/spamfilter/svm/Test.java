package com.spamfilter.svm;

public class Test {

	public static void main(String[] args) {
		SVMSpam svmSpam = new SVMSpam(4, "train.1", "test.1");
		svmSpam.start();
	}
}
