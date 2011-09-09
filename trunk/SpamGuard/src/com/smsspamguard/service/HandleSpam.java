//package com.smsspamguard.service;
//
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.IBinder;
//import android.util.Log;
//
//import com.smsspamguard.R;
//import com.smsspamguard.activity.Main;
//import com.smsspamguard.constant.Constants;
//import com.smsspamguard.db.Database;
//import com.smsspamguard.model.Message;
//import com.smsspamguard.receiver.SmsIntentReceiver;
//
//public class HandleSpam extends Service {
//	
//	
//	private NotificationManager mNotificationManager;
//	private int SIMPLE_NOTFICATION_ID = 1;
//	private static ExecutorService executor = Executors.newSingleThreadExecutor();
//	
//	private class SpamThread implements Runnable {
//
//		Context ctx;
//		Database db;
//
//		public SpamThread(Context ctx) {
//			this.ctx = ctx;
//		}
//
//		@Override
//		public void run() {
//
//			db = new Database(ctx);
//			Uri uri = Uri.parse("content://sms/inbox");
//			Cursor cursor = ctx.getContentResolver().query(uri, new String[] { "_id" }, null, null, null);
//			
//			int before = cursor.getCount();
//
//			while (before == cursor.getCount()) {
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				cursor.requery();
//			}
//			cursor.close();
//			
//			Log.i(Constants.DEBUG_TAG,"Passed infinite loop");
//			
//			boolean unreadOnly = false;
//			String SMS_READ_COLUMN = "read";
//			String WHERE_CONDITION = unreadOnly ? SMS_READ_COLUMN + " = 0" : null;
//			String SORT_ORDER = "date DESC";
//
//			cursor = ctx.getContentResolver().query(uri, new String[] {"address", "date", "body" },
//					WHERE_CONDITION, null, SORT_ORDER);
//
//			cursor.moveToFirst();
//
////			long messageId = cursor.getLong(0);
////			long threadId = cursor.getLong(1);
//			String address = cursor.getString(0);
//			//long contactId = cursor.getLong(1);
//			long date = cursor.getLong(1);
//			String messageBody = cursor.getString(2);
//
//			Message message = new Message(address, date, messageBody);
//			db.insertSpam(message);
//
//			Log.i(Constants.DEBUG_TAG,address + " " + date + " " + messageBody);
//			
//			ContentValues values = new ContentValues();
//			values.put("read", 1);
//			ctx.getContentResolver().update(uri, values, "date=?",
//					new String[] { String.valueOf(date) });
//
//			ctx.getContentResolver()
//					.delete(uri, "date=?", new String[] { String.valueOf(date) });
//
//			cursor.close();
//			db.close();
//
//			// display a notification for caught spam
//			mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
//			Notification notifySpam = new Notification(R.drawable.ic_menu_add, "SpamGuarded!", System.currentTimeMillis());
//			
//			Intent intent= new Intent(ctx, Main.class);
//			intent.putExtra("defaultTab", 3);
//			PendingIntent myIntent = PendingIntent.getActivity(ctx, 0, intent , 0);
//			notifySpam.flags |= Notification.FLAG_AUTO_CANCEL;
//			notifySpam.setLatestEventInfo(ctx, "SpamGuard", "Click to view spams", myIntent);
//			mNotificationManager.notify(SIMPLE_NOTFICATION_ID, notifySpam);
//		}
//	}
//	
//    @Override
//    public void onCreate() {
//        super.onCreate();
////        Runnable r = new SpamThread(this);
////		executor.execute(r);
//        Log.i(Constants.DEBUG_TAG,"onCreate'te");
//    }
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//    	Log.i(Constants.DEBUG_TAG,"onStartCommand'de");
//    	Context ctx;
//		Database db;
//
//			db = Database.getInstance();
//			Uri uri = Uri.parse("content://sms/inbox");
//			Cursor cursor = getContentResolver().query(uri, new String[] { "_id" }, null, null, null);
//			
//			int before = cursor.getCount();
//			Log.i(Constants.DEBUG_TAG,"before: " + before);
//			while (before == cursor.getCount()) {
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				cursor.requery();
//				Log.i(Constants.DEBUG_TAG,"waiting..");
//			}
//			cursor.close();
//			
//			Log.i(Constants.DEBUG_TAG,"Passed infinite loop");
//			
//			boolean unreadOnly = false;
//			String SMS_READ_COLUMN = "read";
//			String WHERE_CONDITION = unreadOnly ? SMS_READ_COLUMN + " = 0" : null;
//			String SORT_ORDER = "date DESC";
//
//			cursor = getContentResolver().query(uri, new String[] {"address", "date", "body" },
//					WHERE_CONDITION, null, SORT_ORDER);
//
//			cursor.moveToFirst();
//
////			long messageId = cursor.getLong(0);
////			long threadId = cursor.getLong(1);
//			String address = cursor.getString(0);
//			//long contactId = cursor.getLong(1);
//			long date = cursor.getLong(1);
//			String messageBody = cursor.getString(2);
//
//			Message message = new Message(address, date, messageBody);
//			db.insertSpam(message);
//
//			Log.i(Constants.DEBUG_TAG,address + " " + date + " " + messageBody);
//			
//			ContentValues values = new ContentValues();
//			values.put("read", 1);
//			getContentResolver().update(uri, values, "date=?",
//					new String[] { String.valueOf(date) });
//
//			getContentResolver()
//					.delete(uri, "date=?", new String[] { String.valueOf(date) });
//
//			cursor.close();
//			db.close();
//
//			// display a notification for caught spam
//			mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//			Notification notifySpam = new Notification(R.drawable.ic_menu_add, "SpamGuarded!", System.currentTimeMillis());
//			
//			intent= new Intent(this, Main.class);
//			intent.putExtra("defaultTab", 3);
//			PendingIntent myIntent = PendingIntent.getActivity(this, 0, intent , 0);
//			notifySpam.flags |= Notification.FLAG_AUTO_CANCEL;
//			notifySpam.setLatestEventInfo(this, "SpamGuard", "Click to view spams", myIntent);
//			mNotificationManager.notify(SIMPLE_NOTFICATION_ID, notifySpam);
//    	
//        return START_NOT_STICKY;
//    }
//	@Override
//	public IBinder onBind(Intent intent) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//}