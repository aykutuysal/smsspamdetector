package com.smsspamguard.engine.svm;

import android.content.Context;

import com.smsspamguard.constant.Constants;
import com.smsspamguard.engine.svm.core.SVMSpam;

public class SvmManager {

	private static SVMSpam svmSpam;

	public static SVMSpam getSvm(Context context) {
		if(svmSpam == null) {
			svmSpam = new SVMSpam(6, Constants.SVM_INPUT_FILENAME, "");
			svmSpam.setContext(context);
			return svmSpam;
		}
		else {
			return svmSpam;
		}
	}
}
