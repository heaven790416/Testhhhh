package com.yowoo.newbuyhouse.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.yowoo.newbuyhouse.BHConstants;

public class House {

	public String NO = "";
	public String name="";
	public String imgDefault = "";
	public int price = 0;
	public int priceFirst = 0;
	public float discount = 0.0f;
	public String type = "";
	public String address = "";
	public float lat = 0.0f;
	public float lng = 0.0f;
	
	//for house list row
	public float areaBuilding = 0.0f;
	public String layout = "";
	public float age = 0.0f;
	
	//for subscribe house row
	public Date createDate = new Date();
	
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	
	public House(){
		super();
	}
	
	public House(JSONObject houseObject) throws JSONException{
		super();

		this.NO = houseObject.getString(BHConstants.JSON_KEY_NO);
		
		this.name = houseObject.getString(BHConstants.JSON_KEY_NAME);

		try{
			this.imgDefault = houseObject.getString(BHConstants.JSON_KEY_IMG_DEFAULT);
		}catch(Exception e){}
		
		this.price = houseObject.getInt(BHConstants.JSON_KEY_PRICE);
		
		try{
			this.priceFirst = houseObject.getInt(BHConstants.JSON_KEY_PRICE_FIRST);
		}catch(Exception e){}
		
		try{
			this.discount = (float) houseObject.getDouble(BHConstants.JSON_KEY_DISCOUNT);
		}catch(Exception e){}
		
		try{
			this.type = houseObject.getString(BHConstants.JSON_KEY_TYPE);
		}catch(Exception e){}
		
		try{
			this.address = houseObject.getString(BHConstants.JSON_KEY_ADDRESS);
		}catch(Exception e){}
		
		try{
			this.lat = (float) houseObject.getDouble(BHConstants.JSON_KEY_LAT);
		}catch(Exception e){}
		
		try{
			this.lng = (float) houseObject.getDouble(BHConstants.JSON_KEY_LNG);
		}catch(Exception e){}
		
		
		//For house list row
		try{
			this.areaBuilding = (float) houseObject.getDouble(BHConstants.JSON_KEY_AREA_BUILDING);
		}catch(Exception e){}
		
		try{
			this.layout = houseObject.getString(BHConstants.JSON_KEY_LAYOUT);
		}catch(Exception e){}
		
		try{
			this.age = (float) houseObject.getDouble(BHConstants.JSON_KEY_AGE);
		}catch(Exception e){}
		
		try{
			String createDatetime = houseObject.getString(BHConstants.JSON_KEY_CREATE_DATETIME);
			if (!createDatetime.equals("")){
				createDate = House.sdf.parse(createDatetime);
			}else{
				//有些資料沒有房屋上架時間，暫定指派一個較早的時間，以方便做新屋的判斷
				createDate = House.sdf.parse("1900-01-01 00:00:00");
			}
		}catch(Exception e){
			
		}
	}
	
	
	
}
