package spamguard.svm.test;

import spamguard.svm.core.SVMSpam;
import spamguard.svm.input.InputFileCreator;

public class SVMTest {

	public static void main(String[] args) {
		
		InputFileCreator.start();
		
		SVMSpam svmSpam = new SVMSpam(6, "inputset/train", "inputset/test");
		svmSpam.start();
//		svmSpam.getSvmSpamProblem();
		
//		svm_problem problem = svmSpam.getSvmSpamProblem();
//		System.out.println("Problem l: " + problem.l);
//		
//		double[] ys = problem.y;
//		System.out.println("Problem y values: ");
//		for(double d : ys)
//			System.out.println(""+d);
//		
//		svm_node[][] xs = problem.x;
//		System.out.println("Problem x values: ");
//		for(int i=0;i<xs.length;i++) {
//			for(int j=0;j<xs[i].length;j++) {
//				System.out.print(xs[i][j].index + ":" + xs[i][j].value);
//			}
//			System.out.print("\n");
//		}
	}
}
