package com.yowoo.newbuyhouse.network;

import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.login.LoginInfo;
import com.yowoo.newbuyhouse.login.UserConstants;
import com.yowoo.newbuyhouse.network.ConnectService.HttpGetRequestDelegate;
import com.yowoo.newbuyhouse.network.ConnectService.HttpPostRequestDelegate;
import com.yowoo.newbuyhouse.network.TrackService.TrackCallback;

public class LoginService {

	public interface MemberCallback{
		void onResult(boolean success, JSONObject memberJSONObject, String debubMessage);
	}
	
	public interface LoginCallback{
		void onResult(boolean success, String debugMessage);
	}
	
	public interface ProfileCallback{
		void onResult(boolean success, JSONObject profileJSONObject, String debubMessage);
	}
	
	public interface RequestCallback{
		void onResult(boolean success, JSONObject responseObject, String debugMessage);
	}
	
	
	
	/****************** 
	 * API: Login Action
	 ******************/
	public static void login(String account, String pw, final LoginCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.LOGIN_REST_URL_LOGIN;
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_ACCOUNT, account);
		params.put(BHConstants.PARAM_PW, pw);
		
		ConnectService.sendPostRequest(requestUrl, params, new HttpPostRequestDelegate(){

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
					
					//befor callback, store cookie to loginInfo
					for (int i=0; i<headerArray.length; i++){
						if (headerArray[i].getName().equals("Set-Cookie")){
							Log.e("header", headerArray[i].getName() + ":" +headerArray[i].getValue());
							LoginInfo.getInstance().setToken(headerArray[i].getValue());
							break;
						}
					}
						
					//save "loginSource" to loginInfo, 
					//but not save to pref until next step isLogin=true;
					LoginInfo.getInstance().setLoginBy(UserConstants.LOGIN_BY_ONE_ACCOUNT);
					
					callback.onResult(true, message);
						
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, "");
				}
			}
		});
		
	}
	
	
	/****************** 
	 * API: isLogin Action
	 ******************/
	public static void isLogin(HashMap<String, Object> params, final MemberCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.LOGIN_REST_URL_IS_LOGIN;
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", LoginInfo.getInstance().getToken());
		
		ConnectService.sendPostRequest(requestUrl, headers, params, new HttpPostRequestDelegate(){

			@Override
			public void didGetResponse(String url, String response, Header[] headerArray) {
				// TODO Auto-generated method stub
				if(response==null) {
					callback.onResult(false, null, "");
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
						callback.onResult(false, null, message);
						return;
					}
						
					Boolean isLogin = optObject.getBoolean(UserConstants.KEY_IS_LOGIN);
					if (isLogin){
						JSONObject member = optObject.getJSONObject(UserConstants.KEY_MEMBER);
						
						//確定為登入狀況，並可抓到使用者資料
						//store user info to LoginInfo
						LoginInfo.getInstance().setMember(member);
						LoginInfo.getInstance().setIsLogin(true);
						if (optObject.has(UserConstants.KEY_FAV)){
							JSONArray fav = optObject.getJSONArray(UserConstants.KEY_FAV);
							LoginInfo.getInstance().setFav(fav);
						}
						
						//存至preference
						LoginInfo.getInstance().storeInfoToPreference();
						
						callback.onResult(true, member, message);
						return;
					}
					
					callback.onResult(false, null, message);
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, null, "");
				}
			}
		});
		
	}
	
	/****************** 
	 * API: Logout Action
	 ******************/
	public static void logout(final LoginCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.LOGIN_REST_URL_IS_LOGIN;
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", LoginInfo.getInstance().getToken());
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		
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
						
					//存至preference
					LoginInfo.getInstance().logout();
					callback.onResult(true, message);
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, "");
				}
			}
		});
		
	}
	
	/****************** 
	 * API: Forget Pw Action
	 ******************/
	public static void forgetPw(String account, final LoginCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.LOGIN_REST_URL_FORGET_PW;
		
		HashMap<String, String> headers = new HashMap<String, String>();
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_ACCOUNT, account);
		
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
	 * API: Register Action
	 ******************/
	public static void register(String account, String pw, final LoginCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.LOGIN_REST_URL_REGISTER;
		
		HashMap<String, String> headers = new HashMap<String, String>();
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_ACCOUNT, account);
		params.put(BHConstants.PARAM_PW, pw);
		
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
						
					//TODO: 是否有資料需要存入loginInfo?
					
					callback.onResult(true, message);
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, "");
				}
			}
		});
		
	}
	
	
	/****************** 
	 * API: Forget Pw Action
	 ******************/
	public static void setMemberData(final HashMap<String, Object> params, final LoginCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.LOGIN_REST_URL_SET_MEMBER_DATA;
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", LoginInfo.getInstance().getToken());
		
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
					
					//改為OPT裡面的message
					String message = "";
					if (optObject.has(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE)){
						message = optObject.isNull(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE)? 
							"" : optObject.getString(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE);
					}
					
					if (!(optObject.getString(BHConstants.JSON_KEY_STATUS).equalsIgnoreCase("OK"))){
						callback.onResult(false, message);
						return;
					}
						
					//TODO: 是否有資料需要存入loginInfo?
					LoginInfo.getInstance().updateProfile(true, params);
					
					callback.onResult(true, message);
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, "");
				}
			}
		});
		
	}
	
	/****************** 
	 * API: getMemberProfile Action
	 ******************/
	public static void getMemberProfile(HashMap<String, Object> params, final ProfileCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.LOGIN_REST_URL_GET_MEMBER_PROFILE;
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", LoginInfo.getInstance().getToken());
		
		ConnectService.sendPostRequest(requestUrl, headers, params, new HttpPostRequestDelegate(){

			@Override
			public void didGetResponse(String url, String response, Header[] headerArray) {
				// TODO Auto-generated method stub
				if(response==null) {
					callback.onResult(false, null, "");
					return;
				}

				try {
					JSONObject responseObject = new JSONObject(response);
					JSONObject optObject = responseObject.getJSONObject(BHConstants.JSON_KEY_OPT);
					
					//改為OPT裡面的message
					String message = "";
					if (optObject.has(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE)){
						message = optObject.isNull(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE)? 
							"" : optObject.getString(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE);
					}
					
					if (!(optObject.getString(BHConstants.JSON_KEY_STATUS).equalsIgnoreCase("OK"))){
						callback.onResult(false, null, message);
						return;
					}
						
					//存入loginInfo?
					JSONObject data = optObject.getJSONObject(BHConstants.JSON_KEY_DATA);
					LoginInfo.getInstance().setProfile(data);
					LoginInfo.getInstance().storeInfoToPreference();
					
					callback.onResult(true, data, message);
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, null, "");
				}
			}
		});
		
	}
	
	/****************** 
	 * API: setProfile Action
	 ******************/
	public static void setProfile(HashMap<String, Object> params, final LoginCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.LOGIN_REST_URL_SET_PROFILE;
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", LoginInfo.getInstance().getToken());
		
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
					
					//改為OPT裡面的message
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
	 * API: verifyAccount Action
	 ******************/
	public static void verifyAccount(String account, String code, final LoginCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.LOGIN_REST_URL_VERIFY_ACCOUNT;
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", LoginInfo.getInstance().getToken());
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_ACCOUNT, account);
		params.put(BHConstants.PARAM_VERIFY_CODE, code);
		
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
					
					//改為OPT裡面的message
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
	 * API: openidLogin Action
	 ******************/
	public static void openidLogin(String account, String openId, String token, 
			String from, String field, Bundle extraData, 
			final LoginCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.LOGIN_REST_URL_OPENID_LOGIN;
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Cookie", LoginInfo.getInstance().getToken());
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_ACCOUNT, account);
		params.put(BHConstants.PARAM_OPEN_UID, openId);
		params.put(BHConstants.PARAM_TOKEN, token);
		params.put(BHConstants.PARAM_FROM, from);
		params.put(BHConstants.PARAM_FIELD, field);
		
		//如果是yahoo, 需多放其他資訊
		if (from.equals("y")){
			try{
				params.put(BHConstants.PARAM_TOKEN_SECRET, extraData.getString(BHConstants.PARAM_TOKEN_SECRET));
				params.put(BHConstants.PARAM_COMSUMER_SECRET, extraData.getString(BHConstants.PARAM_COMSUMER_SECRET));
				params.put(BHConstants.PARAM_COMSUMER_KEY, extraData.getString(BHConstants.PARAM_COMSUMER_KEY));
			}catch(Exception e){
				e.printStackTrace();
			}
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
					
					//改為OPT裡面的message
					String message = "";
					if (optObject.has(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE)){
						message = optObject.isNull(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE)? 
							"" : optObject.getString(BHConstants.JSON_KEY_MESSAGE_LOWER_CASE);
					}
					
					if (!(optObject.getString(BHConstants.JSON_KEY_STATUS).equalsIgnoreCase("OK"))){
						callback.onResult(false, message);
						return;
					}
					
					//save "loginSource" to loginInfo, 
					//but not save to pref until next step isLogin=true;
					LoginInfo.getInstance().setLoginBy(UserConstants.LOGIN_BY_OPEN_ID);
					
					callback.onResult(true, message);
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, "");
				}
			}
		});
		
	}
	
	
	/****************** 
	 * API: getCookie Action
	 ******************/
	public static void getCookie(final LoginCallback callback) {

		String baseUrl = (BHConstants.IS_API_TEST)? BHConstants.BASE_RC_URL : BHConstants.BASE_URL;
		String requestUrl = baseUrl + BHConstants.LOGIN_REST_URL_IS_LOGIN;
		
		HashMap<String, String> headers = new HashMap<String, String>();
		HashMap<String, Object> params = new HashMap<String, Object>();
		
		ConnectService.sendPostRequest(requestUrl, headers, params, new HttpPostRequestDelegate(){

			@Override
			public void didGetResponse(String url, String response, Header[] headerArray) {
				// TODO Auto-generated method stub
				if(response==null) {
					callback.onResult(false, "");
					return;
				}

				String cookie = "";
				for (int i=0; i<headerArray.length; i++){
					if (headerArray[i].getName().equals("Set-Cookie")){
						Log.e("header", headerArray[i].getName() + ":" +headerArray[i].getValue());
						cookie = headerArray[i].getValue();
						LoginInfo.getInstance().setToken(cookie);
						break;
					}
				}
				
				if (cookie.equals("")){
					callback.onResult(false, "no cookie");
					return;
				}
				
				callback.onResult(true, "get cookie(no saved to logininfo)");
				
			}
		});
		
	}
	
	
	/****************** 
	 * API: register GCM Token Action to sinyi when login
	 ******************/
	public static void loginGCMToken(String subscribeId, Boolean needUpdateTime, HashMap<String, Object> params, final TrackCallback callback) {

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
	 * API: get Yahoo profile
	 ******************/
	public static void getYahooProfile(String url, final RequestCallback callback) {
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		
		ConnectService.sendGetRequest(url, params, new HttpGetRequestDelegate(){

			@Override
			public void didGetResponse(String url, String response) {
				// TODO Auto-generated method stub
				if(response==null) {
					callback.onResult(false, null, "");
					return;
				}
				
				try {
					JSONObject responseObject = new JSONObject(response);
					callback.onResult(true, responseObject, "");
					
				} catch (Exception e) {
					e.printStackTrace();
					callback.onResult(false, null, "");
				}
			}
		});
		
	}
	
}
