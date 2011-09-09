package com.smsspamguard.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import android.util.Log;

import com.smsspamguard.constant.Constants;
import com.smsspamguard.engine.bayesian.common.Token;
import com.smsspamguard.model.Message;

public class Database {

	private static final String DATABASE_NAME = "spamguard.db";
	private static final int DATABASE_VERSION = 3;

	private static final String METADATA_TABLE = "android_metadata";
	private static final String LIST_TABLE = "list_table";
	private static final String SMS_TABLE = "sms_table";
	// private static final String MONO_TOKEN_TABLE = "mono_token_table";
	private static final String BI_TOKEN_TABLE = "bi_token_table";
	private static final String TRI_TOKEN_TABLE = "tri_token_table";
	// private static final String COUNT_TABLE = "count_table";

	private static final String INSERT_LIST = "insert into " + LIST_TABLE + " (type,value) values (?,?)";
	private static final String INSERT_SMS = "insert into " + SMS_TABLE + " (address, date, body, type) values (?,?,?,?)";
	// private static final String INSERT_MONO = "insert into " + MONO_TOKEN_TABLE
	// + " (text, spamCount, cleanCount, spamRatio, cleanRatio) values (?,?,?,?,?)";
	private static final String INSERT_BI = "insert into " + BI_TOKEN_TABLE + " (text, spamCount, cleanCount) values (?,?,?)";
	private static final String INSERT_TRI = "insert into " + TRI_TOKEN_TABLE + " (text, spamCount, cleanCount) values (?,?,?)";

	private static Context context;
	private static SQLiteDatabase db;
	private static OpenHelper openHelper;

	private SQLiteStatement insertStmt;

	private static Database spamguardDB;

	private Database(Context context2) {
		context = context2;
		openHelper = new OpenHelper(context2);
		db = openHelper.getWritableDatabase();
		// openHelper.onUpgrade(db, 0, 1);
	}

	public static Database getInstance(Context context2) {
		if (spamguardDB == null || openHelper == null) {
			spamguardDB = new Database(context2);
		}
		return spamguardDB;
	}

