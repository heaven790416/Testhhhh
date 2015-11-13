package com.yowoo.newbuyhouse.network;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.model.House;
import com.yowoo.newbuyhouse.model.HouseDetail;
import com.yowoo.newbuyhouse.model.HouseMarker;
import com.yowoo.newbuyhouse.model.Price;
import com.yowoo.newbuyhouse.model.PriceMarker;
import com.yowoo.newbuyhouse.network.ConnectService.HttpGetRequestDelegate;
import com.yowoo.newbuyhouse.network.ConnectService.HttpPostRequestDelegate;
import com.yowoo.newbuyhouse.network.HouseService.HouseListCallback;

public class PriceService {

	public interface PriceListCallback {
		public void onResult(boolean success, ArrayList<Price> prices);
	}
	
	public interface PriceMarkerCallback {
		public void onResult(boolean success, ArrayList<PriceMarker> priceMarkers);
	}
	
	public interface HouseJSONCallback {
		public void onResult(boolean success, JSONArray houseJSONArray);
	}
	
	public interface PriceListPageCallback {
		public void onResult(boolean success, ArrayList<Price> prices, int total, int page, int totalPage);
	}
	
	public interface LatLngCallback {
		public void onResult(Boolean success, LatLng pos);
	}

	
	/****************** 
	 * API: GetPriceMarkers 
	 ******************/
	public static void getPriceMarkers(String latlngString, double distance, String filterParams, final PriceMarkerCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.PRICE_REST_URL_MAP_INFO;
		
		Log.e("test", "latlngString: "+latlngString);
		Log.e("test", "filterParams: "+filterParams);
		
		//return params
		String returnParams = "NO,address,soldDate,buildingType,hasGarage,floor,age,areaBuilding,areaLand,inc,layout,lat,lng,price,unitPrice";

		//set params
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_LATLON, latlngString);
		params.put(BHConstants.PARAM_PARAMS, filterParams);
		params.put(BHConstants.PARAM_RETURN_PARAMS, returnParams);
		if (distance>0.0){
			params.put(BHConstants.PARAM_DISTANCE, distance);
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
					
					JSONArray priceMarkerJSONArray = optObject.getJSONArray(BHConstants.JSON_KEY_POI);
					ArrayList<PriceMarker> priceMarkerArray = new ArrayList<PriceMarker>();
					for (int i=0; i<priceMarkerJSONArray.length(); i++){
						priceMarkerArray.add(new PriceMarker(priceMarkerJSONArray.getJSONObject(i)));
					}
					callback.onResult(true, priceMarkerArray);
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, null);
				}
			}
		});
		
	}

	/****************** 
	 * API: Price List Search
	 ******************/
	public static void getPriceList(int page, int limit, String filterParams, String boundLatLngString, final PriceListPageCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.PRICE_REST_URL_LIST_INFO;
		
		//set params
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_PAGE, page);
		params.put(BHConstants.PARAM_LIMIT, limit);
		if (!filterParams.equals("")){
			params.put(BHConstants.PARAM_PARAMS, filterParams);
		}
		
		if (!boundLatLngString.equals("")){
			params.put(BHConstants.PARAM_LATLON, boundLatLngString);
		}
		
		//return params
		String returnParams = "NO,address,soldDate,buildingType,hasGarage,floor,age,areaBuilding,areaLand,inc,layout,lat,lng,price,unitPrice,outlier";
		params.put(BHConstants.PARAM_RETURN_PARAMS, returnParams);
		
		ConnectService.sendPostRequest(requestUrl, params, new HttpPostRequestDelegate(){

			@Override
			public void didGetResponse(String url, String response, Header[] headers) {
				// TODO Auto-generated method stub
				if(response==null) {
					callback.onResult(false, null,0,0,0);
					return;
				}

				try {
					JSONObject optObject = new JSONObject(response).getJSONObject(BHConstants.JSON_KEY_OPT);
					if (!(optObject.getString(BHConstants.JSON_KEY_STATUS).equalsIgnoreCase("OK"))){
						callback.onResult(false, null,0,0,0);
						return;
					}
					
					try{
						int total = optObject.getInt(BHConstants.JSON_KEY_TOTAL);
						int page = optObject.getInt(BHConstants.JSON_KEY_PAGE);
						int totalPage = optObject.getInt(BHConstants.JSON_KEY_TOTAL_PAGE);
						
						JSONArray priceJSONArray = optObject.getJSONArray(BHConstants.JSON_KEY_LIST);
						ArrayList<Price> priceArray = new ArrayList<Price>();
						for (int i=0; i<priceJSONArray.length(); i++){
							priceArray.add(new Price(priceJSONArray.getJSONObject(i)));
						}
						callback.onResult(true, priceArray, total, page, totalPage);
					}catch(JSONException e){
						callback.onResult(false, null, 0,0,0);
						e.printStackTrace();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, null, 0,0,0);
				}
			}
		});
		
	}
	
	/****************** 
	 * API: get Price Detail By NOs
	 ******************/
	public static void getPriceDetail(String NOs, final PriceListCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.PRICE_REST_URL_GET_DETAIL;
		
		//set params
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_NO, NOs);
		
		//return params
		String returnParams = "NO,address,soldDate,buildingType,hasGarage,floor,age,areaBuilding,areaLand,inc,layout,lat,lng,price,unitPrice,outlier";
		params.put(BHConstants.PARAM_RETURN_PARAMS, returnParams);
		
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
						JSONArray priceJSONArray = optObject.getJSONArray(BHConstants.JSON_KEY_DATA);
						ArrayList<Price> priceArray = new ArrayList<Price>();
						for (int i=0; i<priceJSONArray.length(); i++){
							priceArray.add(new Price(priceJSONArray.getJSONObject(i)));
						}
						callback.onResult(true, priceArray);
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
	 * API: Get Road LatLng From Google
	 ******************/
//	public static void getRoadLatLngFromGoogle(String address, final LatLngCallback callback) {
//
//		String requestUrl = BHConstants.GOOGLE_API_GET_DATA_BY_ADDR;
//		
//		//set params
//		HashMap<String, Object> params = new HashMap<String, Object>();
//		params.put(BHConstants.PARAM_ADDRESS, address);
//		//params.put(BHConstants.PARAM_SENSOR, false);
//		
//		ConnectService.sendGetRequest(requestUrl, params, new HttpGetRequestDelegate(){
//
//			@Override
//			public void didGetResponse(String url, String response) {
//				if(response==null) {
//					callback.onResult(false, null);
//					return;
//				}
//
//				try {
//					JSONObject resObject = new JSONObject(response);
//					if (!(resObject.getString(BHConstants.JSON_KEY_STATUS).equalsIgnoreCase("OK"))){
//						callback.onResult(false, null);
//						return;
//					}
//						
//					JSONArray results = resObject.getJSONArray(BHConstants.JSON_KEY_RESULTS);	
//					JSONObject location = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
//					
//					callback.onResult(true, new LatLng(location.getDouble("lat"), location.getDouble("lng")));
//					
//				} catch (Exception e) {
//					e.printStackTrace();
//					callback.onResult(false, null);
//				}
//			}
//		});
//		
//	}

}
