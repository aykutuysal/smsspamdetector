package com.smsspamguard.activity;

import java.io.IOException;

import libsvm.svm_node;
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
import com.smsspamguard.engine.svm.SvmManager;
import com.smsspamguard.engine.svm.core.SVMSpam;

public class Spams extends ListActivity {
	private Database db;
	private Cursor spamCursor = null;
	private SimpleCursorAdapter cursorAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String msg = "27-28 Kasim 2010 tarihinde duzenlemis oldugumuz Insaat Muhendisligi Calistayi'nda gorusulen Rapor Taslaklari Subemizin www.imoistanbul.org.tr adresinde yayimlanmistir. Rapor taslaklarini inceleyerek, goruslerinizi 31 Aralik 2010 tarihine kadar Subemize iletmenizi rica ederiz.";
		Log.i(Constants.DEBUG_TAG, "Starting SVM Test for: " + msg);
		SVMSpam svmSpam = SvmManager.getSvm(getApplicationContext());
		svm_node[] nodes = SvmManager.getSvmNodeFromMessage(msg, getApplicationContext());
		svm_node[] scaledNodes = SvmManager.scaleSingleMessage(nodes, getApplicationContext());
		double result = svmSpam.predictSingle(scaledNodes);
		Log.i(Constants.DEBUG_TAG, "Result : " + result);
		Log.i(Constants.DEBUG_TAG, "Finished SVM Test");
		
		
		try {
			FileCopier.backupFiles();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.db = new Database(this);
		spamCursor = db.getSpams();
		String[] from = new String[] { spamCursor.getColumnName(6) };
		cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, spamCursor, from, new int[] { android.R.id.text1 });
		this.setListAdapter(cursorAdapter);
		registerForContextMenu(getListView());
		cursorAdapter.notifyDataSetChanged();	//bu satir istedigim gibi calismiyor, spamse bakarken yeni sms gelince listeyi update etsin istiom
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
			values.put("_id", cursorAdapter.getCursor().getLong(1));
			values.put("thread_id", cursorAdapter.getCursor().getLong(2));
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
