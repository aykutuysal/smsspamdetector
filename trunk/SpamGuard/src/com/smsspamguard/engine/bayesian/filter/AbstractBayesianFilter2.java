package com.smsspamguard.engine.bayesian.filter;

import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.smsspamguard.constant.Constants;
import com.smsspamguard.db.Database;
import com.smsspamguard.engine.bayesian.common.Token;

public abstract class AbstractBayesianFilter2 {

	private HashMap<String, Token> tokens;

	public abstract String[] returnTokenList(String body);

	public AbstractBayesianFilter2() {
		this.tokens = new HashMap<String, Token>();
	}

	public void train(Context context, String feature) {

		Database db = Database.getInstance(context);
		Cursor cursor = db.getSmses();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			int type = cursor.getInt(1);
			String body = cursor.getString(0);
			String[] tokenList = this.returnTokenList(body);

			for (String str : tokenList) {

//				if (str.length() <= 3)
//					continue;

//				str = str.toLowerCase();

				if (!tokens.containsKey(str)) {
					Token t = new Token(str);
					tokens.put(str, t);
				}

				Token temp = tokens.get(str);

				if (type == 1)
					temp.markSpam();
				else
					temp.markClean();
			}
			cursor.moveToNext();
		}
		cursor.close();
		
		Set<String> keys = tokens.keySet();
		for (String key : keys) {
			Token t = tokens.get(key);
//			t.calculateSpamRatio(t.getCleanCount()+t.getSpamCount());
//			t.calculateCleanRatio(keys.size());
			db.insertToken(t, feature);
			Log.i(Constants.DEBUG_TAG, feature);
		}
		
		
		db.close();
	}

//	public void finalizeTraining(Context context) {
//
//		Database db = Database.getInstance();
//		Set<String> keys = tokens.keySet();
//		for (String key : keys) {
//			Token t = tokens.get(key);
//			t.calculateSpamRatio(keys.size());
//			t.calculateCleanRatio(keys.size());
////			db.insert
//		}
//	}

//	public Token findToken(Context context, String tokenKey, String feature) {
//
//		Database db = Database.getInstance();
//		
//		return tokens.get(tokenKey);
//	}
}
