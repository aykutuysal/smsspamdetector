package com.smsspamguard;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Database {

	private static final String DATABASE_NAME = "spamguard.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "whitelist";

	private Context context;
	private SQLiteDatabase db;

	private SQLiteStatement insertStmt;
	private static final String INSERT = "insert into " + TABLE_NAME
			+ "(number) values (?)";

	public Database(Context context) {
		this.context = context;
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		this.insertStmt = this.db.compileStatement(INSERT);
	}

	public long insert(String number) {
		this.db.beginTransaction();
		try {
			this.insertStmt.bindString(1, number);
			return this.insertStmt.executeInsert();
		} finally {
			this.db.endTransaction();
		}
	}

	public void delete(String number) {
		this.db.beginTransaction();
		try {
			this.db.delete(TABLE_NAME, "number=" + number, null);
			this.db.setTransactionSuccessful();
		} finally {
			this.db.endTransaction();
		}
	}

	public void deleteAll() {
		this.db.delete(TABLE_NAME, null, null);
	}

	public List<String> selectAll() {
		List<String> list = new ArrayList<String>();
		Cursor cursor = this.db.query(TABLE_NAME, new String[] { "number" },
				null, null, null, null, "number desc");
		if (cursor.moveToFirst()) {
			do {
				list.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	private static class OpenHelper extends SQLiteOpenHelper {

		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_NAME
					+ "(id INTEGER PRIMARY KEY, number TEXT)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("Example",
					"Upgrading database, this will drop tables and recreate.");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
	}
}
