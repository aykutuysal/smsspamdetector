package com.djan.smscolumns;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class ReadSMS extends Activity 
{
    private static final String tag = "Whozzat";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Uri sms = Uri.parse("content://sms/inbox");
        ContentResolver cr = this.getContentResolver();
        Cursor c = cr.query(sms, null, null, null, null);
        for (int i = 0; i < c.getColumnCount(); i++)
        {
            Log.v(tag, c.getColumnName(i).toString());
        }
        c.close();
    }
}