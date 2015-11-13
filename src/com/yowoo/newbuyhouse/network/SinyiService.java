package com.yowoo.newbuyhouse.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.network.ConnectService.HttpPostRequestDelegate;

public class SinyiService {

	public interface FormCallback{
		void onResult(boolean success, String message);
	}
	
	public interface TokenCallback{
		void onResult(boolean success, String token);
	}
	
	public interface RoadCallback {
		public void onResult(boolean success, ArrayList<String> roads);
	}
	
	/****************** 
	 * API: get GCM token
	 ******************/
	public static void getGCMToken(
			final Context context, final String googleGCMProjectNumber, 
			final TokenCallback callback) {
		
		final Handler handler = new Handler(Looper.getMainLooper());

		new Thread(new Runnable() {
			public void run() {

				try {
					GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
		            
		            final String regid = gcm.register(googleGCMProjectNumber);
		            
		            handler.post(new Runnable() {
						public void run() {
							if ((regid==null)||(regid.equals(""))){
								callback.onResult(false, "");
							}else{
								callback.onResult(true, regid);
							}
						}
					});
		        } catch (IOException ex) {
		        		handler.post(new Runnable() {
						public void run() {
								callback.onResult(false, "");
						}
					});
		        }
			}
		}).start();
	}
	
	
	/****************** 
	 * API: send Form
	 ******************/
	public static void sendForm(HashMap<String, Object> params, final FormCallback callback) {

		//String requestUrl = BHConstants.BASE_URL + BHConstants.SINYI_REST_URL_SEND_FORM;
		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.SINYI_REST_URL_SEND_FORM;
		
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
						
						String message = optObject.getString(BHConstants.JSON_KEY_MESSAGE);
						callback.onResult(true, message);
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
	 * API: GET ROADS Search
	 ******************/
	public static void getRoad(String zipcode, final RoadCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.SINYI_REST_URL_GET_ROADS;
		
		//set params
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_ZIPCODE, zipcode);
		
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
						Log.e("test", "didGetResponse success");
						JSONArray newsJSONArray = optObject.getJSONArray("roads");
						ArrayList<String> roadArray = new ArrayList<String>();
						for (int i=0; i<newsJSONArray.length(); i++){
							JSONObject nameObject = newsJSONArray.getJSONObject(i);
							String road = nameObject.getString("name");
							
							roadArray.add(road);
						}
						callback.onResult(true, roadArray);
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
