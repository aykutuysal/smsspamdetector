package com.smsspamguard.engine.svm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import libsvm.svm_node;
import android.content.Context;
import android.util.Log;

import com.smsspamguard.constant.Constants;
import com.smsspamguard.db.Database;
import com.smsspamguard.engine.bayesian.filter.BayesianFilterBigram;
import com.smsspamguard.engine.bayesian.filter.BayesianFilterTrigram;
import com.smsspamguard.engine.svm.core.SVMSpam;

public class SvmManager {

	private static SVMSpam svmSpam;

	// private static BayesianFilterMonogram monogramFilter;
	private static BayesianFilterBigram bigramFilter;
	private static BayesianFilterTrigram trigramFilter;

	// private static boolean isBayesianTrained = false;

	// public static SVMSpam getSvm(Context context) {
	// if(svmSpam == null) {
	// svmSpam = new SVMSpam(6);
	// svmSpam.setContext(context);
	// svmSpam.createSvmModel();
	// }
	// return svmSpam;
	// }

	public static svm_node[] getSvmNodeFromMessage(String msg, Context context) {

		// monogramFilter = new BayesianFilterMonogram();
		svmSpam = new SVMSpam(context);
		bigramFilter = new BayesianFilterBigram();
		trigramFilter = new BayesianFilterTrigram();
		Database db = Database.getInstance(context);

		// String[] monogramTokens = monogramFilter.returnTokenList(msg);
		// double monoSpamFeature = 0.0;
		// double monoCleanFeature = 0.0;
		// double count = 0.0;
		// for (String tokenKey : monogramTokens) {
		//
		// Token monogramToken = db.getToken(tokenKey, "mono");
		//
		// if (monogramToken != null) {
		// monoSpamFeature += monogramToken.getSpamRatio();
		// monoCleanFeature += monogramToken.getCleanRatio();
		// count++;
		// }
		// }
		// if (count > 0) {
		// monoSpamFeature /= count;
		// monoCleanFeature /= count;
		// }

		// calculate biSpamFeature, biCleanFeauture
		String[] bigramTokens = bigramFilter.returnTokenList(msg);
		double biSpamFeature = 0.0;
		// double biCleanFeature = 0.0;
		double count = 0.0;
		for (String tokenKey : bigramTokens) {

			double bigramRatio = db.getRatio(tokenKey, "bi");
			Log.i(Constants.DEBUG_TAG, String.valueOf(bigramRatio));
			if (bigramRatio != -1) {
				biSpamFeature += bigramRatio;
				// biCleanFeature += bigramToken.getCleanRatio();
				count++;
			}
		}
		if (count > 0) {
			biSpamFeature /= count;
			// biCleanFeature /= count;
		}

		// calculate trigram features triSpamFeature, triCleanFeauture
		String[] trigramTokens = trigramFilter.returnTokenList(msg);
		double triSpamFeature = 0.0;
		// double triCleanFeature = 0.0;
		count = 0.0;
		for (String tokenKey : trigramTokens) {

			double trigramRatio = db.getRatio(tokenKey, "tri");

			if (trigramRatio != -1) {
				triSpamFeature += trigramRatio;
				// triCleanFeature += trigramToken.getCleanRatio();
				count++;
			}
		}
		if (count > 0) {
			triSpamFeature /= count;
			// triCleanFeature /= count;
		}

		svm_node[] nodes = new svm_node[2];
		// svm_node node1 = new svm_node();
		// node1.index = 1;
		// node1.value = monoSpamFeature;
		//
		// svm_node node2 = new svm_node();
		// node2.index = 2;
		// node2.value = monoCleanFeature;

		svm_node node1 = new svm_node();
		node1.index = 1;
		node1.value = biSpamFeature;

		// svm_node node4 = new svm_node();
		// node4.index = 4;
		// node4.value = biCleanFeature;

		svm_node node2 = new svm_node();
		node2.index = 2;
		node2.value = triSpamFeature;

		// svm_node node6 = new svm_node();
		// node6.index = 6;
		// node6.value = triCleanFeature;

		nodes[0] = node1;
		nodes[1] = node2;
		// nodes[2] = node3;
		// nodes[3] = node4;
		// nodes[4] = node5;
		// nodes[5] = node6;

		Log.i(Constants.DEBUG_TAG, "Features for message : " + msg);
		Log.i(Constants.DEBUG_TAG, "BiSpamFeature : " + node1.value);
		Log.i(Constants.DEBUG_TAG, "TriSpamFeature : " + node2.value);
		// Log.i(Constants.DEBUG_TAG, "BiSpamFeature : " + node3.value);
		// Log.i(Constants.DEBUG_TAG, "BiCleanFeature : " + node4.value);
		// Log.i(Constants.DEBUG_TAG, "TriSpamFeature : " + node5.value);
		// Log.i(Constants.DEBUG_TAG, "TriCleanFeature : " + node6.value);

		//return nodes;


		FileOutputStream fos;
		try {
			fos = context.openFileOutput(Constants.SVM_SINGLE_MSG_FEATURE_FILE, Context.MODE_PRIVATE);

			fos.write("1 ".getBytes());
			for (int i = 0; i < nodes.length; i++) {
				svm_node node = nodes[i];
				String feature = node.index + ":" + node.value;

				if (i < nodes.length - 1)
					feature += " ";

				fos.write(feature.getBytes());
			}
			fos.close();

//			FileInputStream fis = context.openFileInput(Constants.SVM_SINGLE_MSG_FEATURE_FILE);
//			BufferedReader br = new BufferedReader(new InputStreamReader(fis), 1024);
//			String line = br.readLine();
//			Log.i(Constants.DEBUG_TAG, "Line from single feature file : " + line);
//			fis.close();

			// create scaled file from the newly created file
			svmSpam.scaleSingle(Constants.SVM_SINGLE_MSG_FEATURE_FILE);

		} catch (FileNotFoundException e) {
			Log.i(Constants.DEBUG_TAG, "Cannot create single message feature file");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			// Read single msg featue file scaled
			FileInputStream fis = context.openFileInput(Constants.SVM_SINGLE_MSG_FEATURE_FILE_SCALED);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis), 1024);
			String line = br.readLine();
			Log.i(Constants.DEBUG_TAG, "Line from single feature scaled file : " + line);

			// Extracting scaled index and value pairs from file
			StringTokenizer tokenizer = new StringTokenizer(line, " \t\n\r\f:");
			tokenizer.nextToken();
			svm_node[] scaledNodes = new svm_node[nodes.length];
			int index = 0;
			while (tokenizer.hasMoreTokens()) {
				svm_node scaledNode = new svm_node();
				scaledNode.index = Integer.parseInt(tokenizer.nextToken());
				scaledNode.value = Double.parseDouble(tokenizer.nextToken());
				scaledNodes[index++] = scaledNode;
			}

			// DEBUG logs
			Log.i(Constants.DEBUG_TAG, "Extracted scaled svm_nodes : ");
			for (svm_node n : scaledNodes) {
				Log.i(Constants.DEBUG_TAG, n.index + ":" + n.value);
			}

			br.close();
			fis.close();

			return scaledNodes;
		} catch (FileNotFoundException e) {
			Log.i(Constants.DEBUG_TAG, "FileNotFound: " + Constants.SVM_SINGLE_MSG_FEATURE_FILE_SCALED);
			e.printStackTrace();
		} catch (IOException e) {
			Log.i(Constants.DEBUG_TAG, "Could not read the line from file: " + Constants.SVM_SINGLE_MSG_FEATURE_FILE_SCALED);
			e.printStackTrace();
		}

		return null;

	}

}
