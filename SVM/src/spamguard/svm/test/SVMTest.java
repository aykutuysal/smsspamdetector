package spamguard.svm.test;

import spamguard.svm.core.SVMSpam;
import spamguard.svm.input.InputFileCreator;

public class SVMTest {

	public static void main(String[] args) {
		
		InputFileCreator.start();
		
		SVMSpam svmSpam = new SVMSpam(6, "inputset/train", "inputset/test");
		svmSpam.start();
	}
}