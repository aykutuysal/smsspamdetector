package com.smsspamguard;

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class Spams extends ListActivity {
	private Database db;

	public void refreshList()
	{
		List<String> names = db.selectAll("spam");
		setListAdapter(new ArrayAdapter<String>(Spams.this, android.R.layout.simple_list_item_1, names));
		getListView().setTextFilterEnabled(true);
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.db = new Database(this);
		refreshList();
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
