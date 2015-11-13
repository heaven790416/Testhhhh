package com.yowoo.newbuyhouse.network;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.login.LoginInfo;
import com.yowoo.newbuyhouse.model.House;
import com.yowoo.newbuyhouse.network.ConnectService.HttpPostRequestDelegate;
import com.yowoo.newbuyhouse.network.HouseService.HouseListCallback;

public class TrackService {

	
	public interface TrackCallback{
		void onResult(boolean success, String debugMessage);
	}
	
	public interface SubscribeCallback{
		void onResult(boolean success, String debugMessage, JSONArray subscribes);
	}
	
	public interface SubscribeHouseListCallback {
		public void onResult(boolean success, ArrayList<House> houses, int total, int page, int totalPage);
	}
	
	/****************** 
	 * API: add Fav Action
	 ******************/
	public static void trackHouse(String houseNO, String salesID, final TrackCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.TRACK_REST_URL_TRACK_HOUSE;
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", LoginInfo.getInstance().getToken());
		
		//TODO: 是否要send member id?
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_NO, houseNO);
		//params.put(BHConstants.PARAM_SALES_ID, salesID);
		params.put(BHConstants.PARAM_MEMBERID, LoginInfo.getInstance().getMemberID());
		params.put(BHConstants.PARAM_FROM, BHConstants.FROM_ANDROID_APP);
		
