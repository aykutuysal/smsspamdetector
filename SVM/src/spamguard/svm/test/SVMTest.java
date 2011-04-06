package spamguard.svm.test;

import spamguard.svm.core.SVMSpam;

public class SVMTest {

	public static void main(String[] args) {
		SVMSpam svmSpam = new SVMSpam(6, "inputset1/train.1", "inputset1/test.1");
		svmSpam.start();
	}
}
