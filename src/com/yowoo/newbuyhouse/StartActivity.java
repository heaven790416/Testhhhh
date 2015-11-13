package com.yowoo.newbuyhouse;

import io.fabric.sdk.android.Fabric;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.store.StoreSearchInfo;
import com.yowoo.newbuyhouse.util.FileUtils;

public class StartActivity extends Activity {

	@Override
	public void onStart() {
	    super.onStart();
	    GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}
	 
	@Override
	public void onStop() {
	    super.onStop();
	    GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Fabric.with(this, new Crashlytics());

		setContentView(R.layout.start);

		Singleton.getInstance(this);

//		//preload city and mrt data to searchInfo
//		String cityData = FileUtils.readAssetsFile(this, "city.json");
//		String mrtData = FileUtils.readAssetsFile(this, "mrt.json");
//
//		//initialize search filter options
//		SearchInfo.initialize(this);
//		StoreSearchInfo.initialize(this);
//		try {
//			JSONObject responseObject = new JSONObject(cityData);
//			SearchInfo.getInstance().initCityData(responseObject.getJSONObject("OPT").getJSONArray("citys"));
//
//			responseObject = new JSONObject(mrtData);
//			SearchInfo.getInstance().initMRTData(responseObject.getJSONObject("OPT"));
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}

		jumpToMainActivity();

	}

	public void jumpToMainActivity() {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setClass(StartActivity.this, MainActivity.class);

		startActivity(intent);
		finish();
	}


	@Override
	protected void onResume() {
		super.onResume();

		//FB: Logs 'install' and 'app activate' App Events.
		AppEventsLogger.activateApp(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Logs 'app deactivate' App Event.
		AppEventsLogger.deactivateApp(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		System.gc();
	}

}