		ConnectService.sendPostRequest(requestUrl, headers, params, new HttpPostRequestDelegate(){

			@Override
			public void didGetResponse(String url, String response, Header[] headerArray) {
				// TODO Auto-generated method stub
				if(response==null) {
					callback.onResult(false, "");
					return;
				}
				
				//TODO TEST
				for (int i=0; i<headerArray.length; i++){
					Log.e("header", headerArray[i].getName() + ":" +headerArray[i].getValue());
				}

				try {
					JSONObject responseObject = new JSONObject(response);
					JSONObject optObject = responseObject.getJSONObject(BHConstants.JSON_KEY_OPT);
					
					//OPT裡面的message
					String message = "";
					if (optObject.has(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE)){
						message = optObject.isNull(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE)? 
							"" : optObject.getString(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE);
					}
					
					if (!(optObject.getString(BHConstants.JSON_KEY_STATUS).equalsIgnoreCase("OK"))){
						callback.onResult(false, message);
						return;
					}
						
					callback.onResult(true, message);
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, "");
				}
			}
		});
		
	}
	
	
	/****************** 
	 * API: remove track Action
	 ******************/
	public static void removeTrackHouse(String houseNO, String salesID, final TrackCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.TRACK_REST_URL_REMOVE_TRACK_HOUSE;
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", LoginInfo.getInstance().getToken());
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_NO, houseNO);
		//params.put(BHConstants.PARAM_SALES_ID, salesID);
		params.put(BHConstants.PARAM_MEMBERID, LoginInfo.getInstance().getMemberID());
		params.put(BHConstants.PARAM_FROM, BHConstants.FROM_ANDROID_APP);
		
		ConnectService.sendPostRequest(requestUrl, headers, params, new HttpPostRequestDelegate(){

			@Override
			public void didGetResponse(String url, String response, Header[] headerArray) {
				// TODO Auto-generated method stub
				if(response==null) {
					callback.onResult(false, "");
					return;
				}

				try {
					JSONObject responseObject = new JSONObject(response);
					JSONObject optObject = responseObject.getJSONObject(BHConstants.JSON_KEY_OPT);
					
					//OPT裡面的message
					String message = "";
					if (optObject.has(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE)){
						message = optObject.isNull(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE)? 
							"" : optObject.getString(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE);
					}
					
					if (!(optObject.getString(BHConstants.JSON_KEY_STATUS).equalsIgnoreCase("OK"))){
						callback.onResult(false, message);
						return;
					}
						
					callback.onResult(true, message);
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, "");
				}
			}
		});
		
	}
	
	
	/****************** 
	 * API: add Subscribe Action
	 ******************/
	public static void subscribeSearch(String filterParams, String displayParams, final TrackCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.TRACK_REST_URL_SUBSCRIBE_SEARCH;
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", LoginInfo.getInstance().getToken());
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_PARAMS, filterParams);
		params.put(BHConstants.PARAM_CRITERIA, displayParams);
		params.put(BHConstants.PARAM_MEMBER_ID, LoginInfo.getInstance().getMemberID());
		params.put(BHConstants.PARAM_FROM, BHConstants.FROM_ANDROID_APP);
		
		ConnectService.sendPostRequest(requestUrl, headers, params, new HttpPostRequestDelegate(){

			@Override
			public void didGetResponse(String url, String response, Header[] headerArray) {
				// TODO Auto-generated method stub
				if(response==null) {
					callback.onResult(false, "");
					return;
				}
				
				//TODO TEST
				for (int i=0; i<headerArray.length; i++){
					Log.e("header", headerArray[i].getName() + ":" +headerArray[i].getValue());
				}

				try {
					JSONObject responseObject = new JSONObject(response);
					JSONObject optObject = responseObject.getJSONObject(BHConstants.JSON_KEY_OPT);
					
					//OPT裡面的message
					String message = "";
					if (optObject.has(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE)){
						message = optObject.isNull(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE)? 
							"" : optObject.getString(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE);
					}
					
					if (!(optObject.getString(BHConstants.JSON_KEY_STATUS).equalsIgnoreCase("OK"))){
						callback.onResult(false, message);
						return;
					}
						
					callback.onResult(true, message);
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, "");
				}
			}
		});
		
	}
	
	
	/****************** 
	 * API: get Subscribe Action
	 ******************/
	public static void getSubscribes(final SubscribeCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.TRACK_REST_URL_GET_SUBSCRIBES;
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", LoginInfo.getInstance().getToken());
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_MEMBER_ID, LoginInfo.getInstance().getMemberID());
		
		ConnectService.sendPostRequest(requestUrl, headers, params, new HttpPostRequestDelegate(){

			@Override
			public void didGetResponse(String url, String response, Header[] headerArray) {
				// TODO Auto-generated method stub
				if(response==null) {
					callback.onResult(false, "", null);
					return;
				}
				
				//TODO TEST
				for (int i=0; i<headerArray.length; i++){
					Log.e("header", headerArray[i].getName() + ":" +headerArray[i].getValue());
				}

				try {
					JSONObject responseObject = new JSONObject(response);
					JSONObject optObject = responseObject.getJSONObject(BHConstants.JSON_KEY_OPT);
					
					//OPT裡面的message
					String message = "";
					if (optObject.has(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE)){
						message = optObject.isNull(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE)? 
							"" : optObject.getString(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE);
					}
					
					if (!(optObject.getString(BHConstants.JSON_KEY_STATUS).equalsIgnoreCase("OK"))){
						callback.onResult(false, message, null);
						return;
					}
						
					JSONArray list = optObject.getJSONArray(BHConstants.JSON_KEY_LIST);
					callback.onResult(true, message, list);
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, "", null);
				}
			}
		});
		
	}
	
	/****************** 
	 * API: add Subscribe Action
	 ******************/
	public static void removeSubscribe(String subIds, String salesID,  final TrackCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.TRACK_REST_URL_REMOVE_SUBSCRIBE;
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", LoginInfo.getInstance().getToken());
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_MEMBER_ID, LoginInfo.getInstance().getMemberID());
		params.put(BHConstants.PARAM_DASH_ID, subIds);
		
		ConnectService.sendPostRequest(requestUrl, headers, params, new HttpPostRequestDelegate(){

			@Override
			public void didGetResponse(String url, String response, Header[] headerArray) {
				// TODO Auto-generated method stub
				if(response==null) {
					callback.onResult(false, "");
					return;
				}
				
				//TODO TEST
				for (int i=0; i<headerArray.length; i++){
					Log.e("header", headerArray[i].getName() + ":" +headerArray[i].getValue());
				}

				try {
					JSONObject responseObject = new JSONObject(response);
					JSONObject optObject = responseObject.getJSONObject(BHConstants.JSON_KEY_OPT);
					
					//OPT裡面的message
					String message = "";
					if (optObject.has(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE)){
						message = optObject.isNull(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE)? 
							"" : optObject.getString(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE);
					}
					
					if (!(optObject.getString(BHConstants.JSON_KEY_STATUS).equalsIgnoreCase("OK"))){
						callback.onResult(false, message);
						return;
					}
						
					callback.onResult(true, message);
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, "");
				}
			}
		});
		
	}
	
	
	/****************** 
	 * API: List Search
	 ******************/
	public static void getSubscribeHouseList(int page, int limit, String filterParams, String boundLatLngString, final SubscribeHouseListCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.TRACK_REST_URL_GET_SUBSCRIBE_HOUSE;
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", LoginInfo.getInstance().getToken());
		
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
						
						JSONArray houseJSONArray = optObject.getJSONArray(BHConstants.JSON_KEY_LIST);
						ArrayList<House> houseArray = new ArrayList<House>();
						for (int i=0; i<houseJSONArray.length(); i++){
							houseArray.add(new House(houseJSONArray.getJSONObject(i)));
						}
						callback.onResult(true, houseArray, total, page, totalPage);
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
	 * API: update Subscribe Action
	 ******************/
	public static void updateSubscirbe(String subscribeId, Boolean needUpdateTime, HashMap<String, Object> params, final TrackCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.TRACK_REST_URL_UPDATE_SUBSCRIBE;
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", LoginInfo.getInstance().getToken());
		
		params.put(BHConstants.PARAM_DASH_ID, subscribeId);
		if (needUpdateTime){
			params.put(BHConstants.JSON_KEY_AFTER_DATE, 1);
		}
		
		ConnectService.sendPostRequest(requestUrl, headers, params, new HttpPostRequestDelegate(){

			@Override
			public void didGetResponse(String url, String response, Header[] headerArray) {
				// TODO Auto-generated method stub
				if(response==null) {
					callback.onResult(false, "");
					return;
				}
				
				//TODO TEST
				for (int i=0; i<headerArray.length; i++){
					Log.e("header", headerArray[i].getName() + ":" +headerArray[i].getValue());
				}

				try {
					JSONObject responseObject = new JSONObject(response);
					JSONObject optObject = responseObject.getJSONObject(BHConstants.JSON_KEY_OPT);
					
					//OPT裡面的message
					String message = "";
					if (optObject.has(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE)){
						message = optObject.isNull(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE)? 
							"" : optObject.getString(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE);
					}
					
					if (!(optObject.getString(BHConstants.JSON_KEY_STATUS).equalsIgnoreCase("OK"))){
						callback.onResult(false, message);
						return;
					}
						
					callback.onResult(true, message);
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, "");
				}
			}
		});
		
	}
	
	/****************** 
	 * API: add GCM Token for this memberID Action
	 ******************/
	public static void loginAppGCMToken(String deviceID,  final TrackCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.TRACK_REST_URL_LOGIN_GCM_TOKEN;
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", LoginInfo.getInstance().getToken());
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_MEMBER_ID, LoginInfo.getInstance().getMemberID());
		params.put(BHConstants.PARAM_DEVICE_ID, deviceID);
		params.put(BHConstants.PARAM_FROM, BHConstants.FROM_ANDROID_APP);
		
		ConnectService.sendPostRequest(requestUrl, headers, params, new HttpPostRequestDelegate(){

			@Override
			public void didGetResponse(String url, String response, Header[] headerArray) {
				// TODO Auto-generated method stub
				if(response==null) {
					callback.onResult(false, "");
					return;
				}
				
				//TODO TEST
				for (int i=0; i<headerArray.length; i++){
					Log.e("header", headerArray[i].getName() + ":" +headerArray[i].getValue());
				}

				try {
					JSONObject responseObject = new JSONObject(response);
					JSONObject optObject = responseObject.getJSONObject(BHConstants.JSON_KEY_OPT);
					
					//OPT裡面的message
					String message = "";
					if (optObject.has(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE)){
						message = optObject.isNull(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE)? 
							"" : optObject.getString(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE);
					}
					
					if (!(optObject.getString(BHConstants.JSON_KEY_STATUS).equalsIgnoreCase("OK"))){
						callback.onResult(false, message);
						return;
					}
						
					callback.onResult(true, message);
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, "");
				}
			}
		});
		
	}
	
	
	
	/****************** 
	 * API: remove GCM Token for this memberID Action
	 ******************/
	public static void logoutAppGCMToken(String deviceID,  final TrackCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.TRACK_REST_URL_LOGOUT_GCM_TOKEN;
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", LoginInfo.getInstance().getToken());
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_MEMBER_ID, LoginInfo.getInstance().getMemberID());
		params.put(BHConstants.PARAM_DEVICE_ID, deviceID);
		
		ConnectService.sendPostRequest(requestUrl, headers, params, new HttpPostRequestDelegate(){

			@Override
			public void didGetResponse(String url, String response, Header[] headerArray) {
				// TODO Auto-generated method stub
				if(response==null) {
					callback.onResult(false, "");
					return;
				}
				
				//TODO TEST
				for (int i=0; i<headerArray.length; i++){
					Log.e("header", headerArray[i].getName() + ":" +headerArray[i].getValue());
				}

				try {
					JSONObject responseObject = new JSONObject(response);
					JSONObject optObject = responseObject.getJSONObject(BHConstants.JSON_KEY_OPT);
					
					//OPT裡面的message
					String message = "";
					if (optObject.has(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE)){
						message = optObject.isNull(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE)? 
							"" : optObject.getString(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE);
					}
					
					if (!(optObject.getString(BHConstants.JSON_KEY_STATUS).equalsIgnoreCase("OK"))){
						callback.onResult(false, message);
						return;
					}
						
					callback.onResult(true, message);
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, "");
				}
			}
		});
		
	}
	
}
