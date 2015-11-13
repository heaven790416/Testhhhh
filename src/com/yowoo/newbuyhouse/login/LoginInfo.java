package com.yowoo.newbuyhouse.login;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.yowoo.newbuyhouse.BHConstants;

public class LoginInfo {

	private static LoginInfo loginInfo = null;
	
	public static SharedPreferences pref;
	public static Editor preferenceEditor;
	

	private Boolean isLogin = false;
	public String token = "";
	public int loginBy = UserConstants.LOGIN_BY_ONE_ACCOUNT;
	public JSONObject memberJSONObject = new JSONObject();//islogin
	public JSONObject profileJSONObject = new JSONObject();//profile
	public JSONArray favJSONArray = new JSONArray();//fav houses
	public ArrayList<String> visitArrayList = new ArrayList<String>();//visit houses
	public ArrayList<JSONObject> searchArrayList = new ArrayList<JSONObject>();//search params
	
	public static void initialize(Context applicationContext){
		
		if (loginInfo == null) {
			synchronized (LoginInfo.class) {
				if (loginInfo == null) {
					loginInfo = new LoginInfo(applicationContext);
				}
			}
		}
		
	}
	
	public static LoginInfo getInstance(){
		return loginInfo;
	}
	
	private LoginInfo(Context applicationContext){
		pref = PreferenceManager
				.getDefaultSharedPreferences(applicationContext);
		preferenceEditor = pref.edit();
		
		loadInfoFromPreference();
	}
	
