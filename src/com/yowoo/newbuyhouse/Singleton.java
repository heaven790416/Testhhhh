package com.yowoo.newbuyhouse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONArray;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshWebView;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.db.NewsDB;


public class Singleton {

	private static Singleton singleton = null;

	public static Context applicationContext = null;

	/* Threads */
//	public static HandlerThread dbHandlerThread;
//	public static Handler dbHandler;
	
	private static PullToRefreshWebView pullRefreshWebView;

	/* Database */
	// public static DatabaseManager dbManager = new DatabaseManager();
	
	/* News DB Setting */
	public static NewsDB newsDB;
	
	/* Download, Upload */
	// public static TransferManager transferManager = null;
	//public static HashMap<String, DownloadObject> downloadObjectDict = new HashMap<String, DownloadObject>();
	// public static HashMap<String, Upload> uploadObjectDict = new HashMap<String, Upload>();


	/* ImageLoader */
	public static DisplayImageOptions thumbnailDisplayImageOptions = null;// for
																			// thumbnail
																			// picture
	public static DisplayImageOptions postImageOptions = null;// for post
																// picture
	
	public static DisplayImageOptions customerDisplayImageOptions = null;
	public static DisplayImageOptions staffDisplayImageOptions = null;
	public static DisplayImageOptions messageThumbDisplayImageOptions = null;
	public static DisplayImageOptions musicDisplayImageOptions = null;

	/* Memory Cache */
	public static LruCache<String, Bitmap> bitmapLruCache = new LruCache<String, Bitmap>(
			(int) (Runtime.getRuntime().maxMemory() / 1024) / 5); // 20% of max
																	// memory
	public static HashMap<String, SoftReference<Bitmap>> bitmapDict = new HashMap<String, SoftReference<Bitmap>>();

	/* GPSTracker class */
	//private static GPSTracker gps;
	public static Double latitude, longitude;

	public static void putCachedBitmap(String key, Bitmap bitmap) {
		if (key == null || bitmap == null) {
			return;
		}

		SoftReference<Bitmap> reference = bitmapDict.get(key);

		if (reference == null || reference.get() == null) {
			bitmapDict.put(key, new SoftReference<Bitmap>(bitmap));
		}

		if (getCachedBitmap(key) == null) {
			bitmapLruCache.put(key, bitmap);
		}
	}

	public static Bitmap getCachedBitmap(String key) {
		if (key == null) {
			return null;
		}

		// use lru cache first
		Bitmap bitmap = bitmapLruCache.get(key);

		if (bitmap != null) {
			return bitmap;
		}

		// use soft ref later
		SoftReference<Bitmap> reference = bitmapDict.get(key);

		if (reference == null || reference.get() == null) {
			return null;
		}

		return reference.get();
	}

	/* NewBuyHouse Flag */
	public static Boolean needRemindOpenGPS = true;
	
	// SellHouse:���ϥ�
	//public static HashMap<String, UserObject> userDict = new HashMap<String, UserObject>();
	public static int nearbyBeforeTime = Constants.MAX_BEFORE_TIME;
	public static String chattingUserObjectId = "";
	public static String lastUpdatedChatRoomId = "";
	public static Boolean isSendingNewMessagePush = false;
	public static Boolean isFetchingMessage = false;
	public static Boolean isAtRequestSongPage = false;//for�I�qshowToast
	public static HashSet<String> demandedSongSet = new HashSet<String>();

	public static Gson gson = new Gson();
	public static Resources resources = null;
	public static SharedPreferences preferences = null;
	public static Editor preferenceEditor = null;
	public static float SCREEN_DENSITY = 0.0f;
	public static int SCREEN_WIDTH = 0;
	public static int SCREEN_HEIGHT = 0;
	public static Handler handler;
	
	private static WebView webView;
	
