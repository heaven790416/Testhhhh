package com.yowoo.newbuyhouse.network;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.Singleton;
import com.yowoo.newbuyhouse.model.Area;
import com.yowoo.newbuyhouse.model.City;
import com.yowoo.newbuyhouse.model.Store;
import com.yowoo.newbuyhouse.network.ConnectService.HttpPostRequestDelegate;

public class StoreService {
	
	public interface StoreListCallback {
		public void onResult(boolean success, ArrayList<Store> stores);
	}
	
	public interface CityCallback{
		public void onResult(Boolean success, ArrayList<City> citys);
	}
	
	public interface AreaCallback{
		public void onResult(Boolean success, ArrayList<Area> areas);
	}

	
	/****************** 
	 * API: Store List Search
	 ******************/
	public static void getStoreList(String boundLatLngString, final StoreListCallback callback) {

		String requestUrl = BHConstants.BASE_URL + BHConstants.STORE_REST_URL_LIST_SEARCH;
		
		//set params
		HashMap<String, Object> params = new HashMap<String, Object>();
		if (!boundLatLngString.equals("")){
			params.put(BHConstants.PARAM_LATLON, boundLatLngString);
		}
		
		ConnectService.sendPostRequest(requestUrl, params, new HttpPostRequestDelegate(){

			@Override
			public void didGetResponse(String url, String response, Header[] headers) {
				// TODO Auto-generated method stub
				if(response==null) {
					callback.onResult(false, null);
					return;
				}

				try {
					JSONObject optObject = new JSONObject(response).getJSONObject(BHConstants.JSON_KEY_OPT);
					if (!(optObject.getString(BHConstants.JSON_KEY_STATUS).equalsIgnoreCase("OK"))){
						callback.onResult(false, null);
						return;
					}
					
					try{
						
						JSONArray storeJSONArray = optObject.getJSONArray(BHConstants.JSON_KEY_DATA);
						ArrayList<Store> storeArray = new ArrayList<Store>();
						for (int i=0; i<storeJSONArray.length(); i++){
							storeArray.add(new Store(storeJSONArray.getJSONObject(i)));
						}
						callback.onResult(true, storeArray);
					}catch(JSONException e){
						callback.onResult(false, null);
						e.printStackTrace();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, null);
				}
			}
		});
		
	}
	
	/****************** 
	 * API: get Store Citys
	 ******************/
	public static void getStoreCitys(final CityCallback callback) {

		String requestUrl = BHConstants.BASE_URL + BHConstants.STORE_REST_URL_GET_CITY;
		
		//set params
		HashMap<String, Object> params = new HashMap<String, Object>();
		
		ConnectService.sendPostRequest(requestUrl, params, new HttpPostRequestDelegate(){

			@Override
			public void didGetResponse(String url, String response, Header[] headers) {
				if(response==null) {
					callback.onResult(false, null);
					return;
				}

				try {
					JSONObject optObject = new JSONObject(response).getJSONObject(BHConstants.JSON_KEY_OPT);
					if (!(optObject.getString(BHConstants.JSON_KEY_STATUS).equalsIgnoreCase("OK"))){
						callback.onResult(false, null);
						return;
					}
					
					try{
						
						JSONArray cityJSONArray = optObject.getJSONArray(BHConstants.JSON_KEY_CITYS);
						ArrayList<City> cityArray = new ArrayList<City>();
						
						//加入不指定
						City city = new City("0", Singleton.resources.getString(R.string.not_restrict), "0", true, 0, 0);
						cityArray.add(city);
						
						for (int i=0; i<cityJSONArray.length(); i++){
							cityArray.add(new City(cityJSONArray.getJSONObject(i)));
						}
						callback.onResult(true, cityArray);
					}catch(JSONException e){
						callback.onResult(false, null);
						e.printStackTrace();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, null);
				}
			}
		});
		
	}
	
	/****************** 
	 * API: get Store Areas
	 ******************/
	public static void getStoreAreas(String cityId, final AreaCallback callback) {

		String requestUrl = BHConstants.BASE_URL + BHConstants.STORE_REST_URL_GET_AREA;
		
		//set params
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_CITY_ID, cityId);
		
		ConnectService.sendPostRequest(requestUrl, params, new HttpPostRequestDelegate(){

			@Override
			public void didGetResponse(String url, String response, Header[] headers) {
				if(response==null) {
					callback.onResult(false, null);
					return;
				}

				try {
					JSONObject optObject = new JSONObject(response).getJSONObject(BHConstants.JSON_KEY_OPT);
					if (!(optObject.getString(BHConstants.JSON_KEY_STATUS).equalsIgnoreCase("OK"))){
						callback.onResult(false, null);
						return;
					}
					
					try{
						
						JSONArray areaJSONArray = optObject.getJSONArray(BHConstants.JSON_KEY_AREAS);
						ArrayList<Area> areaArray = new ArrayList<Area>();
						
						//加入不指定
						Area area = new Area("0", Singleton.resources.getString(R.string.not_restrict), 0, 0);
						areaArray.add(area);
						
						for (int i=0; i<areaJSONArray.length(); i++){
							areaArray.add(new Area(areaJSONArray.getJSONObject(i)));
						}
						callback.onResult(true, areaArray);
					}catch(JSONException e){
						callback.onResult(false, null);
						e.printStackTrace();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, null);
				}
			}
		});
		
	}
	

}
