package com.smsspamguard.activity;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.SimpleCursorAdapter;

import com.smsspamguard.R;
import com.smsspamguard.db.Database;

public class Spams extends ListActivity {
	private Database db;
	private Cursor spamCursor = null;
	private SimpleCursorAdapter cursorAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
//		try {
//			FileCopier.backupFiles();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		this.db = new Database(this);
		registerForContextMenu(getListView());
	}

	public void onStart() {
		super.onStart();
	}

	public void onResume() {
		super.onResume();
		spamCursor = db.getSpams();
		String[] from = new String[] { spamCursor.getColumnName(6) };
		cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, spamCursor, from, new int[] { android.R.id.text1 });
		startManagingCursor(spamCursor);
		this.setListAdapter(cursorAdapter);
	}

	public void onPause() {
		super.onPause();
	}

	public void onDestroy() {
		super.onDestroy();
		if (db != null) {
			db.close();
		}
		spamCursor.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.spamsmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.add_spam_from_inbox:
			Intent intent = new Intent(this,SpamFromInbox.class);
			startActivityForResult(intent, 0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Choose...");
		menu.add(0, 0, 0, R.string.mark_as_not_spam);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case 0:
			ContentValues values = new ContentValues();
			//values.put("_id", cursorAdapter.getCursor().getLong(1));
			//values.put("thread_id", cursorAdapter.getCursor().getLong(2));
			values.put("address", cursorAdapter.getCursor().getString(3));
			values.put("person", cursorAdapter.getCursor().getLong(4));
			values.put("date", cursorAdapter.getCursor().getLong(5));
			values.put("body", cursorAdapter.getCursor().getString(6));
			getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
			
			db.deleteSpam(info.id);
			cursorAdapter.getCursor().requery();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
