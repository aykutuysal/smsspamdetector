package com.smsspamguard.engine.svm.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Formatter;
import java.util.StringTokenizer;

import android.content.Context;

public class SVMScale {

	private int max_index;
	private String line;
	private long num_nonzeros;
	private double[] feature_max;
	private double[] feature_min;
//	private double y_max;
//	private double y_min;
//	private double y_lower;
//	private double y_upper;
//	private boolean y_scaling;
	private double lower;
	private double upper;
	private long new_num_nonzeros;
	
	private static int DEFUALT_BUFF_SIZE = 8*1024;

	private String readline(BufferedReader paramBufferedReader)
			throws IOException {
		this.line = paramBufferedReader.readLine();
		return this.line;
	}

	private BufferedReader rewind(BufferedReader paramBufferedReader,
			String paramString, Context context) throws IOException {
		paramBufferedReader.close();
		FileInputStream fis = context.openFileInput(paramString);
		return new BufferedReader(new InputStreamReader(fis),DEFUALT_BUFF_SIZE);
	}

	public void scale(String rangeSavePath, String rangeLoadPath,
			String inputPath, double lowerBound, double upperBound, Context context)
			throws IOException {

		BufferedReader localBufferedReader1 = null;
		BufferedReader localBufferedReader2 = null;

		this.lower = lowerBound;
		this.upper = upperBound;
		String str1 = rangeSavePath;
		String str2 = rangeLoadPath;
		String str3 = inputPath;

		FileInputStream fis = context.openFileInput(str3);
		localBufferedReader1 = new BufferedReader(new InputStreamReader(fis),DEFUALT_BUFF_SIZE);
		if (this.upper <= this.lower) {
			System.err.println("inconsistent lower/upper specification");
			System.exit(1);
		}
		if ((str2 != null) && (str1 != null)) {
			System.err.println("cannot use -r and -s simultaneously");
			System.exit(1);
		}
		this.max_index = 0;

		if (str2 != null) {
			try {
				FileInputStream fis2 = context.openFileInput(str2);
				localBufferedReader2 = new BufferedReader(new InputStreamReader(fis2),DEFUALT_BUFF_SIZE);
			} catch (Exception localException2) {
				System.err.println("can't open file " + str2);
				System.exit(1);
			}
			int i2;
			if ((i2 = localBufferedReader2.read()) == 121) {
				localBufferedReader2.readLine();
				localBufferedReader2.readLine();
				localBufferedReader2.readLine();
			}
			localBufferedReader2.readLine();
			localBufferedReader2.readLine();

			String str4 = null;
			while ((str4 = localBufferedReader2.readLine()) != null) {
				StringTokenizer localStringTokenizer2 = new StringTokenizer(
						str4);
				int k = Integer.parseInt(localStringTokenizer2.nextToken());
				this.max_index = Math.max(this.max_index, k);
			}
			localBufferedReader2 = rewind(localBufferedReader2, str2, context);
		}
		int j;
		while (readline(localBufferedReader1) != null) {
			StringTokenizer localStringTokenizer1 = new StringTokenizer(
					this.line, " \t\n\r\f:");
			localStringTokenizer1.nextToken();
			while (localStringTokenizer1.hasMoreTokens()) {
				j = Integer.parseInt(localStringTokenizer1.nextToken());
				this.max_index = Math.max(this.max_index, j);
				localStringTokenizer1.nextToken();
				this.num_nonzeros += 1L;
			}
		}
		try {
			this.feature_max = new double[this.max_index + 1];
			this.feature_min = new double[this.max_index + 1];
		} catch (OutOfMemoryError localOutOfMemoryError) {
			System.err.println("can't allocate enough memory");
			System.exit(1);
		}

		for (int i = 0; i <= this.max_index; i++) {
			this.feature_max[i] = -1.797693134862315E+308D;
			this.feature_min[i] = 1.7976931348623157E+308D;
		}

		localBufferedReader1 = rewind(localBufferedReader1, str3, context);
		int m;
		StringTokenizer localStringTokenizer3;
		double d4;
		while (readline(localBufferedReader1) != null) {
			m = 1;

			localStringTokenizer3 = new StringTokenizer(this.line, " \t\n\r\f:");
			double d1 = Double.parseDouble(localStringTokenizer3.nextToken());
//			this.y_max = Math.max(this.y_max, d1);
//			this.y_min = Math.min(this.y_min, d1);

			while (localStringTokenizer3.hasMoreTokens()) {
				j = Integer.parseInt(localStringTokenizer3.nextToken());
				d4 = Double.parseDouble(localStringTokenizer3.nextToken());

				for (int i = m; i < j; i++) {
					this.feature_max[i] = Math.max(this.feature_max[i], 0.0D);
					this.feature_min[i] = Math.min(this.feature_min[i], 0.0D);
				}

				this.feature_max[j] = Math.max(this.feature_max[j], d4);
				this.feature_min[j] = Math.min(this.feature_min[j], d4);
				m = j + 1;
			}

			for (int i = m; i <= this.max_index; i++) {
				this.feature_max[i] = Math.max(this.feature_max[i], 0.0D);
				this.feature_min[i] = Math.min(this.feature_min[i], 0.0D);
			}
		}

		localBufferedReader1 = rewind(localBufferedReader1, str3, context);

		if (str2 != null) {
			localBufferedReader2.mark(2);
			int i2;
			StringTokenizer localStringTokenizer4;
			if ((i2 = localBufferedReader2.read()) == 121) {
				localBufferedReader2.readLine();
				localStringTokenizer4 = new StringTokenizer(
						localBufferedReader2.readLine());
//				this.y_lower = Double.parseDouble(localStringTokenizer4
//						.nextToken());
//				this.y_upper = Double.parseDouble(localStringTokenizer4
//						.nextToken());
//				localStringTokenizer4 = new StringTokenizer(
//						localBufferedReader2.readLine());
//				this.y_min = Double.parseDouble(localStringTokenizer4
//						.nextToken());
//				this.y_max = Double.parseDouble(localStringTokenizer4
//						.nextToken());
//				this.y_scaling = true;
				System.out.println("I am here O_0");
			} else {
				localBufferedReader2.reset();
			}
			if (localBufferedReader2.read() == 120) {
				localBufferedReader2.readLine();
				localStringTokenizer4 = new StringTokenizer(
						localBufferedReader2.readLine());
				this.lower = Double.parseDouble(localStringTokenizer4
						.nextToken());
				this.upper = Double.parseDouble(localStringTokenizer4
						.nextToken());
				String str5 = null;
				while ((str5 = localBufferedReader2.readLine()) != null) {
					StringTokenizer localStringTokenizer5 = new StringTokenizer(
							str5);
					m = Integer.parseInt(localStringTokenizer5.nextToken());
					double d3 = Double.parseDouble(localStringTokenizer5
							.nextToken());
					double d5 = Double.parseDouble(localStringTokenizer5
							.nextToken());
					if (m <= this.max_index) {
						this.feature_min[m] = d3;
						this.feature_max[m] = d5;
					}
				}
			}
			localBufferedReader2.close();
		}

		if (str1 != null) {
			Formatter localFormatter = new Formatter(new StringBuilder());
			BufferedWriter localBufferedWriter = null;
			try {
				FileOutputStream fos1 = context.openFileOutput(str1, Context.MODE_PRIVATE);
				localBufferedWriter = new BufferedWriter(new OutputStreamWriter(fos1),DEFUALT_BUFF_SIZE);
			} catch (IOException localIOException) {
				System.err.println("can't open file " + str1);
				System.exit(1);
			}

//			if (this.y_scaling) {
//				localFormatter.format("y\n", new Object[0]);
//				localFormatter.format("%.16g %.16g\n", new Object[] {
//						Double.valueOf(this.y_lower),
//						Double.valueOf(this.y_upper) });
//				localFormatter.format("%.16g %.16g\n",
//						new Object[] { Double.valueOf(this.y_min),
//								Double.valueOf(this.y_max) });
//			}
			localFormatter.format("x\n", new Object[0]);
			localFormatter.format("%.16g %.16g\n", new Object[] {
					Double.valueOf(this.lower), Double.valueOf(this.upper) });
			for (int i = 1; i <= this.max_index; i++) {
				if (this.feature_min[i] != this.feature_max[i])
					localFormatter.format("%d %.16g %.16g\n", new Object[] {
							Integer.valueOf(i),
							Double.valueOf(this.feature_min[i]),
							Double.valueOf(this.feature_max[i]) });
			}
			localBufferedWriter.write(localFormatter.toString());
			localBufferedWriter.close();
		}

		FileOutputStream outFile = context.openFileOutput(str3 + ".scaled", Context.MODE_PRIVATE);
		
		while (readline(localBufferedReader1) != null) {
			int i1 = 1;

			localStringTokenizer3 = new StringTokenizer(this.line, " \t\n\r\f:");
			double d2 = Double.parseDouble(localStringTokenizer3.nextToken());
			outFile.write(output_target(d2).getBytes());
			while (localStringTokenizer3.hasMoreElements()) {
				j = Integer.parseInt(localStringTokenizer3.nextToken());
				d4 = Double.parseDouble(localStringTokenizer3.nextToken());
				for (int i = i1; i < j; ++i)
					outFile.write(output(i, 0.0D).getBytes());
				outFile.write(output(j, d4).getBytes());
				i1 = j + 1;
			}

			for (int i = i1; i <= this.max_index; ++i)
				outFile.write(output(i, 0.0D).getBytes());
			outFile.write("\n".getBytes());
		}
		if (this.new_num_nonzeros > this.num_nonzeros) {
			System.err.print("Warning: original #nonzeros " + this.num_nonzeros
					+ "\n" + "         new      #nonzeros "
					+ this.new_num_nonzeros + "\n"
					+ "Use -l 0 if many original feature values are zeros\n");
		}

		localBufferedReader1.close();
		outFile.close();
	}

	private String output_target(double paramDouble) {
//		if (this.y_scaling) {
//			if (paramDouble == this.y_min)
//				paramDouble = this.y_lower;
//			else if (paramDouble == this.y_max)
//				paramDouble = this.y_upper;
//			else {
//				paramDouble = this.y_lower + (this.y_upper - this.y_lower)
//						* (paramDouble - this.y_min)
//						/ (this.y_max - this.y_min);
//			}
//		}

		return String.valueOf(paramDouble) + " ";
	}

	private String output(int paramInt, double paramDouble) {
		if (this.feature_max[paramInt] == this.feature_min[paramInt]) {
			return null;
		}
		if (paramDouble == this.feature_min[paramInt])
			paramDouble = this.lower;
		else if (paramDouble == this.feature_max[paramInt])
			paramDouble = this.upper;
		else {
			paramDouble = this.lower + (this.upper - this.lower)
					* (paramDouble - this.feature_min[paramInt])
					/ (this.feature_max[paramInt] - this.feature_min[paramInt]);
		}

		if (paramDouble != 0.0D) {
			this.new_num_nonzeros += 1L;
			return String.valueOf(paramInt) + ":" + String.valueOf(paramDouble) + " ";
		}
		return null;
	}
}
