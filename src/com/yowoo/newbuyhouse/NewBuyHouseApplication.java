package com.yowoo.newbuyhouse;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import java.util.HashMap;

import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.login.LoginInfo;
import com.yowoo.newbuyhouse.price.PriceSearchInfo;
import com.yowoo.newbuyhouse.store.StoreSearchInfo;

public class NewBuyHouseApplication extends Application {
	// The following line should be changed to include the correct property id.
    private static final String PROPERTY_ID = "UA-68535379-1";
	public static GoogleAnalytics googleAnalytics;
	public static Tracker tracker;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("test", "NewBuyHouseApplication: onCreate");

		// initial SQLite Database
		//DatabaseManager.initialDatabaseManager();

		// initial LoginInfo
		LoginInfo.initialize(this);

		// Initialize the SDK before executing any other operations,
		// especially, if you're using Facebook UI elements.
		FacebookSdk.sdkInitialize(getApplicationContext());
	     
		//initialize search filter options
		Singleton.getInstance(this);
		SearchInfo.initialize(this);
		StoreSearchInfo.initialize(this);
		PriceSearchInfo.initialize(this);

		
	}
	
	
    public enum TrackerName {
        APP_TRACKER // Tracker used only in this app.
    }
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public synchronized Tracker getTracker(TrackerName trackerId) {
    	System.out.print("tracker"+trackerId);
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
                    : analytics.newTracker(R.xml.global_tracker);
            t.enableAdvertisingIdCollection(true);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }
  //google anslytics screenName
    public static void initTracker(Activity activity, String fragmentName) {
        Tracker t = ((NewBuyHouseApplication) activity.getApplication()).getTracker(
        		NewBuyHouseApplication.TrackerName.APP_TRACKER);
        t.setScreenName(activity.getClass().getSimpleName() + " - " + fragmentName);
        t.send(new HitBuilders.AppViewBuilder().build());
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }
	
}
