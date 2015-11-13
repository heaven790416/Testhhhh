package com.yowoo.newbuyhouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.model.Area;
import com.yowoo.newbuyhouse.model.City;
import com.yowoo.newbuyhouse.util.FileUtils;

public class SearchInfo {

	private static SearchInfo searchInfo = null;

	public static SharedPreferences preferences;
	public static Editor preferenceEditor;

	private Context context = null;
	
	//Important Mode
	public int currentFilterMode = BHConstants.FILTER_MODE_AREA;//0:區域搜尋 1:捷運搜尋
	public int currentSearchMode = BHConstants.SEARCH_MODE_LATLNG;//0:採用目前經緯度位置資訊 1:採用目前filter的位置資訊
	
	public LatLng centerPos = new LatLng(BHConstants.DEFAULT_LATITUDE, BHConstants.DEFAULT_LONGITUDE);
	public String boundLatLngString = "";
	public float zoom = BHConstants.HOUSE_MAX_ZOOM;
	
	public ArrayList<City> cityList = new ArrayList<City>();
	//public ArrayList<Road> roadList = new ArrayList<Road>();
	public String[] mrtLocations;
	public HashMap<String, String[]> locLineHashMap = new HashMap<String, String[]>();
	public HashMap<String, String[]> lineStationHashMap = new HashMap<String, String[]>();
	public final String[] houseAreas = new String[]{"不指定","20坪以上","30坪以上","40坪以上","50坪以上","60坪以上","100坪以上"};
	public final String[] houseAreasParam = new String[]{"","20","30","40","50","60","100"};
	
	public final String[] houseUses = new String[]{"住宅","店面","辦公","廠房","別墅","倉庫","土地","其他"};
	public final String[] houseUsesParam = new String[]{"house","store","office","factory","villa","earehouse","land","other"};
	
	public final String[] advanceYears = new String[]{"不指定","1~5年","6~10年","11~20年","21~30年","31~40年","40年以上"};
	public final String[] advanceYearsParam = new String[]{"","5-down","6-10","11-20","21-30","31-40","40-up"};
	
	public final String[] advanceTypes = new String[]{"公寓","華廈","大樓","多樓層","透天厝","成屋","新成屋","預售","套房","車位","其他"};
	public final String[] advanceTypesParam = new String[]{"apartment","mansion","building","multi","townhouse","homes","newhomes","presale","flat","parking","other"};
	
	public final String[] advanceParkings = new String[]{"無車位","有車位","另租","坡道平面","機械平面","坡道機械","庭院","其他"};
	public final String[] advanceParkingsParam = new String[]{"no","yes","rent","plane","auto","mix","yard","other"};
			
	public final String[] advanceFaces = new String[]{"東","南","西","北","東南","東北","西南","西北"};
	public final String[] advanceFacesParam = new String[]{"east","south","west","north","easts","eastn","wests","westn"};
	
	public final String[] advanceInteracts = new String[]{"有互動看屋","有社區公設"};
	public final String[] advanceInteractsParam = new String[]{"vryes","cmyes"};
	
	public final String[] advanceLayouts = new String[]{"邊間","頂樓","頂樓加蓋","一樓含地下室","一樓不含地下室"};
	public final String[] advanceLayoutsParam = new String[]{"sfside","sfroof","sfroofplus","sf1fbm","sfbmexc1f"};
	
	public final String[] advanceSpecials = new String[]{"有車位","有電梯","有警衛管理","近捷運","近學校","近公園","近市場","低公設"};
	public final String[] advanceSpecialsParam = new String[]{"parking","lift","security","mrt","nearschool","neargarden","nearmarket","pingrate"};
	
	public final String[] ordersParam = new String[]{"","diff-desc","publish-desc","price-asc","price-desc","area-asc","area-desc","year-asc","year-desc"};
	
	//for area mode
	public int selectedCity = 0;
	public int selectedArea = 0;
	
