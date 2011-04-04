package com.djan.featureanalysis;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Trigram {

	private HashMap<String, Integer> tokens; // int kismi counter olacak her
												// rastgeldiginde ++ yapicaz
												// inti

	public Trigram() {
		this.tokens = new HashMap<String, Integer>();
	}

	public void run() {
		// int spamCount = 0, cleanCount = 0;

		try {
			// check for GSM 7-bit encoding
			Scanner scanner = new Scanner(new FileInputStream("spams.txt"),
					"ISO-8859-9").useDelimiter("\n###Spam Filter Delimiter###\n");
			String sms = "";
			//Pattern p = Pattern.compile("[^\\W]+? (?=(.+? .+?(?=\\W)))");
			Pattern p = Pattern.compile("[^\\s]+?\\s+(?=([^\\s]+?\\s+[^\\s]+))");
			Matcher m = p.matcher(sms);
			String trigram = "";
			while (scanner.hasNext()) {
				sms = scanner.next();
				m = p.matcher(sms);
				int c = 1;
				while(m.find())
				{
					trigram = m.group() + m.group(1);
					if(tokens.containsKey(trigram))
					{
						tokens.put(trigram, tokens.get(trigram)+1);
					}
					else
					{
						tokens.put(trigram, 1);
					}
					//System.out.println(c++ + ": " + trigram);
				}
				// String[] tokenList = sms.replaceAll("[]",
				// "[gusiocGUSIOC]");
				// spamCount++;
				// System.out.println(hede);
			}
			System.out.println(tokens.toString());
			Collection<Integer> vals = tokens.values();
			/*for(Integer i:vals)
			{
				if(i > 2)
				{
					System.out.println(i);
				}
			}*/
		} catch (IOException e) {
			System.out.println("spams.txt not found!");
		}

//		// Create the encoder and decoder for ISO-8859-1
//		Charset charset = Charset.forName("ISO-8859-1");
//		Charset charset2 = Charset.forName("ISO-8859-9");
//		CharsetDecoder decoder = charset.newDecoder();
//		CharsetEncoder encoder = charset2.newEncoder();
//
//		try {
//			// Convert a string to ISO-LATIN-1 bytes in a ByteBuffer
//			// The new ByteBuffer is ready to be read.
//			ByteBuffer bbuf = encoder.encode(CharBuffer.wrap("�"));
//
//			
//			// Convert ISO-LATIN-1 bytes in a ByteBuffer to a character
//			// ByteBuffer and then to a string.
//			// The new ByteBuffer is ready to be read.
//			CharBuffer cbuf = decoder.decode(bbuf);
//			String s = cbuf.toString();
//			System.out.println(s);
//			FileWriter fw = new FileWriter("latin.txt");
//			fw.write(s);
//			fw.close();
//		} catch (CharacterCodingException e) {
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		try {
			// check for GSM 7-bit encoding
			Scanner scanner = new Scanner(new FileInputStream("cleans.txt"),
					"UTF-8").useDelimiter("\r\n###Clean Set Delimiter###\r\n");
			String sms = "";
			while (scanner.hasNext()) {
				sms = scanner.next();

				// cleanCount++;
				// System.out.println(hede);
			}
		} catch (IOException e) {
			System.out.println("cleans.txt not found!");
		}
	}

}
