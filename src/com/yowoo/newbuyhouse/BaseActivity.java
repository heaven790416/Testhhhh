package com.yowoo.newbuyhouse;

import java.io.File;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.thinkermobile.sinyi.R;

public class BaseActivity extends ActionBarActivity{

	protected Singleton globalSingleton = null;
	
	protected LayoutInflater inflater = null;
	protected Resources resources = null;
	protected LocalBroadcastManager broadcastManager = null;
	protected ProgressDialog progressDialog = null;
	protected InputMethodManager inputMethodManager = null;
	//protected ActionBar actionBar;
	protected FragmentManager fragmentManager = null;
	
	protected Handler handler = new Handler();
	Toolbar toolbar;
	
	public final String TAG = getClass().getName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		inflater = LayoutInflater.from(this);
		resources = getResources();
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		broadcastManager = LocalBroadcastManager.getInstance(this);
		fragmentManager = getSupportFragmentManager();
		
		globalSingleton = Singleton.getInstance(getApplicationContext());
		
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		globalSingleton = Singleton.getInstance(getApplicationContext());
//		Singleton.log("SCREEN_DPI: " + Singleton.retrieveDpiTypeOfDevice());

	}
	
	@Override
	protected void onDestroy() {
		if(progressDialog!=null) {
			try {
				progressDialog.dismiss();
				progressDialog = null;
			} catch (Exception e) {

			}
		}

		super.onDestroy();
	}
	
	
	/* Helper Methods */
	public boolean isShowingProgressDialog() {
		return !(progressDialog==null);
	}

	public void showProgressDialog() {
		try {
			if(progressDialog==null) {
				progressDialog = ProgressDialog.show(this, "", resources.getString(R.string.processing));
			}

			progressDialog.setCancelable(true);
		} catch (Exception e) {

		}
	}

	public void showProgressDialog(OnCancelListener cancelListener) {
		try {
			if(progressDialog==null) {
				progressDialog = ProgressDialog.show(this, "", resources.getString(R.string.processing));
			}

			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(cancelListener);
		} catch(Exception e) {

		}
	}

	public void hideProgressDialog() {
		if(progressDialog!=null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	/* Keyboard */
	public void hideKeyboard() {
		ViewGroup view = (ViewGroup) getWindow().getDecorView();
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public void hideKeyboard(long delayMillis) {
		handler.postDelayed(new Runnable() {
			public void run() {
				hideKeyboard();
			}
		}, delayMillis);
	}

	public void showKeyboard() {
		inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	public void showKeyboard(long delayMillis) {
		handler.postDelayed(new Runnable() {
			public void run() {
				showKeyboard();
			}
		}, delayMillis);
	}
	
	public void showToast(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	public void showToast(int resId){
		Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
	}
	
	public void showShortToast(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	public void showNetworkUnstableToast() {
		showImageToast(getString(R.string.network_not_stable), R.drawable.sad);
	}
	
	public void showImageToast(String text, int imageResourceID) {
		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout container = (RelativeLayout) inflater.inflate(R.layout.custom_toast, null);
		TextView toastTextView = (TextView) container.findViewById(R.id.toastTextView);
		ImageView toastImageView = (ImageView) container.findViewById(R.id.toastImageView);

		toastTextView.setText(text);
		toastImageView.setImageResource(imageResourceID);

		final Toast toast = new Toast(this);
		toast.setGravity(Gravity.CENTER, 0, Singleton.dpToPixel(-60));
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(container);
		toast.show();

		handler.postDelayed(new Runnable() {
			public void run() {
				toast.cancel();
			}
		}, 1250);
	}
	
	public interface PriceOkOnClickListener {
		public void didClickOk(int lowPrice, int highPrice);
		public void didClickCancel();
	}
	
	public void showPriceRangeDialog(String okText, String cancelText, int lowPrice, int highPrice, final PriceOkOnClickListener listener) {
		try {
			Log.e("test", "price!");
			final Dialog dialog = new Dialog(this, R.style.MyDialog);
			dialog.setCancelable(false);
			dialog.setContentView(R.layout.show_price_range_dialog);

			RelativeLayout dialogContainer = (RelativeLayout) dialog.findViewById(R.id.dialogContainer);
			Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
			Button okButton = (Button) dialog.findViewById(R.id.okButton);
			final EditText editText1 = (EditText) dialog.findViewById(R.id.editText1);
			final EditText editText2 = (EditText) dialog.findViewById(R.id.editText2);
			
			dialogContainer.getLayoutParams().width = Singleton.SCREEN_WIDTH * 8 / 10;

			editText1.setText(String.valueOf(lowPrice));
			if (highPrice>=BHConstants.MAX_PRICE){
				editText2.setText("");
			}else{
				editText2.setText(String.valueOf(highPrice));
			}
			okButton.setText(okText);
			cancelButton.setText(cancelText);
			
			cancelButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
					
					if (listener!=null){
						listener.didClickCancel();
					}
				}
			});

			okButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					//check price
					int lowPrice = Integer.valueOf(editText1.getText().toString()); 
					String higPriceText = editText2.getText().toString() ;
					int highPrice = (higPriceText.equals(""))? 0:Integer.valueOf(higPriceText);
					lowPrice = (lowPrice<0)? 0 : lowPrice;
					highPrice = (highPrice<=lowPrice)? BHConstants.MAX_PRICE : highPrice;
					
					dialog.dismiss();
					if(listener!=null) {
						listener.didClickOk(lowPrice, highPrice);
					}
				}
			});

			dialog.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					dialog.dismiss();
				}
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public interface OkOnClickListener {
		public void didClickOk();
		public void didClickCancel();
	}
	
	public void showYesNoDialog(String title, String message, final OkOnClickListener listener) {
		try {
			final Dialog dialog = new Dialog(this, R.style.MyDialog);
			dialog.setCancelable(false);
			dialog.setContentView(R.layout.show_dialog);

			RelativeLayout rootView = (RelativeLayout) dialog.findViewById(R.id.rootView);
			Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
			Button okButton = (Button) dialog.findViewById(R.id.okButton);
			TextView titleTextView = (TextView) dialog.findViewById(R.id.titleTextView);
			TextView messageTextView = (TextView) dialog.findViewById(R.id.messageTextView);

			rootView.getLayoutParams().width = Singleton.SCREEN_WIDTH * 8 / 10;

			cancelButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
					
					if (listener!=null){
						listener.didClickCancel();
					}
				}
			});

			okButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();

					if(listener!=null) {
						listener.didClickOk();
					}
				}
			});

			titleTextView.setText(title);
			messageTextView.setText(message);

			dialog.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					dialog.dismiss();
				}
			});

			dialog.show();
		} catch (Exception e) {

		}
	}
	
	public void showYesNoDialog_TrackCollect_Login(String title, String message, String status, final OkOnClickListener listener) {
		try {
			final Dialog dialog = new Dialog(this, R.style.MyDialog);
			dialog.setCancelable(false);
			dialog.setContentView(R.layout.show_dialog_track_collect_login);

			RelativeLayout rootView = (RelativeLayout) dialog.findViewById(R.id.rootView);
			Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
			Button okButton = (Button) dialog.findViewById(R.id.okButton);
			TextView titleTextView = (TextView) dialog.findViewById(R.id.titleTextView);
			TextView messageTextView = (TextView) dialog.findViewById(R.id.messageTextView);

			rootView.getLayoutParams().width = Singleton.SCREEN_WIDTH * 8 / 10;
			
			if(status.equals("success")){
				okButton.setText("是，合併");
			}
			else if(status.equals("confirm")){
				okButton.setText("確認刪除");
				okButton.setBackgroundResource(R.drawable.btn_dialog_delete);
				cancelButton.setText("取消刪除");
			}

			cancelButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
					
					if (listener!=null){
						listener.didClickCancel();
					}
				}
			});

			okButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();

					if(listener!=null) {
						listener.didClickOk();
					}
				}
			});

			titleTextView.setText(title);
			messageTextView.setText(message);

			dialog.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					dialog.dismiss();
				}
			});

			dialog.show();
		} catch (Exception e) {

		}
	}
