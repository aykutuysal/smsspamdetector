package com.smsspamguard.activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.app.ListActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.smsspamguard.R;
import com.smsspamguard.constant.Constants;
import com.smsspamguard.db.Database;
import com.smsspamguard.engine.bayesian.common.Token;
import com.smsspamguard.engine.bayesian.filter.BayesianFilterBigram;
import com.smsspamguard.engine.bayesian.filter.BayesianFilterTrigram;
import com.smsspamguard.model.Message;

public class SpamFromInbox extends ListActivity {

	private Cursor cursor;
	private SimpleCursorAdapter cursorAdapter;
	private Database db;
	private HashSet<Long> idList;

	public void insertTokens(String[] ngrams, String feature, boolean inDB) {
		for (String str : ngrams) {
			long id = db.findToken(str, feature);
			if (id == -1) {
				Token t = new Token(str);
				t.markSpam();
				db.insertToken(t, feature);
			} else {
				ContentValues values = new ContentValues();
				values.put("spamCount", db.getSpamCount(str, feature) + 1);
				if (inDB) {
					values.put("cleanCount", db.getCleanCount(str, feature) - 1);
				}
				db.updateToken(id, values, feature);
				Log.i(Constants.DEBUG_TAG, "cleanCount " + (db.getCleanCount(str, feature)) + ", spamCount " + (db.getSpamCount(str, feature)));
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		idList = new HashSet<Long>();

		db = Database.getInstance(this);// new Database(getApplicationContext());
		boolean unreadOnly = false;
		String SMS_READ_COLUMN = "read";
		String WHERE_CONDITION = unreadOnly ? SMS_READ_COLUMN + " = 0" : null;
		String SORT_ORDER = "date DESC";

		Uri uri = Uri.parse("content://sms/inbox");

		cursor = getContentResolver().query(uri, new String[] { "_id", "thread_id", "address", "date", "body" }, WHERE_CONDITION, null, SORT_ORDER);

		String[] from = new String[] { "body" };
		cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_multiple_choice, cursor, from,
				new int[] { android.R.id.text1 });
		this.setListAdapter(cursorAdapter);
		startManagingCursor(cursor);
		registerForContextMenu(getListView());
	}

	@Override
	protected void onPause() {
		super.onPause();
		db.close();
		Log.i(Constants.DEBUG_TAG, "DB closed by SpamFromInbox");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (cursor != null) {
			cursor.close();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (idList.contains(id)) {
			idList.remove(id);
		}
		else
		{
			idList.add(id);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Choose...");
		menu.add(0, 0, 0, R.string.mark_as_spam);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// AdapterContextMenuInfo info = (AdapterContextMenuInfo)
		// item.getMenuInfo();
		switch (item.getItemId()) {
		case 0:
			cursor.move(item.getItemId());

			// long messageId = cursor.getLong(0);
			long threadId = cursor.getLong(1);
			String address = cursor.getString(2);
			// long contactId = cursor.getLong(1);
			long date = cursor.getLong(3);
			String body = cursor.getString(4);

			boolean inDB = false;
			Cursor dbCursor = db.searchSms(address, date);
			Log.i(Constants.DEBUG_TAG, "matching sms in db: " + dbCursor.getCount());
			if (dbCursor.getCount() > 0) {
				inDB = true;
				dbCursor.moveToFirst();
				ContentValues values = new ContentValues();
				values.put("type", 1);
				db.updateSms(dbCursor.getLong(0), values);
			} else {
				Message message = new Message(address, date, body, 1);
				db.insertSms(message);
			}
			dbCursor.close();

			Uri uri = Uri.parse("content://sms/conversations/" + threadId);
			Log.i(Constants.DEBUG_TAG, address + " " + date + " " + body);
			getContentResolver().delete(uri, "address=? AND date=?", new String[] { address, String.valueOf(date) });

			Log.i("SpamGuard", "Marked As Spam : " + body);

			BayesianFilterBigram bigramFilter = new BayesianFilterBigram();
			BayesianFilterTrigram trigramFilter = new BayesianFilterTrigram();
			String[] bigrams = bigramFilter.returnTokenList(body);
			String[] trigrams = trigramFilter.returnTokenList(body);
			insertTokens(bigrams, "bi", inDB);
			insertTokens(trigrams, "tri", inDB);

			cursorAdapter.notifyDataSetChanged();
			// finishActivity(0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
