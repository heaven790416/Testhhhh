package com.yowoo.newbuyhouse.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yowoo.newbuyhouse.BHConstants;


public class City {
	
	public String cityId = "";
	public String cityName = "";
	public String cityCode = "";
	public ArrayList<Area> areas = new ArrayList<Area>();
	
	public float lat = 0.0f;
	public float lng = 0.0f;
	
	public City(String cityId, String cityName, String cityCode, Boolean addDefaultArea, float lat, float lng){
		this.cityId = cityId;
		this.cityName = cityName;
		this.cityCode = cityCode;
		this.lat = lat;
		this.lng = lng;
		
		//check if add default area
		if (addDefaultArea){
			areas.add(new Area("0", "不指定", 0, 0));
		}
	}
	
	//for json裡面沒有area的情況
	public City(JSONObject cityObject){
		parseCity(cityObject);
	}
	
	//for json裡面有area的情況
	public City(JSONObject cityObject, Boolean addDefaultArea){
		//parse city
		parseCity(cityObject);
		
		//parse area
		try {
			JSONArray areaJSONArray = cityObject.getJSONArray(BHConstants.JSON_KEY_AREAS);
			
			//check if add default area
			if (addDefaultArea){
				this.areas.add(new Area("0","不指定", 0 ,0));
			}
			
			for (int i=0; i<areaJSONArray.length(); i++){
				this.areas.add(new Area(areaJSONArray.getJSONObject(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		//parse location
		try {
			this.lat = (float) cityObject.getDouble(BHConstants.JSON_KEY_LAT);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			this.lng = (float) cityObject.getDouble(BHConstants.JSON_KEY_LNG);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	private void parseCity(JSONObject cityObject){
		try {
			this.cityId = cityObject.getString(BHConstants.JSON_KEY_CITY_ID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		try {
			this.cityName = cityObject.getString(BHConstants.JSON_KEY_CITY_NAME);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			this.cityCode = cityObject.getString(BHConstants.JSON_KEY_CITY_CODE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