	private void loadInfoFromPreference(){
		isLogin = pref.getBoolean(UserConstants.KEY_IS_LOGIN, false);
		token = pref.getString(UserConstants.KEY_TOKEN, "");
		loginBy = pref.getInt(UserConstants.KEY_LOGIN_BY, UserConstants.LOGIN_BY_OPEN_ID);
		
		String memberString = pref.getString(UserConstants.KEY_MEMBER, "");
		if (!memberString.equals("")){
			try {
				memberJSONObject = new JSONObject(memberString);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		String profileString = pref.getString(UserConstants.PREF_KEY_PROFILE, "");
		if (!profileString.equals("")){
			try {
				profileJSONObject = new JSONObject(profileString);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		String favString = pref.getString(UserConstants.KEY_FAV, "");
		if (!favString.equals("")){
			try {
				favJSONArray = new JSONArray(favString);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		String visitString = pref.getString(UserConstants.KEY_VISIT, "");
		if (!visitString.equals("")){
			visitArrayList.clear();
			String[] visits = visitString.split(",");
			for (int i=0; i<visits.length; i++){
				visitArrayList.add(visits[i]);
			}
		}
		
		String searchString = pref.getString(UserConstants.KEY_SEARCH, "");
		if (!searchString.equals("")){
			try {
				JSONArray jsonArray = new JSONArray(searchString);
				
				searchArrayList.clear();
				for (int i=0; i<jsonArray.length(); i++){
					searchArrayList.add(jsonArray.getJSONObject(i));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void storeInfoToPreference(){
		preferenceEditor.putBoolean(UserConstants.KEY_IS_LOGIN, isLogin);
		preferenceEditor.putString(UserConstants.KEY_TOKEN, token);
		preferenceEditor.putInt(UserConstants.KEY_LOGIN_BY, loginBy);
		preferenceEditor.putString(UserConstants.KEY_MEMBER, memberJSONObject.toString());
		preferenceEditor.putString(UserConstants.PREF_KEY_PROFILE, profileJSONObject.toString());
		preferenceEditor.putString(UserConstants.KEY_FAV, favJSONArray.toString());
		preferenceEditor.putString(UserConstants.KEY_VISIT, arrayListToString(visitArrayList));
		preferenceEditor.putString(UserConstants.KEY_SEARCH, arrayListToJSONArray(searchArrayList).toString());
	
		preferenceEditor.commit();
	}
	
	public Boolean isLogined(){
		return isLogin;
	}
	
	public void logout(){
		isLogin = false;
		token = "";
		loginBy = UserConstants.LOGIN_BY_ONE_ACCOUNT;
		memberJSONObject = new JSONObject();
		profileJSONObject = new JSONObject();
		favJSONArray = new JSONArray();
		visitArrayList = new ArrayList<String>();
		searchArrayList = new ArrayList<JSONObject>();
		
		preferenceEditor.remove(UserConstants.KEY_IS_LOGIN);
		preferenceEditor.remove(UserConstants.KEY_TOKEN);
		preferenceEditor.remove(UserConstants.KEY_LOGIN_BY);
		preferenceEditor.remove(UserConstants.KEY_MEMBER);
		preferenceEditor.remove(UserConstants.PREF_KEY_PROFILE);
		preferenceEditor.remove(UserConstants.KEY_FAV);
		preferenceEditor.remove(UserConstants.KEY_VISIT);
		preferenceEditor.remove(UserConstants.KEY_SEARCH);
		preferenceEditor.commit();
	}
	
	public String getToken(){
		return token;
	}
	
	public void setToken(String token){
		this.token = token;
	}
	
	public int getLoginBy(){
		return loginBy;
	}
	
	public void setLoginBy(int loginBy){
		this.loginBy = loginBy;
	}
	
	public String getDisplayName(){
		try {
			return memberJSONObject.getString(UserConstants.KEY_NICK);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public void setIsLogin(Boolean isLogin){
		this.isLogin = isLogin;
	}
	
	public void setMember(JSONObject member){
		this.memberJSONObject = member;
	}
	
	public void setProfile(JSONObject profileData){
		this.profileJSONObject = profileData;
	}
	
	public int getMemberID(){
		try{
			int memberID = memberJSONObject.getInt(UserConstants.KEY_MEMBER_ID);
			return memberID;
		}catch(Exception e){
			return 0;
		}
	}
	
	public String getAccount(){
		// userID -> email -> mobile
		String account = getUserID();
		if (!account.equals("")) return account;
		
		account = getEmail();
		if (!account.equals("")) return account;
		
		return getMobile();
	}
	
	public String getUserID(){
		try{
			String userID = memberJSONObject.getString(UserConstants.KEY_USER_ID);
			if (userID==null) userID = "";
			return userID;
		}catch(Exception e){
			return "";
		}
	}
	
	public String getEmail(){
		try{
			String email = memberJSONObject.getString(UserConstants.KEY_EMAIL);
			if (email==null) email = "";
			return email;
		}catch(Exception e){
			return "";
		}
	}
	
	public String getMobile(){
		try{
			String mobile = memberJSONObject.getString(UserConstants.KEY_MOBILE);
			if (mobile==null) mobile = "";
			return mobile;
		}catch(Exception e){
			return "";
		}
	}
	
	public String getMemberStringData(String key){
		try{
			String value = memberJSONObject.getString(key);
			if (value==null) value = "";
			Log.e("test", key+":"+value);
			return value;
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}
	
	public JSONArray getMemberIdentities(){
		try {
			return memberJSONObject.getJSONArray(UserConstants.KEY_IDENTITY);
		} catch (JSONException e) {
			e.printStackTrace();
			return new JSONArray();
		}
	}
	
	/* Profile */
	public String getProfileStringData(String key){
		try{
			String value = profileJSONObject.getJSONObject(UserConstants.KEY_MEMBER_DATA).getString(key);
			if (value==null) value = "";
			if (value.equals("null")) value = "";
			Log.e("test", key+":"+value);
			return value;
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}
	
	public void setProfileStringData(String key, Object value){
		try{
			profileJSONObject.getJSONObject(UserConstants.KEY_MEMBER_DATA).put(key, value);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void updateProfile(Boolean saveToPref, HashMap<String, Object> profileParams){
		for (String key : profileParams.keySet()) {
            this.setProfileStringData(key, profileParams.get(key));
        }
		
		if (saveToPref){
			storeInfoToPreference();
		}
	}
	
	/* Track : Fav/collect */
	public void setFav(JSONArray fav){
		this.favJSONArray = fav;
	}
	
	public Boolean hasFavHouse(String checkHouseNO){
		try {
			for (int i=0; i<favJSONArray.length(); i++){
				String houseNO = favJSONArray.getJSONObject(i).getString(UserConstants.KEY_HOUSE_NO);
				if (houseNO.equals(checkHouseNO)){
					return true;
				}
			}
			
			return false;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void addFavHouse(Boolean saveToPref, JSONObject fav){
		try {
			//check if exist, no need to add, return
			String addedHouseNO = fav.getString(UserConstants.KEY_HOUSE_NO);
			if (hasFavHouse(addedHouseNO)){
				return;
			}
			
			//not exist, put to new favArray
			JSONArray newArray = new JSONArray();
			newArray.put(fav);
			for (int i=0; i<favJSONArray.length(); i++){
				newArray.put(favJSONArray.get(i));
			}
			
			favJSONArray = newArray;

			if (saveToPref){
				storeInfoToPreference();
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void removeFavHouse(Boolean saveToPref, String rmHouseNO){
		try{
			
			//由於JSONArray的remove methd在API LEVEL 19以上才可用
			//目前remove方式為直接複製一份新的並去除掉不要的
			JSONArray newFavs = new JSONArray();
			for (int i=0; i<this.favJSONArray.length(); i++){
				String houseNO = favJSONArray.getJSONObject(i).getString(UserConstants.KEY_HOUSE_NO);
				if (!houseNO.equals(rmHouseNO)){
					newFavs.put(favJSONArray.get(i));
				}
			}
			
			this.favJSONArray = newFavs;
			
			if (saveToPref){
				storeInfoToPreference();
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String getFavHouseNOs(){
		return getFavHouseNOs(0, favJSONArray.length());
	}
	
	public String getFavHouseNOs(int start, int endNext){
		String houseNOs = "";
		
		if (start>endNext-1) return "";
		endNext = (endNext<favJSONArray.length())? endNext:favJSONArray.length();
		
		try {
			for (int i=start; i<endNext; i++){
				houseNOs += favJSONArray.getJSONObject(i).getString(BHConstants.JSON_KEY_HOUSE_NO);
				if (i!= favJSONArray.length()-1)   houseNOs +=",";
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Log.e("test", "getFavHouseNOs:"+houseNOs);
		
		return houseNOs;
	}
	
	/* Track : Visit */
	public void addVisitHouse(Boolean saveToPref, String visitHouseNO){
			
		for (int i=visitArrayList.size()-1; i>=0; i--){
			String houseNO = visitArrayList.get(i);
			if (houseNO.equals(visitHouseNO)){
				visitArrayList.remove(i);
			}
		}

		visitArrayList.add(0, visitHouseNO);

		Log.e("test", "visit2:"+visitArrayList.toString());

		while(visitArrayList.size()>UserConstants.MAX_VISIT_HOUSE){
			visitArrayList.remove(UserConstants.MAX_VISIT_HOUSE);
		}

		Log.e("test", "visit3:"+visitArrayList.toString());

		if (saveToPref){
			storeInfoToPreference();
		}

	}
	
	public String getVisitHouseNOs(){
		return arrayListToString(visitArrayList);
		
	}
	
//	public String getVisitHouseNO(int index){
//		String houseNO = "";
//		
//		if (index>=visitJSONArray.length()){
//			return houseNO;
//		}
//		
//		try {
//			houseNO = visitJSONArray.getString(index);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		
//		return houseNO;
//	}
	
	/* Search */
	public void addSearchParam(Boolean saveToPref, String filterParams, String displayParams){
		try {

			for (int i=searchArrayList.size()-1; i>=0; i--){
				String tempParams = searchArrayList.get(i).getString(UserConstants.KEY_FILTER_PARAMS);
				if (tempParams.equals(filterParams)){
					searchArrayList.remove(i);
				}
			}

			JSONObject newSearchObject = new JSONObject();
			newSearchObject.put(UserConstants.KEY_FILTER_PARAMS, filterParams);
			newSearchObject.put(UserConstants.KEY_DISPLAY_PARAMS, displayParams);
			searchArrayList.add(0, newSearchObject);

			Log.e("test", "search2:"+searchArrayList.toString());

			while(searchArrayList.size()>UserConstants.MAX_SEARCH_PAREM){
				searchArrayList.remove(UserConstants.MAX_SEARCH_PAREM);
			}

			Log.e("test", "search3:"+searchArrayList.toString());

			if (saveToPref){
				storeInfoToPreference();
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String getSearchFilterParams(int index){
		String params = "";
		
		if (index>=searchArrayList.size()){
			return params;
		}
		
		try {
			params = searchArrayList.get(index).getString(UserConstants.KEY_FILTER_PARAMS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return params;
	}
	
	/* Util : 
	 * return a new jsonArray, 
	 * which copy elements of jsonArray from startIndex to endNextIndex-1 
	 */
	public JSONArray copyJSONArray(JSONArray jsonArray, int startIndex, int endNextIndex) throws JSONException{

		if (endNextIndex>=jsonArray.length()){
			return jsonArray;
		}

		JSONArray newArray = new JSONArray();
		for (int i=0; i<endNextIndex; i++){
			newArray.put(jsonArray.get(i));
		}

		return newArray;
	}
	
	
	public String arrayListToString(ArrayList<String> array){
		String result = "";

		for (int i=0; i<array.size(); i++){
			result += array.get(i);
			if (i!= array.size()-1)   result +=",";
		}

		return result;
	}
		
	public JSONArray arrayListToJSONArray(ArrayList<JSONObject> array){
		JSONArray newJSONArray = new JSONArray();
		for (int i=0; i<array.size(); i++){
			newJSONArray.put(array.get(i));
		}

		return newJSONArray;
	}
}
