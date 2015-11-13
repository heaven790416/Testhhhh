package com.yowoo.newbuyhouse.network;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.model.House;
import com.yowoo.newbuyhouse.model.HouseDetail;
import com.yowoo.newbuyhouse.model.HouseMarker;
import com.yowoo.newbuyhouse.network.ConnectService.HttpPostRequestDelegate;

public class HouseService {

	
	public interface HouseMarkerCallback {
		public void onResult(boolean success, ArrayList<HouseMarker> houseMarkers);
	}
	
	public interface HouseJSONCallback {
		public void onResult(boolean success, JSONArray houseJSONArray);
	}
	
	public interface HouseListCallback {
		public void onResult(boolean success, ArrayList<House> houses, int total, int page, int totalPage);
	}

	public interface SimpleHouseCallback {
		public void onResult(boolean success, ArrayList<House> houses);
	}
	
	public interface SingleDetailHouseCallback {
		public void onResult(boolean success, HouseDetail house);
	}
	
	public interface DetailHouseCallback {
		public void onResult(boolean success, ArrayList<HouseDetail> houses);
	}
	
	/****************** 
	 * API: GetNews 
	 ******************/
	public static void getClusterMarkers(String latlngString, double distance, String filterParams, final HouseMarkerCallback callback) {

		String requestUrl = BHConstants.BASE_URL + BHConstants.HOUSE_REST_URL_MAP_SEARCH;
		
		Log.e("test", "latlngString: "+latlngString);
		Log.e("test", "filterParams: "+filterParams);
		
		//set params
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_LATLON, latlngString);
		params.put(BHConstants.PARAM_PARAMS, filterParams);
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
					
					JSONArray markerJSONArray = optObject.getJSONArray(BHConstants.JSON_KEY_POI);
					ArrayList<HouseMarker> markerArray = new ArrayList<HouseMarker>();
					for (int i=0; i<markerJSONArray.length(); i++){
						markerArray.add(new HouseMarker(markerJSONArray.getJSONObject(i)));
					}
					callback.onResult(true, markerArray);
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, null);
				}
			}
		});
		
		
	}

	/****************** 
	 * API: GetSimpleHouse
	 ******************/
	public static void getSimpleHouse(String NOs, final SimpleHouseCallback callback) {
		
		String returnParams = "NO,lat,lng,name,price,priceFirst,discount,address,type,imgDefault";
		
		getHouseDetail(NOs, returnParams, new HouseJSONCallback(){

			@Override
			public void onResult(boolean success, JSONArray houseJSONArray) {
				if (success){
					try{
						ArrayList<House> houseArray = new ArrayList<House>();
						for (int i=0; i<houseJSONArray.length(); i++){
							houseArray.add(new House(houseJSONArray.getJSONObject(i)));
						}
						callback.onResult(true, houseArray);
					}catch(Exception e){
						e.printStackTrace();
						callback.onResult(false, null);
					}
				}else{
					callback.onResult(false, null);
				}
			}
		});
		
	}
	
	/****************** 
	 * API: GetSimplePlusHouse
	 ******************/
	public static void getSimplePlusHouse(String NOs, final SimpleHouseCallback callback) {
		
		String returnParams = "NO,lat,lng,name,price,priceFirst,discount,address,type,imgDefault,areaBuilding,layout,age";
		
		getHouseDetail(NOs, returnParams, new HouseJSONCallback(){

			@Override
			public void onResult(boolean success, JSONArray houseJSONArray) {
				if (success){
					try{
						ArrayList<House> houseArray = new ArrayList<House>();
						for (int i=0; i<houseJSONArray.length(); i++){
							houseArray.add(new House(houseJSONArray.getJSONObject(i)));
						}
						callback.onResult(true, houseArray);
					}catch(Exception e){
						e.printStackTrace();
						callback.onResult(false, null);
					}
				}else{
					callback.onResult(false, null);
				}
			}
		});
		
	}
	
	/****************** 
	 * API: GetCustomHouse
	 ******************/
	public static void getCustomHouse(String NOs, String returnParams, final DetailHouseCallback callback) {
		
		getHouseDetail(NOs, returnParams, new HouseJSONCallback(){

			@Override
			public void onResult(boolean success, JSONArray houseJSONArray) {
				if (success){
					try{
						ArrayList<HouseDetail> houseArray = new ArrayList<HouseDetail>();
						for (int i=0; i<houseJSONArray.length(); i++){
							houseArray.add(new HouseDetail(houseJSONArray.getJSONObject(i)));
						}
						callback.onResult(true, houseArray);
					}catch(Exception e){
						e.printStackTrace();
						callback.onResult(false, null);
					}
				}else{
					callback.onResult(false, null);
				}
			}
		});
		
	}

	
	/****************** 
	 * API: GetSingleDetailHouse
	 ******************/
	public static void getSingleDetailHouse(String NO, final SingleDetailHouseCallback callback) {
		
		String returnParams = 
				"NO,name,age,floor,family,lift,description,address,"+
				"wallStructure,price,priceFirst,discount,areaPublic,"+
				"areaBuilding,pingDetail,areaLand,ues,type,layout,"+
				"imgDefault,bigImg,areaGarage,VRScene,VRScene,store,"+
				"buildingStructure,MRTInfo,imgCount,vr,vrCom,communityNO,"+
				"storeAddress,security,monthlyFee,storetel,sfside,sfdarkroom,"+
				"inc,parking,primarySchool,juniorSchool,market,garden,layoutImg,"+
				"lng,lat,community,roomplus,hallplus,bathroomplus,openroomplus,"+
				"houseFront,buildingFront,windowFront";
		
		getHouseDetail(NO, returnParams, new HouseJSONCallback(){

			@Override
			public void onResult(boolean success, JSONArray houseJSONArray) {
				if (success){
					try{
						HouseDetail houseDetail = new HouseDetail(houseJSONArray.getJSONObject(0));
						callback.onResult(true, houseDetail);
					}catch(Exception e){
						e.printStackTrace();
						callback.onResult(false, null);
					}
				}else{
					callback.onResult(false, null);
				}
			}
		});
		
	}

	
	
	/****************** 
	 * API: GetHouseDetail
	 ******************/
	public static void getHouseDetail(String NOs, String returnParams, final HouseJSONCallback callback) {

		String requestUrl = BHConstants.BASE_URL + BHConstants.HOUSE_REST_URL_GET_HOUSE_DETAIL;
		
		//set params
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_NO, NOs);
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
						//多個房屋物件
						JSONArray houseJSONArray = optObject.getJSONArray(BHConstants.JSON_KEY_HOUSE);
						callback.onResult(true, houseJSONArray);
					}catch(JSONException e){
						try{
							//單個房屋物件
							JSONObject houseJSONObject = optObject.getJSONObject(BHConstants.JSON_KEY_HOUSE);
							callback.onResult(true, new JSONArray().put(houseJSONObject));
						}catch(JSONException e2){
							callback.onResult(false, null);
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, null);
				}
			}
		});
		
	}
	
	/****************** 
	 * API: List Search
	 ******************/
	public static void getHouseList(int page, int limit, String filterParams, String boundLatLngString, final HouseListCallback callback) {

		String requestUrl = BHConstants.BASE_URL + BHConstants.HOUSE_REST_URL_LIST_SEARCH;
		
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
		
		String returnParams = "NO,lat,lng,name,price,priceFirst,discount,address,type,imgDefault,areaBuilding,layout,age";
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
	


}
