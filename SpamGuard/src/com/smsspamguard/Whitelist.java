package com.smsspamguard;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class Whitelist extends ListActivity {
	static final String[] COUNTRIES = new String[] {
	    "Afghanistan", "Albania", "Algeria"};
	private TextView output;
	private Database db;
	
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case R.id.add:
    	LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.insertnumber, null);
        return new AlertDialog.Builder(Whitelist.this)
            //.setIcon(R.drawable.alert_dialog_icon)
            .setTitle(R.string.insert_number)
            .setView(textEntryView)
            .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	EditText mUserText = (EditText)findViewById(R.id.insert_number);
                	String text = mUserText.getText().toString();
                	db.insert(text);
                }
            })
            .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    /* User clicked cancel so do some stuff */
                }
            })
            .create();
        }
        return null;
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.output = (TextView) this.findViewById(R.id.out_text);
        
        this.db = new Database(this);
        this.db.deleteAll();
        this.db.insert("Porky Pig");
        this.db.insert("Foghorn Leghorn");
        this.db.insert("Yosemite Sam");
        this.db.insert("+905552032593");
        List<String> names = this.db.selectAll();
        StringBuilder sb = new StringBuilder();
        sb.append("Names in database:\n");
        for (String name : names) {
           sb.append(name + "\n");
        }
        
        Log.d("EXAMPLE", "names size - " + names.size());
        
        //this.output.setText(sb.toString());//hata burdda////
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, names));
        getListView().setTextFilterEnabled(true);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.blackwhitelistmenu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
    	Log.i("menuClicked", "true");
    	/*ID'DE PROBLEM VAR ADD IDSI DONMUYOR ONA BAKICAM*/
        switch (item.getItemId()) {
        case R.id.add:
//			Intent intent = new Intent(Whitelist.this, AddDialog.class);
//            startActivity(intent);
        	Log.i("addClicked", "true");
            showDialog(R.id.add);
//        	LayoutInflater factory = LayoutInflater.from(this);
//            final View textEntryView = factory.inflate(R.layout.insertnumber, null);
//            new AlertDialog.Builder(Whitelist.this)
//                //.setIcon(R.drawable.alert_dialog_icon)
//                .setTitle(R.string.insert_number)
//                .setView(textEntryView)
//                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                    	EditText mUserText = (EditText)findViewById(R.id.insert_number);
//                    	String text = mUserText.getText().toString();
//                    	db.insert(text);
//                    }
//                })
//                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//
//                        /* User clicked cancel so do some stuff */
//                    }
//                })
//                .create();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    public void onStart()
    {
    	super.onStart();
    }
    public void onResume()
    {
    	super.onResume();
    }
    public void onPause()
    {
    	super.onPause();
    }
    public void onDestroy()
    {
    	super.onDestroy();
    }
}
