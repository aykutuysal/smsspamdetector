package com.smsspamguard.engine.svm.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import android.content.Context;
import android.util.Log;

import com.smsspamguard.constant.Constants;

public class SVMSpam {

	private svm_problem svmSpamTrainProblem;
	private svm_problem svmSpamTestProblem;
	private svm_parameter svmSpamParameter;
	private svm_model svmSpamModel;
	private SVMScale scaler;

	private final int featureCount = 2;

	private Context context;

	public SVMSpam(Context context) {
		this.scaler = new SVMScale();
		this.context = context;
	}

	// public Context getContext() {
	// return context;
	// }
	//
	// public void setContext(Context context) {
	// this.context = context;
	// }

	public void loadSvmModel() {
		try {
			String path = context.getFileStreamPath(Constants.SVM_MODEL).getAbsolutePath();
			this.svmSpamModel = svm.svm_load_model(path);
			Log.i(Constants.DEBUG_TAG, "SAVED SVM MODEL USED");
		} catch (IOException e) {
			Log.i(Constants.DEBUG_TAG, "NO SVM MODEL FOUND");
			createSvmModel();
			e.printStackTrace();
		}
	}

	public void createSvmModel() {

		try {
			Log.i(Constants.DEBUG_TAG, "Scaling started");
			// this.scaler.scale("data/range", null, "data/" + trainFile, -1.0, 1.0);
			this.scaler.scale(Constants.SVM_RANGE_SAVE_PATH, null, Constants.SVM_INPUT_FILENAME, -1.0, 1.0, context);
			// this.scaler.scale(null, "data/range", "data/" + testFile, -1.0, 1.0);
			// this.scaler.scale(null,Constants.SVM_RANGE_LOAD_PATH, testFile, -1.0, 1.0,context);
			Log.i(Constants.DEBUG_TAG, "Scaling Finished");
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.i(Constants.DEBUG_TAG, "Creating svmProblem");
		this.svmSpamTrainProblem = readInput(Constants.SVM_INPUT_FILENAME + ".scaled");

		Log.i(Constants.DEBUG_TAG, "Creating svmParameter");
		this.svmSpamParameter = createSvmParameter();

		Log.i(Constants.DEBUG_TAG, "Cross validation");
		do_cross_validation();

		Log.i(Constants.DEBUG_TAG, "Starting SVM training");
		this.svmSpamModel = train();
		Log.i(Constants.DEBUG_TAG, "SVM Training has finished.");

		try {
			SVMModelHandle modelHandler = new SVMModelHandle(context);
			modelHandler.svm_save_model(Constants.SVM_MODEL, this.svmSpamModel);
			Log.i(Constants.DEBUG_TAG, "SVM MODEL SAVED");
		} catch (IOException e) {
			Log.i(Constants.DEBUG_TAG, "SVM MODEL COULD NOT BE SAVED :(");
			e.printStackTrace();
		}

		// this.svmSpamTestProblem = readInput("data/" + testFile + ".scaled");
		// predict();
		// double[] target = new double[svmSpamProblem.l];
		// svm.svm_cross_validation(svmSpamProblem, svmSpamParameter, 5, target );
		//
		// // System.out.println("Target array-----------------");
		// // for(int i=0;i<target.length;i++)
		// // System.out.println(target[i] + " - " + svmSpamProblem.y[i]);
		// // System.out.println("target finished ----------------------");
		// System.out.println("Cross Validation Accuracy : " + calculateCrossValidationAccuracy(target));
		//
		// //train();

	}

	public svm_model train() {
		return svm.svm_train(svmSpamTrainProblem, svmSpamParameter);
	}

	public void scaleSingle(String filePath) {
		try {
			this.scaler.scale(null, Constants.SVM_RANGE_LOAD_PATH, filePath, -1.0, 1.0, context);
		} catch (IOException e) {
			Log.i(Constants.DEBUG_TAG, "Cannot open: " + filePath);
			e.printStackTrace();
		}
	}

	public double predictSingle(svm_node[] nodes) {

		boolean isAllLowerBound = true;
		for (svm_node node : nodes) {
			if (node.value != 0.0)
				isAllLowerBound = false;
		}

		// if all features are the lower bound value of scale
		// then svm does not know anything about the message
		// so it should return a default value, which is zero(clean)
		if (isAllLowerBound)
			return 0;
		else
			return svm.svm_predict(svmSpamModel, nodes);
	}

	public void predictMultiple() {

		try {

			FileWriter output = new FileWriter(new File("data/predictResults.txt"));

			double count = 0;
			output.write("Real - Predicted\n");
			for (int i = 0; i < svmSpamTestProblem.l; i++) {
				double result = svm.svm_predict(svmSpamModel, svmSpamTestProblem.x[i]);
				output.write(svmSpamTestProblem.y[i] + "  -  " + String.valueOf(result) + "\n");
				if (result == svmSpamTestProblem.y[i]) {
					count++;
				}
			}

			System.out.println("Predict Accuracy : %" + count / (double) svmSpamTestProblem.l * 100);
			output.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void do_cross_validation() {
		int j = 0;
		double d1 = 0.0D;
		double d2 = 0.0D;
		double d3 = 0.0D;
		double d4 = 0.0D;
		double d5 = 0.0D;
		double d6 = 0.0D;
		double[] arrayOfDouble = new double[this.svmSpamTrainProblem.l];

		svm.svm_cross_validation(this.svmSpamTrainProblem, this.svmSpamParameter, 5, arrayOfDouble);
		int i;
		if ((this.svmSpamParameter.svm_type == 3) || (this.svmSpamParameter.svm_type == 4)) {
			for (i = 0; i < this.svmSpamTrainProblem.l; i++) {
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
			System.out.print("Cross Validation Squared correlation coefficient = " + (this.svmSpamTrainProblem.l * d6 - d2 * d3)
					* (this.svmSpamTrainProblem.l * d6 - d2 * d3)
					/ ((this.svmSpamTrainProblem.l * d4 - d2 * d2) * (this.svmSpamTrainProblem.l * d5 - d3 * d3)) + "\n");
		} else {
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
		svmParameter.gamma = 1.0 / (double) featureCount;
		svmParameter.coef0 = 0.0D;
		svmParameter.nu = 0.5D;
		svmParameter.cache_size = 15.0D;
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
			Log.i(Constants.DEBUG_TAG, "SVM Read Input Started");
			Log.i(Constants.DEBUG_TAG, "Opening file stream : " + path);
			FileInputStream fis = context.openFileInput(path);
			Scanner scanner = new Scanner(fis);

			// length of the input
			int length = 0;
			while (scanner.hasNext()) {
				scanner.nextLine();
				length++;
			}

			scanner.close();
			fis.close();

			Log.i(Constants.DEBUG_TAG, "Length calculated: " + length);

			Log.i(Constants.DEBUG_TAG, "Opening file stream again : " + path);
			FileInputStream fis2 = context.openFileInput(path);
			Scanner scanner2 = new Scanner(fis2);

			double[] yList = new double[length];
			int index = 0;

			svm_node[][] nodes = new svm_node[length][featureCount];
			while (scanner2.hasNext()) {
				String line = scanner2.nextLine();
				// Log.i(Constants.DEBUG_TAG,"Line is read: " + line);

				String[] instanceValues = line.split(" .:");
				yList[index] = Double.parseDouble(instanceValues[0]);

				// Log.i(Constants.DEBUG_TAG,"InstanceValues starting:");
				// for(int i=1;i<instanceValues.length;i++) {
				// Log.i(Constants.DEBUG_TAG,i + " " + Double.parseDouble(instanceValues[i]));
				// }

				for (int i = 1; i < instanceValues.length; i++) {
					svm_node node = new svm_node();
					node.index = i;
					node.value = Double.parseDouble(instanceValues[i]);
					nodes[index][i - 1] = node;
				}
				index++;
			}
			scanner2.close();
			fis2.close();
			// Log.i(Constants.DEBUG_TAG,"YList Length: " + yList.length);

			// for(int i=0;i<yList.length;i++) {
			// Log.i(Constants.DEBUG_TAG, ""+yList[i]);
			// }

			// //print nodes
			// for(int i=0;i<length;i++) {
			// for(int j=0;j<featureCount;j++) {
			// System.out.print(i + ". " + yList[i]);
			// System.out.print(" " + nodes[i][j].index + ":");
			// System.out.print(nodes[i][j].value + " ");
			// System.out.println("i = " + i);
			// }
			// System.out.println();
			// }

			svm_problem svmProblem = new svm_problem();
			svmProblem.l = length;
			svmProblem.y = yList;
			svmProblem.x = nodes;

			scanner2.close();
			fis2.close();

			return svmProblem;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

	public double calculateCrossValidationAccuracy(double[] target) {
		int totalMatch = 0;
		for (int i = 0; i < target.length; i++) {

			if (target[i] == svmSpamTrainProblem.y[i]) {
				totalMatch++;
			}

		}
		return totalMatch * 100 / (double) target.length + 1;
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

	public int getFeatureCount() {
		return featureCount;
	}

}
