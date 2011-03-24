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
	private SVMScale scaler;
	
	private int featureCount;
	
	public SVMSpam(int featureCount) {
		this.featureCount = featureCount;
		this.scaler = new SVMScale();
	}

	
	public void start() {
		
		
		try {
			this.scaler.scale("data/range", null, "data/train.1", -1.0, 1.0);
			this.scaler.scale(null, "data/range", "data/test.1", -1.0, 1.0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		this.svmSpamTrainProblem = readInput("data/train.1.scaled");
		this.svmSpamParameter = createSvmParameter();
		
		
		
		do_cross_validation();
		
		this.svmSpamModel = train();
		
		this.svmSpamTestProblem = readInput("data/test.1.scaled");
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
			
			double count = 0;
			
			for(int i=0;i<svmSpamTestProblem.l;i++) {
				double result = svm.svm_predict(svmSpamModel, svmSpamTestProblem.x[i]);
//				System.out.println(result);
				output.write(String.valueOf(result) + "\n");
				if( result == svmSpamTestProblem.y[i] )
				{
					count++;
				}
			}
			
			System.out.println("Predict Accuracy : %" + count/(double)svmSpamTestProblem.l*100);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
	
	private void do_cross_validation()
	  {
	    int j = 0;
	    double d1 = 0.0D;
	    double d2 = 0.0D; double d3 = 0.0D; double d4 = 0.0D; double d5 = 0.0D; double d6 = 0.0D;
	    double[] arrayOfDouble = new double[this.svmSpamTrainProblem.l];

	    svm.svm_cross_validation(this.svmSpamTrainProblem, this.svmSpamParameter, 5, arrayOfDouble);
	    int i;
	    if ((this.svmSpamParameter.svm_type == 3) || (this.svmSpamParameter.svm_type == 4))
	    {
	      for (i = 0; i < this.svmSpamTrainProblem.l; i++)
	      {
	        double d7 = this.svmSpamTrainProblem.y[i];
	        double d8 = arrayOfDouble[i];
	        d1 += (d8 - d7) * (d8 - d7);
	        d2 += d8;
	        d3 += d7;
	        d4 += d8 * d8;
	        d5 += d7 * d7;
	        d6 += d8 * d7;
	      }
	      System.out.print("Cross Validation Mean squared error = " + d1 / this.svmSpamTrainProblem.l + "\n");
	      System.out.print("Cross Validation Squared correlation coefficient = " + (this.svmSpamTrainProblem.l * d6 - d2 * d3) * (this.svmSpamTrainProblem.l * d6 - d2 * d3) / ((this.svmSpamTrainProblem.l * d4 - d2 * d2) * (this.svmSpamTrainProblem.l * d5 - d3 * d3)) + "\n");
	    }
	    else
	    {
	      for (i = 0; i < this.svmSpamTrainProblem.l; i++)
	        if (arrayOfDouble[i] == this.svmSpamTrainProblem.y[i])
	          j++;
	      System.out.print("Cross Validation Accuracy = " + 100.0D * j / this.svmSpamTrainProblem.l + "%\n");
	    }
	  }
	
	
	public svm_parameter createSvmParameter() {
		svm_parameter svmParameter = new svm_parameter();
		svmParameter.svm_type = 0;
	    svmParameter.kernel_type = 2;
	    svmParameter.degree = 3;
	    svmParameter.gamma = 1.0/(double)featureCount;
	    svmParameter.coef0 = 0.0D;
	    svmParameter.nu = 0.5D;
	    svmParameter.cache_size = 100.0D;
	    svmParameter.C = 1.0D;
	    svmParameter.eps = 0.001D;
	    svmParameter.p = 0.1D;
	    svmParameter.shrinking = 1;
	    svmParameter.probability = 0;
	    svmParameter.nr_weight = 0;
	    svmParameter.weight_label = new int[0];
	    svmParameter.weight = new double[0];
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
//					System.out.print(i + ". " + yList[i] + " " + nodes[i][j].index + ":" + nodes[i][j].value + " ");
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
