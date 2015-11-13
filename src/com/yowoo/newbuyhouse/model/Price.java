package com.yowoo.newbuyhouse.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.yowoo.newbuyhouse.BHConstants;

public class Price {

	
	private JSONObject priceJSONObject = new JSONObject();
	
//	NO (銷編)
//	address (地址)
//	soldDate (年月)
//	buildingType (類型)
//	hasGarage (有無車位)
//	floor (樓層)
//	age	(屋齡)
//	areaBuilding (建坪)
//	areaLand (地坪)
//	inc (來源)
//	layout  (格局)
//	lat (緯度)
//	lng (經度)
//	price (總價)
//	unitPrice (單價)

	
	
	public Price(){
		super();
	}
	
	public Price(JSONObject priceJSONObject) throws JSONException{
		super();

		this.priceJSONObject = priceJSONObject;
		
	}
	
	public String getStringData(String key){
		try{
			String value = priceJSONObject.getString(key);
			if (value==null) value = "";
			return value;
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}
	
	public int getIntData(String key){
		try{
			int value = priceJSONObject.getInt(key);
			return value;
		}catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}
	
	public float getFloatData(String key){
		try{
			float value = (float) priceJSONObject.getDouble(key);
			return value;
		}catch(Exception e){
			e.printStackTrace();
			return 0.0f;
		}
	}
	
	public Boolean getBooleanData(String key){
		try{
			Boolean value = (Boolean) priceJSONObject.getBoolean(key);
			return value;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
}
