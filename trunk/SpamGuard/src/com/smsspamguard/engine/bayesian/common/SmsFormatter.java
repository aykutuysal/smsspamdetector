package com.smsspamguard.engine.bayesian.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsFormatter {

	/**
	 * formats given sms according to following rules :
	 * 
	 * 1. all characters are lower case
	 * 2. no turkish characters are allowed
	 * 3. apostrophes are removed
	 * 4. no multiple blanks, tabs or new lines
	 * 
	 * @return formatted sms
	 */
	public static String format(String sms) {
		
		sms = sms.toLowerCase();
		sms = replaceTurkishChars(sms);
		
		Pattern apostrophePattern = Pattern.compile("'");
		Matcher apostropheMatcher = apostrophePattern.matcher(sms);
		sms = apostropheMatcher.replaceAll("");		
		
		Pattern nonWordPattern = Pattern.compile("\\W");
		Matcher nonWordMatcher = nonWordPattern.matcher(sms);
		sms = nonWordMatcher.replaceAll(" ");

		Pattern multipleBlanksPattern = Pattern.compile("[\\s]+");		
		Matcher multipleBlanksMatcher = multipleBlanksPattern.matcher(sms);
		sms = multipleBlanksMatcher.replaceAll(" ");
		
		return sms;
	}
	
	private static String replaceTurkishChars(String str) {

		str = str.replaceAll("ğ", "g");
		str = str.replaceAll("Ğ", "G");
		str = str.replaceAll("ü", "u");
		str = str.replaceAll("Ü", "U");
		str = str.replaceAll("ş", "s");
		str = str.replaceAll("Ş", "S");
		str = str.replaceAll("İ", "I");
		str = str.replaceAll("ı", "i");
		str = str.replaceAll("ç", "c");
		str = str.replaceAll("Ç", "C");
		str = str.replaceAll("ö", "o");
		str = str.replaceAll("Ö", "O");
		
		return str;
	}
}
