package com.smsspamguard.engine.svm.core;

import java.io.FileOutputStream;
import java.io.IOException;

import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import android.content.Context;
import android.util.Log;

import com.smsspamguard.constant.Constants;

public class SVMModelHandle {

	static final String[] svm_type_table = { "c_svc", "nu_svc", "one_class", "epsilon_svr", "nu_svr" };
	static final String[] kernel_type_table = { "linear", "polynomial", "rbf", "sigmoid", "precomputed" };
	private Context context;

	public SVMModelHandle(Context context) {
		this.context = context;
	}

	public void svm_save_model(String paramString, svm_model paramsvm_model) throws IOException {

		FileOutputStream fos = context.openFileOutput(Constants.SVM_MODEL, Context.MODE_PRIVATE);

		svm_parameter localsvm_parameter = paramsvm_model.param;
		Log.i(Constants.DEBUG_TAG,"starting first line");
		fos.write(new String("svm_type " + svm_type_table[localsvm_parameter.svm_type] + "\n").getBytes());
		fos.write(new String("kernel_type " + kernel_type_table[localsvm_parameter.kernel_type] + "\n").getBytes());
		Log.i(Constants.DEBUG_TAG,"passed first line");
		if (localsvm_parameter.kernel_type == 1) {
			fos.write(new String("degree " + localsvm_parameter.degree + "\n").getBytes());
		}
		if ((localsvm_parameter.kernel_type == 1) || (localsvm_parameter.kernel_type == 2) || (localsvm_parameter.kernel_type == 3)) {
			fos.write(new String("gamma " + localsvm_parameter.gamma + "\n").getBytes());
		}
		if ((localsvm_parameter.kernel_type == 1) || (localsvm_parameter.kernel_type == 3)) {
			fos.write(new String("coef0 " + localsvm_parameter.coef0 + "\n").getBytes());
		}
		int i = paramsvm_model.nr_class;
		int j = paramsvm_model.l;
		fos.write(new String("nr_class " + i + "\n").getBytes());
		fos.write(new String("total_sv " + j + "\n").getBytes());

		fos.write(new String("rho").getBytes());
		for (int k = 0; k < i * (i - 1) / 2; ++k)
			fos.write(new String(" " + paramsvm_model.rho[k]).getBytes());
		fos.write(new String("\n").getBytes());

		if (paramsvm_model.label != null) {
			fos.write(new String("label").getBytes());
			for (int k = 0; k < i; ++k)
				fos.write(new String(" " + paramsvm_model.label[k]).getBytes());
			fos.write(new String("\n").getBytes());
		}

		if (paramsvm_model.probA != null) {
			fos.write(new String("probA").getBytes());
			for (int k = 0; k < i * (i - 1) / 2; ++k)
				fos.write(new String(" " + paramsvm_model.probA[k]).getBytes());
			fos.write(new String("\n").getBytes());
		}
		if (paramsvm_model.probB != null) {
			fos.write(new String("probB").getBytes());
			for (int k = 0; k < i * (i - 1) / 2; ++k)
				fos.write(new String(" " + paramsvm_model.probB[k]).getBytes());
			fos.write(new String("\n").getBytes());
		}

		if (paramsvm_model.nSV != null) {
			fos.write(new String("nr_sv").getBytes());
			for (int k = 0; k < i; ++k)
				fos.write(new String(" " + paramsvm_model.nSV[k]).getBytes());
			fos.write(new String("\n").getBytes());
		}

		fos.write(new String("SV\n").getBytes());
		double[][] arrayOfDouble = paramsvm_model.sv_coef;
		svm_node[][] arrayOfsvm_node = paramsvm_model.SV;

		for (int l = 0; l < j; ++l) {
			for (int i1 = 0; i1 < i - 1; ++i1) {
				fos.write(new String(arrayOfDouble[i1][l] + " ").getBytes());
			}
			svm_node[] arrayOfsvm_node1 = arrayOfsvm_node[l];
			if (localsvm_parameter.kernel_type == 4)
				fos.write(new String("0:" + (int) arrayOfsvm_node1[0].value).getBytes());
			else
				for (int i2 = 0; i2 < arrayOfsvm_node1.length; ++i2)
					fos.write(new String(arrayOfsvm_node1[i2].index + ":" + arrayOfsvm_node1[i2].value + " ").getBytes());
			fos.write(new String("\n").getBytes());
		}

		fos.close();
	}
}
