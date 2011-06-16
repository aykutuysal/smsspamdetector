package com.smsspamguard.activity;

import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.SimpleCursorAdapter;

import com.smsspamguard.R;
import com.smsspamguard.db.Database;
import com.smsspamguard.model.Message;

public class SpamFromInbox extends ListActivity {
	
	private Cursor cursor;
	private SimpleCursorAdapter cursorAdapter;
	private Database db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		db = new Database(getApplicationContext());
		boolean unreadOnly = false;
		String SMS_READ_COLUMN = "read";
		String WHERE_CONDITION = unreadOnly ? SMS_READ_COLUMN + " = 0" : null;
		String SORT_ORDER = "date DESC";

		Uri uri = Uri.parse("content://sms/inbox");

		cursor = getContentResolver().query(
				uri,
				new String[] { "_id", "thread_id", "address", "person", "date",
						"body" }, WHERE_CONDITION, null, SORT_ORDER);
		
		String[] from = new String[] { "body" };
		cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, new int[] { android.R.id.text1 });
		this.setListAdapter(cursorAdapter);
		startManagingCursor(cursor);
		registerForContextMenu(getListView());
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (db != null) {
			db.close();
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Choose...");
		menu.add(0, 0, 0, R.string.mark_as_spam);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		//AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case 0:
			cursor.move(item.getItemId());
			
			long messageId = cursor.getLong(0);
			long threadId = cursor.getLong(1);
			String address = cursor.getString(2);
			long contactId = cursor.getLong(3);
			long date = cursor.getLong(4);
			String messageBody = cursor.getString(5);
			
			Message message = new Message(messageId, threadId, address, contactId, date, messageBody);
			db.insertSpam(message);
			getContentResolver().delete(Uri.parse("content://sms/inbox/" + messageId),null,null);
			
			Log.i("SpamGuard", "Marked As Spam : " + messageBody );
			
			cursorAdapter.notifyDataSetChanged();
			//setResult();
			finishActivity(0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
