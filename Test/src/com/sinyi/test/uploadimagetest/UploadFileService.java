package com.sinyi.test.uploadimagetest;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class UploadFileService extends IntentService {

	private static final String SERVICE_NAME = UploadFileService.class.getName();
	private static final String TAG = "AndroidUploadService";

	public static final String ACTION_ON_UPLOAD_CHANGE = "actionOnUploadChange";
	// For testing
	private static final String IMGUR_CLIENT_ID = "b2dfa5728d0ee20";

	private static final String actionName = "com.yowoo.uploadservice.action.upload";
	public static final String PARAM_UPLOAD_IDS = "uploadIds";
	// public static final String PARAM_UPLOAD_ID = "uploadId";

	private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
	private static final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");

	private final OkHttpClient client = new OkHttpClient();
	public static String ImageUrl = "";

	private NotificationManager notificationManager;
	private Builder notification;
	private PowerManager.WakeLock wakeLock;
	private SharedPreferences preferences;
	private Editor preferencesEditor;

	public UploadFileService() {
		super(SERVICE_NAME);
	}

	public static void startUpload(Context context, Intent intent) throws Exception {

		intent.setAction(actionName);
		context.startService(intent);
	}

	@Override
	public void onCreate() {
		super.onCreate();

		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notification = new Builder(this);
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferencesEditor = preferences.edit();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent != null) {
			final String action = intent.getAction();
			if (actionName.equals(action)) {
				// Gather params from Intent here...
				// final String uploadId =
				// intent.getStringExtra(PARAM_UPLOAD_ID);
				// final String uploadIds =
				// preferences.getString(PARAM_UPLOAD_IDS, null);
				// Get all images
				String uploadIds = intent.getStringExtra("imageUrls");

				String[] orderIds = null;

				if (null != uploadIds && !"".equals(uploadIds)) {
					// orderIds = uploadIds.split(",");
					// for (int i = 0; i < orderIds.length; i++) {
					// String orderId = orderIds[i];
					// if (null != orderId && !"".equals(orderId) &&
					// orderId.length() > 1) {
					uploadPhotosByThisOrderId(uploadIds);
					// }
					// }
				}
			}
		}
	}

	private void uploadPhotosByThisOrderId(String uploadUrlsString) {
		// final String uploadUrlsString = preferences.getString(orderId, null);
		// final String uploadUrlsString = preferences.getString(orderId, null);

		System.out.println("Content of all the upload files: " + uploadUrlsString);
		if (null != uploadUrlsString && !"".equals(uploadUrlsString)) {
			// String[] uploadUrls = uploadUrlsString.split(",");
			// System.out.println("Size of all the upload files: " +
			// uploadUrls.length);
			// for (String fileUrl : uploadUrls) {
			// //the length of the "a.png" is at least 5
			// if (fileUrl.length() > 4) {
			wakeLock.acquire();
			try {
				createNotification();
				handleFileUpload(uploadUrlsString, uploadUrlsString);
			} catch (Exception e) {
				System.out.println("################");
				System.out.println("################");
				System.out.println("################");
				System.out.println("OrderId: " + uploadUrlsString + ", fileUrl: " + uploadUrlsString
						+ ", ERROR handleFileUpload()!! ==> " + e.getMessage());
				System.out.println("################");
				System.out.println("################");
				System.out.println("################");
				broadcastError(uploadUrlsString, e);
			} finally {
				wakeLock.release();
			}
			// }
			// }
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			System.out.println("All files have been uploaded!!!");
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		}
	}

	private void handleFileUpload(String orderId, String uploadUrl) throws Exception {

		System.out.println("Start to upload File: " + uploadUrl + " to order id: " + orderId);

		// RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JPEG, new
		// File(uploadUrl));
		File tempFile = new File(uploadUrl);
		Bitmap large = BitmapHelper.getBitmap(uploadUrl, 360000);
		saveBitmap(large, tempFile.getPath(), Bitmap.CompressFormat.JPEG);

		// RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JPEG,
		// tempFile);

		RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
				// .addPart(Headers.of("Content-Disposition", "form-data;
				// name=\"title\""),
				// RequestBody.create(null, orderId + "'s image"))
				// .addPart(Headers.of("Content-Disposition", "form-data;
				// name=\"image\""),
				// RequestBody.create(MEDIA_TYPE_JPEG,
				// new File(uploadUrl)))
				// .addPart(Headers.of("Content-Disposition", "form-data;
				// name=\"userPhoto\""),
				// RequestBody.create(MEDIA_TYPE_JPEG,
				// new File(uploadUrl)))
				.addPart(
						Headers.of("Content-Disposition",
								"form-data; name=\"userPhoto\";filename=\"" + tempFile.getName() + "\""),
						RequestBody.create(MEDIA_TYPE_JPEG, tempFile))
				// .addPart(Headers.of("Content-Disposition", "form-data;
				// filename=\"123456.jpg\""),
				// RequestBody.create(null,
				// tempFile.getName()))
				.build();

		Request request = new Request.Builder()
				// .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
				.header("Content-Type", "image/jpeg").url("http://180.150.179.114:8080/api/photo").post(requestBody)
				.build();

		Response response = client.newCall(request).execute();
		System.out.print("Sheri test" + response);
		if (!response.isSuccessful()) {
			System.out.println("ERROR Upload files but with response!! ==> " + response);
			System.out.println("================");
			System.out.println("");
			System.out.println("");
			System.out.println("");
			System.out.println("");
			throw new IOException("Unexpected code " + response);
		} else {
			System.out.println("End to upload File: " + uploadUrl + " to order id: " + orderId);
			System.out.println("And the file upload response is: " + response.body().string());
			// removeThisFileUrlFromOrderIdChildrenUrls(orderId, uploadUrl);
			System.out.println("uploadUrl: " + uploadUrl + " from orderId: " + orderId + " has been removed!");
			System.out.println("========END This file uploading========");
			System.out.println("");
			System.out.println("");
			System.out.println("");
			System.out.println("");
			// brocast
			Intent intent = new Intent(ACTION_ON_UPLOAD_CHANGE);
			// intent.putExtra("successUpload",tempFile.getName());
			ImageUrl = tempFile.getName();
			sendBroadcast(intent);

		}
	}/* SellHouse: File */

	public static void saveBitmap(Bitmap bitmap, String filePath, Bitmap.CompressFormat format) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath));
			bitmap.compress(format, 85, fileOutputStream);

		} catch (Exception e) {
			Log.e("SAVE BITMAP EXCEPTION:", e.toString());

		}
	}

	private void removeThisFileUrlFromOrderIdChildrenUrls(String orderId, String uploadUrl) {
		// 檔案上傳成功，從local中拿掉此連結
		String oldUploadUrlsString = preferences.getString(orderId, "");
		String newUploadUrlsString = oldUploadUrlsString.replace(uploadUrl + ",", "");
		if (newUploadUrlsString.indexOf(",") < 0) {
			// 檔案全部上傳完，所以string被移空了，因此就可以將此orderId刪除
			preferencesEditor.remove(orderId);
			removeThisOrderIdFromUploadIds(orderId);
		} else {
			preferencesEditor.remove(orderId);
			preferencesEditor.putString(orderId, newUploadUrlsString);
			preferencesEditor.commit();
		}
	}

	private void removeThisOrderIdFromUploadIds(String orderId) {
		String uploadIds = preferences.getString(PARAM_UPLOAD_IDS, "");
		String newUploadIds = uploadIds.replace(orderId + ",", "");
		if (newUploadIds.indexOf(",") < 0) {
			preferencesEditor.remove(PARAM_UPLOAD_IDS);
		} else {
			preferencesEditor.remove(PARAM_UPLOAD_IDS);
			preferencesEditor.putString(PARAM_UPLOAD_IDS, newUploadIds);
		}
		preferencesEditor.commit();
	}

	private void broadcastError(final String uploadId, final Exception exception) {

	}

	private void createNotification() {
		// notification.setContentTitle(notificationConfig.getTitle()).setContentText(notificationConfig.getMessage())
		// .setContentIntent(PendingIntent.getBroadcast(this, 0, new
		// Intent(), PendingIntent.FLAG_UPDATE_CURRENT))
		// .setSmallIcon(notificationConfig.getIconResourceID()).setProgress(100,
		// 0, true).setOngoing(true);
		//
		// startForeground(UPLOAD_NOTIFICATION_ID,
		// notification.build());
	}
}
