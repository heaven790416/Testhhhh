package com.yowoo.newbuyhouse.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.ui.IconGenerator;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.Constants;
import com.yowoo.newbuyhouse.MainActivity;
import com.yowoo.newbuyhouse.NewBuyHouseApplication;
import com.yowoo.newbuyhouse.SearchInfo;
import com.yowoo.newbuyhouse.Singleton;
import com.yowoo.newbuyhouse.house.HouseDetailActivity;
import com.yowoo.newbuyhouse.model.House;
import com.yowoo.newbuyhouse.model.HouseMarker;
import com.yowoo.newbuyhouse.model.Price;
import com.yowoo.newbuyhouse.model.PriceMarker;
import com.yowoo.newbuyhouse.network.HouseService;
import com.yowoo.newbuyhouse.network.HouseService.HouseMarkerCallback;
import com.yowoo.newbuyhouse.network.HouseService.SimpleHouseCallback;
import com.yowoo.newbuyhouse.network.PriceService;
import com.yowoo.newbuyhouse.network.PriceService.PriceListCallback;
import com.yowoo.newbuyhouse.network.PriceService.PriceMarkerCallback;
import com.yowoo.newbuyhouse.price.PriceIconGenerator;
import com.yowoo.newbuyhouse.util.MarkerUtils;
import com.yowoo.newbuyhouse.view.DealPriceRow;
import com.yowoo.newbuyhouse.view.HouseRow;
import com.yowoo.newbuyhouse.view.HouseRow.HouseRowCallback;
import com.yowoo.newbuyhouse.view.OrderView;
import com.yowoo.newbuyhouse.view.OrderView.OrderViewListener;