	private Singleton(Context context) {
//		dbHandlerThread = new HandlerThread("DB_THREAD");
//		dbHandlerThread.start();
//		dbHandler = new Handler(dbHandlerThread.getLooper());

		try {
			// database = getDataBase();
			loadCache();

			// image loader settings
			File cacheDir = StorageUtils.getCacheDirectory(context);

			DisplayImageOptions defaultDisplayImageOptions = new DisplayImageOptions.Builder()
					.cacheInMemory(true).cacheOnDisc(true).build();

			ImageLoaderConfiguration.Builder imageLoaderConfigBuilder = new ImageLoaderConfiguration.Builder(
					context)
					.memoryCacheExtraOptions(480, 800)
					// default = device screen dimensions
					//.discCacheExtraOptions(480, 800, CompressFormat.JPEG)
					.threadPoolSize(5)
					// default
					.denyCacheImageMultipleSizesInMemory()
					.defaultDisplayImageOptions(defaultDisplayImageOptions)
					.memoryCache(new LruMemoryCache(4 * 1024 * 1024))
					.memoryCacheSize(4 * 1024 * 1024)
					.memoryCacheSizePercentage(20)
					// default
					.discCache(new UnlimitedDiscCache(cacheDir))
					// default
					.discCacheSize(64 * 1024 * 1024).discCacheFileCount(10000);
			
			//add some config to control memory of imageloader
			//depend on the maxMemory of this app
			try{
				Runtime rt = Runtime.getRuntime();
				long maxMemory = rt.maxMemory();
				Log.e("test", "maxMemory: "+maxMemory);
				if (maxMemory > (1024*1024*BHConstants.IMAGE_LOADER_CONFIG_MEMORY_LIMIT)){
					//maxMemory較大的手機
				}else{
					//maxMemory較小的手機，需開啟此項設定
					imageLoaderConfigBuilder.diskCacheExtraOptions(480, 320, null);
				}
			}catch(Exception e){}
	        
			//build and set config to imageloader
	        ImageLoaderConfiguration config = imageLoaderConfigBuilder.build();	        
			ImageLoader.getInstance().init(config);

			// initial for thumbnail picture
			thumbnailDisplayImageOptions = new DisplayImageOptions.Builder()
			 .cacheInMemory(true)
			 .cacheOnDisc(true)
			 .showImageOnLoading(R.drawable.placeholder_photo).build();

			customerDisplayImageOptions = new DisplayImageOptions.Builder()
			 .cacheInMemory(true)
			 .cacheOnDisc(true)
			 .showImageOnLoading(R.drawable.ic_launcher).build();

			staffDisplayImageOptions = new DisplayImageOptions.Builder()
			.displayer(new RoundedBitmapDisplayer(Singleton.dpToPixel(35)))
			.cacheInMemory(true)
			 .cacheOnDisc(true)
			 .showImageOnLoading(R.drawable.ic_launcher).build();
			
			messageThumbDisplayImageOptions = new DisplayImageOptions.Builder()
			 .cacheInMemory(true)
			 .cacheOnDisc(true)
			 .showImageOnLoading(R.drawable.ic_launcher).build();
			
			musicDisplayImageOptions = new DisplayImageOptions.Builder()
			 .cacheInMemory(true)
			 .cacheOnDisc(true)
			 .showImageOnLoading(R.drawable.ic_launcher).build();

			// postImageOptions = new DisplayImageOptions.Builder()
			// .cacheInMemory(true)
			// .cacheOnDisc(true)
			// .showImageOnLoading(R.drawable.empty).build();
			
//			((ViewGroup)pullRefreshWebView.getParent()).removeView(pullRefreshWebView);
//			((ViewGroup)pullRefreshWebView.getParent()).removeView(webView);
			
		} catch (Exception e) {
		}
	}
	
//	public static void initEbookWebView(View root) {
//		//Init the web view first!
//		pullRefreshWebView = (PullToRefreshWebView) root.findViewById(R.id.pull_refresh_webview);
//		pullRefreshWebView
//		.setOnRefreshListener(new OnRefreshListener<WebView>() {
//
//			@Override
//			public void onRefresh(
//					final PullToRefreshBase<WebView> refreshView) {
//				webView.loadUrl(Constants.PRESENTATION_DEFAULT_URL);
//				refreshView.postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						refreshView.onRefreshComplete();
//					}
//				}, 2 * 1000);
//			}
//		});
//		webView = pullRefreshWebView.getRefreshableView();
//		webView.getSettings().setJavaScriptEnabled(true);
//		webView.getSettings().setSupportZoom(false);
//		webView.getSettings().setPluginState(PluginState.ON);
//		webView.getSettings().setBuiltInZoomControls(false);
//		webView.getSettings().setDatabaseEnabled(true);
//		webView.getSettings().setDomStorageEnabled(true);
//		webView.getSettings().setDatabasePath("/data/data/" + webView.getContext().getPackageName() + "/databases/");
//		
//		//for webview cache
//		webView.getSettings().setAppCacheMaxSize( 5 * 1024 * 1024 ); // 5MB, API 18�H��|�۰�manage�j�p
//		if(null != applicationContext) {
//			webView.getSettings().setAppCachePath(applicationContext.getCacheDir().getAbsolutePath());
//		}
//		webView.getSettings().setAllowFileAccess( true );
//		webView.getSettings().setAppCacheEnabled( true );
//		webView.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT ); // load online by default
//
//		if (!isNetworkAvailable()) { // loading offline
//		    webView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
//		}
//	}

	public static Singleton getInstance(Context appContext) {
		if (applicationContext == null && appContext != null) {
			applicationContext = appContext;
			handler = new Handler(appContext.getMainLooper());

			resources = applicationContext.getResources();

			preferences = PreferenceManager
					.getDefaultSharedPreferences(applicationContext);
			preferenceEditor = preferences.edit();

			WindowManager wm = (WindowManager) applicationContext
					.getSystemService(Context.WINDOW_SERVICE);
			Display disp = wm.getDefaultDisplay();

			SCREEN_WIDTH = disp.getWidth();
			SCREEN_HEIGHT = disp.getHeight();
			SCREEN_DENSITY = resources.getDisplayMetrics().density;

			// Location settings
			
			// News DB setting
			newsDB = new NewsDB(applicationContext);
		}

		if (singleton == null) {
			synchronized (Singleton.class) {
				if (singleton == null) {
					singleton = new Singleton(appContext);
				}
			}
		}

		return singleton;
	}

	public static void loadCache() {
		
	}

	public static void log(String log) {
		if (Constants.IS_SANDBOX) {
			Log.e("SellHouse", log);
		}
	}

	/* ============= Sell House (Chat) ================== */
	public static String getMyUserType() {
		String userType = ParseUser.getCurrentUser().getString(
				Constants.col_user_userType);
		if (userType == null) {
			userType = Constants.USER_TYPE_CUSTOMER;
		}
		// log("getMyUserType: "+ userType);
		return userType;
	}

	public static String getMyStaffObjectId() {
		//get from parseUser
		String staffObjectId = ParseUser.getCurrentUser().getString(
				Constants.col_user_staffObjectId);
		
		//if null, get from preference
		if (staffObjectId == null) {
			staffObjectId = preferences.getString(Constants.MY_STAFF_OBJECT_ID, "");
		}
		
		log("getMyStaffObjectId: " + staffObjectId);
		return staffObjectId;
	}

	public static String getMyRoomIDWithStaff() {
		String roomID = preferences.getString(Constants.MY_ROOMID_WITH_STAFF,
				"");
		if (roomID == null) {
			roomID = "";
		}
		log("getMyRoomID: " + roomID);
		return roomID;
	}

	public static String getMyNickname() {
		String nickname = ParseUser.getCurrentUser().getString(
				Constants.col_user_nickname);
		if (nickname == null) {
			nickname = "";
		}
		return nickname;
	}

