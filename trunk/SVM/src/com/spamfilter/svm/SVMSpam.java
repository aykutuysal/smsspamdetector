package com.spamfilter.svm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class SVMSpam {

	private svm_problem svmSpamTrainProblem;
	private svm_problem svmSpamTestProblem;
	private svm_parameter svmSpamParameter;
	private svm_model svmSpamModel;
	
	private int featureCount;
	
	
	
	public SVMSpam(int featureCount) {
		this.featureCount = featureCount;
	}

	
	public void start() {
		
		this.svmSpamTrainProblem = readInput("data/train.1");
		this.svmSpamParameter = createSvmParameter();
		this.svmSpamModel = train();
		this.svmSpamTestProblem = readInput("data/test.1");
		
		predict();
		
		
		
		
		
//		double[] target = new double[svmSpamProblem.l];
//		svm.svm_cross_validation(svmSpamProblem, svmSpamParameter, 5, target );
//		
////		System.out.println("Target array-----------------");
////		for(int i=0;i<target.length;i++)
////			System.out.println(target[i] + " - " + svmSpamProblem.y[i]);
////		System.out.println("target finished ----------------------");
//		System.out.println("Cross Validation Accuracy : " + calculateCrossValidationAccuracy(target));
//		
//		//train();
		
	}
	
	public svm_model train() {
		return svm.svm_train(svmSpamTrainProblem, svmSpamParameter);
	}
	
	public void predict() {
		
		try {
			
			FileWriter output = new FileWriter(new File("data/out.txt"));
			
			int count = 0;
			
			for(int i=0;i<featureCount;i++) {
				double result = svm.svm_predict(svmSpamModel, svmSpamTestProblem.x[i]);
				if( result == svmSpamTestProblem.y[i] )
				{
					count++;
				}
				output.write(String.valueOf(result));
			}
			
			System.out.println("Predict Accuracy : %" + count/svmSpamTestProblem.l*100);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
	
	public svm_parameter createSvmParameter() {
		svm_parameter svmParameter = new svm_parameter();
		svmParameter.svm_type = svm_parameter.C_SVC;
		svmParameter.kernel_type = svm_parameter.RBF;
		svmParameter.cache_size = 100;
		svmParameter.eps = 0.001;
		svmParameter.nr_weight = 1;
		svmParameter.probability = 0;
		svmParameter.shrinking = 1;
		svmParameter.gamma = 1/featureCount;
		//svmParameter.C = 3;
		return svmParameter;
	}
	
	public svm_problem readInput(String path) {
		
		try {
			File trainingFile = new File(path);
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
			
			svm_problem svmProblem = new svm_problem();
			svmProblem.l = length;
			svmProblem.y = yList;
			svmProblem.x = nodes;	
			
			return svmProblem;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	public double calculateCrossValidationAccuracy(double[] target) {
		int totalMatch = 0;
		for(int i=0;i<target.length;i++) {
			
			if(target[i] == svmSpamTrainProblem.y[i] ){
				totalMatch++;
			}
			
		}
		return totalMatch*100/ (double)target.length+1;
	}
	

	public svm_problem getSvmSpamProblem() {
		return svmSpamTrainProblem;
	}

	public void setSvmSpamProblem(svm_problem svmSpamProblem) {
		this.svmSpamTrainProblem = svmSpamProblem;
	}

	public svm_parameter getSvmSpamParameter() {
		return svmSpamParameter;
	}

	public void setSvmSpamParameter(svm_parameter svmSpamParameter) {
		this.svmSpamParameter = svmSpamParameter;
	}
	
	
}