public class MainHouseFragment extends MyFragment implements OnCameraChangeListener, 
															 OnMapReadyCallback{

	public static Fragment newInstance(Context context) {
		MainHouseFragment f = new MainHouseFragment();
 
        return f;
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	private static View root;
	private GoogleMap mMap;
	//private Button leftButton, rightButton;
	private RelativeLayout leftContainer, rightContainer;
	private Button leftButton, rightButton;
	private TextView leftTextView, rightTextView;
	private OrderView orderView;
	
	private int currentDisplayMode=0;
	MainHouseListFragment mainHouseListFragment;
	ViewPager housePager, pricePager;
	
	//需同時update的data
	private ArrayList<House> houseList = new ArrayList<House>();
	private HashMap<Marker, HouseMarker> markerHashMap = new HashMap<Marker, HouseMarker>();
	private Marker currentMapMarker;
	//附屬的MarkerType: price
	private ArrayList<Price> priceListForPager = new ArrayList<Price>();
	private HashMap<Marker, PriceMarker> priceMarkerHashMap = new HashMap<Marker, PriceMarker>();
	
	
	//marker icon
	IconGenerator anchorIconFactory, anchorPressedIconFactory;
	PriceIconGenerator priceIconFactory, pricePressedIconFactory;

	private final float MAX_ZOOM = BHConstants.HOUSE_MAX_ZOOM;
	private final float PRICE_DISPLAY_ZOOM = 16.0f;
	//for mode switch: unlock from locationMode to latlngMode
	//Boolean needSwitchToLatLngMode=false;
	
	//for animation
	private Animation slideInFromBottomAnimation, houseSlideOutToBottomAnimation, priceSlideOutToBottomAnimation;
	
	//remark: outlier
	RelativeLayout remarkContainer;

	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        if (root != null) {
            ViewGroup parent = (ViewGroup) root.getParent();
            if (parent != null)
                parent.removeView(root);
        }
        try {
        		root = inflater.inflate(R.layout.fragment_main_house, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
        
        //set view
        rightContainer = (RelativeLayout) root.findViewById(R.id.rightContainer);
        leftContainer = (RelativeLayout) root.findViewById(R.id.leftContainer);
        rightTextView = (TextView) root.findViewById(R.id.rightTextView);
        leftTextView = (TextView) root.findViewById(R.id.leftTextView);
        rightButton = (Button) root.findViewById(R.id.rightButton);
        leftButton = (Button) root.findViewById(R.id.leftButton);
        orderView = (OrderView) root.findViewById(R.id.orderView);
        remarkContainer = (RelativeLayout) root.findViewById(R.id.remarkContainer);
		
        //set map
        this.setUpMapIfNeeded();
        getMap().setOnCameraChangeListener(this);
        getMap().setMyLocationEnabled(true);
        getMap().getUiSettings().setMyLocationButtonEnabled(false);
        
        if (SearchInfo.getInstance().isCenterPosDefault()){
        		//未設定過搜尋條件並搜尋
        		SearchInfo.getInstance().centerPos = getMyCurrentLocation();
        		setZoomAndStartMoveCamera();
        }else{
        		//曾搜尋過，到某中心點，跳離此功能再回來要定位到上次搜尋位置
        		moveCamera(SearchInfo.getInstance().centerPos, SearchInfo.getInstance().zoom);
        }
        
        
        //set marker factory
        anchorIconFactory = new IconGenerator(getActivity());
        anchorIconFactory.setColor(getResources().getColor(R.color.pin_gray_color));
        anchorIconFactory.setTextAppearance(R.style.MarkerWhiteText);
    		
        anchorPressedIconFactory = new IconGenerator(getActivity());
        anchorPressedIconFactory.setColor(getResources().getColor(R.color.pin_green_color));
        anchorPressedIconFactory.setTextAppearance(R.style.MarkerWhiteText);
        
        //set price marker factory
        priceIconFactory = new PriceIconGenerator(getActivity());
        priceIconFactory.setBackgroundDrawable(R.drawable.pin_price);
        priceIconFactory.setTextColor(getResources().getColor(R.color.pin_price_color));

        pricePressedIconFactory = new PriceIconGenerator(getActivity());
        pricePressedIconFactory.setBackgroundDrawable(R.drawable.pin_price_pressed);
        pricePressedIconFactory.setTextColor(getResources().getColor(R.color.pin_green_color));

        
        //set pager
        housePager = (ViewPager) root.findViewById(R.id.housePager);
        housePager.setAdapter(housePagerAdapter);
        housePager.setOffscreenPageLimit(3);
        
        housePager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

        		@Override
        		public void onPageSelected(int position) {
        			Log.e("test", "onPageSelected: "+position);
        			if (position==houseList.size()-1){
        				fetchMoreHouseByNOs(false);
        			}
        		}

        		@Override
        		public void onPageScrolled(int arg0, float arg1, int arg2) {}

        		@Override
        		public void onPageScrollStateChanged(int arg0) {}
        });
        
      //set price pager
        pricePager = (ViewPager) root.findViewById(R.id.pricePager);
        pricePager.setAdapter(pricePagerAdapter);
        pricePager.setOffscreenPageLimit(3);

        pricePager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        		@Override
        		public void onPageSelected(int position) {
        			Log.e("test", "onPageSelected: "+position);
        			//remark
        			showOrHideOutlier(position);

        			if (position==priceListForPager.size()-1){
        				fetchMorePriceByNOs(false);
        			}
        		}

        		@Override
        		public void onPageScrolled(int arg0, float arg1, int arg2) {}

        		@Override
        		public void onPageScrollStateChanged(int arg0) {}
        });
        
        //init views
        orderView.initItems(getResources().getStringArray(R.array.house_list_order_item));
        reloadBottomViews();
        
        //set listener
        setListener();
        
        //register broadcast receiver
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        broadcastManager.registerReceiver(refreshHouseBroadcastReceiver, new IntentFilter(BHConstants.BROADCAST_HOUSE_SEARCH_REFRESH_MAP));
        broadcastManager.registerReceiver(refreshHouseBroadcastReceiver, new IntentFilter(BHConstants.BROADCAST_HOUSE_SEARCH_REFRESH_LIST));
        
        //init animation
        initAnimation();
        
        //check and set gps
        checkAndShowGPSDialog();
        
        return root;
    }
    
    @Override
	public void onResume() {
		super.onResume();
		
		Log.e("test", "MainHouseFragment: onResume!");
		reloadBottomViews();
	NewBuyHouseApplication.initTracker(getActivity(),"MainHouseFragment");
		
    }
    
    @Override
    public void onDestroy() {
		super.onDestroy();
		Log.e("test", "MainHouseFragment: onDestroy!");
	    
		//((MainActivity) activity).onSectionAttached(getArguments().getInt(Constants.FRAGMENT_ARG_POSITION));
		
		LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		broadcastManager.unregisterReceiver(refreshHouseBroadcastReceiver);
	}
    
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(Constants.FRAGMENT_ARG_POSITION));
	}
    
    
    
    
    
    private void setZoomAndStartMoveCamera(){
    		//在執行此方法前，不管是區域搜尋模式或定位模式，都要先算好經緯度，存入searchInfo
    	
    		//TODO: 根據各種mode，決定要設定何種zoom level
    		//float zoom = SearchInfo.getInstance().calculateZoomAndGet();
    	
    		moveCamera(SearchInfo.getInstance().centerPos, SearchInfo.getInstance().calculateZoomAndGet());
    		
    }
    
    
    private void moveCamera(LatLng centerPos, float zoom){
    		getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(centerPos, zoom));
    	
    }
    
    private void fetchMarkersFromServer(){
    		VisibleRegion vr = mMap.getProjection().getVisibleRegion();
    		float left = (float) vr.latLngBounds.southwest.longitude;
    		float top = (float) vr.latLngBounds.northeast.latitude;
    		float right = (float) vr.latLngBounds.northeast.longitude;
    		float bottom = (float) vr.latLngBounds.southwest.latitude;
    		
    		String latlngString = String.valueOf(top)+","+String.valueOf(left)+"_"+String.valueOf(bottom)+","+String.valueOf(right);
    		//Store To SearchInfo
    		SearchInfo.getInstance().boundLatLngString = latlngString;
    		
    		float zoom = mMap.getCameraPosition().zoom;
    		Log.e("test", "zoom: "+ zoom);
    		double distance = this.measureDistanceFromZoom(zoom);
    		Log.e("test", "distance: "+distance);
    		
    		String filterParams = SearchInfo.getInstance().getFilterParams(false, false);
    		
    		HouseService.getClusterMarkers(latlngString, distance, filterParams, new HouseMarkerCallback(){
    			@Override
    			public void onResult(boolean success, ArrayList<HouseMarker> data) {
    				if (success){
    					Log.e("test", "Marker count: "+data.size());
    					if (getActivity()!=null){
    						//從map上移除house marker
    						for(Entry<Marker, HouseMarker> entry : markerHashMap.entrySet()) {
    							Marker key = entry.getKey();
    							key.remove();
    						}
    						markerHashMap.clear();
    						
    						if (data.size()==0){
    							showActivityToast(R.string.no_house_result_under_filter);
    							return;
    						}
    						
    						Marker marker;
    						for (int i=0; i<data.size(); i++){
    							int houseCount = data.get(i).houseNOs.length();
    							if (houseCount==1){
    								marker = addAnchorIcon(anchorIconFactory, String.valueOf(data.get(i).label), new LatLng(data.get(i).lat, data.get(i).lng));
    							}else{
    								marker = addCircleIcon(String.valueOf(data.get(i).houseNOs.length()), new LatLng(data.get(i).lat, data.get(i).lng));
    							}
    							markerHashMap.put(marker, data.get(i));
    						}
    						
    					}
    				}else{
    					Log.e("test", "getClusterMarkers fail!");
    					showActivityToast(R.string.no_network_please_check);
    				}
    			}
    		});
    		
    }
    
    /* House Adapter */
    PagerAdapter housePagerAdapter = new PagerAdapter() {

        public Object instantiateItem(View container, int position) {
            final HouseRow houseRow = new HouseRow(getActivity());

            housePager.addView(houseRow);

            final House house = houseList.get(position);
            houseRow.reloadCell(house);
            houseRow.setHouseRowCallback(houseRowCallback);
            
            //set prev & next arrow
            houseRow.reloadArrow(position, houseList.size());
            
            return houseRow;
        };

        public void destroyItem(View container, int position, Object childView) {
            ((ViewPager) container).removeView((View) childView);
        }

        public boolean isViewFromObject(View container, Object childView) {
            return container == ((View) childView);
        }

        public Parcelable saveState() {
            return null;
        }

        public int getCount() {
            return houseList.size();
        }
        
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    };
    
    /* Set Map */
    private void setUpMapIfNeeded() {
        if (mMap != null) {
            return;
        }
        mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
    }
    
    protected GoogleMap getMap() {
        setUpMapIfNeeded();
        return mMap;
    }
    
    private MyMapFragment getMyMapFragment(){
    		return (MyMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
    }
    
    
    private void startFetchListFromFilter(){
    	
    		if (currentDisplayMode==BHConstants.HOUSE_MODE_MAP){
			//目前是map, 要切去list,並根據filter撈取資料
        		
			//先做searchmode設定
			SearchInfo.getInstance().currentSearchMode = BHConstants.SEARCH_MODE_LOCATION;
			
			//切換至list
			if (mainHouseListFragment!=null){
				//如果本來listFragment已存在，show & reload
				FragmentTransaction tx = getChildFragmentManager().beginTransaction();
				tx.show(mainHouseListFragment);
				tx.commitAllowingStateLoss();
				
				mainHouseListFragment.reloadData();
			}else{
				//如果本來listFragment不存在，new & add
				mainHouseListFragment = new MainHouseListFragment();
				FragmentTransaction tx = getChildFragmentManager().beginTransaction();
				tx.add(R.id.container, mainHouseListFragment);
				tx.commitAllowingStateLoss();
			}
			
			//切換display mode
			currentDisplayMode = BHConstants.HOUSE_MODE_LIST;
		}else{
    		
			//目前已是list, 重load資料
			if (mainHouseListFragment!=null){
				mainHouseListFragment.reloadData();
			}
		}
    		
    		//reload bottom view
    		reloadBottomViews();
    		
    		//hide simple house viewer
    		//TODO
    		//this.housePager.setVisibility(View.GONE);
    		hideHousePager();
    		hidePricePager();
    }
    
    private void startFetchMapFromFilter(){
    		if (currentDisplayMode==BHConstants.HOUSE_MODE_MAP){
    			//在執行此方法前，各種設定已經設好
    			//reset views
    			this.closePagerAndResetMarker();
    		}else{
    			//目前是list, 需切換到map
    		}
    		
    		//不管原本在map or list, 都清除掉listFragmet
    		if (mainHouseListFragment!=null){
    			//切去map
    			FragmentTransaction tx = getChildFragmentManager().beginTransaction();
    			tx.remove(mainHouseListFragment);
    			tx.commitAllowingStateLoss();  
    			mainHouseListFragment = null;
    		}
    		
    		//切換display mode
		currentDisplayMode = BHConstants.HOUSE_MODE_MAP;
    		
    		//refresh map, get data
    		setZoomAndStartMoveCamera();
    		
    		//reload bottom view
    		reloadBottomViews();
			
    }
    
    private void switchHouseDisplayMode(){
    		if (currentDisplayMode==BHConstants.HOUSE_MODE_MAP){
    			//切換至list
    			if (mainHouseListFragment==null){
    				mainHouseListFragment = new MainHouseListFragment();
    				FragmentTransaction tx = getChildFragmentManager().beginTransaction();
        			tx.add(R.id.container, mainHouseListFragment);
        			tx.commit();
    			}else{
    				FragmentTransaction tx = getChildFragmentManager().beginTransaction();
        			tx.show(mainHouseListFragment);
        			tx.commit();
    			}
    			
    		}else{
    			//目前是list, 要切去map
    			if (mainHouseListFragment!=null){
    				//兩種情況：
    				if (SearchInfo.getInstance().currentFilterMode==BHConstants.FILTER_MODE_AREA){
    					//1.目前list是從區域切過來的，只要直接切回map即可
    					Log.e("test", "直接切回map");
    					
    					//切去map
        				FragmentTransaction tx = getChildFragmentManager().beginTransaction();
        				tx.remove(mainHouseListFragment);
        				tx.commit();  
        				mainHouseListFragment = null;
    				}else{
    					//2.目前list是從捷運搜尋切過來的，到map前，要先改變中心點為「目前滑到的第一筆」
    					//sendNotification: 請map重新load資料
    					SearchInfo.getInstance().centerPos = mainHouseListFragment.getCurrentFirstHouseLatLng();
    					setZoomAndStartMoveCamera();
    					Log.e("test", "切回map前，先改變center位置-第一筆資料");
    					
    					//reset flag
    					//needSwitchToLatLngMode = false; 
    					
    					//切去map
    					FragmentTransaction tx = getChildFragmentManager().beginTransaction();
        				tx.hide(mainHouseListFragment);
        				tx.commit();  
    				}
    			}
    		}
    		
    		//切換DisplayMode
    		currentDisplayMode = (currentDisplayMode+1)%2;
    		
    }
    
    /* Listener */
    private void setListener(){
    		rightContainer.setOnClickListener(new View.OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				//先確認orderView關掉
    				orderView.setVisibility(View.GONE);
    				
    				//切換map & list模式
    				switchHouseDisplayMode();
    				reloadBottomViews();
    			}
    		});
    		
    		leftContainer.setOnClickListener(new View.OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				if (currentDisplayMode==BHConstants.DISPLAY_MODE_MAP){
    					//地圖模式時：移動到目前位置做搜尋
    					getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(getMyCurrentLocation(), 14));
    				}else{
    					//列表模式時：改變排序選項
    					if (orderView.getVisibility()==View.VISIBLE){
    						orderView.setVisibility(View.GONE);
    					}else{
    						orderView.reloadView(SearchInfo.getInstance().selectedOrder);
    						orderView.setVisibility(View.VISIBLE);
    					}
    				}
    			}
    		});
    		
    		orderView.setOrderViewListener(new OrderViewListener(){

    			@Override
    			public void onClickItem(int index, String selectedText) {
    				SearchInfo.getInstance().selectedOrder = index;
    				leftTextView.setText(selectedText);
    				orderView.setVisibility(View.GONE);
    				
    				if (currentDisplayMode==BHConstants.DISPLAY_MODE_LIST){
    					if (mainHouseListFragment!=null){
    						mainHouseListFragment.reloadData();
    					}
    				}
    			}
    		});
    		
    		getMap().setOnMarkerClickListener(new OnMarkerClickListener(){

    			@Override
    			public boolean onMarkerClick(Marker marker) {
    				//更新目前marker顏色
    				if (markerHashMap.containsKey(currentMapMarker)){
    					//resetHouseMarker
    					resetHouseMarkerIcon(currentMapMarker, false);
    				}else{
    					resetPriceMarkerIcon(currentMapMarker, false);
    				}
    				
    				//更新新的marker顏色
    				if (markerHashMap.containsKey(marker)){
    					//TODO: 新點擊的是house marker, 動作
    					resetHouseMarkerIcon(marker, true);
    					HouseMarker houseMarker = markerHashMap.get(marker);
    					Log.e("test", "prices length: "+houseMarker.houseNOs.length());
    					currentMapMarker = marker;
    					fetchMoreHouseByNOs(true);
    				}else{
    					resetPriceMarkerIcon(marker, true);
    					PriceMarker priceMarker = priceMarkerHashMap.get(marker);
    					Log.e("test", "prices length: "+priceMarker.priceJSONArray.length());
    					currentMapMarker = marker;
    					fetchMorePriceByNOs(true);
    				}
    				return true;
    			}
    		});
    		
    		getMap().setOnMapClickListener(new OnMapClickListener(){
    			@Override
    			public void onMapClick(LatLng arg0) {
    				Log.e("test", "onMapClickListener!");
    				closePagerAndResetMarker();
    			}
    		});
    }
    
    /* Fetch data */
    private void fetchMoreHouseByNOs(final Boolean refresh){
    		//page: start from 0
    		
    		//special issue: 
    		final int tempHouseListSize = (refresh)? 0: houseList.size();
    	
    		//get all houseNOs
    		if (currentMapMarker==null) return;
    		HouseMarker houseMarker = markerHashMap.get(currentMapMarker);
    		if (houseMarker==null) return;
    		JSONArray houseNOs = houseMarker.houseNOs;
		if (houseNOs.length()==tempHouseListSize) return;
		
    		//calculate params: page,limit
    		final int limit = BHConstants.SIMPLE_HOUSE_FETCH_LIMIT;
    		int page=0;
    		if (!refresh){
    			page = tempHouseListSize/limit;
			if (page<=0) return;
    		}
    		
    		//檢查是否還有更多資料可以取，如果沒有，return
    		if ((page*limit)>=houseNOs.length()) return;
    		
    		//還有資料，取出需要區間的houseNOs
    		int startIndex = page*limit;
    		JSONArray subHouseNos = new JSONArray();
    		try{
    			for (int i=startIndex; i<startIndex+limit; i++){
    				if (i>=houseNOs.length()) {
    					Log.e("test", "ready to get: "+startIndex+"~"+(i-1));
    					break;
    				}
    				subHouseNos.put(houseNOs.get(i));
    			}
    		}catch(JSONException e){
    			e.printStackTrace();
    		}
    		
    		//轉成用逗號分隔的string
    		String NOs = "";
    		try {
    			for (int i=0; i<subHouseNos.length(); i++){
    				NOs += subHouseNos.get(i);
    				if (i!=subHouseNos.length()-1)  NOs += ",";
    			}
    		} catch (JSONException e) {
    			e.printStackTrace();
    		}
    		Log.e("test", "NOs: "+NOs);
    		
    		final int savedPage = page;
    		final Marker savedCurrentMapParker = currentMapMarker;
    		HouseService.getSimpleHouse(NOs, new SimpleHouseCallback(){
    			@Override
    			public void onResult(boolean success,ArrayList<House> houses) {
    				if (success){
    					Log.e("test", "get simple houses success! page:"+savedPage+" count:"+houses.size());
    					
    					//check抓到資料時，是否還是當初的那個marker
    					if (savedCurrentMapParker!=currentMapMarker) return;
    					
    					//check抓到此page的資料，是否已經有被抓過並放入了
    					if (savedPage*limit<tempHouseListSize) return;
    					
    					if (refresh) houseList.clear();
    					houseList.addAll(houses);
    					
    					housePagerAdapter.notifyDataSetChanged();
    					if (savedPage==0){
    						housePager.setCurrentItem(0);
    					}
    					
    					//TODO
    					//housePager.setVisibility(View.VISIBLE);
    					if (housePager.getVisibility()==View.GONE){
    						showHousePager();
    						hidePricePager();
    					}
    					
    				}else{
    					Log.e("test", "get simple houses fail!");
    				}
    			}
    		});
    }

    /* Camera Listner */
	@Override
	public void onCameraChange(CameraPosition cameraPos) {
		Log.e("test", "onCameraChange");
		
		//偵測是否使用者仍在touch地圖
		//如果是，暫時不更新資料，直到使用者untouch地圖，才更新資料
		try{
			if (getMyMapFragment()!=null){
				if (getMyMapFragment().mTouchView.mMapIsTouched) {
					return;
				}
			}
		}catch(Exception e){}
		
		//關閉housePager, reset marker
		closePagerAndResetMarker();
		
		if (cameraPos.zoom < MAX_ZOOM){
	        mMap.animateCamera(CameraUpdateFactory.zoomTo(MAX_ZOOM));
	        return;
		}
		
		//save centerPos and zoom for restore
		SearchInfo.getInstance().centerPos = new LatLng(cameraPos.target.latitude, cameraPos.target.longitude);
		SearchInfo.getInstance().zoom = cameraPos.zoom;
		
		fetchMarkersFromServer();
		
		if ((cameraPos.zoom>PRICE_DISPLAY_ZOOM) && Singleton.preferences.getBoolean(BHConstants.PREF_HOUSE_SWITCH, false)){
			fetchPriceFromServer();
		}else{
			cleanPriceMarker();
		}
		
		//search mode switch: 設定flag 
		//區域搜訊/捷運搜尋，這兩種都有設定需解鎖，但區域搜尋其實不需用到，但仍有設
//		if (SearchInfo.getInstance().currentSearchMode==BHConstants.SEARCH_MODE_LOCATION){
//			if (this.needSwitchToLatLngMode) {
//				//原本已經上鎖，現在移動了，就解鎖了（變成以地圖移動為主）
//				SearchInfo.getInstance().currentSearchMode=BHConstants.SEARCH_MODE_LATLNG;
//				SearchInfo.getInstance().currentFilterMode=BHConstants.FILTER_MODE_AREA;
//				needSwitchToLatLngMode = false;
//			}else{
//				//此次camera移動是filter搜尋造成的，設定下一次移動要解鎖
//				needSwitchToLatLngMode = true;
//			}
//		}
		
		//Log.e("test", "needSwitch:"+needSwitchToLatLngMode);
		Log.e("test", "searchMode:"+SearchInfo.getInstance().currentSearchMode);
		Log.e("test", "filterMode:"+SearchInfo.getInstance().currentFilterMode);
		
	}

	/* Map Listener */
	@Override
	public void onMapReady(GoogleMap map) {
		
	}
	
	/* HouseRow Listener */
	private HouseRowCallback houseRowCallback = new HouseRowCallback(){
		@Override
		public void onClickHouse(String houseNO) {
			Log.e("test", "onClickHouse!");
			Intent intent = new Intent(getActivity(), HouseDetailActivity.class);
			intent.putExtra(BHConstants.EXTRA_HOUSE_NO, houseNO);
			startActivity(intent);
		}

		@Override
		public void onClickPrevArrow() {
			int currentPos = housePager.getCurrentItem();
			if (currentPos>0){
				housePager.setCurrentItem(currentPos-1);
			}
		}

		@Override
		public void onClickNextArrow() {
			int currentPos = housePager.getCurrentItem();
			housePager.setCurrentItem(currentPos+1);
		}
	};
	
	/* Broadcast Receiver */
	private BroadcastReceiver refreshHouseBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Singleton.log("MainHouseFragment: get broadcast receiver!");
			
			try {
				String action = intent.getAction();
				if(BHConstants.BROADCAST_HOUSE_SEARCH_REFRESH_MAP.equals(action)) {
					//refresh map, close list, get data
					//needSwitchToLatLngMode = false;//for reset解鎖
					startFetchMapFromFilter();
				}else if (BHConstants.BROADCAST_HOUSE_SEARCH_REFRESH_LIST.equals(action)){
					//create or refresh list, get data
					//needSwitchToLatLngMode = false;//for reset解鎖
					startFetchListFromFilter();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	private void reloadBottomViews(){
		if (this.currentDisplayMode==BHConstants.DISPLAY_MODE_MAP){
			leftButton.setBackgroundResource(R.drawable.ic_site);
			leftTextView.setText(getString(R.string.current_location));
			
			rightButton.setBackgroundResource(R.drawable.ic_list);
			rightTextView.setText(getString(R.string.switch_to_list));
		}else{
			leftButton.setBackgroundResource(R.drawable.ic_order);
			int selectedOrder = SearchInfo.getInstance().selectedOrder;
			leftTextView.setText(orderView.getSelectedItemText(selectedOrder));
			
			rightButton.setBackgroundResource(R.drawable.ic_map);
			rightTextView.setText(getString(R.string.switch_to_map));
		}
	}
	
	public void closePagerAndResetMarker(){
		hideHousePager();
		hidePricePager();
		
		if (currentMapMarker!=null){
			this.resetHouseMarkerIcon(currentMapMarker, false);
			this.resetPriceMarkerIcon(currentMapMarker, false);
		}
	}
	
	/* Helper Method */
	private double measureDistanceFromZoom(float zoom){
		//TODO:改成更好的寫法
		//且是否依據不同行政區域，採用不同的策略？（因各區域房屋疏密不同）
		double distance = 0.05;
		if (zoom >= MAX_ZOOM && zoom<=11.0){
			distance = 0.1;
		}else if (zoom > 11.0 && zoom<=12.0){
			distance = 0.05;
		}else if (zoom > 12.0 && zoom<=13.0){
			distance = 0.03;
		}else if (zoom > 13.0 && zoom<=14.0){
			distance = 0.01;
		}else if (zoom > 14.0 && zoom<=15.0){
			distance = 0.008;
		}else if (zoom > 15.0 && zoom<=16.0){
			distance = 0.005;
		}else if (zoom > 16.0 && zoom<=17.0){
			distance = 0.003;
		}else if (zoom > 17.0 && zoom<=18.0){
			distance = 0.001;
		}else if (zoom > 18.0){
			distance = 0;
		}
		
		return distance;
	}
	
	private Marker addAnchorIcon(IconGenerator iconFactory, String text, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions()
        			.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text)))
                .position(position)
                .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        return getMap().addMarker(markerOptions);
    }
	
	private Marker addCircleIcon(String text, LatLng position) {
		return mMap.addMarker(new MarkerOptions()
				   .position(position)
				   .icon(BitmapDescriptorFactory.fromBitmap(MarkerUtils.writeTextOnDrawable(getActivity(),R.drawable.pin_round, text))));
    }
	
	private void setCircleIcon(Marker marker, int resourceId, String text){
		marker.setIcon(BitmapDescriptorFactory.fromBitmap(MarkerUtils.writeTextOnDrawable(getActivity(),resourceId, text)));
	}
	
	private void setAnchorIcon(Marker marker, IconGenerator iconFactory, String text){
		marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text)));
	}
	
	private void resetHouseMarkerIcon(Marker marker, Boolean isPressed){
		HouseMarker houseMarker = markerHashMap.get(marker);
		
		if (houseMarker!=null){
			int houseCount = houseMarker.houseNOs.length();
			if (houseCount==1){
				if (isPressed){
					setAnchorIcon(marker, anchorPressedIconFactory, String.valueOf(houseMarker.label));
				}else{
					setAnchorIcon(marker, anchorIconFactory, String.valueOf(houseMarker.label));
				}
			}else{
				if (isPressed){
					setCircleIcon(marker, R.drawable.pin_round_pressed, String.valueOf(houseCount));
				}else{
					setCircleIcon(marker, R.drawable.pin_round, String.valueOf(houseCount));
				}
			}
		}
	}
	
	private void resetPriceMarkerIcon(Marker marker, Boolean isPressed){
		if (marker==null) return;
		if (priceMarkerHashMap.get(marker)==null) return;

		PriceMarker priceMarker = priceMarkerHashMap.get(marker);
		try{
			int houseCount = priceMarker.priceJSONArray.length();
			if (isPressed){
				setPriceIcon(marker, pricePressedIconFactory, String.valueOf(houseCount), priceMarker.label);
			}else{
				setPriceIcon(marker, priceIconFactory, String.valueOf(houseCount), priceMarker.label);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/* Animation */
	private void initAnimation(){
		this.slideInFromBottomAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_bottom);
		this.priceSlideOutToBottomAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_bottom);
		this.priceSlideOutToBottomAnimation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				pricePager.setVisibility(View.GONE);
			}
		});
		this.houseSlideOutToBottomAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_bottom);
		this.houseSlideOutToBottomAnimation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				housePager.setVisibility(View.GONE);
			}
		});
		
		hideHousePager();
		hidePricePager();
	}
	
	private void showHousePager(){
		if (housePager.getVisibility()==View.GONE){
			housePager.setVisibility(View.VISIBLE);
			housePager.startAnimation(this.slideInFromBottomAnimation);
		}
	}
	
	private void hideHousePager(){
		if (housePager.getVisibility()==View.VISIBLE){
			housePager.startAnimation(this.houseSlideOutToBottomAnimation);
		}
	}
	
	private void showPricePager(){
		if (pricePager.getVisibility()==View.GONE){
			pricePager.setVisibility(View.VISIBLE);
			pricePager.startAnimation(this.slideInFromBottomAnimation);
		}
	}

	private void hidePricePager(){
		if (pricePager.getVisibility()==View.VISIBLE){
			pricePager.startAnimation(this.priceSlideOutToBottomAnimation);
			
			//for price remark: outlier
			this.remarkContainer.setVisibility(View.GONE);
		}
	}
	
	private Marker addPriceIcon(PriceIconGenerator iconFactory, String count, String unitPrice, LatLng position) {
		MarkerOptions markerOptions = new MarkerOptions()
		.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(count, unitPrice)))
		.position(position);

		return getMap().addMarker(markerOptions);
	}
	
	private void setPriceIcon(Marker marker, PriceIconGenerator iconFactory, String count, String unitPrice){
		marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(count, unitPrice)));
	}
	
	private void cleanPriceMarker(){
		for(Entry<Marker, PriceMarker> entry : priceMarkerHashMap.entrySet()) {
			Marker key = entry.getKey();
			key.remove();
		}
		priceMarkerHashMap.clear();
	}
	
	/* Price */
	private void fetchPriceFromServer(){
		VisibleRegion vr = mMap.getProjection().getVisibleRegion();
		float left = (float) vr.latLngBounds.southwest.longitude;
		float top = (float) vr.latLngBounds.northeast.latitude;
		float right = (float) vr.latLngBounds.northeast.longitude;
		float bottom = (float) vr.latLngBounds.southwest.latitude;

		String latlngString = String.valueOf(bottom)+","+String.valueOf(left)+"_"+String.valueOf(top)+","+String.valueOf(right);
		//Store To SearchInfo
		//PriceSearchInfo.getInstance().boundLatLngString = latlngString;

		float zoom = mMap.getCameraPosition().zoom;
		Log.e("test", "zoom: "+ zoom);
		double distance = this.measureDistanceFromZoom(zoom);
		Log.e("test", "distance: "+distance);

		String filterParams = SearchInfo.getInstance().getFilterParams(false, false);

		PriceService.getPriceMarkers(latlngString, distance, filterParams, new PriceMarkerCallback(){

			@Override
			public void onResult(boolean success, ArrayList<PriceMarker> priceMarkers) {
				if (success){
					Log.e("test", "price count: "+priceMarkers.size());
					if (getActivity()!=null){
						//mMap.clear();
						//從map上移除price marker
						cleanPriceMarker();

//						if (priceMarkers.size()==0){
//							showActivityToast(R.string.no_result_under_filter);
//							return;
//						}

						Marker marker;
						for (int i=0; i<priceMarkers.size(); i++){
							int houseCount = priceMarkers.get(i).priceJSONArray.length();
							marker = addPriceIcon(priceIconFactory, String.valueOf(houseCount), priceMarkers.get(i).label, new LatLng(priceMarkers.get(i).lat, priceMarkers.get(i).lng));
							//							if (houseCount==1){
							//								marker = addAnchorIcon(anchorIconFactory, String.valueOf(priceMarkers.get(i).label), new LatLng(priceMarkers.get(i).lat, priceMarkers.get(i).lng));
							//							}else{
							//								marker = addCircleIcon(String.valueOf(priceMarkers.get(i).label), new LatLng(priceMarkers.get(i).lat, priceMarkers.get(i).lng));
							//							}
							priceMarkerHashMap.put(marker, priceMarkers.get(i));
						}
					}
				}else{
					Log.e("test", "getPriceList fail!");
					showActivityToast(R.string.no_network_please_check);
				}

			}
		});

	}
	
	/* Price Adapter */
	PagerAdapter pricePagerAdapter = new PagerAdapter() {

		public Object instantiateItem(View container, int position) {
			Log.e("test", "instantiateItem: position:"+position);
			final DealPriceRow dealPriceRow = new DealPriceRow(getActivity());

			pricePager.addView(dealPriceRow);

			final Price price = priceListForPager.get(position);
			dealPriceRow.reloadCell(price);
			dealPriceRow.reloadArrow(position, priceListForPager.size());

			return dealPriceRow;
		};

		public void destroyItem(View container, int position, Object childView) {
			((ViewPager) container).removeView((View) childView);
		}

		public boolean isViewFromObject(View container, Object childView) {
			return container == ((View) childView);
		}

		public Parcelable saveState() {
			return null;
		}

		public int getCount() {
			return priceListForPager.size();
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

	};
	
	/* Fetch data */
	private void fetchMorePriceByNOs(final Boolean refresh){
		//page: start from 0

		//special issue: 
		final int tempHouseListSize = (refresh)? 0: priceListForPager.size();

		//get all houseNOs
		if (currentMapMarker==null) return;
		PriceMarker priceMarker = priceMarkerHashMap.get(currentMapMarker);
		if (priceMarker==null) return;
		JSONArray houseNOs = priceMarker.priceJSONArray;
		if (houseNOs.length()==tempHouseListSize) return;

		//calculate params: page,limit
		final int limit = BHConstants.SIMPLE_HOUSE_FETCH_LIMIT;
		int page=0;
		if (!refresh){
			page = tempHouseListSize/limit;
			if (page<=0) return;
		}

		//檢查是否還有更多資料可以取，如果沒有，return
		if ((page*limit)>=houseNOs.length()) return;

		//還有資料，取出需要區間的houseNOs
		int startIndex = page*limit;
		JSONArray subHouseNos = new JSONArray();
		try{
			for (int i=startIndex; i<startIndex+limit; i++){
				if (i>=houseNOs.length()) {
					Log.e("test", "ready to get: "+startIndex+"~"+(i-1));
					break;
				}
				subHouseNos.put(houseNOs.get(i));
			}
		}catch(JSONException e){
			e.printStackTrace();
		}

		//轉成用逗號分隔的string
		String NOs = "";
		try {
			for (int i=0; i<subHouseNos.length(); i++){
				NOs += subHouseNos.get(i);
				if (i!=subHouseNos.length()-1)  NOs += ",";
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.e("test", "NOs: "+NOs);

		final int savedPage = page;
		final Marker savedCurrentMapParker = currentMapMarker;

		PriceService.getPriceDetail(NOs, new PriceListCallback(){

			@Override
			public void onResult(boolean success, ArrayList<Price> prices) {
				if (success){
					Log.e("test", "get simple houses success! page:"+savedPage+" count:"+prices.size());

					//check抓到資料時，是否還是當初的那個marker
					if (savedCurrentMapParker!=currentMapMarker) return;

					//check抓到此page的資料，是否已經有被抓過並放入了
					if (savedPage*limit<tempHouseListSize) return;

					if (refresh) priceListForPager.clear();
					priceListForPager.addAll(prices);

					pricePagerAdapter.notifyDataSetChanged();
					if (savedPage==0){
						pricePager.setCurrentItem(0);
						Log.e("test", "setCurrentItem:0");
					}

					//tricky:onPageSelected沒有被trigger,
					//所以直接檢查outlier是否顯示或隱藏
					showOrHideOutlier(0);
					
					//TODO
					//if (pricePager.getVisibility()==View.GONE){
						showPricePager();
						hideHousePager();
					//}

				}else{
					Log.e("test", "get prices fail!");
				}
			}
		});

	}
	
	/* Remark: outlier */
	private void showOrHideOutlier(int position){
		if (position<priceListForPager.size()){
			Boolean isOutlier = priceListForPager.get(position).getBooleanData(BHConstants.JSON_KEY_OUTLIER);
			if (isOutlier){
				remarkContainer.setVisibility(View.VISIBLE);
			}else{
				remarkContainer.setVisibility(View.GONE);
			}
			Log.e("test", "isOutlier:"+isOutlier);
		}
	}
	
}
