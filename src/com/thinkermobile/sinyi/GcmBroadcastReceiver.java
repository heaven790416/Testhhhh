package com.thinkermobile.sinyi;

import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.Constants;
import com.yowoo.newbuyhouse.Singleton;
import com.yowoo.newbuyhouse.track.TrackSubscribeActivity;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
	
	
    @Override
    public void onReceive(Context context, Intent intent) {
       
    	
    		Log.e("test", "onRecieve!");
    		
    		//GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
    		// The getMessageType() intent parameter must be the intent you received
    		// in your BroadcastReceiver.
    		//String messageType = gcm.getMessageType(intent);//gcm
    		Bundle extras = intent.getExtras();
    		
    		String title = extras.getString("title");
    		String notificationType = extras.getString("message");
    		JSONObject extraData;
    		try {
    			extraData = new JSONObject(extras.getString("extra"));
    		} catch (Exception e) {
    			extraData = new JSONObject();
    			e.printStackTrace();
    		}
    		//showToast();
    		//Log.i("GCM", "Received : (" +messageType+")  "+extras.getString("title"));

    		if (notificationType.equals(Constants.NEW_SUBSCRIBE_HOUSE)){
    			showNotification(context, title, Constants.NEW_SUBSCRIBE_HOUSE_NOTIFICATION, extraData);
    		}

    		//TODO:若有需要再背景執行較長作業的，可使用此方式
//    		// Explicitly specify that GcmMessageHandler will handle the intent.
//        ComponentName comp = new ComponentName(context.getPackageName(),
//        		GcmMessageHandler.class.getName());
//        
//        // Start the service, keeping the device awake while it is launching.
//        startWakefulService(context, (intent.setComponent(comp)));
//        setResultCode(Activity.RESULT_OK);
    }
    
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
//			Intent resultIntent = new Intent(context, MainActivity.class);
//			resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			resultIntent.putExtra(BHConstants.EXTRA_GOTO_FRAGMENT_POS, BHConstants.MENU_TRACK_POSITION);
			Intent resultIntent = new Intent(context, TrackSubscribeActivity.class);
			resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			resultIntent.putExtra(BHConstants.EXTRA_FROM_NOTIFICATION, true);
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
