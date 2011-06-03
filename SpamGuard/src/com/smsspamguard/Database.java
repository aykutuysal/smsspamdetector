package com.smsspamguard;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import android.telephony.SmsMessage;
import android.util.Log;

public class Database {

	private static final String DATABASE_NAME = "spamguard.db";
	private static final int DATABASE_VERSION = 1;

	private static final String LIST_TABLE = "list_table";
	private static final String SPAM_TABLE = "spam_table";

	private static final String INSERT_LIST = "insert into " + LIST_TABLE + " (type,value) values (?,?)";
	private static final String UPDATE_LIST = "update " + LIST_TABLE + " set value=? where " + BaseColumns._ID + "=?";
	private static final String INSERT_SPAM = "insert into " + SPAM_TABLE + " (displayMessageBody,displayOriginatingAddress,timestampMillis,pdu) "
			+ "values (?,?,?,?)";

	private Context context;
	private SQLiteDatabase db;
	private OpenHelper openHelper = null;

	private SQLiteStatement insertStmt;

	public Database(Context context) {
		this.context = context;
		openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		openHelper.onUpgrade(db, 0, 1);
	}

	public void insertSpam(SmsMessage message) {
		this.db.beginTransaction();
		try {
			this.insertStmt = this.db.compileStatement(INSERT_SPAM);
			this.insertStmt.bindString(1, message.getDisplayMessageBody());
			this.insertStmt.bindString(2, message.getDisplayOriginatingAddress());
			this.insertStmt.bindLong(3, message.getTimestampMillis());
			this.insertStmt.bindBlob(4, message.getPdu());
			this.insertStmt.executeInsert();
			this.db.setTransactionSuccessful();
		} finally {
			this.db.endTransaction();
		}
	}

	public List<SmsMessage> selectAllSpam() {

		List<SmsMessage> list = new ArrayList<SmsMessage>();
		Cursor cursor = this.db.query(SPAM_TABLE, new String[] { "pdu" }, null, null, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				SmsMessage smsMessage = SmsMessage.createFromPdu(cursor.getBlob(0));
				list.add(smsMessage);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	public void insertList(String type, String value) {
		this.db.beginTransaction();
		try {
			this.insertStmt = this.db.compileStatement(INSERT_LIST);
			this.insertStmt.bindString(1, type);
			this.insertStmt.bindString(2, value);
			this.insertStmt.executeInsert();
			this.db.setTransactionSuccessful();
		} finally {
			this.db.endTransaction();
		}
	}
	
	public void updateList(long id, ContentValues values)
	{
		this.db.beginTransaction();
		try {
			this.db.update(LIST_TABLE, values, BaseColumns._ID + "=" + id, null);
//			this.insertStmt = this.db.compileStatement(UPDATE_LIST);
//			this.insertStmt.bindString(1, value);
//			this.insertStmt.bindLong(2, id);
//			this.insertStmt.executeInsert();
			this.db.setTransactionSuccessful();
		} finally {
			this.db.endTransaction();
		}
	}

	public void deleteList(long id) {
		this.db.beginTransaction();
		try {
			this.db.delete(LIST_TABLE, BaseColumns._ID + "=" + id, null);
			this.db.setTransactionSuccessful();
		} finally {
			this.db.endTransaction();
		}
	}

	public void deleteAllList() {
		this.db.delete(LIST_TABLE, null, null);
	}
	
	public Cursor getList(String type)
	{
		Cursor cursor = null;
		if (type.equals("w")) {
			cursor = this.db.query(LIST_TABLE, new String[] { BaseColumns._ID, "value" }, "type like 'w%'", null, null, null, "value asc");
		} else if (type.equals("b")) {
			cursor = this.db.query(LIST_TABLE, new String[] { BaseColumns._ID, "value" }, "type like 'b%'", null, null, null, "value asc");
		}
		return cursor;
	}

	public Cursor searchList(String sender) {
		Cursor cursor = this.db.query(LIST_TABLE, new String[] { "type" }, "value=?", new String[] { sender }, null, null, null);
		return cursor;
	}

	public void close() {
		// NOTE: openHelper must now be a member of CallDataHelper;
		// you currently have it as a local in your constructor
		if (openHelper != null) {
			openHelper.close();
		}
	}

	public class OpenHelper extends SQLiteOpenHelper {

		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE IF NOT EXISTS " + LIST_TABLE + " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, value TEXT UNIQUE ON CONFLICT ROLLBACK)");

			db.execSQL("CREATE TABLE IF NOT EXISTS " + SPAM_TABLE + " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, displayMessageBody TEXT, displayOriginatingAddress TEXT, "
					+ "timestampMillis INTEGER, pdu BLOB UNIQUE ON CONFLICT ROLLBACK)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("Example", "Upgrading database, this will drop tables and recreate.");
			db.execSQL("DROP TABLE IF EXISTS " + LIST_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + SPAM_TABLE);
			onCreate(db);
		}
	}
}
