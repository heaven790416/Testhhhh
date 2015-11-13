package com.yowoo.newbuyhouse.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yowoo.newbuyhouse.BHConstants;

public class PriceMarker {
	
	public double lat = 0.0;
	public double lng = 0.0;
	public JSONArray priceJSONArray = new JSONArray();
	public String label = "";
	
	public PriceMarker(JSONObject markerObject) throws JSONException{
		super();
		
		this.lat = markerObject.getDouble(BHConstants.JSON_KEY_LAT);
		this.lng = markerObject.getDouble(BHConstants.JSON_KEY_LNG);
		
		try{
			this.priceJSONArray = markerObject.getJSONArray(BHConstants.JSON_KEY_NO);
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		//if (priceJSONArray.length()==1){
			this.label = markerObject.getString(BHConstants.JSON_KEY_LABEL);
//		}else{
//			this.label = String.valueOf(priceJSONArray.length());
//		}
		
	}
	
	public int isClusterMarker(){
		int count = this.priceJSONArray.length();

		if (count>1){
			return BHConstants.MARKER_TYPE_CLUSTERED;
		}
		
		return BHConstants.MARKER_TYPE_SINGLE;
	}
}