	public static String getMyEmail() {
		String email = ParseUser.getCurrentUser().getString(
				Constants.col_user_email);
		if (email == null) {
			email = "";
		}
		return email;
	}
	
	public static String getMyPictureUrl() {
		String pictureUrl = ParseUser.getCurrentUser().getString(
				Constants.col_user_pictureUrl);
		if (pictureUrl == null) {
			pictureUrl = "";
		}
		return pictureUrl;
	}
	
	public static String getMyPictureThumbSizeUrl() {
		String pictureUrl = ParseUser.getCurrentUser().getString(
				Constants.col_user_pictureUrl);
		if (pictureUrl == null) {
			pictureUrl = "";
		}
		
		if (!pictureUrl.equals("")){
			String[] splits = pictureUrl.split(",");
			pictureUrl = splits[0];
		}
		
		return pictureUrl;
	}
	
	public static String getMyPictureOriginalSizeUrl() {
		String pictureUrl = ParseUser.getCurrentUser().getString(
				Constants.col_user_pictureUrl);
		if (pictureUrl == null) {
			pictureUrl = "";
		}
		
		if (!pictureUrl.equals("")){
			String[] splits = pictureUrl.split(",");
			
			if (splits.length>1){
				pictureUrl = splits[1];
			}
		}
		
		return pictureUrl;
	}
	
	public static String getUserPictureThumbSizeUrl(String pictureUrl) {
		String thumbUrl = "";
		if (!pictureUrl.equals("")){
			String[] splits = pictureUrl.split(",");
			thumbUrl = splits[0];
		}
		
		return thumbUrl;
	}
	/* ============= Sell House (Chat) ================== */
	

	/* TOAST */
	public static void showAlertDialog(String title, String message,
			Context context) {
		showAlertDialog(title, message, context, null);
	}

	public static void showAlertDialog(String title, String message,
			Context context, final Runnable runnable) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle(title);
		builder.setMessage(message);
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (runnable != null) {
					handler.post(runnable);
				}
			}
		});

		builder.create().show();
	}

	public static void toastMessage(String message) {
		Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show();
	}

	/* Audio */
	public static int getAudioDuration(String filePath) {
		try {
			MediaPlayer mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(new FileInputStream(filePath).getFD());
			mediaPlayer.prepare(); // might be optional
			int duration = mediaPlayer.getDuration();
			mediaPlayer.release();

			return duration;
		} catch (Exception e) {
			log("AUDIO DURATION FETCH EXCEPTION: "+e.toString());
			return 0;
		}
	}
	
	/* Get GCM Token */
//	public static String getGCMToken() {
//		GCMRegistrar.checkDevice(applicationContext);
//		GCMRegistrar.checkManifest(applicationContext);
//
//		String gcmToken = GCMRegistrar.getRegistrationId(applicationContext);
//
//		if (gcmToken.equals("")) {
//			GCMRegistrar.register(applicationContext, Constants.GCM_APP_ID);
//		}
//
//		return gcmToken;
//	}

	public static boolean isSdCardMounted() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	public static String getSdcardDir() {
		if (isSdCardMounted()) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}

	public static void createExternalMediaFolder() {
		String mediaFolderPath = getExternalMediaFolderPath();
		new File(mediaFolderPath).mkdirs();
	}

	public static String getExternalMediaFolderPath() {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/"+Constants.PACKAGE_NAME+"/";
		
		File dirFile = new File(path);
		if (!dirFile.exists()) dirFile.mkdirs();
		return path;
	}