	//for mrt mode
	public int selectedLocation = 0;
	public int selectedLine = 0;
	public int selectedStation = 0;
	
	//for house info
	//public int selectedHousePriceMode = 0;
    public int selectedHouseLowPrice = 0;
    public int selectedHouseHighPrice = BHConstants.MAX_PRICE;
	public int selectedHouseArea = 0;
	public HashSet<Integer> selectedHouseUses = new HashSet<Integer>();
	public String selectedHouseKeyword = "";
	
	//for advance info
	public int selectedAdvanceYear = 0;
	public String selectedAdvanceLowRoom = "";//room
	public String selectedAdvanceHighRoom = "";//room
	public HashSet<Integer> selectedAdvanceTypes = new HashSet<Integer>();
	public HashSet<Integer> selectedAdvanceParkings = new HashSet<Integer>();
	public HashSet<Integer> selectedAdvanceFaces = new HashSet<Integer>();
	public HashSet<Integer> selectedAdvanceInteracts = new HashSet<Integer>();
	public HashSet<Integer> selectedAdvanceLayouts = new HashSet<Integer>();
	public HashSet<Integer> selectedAdvanceSpecials = new HashSet<Integer>();
	public Boolean isAdvanceOpen = false;
	
	//for list order
	public int selectedOrder = 0;//default
	
	//for "has keyword, but no loc" situation
	public Boolean isForKeyword = false;
	
	public static void initialize(Context applicationContext){

		if (searchInfo == null) {
			synchronized (SearchInfo.class) {
				if (searchInfo == null) {
					searchInfo = new SearchInfo(applicationContext);
				}
			}
		}

	}

	public static SearchInfo getInstance(){
		return searchInfo;
	}