	public void insertSms(Message msg) {
		db.beginTransaction();
		try {
			this.insertStmt = db.compileStatement(INSERT_SMS);
			// this.insertStmt.bindLong(1, msg.getMessageId());
			// this.insertStmt.bindLong(2, msg.getThreadId());
			this.insertStmt.bindString(1, msg.getAddress());
			// this.insertStmt.bindLong(2, msg.getContactId());
			this.insertStmt.bindLong(2, msg.getDate());
			this.insertStmt.bindString(3, msg.getBody());
			this.insertStmt.bindLong(4, msg.getType());
			this.insertStmt.executeInsert();
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public void updateSms(long id, ContentValues values) {
		db.beginTransaction();
		try {
			db.update(SMS_TABLE, values, BaseColumns._ID + "=" + id, null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public Cursor searchSms(String address, long date) {
		Cursor cursor = db.query(SMS_TABLE, new String[] { BaseColumns._ID }, "date=? AND address=?", new String[] { String.valueOf(date), address },
				null, null, null);
		return cursor;
	}

	public Cursor getSpams() {
		Cursor cursor = db.query(SMS_TABLE, new String[] { BaseColumns._ID, "address", "date", "body" }, "type=? AND deleted=?", new String[] { "1",
				"0" }, null, null, BaseColumns._ID + " DESC");
		return cursor;
	}

	public Cursor getSmses() {
		Cursor cursor = db.query(SMS_TABLE, new String[] { "body", "type" }, null, null, null, null, BaseColumns._ID + " DESC");
		return cursor;
	}

	public int getDbCount() {
		Cursor cursor = db.query(SMS_TABLE, new String[] { BaseColumns._ID }, null, null, null, null, null);
		return cursor.getCount();
	}

	public void insertList(String type, String value) {
		db.beginTransaction();
		try {
			this.insertStmt = db.compileStatement(INSERT_LIST);
			this.insertStmt.bindString(1, type);
			this.insertStmt.bindString(2, value);
			this.insertStmt.executeInsert();
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public void updateList(long id, ContentValues values) {
		db.beginTransaction();
		try {
			db.update(LIST_TABLE, values, BaseColumns._ID + "=" + id, null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public void deleteList(long id) {
		db.beginTransaction();
		try {
			db.delete(LIST_TABLE, BaseColumns._ID + "=" + id, null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public void deleteAllList() {
		db.delete(LIST_TABLE, null, null);
	}

	public Cursor getList(String type) {
		Cursor cursor = null;
		if (type.equals("ws")) {
			cursor = db.query(LIST_TABLE, new String[] { BaseColumns._ID, "value", "type" }, "type like 'ws_'", null, null, null, "value asc");
		} else if (type.equals("wc")) {
			cursor = db.query(LIST_TABLE, new String[] { BaseColumns._ID, "value", "type" }, "type like 'wc_'", null, null, null, "value asc");
		} else if (type.equals("bs")) {
			cursor = db.query(LIST_TABLE, new String[] { BaseColumns._ID, "value", "type" }, "type like 'bs_'", null, null, null, "value asc");
		} else if (type.equals("bc")) {
			cursor = db.query(LIST_TABLE, new String[] { BaseColumns._ID, "value", "type" }, "type like 'bc_'", null, null, null, "value asc");
		}
		return cursor;
	}

	public Cursor searchList(String value) {
		Cursor cursor = db.query(LIST_TABLE, new String[] { "type" }, "value=?", new String[] { value }, null, null, null);
		return cursor;
	}

	public Cursor getRegex(String type) {
		Cursor cursor = db.query(LIST_TABLE, new String[] { "type", "value" }, "type like '" + type + "'", null, null, null, null);
		return cursor;
	}

	public Cursor conflictCheck(String type, String value) {
		Cursor cursor = db.query(LIST_TABLE, new String[] { "type" }, "type like '" + type + "' AND value=?", new String[] { value }, null, null,
				null);
		return cursor;
	}

	public void insertToken(Token t, String feature) {
		db.beginTransaction();
		try {
			String statement;
			/*
			 * if (feature.equals("mono")) { statement = INSERT_MONO; } else
			 */if (feature.equals("bi")) {
				statement = INSERT_BI;
			} else {
				statement = INSERT_TRI;
			}
			this.insertStmt = db.compileStatement(statement);
			this.insertStmt.bindString(1, t.getText());
			this.insertStmt.bindLong(2, t.getSpamCount());
			this.insertStmt.bindLong(3, t.getCleanCount());
			// this.insertStmt.bindDouble(4, t.getSpamRatio());
			// this.insertStmt.bindDouble(5, t.getCleanRatio());
			this.insertStmt.executeInsert();
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public void updateToken(long id, ContentValues values, String feature) {
		db.beginTransaction();
		try {
			String table;
			/*
			 * if (feature.equals("mono")) { table = INSERT_MONO; } else
			 */if (feature.equals("bi")) {
				table = BI_TOKEN_TABLE;
			} else {
				table = TRI_TOKEN_TABLE;
			}
			db.update(table, values, BaseColumns._ID + "=" + id, null);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public long findToken(String key, String feature) {
		String table;
		if (feature.equals("bi")) {
			table = BI_TOKEN_TABLE;
		} else {
			table = TRI_TOKEN_TABLE;
		}
		Cursor cursor = db.query(table, new String[] { "_id" }, "text=?", new String[] { key }, null, null, null);
		long id;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			id = cursor.getLong(0);
		} else {
			id = -1;
		}
		cursor.close();
		return id;
	}

	public int getSpamCount(String tokenKey, String feature) {
		String table;
		if (feature.equals("bi")) {
			table = BI_TOKEN_TABLE;
		} else {
			table = TRI_TOKEN_TABLE;
		}
		Cursor cursor = db.query(table, new String[] { "spamCount" }, "text=?", new String[] { tokenKey }, null, null, null);
		cursor.moveToFirst();
		int spamCount = cursor.getInt(0);
		cursor.close();
		return spamCount;
	}

	public int getCleanCount(String tokenKey, String feature) {
		String table;
		if (feature.equals("bi")) {
			table = BI_TOKEN_TABLE;
		} else {
			table = TRI_TOKEN_TABLE;
		}
		Cursor cursor = db.query(table, new String[] { "cleanCount" }, "text=?", new String[] { tokenKey }, null, null, null);
		cursor.moveToFirst();
		int cleanCount = cursor.getInt(0);
		cursor.close();
		return cleanCount;
	}

	public double getRatio(String tokenKey, String feature) {
		String table;
		/*
		 * if (feature.equals("mono")) { table = INSERT_MONO; } else
		 */if (feature.equals("bi")) {
			table = BI_TOKEN_TABLE;
		} else {
			table = TRI_TOKEN_TABLE;
		}
		Cursor cursor = db.query(table, new String[] { "spamCount", "cleanCount" }, "text=?", new String[] { tokenKey }, null, null, null);
		// Token t = new Token(cursor.getString(1));
		// t.setSpamCount(cursor.getInt(2));
		// t.setCleanCount(cursor.getInt(3));
		// t.setSpamRatio(cursor.getDouble(4));
		// t.setCleanRatio(cursor.getDouble(5));
		double ratio;
		if (cursor.getCount() == 0) {
			ratio = -1;
		} else {
			cursor.moveToFirst();
			ratio = cursor.getInt(0) / (cursor.getInt(0) + cursor.getInt(1));
		}
		cursor.close();
		return ratio;
	}

	// public void insertCount(String type, int count) {
	// db.beginTransaction();
	// try {
	// this.insertStmt = db.compileStatement("insert into " + COUNT_TABLE + " (type,count) values (?,?)");
	// this.insertStmt.bindString(1, type);
	// this.insertStmt.bindLong(2, count);
	// this.insertStmt.executeInsert();
	// db.setTransactionSuccessful();
	// } finally {
	// db.endTransaction();
	// }
	// }

	public void close() {
		// NOTE: openHelper must now be a member of CallDataHelper;
		// you currently have it as a local in your constructor
		if (openHelper != null) {
			openHelper.close();
			openHelper = null;
		}
	}

	public class OpenHelper extends SQLiteOpenHelper {

		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			 db.execSQL("CREATE TABLE IF NOT EXISTS " + LIST_TABLE + " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			 + "type TEXT, value TEXT)");
			
			 db.execSQL("CREATE TABLE IF NOT EXISTS " + SMS_TABLE + " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			 + "address TEXT, date INTEGER, body TEXT, type INTEGER, deleted INTEGER DEFAULT 0)");
			//
			// // db.execSQL("CREATE TABLE IF NOT EXISTS " + MONO_TOKEN_TABLE + " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			// // + "text TEXT UNIQUE, spamCount INTEGER DEFAULT 0, cleanCount INTEGER DEFAULT 0, spamRatio REAL, cleanRatio REAL)");
			//
			 db.execSQL("CREATE TABLE IF NOT EXISTS " + BI_TOKEN_TABLE + " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			 + "text TEXT UNIQUE, spamCount INTEGER DEFAULT 0, cleanCount INTEGER DEFAULT 0)");
			
			 db.execSQL("CREATE TABLE IF NOT EXISTS " + TRI_TOKEN_TABLE + " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			 + "text TEXT UNIQUE, spamCount INTEGER DEFAULT 0, cleanCount INTEGER DEFAULT 0)");

			// db.execSQL("CREATE TABLE IF NOT EXISTS " + COUNT_TABLE + " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			// + "type TEXT, count INTEGER)");

			db.execSQL("CREATE TABLE IF NOT EXISTS " + METADATA_TABLE + " (locale TEXT DEFAULT 'en_US')");

			Log.i(Constants.DEBUG_TAG, "DB created.");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i(Constants.DEBUG_TAG, "Upgrading database, this will drop tables and recreate.");
			db.execSQL("DROP TABLE IF EXISTS " + LIST_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + SMS_TABLE);
			// db.execSQL("DROP TABLE IF EXISTS " + MONO_TOKEN_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + BI_TOKEN_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + TRI_TOKEN_TABLE);
			// db.execSQL("DROP TABLE IF EXISTS " + COUNT_TABLE);
			onCreate(db);
		}
	}
}
