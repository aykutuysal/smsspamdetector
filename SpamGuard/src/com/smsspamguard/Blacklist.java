package com.smsspamguard;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

public class Blacklist extends ListActivity {
	private Database db;

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case R.id.add_number:
			Log.i("dialog", "dialog");
			Log.i("dialogNumber", String.valueOf(R.id.add_number));
			final EditText input = new EditText(this);
			return new AlertDialog.Builder(Blacklist.this).setTitle(
					R.string.insert_number).setView(input).setPositiveButton(
					R.string.alert_dialog_ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							if (input.getText().toString() != null) {
								try {
									db.insert("bn", input.getText().toString());
									input.setText(null);
									List<String> names = db.selectAll("b");
									setListAdapter(new ArrayAdapter<String>(
											Blacklist.this,
											android.R.layout.simple_list_item_1,
											names));
									getListView().setTextFilterEnabled(true);
								}
								catch(SQLiteConstraintException e)
								{
									Toast.makeText(Blacklist.this, "Phone number already exists in either whitelist or blacklist.", Toast.LENGTH_LONG).show();
								}
							}
						}
					}).setNegativeButton(R.string.alert_dialog_cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							input.setText(null);
						}
					}).create();
		default:
			Log.i("dialogDefault", "dialog");
			Log.i("dialogCaseNumber", String.valueOf(R.id.add_number));
			Log.i("dialogReceivedNumber", String.valueOf(id));
		}

		return null;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.blackwhitelistmenu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("menuClicked", "true");
		Log.i("add_number", String.valueOf(R.id.add_number));
		switch (item.getItemId()) {
		case R.id.add_number:
			Log.i("addClicked", "true");
			showDialog(R.id.add_number);
			return true;
		default:
			Log.i("whatIsClicked", String.valueOf(item.getItemId()));
			return super.onOptionsItemSelected(item);
		}
	}

	/** press-hold/context menu */
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Choose...");
		menu.add(0, 0, 0, R.string.delete_entry);
	}

	/** when press-hold option selected */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// AdapterContextMenuInfo info = (AdapterContextMenuInfo)
		// item.getMenuInfo();
		// info.
		// db.delete(item.getTitle().toString());
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
     
		this.db = new Database(this);
		List<String> names = this.db.selectAll("b");
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, names));
		registerForContextMenu(getListView());
	}

	public void onStart() {
		super.onStart();
	}

	public void onResume() {
		super.onResume();
	}

	public void onPause() {
		super.onPause();
	}

	public void onDestroy() {
		super.onDestroy();
		if (db != null) {
			db.close();
		}
	}
}
