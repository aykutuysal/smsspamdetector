package spamguard.svm.test;

import spamguard.svm.core.SVMSpam;

public class SVMTest {

	public static void main(String[] args) {
		SVMSpam svmSpam = new SVMSpam(6, "inputset2/train.2", "inputset2/test.2");
		svmSpam.start();
	}
}