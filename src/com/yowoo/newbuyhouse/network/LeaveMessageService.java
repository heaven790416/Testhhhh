package com.yowoo.newbuyhouse.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.model.House;
import com.yowoo.newbuyhouse.network.ConnectService.HttpPostRequestDelegate;
import com.yowoo.newbuyhouse.network.HouseService.HouseJSONCallback;
import com.yowoo.newbuyhouse.network.HouseService.SimpleHouseCallback;
import com.yowoo.newbuyhouse.network.NewsService.NewsListCallback;
import com.yowoo.newbuyhouse.network.SinyiService.FormCallback;
import com.yowoo.newbuyhouse.news.News;

import android.util.Log;

public class LeaveMessageService {
	
	
	public interface FormCallback{
		void onResult(boolean success, String message);
	}
	
	
	
	/****************** 
	 * API: send buy Form
	 ******************/
	public static void sendForm(HashMap<String, Object> params, final FormCallback callback) {

		//String requestUrl = BHConstants.BASE_URL + BHConstants.SINYI_REST_URL_SEND_FORM;
		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.SINYI_REST_URL_SEND_FORM;
		
		ConnectService.sendPostRequest(requestUrl, params, new HttpPostRequestDelegate(){

			@Override
			public void didGetResponse(String url, String response, Header[] headers) {
				
				Log.e("test5", response);
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
	
	

}
