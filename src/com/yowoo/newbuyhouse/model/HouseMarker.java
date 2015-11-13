package com.yowoo.newbuyhouse.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yowoo.newbuyhouse.BHConstants;

public class HouseMarker {
	
	public double lat = 0.0;
	public double lng = 0.0;
	public JSONArray houseNOs = new JSONArray();
	public String label = "";
	
	public HouseMarker(JSONObject markerObject) throws JSONException{
		super();
		
		this.lat = markerObject.getDouble(BHConstants.JSON_KEY_LAT);
		this.lng = markerObject.getDouble(BHConstants.JSON_KEY_LNG);
		this.label = markerObject.getString(BHConstants.JSON_KEY_LABEL);
		
		try{
			this.houseNOs = markerObject.getJSONArray(BHConstants.JSON_KEY_NO);
		}catch(JSONException e){
			this.houseNOs.put(markerObject.getString(BHConstants.JSON_KEY_NO));
		}
		
	}
	
	public int isClusterMarker(){
		int count = this.houseNOs.length();

		if (count>1){
			return BHConstants.MARKER_TYPE_CLUSTERED;
		}
		
		return BHConstants.MARKER_TYPE_SINGLE;
	}
}