//	
//	public interface HandyOnClickListener {
//		public void didClickOnItem(int index);
//	}
//	
//	public void showPickerDialog(final List<String> items, final HandyOnClickListener onClickListener) {
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//		CharSequence[] itemTitles = items.toArray(new CharSequence[items.size()]);
//
//		builder.setItems(itemTitles, new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//				onClickListener.didClickOnItem(which);
//			}
//		});
//
//		AlertDialog dialog = builder.create();
//
//		dialog.setOnCancelListener(new OnCancelListener() {
//			@Override
//			public void onCancel(DialogInterface dialog) {
//				dialog.dismiss();
//			}
//		});
//
//		dialog.show();
//	}
	
	/* Media Action */
	public void startPickFileIntent(int mediaAction, int fileType) {
		//new File(Constants.INTERNAL_TEMP_IMAGE_PATH).delete();
		Intent intent = new Intent();
		
		Uri uri = null;
		if (fileType==Constants.FILE_TYPE_ONLY_IMAGE){
			intent.setType("image/*");
			uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		}
		
		if (fileType==Constants.FILE_TYPE_ONLY_VIDEO){
			intent.setType("video/*");
			uri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
		}

		if (Build.VERSION.SDK_INT < 19 || uri == null){
			//小於API Level 19，沒有新的Storage Access Framework，
			//可以使用GET_CONTENT，讓使用者可以選擇
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, mediaAction);
		} else {
			//大於API Level 19，為了躲過新的Storage Access Framework
			//直接指定打開內建相簿
			intent = new Intent(Intent.ACTION_PICK, uri);
			startActivityForResult(intent, mediaAction);			
		}
		
		// �쥻���{���X
		// intent.setAction(Intent.ACTION_GET_CONTENT);
		// startActivityForResult(intent, mediaAction);
	}
	
	/*for take picture */
	protected String mCurrentPhotoPath;

	public void startTakePictureIntent() {
		//���w�s��tmp.jpg�A���᧹����A�A�qtmp.jpg���X
		new File(Singleton.getExternalTempImagePath()).delete();
		mCurrentPhotoPath = Singleton.getExternalTempImagePath();
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Singleton.getExternalTempImagePath())));
		startActivityForResult(intent, Constants.MEDIA_ACTION_TAKE_PICTURE);
	}
	
	public void startTakeVideoIntent() {
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		startActivityForResult(intent, Constants.MEDIA_ACTION_TAKE_VIDEO);
	}

	public String getFilePathFromMediaIntent(Intent data){
		Uri contentUri = data.getData();
		Cursor cursor = getContentResolver().query(contentUri, null, null, null, null); 
		if (cursor!=null){
			//是手機多媒體相簿的多媒體
			try {
				cursor.moveToFirst();
				int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
				return cursor.getString(idx);  // 會產生 Exception
			} catch (Exception e) {
				return "";
			}
		}else{
			//是其他檔案軟體的多媒體
			return getFilePathFromFileIntent(data);
		}
	}
	
	public String getFilePathFromFileIntent(Intent data){
		Uri contentUri = data.getData();
		Singleton.log("getPath: "+contentUri.getPath());
		return contentUri.getPath();
	}
	
	/* BuyHouse */
	public LatLng getMyCurrentLocation(){
		//1.先抓取當前網路位置
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String locationProvider = LocationManager.NETWORK_PROVIDER;
		// Or use LocationManager.GPS_PROVIDER
		Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
		if (lastKnownLocation!=null){
			Log.e("test", "lastKnownLocation: "+ lastKnownLocation.getLatitude()+","+lastKnownLocation.getLongitude());
			return new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
		}
		Log.e("test", "lastKnownLocation==null");

		//2.嘗試抓取歷史位置
		float lat = Singleton.preferences.getFloat(BHConstants.LATEST_LOCATION_LAT, 0.0f);
		float lng = Singleton.preferences.getFloat(BHConstants.LATEST_LOCATION_LNG, 0.0f);

		if (lat>0 && lng>0){
			return new LatLng(lat, lng);
		}
		Log.e("test", "historyLocation==null");

		//3.前兩種位置都沒有，回傳default位置
		return new LatLng(BHConstants.DEFAULT_LATITUDE, BHConstants.DEFAULT_LONGITUDE);
	}
	
	protected void setToolbarView(String title){
		toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_selector);
        toolbar.setTitleTextColor(getResources().getColor(R.color.title_text_color));
        TextView mTitleTextView = (TextView)toolbar.findViewById(R.id.mTitleTextView);
        mTitleTextView.setText(title);
        mTitleTextView.setVisibility(View.VISIBLE);
        ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("");
	}
	
	/* Animation */
	protected void slideOutToFinish(){
		finish();
		//overridePendingTransition(R.anim.left_in, R.anim.slide_out_to_right);
	}
	
	public void slideInToStartActivity(Intent intent){
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
	}
	
	/* Contact Service */
	public void contactService(){
		Time t=new Time(); 
		t.setToNow(); // 取得系統時間。 
		int hour = t.hour; // 0-23 
		int minute = t.minute;
		
		if(hour>=10 && hour<18){  //客服營業時間
			Uri uri=Uri.parse("http://line.me/R/ti/p/~sinyihouse");
			Intent i=new Intent(Intent.ACTION_VIEW,uri);
			startActivity(i);
		}else{
			Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
					"mailto","sinyi@sinyi.com.tw", null));
			startActivity(Intent.createChooser(emailIntent, "寄信給客服..."));
		}
	}
	
}
