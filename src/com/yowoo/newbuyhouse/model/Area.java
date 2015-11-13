package com.yowoo.newbuyhouse.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.yowoo.newbuyhouse.BHConstants;

public class Area {
	
	public String zipCode = "";
	public String name = "";
	
	public float lat = 0.0f;
	public float lng = 0.0f;
	
	public Area(String zipCode, String name, float lat, float lng){
		this.zipCode = zipCode;
		this.name = name;
		this.lat = lat;
		this.lng = lng;
	}
	
	public Area(JSONObject areaObject){
		try {
			this.zipCode = areaObject.getString(BHConstants.JSON_KEY_ZIPCODE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			this.name = areaObject.getString(BHConstants.JSON_KEY_NAME);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			this.lat = (float) areaObject.getDouble(BHConstants.JSON_KEY_LAT);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			this.lng = (float) areaObject.getDouble(BHConstants.JSON_KEY_LNG);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
	}

}
