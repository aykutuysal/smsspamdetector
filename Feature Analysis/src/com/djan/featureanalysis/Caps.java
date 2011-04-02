package com.djan.featureanalysis;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Caps {
	public void run() {
		try {
			// check for GSM 7-bit encoding
			Scanner scanner = new Scanner(new FileInputStream("spams.txt"),
					"ISO-8859-9")
					.useDelimiter("\n###Spam Filter Delimiter###\n");
			String sms = "";
			System.out.println("caps\tsmalls\ttotal\tratio");
			while (scanner.hasNext()) {
				sms = scanner.next();
				int caps = 0, smalls = 0;
				for(int i = 0; i < sms.length(); i++)
				{
					if(Character.isLowerCase(sms.charAt(i)))
						smalls++;
					else if(Character.isUpperCase(sms.charAt(i)))
						caps++;
				}
				System.out.format(caps + "\t" + smalls +"\t" + sms.length() + "\t%.2f\n", (double)caps/(double)(smalls+1));
			}
		} catch (IOException e) {
			System.out.println("spams.txt not found!");
		}
	}
}