//	public static String getExternalPublicFolderPath() {
//		return Environment.getExternalStorageDirectory().getAbsolutePath()
//				+ "/"+Constants.PACKAGE_NAME+"/";
//	}

	public static String getExternalTempImagePath() {
		return getExternalMediaFolderPath() + "tmp.jpg";
	}
	
	public static String getExternalTempVideoPath() {
		return getExternalMediaFolderPath() + "tmp.mp4";
	}
	
	public static String getExternalThumbnailImagePath(String noFormatFileName){
		return Singleton.getExternalMediaFolderPath()+Constants.THUMBNAIL_PREFIX+noFormatFileName+".jpg";
	}
	
	public static String getFileNameFromParseFileUrl(String url){
		String[] urlSplits = url.split("-");
		if (urlSplits.length>0){
			return urlSplits[urlSplits.length-1];
		}
		
		return "";
	}
	
	public static String getFileNameFromFilePath(String filePath){
		//filePath�i��Ourl��local path
		String filePathSplits[] = filePath.split("/");
		String[] urlSplits = filePathSplits[filePathSplits.length-1].split("-");
		
		if (urlSplits.length>0){
			return urlSplits[urlSplits.length-1];
		}
		return "";
	}
	
	public static String getSDCardPathByContent(String contentPath){
		String filePath = "";
		if (contentPath.contains("http:")){
			filePath = getExternalMediaFolderPath() + getFileNameFromParseFileUrl(contentPath);
		}else{
			filePath = contentPath;
		}
		return filePath;
	}
	
	public static String getFileFormatFromFilePath(String filePath){
		String filePathSplits[] = filePath.split("\\.");
		return filePathSplits[filePathSplits.length-1];
	}
	
	public static Boolean isHttpUrl(String filePath){
		if (filePath.contains("http:")){
			return true;
		}else{
			return false;
		}
	}
	
	public static Boolean isFileOverUploadMBLimit(String filePath, int mbLimit){
		File file = new File(filePath);
        if (file.length() > (mbLimit*1024*1024)){
        		return true;
        }
        
        return false;
	}
	
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	public static String getFileName(String filePath) {
		String[] components = filePath.split("/");
		return components[components.length - 1];
	}
	
	public static String getFileNameNoFormat(String filePath){
		String fileName = getFileName(filePath);
		String[] noFormatFileName = fileName.split("\\.");
		return noFormatFileName[0];
	}

	public static int dpToPixel(int x) {
		return (int) (SCREEN_DENSITY * (float) x);
	}

	public static int dpToPixel(float x) {
		return (int) (SCREEN_DENSITY * x);
	}

	public static int pixelToDp(int x) {
		return (int) ((float) x / SCREEN_DENSITY);
	}

	public static String toJSON(Object obj) {
		if (gson == null) {
			gson = new Gson();
		}

		return gson.toJson(obj);
	}

	public static boolean isNetworkAvailable() {
		final ConnectivityManager connectivityManager = (ConnectivityManager) applicationContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = connectivityManager
				.getActiveNetworkInfo();

		if (activeNetwork != null && activeNetwork.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	public static String getRealPathFromURI(Uri contentUri, Activity activity) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = activity.managedQuery(contentUri, proj, null, null,
				null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String path = cursor.getString(column_index);

		return path;
	}

	public static String getExternalStoragePath() {
		if (externalStorageAvailable() == false) {
			return null;
		} else {
			return Environment.getExternalStorageDirectory().getPath();
		}
	}

	/* check if external sdcard is mounted */
	public static boolean externalStorageAvailable() {
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state))
			return true;
		else
			return false;
	}

	/* Date Formatter */
	private static SimpleDateFormat hour_Minute_DateFormatter = new SimpleDateFormat(
			"HH:mm");;
	private static SimpleDateFormat month_Day_Weekday_DateFormatter = new SimpleDateFormat(
			"M/d (EEE)");;
	private static SimpleDateFormat month_Day_DateFormatter = new SimpleDateFormat(
			"M/d");;
	private static SimpleDateFormat weekday_DateFormatter = new SimpleDateFormat(
			"EEE");
	public static SimpleDateFormat birthday_DateFormatter = new SimpleDateFormat(
			"yyyy/MM/dd");
	private static SimpleDateFormat message_DateFormatter = new SimpleDateFormat(
			"yyyy.MM.dd (EEE)");
	
	public static SimpleDateFormat news_DateFormatter = new SimpleDateFormat(
			"yyyy年MM月dd日");
	
	//community: Parse
	private static SimpleDateFormat parse_DateFormatter = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	
	//community: Parse
	public static Date getDateFromParseDateString(String dateString){
		if (dateString.equals("")){
			return new Date();
		}
		
		try {
			return parse_DateFormatter.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			return new Date();
		}
	}
	
	public static String toMessageDateString(int timestamp) {
		Date date = new Date(timestamp * 1000L);
		return message_DateFormatter.format(date);
	}

	public static String toBirthdayDateString(int timestamp) {
		Date date = new Date(timestamp * 1000L);
		return birthday_DateFormatter.format(date);
	}

	// => MM/DD (�P���X)
	public static String toDateString(int timestamp) {
		Date date = new Date(timestamp * 1000L);
		return month_Day_Weekday_DateFormatter.format(date);
	}

	// => HH:mm
	public static String toMessageTimeString(int timestamp) {
		Date date = new Date(timestamp * 1000L);
		if (date.getHours() > 12) {

		}
		return hour_Minute_DateFormatter.format(date);
	}

	// => HH:mm AM/PM
	public static String toMessageHourMinString(int timestamp) {
		Date date = new Date(timestamp * 1000L);

		String minsString = Integer.toString(date.getMinutes());
		if ((date.getMinutes() / 10) < 1) {
			minsString = "0" + minsString;
		}

		if (date.getHours() > 12) {
			return (date.getHours() - 12) + ":" + minsString + " PM";
		} else {
			return date.getHours() + ":" + minsString + " AM";
		}
	}

	// 同一天=> HH:mm, 一個禮拜內=> 星期幾, 超過一個禮拜=> MM/DD
	public static String toChatThreadTimeString(int timestamp) {
		
		Date date = new Date(timestamp * 1000L);

		int currentTime = getCurrentTimestamp();
		int timeOffset = currentTime - timestamp;

		if (timeOffset < 86400) {
			return hour_Minute_DateFormatter.format(date);
		} else if (timeOffset < 86400 * 5) {
			return weekday_DateFormatter.format(date);
		} else {
			return month_Day_DateFormatter.format(date);
		}
	}

	public static String getElapsedTimeString(int timestamp) {
		int elapsedTime = getCurrentTimestamp() - timestamp;

		if (elapsedTime < 60) {
			return "剛剛";
		} else if (elapsedTime < 3600) {
			return String.valueOf(elapsedTime / 60) + "分鐘前";
		} else if (elapsedTime < 86400) {
			return String.valueOf(elapsedTime / 3600) + "小時前";
		} else if (elapsedTime < 86400 * 30) {
			return String.valueOf(elapsedTime / 86400) + "天前";
		} else if (elapsedTime < 86400 * 360) {
			return String.valueOf(elapsedTime / 86400 / 30) + "個月前";
		} else {
			return String.valueOf(elapsedTime / 86400 / 300) + "年前";
		}
	}

	/* File Download, Upload */
	public interface FileUploadInterface {
		public void uploadProgressCallback(String filename, double progress);

		public void uploadCompletedCallback(String filename);

		public void uploadFailedCallback(String filename);
	}
	
//	public static void startDownload(final String filename, final DownloadObject.FileDownloadInterface fileDownloadInterface) {
//		try {
//			DownloadObject d = downloadObjectDict.get(filename);
//
//			if(d==null) {
//				d = new DownloadObject(filename);
//				downloadObjectDict.put(filename, d);
//
//				if(fileDownloadInterface!=null) {
//					d.addFileDownloadDelegate(fileDownloadInterface);
//				}
//
//				d.startDownload();
//			} else {
//				if(fileDownloadInterface!=null) {
//					d.addFileDownloadDelegate(fileDownloadInterface);
//				}
//			}
//		} catch (Exception e) {
//			log("DOWNLOAD FILE EXCEPTION: " + e.toString());
//		}
//	}
	
