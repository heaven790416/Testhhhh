package com.yowoo.newbuyhouse.house;

import java.util.HashSet;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.BaseActivity;
import com.yowoo.newbuyhouse.SearchInfo;
import com.yowoo.newbuyhouse.Singleton;
import com.yowoo.newbuyhouse.login.LoginInfo;
import com.yowoo.newbuyhouse.network.TrackService;
import com.yowoo.newbuyhouse.network.TrackService.TrackCallback;
import com.yowoo.newbuyhouse.view.CityWheelView;
import com.yowoo.newbuyhouse.view.CityWheelView.WheelListener;
import com.yowoo.newbuyhouse.view.KeywordRow;
import com.yowoo.newbuyhouse.view.MRTWheelView;
import com.yowoo.newbuyhouse.view.MRTWheelView.MRTWheelListener;
import com.yowoo.newbuyhouse.view.MultiChooseRow;
import com.yowoo.newbuyhouse.view.MultiChooseRow.MultiChooseListener;
import com.yowoo.newbuyhouse.view.PriceRow;
import com.yowoo.newbuyhouse.view.PriceRow.PriceRowListener;
import com.yowoo.newbuyhouse.view.PriceWheelView;
import com.yowoo.newbuyhouse.view.PriceWheelView.PriceWheelListener;
import com.yowoo.newbuyhouse.view.RoomWheelView;
import com.yowoo.newbuyhouse.view.RoomWheelView.RoomWheelListener;
import com.yowoo.newbuyhouse.view.SingleChooseRow;
import com.yowoo.newbuyhouse.view.SingleWheelView;
import com.yowoo.newbuyhouse.view.SingleWheelView.SingleWheelListener;
import com.yowoo.newbuyhouse.view.TitleRow;

public class HouseFilterActivity extends BaseActivity{

	Button areaTabButton, mrtTabButton;
	LinearLayout areaContainer, mrtContainer;
	Button clearButton, searchButton;
	SingleChooseRow cityRow, areaRow;
	SingleChooseRow mrtLocationRow, mrtLineRow, mrtStationRow;
	SingleChooseRow houseAreaRow, advanceYearRow, advanceRoomRow;
	MultiChooseRow houseUseRow, advanceTypeRow, advanceParkingRow, advanceFaceRow; 
	MultiChooseRow advanceInteractRow, advanceLayoutRow, advanceSpecialRow;
	PriceRow housePriceRow;
	RelativeLayout shadow;
	LinearLayout advanceItemContainer;
	TitleRow advanceTitleRow;
	KeywordRow houseKeywordRow;
	ScrollView filterScrollView;
	
	public int currentFilterMode = 0;
	//for area mode
	public int selectedCity = 0;
	public int selectedArea = 0;
	//for mrt mode
	public int selectedLocation = 0;
	public int selectedLine = 0;
	public int selectedStation = 0;
	//for house info
	public int selectedHouseLowPrice = 0;
	public int selectedHouseHighPrice = 10000;//萬
	public int selectedHouseArea = 0;
	public HashSet<Integer> selectedHouseUses = new HashSet<Integer>();
	//public String selectedHouseKeyword = "";
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
	
	//wheel
	CityWheelView areaWheelView;
	MRTWheelView mrtWheelView;
	SingleWheelView singleWheelView;
	PriceWheelView priceWheelView;
	RoomWheelView roomWheelView;
	
