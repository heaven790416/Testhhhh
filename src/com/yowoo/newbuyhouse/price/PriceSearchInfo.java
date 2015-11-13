package com.yowoo.newbuyhouse.price;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;
import com.yowoo.newbuyhouse.BHConstants;

public class PriceSearchInfo {

	private static PriceSearchInfo searchInfo = null;

	public static SharedPreferences preferences;
	public static Editor preferenceEditor;

	private Context context = null;
	
	//Important Mode
	public int currentFilterMode = BHConstants.FILTER_MODE_AREA;//0:區域搜尋 1:捷運搜尋
	public int currentSearchMode = BHConstants.SEARCH_MODE_LATLNG;//0:採用目前經緯度位置資訊 1:採用目前filter的位置資訊
	
	public LatLng centerPos = new LatLng(BHConstants.DEFAULT_LATITUDE, BHConstants.DEFAULT_LONGITUDE);
	public String boundLatLngString = "";
	public float zoom = 13.0f;
	
	//TODO
	public String[] houseRoads = new String[]{"不指定"};//需從網路load
	
	public final String[] houseAreas = new String[]{"不指定","20坪以上","30坪以上","40坪以上","50坪以上","60坪以上","100坪以上"};
	public final String[] houseAreasParam = new String[]{"","20","30","40","50","60","100"};
	
	public final String[] houseYears = new String[]{"不指定","1~5年","6~10年","11~20年","21~30年","31~40年","40年以上"};
	public final String[] houseYearsParam = new String[]{"","5-down","6-10","11-20","21-30","31-40","40-up"};
	
	public final String[] advanceIntervals = new String[]{"不指定","近三個月","近半年","近九個月","近一年","近一年半","近兩年"};
	public final String[] advanceIntervalsParam = new String[]{"","3","6","9","12","18","24"};
	
	public final String[] advanceTypes = new String[]{"不指定","電梯大樓/華廈","無電梯公寓","套房","別墅/透天厝"};
	public final String[] advanceTypesParam = new String[]{"","building-mansion","apartment","flat","townhouse"};
	
	public final String[] advanceParkings = new String[]{"不指定","有車位","無車位"};
	public final String[] advanceParkingsParam = new String[]{"","yes","no"};
	
	public final String[] ordersParam = new String[]{"","price-asc","price-desc","area-asc","area-desc","year-asc","year-desc"};
	
	//for area mode
	public int selectedCity = 0;
	public int selectedArea = 0;
	public int selectedRoad = 0;
	public int selectedHouseArea = 0;
	public int selectedHouseYear = 0;
	
	//for key mode
	public String selectedHouseKeyword = "";
	
	//for advance info
	public int selectedAdvanceInterval = 0;
	public int selectedAdvanceType = 0;
	public int selectedAdvanceParking = 0;
	public Boolean isAdvanceOpen = false;
	
	//for list order
	public int selectedOrder = 0;//default
	
	//for "has keyword, but no loc" situation
	public Boolean isForKeyword = false;
	
	final int CHINESE_YEAR = 1911;
	
	public static void initialize(Context applicationContext){

		if (searchInfo == null) {
			synchronized (PriceSearchInfo.class) {
				if (searchInfo == null) {
					searchInfo = new PriceSearchInfo(applicationContext);
				}
			}
		}

	}

	public static PriceSearchInfo getInstance(){
		return searchInfo;
	}

	private PriceSearchInfo(Context applicationContext){
		this.context = applicationContext;
		preferences = PreferenceManager
				.getDefaultSharedPreferences(applicationContext);
		preferenceEditor = preferences.edit();
		
				
	}
	
	
	public String getFilterParams(Boolean isListMode){
		String params = "";
		
		if (this.currentFilterMode==BHConstants.FILTER_MODE_AREA){
//			if (addLocaionInfo){
//				//區域: 只有在儲存搜尋條件＆訂閱新屋通知時會用到,
//				//mapSearch不會用到，因為mapSearch會直接定位到中心點去抓左上右下點做搜尋
//				params += getAreaParam(selectedCity, selectedArea);
//			}
			
			params += this.getHouseAreaParam(selectedHouseArea);
			params += this.getAdvanceYearParam(selectedHouseYear);
			
			//advance相關
			params += this.getAdvanceIntervalParam(selectedAdvanceInterval);
			params += this.getAdvanceTypeParam(selectedAdvanceType);
			params += this.getAdvanceParkingParam(selectedAdvanceParking);
			
		}else{
			params += this.getHouseKeywordParam(selectedHouseKeyword);
		}
		
		//if isList, add order相關 
		if (isListMode){
			params += this.getOrderParam(selectedOrder);
		}
		
		return params;
	}
	
	/* 獲取各類filter params */
	
	
//	public String getAreaParam(int selectedCity, int selectedArea){
//		
//		//TODO
//		String params = "";
//		if (selectedCity==0) return "";
//		
//		//city param
//		String cityCode = cityList.get(selectedCity).cityCode;
//		params += cityCode+"-city/";
//		
//		//area param
//		if (selectedArea==0) return params;
//		String zipCode = cityList.get(selectedCity).areas.get(selectedArea).zipCode;
//		params += zipCode+"-zip/";
//		
//		return params;
//	}
	
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
	
	//單選
	public String getAdvanceYearParam(int selectedAdvanceYear){
		String params = "";

		if (selectedAdvanceYear==0) {
			return params;
		}else{
			params += houseYearsParam[selectedAdvanceYear]+"-year/";
		}

		return params;
	}
	
	//單選
	public String getAdvanceIntervalParam(int selectedAdvanceInterval){
		String params = "";

		if (selectedAdvanceInterval==0) {
			return params;
		}else{
			int monthInterval = Integer.valueOf(advanceIntervalsParam[selectedAdvanceInterval]);

			Calendar c = Calendar.getInstance();
			c.add(Calendar.MONTH, -monthInterval);
			String yearString = String.valueOf(c.get(Calendar.YEAR)-CHINESE_YEAR);
			int month = c.get(Calendar.MONTH)+1;
			String monthString = ((month>=10)? "":"0") + String.valueOf(month); 
			params += yearString+monthString+"-up-interval/";
		}

		return params;
	}
	
	//單選
	public String getAdvanceTypeParam(int selectedAdvanceTypes){
		String params = "";

		if (selectedAdvanceTypes==0) {
			return params;
		}else{
			params += advanceTypesParam[selectedAdvanceTypes]+"-type/";
		}

		return params;
	}
	
	//單選
	public String getAdvanceParkingParam(int selectedAdvanceParkings){
		String params = "";

		if (selectedAdvanceParkings==0) {
			return params;
		}else{
			params += advanceParkingsParam[selectedAdvanceParkings]+"-parking/";
		}

		return params;
	}
	
	
	public String getHouseKeywordParam(String selectedHouseKeyword){
		if (selectedHouseKeyword.equals("")) return "";
		return selectedHouseKeyword+"-keyword/";
	}
	
	public String getOrderParam(int selectedOrder){
		
		return ordersParam[selectedOrder];
	}
	
	
	/* reset search info */
	public void clearStatus(){
		selectedCity = 0;
		selectedArea = 0;
		
		selectedRoad = 0;
		houseRoads = new String[]{"不指定"};
		
		selectedHouseArea = 0;
		selectedHouseYear = 0;

		selectedHouseKeyword = "";
		
		selectedAdvanceInterval = 0;
		selectedAdvanceType = 0;
		selectedAdvanceParking = 0;
		
		isAdvanceOpen = false;		
		selectedOrder = 0;
		isForKeyword = false;
	}
	
	
	
}
