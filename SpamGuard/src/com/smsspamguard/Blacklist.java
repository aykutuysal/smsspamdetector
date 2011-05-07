package com.smsspamguard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Blacklist extends ListActivity {
	private Database db;
	private Cursor listCursor = null;
	private SimpleCursorAdapter cursorAdapter;

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case R.id.add_number:
			final EditText input = new EditText(this);
			final AlertDialog dialog = new AlertDialog.Builder(Blacklist.this).setTitle(R.string.insert_number).setView(input).setPositiveButton(
					R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							if (input.getText().toString() != null) {
								try {
									db.insertList("bn", input.getText().toString());
									input.setText(null);
									cursorAdapter.getCursor().requery();
								} catch (SQLiteConstraintException e) {
									Toast
											.makeText(Blacklist.this, "Phone number already exists in either blacklist or blacklist.",
													Toast.LENGTH_LONG).show();
								}
							}
						}
					}).setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					input.setText(null);
				}
			}).create();

			input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
					}
				}
			});
			return dialog;
		default:
			return null;
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.blackwhitelistmenu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_number:
			showDialog(R.id.add_number);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Choose...");
		menu.add(0, 0, 0, R.string.update_entry);
		menu.add(0, 1, 0, R.string.delete_entry);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			db.deleteList(info.id);
			cursorAdapter.getCursor().requery();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mylist);
		this.db = new Database(this);
		listCursor = db.getList("b");
		String[] from = new String[] { listCursor.getColumnName(1) };
		int[] to = new int[] { R.id.list_entry };
		cursorAdapter = new SimpleCursorAdapter(this, R.layout.mylist, listCursor, from, to);
		this.setListAdapter(cursorAdapter);
		registerForContextMenu(getListView());
	}

	public void onDestroy() {
		super.onDestroy();
		if (db != null) {
			db.close();
		}
	}
}
