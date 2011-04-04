package spamguard.bayesian.common;

public class Token {

	private String text;
	private int spamCount;
	private int nonSpamCount;
	private double spamRatio;
	private double nonSpamRatio;
	private double spamicity;
	
	public Token(String text) {
		this.text = text;
		this.spamCount = 0;
		this.nonSpamCount = 0;
		this.spamRatio = 0.0;
		this.nonSpamRatio = 0.0;
		this.spamicity = 0.0;
	}
	
	public void markSpam() {
		this.spamCount++;
	}
	
	public void markNonSpam() {
		this.nonSpamCount++;
	}
	
	public void calculateSpamRatio(int total) {
		if( total == 0) return;
		this.spamRatio = this.spamCount / (double) total;
	}
	
	public void calculateNonSpamRatio(int total) {
		if( total == 0) return;
		this.nonSpamRatio = this.nonSpamCount / (double) total;
	}
	
	public void calculateSpammicity() {
		if (spamRatio + nonSpamRatio > 0) 
			spamicity = spamRatio / (spamRatio + nonSpamRatio);
	    
		if (spamicity < 0.01) spamicity = 0.01;
	    else if (spamicity > 0.99) spamicity = 0.99;
	}

	public double getInterestingRate(){
		return Math.abs(0.5 - spamicity);
	}
	
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

	public int getNonSpamCount() {
		return nonSpamCount;
	}

	public void setNonSpamCount(int nonSpamCount) {
		this.nonSpamCount = nonSpamCount;
	}

	public double getSpamRatio() {
		return spamRatio;
	}

	public void setSpamRatio(double spamRatio) {
		this.spamRatio = spamRatio;
	}

	public double getNonSpamRatio() {
		return nonSpamRatio;
	}

	public void setNonSpamRatio(double nonSpamRatio) {
		this.nonSpamRatio = nonSpamRatio;
	}

	public double getSpamicity() {
		return spamicity;
	}

	public void setSpamicity(double spamicity) {
		this.spamicity = spamicity;
	}	
	
}
