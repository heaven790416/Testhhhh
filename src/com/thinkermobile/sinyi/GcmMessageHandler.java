package com.thinkermobile.sinyi;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.Constants;
import com.yowoo.newbuyhouse.MainActivity;
import com.yowoo.newbuyhouse.Singleton;

public class GcmMessageHandler extends IntentService {

	//String mes;
	private Handler handler;
	public GcmMessageHandler() {
		super("GcmMessageHandler");
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		handler = new Handler();
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();

		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);//gcm

		String title = extras.getString("title");
		String notificationType = extras.getString("message");
		JSONObject extraData;
		try {
			extraData = new JSONObject(extras.getString("extra"));
		} catch (JSONException e) {
			extraData = new JSONObject();
			e.printStackTrace();
		}
		//showToast();
		Log.i("GCM", "Received : (" +messageType+")  "+extras.getString("title"));

		if (notificationType.equals(Constants.NEW_SUBSCRIBE_HOUSE)){
			showNotification(getApplicationContext(), title, Constants.NEW_SUBSCRIBE_HOUSE_NOTIFICATION, extraData);
		}
		
		GcmBroadcastReceiver.completeWakefulIntent(intent);

	}

//	public void showToast(){
//		handler.post(new Runnable() {
//			public void run() {
//				Toast.makeText(getApplicationContext(),mes , Toast.LENGTH_LONG).show();
//			}
//		});
//
//	}

	private void showNotification(Context context, final String message, final int notificationType, JSONObject json) {
		Singleton.log("SHOW NOTIFICATION: " + message + ", "+notificationType);

		//set sound
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		//show Test Notification
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
		.setAutoCancel(true)
		.setSmallIcon(R.drawable.sy_icon)
		.setContentTitle(context.getResources().getString(R.string.app_name))
		.setContentText(message)
		.setSound(alarmSound);

		NotificationManager mNotificationManager =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


		if (notificationType==Constants.NEW_SUBSCRIBE_HOUSE_NOTIFICATION) {

			// Creates an explicit intent for an Activity in your app
			Intent resultIntent = new Intent(context, MainActivity.class);
			resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			mNotificationManager.cancel(Constants.NEW_SUBSCRIBE_HOUSE_NOTIFICATION); // first, cancel all notifications

			// The stack builder object will contain an artificial back stack for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out of
			// your application to the Home screen.
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			// Adds the back stack for the Intent (but not the Intent itself)
			//stackBuilder.addParentStack(WebChatActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent =
					stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(resultPendingIntent);
			// mId allows you to update the notification later on.
			mNotificationManager.notify(Constants.NEW_SUBSCRIBE_HOUSE_NOTIFICATION, mBuilder.build());
		}

	}




}
