package com.yowoo.newbuyhouse.network;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.Singleton;
import com.yowoo.newbuyhouse.model.Area;
import com.yowoo.newbuyhouse.model.City;
import com.yowoo.newbuyhouse.model.Store;
import com.yowoo.newbuyhouse.network.ConnectService.HttpPostRequestDelegate;
import com.yowoo.newbuyhouse.network.StoreService.StoreListCallback;
import com.yowoo.newbuyhouse.news.News;

public class NewsService {
	
	public interface NewsListCallback {
		public void onResult(boolean success, ArrayList<News> news);
	}
	
	public interface SingleNewsCallback {
		public void onResult(boolean success, News news);
	}
	

	
	/****************** 
	 * API: News List Search
	 ******************/
	public static void getNewsList(int pageStartFromOne, int count, final NewsListCallback callback) {

		String requestUrl = BHConstants.BASE_URL + BHConstants.NEWS_REST_URL_GET_NEWS;
		
		//set params
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_PAGE, pageStartFromOne);
		params.put(BHConstants.PARAM_PON, count);
		
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
						
						JSONArray newsJSONArray = optObject.getJSONArray(BHConstants.JSON_KEY_DATA);
						ArrayList<News> newsArray = new ArrayList<News>();
						for (int i=0; i<newsJSONArray.length(); i++){
							newsArray.add(new News(newsJSONArray.getJSONObject(i)));
						}
						callback.onResult(true, newsArray);
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
	 * API: News List Search
	 ******************/
	public static void getNewsDetail(String id, final SingleNewsCallback callback) {

		String requestUrl = BHConstants.BASE_URL + BHConstants.NEWS_REST_URL_GET_NEWS_DETAIL;
		
		//set params
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_ID, id);
		
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
						
						JSONObject newsJSONObject = optObject.getJSONObject(BHConstants.JSON_KEY_DATA);
						News news = new News(newsJSONObject);
						
						callback.onResult(true, news);
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
