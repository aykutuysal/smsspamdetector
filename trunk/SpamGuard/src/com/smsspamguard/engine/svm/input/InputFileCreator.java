package com.smsspamguard.engine.svm.input;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.util.Log;

import com.smsspamguard.constant.Constants;
import com.smsspamguard.db.Database;
import com.smsspamguard.engine.bayesian.filter.BayesianFilterBigram;
import com.smsspamguard.engine.bayesian.filter.BayesianFilterTrigram;
import com.smsspamguard.model.Message;

public class InputFileCreator {

	// private BayesianFilterMonogram monogramFilter;
	private BayesianFilterBigram bigramFilter;
	private BayesianFilterTrigram trigramFilter;
	private Context context;
	private Database db;

	public InputFileCreator(Context context) {
		// this.monogramFilter = new BayesianFilterMonogram();
		this.bigramFilter = new BayesianFilterBigram();
		this.trigramFilter = new BayesianFilterTrigram();
		this.context = context;
		this.db = Database.getInstance(context);
	}

	public void createSvmInputs() {

		// Cursor cursor = db.searchSms("0", 0);
		// if (cursor.getCount() == 0) {
		// registerDefaultcorpus();
		// }
		// cursor.close();
		//
		// Log.i(Constants.DEBUG_TAG, "Starting bi train");
		// bigramFilter.train(context, "bi");
		// Log.i(Constants.DEBUG_TAG, "Starting tri train");
		// trigramFilter.train(context, "tri");

		Log.i(Constants.DEBUG_TAG, "Starting createInputData");
		createInputData(Constants.SVM_INPUT_FILENAME);

		if (db != null)
			db.close();
	}

	private void registerDefaultcorpus() {

		Log.i(Constants.DEBUG_TAG, "Default entry not found in db");
		InputStream defaultCorpusIs;
		try {
			defaultCorpusIs = context.getAssets().open("defaultCorpus", AssetManager.ACCESS_UNKNOWN);
			Scanner scanner = new Scanner(defaultCorpusIs, "UTF-8").useDelimiter("\n");
			while (scanner.hasNext()) {
				String line = scanner.next();
				String type = line.split("\\W")[0]; // get type, first word of the line
				String body = line.substring(type.length() + 1); // get message, rest of the line
				int classNo = type.equals("spam") ? 1 : 0;
				Message m = new Message("0", 0, body, classNo);
				// Log.i(Constants.DEBUG_TAG, "inserting sms: " + body);
				db.insertSms(m);
			}
		} catch (IOException e1) {
			Log.i(Constants.DEBUG_TAG, "failed in reading defaultCorpus segment in createSvmModel");
			e1.printStackTrace();
		}
		// db.close();
	}

	private void createInputData(String destPath) {
		db = Database.getInstance(context);
		Cursor cursor = db.getSmses();
		try {
			// FileInputStream fis = context.openFileInput(sourcePath);
			// Scanner scanner = new Scanner(fis, "UTF-8").useDelimiter("\n");
			//
			// FileOutputStream fos = context.openFileOutput(destPath, Context.MODE_PRIVATE);
			// while (scanner.hasNext()) {
			// String line = scanner.next();
			// String type = line.split("\\W")[0]; // get type, first word of the line
			// String sms = line.substring(type.length() + 1); // get message, rest of the line
			// int classNo = type.equals("spam") ? 1 : 0;

			FileOutputStream fos = context.openFileOutput(destPath, Context.MODE_PRIVATE);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				int type = cursor.getInt(1);
				String body = cursor.getString(0);

				// calculate monogram features monoSpamFeature, monoCleanFeauture
				// String[] monogramTokens = monogramFilter.returnTokenList(body);
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

				// calculate bigram features biSpamFeature, biCleanFeauture
				String[] bigramTokens = bigramFilter.returnTokenList(body);
				double bigramFeature = 0.0;
				// double biCleanFeature = 0.0;
				double count = 0.0;
				for (String tokenKey : bigramTokens) {
					double bigramRatio = db.getRatio(tokenKey, "bi");

					// if (bigramToken != null) {
					bigramFeature += bigramRatio;
					// biCleanFeature += bigramToken.getCleanRatio();
					count++;
					// }
				}

				if (count > 0) {
					bigramFeature /= count;
				}

				// calculate trigram features triSpamFeature, triCleanFeauture
				String[] trigramTokens = trigramFilter.returnTokenList(body);
				double trigramFeature = 0.0;
				// double triCleanFeature = 0.0;
				count = 0.0;
				for (String tokenKey : trigramTokens) {

					double trigramRatio = db.getRatio(tokenKey, "tri");

					// if (trigramToken != null) {
					trigramFeature += trigramRatio;
					// triCleanFeature += trigramToken.getCleanRatio();
					count++;
					// }
				}
				if (count > 0) {
					trigramFeature /= count;
				}

				String writeLine = type + " 1:" + bigramFeature + " 2:" + trigramFeature + /*
																							 * " 3:" + biSpamFeature + " 4:" + biCleanFeature + " 5:"
																							 * + triSpamFeature + " 6:" + triCleanFeature +
																							 */"\n";
				fos.write(writeLine.getBytes());
				cursor.moveToNext();
			}
			Log.i(Constants.DEBUG_TAG, "SVM Input File is created(" + destPath + ")");
			// fis.close();
			cursor.close();
			db.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
