package com.smsspamguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		String message = bundle.getString("alarm_message");
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

}
