package com.smsspamguard;

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
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.SimpleCursorAdapter;

public class SpamFromInbox extends ListActivity {
	
	private Cursor cursor;
	private SimpleCursorAdapter cursorAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
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
		int[] to = new int[] { R.id.list_entry };
		cursorAdapter = new SimpleCursorAdapter(this, R.layout.mylist, cursor, from, to);
		this.setListAdapter(cursorAdapter);
		registerForContextMenu(getListView());
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Choose...");
		menu.add(0, 0, 0, R.string.mark_as_spam);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case 0:
			cursor.move(item.getItemId());
			
			long messageId = cursor.getLong(0);
			long threadId = cursor.getLong(1);
			String address = cursor.getString(2);
			long contactId = cursor.getLong(3);
			String contactId_string = String.valueOf(contactId);
			long timestamp = cursor.getLong(4);
			String body = cursor.getString(5);
			
			getContentResolver().delete(Uri.parse("content://sms/conversations/" + threadId),null,null);
			
			Log.i("SPAMGUARD", "Marking As Spam : " + body );
			
			cursorAdapter.notifyDataSetChanged();
			//setResult();
			finishActivity(0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
