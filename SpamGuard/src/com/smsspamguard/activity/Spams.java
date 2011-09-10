package com.smsspamguard.activity;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.SimpleCursorAdapter;

import com.smsspamguard.R;
import com.smsspamguard.constant.Constants;
import com.smsspamguard.db.Database;
import com.smsspamguard.engine.bayesian.filter.BayesianFilterBigram;
import com.smsspamguard.engine.bayesian.filter.BayesianFilterTrigram;

public class Spams extends ListActivity {
	private Database db;
	private Cursor spamCursor = null;
	private SimpleCursorAdapter cursorAdapter;

	public void updateTokens(String[] ngrams, String feature) {
		for (String str : ngrams) {
			long id = db.findToken(str, feature);
			ContentValues values = new ContentValues();
			values.put("cleanCount", db.getCleanCount(str, feature) + 1);
			values.put("spamCount", db.getSpamCount(str, feature) - 1);
			db.updateToken(id, values, feature);
			Log.i(Constants.DEBUG_TAG, "cleanCount " + (db.getCleanCount(str, feature)) + ", spamCount " + (db.getSpamCount(str, feature)));
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// try {
		// FileCopier.backupFiles();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// db = Database.getInstance(this);

	}

	@Override
	public void onResume() {
		super.onResume();
		db = Database.getInstance(this);
		Log.i(Constants.DEBUG_TAG, "DB opened by Spam onResume");
		spamCursor = db.getSpams();
		cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, spamCursor, new String[] { "body" },
				new int[] { android.R.id.text1 });
		this.setListAdapter(cursorAdapter);
		startManagingCursor(spamCursor);
		registerForContextMenu(getListView());
	}

	@Override
	protected void onPause() {
		super.onPause();
		spamCursor.close();
		Log.i(Constants.DEBUG_TAG, "Paused");
		// db.close();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// if (db != null) {
		// db.close();
		// }
		// spamCursor.close();
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
		case R.id.delete_spams:
			ContentValues values = new ContentValues();
			values.put("deleted", 1);
			db.updateVisibility(values);
			cursorAdapter.getCursor().requery();
			return true;
		case R.id.add_spam_from_inbox:
			Intent intent = new Intent(this, SpamFromInbox.class);
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
//			db = Database.getInstance(this);
			spamCursor.move(item.getItemId());
			ContentValues values = new ContentValues();
			// values.put("_id", cursorAdapter.getCursor().getLong(1));
			// values.put("thread_id", cursorAdapter.getCursor().getLong(2));
			values.put("address", spamCursor.getString(1));
			// values.put("person", cursorAdapter.getCursor().getLong(1));
			values.put("date", spamCursor.getLong(2));
			values.put("body", spamCursor.getString(3));
			getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
			
			Log.i(Constants.DEBUG_TAG, "Clean: " + spamCursor.getString(3));
			
			values.clear();
			values.put("type", 0);
			db.updateSms(info.id, values);
			BayesianFilterBigram bigramFilter = new BayesianFilterBigram();
			BayesianFilterTrigram trigramFilter = new BayesianFilterTrigram();
			String[] bigrams = bigramFilter.returnTokenList(spamCursor.getString(3));
			String[] trigrams = trigramFilter.returnTokenList(spamCursor.getString(3));
			updateTokens(bigrams, "bi");
			updateTokens(trigrams, "tri");
//			spamCursor.close();
//			 spamCursor = db.getSpams();
//			cursorAdapter.notifyDataSetChanged();
			cursorAdapter.getCursor().requery();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
