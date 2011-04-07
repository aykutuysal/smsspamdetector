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
	private static final String LIST_TABLE = "list_table";
	
	private Context context;
	private SQLiteDatabase db;
	private OpenHelper openHelper = null;

	private SQLiteStatement insertStmt;
	private static final String INSERT = "insert into " + LIST_TABLE + " (type,value) values (?,?)";

	public Database(Context context) {
		this.context = context;
		openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
//		openHelper.onUpgrade(db, 0, 1);
	}

	public void insert(String type, String value) {
		this.db.beginTransaction();
		try {
			this.insertStmt = this.db.compileStatement(INSERT);
			this.insertStmt.bindString(1, type);
			this.insertStmt.bindString(2, value);
			this.insertStmt.executeInsert();
			this.db.setTransactionSuccessful();
		} finally {
			this.db.endTransaction();
		}
	}

	public void delete(String value) {
		this.db.beginTransaction();
		try {
			this.db.delete(LIST_TABLE, "value=" + value, null);
			this.db.setTransactionSuccessful();
		} finally {
			this.db.endTransaction();
		}
	}

	public void deleteAll() {
		this.db.delete(LIST_TABLE, null, null);
	}

	public List<String> selectAll(String type) {
		List<String> list = new ArrayList<String>();
		Cursor cursor = null;
		if(type.equals("w"))
		{
			cursor = this.db.query(LIST_TABLE, new String[] { "value" },
					"type=? OR type=?", new String[] {"wn","wt"}, null, null, "value asc");
		}
		else if(type.equals("b"))
		{
			cursor = this.db.query(LIST_TABLE, new String[] { "value" },
					"type=? OR type=?", new String[] {"bn","bt"}, null, null, "value asc");
		}
		else
		{
			return list;
		}
		
		
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

	public void close() {
		// NOTE: openHelper must now be a member of CallDataHelper;
		// you currently have it as a local in your constructor
		if (openHelper != null) {
			openHelper.close();
		}
	}

	private static class OpenHelper extends SQLiteOpenHelper {

		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + LIST_TABLE
					+ "(id INTEGER PRIMARY KEY, type TEXT, value TEXT UNIQUE ON CONFLICT ROLLBACK)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("Example",
					"Upgrading database, this will drop tables and recreate.");
			db.execSQL("DROP TABLE IF EXISTS " + LIST_TABLE);
			onCreate(db);
		}
	}
}
