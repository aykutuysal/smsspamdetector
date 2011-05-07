package com.smsspamguard;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.ArrayAdapter;

public class Spams extends ListActivity {
	private Database db;

	public void refreshList()
	{
		List<SmsMessage> spams = db.selectAllSpam();
		List<String> messageBodies = new ArrayList<String>();
		
		for(SmsMessage message : spams) {
			messageBodies.add(message.getDisplayMessageBody());
		}
		
		setListAdapter(new ArrayAdapter<String>(Spams.this, android.R.layout.simple_list_item_1, messageBodies));
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