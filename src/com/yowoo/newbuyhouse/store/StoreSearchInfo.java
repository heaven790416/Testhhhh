package com.yowoo.newbuyhouse.store;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.SearchInfo;
import com.yowoo.newbuyhouse.model.Area;
import com.yowoo.newbuyhouse.model.City;


public class StoreSearchInfo {

	private static StoreSearchInfo searchInfo = null;

	public static SharedPreferences preferences;
	public static Editor preferenceEditor;
	
	public LatLng centerPos = new LatLng(BHConstants.DEFAULT_LATITUDE, BHConstants.DEFAULT_LONGITUDE);
	
	//for area mode
	public int selectedCity = 0;
	public int selectedArea = 0;
	
	
	public static void initialize(Context applicationContext){

		if (searchInfo == null) {
			synchronized (StoreSearchInfo.class) {
				if (searchInfo == null) {
					searchInfo = new StoreSearchInfo(applicationContext);
				}
			}
		}

	}

	public static StoreSearchInfo getInstance(){
		return searchInfo;
	}

	private StoreSearchInfo(Context applicationContext){
		preferences = PreferenceManager
				.getDefaultSharedPreferences(applicationContext);
		preferenceEditor = preferences.edit();
		
	}
	
	public LatLng getSelectedCityAreaLatLng(){

		if (selectedCity==0) 
			return new LatLng(BHConstants.DEFAULT_LATITUDE, BHConstants.DEFAULT_LONGITUDE);

		City city = SearchInfo.getInstance().cityList.get(this.selectedCity);
		if (selectedArea==0) return new LatLng(city.lat, city.lng);

		Area area = city.areas.get(this.selectedArea);
		return new LatLng(area.lat, area.lng);
	}
	
	public LatLng getLatLngByZipCode(String zipCode){

		Log.e("test", "zipCode:"+zipCode);
		ArrayList<City> cityList = SearchInfo.getInstance().cityList;
		for (int i=0; i<cityList.size(); i++){
			ArrayList<Area> areaList = cityList.get(i).areas;
			for (int j=0; j<areaList.size(); j++){
				Area area = areaList.get(j);
				Log.e("test", "test zipCode:"+area.name+":"+area.zipCode);
				
				if (area.zipCode.equals(zipCode)){
					return new LatLng(area.lat, area.lng);
				}
			}
		}
		
		Log.e("test", "no zipCode");
		
		return new LatLng(BHConstants.DEFAULT_LATITUDE, BHConstants.DEFAULT_LONGITUDE);

	}
	
	public String getFilterParams(Boolean addLocaionInfo, Boolean isListMode){
		String params = "";
		
		//區域相關
		//直接取得經緯度，不需處理
		
		return params;
	}
	
	/* reset search info */
	public void clearStatus(){
		selectedCity = 0;
		selectedArea = 0;
	}
	
}
