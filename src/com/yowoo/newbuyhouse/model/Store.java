package com.yowoo.newbuyhouse.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.yowoo.newbuyhouse.BHConstants;

public class Store {

	public String storeNO = "";
	public String name = ""; //店名
	public String city = ""; //台北市
	public String county = ""; //中山區
	public String address = ""; //長安東路二段２１０號
	public String tel1 = ""; //"02-89786630"
	public String tel2 = "";
	public String traffic = ""; //捷運忠孝復興站五號出口往微風廣場；公車202、203、205、257、41、52路公車於中興中學站下
	
	public float lat = 0.0f;//yPoint
	public float lng = 0.0f;//xPoint
	
	public String title = ""; //明顯三角窗+優質黃金團隊=快速成交
	public String contentText = "";//<p>我們的團隊充滿熱情...</p>
	public String img = ""; //http://img.sinyi.com/u/service/2011/03/3bc8f85a957c0acfe665903c0211c53d.jpg

	public Store(JSONObject storeObject) throws JSONException{
		super();

		this.storeNO = storeObject.getString(BHConstants.JSON_KEY_STORE_NO);
		
		this.name = storeObject.getString(BHConstants.JSON_KEY_NAME);

		try{
			this.city = storeObject.getString(BHConstants.JSON_KEY_CITY);
		}catch(Exception e){}
		
		try{
			this.county = storeObject.getString(BHConstants.JSON_KEY_COUNTY);
		}catch(Exception e){}
		
		try{
			this.address = storeObject.getString(BHConstants.JSON_KEY_ADDRESS);
		}catch(Exception e){}
		
		try{
			this.tel1 = storeObject.getString(BHConstants.JSON_KEY_TEL_1);
		}catch(Exception e){}
		
		try{
			this.tel2 = storeObject.getString(BHConstants.JSON_KEY_TEL_2);
		}catch(Exception e){}
		
		try{
			this.traffic = storeObject.getString(BHConstants.JSON_KEY_TRAFFIC);
		}catch(Exception e){}
		
		try{
			this.lat = (float)storeObject.getDouble(BHConstants.JSON_KEY_YPOINT);
		}catch(Exception e){}
		
		try{
			this.lng = (float)storeObject.getDouble(BHConstants.JSON_KEY_XPOINT);
		}catch(Exception e){}
		
		try{
			this.title = storeObject.getString(BHConstants.JSON_KEY_TITLE);
		}catch(Exception e){}
		
		try{
			this.contentText = storeObject.getString(BHConstants.JSON_KEY_CONTENT_TEXT);
		}catch(Exception e){}
		
		try{
			this.img = storeObject.getString(BHConstants.JSON_KEY_IMG);
		}catch(Exception e){}
		
	}
	
}