//	public static void stopDownload(final String filename){
//		DownloadObject d = downloadObjectDict.get(filename);
//		
//		if (d!=null){
//			d.cancel(true);
//		}
//	}

	/* Resource */
	public static int getDrawableResourceID(String name) {
		try {
			return resources.getIdentifier(name, "drawable",
					Constants.PACKAGE_NAME);
		} catch (Exception e) {
			return 0;
		}
	}

	public static int getStringResourceID(String name) {
		try {
			return resources.getIdentifier(name, "string",
					Constants.PACKAGE_NAME);
		} catch (Exception e) {
			return 0;
		}
	}

	public static int getRawResourceID(String name) {
		try {
			return resources.getIdentifier(name, "raw", Constants.PACKAGE_NAME);
		} catch (Exception e) {
			return 0;
		}
	}

	/* helper methods */
	public static String getUUIDFileName(String fileExtension) {
		return getUUID() + "." + fileExtension;
	}
	
	public static String getUUIDNoDashShortFileName(int length, String fileExtension) {
		
		if (length>30){
			length = 30;
		}
		
		return getUUID().replace("-", "").substring(0, length)+ "." + fileExtension;
	}

	public static int getTimeZoneOffset() {
		return TimeZone.getDefault().getOffset(0) / 1000;
	}

	public static boolean isNeedPreDownloadMedia(String type) {
		if(type.equals(Constants.MESSAGE_TYPE_AUDIO)) {
			return true;
		} else {
			return false;
		}
	}

	public static String getUUID() {
		return UUID.randomUUID().toString();
	}

	public static int getCurrentTimestamp() {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		return (int) (calendar.getTimeInMillis() / 1000L);
	}

	// file related
	public static boolean fileExist(String filePath) {
		return new File(filePath).exists();
	}

	public static long getFileSize(String filePath) {
		return new File(filePath).length();
	}

	public static void copyFile(File sourceFile, File destFile) {
		FileChannel source = null;
		FileChannel destination = null;

		File folder = new File(Singleton.getExternalMediaFolderPath());
		if (!folder.exists()){
			Boolean success = folder.mkdirs();
			Log.e("test", "create folder success");
		}
		
		try {
			if (!destFile.exists()) {
				destFile.createNewFile();
			}

			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());

			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		} catch (Exception e) {
			destFile.delete();
			log("COPY FILE EXCEPTION: " + e.toString());
		}
	}

	/* Helpers */
