package com.yowoo.newbuyhouse.price;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.BaseActivity;
import com.yowoo.newbuyhouse.SearchInfo;
import com.yowoo.newbuyhouse.Singleton;
import com.yowoo.newbuyhouse.network.PriceService;
import com.yowoo.newbuyhouse.network.PriceService.LatLngCallback;
import com.yowoo.newbuyhouse.network.SinyiService;
import com.yowoo.newbuyhouse.network.SinyiService.RoadCallback;
import com.yowoo.newbuyhouse.view.CityWheelView;
import com.yowoo.newbuyhouse.view.CityWheelView.WheelListener;
import com.yowoo.newbuyhouse.view.SingleChooseRow;
import com.yowoo.newbuyhouse.view.SingleWheelView;
import com.yowoo.newbuyhouse.view.SingleWheelView.SingleWheelListener;
import com.yowoo.newbuyhouse.view.TitleRow;

public class PriceFilterActivity extends BaseActivity{

	Button areaTabButton, keyTabButton;
	LinearLayout areaContainer, keyContainer;
	Button clearButton, searchButton;
	SingleChooseRow cityRow, areaRow, roadRow;
	SingleChooseRow houseAreaRow, houseYearRow, advanceIntervalRow, advanceTypeRow, advanceParkingRow;
	RelativeLayout shadow;
	LinearLayout advanceItemContainer;
	TitleRow advanceTitleRow;
	EditText keywordEditText;
	ScrollView filterScrollView;
	
	public int currentFilterMode = 0;
	//for area mode
	public int selectedCity = 0;
	public int selectedArea = 0;
	public int selectedRoad = 0;
	public String[] houseRoads = new String[]{"不指定"};
	public int selectedHouseArea = 0;
	public int selectedHouseYear = 0;
	
	//for key mode
	//public String selectedHouseKeyword = "";
	
	//for advance info
	public int selectedAdvanceInterval = 0;
	public int selectedAdvanceType = 0;
	public int selectedAdvanceParking = 0;
	public Boolean isAdvanceOpen = false;
	
	//wheel
	CityWheelView areaWheelView;
	SingleWheelView singleWheelView;
	
