//package com.smsspamguard;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.EditText;
//
//public class AddDialog extends Activity {
//    protected Dialog onCreateDialog() {
//    	LayoutInflater factory = LayoutInflater.from(this);
//        final View textEntryView = factory.inflate(R.layout.insertnumber, null);
//        return new AlertDialog.Builder(AddDialog.this)
//            //.setIcon(R.drawable.alert_dialog_icon)
//            .setTitle(R.string.insert_number)
//            .setView(textEntryView)
//            .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                	EditText mUserText = (EditText)findViewById(R.id.insert_number);
//                	String text = mUserText.getText().toString();
////                	db.insert(text);
//                }
//            })
//            .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//
//                    /* User clicked cancel so do some stuff */
//                }
//            })
//            .create();
//    }
//}