//	public static boolean loginCompleted() {
//		int myUserID = preferences.getInt(Constants.USER_ID, 0);
//
//		if (myUserID == 0) {
//			return false;
//		} else {
//			return true;
//		}
//	}

	public static String myUserID() {
		String userObjectId = ParseUser.getCurrentUser().getObjectId();
		if (userObjectId == null) {
			userObjectId = "";
		}
		// log("getMyUserID: "+ userObjectId);
		return userObjectId;
	}

	public static String myUserName(){
		String userName = ParseUser.getCurrentUser().getUsername();
		if (userName==null){
			userName = "";
		}
		return userName;
	}

	public static String myGender() {
		return preferences.getString(Constants.GENDER, "");
	}

	public static ArrayList<String> jsonToStringArrayList(String jsonString) {
		ArrayList<String> array = new ArrayList<String>();

		try {
			JSONArray jsonObjectArray = new JSONArray(jsonString);

			for (int i = 0; i < jsonObjectArray.length(); i++) {
				array.add(jsonObjectArray.getString(i));
			}
		} catch (Exception e) {
			log("JSON TO STRING ARRAY EXCEPTION: " + e.toString());
		}

		return array;
	}

	public static ArrayList<Integer> jsonToIntegerArrayList(String jsonString) {
		ArrayList<Integer> array = new ArrayList<Integer>();

		try {
			JSONArray jsonObjectArray = new JSONArray(jsonString);

			for (int i = 0; i < jsonObjectArray.length(); i++) {
				array.add(new Integer(jsonObjectArray.getInt(i)));
			}
		} catch (Exception e) {
			log("JSON TO STRING ARRAY EXCEPTION: " + e.toString());
		}

		return array;
	}

	public static HashSet<Integer> jsonToIntegerHashSet(String jsonString) {
		log("hashSet: " + jsonString);

		HashSet<Integer> hashSet = new HashSet<Integer>();
		try {
			JSONArray jsonObjectArray = new JSONArray(jsonString);

			for (int i = 0; i < jsonObjectArray.length(); i++) {
				hashSet.add(new Integer(jsonObjectArray.getInt(i)));
			}
			log("hashSet size:" + hashSet.size());
		} catch (Exception e) {
			log("JSON TO INTEGER HASH SET EXCEPTION: " + e.toString());
		}

		return hashSet;

	}

	public static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return Pattern
					.compile(
							"[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
									+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "("
									+ "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}"
									+ ")+").matcher(target).matches();
		}
	}

	// public static UserObject getUserObject(int userID) {
	//
	// if(userDict.containsKey(userID)) {
	// return userDict.get(userID);
	// }
	//
	// Cursor cursor = dbManager.sendQuery("SELECT * FROM User WHERE userID=?",
	// new String[]{Integer.toString(userID)});
	//
	// if(cursor.getCount()==0) {
	// return null;
	// }
	//
	// cursor.moveToFirst();
	//
	// UserObject userObject = cursorToUserObject(cursor);
	// cursor.close();
	//
	// return userObject;
	// }

	/*
	 * public static ArrayList<UserObject>
	 * getUserObjectsFromIDs(ArrayList<Integer> userIDs) { if(userIDs.size()==0)
	 * { return new ArrayList<UserObject>(); }
	 * 
	 * String separatedUserIDs = "";
	 * 
	 * for(Integer userID : userIDs) { separatedUserIDs = separatedUserIDs +
	 * userID + ","; }
	 * 
	 * separatedUserIDs = separatedUserIDs.substring(0,
	 * separatedUserIDs.length()-1);
	 * 
	 * Cursor cursor = database.rawQuery("SELECT * FROM user WHERE user_id IN ("
	 * + separatedUserIDs + ")", null);
	 * 
	 * ArrayList<UserObject> userObjects = new ArrayList<UserObject>();
	 * 
	 * int count = cursor.getCount();
	 * 
	 * cursor.moveToFirst();
	 * 
	 * if(count>0) { while(cursor.isAfterLast()==false) {
	 * userObjects.add(cursorToUserObject(cursor)); cursor.moveToNext(); } }
	 * 
	 * cursor.close();
	 * 
	 * return userObjects; }
	 */

	// public static UserObject cursorToUserObject(Cursor cursor) {
	// UserObject userObject = new UserObject();
	//
	// int userID = cursor.getInt(cursor.getColumnIndex("userID"));
	//
	// userObject.userID = userID;
	// userObject.isHidden = cursor.getInt(cursor.getColumnIndex("isHidden"));
	// userObject.vipExpiration =
	// cursor.getInt(cursor.getColumnIndex("vipExpiration"));
	// userObject.nickname =
	// cursor.getString(cursor.getColumnIndex("nickname"));
	// userObject.picture = cursor.getString(cursor.getColumnIndex("picture"));
	// userObject.age = cursor.getInt(cursor.getColumnIndex("age"));
	// userObject.gender = cursor.getString(cursor.getColumnIndex("gender"));
	// userObject.constellation =
	// cursor.getString(cursor.getColumnIndex("constellation"));
	// userObject.height = cursor.getInt(cursor.getColumnIndex("height"));
	// userObject.weight = cursor.getInt(cursor.getColumnIndex("weight"));
	// userObject.signature =
	// cursor.getString(cursor.getColumnIndex("signature"));
	// userObject.interest =
	// cursor.getString(cursor.getColumnIndex("interest"));
	// userObject.job = cursor.getString(cursor.getColumnIndex("job"));
	// userObject.bodyType =
	// cursor.getString(cursor.getColumnIndex("bodyType"));
	// userObject.relationshipStatus =
	// cursor.getString(cursor.getColumnIndex("relationshipStatus"));
	// userObject.lookingFor =
	// cursor.getString(cursor.getColumnIndex("lookingFor"));
	// userObject.latitude = cursor.getInt(cursor.getColumnIndex("latitude"));
	// userObject.longitude = cursor.getInt(cursor.getColumnIndex("longitude"));
	// userObject.visitCount =
	// cursor.getInt(cursor.getColumnIndex("visitCount"));
	// userObject.lastLogin = cursor.getInt(cursor.getColumnIndex("lastLogin"));
	// userObject.bid = cursor.getInt(cursor.getColumnIndex("bid"));
	//
	// userDict.put(userID, userObject);
	//
	// return userObject;
	// }

	public static boolean isLocationAvailable() {
		float myLatitude = preferences.getFloat(Constants.LATITUDE, 0.0f);
		float myLongitude = preferences.getFloat(Constants.LONGITUDE, 0.0f);

		if (myLatitude == 0.0f && myLongitude == 0.0f) {
			return false;
		} else {
			return true;
		}
	}

	public static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	/*
	 * public static String getDisplayedMessage(int messageType, String
	 * messageContent) {
	 * 
	 * if(messageType==Constants.MESSAGE_TYPE_TEXT) { return messageContent; }
	 * 
	 * if(messageType==Constants.MESSAGE_TYPE_IMAGE) { return
	 * resources.getString(R.string.image); }
	 * 
	 * if(messageType==Constants.MESSAGE_TYPE_AUDIO) { return
	 * resources.getString(R.string.audio); }
	 * 
	 * if(messageType==Constants.MESSAGE_TYPE_LOCATION) { return
	 * resources.getString(R.string.location); }
	 * 
	 * if(messageType==Constants.MESSAGE_TYPE_STICKER) { return
	 * resources.getString(R.string.sticker); }
	 * 
	 * if(messageType==Constants.MESSAGE_TYPE_GIFT) { return
	 * resources.getString(R.string.gift); }
	 * 
	 * if(messageType==Constants.MESSAGE_TYPE_FOOD) { return
	 * resources.getString(R.string.gift); }
	 * 
	 * if(messageType==Constants.MESSAGE_TYPE_CALL) { return
	 * resources.getString(R.string.call); }
	 * 
	 * return resources.getString(R.string.unknown); }
	 */

	public static int inverseInt(int input) {
		if (input == 0) {
			return 1;
		} else {
			return 0;
		}
	}

	public static void logoutAction(Context context) {

		// Parse logout
		ParseUser.logOut();
		log("user:" + ParseUser.getCurrentUser().getUsername());

		// Clean installation
		ParseInstallation.getCurrentInstallation();
		ParseInstallation installation = ParseInstallation.getCurrentInstallation();
		installation.put("userobjectid", "");
		installation.put("username", "");
		installation.saveInBackground();

		// cancel all notification
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();

		// clean all file cache
		new File(Singleton.getExternalMediaFolderPath()).delete();

		//delete chat db
		//DatabaseManager.dropDatabase();

		//delete news db
		//DBOpenHelper.dropDatabase();
		
		preferenceEditor.clear();
		preferenceEditor.commit();
		
	}

	public static String getLastGalleryPictureFilePath(Activity activity) {
		String[] projection = new String[] {
				MediaStore.Images.ImageColumns._ID,
				MediaStore.Images.ImageColumns.DATA,
				MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
				MediaStore.Images.ImageColumns.DATE_TAKEN,
				MediaStore.Images.ImageColumns.MIME_TYPE };

		String selection = MediaStore.Images.Media.DATE_TAKEN + "<"
				+ String.valueOf((long) getCurrentTimestamp() * 1000L + 10000L);

		final Cursor cursor = activity.managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
				selection, null, MediaStore.Images.ImageColumns.DATE_TAKEN
						+ " DESC");

		if (cursor.moveToFirst()) {
			String imageLocation = cursor.getString(1);

			return imageLocation;
		}

		return null;
	}

	public static ArrayList<String> getAllPhoneNumbers() {
		ArrayList<String> phoneNumberArray = new ArrayList<String>(); // an
																		// Array
																		// of
																		// [name,
																		// phone
																		// number]

		ContentResolver contentResolver = applicationContext
				.getContentResolver();
		Cursor contactCursor = contentResolver.query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		contactCursor.moveToFirst();

		for (int i = 0; i < contactCursor.getCount(); i++) {
			String contactID = contactCursor.getString(contactCursor
					.getColumnIndex(ContactsContract.Contacts._ID));

			// get the phone number
			Cursor phoneNumberCursor = contentResolver.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
					new String[] { contactID }, null);
			phoneNumberCursor.moveToFirst();

			for (int j = 0; j < phoneNumberCursor.getCount(); j++) {
				String phoneNumber = phoneNumberCursor
						.getString(phoneNumberCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				phoneNumberArray.add(phoneNumber);
			}

			phoneNumberCursor.close();

			contactCursor.moveToNext();
		}

		contactCursor.close();

		return phoneNumberArray;
	}

	public static void playSound(int soundResourceID) {
		// SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,
		// 0);
		// int soundID = soundPool.load(applicationContext, soundResourceID, 1);
		// AudioManager audioManager = (AudioManager)
		// applicationContext.getSystemService(Context.AUDIO_SERVICE);
		// float volume = (float)
		// audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
		// soundPool.play(soundID, volume, volume, 1, 0, 1f);

		final MediaPlayer player = MediaPlayer.create(applicationContext,
				soundResourceID);

		player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				player.release();
			}
		});

		player.start();
	}

	public static ArrayList<String> getEmailCandidates(String emailAccountName) {
		ArrayList<String> emailAccounts = new ArrayList<String>();

		Pattern emailPattern = Patterns.EMAIL_ADDRESS;
		Account[] accounts = AccountManager.get(applicationContext)
				.getAccounts();

		for (Account account : accounts) {
			if (emailPattern.matcher(account.name).matches()) {
				String possibleEmail = account.name;

				if (!emailAccounts.contains(possibleEmail)
						&& possibleEmail.contains(emailAccountName)) {
					emailAccounts.add(possibleEmail);
				}
			}
		}

		return emailAccounts;
	}

	public static void killProcess() {
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	// public static String getWifiMacAddress() {
	// try {
	// WifiManager wifiMan = (WifiManager)
	// applicationContext.getSystemService(Context.WIFI_SERVICE);
	// WifiInfo wifiInf = wifiMan.getConnectionInfo();
	// String macAddress = wifiInf.getMacAddress();
	//
	// if(macAddress==null || macAddress.isEmpty()) {
	// return "";
	// }
	//
	// return wifiInf.getMacAddress();
	// } catch (Exception e) {
	// return "";
	// }
	// }

	private static boolean isAppInstalled(String packageName) {
		PackageManager pm = applicationContext.getPackageManager();
		boolean installed = false;
		try {
			pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			installed = false;
		}
		return installed;
	}

	public static long dirSize(File dir) {
		if (dir.exists()) {
			long result = 0;
			File[] fileList = dir.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				// Recursive call if it's a directory
				if (fileList[i].isDirectory()) {
					result += dirSize(fileList[i]);
				} else {
					// Sum the file size in bytes
					result += fileList[i].length();
				}
			}
			return result; // return the file size
		}

		return 0;
	}

	public static void cleanupFileCache() {
		new Thread(new Runnable() {
			public void run() {
				long size = Singleton.dirSize(new File(Singleton
						.getExternalMediaFolderPath()));

				if (size > Constants.MAX_FILE_CACHE_SIZE) {
					File rootFolder = new File(
							Singleton.getExternalMediaFolderPath());

					File[] files = rootFolder.listFiles();

					for (File file : files) {
						String filePath = file.getAbsolutePath();

						if (filePath.endsWith(".jpg")
								|| filePath.endsWith(".mp4")
								|| filePath.endsWith(".aac")) {
							file.delete();
						}
					}
				}
			}
		}).start();
	}

	public static int getNetworkConn(Context context) {
		ConnectivityManager conMan = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo.State wifiState = conMan.getNetworkInfo(
				ConnectivityManager.TYPE_WIFI).getState();
		NetworkInfo.State mobileNetworkState = conMan.getNetworkInfo(
				ConnectivityManager.TYPE_MOBILE).getState();

		if (wifiState == NetworkInfo.State.CONNECTED
				|| wifiState == NetworkInfo.State.CONNECTING) {
			return Constants.NETWORK_WIFI;
		} else if (mobileNetworkState == NetworkInfo.State.CONNECTED
				|| mobileNetworkState == NetworkInfo.State.CONNECTING) {
			return Constants.NETWORK_3G;
		}

		return Constants.NETWORK_NONE;
	}

	public static String loadAssetFile(Context context, String fileName) {
		String tContents = "";

		try {
			InputStream stream = context.getAssets().open(fileName);

			int size = stream.available();
			byte[] buffer = new byte[size];
			stream.read(buffer);
			stream.close();
			tContents = new String(buffer);
		} catch (IOException e) {
			// Handle exceptions here
		}

		return tContents;
	}

	// public static boolean isVideoCallAvailable() {
	// return Camera.getNumberOfCameras()>2;
	// }

	public static String getIPAddress() {
		try {
			List<NetworkInterface> interfaces = Collections
					.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf
						.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress().toUpperCase();
						boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);

						if (isIPv4) {
							return sAddr;
						}
					}
				}
			}
		} catch (Exception ex) {
		} // for now eat exceptions

		return "";
	}

	/* Emoji */
	// public static SpannableStringBuilder parseEmojiText(String s, int
	// inSampleSize) {
	// SpannableStringBuilder spannableStringBuilder = new
	// SpannableStringBuilder(s);
	// int size = (int)Math.ceil(2/SCREEN_DENSITY);
	//
	// int stringLength = s.length();
	//
	// // find target emoji
	// for(int i=0;i<stringLength-1;i++) {
	// String candidate = s.substring(i, i+2);
	//
	// if(Constants.EMOJI_SET.contains(candidate)) {
	// // emoji found
	// String imageCacheID = Constants.EMOJI_FILE_PREFIX +
	// Constants.EMOJI_DICTIONARY.get(candidate);
	// Bitmap emojiBitmap = Singleton.getCachedBitmap(imageCacheID);
	//
	// if(emojiBitmap==null) {
	// int emojiResourceID = getDrawableResourceID(Constants.EMOJI_FILE_PREFIX +
	// Constants.EMOJI_DICTIONARY.get(candidate));
	// emojiBitmap = BitmapFactory.decodeResource(resources, emojiResourceID,
	// BitmapHelper.getBitmapOptions(size));
	// Singleton.putCachedBitmap(imageCacheID, emojiBitmap);
	// }
	//
	// ImageSpan imageSpan = new ImageSpan(emojiBitmap);
	// spannableStringBuilder.setSpan(imageSpan, i, i+2,
	// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	//
	// i++; // skip
	// }
	// }
	//
	// return spannableStringBuilder;
	// }
	//
	//
	// public static UserObject getSelfUserObject() {
	// UserObject userObject = new UserObject();
	//
	// userObject.userID = myUserID();
	// userObject.picture = preferences.getString(Constants.PICTURE, "");
	// userObject.gender = myGender();
	// userObject.age = preferences.getInt(Constants.AGE, 18);
	// userObject.nickname = preferences.getString(Constants.NICKNAME, "");
	//
	// return userObject;
	// }

	public static void generateAppUUID() {
		String filePath = getExternalMediaFolderPath() + "uuid.txt";

		if (!fileExist(filePath)) {
			String appUUID = Secure.getString(
					applicationContext.getContentResolver(), Secure.ANDROID_ID);

			if (appUUID == null || appUUID.equals("")) {
				appUUID = getUUID();
			}

			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(filePath));
				writer.write(appUUID);

			} catch (IOException e) {

			} finally {
				try {
					if (writer != null) {
						writer.close();
					}
				} catch (IOException e) {

				}
			}
		}
	}

	public static String getAppUUID() {
		String filePath = getExternalMediaFolderPath() + "uuid.txt";

		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));

			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}

			br.close();

			return sb.toString().trim();
		} catch (Exception e) {
			return "";
		}
	}
	
	public static String readExternalFile(String filename){
		String filePath = getExternalMediaFolderPath() + "/" + filename;

		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));

			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}

			br.close();

			return sb.toString().trim();
		} catch (Exception e) {
			return "";
		}
	}

	public static boolean isPad() {
		if (((float) Singleton.SCREEN_WIDTH / Singleton.SCREEN_DENSITY) > 400.0f) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean packageExist(String targetPackage) {
		List<ApplicationInfo> packages;
		PackageManager pm;
		pm = applicationContext.getPackageManager();
		packages = pm.getInstalledApplications(0);

		for (ApplicationInfo packageInfo : packages) {
			if (packageInfo.packageName.equals(targetPackage))
				return true;
		}

		return false;
	}

	public static String getVersion() {
		try {
			return applicationContext.getPackageManager().getPackageInfo(
					applicationContext.getPackageName(), 0).versionName;
		} catch (Exception e) {
			return "1.0";
		}
	}

	// FreeTV
	private static String convertToHex(byte[] data) {
		StringBuilder buf = new StringBuilder();
		for (byte b : data) {
			int halfbyte = (b >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte)
						: (char) ('a' + (halfbyte - 10)));
				halfbyte = b & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	public static String SHA1(String text) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		byte[] sha1hash = md.digest();
		return convertToHex(sha1hash);
	}

	public static String fileNameFromUrl(String url) {
		try {
			return SHA1(url) + ".jpg";
		} catch (Exception e) {
			return "";
		}
	}

	public static boolean isTablet() {
		return (applicationContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	
	/* SellHouse: File */
	public static void saveBitmap(Bitmap bitmap, String filePath, Bitmap.CompressFormat format) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath));
            bitmap.compress(format, 85, fileOutputStream);
        } catch (Exception e) {
            Singleton.log("SAVE BITMAP EXCEPTION: " + e.toString());
        }
    }

	public static String retrieveDpiTypeOfDevice() {
//		float density = applicationContext.getResources().getDisplayMetrics().density;

		String result = "null";
		try {
			if(null != applicationContext) {
				switch (applicationContext.getResources().getDisplayMetrics().densityDpi) {
				case DisplayMetrics.DENSITY_LOW:
					result = "LDPI";
					break;
				case DisplayMetrics.DENSITY_MEDIUM:
					result = "MDPI";
					break;
				case DisplayMetrics.DENSITY_HIGH:
					result = "HDPI";
					break;
				case DisplayMetrics.DENSITY_XHIGH:
					result = "XHDPI";
					break;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static WebView getWebView() {
		return webView;
	}

}