	//switch
	ImageView switchImageView;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_price_filter);

        //set toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_selector);
        toolbar.setTitleTextColor(getResources().getColor(R.color.title_text_color));
        TextView mTitleTextView = (TextView)toolbar.findViewById(R.id.mTitleTextView);
        mTitleTextView.setText(getString(R.string.price_filter));
        mTitleTextView.setVisibility(View.VISIBLE);
        ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("");
		
        shadow = (RelativeLayout) this.findViewById(R.id.shadow);
        
        areaTabButton = (Button) this.findViewById(R.id.areaTabButton);
        keyTabButton = (Button) this.findViewById(R.id.keyTabButton);
        
        areaContainer = (LinearLayout) this.findViewById(R.id.areaContainer);
        keyContainer = (LinearLayout) this.findViewById(R.id.keyContainer);
        
        clearButton = (Button) this.findViewById(R.id.clearButton);
        searchButton = (Button) this.findViewById(R.id.searchButton);
        
        areaWheelView = (CityWheelView) this.findViewById(R.id.aeraWheelView);
        singleWheelView = (SingleWheelView) this.findViewById(R.id.singleWheelView);
        
        cityRow = (SingleChooseRow) this.findViewById(R.id.cityRow);//縣市
        areaRow = (SingleChooseRow) this.findViewById(R.id.areaRow);//行政區
        roadRow = (SingleChooseRow) this.findViewById(R.id.roadRow);//行政區
        houseAreaRow = (SingleChooseRow) this.findViewById(R.id.houseAreaRow);//坪數
        houseYearRow = (SingleChooseRow) this.findViewById(R.id.houseYearRow);//屋齡
        
        //houseKeywordRow = (KeywordRow) this.findViewById(R.id.houseKeywordRow);//關鍵字
        keywordEditText = (EditText) this.findViewById(R.id.keywordEditText);//關鍵字
        
        advanceIntervalRow = (SingleChooseRow) this.findViewById(R.id.advanceIntervalRow);//期間
        advanceTypeRow = (SingleChooseRow) this.findViewById(R.id.advanceTypeRow);//類型
        advanceParkingRow = (SingleChooseRow) this.findViewById(R.id.advanceParkingRow);//車位
        
        advanceTitleRow = (TitleRow) this.findViewById(R.id.advanceTitleRow);
        advanceItemContainer = (LinearLayout) this.findViewById(R.id.advanceItemContainer);
        
        filterScrollView = (ScrollView) this.findViewById(R.id.filterScrollView);
        
        reloadSearchInfoStatus();
        	
        //reload views
        reloadViews();

        //reload switch
        switchImageView = (ImageView) this.findViewById(R.id.switchImageView);
        Boolean switchIsOpen = Singleton.preferences.getBoolean(BHConstants.PREF_PRICE_SWITCH, false);
        switchImageView.setSelected(switchIsOpen);
        
        //set listener
        setListener();
        
	}
	
	private void reloadViews(){
		//reload tab & location顯示
		reloadLocationPartByTab();
		
		//reload區域資訊
		reloadAreaViews();
		
		//reload捷運資訊
		reloadMRTViews();
		
		//reload路街資訊
		reloadRoadView();
		
		//reload房屋資訊
		reloadHouseViews();
		
		//reload進階資訊
		reloadAdvanceViews();
	}
	
	private void reloadLocationPartByTab(){
		//location分為：「區域」＆「捷運」
		if (currentFilterMode==BHConstants.FILTER_MODE_AREA){
			areaContainer.setVisibility(View.VISIBLE);
			keyContainer.setVisibility(View.GONE);
			
			this.areaTabButton.setSelected(true);
			this.areaTabButton.setTextColor(getResources().getColor(R.color.filter_green_color));
			this.keyTabButton.setSelected(false);
			this.keyTabButton.setTextColor(getResources().getColor(R.color.filter_tab_text_color));
		}else{
			areaContainer.setVisibility(View.GONE);
			keyContainer.setVisibility(View.VISIBLE);
			
			this.areaTabButton.setSelected(false);
			this.areaTabButton.setTextColor(getResources().getColor(R.color.filter_tab_text_color));
			this.keyTabButton.setSelected(true);
			this.keyTabButton.setTextColor(getResources().getColor(R.color.filter_green_color));
		}
	}
	
	private void reloadAreaViews(){
		cityRow.setSelectedText(SearchInfo.getInstance().cityList.get(this.selectedCity).cityName);
		
		String areaName = SearchInfo.getInstance().cityList.get(this.selectedCity).areas.get(this.selectedArea).name;
		areaRow.setSelectedText(areaName);
	}
	
	private void reloadMRTViews(){
		//TODO
	}
	
	private void reloadRoadView(){
		if (selectedRoad<houseRoads.length){
			roadRow.setSelectedText(houseRoads[selectedRoad]);
		}else{
			roadRow.setSelectedText("不指定");
		}
	}
	
	private void reloadHouseViews(){
		//TODO: road
		
		//坪數
		String houseArea = PriceSearchInfo.getInstance().houseAreas[this.selectedHouseArea];
		this.houseAreaRow.setSelectedText(houseArea);
		//屋齡
		String year = PriceSearchInfo.getInstance().houseYears[this.selectedHouseYear];
		this.houseYearRow.setSelectedText(year);
		
	}
	
	private void reloadAdvanceViews(){
		
		//advance is open or close 
		reloadAdvanceOpenClose(this.isAdvanceOpen);
		
		//期間
		String interval = PriceSearchInfo.getInstance().advanceIntervals[this.selectedAdvanceInterval];
		this.advanceIntervalRow.setSelectedText(interval);
				
		//類型
		String type = PriceSearchInfo.getInstance().advanceTypes[this.selectedAdvanceType];
		this.advanceTypeRow.setSelectedText(type);
		
		//車位
		String parking = PriceSearchInfo.getInstance().advanceParkings[this.selectedAdvanceParking];
		this.advanceParkingRow.setSelectedText(parking);
	}
	
	private void reloadAdvanceOpenClose(Boolean isOpen){
		this.advanceTitleRow.reloadCell(isOpen);
		if (!isOpen){
			this.advanceItemContainer.setVisibility(View.GONE);
		}else{
			this.advanceItemContainer.setVisibility(View.VISIBLE);
			this.scrollToViewBottom(advanceItemContainer);
			//TODO: 需關掉所有openedRow.
			closeAdvanceRows();
		}
	}
	
	private void fetchRoads(int selectedCity, int selectedArea){
		
		if (selectedCity==0||selectedArea==0) return;
		
		String zipCode = SearchInfo.getInstance().cityList.get(selectedCity).areas.get(selectedArea).zipCode;
		
		SinyiService.getRoad(zipCode, new RoadCallback(){
			@Override
			public void onResult(boolean success, ArrayList<String> roads) {
				if (success){
					String[] roadArray = new String[roads.size()+1];
					roadArray[0] = "不指定";
					for (int i=0; i<roads.size(); i++){
						roadArray[i+1] = roads.get(i);
					}
					
					houseRoads = roadArray;
					reloadRoadView();
					
					showRoadWheel();
				}else{
					reloadRoadView();
				}
			}
		});
	}
	
	private void setListener(){
		areaTabButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				currentFilterMode = BHConstants.FILTER_MODE_AREA;
				reloadLocationPartByTab();
			}
		});
		
		keyTabButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				currentFilterMode = BHConstants.FILTER_MODE_MRT;
				reloadLocationPartByTab();
			}
		});
		
		View.OnClickListener areaRowListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (areaWheelView.getVisibility()==View.GONE){
					hideAllWheels();
					shadow.setVisibility(View.VISIBLE);
					areaWheelView.reloadWheelsToSelected(selectedCity, selectedArea);
					areaWheelView.setVisibility(View.VISIBLE);
				}
			}
		};
		
		areaRow.setOnClickListener(areaRowListener);
		cityRow.setOnClickListener(areaRowListener);
		
		areaWheelView.setWheelListener(new WheelListener(){
			@Override
			public void onClickOk(int cityIndex, int areaIndex) {
				if ((selectedCity!=cityIndex)||(selectedArea!=areaIndex)){
					//reset road
					selectedRoad = 0;
					reloadRoadView();
				}
				
				selectedCity = cityIndex;
				selectedArea = areaIndex;
				
				reloadAreaViews();
				shadow.setVisibility(View.GONE);
			}

			@Override
			public void onClickCancel() {
				shadow.setVisibility(View.GONE);
			}
		});
		
		//TODO: roadRow
		this.roadRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (selectedCity==0||selectedArea==0){
					showToast(R.string.please_select_area);
					return;
				}
				
				//get zipcode of area
				fetchRoads(selectedCity, selectedArea);
				
			}
		});
		
		houseAreaRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shadow.setVisibility(View.VISIBLE);
				hideAllWheels();
				singleWheelView.initWheels(PriceSearchInfo.getInstance().houseAreas, selectedHouseArea);
				singleWheelView.setVisibility(View.VISIBLE);
				
				singleWheelView.setWheelListener(new SingleWheelListener(){
					@Override
					public void onClickOk(int selectedIndex) {
						selectedHouseArea = selectedIndex;
						reloadHouseViews();
						shadow.setVisibility(View.GONE);
					}

					@Override
					public void onClickCancel() {
						shadow.setVisibility(View.GONE);
					}
				});
			}
		});
		
		houseYearRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shadow.setVisibility(View.VISIBLE);
				hideAllWheels();
				singleWheelView.initWheels(PriceSearchInfo.getInstance().houseYears, selectedHouseYear);
				singleWheelView.setVisibility(View.VISIBLE);
				
				singleWheelView.setWheelListener(new SingleWheelListener(){
					@Override
					public void onClickOk(int selectedIndex) {
						selectedHouseYear = selectedIndex;
						reloadHouseViews();
						shadow.setVisibility(View.GONE);
					}

					@Override
					public void onClickCancel() {
						shadow.setVisibility(View.GONE);
					}
				});
			}
		});
		
		advanceIntervalRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shadow.setVisibility(View.VISIBLE);
				hideAllWheels();
				singleWheelView.initWheels(PriceSearchInfo.getInstance().advanceIntervals, selectedAdvanceInterval);
				singleWheelView.setVisibility(View.VISIBLE);
				
				singleWheelView.setWheelListener(new SingleWheelListener(){
					@Override
					public void onClickOk(int selectedIndex) {
						selectedAdvanceInterval = selectedIndex;
						reloadAdvanceViews();
						shadow.setVisibility(View.GONE);
					}

					@Override
					public void onClickCancel() {
						shadow.setVisibility(View.GONE);
					}
				});
			}
		});
		
		advanceTypeRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shadow.setVisibility(View.VISIBLE);
				hideAllWheels();
				singleWheelView.initWheels(PriceSearchInfo.getInstance().advanceTypes, selectedAdvanceType);
				singleWheelView.setVisibility(View.VISIBLE);
				
				singleWheelView.setWheelListener(new SingleWheelListener(){
					@Override
					public void onClickOk(int selectedIndex) {
						selectedAdvanceType = selectedIndex;
						reloadAdvanceViews();
						shadow.setVisibility(View.GONE);
					}

					@Override
					public void onClickCancel() {
						shadow.setVisibility(View.GONE);
					}
				});
			}
		});
		
		advanceParkingRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shadow.setVisibility(View.VISIBLE);
				hideAllWheels();
				singleWheelView.initWheels(PriceSearchInfo.getInstance().advanceParkings, selectedAdvanceParking);
				singleWheelView.setVisibility(View.VISIBLE);
				
				singleWheelView.setWheelListener(new SingleWheelListener(){
					@Override
					public void onClickOk(int selectedIndex) {
						selectedAdvanceParking = selectedIndex;
						reloadAdvanceViews();
						shadow.setVisibility(View.GONE);
					}

					@Override
					public void onClickCancel() {
						shadow.setVisibility(View.GONE);
					}
				});
			}
		});
		
		searchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (currentFilterMode==BHConstants.FILTER_MODE_MRT){
					if (keywordEditText.getText().toString().equals("")){
						showToast(getString(R.string.price_keyword_hint));
						return;
					}
				}
				
				hideKeyboard();
				
				//save all status to PriceSearchInfo
				PriceSearchInfo.getInstance().selectedCity = selectedCity;
				PriceSearchInfo.getInstance().selectedArea = selectedArea;
				PriceSearchInfo.getInstance().selectedRoad = selectedRoad;
				PriceSearchInfo.getInstance().houseRoads = houseRoads;
				
				PriceSearchInfo.getInstance().selectedHouseArea = selectedHouseArea;
				PriceSearchInfo.getInstance().selectedHouseYear = selectedHouseYear;
				
				PriceSearchInfo.getInstance().selectedHouseKeyword = keywordEditText.getText().toString();	
				
				PriceSearchInfo.getInstance().selectedAdvanceInterval = selectedAdvanceInterval;
				PriceSearchInfo.getInstance().selectedAdvanceType = selectedAdvanceType;
				PriceSearchInfo.getInstance().selectedAdvanceParking = selectedAdvanceParking;

				//前往搜尋
				
				if (currentFilterMode==BHConstants.FILTER_MODE_AREA){
					//前往map
					PriceSearchInfo.getInstance().currentFilterMode = BHConstants.FILTER_MODE_AREA;
					
					
					if ((selectedCity==0)&&(selectedArea==0)){
						//區域搜尋沒有設定位置條件，searchMode為經緯度位置
						//center設為當前位置
						PriceSearchInfo.getInstance().centerPos = getMyCurrentLocation();
						PriceSearchInfo.getInstance().currentSearchMode = BHConstants.SEARCH_MODE_LATLNG;
						Log.e("test", "HouseFilter: centerPos:設為當前位置");
						
						Intent intent = new Intent();
						intent.setAction(BHConstants.BROADCAST_PRICE_SEARCH_REFRESH_MAP);
						LocalBroadcastManager.getInstance(PriceFilterActivity.this).sendBroadcast(intent);
						
						finish();
					}else{
						//區域搜尋有設定位置條件，searchMode為區域位置
						//center設為此area的定點經緯度
						
						final int selectedCity = PriceSearchInfo.getInstance().selectedCity;
						final int selectedArea = PriceSearchInfo.getInstance().selectedArea;
						PriceSearchInfo.getInstance().currentSearchMode = BHConstants.SEARCH_MODE_LOCATION;
						
						//判斷縣市行政區 or 路街 經緯度
						if ((selectedRoad!=0)&&(selectedRoad<houseRoads.length)){
							//有選擇路街：pre-fetch路街keyword的第一筆資料經緯度
							String areaAddr = SearchInfo.getInstance().getAreaAddress(selectedCity, selectedArea);
							String road = houseRoads[selectedRoad];
							LatLng pos = fetchRoadLatLng(areaAddr+road);
							if (pos!=null){
								//有抓到路街經緯度
								Log.e("test", "PriceFilter: centerPos:設為目前選擇路街的位置");
								PriceSearchInfo.getInstance().centerPos = pos;
							}else{
								//抓取縣市＆行政區經緯度
								Log.e("test", "PriceFilter: centerPos:設為目前選擇行政區的位置");
								PriceSearchInfo.getInstance().centerPos = SearchInfo.getInstance().getSelectedCityAreaLatLng(selectedCity, selectedArea);
							}
							
							Intent intent = new Intent();
							intent.setAction(BHConstants.BROADCAST_PRICE_SEARCH_REFRESH_MAP);
							LocalBroadcastManager.getInstance(PriceFilterActivity.this).sendBroadcast(intent);
							
							finish();
						}else{
							//抓取縣市＆行政區經緯度
							Log.e("test", "PriceFilter: centerPos:設為目前選擇行政區的位置");
							PriceSearchInfo.getInstance().centerPos = SearchInfo.getInstance().getSelectedCityAreaLatLng(selectedCity, selectedArea);
							Intent intent = new Intent();
							intent.setAction(BHConstants.BROADCAST_PRICE_SEARCH_REFRESH_MAP);
							LocalBroadcastManager.getInstance(PriceFilterActivity.this).sendBroadcast(intent);
							
							finish();
						}
						
					}
					
				}else{
					//前往list
					PriceSearchInfo.getInstance().currentFilterMode = BHConstants.FILTER_MODE_MRT;
					PriceSearchInfo.getInstance().currentSearchMode=BHConstants.SEARCH_MODE_LOCATION;
					
					Intent intent = new Intent();
					intent.setAction(BHConstants.BROADCAST_PRICE_SEARCH_REFRESH_LIST);
					LocalBroadcastManager.getInstance(PriceFilterActivity.this).sendBroadcast(intent);
					
					finish();
				}
				
			}
		});
		
		clearButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PriceSearchInfo.getInstance().clearStatus();
				reloadSearchInfoStatus();
				reloadViews();
			}
		});
		
		shadow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shadow.setVisibility(View.GONE);
				hideAllWheels();
			}
		});
		
		advanceTitleRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isAdvanceOpen = !isAdvanceOpen;
				reloadAdvanceOpenClose(isAdvanceOpen);
				PriceSearchInfo.getInstance().isAdvanceOpen = isAdvanceOpen;
			}
		});
		
		keywordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus){
					scrollToViewBottom(keywordEditText);
				}
			}
		});
		
		switchImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switchImageView.setSelected(!switchImageView.isSelected());
				Singleton.preferenceEditor.putBoolean(BHConstants.PREF_PRICE_SWITCH, switchImageView.isSelected()).commit();
			}
		});
	}
	
	/* filter */
	private void reloadSearchInfoStatus(){
		//load previous status
        this.currentFilterMode = PriceSearchInfo.getInstance().currentFilterMode;
        this.selectedCity = PriceSearchInfo.getInstance().selectedCity;
        this.selectedArea = PriceSearchInfo.getInstance().selectedArea;
        this.selectedRoad = PriceSearchInfo.getInstance().selectedRoad;
        this.houseRoads = PriceSearchInfo.getInstance().houseRoads;
        this.selectedHouseArea = PriceSearchInfo.getInstance().selectedHouseArea;
        this.selectedHouseYear = PriceSearchInfo.getInstance().selectedHouseYear;
        
        keywordEditText.setText(PriceSearchInfo.getInstance().selectedHouseKeyword);
        
        this.isAdvanceOpen = PriceSearchInfo.getInstance().isAdvanceOpen;
        
        this.selectedAdvanceInterval = PriceSearchInfo.getInstance().selectedAdvanceInterval;
        this.selectedAdvanceType = PriceSearchInfo.getInstance().selectedAdvanceType;
        this.selectedAdvanceParking = PriceSearchInfo.getInstance().selectedAdvanceParking;
        
	}
	
	
	private void hideAllWheels(){
		areaWheelView.setVisibility(View.GONE);
        singleWheelView.setVisibility(View.GONE);
	}
	
	/* Scroll To */
	private void scrollToViewBottom(final View view){
		new Handler().post(new Runnable() {
            @Override
            public void run() {
            		filterScrollView.smoothScrollTo(0, view.getBottom());
            }
        });
	}
	
	private void closeAdvanceRows(){
		//this.advanceTypeRow.close();
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.main, menu);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home){
			finish();
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void showRoadWheel(){
		shadow.setVisibility(View.VISIBLE);
		hideAllWheels();
		singleWheelView.initWheels(houseRoads, selectedRoad);
		singleWheelView.setVisibility(View.VISIBLE);
		
		singleWheelView.setWheelListener(new SingleWheelListener(){
			@Override
			public void onClickOk(int selectedIndex) {
				selectedRoad = selectedIndex;
				reloadRoadView();
				shadow.setVisibility(View.GONE);
				
			}

			@Override
			public void onClickCancel() {
				shadow.setVisibility(View.GONE);
			}
		});
	}
	
//	private void fetchRoadLatLng(String zipCode, String keyword, final LatLngCallback callback){
//		String filterParams = zipCode+"-zip/"+keyword+"-keyword/";
//		
//		PriceService.getPriceList(1, 10, filterParams, "", new PriceListPageCallback(){
//			@Override
//			public void onResult(boolean success, ArrayList<Price> prices,
//					int total, int page, int totalPage) {
//				if ((success)&&(prices.size()>0)){
//					float lat = prices.get(0).getFloatData(BHConstants.JSON_KEY_LAT);
//					float lng = prices.get(0).getFloatData(BHConstants.JSON_KEY_LNG);
//					if ((lat!=0.0f)&&(lng!=0.0f)){
//						callback.onResult(true, new LatLng(lat, lng));
//						return;
//					}
//				}
//				
//				callback.onResult(false, null);
//			}
//		});
//	}
	
	private LatLng fetchRoadLatLng(String strAddress){
		Geocoder coder = new Geocoder(this);
		List<Address> address;

		try {
			address = coder.getFromLocationName(strAddress,5);
			Address location = address.get(0);
			location.getLatitude();
			location.getLongitude();
			return new LatLng(location.getLatitude(), location.getLongitude());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
}
