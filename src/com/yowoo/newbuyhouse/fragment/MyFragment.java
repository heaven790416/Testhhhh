package com.yowoo.newbuyhouse.fragment;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.BaseActivity;
import com.yowoo.newbuyhouse.BaseActivity.OkOnClickListener;
import com.yowoo.newbuyhouse.Singleton;

public class MyFragment extends Fragment{

	
	/* helper method */
	protected void showActivityToast(int stringResId){
		if (getActivity()==null)	 return;
		
		try{
			((BaseActivity)getActivity()).showToast(getString(stringResId));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void showActivityImageToast(String toastString, int resId){
		if (getActivity()==null)	 return;
		
		try{
			((BaseActivity)getActivity()).showImageToast(toastString, resId);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void showActivityProgressDialog(){
		if (getActivity()==null)	 return;
		
		try{
			((BaseActivity)getActivity()).showProgressDialog();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void hideActivityProgressDialog(){
		if (getActivity()==null)	 return;
		
		try{
			((BaseActivity)getActivity()).hideProgressDialog();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/* Map */
	public LatLng getMyCurrentLocation(){

		//1.先抓取當前網路位置
		LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

		//1.1 抓取GPS位置
		Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (lastKnownLocation!=null){
			Log.e("test", "GPS lastKnownLocation: "+ lastKnownLocation.getLatitude()+","+lastKnownLocation.getLongitude());
			Singleton.preferenceEditor
			.putFloat(BHConstants.LATEST_LOCATION_LAT, (float)lastKnownLocation.getLatitude())
			.putFloat(BHConstants.LATEST_LOCATION_LNG, (float)lastKnownLocation.getLongitude())
			.commit();
			return new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
		}
		Log.e("test", "GPS lastKnownLocation==null");

		//1.2 抓取網路位置
		lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (lastKnownLocation!=null){
			Log.e("test", "Network lastKnownLocation: "+ lastKnownLocation.getLatitude()+","+lastKnownLocation.getLongitude());
			Singleton.preferenceEditor
			.putFloat(BHConstants.LATEST_LOCATION_LAT, (float)lastKnownLocation.getLatitude())
			.putFloat(BHConstants.LATEST_LOCATION_LNG, (float)lastKnownLocation.getLongitude())
			.commit();
			return new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
		}
		Log.e("test", "Network lastKnownLocation==null");

		this.showActivityToast(R.string.no_gps_location_please_check);

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
	
	/* GPS */
	public void checkAndShowGPSDialog(){
		try{
			if (!Singleton.needRemindOpenGPS) return;
			
			//check if gps is opened
			LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
			Boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
			
			if (!isGPSEnabled){
				//沒開gps
				Singleton.needRemindOpenGPS = false;
				this.showActivityYesNoDialog(getString(R.string.remind), getString(R.string.no_gps_ask_open_gps), new OkOnClickListener(){

					@Override
					public void didClickOk() {
						// TODO Auto-generated method stub
						startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}

					@Override
					public void didClickCancel() {
						// TODO Auto-generated method stub

					}
				});
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void showActivityYesNoDialog(String title, String message, final OkOnClickListener listener){
		if (getActivity()==null)	 return;
		
		try{
			((BaseActivity)getActivity()).showYesNoDialog(title, message, listener);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/* animation */
//	protected void slideInToStartActivity(Intent intent){
//		startActivity(intent);
//		if (getActivity()!=null){
//			getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.hold);
//		}
//	}
}