	//switch
	ImageView switchImageView;
		
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_house_filter);

        //set toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_selector);
        toolbar.setTitleTextColor(getResources().getColor(R.color.title_text_color));
        TextView mTitleTextView = (TextView)toolbar.findViewById(R.id.mTitleTextView);
        mTitleTextView.setText(getString(R.string.house_filter));
        mTitleTextView.setVisibility(View.VISIBLE);
        ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("");
		
        shadow = (RelativeLayout) this.findViewById(R.id.shadow);
        
        areaTabButton = (Button) this.findViewById(R.id.areaTabButton);
        mrtTabButton = (Button) this.findViewById(R.id.mrtTabButton);
        
        areaContainer = (LinearLayout) this.findViewById(R.id.areaContainer);
        mrtContainer = (LinearLayout) this.findViewById(R.id.mrtContainer);
        
        clearButton = (Button) this.findViewById(R.id.clearButton);
        searchButton = (Button) this.findViewById(R.id.searchButton);
        
        areaWheelView = (CityWheelView) this.findViewById(R.id.aeraWheelView);
        mrtWheelView = (MRTWheelView) this.findViewById(R.id.mrtWheelView);
        singleWheelView = (SingleWheelView) this.findViewById(R.id.singleWheelView);
        priceWheelView = (PriceWheelView) this.findViewById(R.id.priceWheelView);
        roomWheelView = (RoomWheelView) this.findViewById(R.id.roomWheelView);
        
        cityRow = (SingleChooseRow) this.findViewById(R.id.cityRow);//縣市
        areaRow = (SingleChooseRow) this.findViewById(R.id.areaRow);//行政區
        
        mrtLocationRow = (SingleChooseRow) this.findViewById(R.id.mrtLocationRow);//捷運區域
        mrtLineRow = (SingleChooseRow) this.findViewById(R.id.mrtLineRow);//捷運主線
        mrtStationRow = (SingleChooseRow) this.findViewById(R.id.mrtStationRow);//捷運站
        
        housePriceRow = (PriceRow) this.findViewById(R.id.housePriceRow);//價格
        houseAreaRow = (SingleChooseRow) this.findViewById(R.id.houseAreaRow);//坪數
        houseUseRow = (MultiChooseRow) this.findViewById(R.id.houseUseRow);//用途
        houseKeywordRow = (KeywordRow) this.findViewById(R.id.houseKeywordRow);//關鍵字
        
        advanceYearRow = (SingleChooseRow) this.findViewById(R.id.advanceYearRow);//屋齡
        advanceRoomRow = (SingleChooseRow) this.findViewById(R.id.advanceRoomRow);//房數
        advanceTypeRow = (MultiChooseRow) this.findViewById(R.id.advanceTypeRow);//類型
        advanceParkingRow = (MultiChooseRow) this.findViewById(R.id.advanceParkingRow);//車位
        advanceFaceRow = (MultiChooseRow) this.findViewById(R.id.advanceFaceRow);//朝向
        advanceInteractRow = (MultiChooseRow) this.findViewById(R.id.advanceInteractRow);//互動瀏覽
        advanceLayoutRow = (MultiChooseRow) this.findViewById(R.id.advanceLayoutRow);//特殊格局
        advanceSpecialRow = (MultiChooseRow) this.findViewById(R.id.advanceSpecialRow);//物件特色
        
        advanceTitleRow = (TitleRow) this.findViewById(R.id.advanceTitleRow);
        advanceItemContainer = (LinearLayout) this.findViewById(R.id.advanceItemContainer);
        
        filterScrollView = (ScrollView) this.findViewById(R.id.filterScrollView);
        
        reloadSearchInfoStatus();
        	
        //initial multi choose row
        this.houseUseRow.initItems(SearchInfo.getInstance().houseUses, this.selectedHouseUses);
        this.advanceTypeRow.initItems(SearchInfo.getInstance().advanceTypes, this.selectedAdvanceTypes);
        this.advanceParkingRow.initItems(SearchInfo.getInstance().advanceParkings, this.selectedAdvanceParkings);
        this.advanceFaceRow.initItems(SearchInfo.getInstance().advanceFaces, this.selectedAdvanceFaces);
        this.advanceInteractRow.initItems(SearchInfo.getInstance().advanceInteracts, this.selectedAdvanceInteracts);
        this.advanceLayoutRow.initItems(SearchInfo.getInstance().advanceLayouts, this.selectedAdvanceLayouts);
        this.advanceSpecialRow.initItems(SearchInfo.getInstance().advanceSpecials, this.selectedAdvanceSpecials);
        
        //reload views
        reloadViews();
        
        //reload switch
        switchImageView = (ImageView) this.findViewById(R.id.switchImageView);
        Boolean switchIsOpen = Singleton.preferences.getBoolean(BHConstants.PREF_HOUSE_SWITCH, false);
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
		
		//reload房屋資訊
		reloadHouseViews();
		
		//reload進階資訊
		reloadAdvanceViews();
	}
	
	private void reloadLocationPartByTab(){
		//location分為：「區域」＆「捷運」
		if (currentFilterMode==BHConstants.FILTER_MODE_AREA){
			areaContainer.setVisibility(View.VISIBLE);
			mrtContainer.setVisibility(View.GONE);
			
			this.areaTabButton.setSelected(true);
			this.areaTabButton.setTextColor(getResources().getColor(R.color.filter_green_color));
			this.mrtTabButton.setSelected(false);
			this.mrtTabButton.setTextColor(getResources().getColor(R.color.filter_tab_text_color));
		}else{
			areaContainer.setVisibility(View.GONE);
			mrtContainer.setVisibility(View.VISIBLE);
			
			this.areaTabButton.setSelected(false);
			this.areaTabButton.setTextColor(getResources().getColor(R.color.filter_tab_text_color));
			this.mrtTabButton.setSelected(true);
			this.mrtTabButton.setTextColor(getResources().getColor(R.color.filter_green_color));
		}
	}
	
	private void reloadAreaViews(){
		cityRow.setSelectedText(SearchInfo.getInstance().cityList.get(this.selectedCity).cityName);
		
		String areaName = SearchInfo.getInstance().cityList.get(this.selectedCity).areas.get(this.selectedArea).name;
		areaRow.setSelectedText(areaName);
	}
	
	private void reloadMRTViews(){
		String locKey = SearchInfo.getInstance().mrtLocations[this.selectedLocation];
		this.mrtLocationRow.setSelectedText(locKey);
		
		String lineKey = SearchInfo.getInstance().locLineHashMap.get(locKey)[this.selectedLine];
		this.mrtLineRow.setSelectedText(lineKey);
		
		String stationKey = SearchInfo.getInstance().lineStationHashMap.get(lineKey)[this.selectedStation];
		this.mrtStationRow.setSelectedText(stationKey);
	}
	
	private void reloadHouseViews(){
		//價格
		this.housePriceRow.reloadRow(selectedHouseLowPrice, selectedHouseHighPrice);
		
		//坪數
		String houseArea = SearchInfo.getInstance().houseAreas[this.selectedHouseArea];
		this.houseAreaRow.setSelectedText(houseArea);
		
		//用途
		this.houseUseRow.reloadRow(this.selectedHouseUses);
	}
	
	private void reloadAdvanceViews(){
		
		//advance is open or close 
		reloadAdvanceOpenClose(this.isAdvanceOpen);
		
		//屋齡
		String year = SearchInfo.getInstance().advanceYears[this.selectedAdvanceYear];
		this.advanceYearRow.setSelectedText(year);
		
		//房數
		String room = "";
		if (this.selectedAdvanceLowRoom.equals("") && this.selectedAdvanceHighRoom.equals("")){
			room = "不指定";
		}else{
			room += ((selectedAdvanceLowRoom.equals(""))?"不指定":(selectedAdvanceLowRoom+"房"));
			room += " ~ ";
			room += ((selectedAdvanceHighRoom.equals(""))?"不指定":(selectedAdvanceHighRoom+"房"));
		}
		this.advanceRoomRow.setSelectedText(room);
		
		this.advanceTypeRow.reloadRow(this.selectedAdvanceTypes);//類型
		this.advanceParkingRow.reloadRow(this.selectedAdvanceParkings);//車位
		this.advanceFaceRow.reloadRow(this.selectedAdvanceFaces);//朝向
		this.advanceInteractRow.reloadRow(this.selectedAdvanceInteracts);//互動瀏覽
		this.advanceLayoutRow.reloadRow(this.selectedAdvanceLayouts);//特殊格局
		this.advanceSpecialRow.reloadRow(this.selectedAdvanceSpecials);//物件特色
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
	
	private void setListener(){
		areaTabButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				currentFilterMode = BHConstants.FILTER_MODE_AREA;
				reloadLocationPartByTab();
			}
		});
		
		mrtTabButton.setOnClickListener(new View.OnClickListener() {
			
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
		
		View.OnClickListener mrtRowListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mrtWheelView.getVisibility()==View.GONE){
					hideAllWheels();
					shadow.setVisibility(View.VISIBLE);
					mrtWheelView.reloadWheelsToSelected(selectedLocation, selectedLine, selectedStation);
					mrtWheelView.setVisibility(View.VISIBLE);
				}
			}
		};
		
		mrtLocationRow.setOnClickListener(mrtRowListener);
		mrtLineRow.setOnClickListener(mrtRowListener);
		mrtStationRow.setOnClickListener(mrtRowListener);
		
		areaWheelView.setWheelListener(new WheelListener(){
			@Override
			public void onClickOk(int cityIndex, int areaIndex) {
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
		
		mrtWheelView.setWheelListener(new MRTWheelListener(){

			@Override
			public void onClickOk(int locationIndex, int lineIndex, int stationIndex) {
				selectedLocation = locationIndex;
				selectedLine = lineIndex;
				selectedStation = stationIndex;
				
				reloadMRTViews();
				shadow.setVisibility(View.GONE);
			}

			@Override
			public void onClickCancel() {
				shadow.setVisibility(View.GONE);
			}
		});
		
		housePriceRow.setListener(new PriceRowListener(){

			@Override
			public void onClickLowPrice() {
				if (priceWheelView.getVisibility()==View.GONE){
					shadow.setVisibility(View.VISIBLE);
					hideAllWheels();
					showPriceWheel();
				}
			}

			@Override
			public void onClickHighPrice() {
				if (priceWheelView.getVisibility()==View.GONE){
					shadow.setVisibility(View.VISIBLE);
					hideAllWheels();
					showPriceWheel();
				}
			}

			@Override
			public void onClickButton() {
				showPriceDialog();
			}
		});
		
		houseAreaRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shadow.setVisibility(View.VISIBLE);
				hideAllWheels();
				singleWheelView.initWheels(SearchInfo.getInstance().houseAreas, selectedHouseArea);
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
		
		advanceYearRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shadow.setVisibility(View.VISIBLE);
				hideAllWheels();
				singleWheelView.initWheels(SearchInfo.getInstance().advanceYears, selectedAdvanceYear);
				singleWheelView.setVisibility(View.VISIBLE);
				
				singleWheelView.setWheelListener(new SingleWheelListener(){
					@Override
					public void onClickOk(int selectedIndex) {
						selectedAdvanceYear = selectedIndex;
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
		
		advanceRoomRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shadow.setVisibility(View.VISIBLE);
				hideAllWheels();
				
				roomWheelView.reloadWheelsToSelected(selectedAdvanceLowRoom, selectedAdvanceHighRoom);
				roomWheelView.setVisibility(View.VISIBLE);

				roomWheelView.setWheelListener(new RoomWheelListener(){

					@Override
					public void onClickOk(int lowIndex, int highIndex,String lowParam, String highParam) {
						selectedAdvanceLowRoom = lowParam;
						selectedAdvanceHighRoom = highParam;
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
				
				hideKeyboard();
				
				//save all status to searchInfo
				SearchInfo.getInstance().selectedCity = selectedCity;
				SearchInfo.getInstance().selectedArea = selectedArea;
				
				SearchInfo.getInstance().selectedLocation = selectedLocation;
				SearchInfo.getInstance().selectedLine = selectedLine;
				SearchInfo.getInstance().selectedStation = selectedStation;
				
				SearchInfo.getInstance().selectedHouseLowPrice = selectedHouseLowPrice;
				SearchInfo.getInstance().selectedHouseHighPrice = selectedHouseHighPrice;
				//SearchInfo.getInstance().selectedHousePriceMode = selectedHousePriceMode;
				SearchInfo.getInstance().selectedHouseArea = selectedHouseArea;
				SearchInfo.getInstance().selectedHouseKeyword = 	houseKeywordRow.getKeywordText();	
				
				SearchInfo.getInstance().selectedAdvanceYear = selectedAdvanceYear;
				//SearchInfo.getInstance().selectedAdvanceRoom = selectedAdvanceRoom;
				SearchInfo.getInstance().selectedAdvanceLowRoom = selectedAdvanceLowRoom;
				SearchInfo.getInstance().selectedAdvanceHighRoom = selectedAdvanceHighRoom;
				
				SearchInfo.getInstance().selectedHouseUses.clear();
				SearchInfo.getInstance().selectedHouseUses.addAll(selectedHouseUses);
				
				SearchInfo.getInstance().selectedAdvanceTypes.clear();
				SearchInfo.getInstance().selectedAdvanceTypes.addAll(selectedAdvanceTypes);
				
				SearchInfo.getInstance().selectedAdvanceParkings.clear();
				SearchInfo.getInstance().selectedAdvanceParkings.addAll(selectedAdvanceParkings);

				SearchInfo.getInstance().selectedAdvanceFaces.clear();
				SearchInfo.getInstance().selectedAdvanceFaces.addAll(selectedAdvanceFaces);
				
				SearchInfo.getInstance().selectedAdvanceInteracts.clear();
				SearchInfo.getInstance().selectedAdvanceInteracts.addAll(selectedAdvanceInteracts);
				
				SearchInfo.getInstance().selectedAdvanceLayouts.clear();
				SearchInfo.getInstance().selectedAdvanceLayouts.addAll(selectedAdvanceLayouts);
				
				SearchInfo.getInstance().selectedAdvanceSpecials.clear();
				SearchInfo.getInstance().selectedAdvanceSpecials.addAll(selectedAdvanceSpecials);
				
				//前往搜尋
				
				//處理特殊狀況：捷運搜尋如果都沒有設捷運相關位置條件，需前往map
				int tempMode = currentFilterMode;//處理filterMode
				SearchInfo.getInstance().isForKeyword = false;//處理keyword
				if (tempMode==BHConstants.FILTER_MODE_MRT){
					if ((selectedLocation==0)&&(selectedLine==0)&&(selectedStation==0)){
						if (SearchInfo.getInstance().selectedHouseKeyword.equals("")){
							tempMode = BHConstants.FILTER_MODE_AREA;
						}else{
							tempMode = BHConstants.FILTER_MODE_MRT;
							SearchInfo.getInstance().isForKeyword = true;
						}
					}
				}else{
					if ((selectedCity==0)&&(selectedArea==0)){
						if (SearchInfo.getInstance().selectedHouseKeyword.equals("")){
							tempMode = BHConstants.FILTER_MODE_AREA;
						}else{
							tempMode = BHConstants.FILTER_MODE_MRT;
							SearchInfo.getInstance().isForKeyword = true;
						}
					}
				}

				
				if (tempMode==BHConstants.FILTER_MODE_AREA){
					//前往map
					SearchInfo.getInstance().currentFilterMode = BHConstants.FILTER_MODE_AREA;
					
					
					if ((selectedCity==0)&&(selectedArea==0)){
						//區域搜尋沒有設定位置條件，searchMode為經緯度位置
						//center設為當前位置
						SearchInfo.getInstance().centerPos = getMyCurrentLocation();
						SearchInfo.getInstance().currentSearchMode = BHConstants.SEARCH_MODE_LATLNG;
						Log.e("test", "HouseFilter: centerPos:設為當前位置");
					}else{
						//區域搜尋有設定位置條件，searchMode為區域位置
						//TODO:center設為此area的定點經緯度
						SearchInfo.getInstance().centerPos = SearchInfo.getInstance().getSelectedCityAreaLatLng();
						SearchInfo.getInstance().currentSearchMode = BHConstants.SEARCH_MODE_LOCATION;
						Log.e("test", "HouseFilter: centerPos:設為目前選擇行政區的位置(未處理)");
					}
					
					//save search param to track: recent search
					String filterParams = SearchInfo.getInstance().getFilterParams(true, false);
					if (!filterParams.equals("")){
						//如果有設條件的搜尋，才記錄到最近搜尋
						LoginInfo.getInstance().addSearchParam(true,
							filterParams,SearchInfo.getInstance().getDisplayParams(true, false));
					}
					
					Intent intent = new Intent();
					intent.setAction(BHConstants.BROADCAST_HOUSE_SEARCH_REFRESH_MAP);
					LocalBroadcastManager.getInstance(HouseFilterActivity.this).sendBroadcast(intent);
					
					finish();
				}else{
					//前往list
					SearchInfo.getInstance().currentFilterMode = BHConstants.FILTER_MODE_MRT;
					SearchInfo.getInstance().currentSearchMode=BHConstants.SEARCH_MODE_LOCATION;
							
					//save search param to track: recent search
					String filterParams = SearchInfo.getInstance().getFilterParams(true, false);
					if (!filterParams.equals("")){
						//如果有設條件的搜尋，才記錄到最近搜尋
						LoginInfo.getInstance().addSearchParam(true,
							filterParams,SearchInfo.getInstance().getDisplayParams(true, false));
					}
					
					Intent intent = new Intent();
					intent.setAction(BHConstants.BROADCAST_HOUSE_SEARCH_REFRESH_LIST);
					LocalBroadcastManager.getInstance(HouseFilterActivity.this).sendBroadcast(intent);
					
					finish();
				}
				
			}
		});
		
		clearButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SearchInfo.getInstance().clearStatus();
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
				SearchInfo.getInstance().isAdvanceOpen = isAdvanceOpen;
			}
		});
		
		houseKeywordRow.keywordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus){
					scrollToViewBottom(houseKeywordRow);
				}
			}
		});
		
		advanceSpecialRow.setListener(new MultiChooseListener(){
			@Override
			public void onClickTitle(Boolean isOpened) {
				if (isOpened){
					scrollToViewBottom(advanceSpecialRow);
				}
			}
		});
		
		switchImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switchImageView.setSelected(!switchImageView.isSelected());
				Singleton.preferenceEditor.putBoolean(BHConstants.PREF_HOUSE_SWITCH, switchImageView.isSelected()).commit();
			}
		});
	}
	
	/* filter */
	private void reloadSearchInfoStatus(){
		//load previous status
        this.currentFilterMode = SearchInfo.getInstance().currentFilterMode;
        this.selectedCity = SearchInfo.getInstance().selectedCity;
        this.selectedArea = SearchInfo.getInstance().selectedArea;
        this.selectedLocation = SearchInfo.getInstance().selectedLocation;
        this.selectedLine = SearchInfo.getInstance().selectedLine;
        this.selectedStation = SearchInfo.getInstance().selectedStation;
        
        //this.selectedHousePriceMode = SearchInfo.getInstance().selectedHousePriceMode;
        this.selectedHouseLowPrice = SearchInfo.getInstance().selectedHouseLowPrice;
        this.selectedHouseHighPrice = SearchInfo.getInstance().selectedHouseHighPrice;
        this.selectedHouseArea = SearchInfo.getInstance().selectedHouseArea;
        houseKeywordRow.setKeywordText(SearchInfo.getInstance().selectedHouseKeyword);
        this.selectedAdvanceYear = SearchInfo.getInstance().selectedAdvanceYear;
        //this.selectedAdvanceRoom = SearchInfo.getInstance().selectedAdvanceRoom;
        this.selectedAdvanceLowRoom = SearchInfo.getInstance().selectedAdvanceLowRoom;
        this.selectedAdvanceHighRoom = SearchInfo.getInstance().selectedAdvanceHighRoom;
        
        this.isAdvanceOpen = SearchInfo.getInstance().isAdvanceOpen;
        
        //loae previous status for multi choose
        this.selectedHouseUses = new HashSet<Integer>();
        	selectedHouseUses.addAll(SearchInfo.getInstance().selectedHouseUses);
        	
        	this.selectedAdvanceTypes = new HashSet<Integer>();
        	selectedAdvanceTypes.addAll(SearchInfo.getInstance().selectedAdvanceTypes);
        
        	this.selectedAdvanceParkings = new HashSet<Integer>();
        	selectedAdvanceParkings.addAll(SearchInfo.getInstance().selectedAdvanceParkings);
        
        	this.selectedAdvanceFaces = new HashSet<Integer>();
        	selectedAdvanceFaces.addAll(SearchInfo.getInstance().selectedAdvanceFaces);
        
        	this.selectedAdvanceInteracts = new HashSet<Integer>();
        	selectedAdvanceInteracts.addAll(SearchInfo.getInstance().selectedAdvanceInteracts);
        
        	this.selectedAdvanceLayouts = new HashSet<Integer>();
        	selectedAdvanceLayouts.addAll(SearchInfo.getInstance().selectedAdvanceLayouts);
        
        	this.selectedAdvanceSpecials = new HashSet<Integer>();
        	selectedAdvanceSpecials.addAll(SearchInfo.getInstance().selectedAdvanceSpecials);
        
	}
	
	/* Price Wheel */
	private void showPriceWheel(){
		Log.e("test", "showPriceWheel!");
		
		priceWheelView.reloadWheelsToSelected(selectedHouseLowPrice, selectedHouseHighPrice);
		priceWheelView.setVisibility(View.VISIBLE);

		priceWheelView.setWheelListener(new PriceWheelListener(){

			@Override
			public void onClickOk(int lowPrice, int highPrice) {
				selectedHouseLowPrice = lowPrice;
				selectedHouseHighPrice = highPrice;
				reloadHouseViews();
				shadow.setVisibility(View.GONE);
			}

			@Override
			public void onClickCancel() {
				shadow.setVisibility(View.GONE);
			}
		});
	}
	
	private void showPriceDialog(){
		showPriceRangeDialog("確定","取消", selectedHouseLowPrice, selectedHouseHighPrice, new PriceOkOnClickListener(){

			@Override
			public void didClickOk(int lowPrice, int highPrice) {
				hideKeyboard(200);
				selectedHouseLowPrice = lowPrice;
				selectedHouseHighPrice = highPrice;
				reloadHouseViews();
			}

			@Override
			public void didClickCancel() {
				hideKeyboard(200);
			}
		});
	}
	
	private void hideAllWheels(){
		areaWheelView.setVisibility(View.GONE);
        mrtWheelView.setVisibility(View.GONE);
        singleWheelView.setVisibility(View.GONE);
        priceWheelView.setVisibility(View.GONE);
        roomWheelView.setVisibility(View.GONE);
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
		this.advanceTypeRow.close();
		this.advanceParkingRow.close();
		this.advanceFaceRow.close();
		this.advanceInteractRow.close();
		this.advanceLayoutRow.close();
		this.advanceSpecialRow.close();
	}
	
	/* subscribe */
	private void subscribeSearchAction(){
		//check first
		String filterParams = getFilterParams(true);
		if (filterParams.equals("")) {
			showToast("請選擇至少一個條件");
			return;
		}
		
		//有設定條件，開始訂閱
		String displayParams = getDisplayParams(true);
		Log.e("test", "filter:"+filterParams);
		Log.e("test", "display:"+displayParams);
		
		TrackService.subscribeSearch(filterParams, displayParams, new TrackCallback(){

			@Override
			public void onResult(boolean success, String debugMessage) {
				if (success){
					showToast("訂閱成功，您可至瀏覽追蹤查看新屋通知");
				}else{
					showToast(getString(R.string.network_not_stable));
				}
			}
		});
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_house_filter, menu);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home){
			slideOutToFinish();
			//finish();
		}else if (id == R.id.action_subscribe) {
			subscribeSearchAction();
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		
		slideOutToFinish();
	}
	
	/* Subscribe */
	public String getFilterParams(Boolean addLocaionInfo){
		String params = "";
		
		//location相關：區域/捷運
		if (addLocaionInfo){
			if (currentFilterMode==BHConstants.FILTER_MODE_MRT){
				params += SearchInfo.getInstance().getMRTParam(selectedLocation, selectedLine, selectedStation);
			}else{
				//區域: 只有在儲存搜尋條件＆訂閱新屋通知時會用到,
				//mapSearch不會用到，因為mapSearch會直接定位到中心點去抓左上右下點做搜尋
				params += SearchInfo.getInstance().getAreaParam(selectedCity, selectedArea);
			}
		}
		
		//house相關
		params += SearchInfo.getInstance().getHousePriceParam(selectedHouseLowPrice, selectedHouseHighPrice);
		params += SearchInfo.getInstance().getHouseAreaParam(selectedHouseArea);
		params += SearchInfo.getInstance().getHouseUseParam(selectedHouseUses);
		params += SearchInfo.getInstance().getHouseKeywordParam(houseKeywordRow.getKeywordText());
		
		//advance相關
		params += SearchInfo.getInstance().getAdvanceYearParam(selectedAdvanceYear);
		params += SearchInfo.getInstance().getAdvanceRoomParam(selectedAdvanceLowRoom, selectedAdvanceHighRoom);
		params += SearchInfo.getInstance().getAdvanceTypeParam(selectedAdvanceTypes);
		params += SearchInfo.getInstance().getAdvanceParkingParam(selectedAdvanceParkings);
		params += SearchInfo.getInstance().getAdvanceFaceParam(selectedAdvanceFaces);
		params += SearchInfo.getInstance().getAdvanceInteractAndLayoutParam(selectedAdvanceInteracts, selectedAdvanceLayouts);
		params += SearchInfo.getInstance().getAdvanceSpecialParam(selectedAdvanceSpecials);
		
		return params;
	}
	
	public String getDisplayParams(Boolean addLocaionInfo){
		String params = "";
		
		//location相關：區域/捷運
		if (addLocaionInfo){
			if (this.currentFilterMode==BHConstants.FILTER_MODE_MRT){
				params += SearchInfo.getInstance().getMRTDisplayParam(selectedLocation,selectedLine,selectedStation);
			}else{
				//區域: 只有在儲存搜尋條件＆訂閱新屋通知時會用到,
				//mapSearch不會用到，因為mapSearch會直接定位到中心點去抓左上右下點做搜尋
				params += SearchInfo.getInstance().getAreaDisplayParam(selectedCity, selectedArea);
			}
		}
		
		//house相關
		params += SearchInfo.getInstance().getHousePriceDisplayParam(selectedHouseLowPrice, selectedHouseHighPrice);
		params += SearchInfo.getInstance().getHouseAreaDisplayParam(selectedHouseArea);
		params += SearchInfo.getInstance().getHouseUseDisplayParam(selectedHouseUses);
		params += SearchInfo.getInstance().getHouseKeywordDisplayParam(houseKeywordRow.getKeywordText());
		
		//advance相關
		params += SearchInfo.getInstance().getAdvanceYearDisplayParam(selectedAdvanceYear);
		params += SearchInfo.getInstance().getAdvanceRoomDisplayParam(selectedAdvanceLowRoom, selectedAdvanceHighRoom);
		params += SearchInfo.getInstance().getAdvanceTypeDisplayParam(selectedAdvanceTypes);
		params += SearchInfo.getInstance().getAdvanceParkingDisplayParam(selectedAdvanceParkings);
		params += SearchInfo.getInstance().getAdvanceFaceDisplayParam(selectedAdvanceFaces);
		params += SearchInfo.getInstance().getAdvanceInteractAndLayoutDisplayParam(selectedAdvanceInteracts, selectedAdvanceLayouts);
		params += SearchInfo.getInstance().getAdvanceSpecialDisplayParam(selectedAdvanceSpecials);
		
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
	
	
}
