package com.smsspamguard;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class BlacklistText extends Activity {

	@Override
	protected void onResume() {
		super.onResume();

		boolean unreadOnly = false;

		String SMS_READ_COLUMN = "read";
		String WHERE_CONDITION = unreadOnly ? SMS_READ_COLUMN + " = 0" : null;
		String SORT_ORDER = "date DESC";
		int count = 0;

		Uri uri = Uri.parse("content://sms/inbox");

		Cursor cursor = getContentResolver().query(
				uri,
				new String[] { "_id", "thread_id", "address", "person", "date",
						"body" }, WHERE_CONDITION, null, SORT_ORDER);

		if (cursor != null) {
			try {
				count = cursor.getCount();
				if (count > 0) {

					System.out.println("SMS Messages Retrieved ---------------------------------------------------");
					while (cursor.moveToNext()) {

						String[] columns = cursor.getColumnNames();
						for (int i = 0; i < columns.length; i++) {
							Log.i("SPAMGUARD", "columns " + i + ": "
									+ columns[i] + ": " + cursor.getString(i));
						}

						long messageId = cursor.getLong(0);
						long threadId = cursor.getLong(1);
						String address = cursor.getString(2);
						long contactId = cursor.getLong(3);
						String contactId_string = String.valueOf(contactId);
						long timestamp = cursor.getLong(4);

						String body = cursor.getString(5);

						if (!unreadOnly) {
							count = 0;
						}

						System.out.println(messageId + " " + threadId + " "
								+ address + " " + contactId + " " + timestamp
								+ " " + body);
					}
					System.out.println("Finished --------------------------------------------------------");

				}
			} finally {
				cursor.close();
			}
		}

	}
}
