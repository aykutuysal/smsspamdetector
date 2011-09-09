package com.smsspamguard.engine.bayesian.common;

public class Token {

	private String text;
	private int spamCount;
	private int cleanCount;
//	private double spamRatio;
//	private double cleanRatio;
	
	public Token(String text) {
		this.text = text;
		this.spamCount = 0;
		this.cleanCount = 0;
//		this.spamRatio = 0.0;
//		this.cleanRatio = 0.0;
	}
	
	public void markSpam() {
		this.spamCount++;
	}
	
	public void markClean() {
		this.cleanCount++;
	}
	
//	public void calculateSpamRatio(int total) {
//		if( total == 0) return;
//		this.spamRatio = this.spamCount / (double) total;
//	}
	
//	public void calculateCleanRatio(int total) {
//		if( total == 0) return;
//		this.cleanRatio = this.cleanCount / (double) total;
//	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getSpamCount() {
		return spamCount;
	}

	public void setSpamCount(int spamCount) {
		this.spamCount = spamCount;
	}

	public int getCleanCount() {
		return cleanCount;
	}

	public void setCleanCount(int cleanCount) {
		this.cleanCount = cleanCount;
	}

//	public double getSpamRatio() {
//		return spamRatio;
//	}
//
//	public void setSpamRatio(double spamRatio) {
//		this.spamRatio = spamRatio;
//	}

//	public double getCleanRatio() {
//		return cleanRatio;
//	}
//
//	public void setCleanRatio(double cleanRatio) {
//		this.cleanRatio = cleanRatio;
//	}
}
