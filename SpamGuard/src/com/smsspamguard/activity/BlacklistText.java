package com.smsspamguard.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
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

import com.smsspamguard.R;
import com.smsspamguard.db.Database;

public class BlacklistText extends ListActivity {
	private Database db;
	private Cursor listCursor = null;
	private Cursor conflictCursor = null;
	private SimpleCursorAdapter cursorAdapter;
	private long selectedItemId;
	private String type;

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			final EditText input = new EditText(this);
			final AlertDialog dialog = new AlertDialog.Builder(getParent()).setTitle(R.string.add_text).setView(input).setPositiveButton(
					R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							if (!input.getText().toString().equals("")) {
								conflictCursor = db.conflictCheck("_c_", input.getText().toString());
								if(conflictCursor.getCount() == 0)
								{
									db.insertList(type, input.getText().toString());
									input.setText(null);
									cursorAdapter.getCursor().requery();
									//cursorAdapter.notifyDataSetChanged();
								} else {
									input.setText(null);
									Toast
											.makeText(BlacklistText.this, "Text already exists in either whitelist or blacklist.",
													Toast.LENGTH_LONG).show();
								}
								conflictCursor.close();
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
		case 1:
			final EditText input2 = new EditText(this);
			input2.setText(cursorAdapter.getCursor().getString(1));
			final AlertDialog dialog2 = new AlertDialog.Builder(getParent()).setTitle(R.string.update_entry).setView(input2).setPositiveButton(
					R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							if (!input2.getText().toString().equals("")) {
								conflictCursor = db.conflictCheck("_c_", input2.getText().toString());
								if(conflictCursor.getCount() == 0)
								{
									ContentValues values = new ContentValues();
									values.put("value", input2.getText().toString());
									db.updateList(selectedItemId, values);
									cursorAdapter.getCursor().requery();
								} else {
									Toast
											.makeText(BlacklistText.this, "Text already exists in either whitelist or blacklist.",
													Toast.LENGTH_LONG).show();
								}
								conflictCursor.close();
							} else {
								Toast
								.makeText(BlacklistText.this, "Entry cannot be empty.",
										Toast.LENGTH_LONG).show();
							}
						}
					}).setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			}).create();

			input2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						dialog2.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
					}
				}
			});
			return dialog2;
		default:
			return null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.listcontentmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_text:
			type = "bct";
			showDialog(0);
			return true;
		case R.id.add_regex:
			type = "bcr";
			showDialog(0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Choose...");
		menu.add(0, 0, 0, R.string.update_entry);
		menu.add(0, 1, 0, R.string.delete_entry);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case 0:
			selectedItemId = info.id;
			showDialog(1);
			return true;
		case 1:
			db.deleteList(info.id);
			cursorAdapter.getCursor().requery();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.db = Database.getInstance(this);
		listCursor = db.getList("bc");
		String[] from = new String[] { listCursor.getColumnName(1) };
		cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, listCursor, from, new int[] { android.R.id.text1 });
		this.setListAdapter(cursorAdapter);
		startManagingCursor(listCursor);
		registerForContextMenu(getListView());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
//		if (db != null) {
//			db.close();
//		}
		if(listCursor != null) {
			listCursor.close();
		}
	}
}
