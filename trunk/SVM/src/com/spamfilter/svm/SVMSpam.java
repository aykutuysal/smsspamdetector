package com.spamfilter.svm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import libsvm.svm;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class SVMSpam {

	private svm_problem svmSpamProblem;
	private svm_parameter svmSpamParameter;
	
	private int featureCount;
	
	
	
	public SVMSpam(int featureCount) {
		this.featureCount = featureCount;
		this.svmSpamParameter = new svm_parameter();
		this.svmSpamProblem = new svm_problem();
	}

	
	public void start() {
		readInput();
		createSvmParameter();
		
		double[] target = new double[svmSpamProblem.l];
		svm.svm_cross_validation(svmSpamProblem, svmSpamParameter, 5, target );
		
//		System.out.println("Target array-----------------");
//		for(int i=0;i<target.length;i++)
//			System.out.println(target[i] + " - " + svmSpamProblem.y[i]);
//		System.out.println("target finished ----------------------");
		System.out.println("Cross Validation Accuracy : " + calculateCrossValidationAccuracy(target));
		
		//train();
		
	}
	
	public void train() {
		svm.svm_train(svmSpamProblem, svmSpamParameter);
	}
	
	public void readInput() {
		
		
		try {
			File trainingFile = new File("data/train.1");
			Scanner scanner = new Scanner(trainingFile);
			
			// length of the input
			int length = 0;
			while( scanner.hasNext() ) {
				scanner.nextLine();
				length++;
			}
		
			scanner.close();
			scanner = new Scanner(trainingFile);
			
			double[] yList = new double[length];
			int index = 0;
			
			svm_node[][] nodes = new svm_node[length][featureCount];
			while( scanner.hasNext() ) {
				String line = scanner.nextLine();	
				String[] instanceValues = line.split(" .:");
				yList[index] = Double.parseDouble(instanceValues[0]);
				
				for(int i=1;i<instanceValues.length;i++) {
					svm_node node = new svm_node();
					node.index = i;
					node.value = Double.parseDouble(instanceValues[i]);
					nodes[index][i-1] = node;
				}
				index++;
			}

//			//print nodes
//			for(int i=0;i<length;i++) {
//				for(int j=0;j<featureCount;j++) {
//					System.out.print(nodes[i][j].index + "," + nodes[i][j].value + " ");
//				}
//				System.out.println();
//			}
			
			svmSpamProblem.l = length;
			svmSpamProblem.y = yList;
			svmSpamProblem.x = nodes;	
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}


	public void createSvmParameter() {
		svmSpamParameter.svm_type = svm_parameter.C_SVC;
		svmSpamParameter.kernel_type = svm_parameter.RBF;
		svmSpamParameter.cache_size = 10;
		svmSpamParameter.eps = 0.001;
		svmSpamParameter.nr_weight = 0;
		svmSpamParameter.probability = 0;
		svmSpamParameter.shrinking = 0;
		svmSpamParameter.gamma = 1/featureCount;
		svmSpamParameter.C = 3;
	}
	
	public double calculateCrossValidationAccuracy(double[] target) {
		int totalMatch = 0;
		for(int i=0;i<target.length;i++) {
			
			if(target[i] == svmSpamProblem.y[i] ){
				totalMatch++;
			}
			
		}
		return totalMatch*100/ (double)target.length+1;
	}
	

	public svm_problem getSvmSpamProblem() {
		return svmSpamProblem;
	}

	public void setSvmSpamProblem(svm_problem svmSpamProblem) {
		this.svmSpamProblem = svmSpamProblem;
	}

	public svm_parameter getSvmSpamParameter() {
		return svmSpamParameter;
	}

	public void setSvmSpamParameter(svm_parameter svmSpamParameter) {
		this.svmSpamParameter = svmSpamParameter;
	}
	
	
}