	private SearchInfo(Context applicationContext){
		this.context = applicationContext;
		preferences = PreferenceManager
				.getDefaultSharedPreferences(applicationContext);
		preferenceEditor = preferences.edit();
		
		//preload city and mrt data to searchInfo
		String cityData = FileUtils.readAssetsFile(applicationContext, "city.json");
		String mrtData = FileUtils.readAssetsFile(applicationContext, "mrt.json");
		try {
			JSONObject responseObject = new JSONObject(cityData);
			initCityData(responseObject.getJSONObject("OPT").getJSONArray("citys"));

			responseObject = new JSONObject(mrtData);
			initMRTData(responseObject.getJSONObject("OPT"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
				
	}
	
	public void initCityData(JSONArray cityJSONArray){
		try{
			ArrayList<City> cityList = new ArrayList<City>();
			
			//add default city
			City city = new City("0", context.getResources().getString(R.string.not_restrict), "0", true, 0, 0);
			cityList.add(city);
			
			for (int i=0; i<cityJSONArray.length(); i++){
				city = new City(cityJSONArray.getJSONObject(i),true);
				cityList.add(city);
			}
			
			this.cityList = cityList;
			Log.e("test", "cityList:"+cityList.size());
		}catch(JSONException e){
			e.printStackTrace();
		}
		
	}
	
	public void initMRTData(JSONObject jsonObject){
		try{
			
			//location -> line
			JSONArray locJSONArray = jsonObject.getJSONArray("mrtLocation");
			mrtLocations = new String[locJSONArray.length()+1];
			
			//先加入未指定欄位
			String notRestrictText = context.getResources().getString(R.string.not_restrict);
			mrtLocations[0] = notRestrictText;
			String temp[] = new String[]{notRestrictText};
			locLineHashMap.put(mrtLocations[0], temp);
			
			//北區中區南區
			for (int i=0; i<locJSONArray.length(); i++){
				String title = locJSONArray.getJSONObject(i).getString("title");
				mrtLocations[i+1] = title;
				
				JSONArray lineJSONArray = locJSONArray.getJSONObject(i).getJSONArray("child");
				String[] lines = new String[lineJSONArray.length()+1];
				lines[0] = notRestrictText;
				for (int j=0; j<lineJSONArray.length(); j++){
					lines[j+1] = lineJSONArray.getString(j);
				}
				locLineHashMap.put(title, lines);
			}
			
			//line -> station
			Log.e("test", "mrt.json:"+jsonObject.toString());
			JSONObject linesJSONObject = jsonObject.getJSONObject("mrtLine");
			Iterator<?> keys = linesJSONObject.keys();

			//加入未指定欄位
			String[] stations = new String[]{notRestrictText};
			this.lineStationHashMap.put(notRestrictText, stations);
			
			while( keys.hasNext() ) {
			    String key = (String)keys.next();
			    JSONArray stationJSONArray = linesJSONObject.getJSONArray(key);
			    stations = new String[stationJSONArray.length()+1];
			    stations[0] = notRestrictText;
			    for (int k=0; k<stationJSONArray.length(); k++){
			    		stations[k+1] = stationJSONArray.getString(k);
			    }
			    this.lineStationHashMap.put(key, stations);
			}
			
			
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	
	public LatLng getSelectedCityAreaLatLng(){
		
		if (selectedCity==0) 
			return new LatLng(BHConstants.DEFAULT_LATITUDE, BHConstants.DEFAULT_LONGITUDE);
		
		City city = cityList.get(this.selectedCity);
		if (selectedArea==0) return new LatLng(city.lat, city.lng);
		
		Area area = city.areas.get(this.selectedArea);
		return new LatLng(area.lat, area.lng);
	}
	
	//TODO:temp, 可讓上面的用這個
	public LatLng getSelectedCityAreaLatLng(int selectedCity, int selectedArea){
		
		if (selectedCity==0) 
			return new LatLng(BHConstants.DEFAULT_LATITUDE, BHConstants.DEFAULT_LONGITUDE);
		
		City city = cityList.get(selectedCity);
		if (selectedArea==0) return new LatLng(city.lat, city.lng);
		
		Area area = city.areas.get(selectedArea);
		return new LatLng(area.lat, area.lng);
	}
	
	public Boolean isCenterPosDefault(){
		if (centerPos.latitude!=BHConstants.DEFAULT_LATITUDE) return false;
		if (centerPos.longitude!=BHConstants.DEFAULT_LONGITUDE) return false;
		return true;
	}
	
	public String getFilterParams(Boolean addLocaionInfo, Boolean isListMode){
		String params = "";
		
		//location相關：區域/捷運
		if (addLocaionInfo){
			if (this.currentFilterMode==BHConstants.FILTER_MODE_MRT){
				params += getMRTParam(selectedLocation, selectedLine, selectedStation);
			}else{
				//區域: 只有在儲存搜尋條件＆訂閱新屋通知時會用到,
				//mapSearch不會用到，因為mapSearch會直接定位到中心點去抓左上右下點做搜尋
				params += getAreaParam(selectedCity, selectedArea);
			}
		}
		
		//house相關
		params += this.getHousePriceParam(selectedHouseLowPrice, selectedHouseHighPrice);
		params += this.getHouseAreaParam(selectedHouseArea);
		params += this.getHouseUseParam(selectedHouseUses);
		params += this.getHouseKeywordParam(selectedHouseKeyword);
		
		//advance相關
		params += this.getAdvanceYearParam(selectedAdvanceYear);
		params += this.getAdvanceRoomParam(selectedAdvanceLowRoom, selectedAdvanceHighRoom);
		params += this.getAdvanceTypeParam(selectedAdvanceTypes);
		params += this.getAdvanceParkingParam(selectedAdvanceParkings);
		params += this.getAdvanceFaceParam(selectedAdvanceFaces);
		params += this.getAdvanceInteractAndLayoutParam(selectedAdvanceInteracts, selectedAdvanceLayouts);
		params += this.getAdvanceSpecialParam(selectedAdvanceSpecials);
		
		//if isList, add order相關 
		if (isListMode){
			params += this.getOrderParam(selectedOrder);
		}
		
		return params;
	}
	
	/* 獲取各類filter params */
	public String getMRTParam(int selectedLocation, int selectedLine, int selectedStation){
		String params = "";
		if (selectedLocation==0) return "";
		if (selectedLine==0) return "";
		
		//mrt line param
		String lineKey = locLineHashMap.get(mrtLocations[selectedLocation])[selectedLine];
		params += lineKey+"-mrtline/";
		
		//mrt station param
		if (selectedStation==0) return params;
		String stationKey = lineStationHashMap.get(lineKey)[selectedStation];
		params += stationKey+"-mrt/";
		
		return params;
	}
	
	public String getAreaParam(int selectedCity, int selectedArea){
		
		//TODO
		String params = "";
		if (selectedCity==0) return "";
		
		//city param
		String cityCode = cityList.get(selectedCity).cityCode;
		params += cityCode+"-city/";
		
		//area param
		if (selectedArea==0) return params;
		String zipCode = cityList.get(selectedCity).areas.get(selectedArea).zipCode;
		params += zipCode+"-zip/";
		
		return params;
	}
	
	//單選
	public String getHouseAreaParam(int selectedHouseArea){
		String params = "";
		
		if (selectedHouseArea==0) {
			return params;
		}else{
			params += houseAreasParam[selectedHouseArea]+"-up-area/";
		}
		
		return params;
	}
	
	//多選
	public String getHouseUseParam(HashSet<Integer> selectedHouseUses){
		String params = "";
		
		if (selectedHouseUses.size()==0) return params;
		
		for (int i : selectedHouseUses) {
		    params += houseUsesParam[i]+"-";
		}
		
		return params+"use/";
	}
	
	//單選
	public String getAdvanceYearParam(int selectedAdvanceYear){
		String params = "";

		if (selectedAdvanceYear==0) {
			return params;
		}else{
			params += advanceYearsParam[selectedAdvanceYear]+"-year/";
		}

		return params;
	}
	
	//雙滾輪
	public String getAdvanceRoomParam(String selectedAdvanceLowRoom, String selectedAdvanceHighRoom){
		String params = "";

		if (selectedAdvanceLowRoom.equals("") && selectedAdvanceHighRoom.equals("")){
			return params;
		}
		
		if (!selectedAdvanceLowRoom.equals("")) {
			params += selectedAdvanceLowRoom+"-up-room/";
		}
		
		if (!selectedAdvanceHighRoom.equals("")) {
			params += selectedAdvanceHighRoom+"-down-room/";
		}

		return params;
	}
	
	//多選
	public String getAdvanceTypeParam(HashSet<Integer> selectedAdvanceTypes){
		String params = "";

		if (selectedAdvanceTypes.size()==0) return params;

		for (int i : selectedAdvanceTypes) {
			params += advanceTypesParam[i]+"-";
		}

		return params+"type/";
	}
	
	//多選
	public String getAdvanceParkingParam(HashSet<Integer> selectedAdvanceParkings){
		String params = "";

		if (selectedAdvanceParkings.size()==0) return params;

		for (int i : selectedAdvanceParkings) {
			params += advanceParkingsParam[i]+"-";
		}

		return params+"parking/";
	}
	
	//多選
	public String getAdvanceFaceParam(HashSet<Integer> selectedAdvanceFaces){
		String params = "";

		if (selectedAdvanceFaces.size()==0) return params;

		for (int i : selectedAdvanceFaces) {
			params += advanceFacesParam[i]+"-";
		}

		return params+"face/";
	}
	
	//多選:互動瀏覽＆特殊格局都屬於other
	public String getAdvanceInteractAndLayoutParam(HashSet<Integer> selectedAdvanceInteracts, HashSet<Integer> selectedAdvanceLayouts){
		String params = "";

		if ((selectedAdvanceInteracts.size()==0)&&(selectedAdvanceLayouts.size()==0)) return params;

		for (int i : selectedAdvanceInteracts) {
			params += advanceInteractsParam[i]+"-";
		}
		
		for (int i : selectedAdvanceLayouts) {
			params += advanceLayoutsParam[i]+"-";
		}

		return params+"other/";
	}
	
	//多選:物件特色（每個選項分別送一個param）
	public String getAdvanceSpecialParam(HashSet<Integer> selectedAdvanceSpecials){
		String params = "";

		if (selectedAdvanceSpecials.size()==0) return params;

		for (int i : selectedAdvanceSpecials) {
			params += ("yes-"+advanceSpecialsParam[i]+"/");
		}

		return params;
	}
	
	public String getHousePriceParam(int selectedHouseLowPrice, int selectedHouseHighPrice){
		//0-不限：空字串
		//0-highPrice: 0-highPrice-price/
		//lowPrice-不限: lowPrice-up-price/
		
		String params = "";
		
		if ((selectedHouseLowPrice==0)&&(selectedHouseHighPrice==BHConstants.MAX_PRICE)){
			return params;
		}
		
		String highText = (selectedHouseHighPrice==BHConstants.MAX_PRICE)? "up":String.valueOf(selectedHouseHighPrice);
		params += selectedHouseLowPrice+"-"+highText+"-price/";
		
		return params;
	}
	
	public String getHouseKeywordParam(String selectedHouseKeyword){
		if (selectedHouseKeyword.equals("")) return "";
		return selectedHouseKeyword+"-keyword/";
	}
	
	public String getOrderParam(int selectedOrder){
		
		return ordersParam[selectedOrder];
	}
	
	/* display params */
	public String getDisplayParams(Boolean addLocaionInfo, Boolean isListMode){
		String params = "";
		
		//location相關：區域/捷運
		if (addLocaionInfo){
			if (this.currentFilterMode==BHConstants.FILTER_MODE_MRT){
				params += getMRTDisplayParam(selectedLocation,selectedLine,selectedStation);
			}else{
				//區域: 只有在儲存搜尋條件＆訂閱新屋通知時會用到,
				//mapSearch不會用到，因為mapSearch會直接定位到中心點去抓左上右下點做搜尋
				params += getAreaDisplayParam(selectedCity, selectedArea);
			}
		}
		
		//house相關
		params += this.getHousePriceDisplayParam(selectedHouseLowPrice, selectedHouseHighPrice);
		params += this.getHouseAreaDisplayParam(selectedHouseArea);
		params += this.getHouseUseDisplayParam(selectedHouseUses);
		params += this.getHouseKeywordDisplayParam(selectedHouseKeyword);
		
		//advance相關
		params += this.getAdvanceYearDisplayParam(selectedAdvanceYear);
		params += this.getAdvanceRoomDisplayParam(selectedAdvanceLowRoom, selectedAdvanceHighRoom);
		params += this.getAdvanceTypeDisplayParam(selectedAdvanceTypes);
		params += this.getAdvanceParkingDisplayParam(selectedAdvanceParkings);
		params += this.getAdvanceFaceDisplayParam(selectedAdvanceFaces);
		params += this.getAdvanceInteractAndLayoutDisplayParam(selectedAdvanceInteracts, selectedAdvanceLayouts);
		params += this.getAdvanceSpecialDisplayParam(selectedAdvanceSpecials);
		
		//if isList, add order相關 
		if (isListMode){
			params += this.getOrderDisplayParam(selectedOrder);
		}
		
		
		if (params.equals("")){
			//沒有設任何搜尋條件的搜尋，設為
			params = "全域搜尋";
		}else{
			//如果最後面有","，處理掉
			if (params.substring(params.length()-1, params.length()).equals(",")){
				params = params.substring(0, params.length()-1);
			}
		}
		
		return params;
	}
	
	/* 獲取各類display params */
	public String getMRTDisplayParam(int selectedLocation, int selectedLine, int selectedStation){
		String params = "";
		if (selectedLocation==0) return "";
		if (selectedLine==0) return "";
		
		//mrt line param
		String lineKey = locLineHashMap.get(mrtLocations[selectedLocation])[selectedLine];
		params += lineKey+",";
		
		//mrt station param
		if (selectedStation==0) return params;
		String stationKey = lineStationHashMap.get(lineKey)[selectedStation];
		params += stationKey+",";
		
		return params;
	}
	
	public String getAreaDisplayParam(int selectedCity, int selectedArea){
		
		String params = "";
		if (selectedCity==0) return "";
		
		//city param
		String cityName = cityList.get(selectedCity).cityName;
		params += cityName+",";
		
		//area param
		if (selectedArea==0) return params;
		String areaName = cityList.get(selectedCity).areas.get(selectedArea).name;
		params += areaName+",";
		
		return params;
	}
	
	//單選
	public String getHouseAreaDisplayParam(int selectedHouseArea){
		//TODO
		String params = "";
		
		if (selectedHouseArea==0) {
			return params;
		}else{
			params += houseAreas[selectedHouseArea]+",";
		}
		
		return params;
	}
	
	//多選
	public String getHouseUseDisplayParam(HashSet<Integer> selectedHouseUses){
		String params = "";
		
		if (selectedHouseUses.size()==0) return params;
		
		for (int i : selectedHouseUses) {
		    params += houseUses[i]+",";
		}
		
		return params;
	}
	
	//單選
	public String getAdvanceYearDisplayParam(int selectedAdvanceYear){
		String params = "";

		if (selectedAdvanceYear==0) {
			return params;
		}else{
			params += advanceYears[selectedAdvanceYear]+",";
		}

		return params;
	}

	//雙滾輪
	public String getAdvanceRoomDisplayParam(String selectedAdvanceLowRoom, String selectedAdvanceHighRoom){
		String params = "";

		if (selectedAdvanceLowRoom.equals("") && selectedAdvanceHighRoom.equals("")){
			return params;
		}

		if (!selectedAdvanceLowRoom.equals("")) {
			params += selectedAdvanceLowRoom+"房以上"+",";
		}

		if (!selectedAdvanceHighRoom.equals("")) {
			params += selectedAdvanceHighRoom+"房以下"+",";
		}

		return params;
	}

	//多選
	public String getAdvanceTypeDisplayParam(HashSet<Integer> selectedAdvanceTypes){
		String params = "";

		if (selectedAdvanceTypes.size()==0) return params;

		for (int i : selectedAdvanceTypes) {
			params += advanceTypes[i]+",";
		}

		return params;
	}

	//多選
	public String getAdvanceParkingDisplayParam(HashSet<Integer> selectedAdvanceParkings){
		String params = "";

		if (selectedAdvanceParkings.size()==0) return params;

		for (int i : selectedAdvanceParkings) {
			params += advanceParkings[i]+",";
		}

		return params;
	}

	//多選
	public String getAdvanceFaceDisplayParam(HashSet<Integer> selectedAdvanceFaces){
		String params = "";

		if (selectedAdvanceFaces.size()==0) return params;

		for (int i : selectedAdvanceFaces) {
			params += advanceFaces[i]+",";
		}

		return params;
	}

	//多選:互動瀏覽＆特殊格局都屬於other
	public String getAdvanceInteractAndLayoutDisplayParam(HashSet<Integer> selectedAdvanceInteracts, HashSet<Integer> selectedAdvanceLayouts){
		String params = "";

		if ((selectedAdvanceInteracts.size()==0)&&(selectedAdvanceLayouts.size()==0)) return params;

		for (int i : selectedAdvanceInteracts) {
			params += advanceInteracts[i]+",";
		}

		for (int i : selectedAdvanceLayouts) {
			params += advanceLayouts[i]+",";
		}

		return params;
	}

	//多選:物件特色（每個選項分別送一個param）
	public String getAdvanceSpecialDisplayParam(HashSet<Integer> selectedAdvanceSpecials){
		String params = "";

		if (selectedAdvanceSpecials.size()==0) return params;

		for (int i : selectedAdvanceSpecials) {
			params += ("有"+advanceSpecials[i]+",");
		}

		return params;
	}

	public String getHousePriceDisplayParam(int selectedHouseLowPrice, int selectedHouseHighPrice){
		//0-不限：空字串
		//0-highPrice: 0-highPrice-price/
		//lowPrice-不限: lowPrice-up-price/

		String params = "";

		if ((selectedHouseLowPrice==0)&&(selectedHouseHighPrice==BHConstants.MAX_PRICE)){
			return params;
		}

		String highText = (selectedHouseHighPrice==BHConstants.MAX_PRICE)? "不限":(String.valueOf(selectedHouseHighPrice)+"萬");
		params += selectedHouseLowPrice+"萬~"+highText+",";

		return params;
	}

	public String getHouseKeywordDisplayParam(String selectedHouseKeyword){
		if (selectedHouseKeyword.equals("")) return "";
		return "關鍵字："+selectedHouseKeyword+",";
	}

	public String getOrderDisplayParam(int selectedOrder){
		return "";
		//return ordersParam[selectedOrder];
	}
	
	
	/* reset search info */
	public void clearStatus(){
		selectedCity = 0;
		selectedArea = 0;
		
		selectedLocation = 0;
		selectedLine = 0;
		selectedStation = 0;
		
		selectedHouseLowPrice = 0;
		selectedHouseHighPrice = BHConstants.MAX_PRICE;
		//selectedHousePriceMode = 0;
		
		selectedHouseArea = 0;
		selectedHouseKeyword = "";
		
		selectedAdvanceYear = 0;
		//selectedAdvanceRoom = 0;
		selectedAdvanceLowRoom = "";
		selectedAdvanceHighRoom = "";
		
		selectedHouseUses.clear();
		selectedAdvanceTypes.clear();
		selectedAdvanceParkings.clear();
		selectedAdvanceFaces.clear();
		selectedAdvanceInteracts.clear();
		selectedAdvanceLayouts.clear();
		selectedAdvanceSpecials.clear();
		
		isAdvanceOpen = false;
		
		selectedOrder = 0;
		
		isForKeyword = false;
	}
	
	/* Calculate Zoom Level */
	public float calculateZoomAndGet(){
		//根據各種mode，決定要設定何種zoom level
		float MAX_ZOOM = BHConstants.HOUSE_MAX_ZOOM;
		float tempZoom = MAX_ZOOM;
		
		if (SearchInfo.getInstance().currentSearchMode==BHConstants.SEARCH_MODE_LATLNG){
			tempZoom = MAX_ZOOM;
			Log.e("test", "startFetchBySearchMode: zoom1: "+tempZoom);
		}else if (SearchInfo.getInstance().currentSearchMode==BHConstants.SEARCH_MODE_LOCATION){
			if (SearchInfo.getInstance().selectedArea>0){
				//行政區有指定
				tempZoom = 15.0f;
				Log.e("test", "startFetchBySearchMode: zoom2: "+tempZoom);
			}else if (SearchInfo.getInstance().selectedCity>0){
				//城市有指定
				tempZoom = MAX_ZOOM;
				Log.e("test", "startFetchBySearchMode: zoom3: "+tempZoom);
			}else{
				//未指定區域
				tempZoom = MAX_ZOOM;
				Log.e("test", "startFetchBySearchMode: zoom3: "+tempZoom);
			}
		}
		
		this.zoom = tempZoom;
		return this.zoom;
	}
	
	//for google get latlng from address
	public String getAreaAddress(int selectedCity, int selectedArea){
		
		String address = "";
		if (selectedCity==0) return "";
		
		//city param
		String cityName = cityList.get(selectedCity).cityName;
		address += cityName;
		
		//area param
		if (selectedArea==0) return address;
		String areaName = cityList.get(selectedCity).areas.get(selectedArea).name;
		address += areaName;
		
		return address;
	}
	
}
