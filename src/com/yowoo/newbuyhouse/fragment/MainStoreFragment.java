package com.yowoo.newbuyhouse.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.Constants;
import com.yowoo.newbuyhouse.MainActivity;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.Singleton;
import com.yowoo.newbuyhouse.model.Store;
import com.yowoo.newbuyhouse.network.StoreService;
import com.yowoo.newbuyhouse.network.StoreService.StoreListCallback;
import com.yowoo.newbuyhouse.store.StoreSearchInfo;
import com.yowoo.newbuyhouse.util.MarkerUtils;
import com.yowoo.newbuyhouse.view.OrderView;
import com.yowoo.newbuyhouse.view.StoreRow;
import com.yowoo.newbuyhouse.view.StoreRow.StoreRowCallback;

public class MainStoreFragment extends MyFragment implements OnCameraChangeListener, 
OnMapReadyCallback{

	public static Fragment newInstance(Context context) {
		MainStoreFragment f = new MainStoreFragment();

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private static View root;
	private GoogleMap mMap;//is framgnet
	private RelativeLayout leftContainer, rightContainer;
	private Button leftButton, rightButton;
	private TextView leftTextView, rightTextView;
	private OrderView orderView;

	private int currentDisplayMode=0;
	MainStoreListFragment mainStoreListFragment;
	ViewPager storePager;

	//需同時update的data
	private ArrayList<Store> storeListForMap = new ArrayList<Store>();
	private ArrayList<Store> storeListForPager = new ArrayList<Store>();
	private HashMap<Marker, Store> markerHashMap = new HashMap<Marker, Store>();
	private Marker currentMapMarker;

	//marker icon
	IconGenerator anchorIconFactory, anchorPressedIconFactory;

	//for animation
	private Animation slideInFromBottomAnimation, slideOutToBottomAnimation;
		
	
	private final float MAX_ZOOM = 13.0f;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		if (root != null) {
			ViewGroup parent = (ViewGroup) root.getParent();
			if (parent != null)
				parent.removeView(root);
		}
		try {
			root = inflater.inflate(R.layout.fragment_main_store, container, false);
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
		
		//set list
		this.setUpListIfNeeded();

		//set map
		this.setUpMapIfNeeded();
		getMap().setOnCameraChangeListener(this);
		getMap().setMyLocationEnabled(true);
        getMap().getUiSettings().setMyLocationButtonEnabled(false);
        
		StoreSearchInfo.getInstance().centerPos = getMyCurrentLocation();
		setZoomAndStartMoveCamera();

		//set marker factory
		anchorIconFactory = new IconGenerator(getActivity());
		anchorIconFactory.setColor(getResources().getColor(R.color.pin_gray_color));
		anchorIconFactory.setTextAppearance(R.style.MarkerWhiteText);

		anchorPressedIconFactory = new IconGenerator(getActivity());
		anchorPressedIconFactory.setColor(getResources().getColor(R.color.pin_green_color));
		anchorPressedIconFactory.setTextAppearance(R.style.MarkerWhiteText);

		//set pager
		storePager = (ViewPager) root.findViewById(R.id.housePager);
		storePager.setAdapter(housePagerAdapter);
		storePager.setOffscreenPageLimit(3);

		storePager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				Log.e("test", "onPageSelected: "+position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}

			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});

		//init views
		reloadBottomViews();

		//set listener
		setListener();

		//register broadcast receiver
		LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		broadcastManager.registerReceiver(refreshStoreBroadcastReceiver, new IntentFilter(BHConstants.BROADCAST_STORE_SEARCH_REFRESH_MAP));

		//init animation
		initAnimation();
		
		return root;
	}


	@Override
	public void onResume() {
		super.onResume();

		Log.e("test", "MainHouseFragment: onResume!");
		reloadBottomViews();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e("test", "MainHouseFragment: onDestroy!");

		//((MainActivity) activity).onSectionAttached(getArguments().getInt(Constants.FRAGMENT_ARG_POSITION));

		LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		broadcastManager.unregisterReceiver(refreshStoreBroadcastReceiver);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(Constants.FRAGMENT_ARG_POSITION));
	}

	private void setZoomAndStartMoveCamera(){
		//在執行此方法前，不管是區域搜尋模式或定位模式，都要先算好經緯度，存入searchInfo

		//根據各種mode，決定要設定何種zoom level
		float zoom = 14.0f;
		moveCamera(StoreSearchInfo.getInstance().centerPos, zoom);

	}


	private void moveCamera(LatLng centerPos, float zoom){
		getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(centerPos, zoom));

	}

	private void fetchStoreFromServer(){
		VisibleRegion vr = mMap.getProjection().getVisibleRegion();
		float left = (float) vr.latLngBounds.southwest.longitude;
		float top = (float) vr.latLngBounds.northeast.latitude;
		float right = (float) vr.latLngBounds.northeast.longitude;
		float bottom = (float) vr.latLngBounds.southwest.latitude;

		String latlngString = String.valueOf(bottom)+","+String.valueOf(left)+"_"+String.valueOf(top)+","+String.valueOf(right);
		
		float zoom = mMap.getCameraPosition().zoom;
		Log.e("test", "zoom: "+ zoom);
		//double distance = this.measureDistanceFromZoom(zoom);
		//Log.e("test", "distance: "+distance);

		StoreService.getStoreList(latlngString, new StoreListCallback(){

			@Override
			public void onResult(boolean success, ArrayList<Store> stores) {
				if (success){
					Log.e("test", "store count: "+stores.size());
					if (getActivity()!=null){
						mMap.clear();
						markerHashMap.clear();
						storeListForMap = stores;

						Marker marker;
						for (int i=0; i<stores.size(); i++){
							marker = addStoreIcon("", new LatLng(stores.get(i).lat, stores.get(i).lng));
							markerHashMap.put(marker, stores.get(i));
						}

					}
				}else{
					Log.e("test", "getStoreList fail!");
					showActivityToast(R.string.no_network_please_check);
				}
			}
		});


	}

	/* House Adapter */
	PagerAdapter housePagerAdapter = new PagerAdapter() {

		public Object instantiateItem(View container, int position) {
			final StoreRow storeRow = new StoreRow(getActivity());

			storePager.addView(storeRow);

			final Store store = storeListForPager.get(position);
			storeRow.reloadCell(store);
			storeRow.setStoreRowCallback(storeRowCallback);

			return storeRow;
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
			return storeListForPager.size();
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

	};

	private StoreRowCallback storeRowCallback = new StoreRowCallback(){
		@Override
		public void onClickCall(String tel) {
			Intent intentDial = new Intent("android.intent.action.CALL",Uri.parse("tel:"+tel));
		    startActivity(intentDial);
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
	
	protected Fragment getMapFragment(){
		return ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
	}
	
	/* Set List*/
	private void setUpListIfNeeded() {
		if (mainStoreListFragment != null) {
			return;
		}
		mainStoreListFragment = ((MainStoreListFragment) getChildFragmentManager().findFragmentById(R.id.listFragment));
	}
	
	protected Fragment getListFragment(){
		setUpListIfNeeded();
		return mainStoreListFragment;
	}
	
	private void startFetchMapFromFilter(){
		if (currentDisplayMode==BHConstants.HOUSE_MODE_MAP){
			//在執行此方法前，各種設定已經設好
			//reset views
			this.closePagerAndResetMarker();
		}else{
			//目前是list, 需切換到map
			this.switchToMap();
		}

		//切換display mode
		currentDisplayMode = BHConstants.HOUSE_MODE_MAP;

		//refresh map, get data
		setZoomAndStartMoveCamera();

		//reload bottom view
		reloadBottomViews();

	}

	private void switchDisplayMode(){
		if (currentDisplayMode==BHConstants.HOUSE_MODE_MAP){
			//切換至list
			mainStoreListFragment.reloadData(storeListForMap);
			switchToList();
		}else{
			//切換至map
			switchToMap(); 
		}

		//切換DisplayMode
		currentDisplayMode = (currentDisplayMode+1)%2;
	}
	
	private void switchToMap(){
		FragmentTransaction tx = getChildFragmentManager().beginTransaction();
		tx.hide(getListFragment());
		tx.show(getMapFragment());
		tx.commitAllowingStateLoss(); 
	}
	
	private void switchToList(){
		FragmentTransaction tx = getChildFragmentManager().beginTransaction();
		tx.show(getListFragment());
		tx.hide(getMapFragment());
		tx.commitAllowingStateLoss();
	}

	/* Listener */
	private void setListener(){
		rightContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//切換map & list模式
				switchDisplayMode();
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
					//列表模式時：do nothing
				}
			}
		});

		getMap().setOnMarkerClickListener(new OnMarkerClickListener(){

			@Override
			public boolean onMarkerClick(Marker marker) {
				//更新marker顏色
				resetMarkerIcon(currentMapMarker, false);
				resetMarkerIcon(marker, true);

				Store store = markerHashMap.get(marker);
				Log.e("test", "houseNOs: "+store.name.toString());
				currentMapMarker = marker;

				storeListForPager.clear();
				storeListForPager.add(store);
				housePagerAdapter.notifyDataSetChanged();
				
				showStorePager();
				//storePager.setVisibility(View.VISIBLE);
					
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



	/* Camera Listner */
	@Override
	public void onCameraChange(CameraPosition cameraPos) {
		//關閉housePager, reset marker
		closePagerAndResetMarker();
		
		if (cameraPos.zoom < MAX_ZOOM){
	        mMap.animateCamera(CameraUpdateFactory.zoomTo(MAX_ZOOM));
	        return;
		}

		fetchStoreFromServer();
	}

	/* Map Listener */
	@Override
	public void onMapReady(GoogleMap map) {

	}

	

	/* Broadcast Receiver */
	private BroadcastReceiver refreshStoreBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Singleton.log("MainStoreFragment: get broadcast receiver!");

			try {
				String action = intent.getAction();
				if(BHConstants.BROADCAST_STORE_SEARCH_REFRESH_MAP.equals(action)) {
					//refresh map, close list, get data
					startFetchMapFromFilter();
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
			leftButton.setBackgroundDrawable(null);
			leftTextView.setText("");

			rightButton.setBackgroundResource(R.drawable.ic_map);
			rightTextView.setText(getString(R.string.switch_to_map));
		}
	}

	public void closePagerAndResetMarker(){
		
		//storePager.setVisibility(View.GONE);
		hideStorePager();
		
		if (currentMapMarker!=null){
			this.resetMarkerIcon(currentMapMarker, false);
		}
	}

	/* Helper Method */
//	private double measureDistanceFromZoom(float zoom){
//		//TODO:改成更好的寫法
//		//且是否依據不同行政區域，採用不同的策略？（因各區域房屋疏密不同）
//		double distance = 0.05;
//		if (zoom > 12.0 && zoom<=13.0){
//			distance = 0.03;
//		}else if (zoom > 13.0 && zoom<=14.0){
//			distance = 0.01;
//		}else if (zoom > 14.0 && zoom<=15.0){
//			distance = 0.008;
//		}else if (zoom > 15.0 && zoom<=16.0){
//			distance = 0.005;
//		}else if (zoom > 16.0 && zoom<=18.0){
//			distance = 0.001;
//		}else if (zoom > 18.0){
//			distance = 0;
//		}
//
//		return distance;
//	}

	private void resetMarkerIcon(Marker marker, Boolean isPressed){
		if (marker==null) return;
		if (markerHashMap.get(marker)==null) return;
		
		try{
			if (isPressed){
				setStoreIcon(marker, R.drawable.pin_store_pressed, "");
			}else{
				setStoreIcon(marker, R.drawable.pin_store, "");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private Marker addStoreIcon(String text, LatLng position) {
		return mMap.addMarker(new MarkerOptions()
				   .position(position)
				   .icon(BitmapDescriptorFactory.fromBitmap(MarkerUtils.writeTextOnDrawable(getActivity(),R.drawable.pin_store, text))));
    }

	private void setStoreIcon(Marker marker, int resourceId, String text){
		marker.setIcon(BitmapDescriptorFactory.fromBitmap(MarkerUtils.writeTextOnDrawable(getActivity(), resourceId, text)));
	}
	
	/* Animation */
	private void initAnimation(){
		this.slideInFromBottomAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_bottom);
		this.slideOutToBottomAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_bottom);
		this.slideOutToBottomAnimation.setAnimationListener(new Animation.AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				storePager.setVisibility(View.GONE);
			}
		});
		
		hideStorePager();
	}
	
	private void showStorePager(){
		if (storePager.getVisibility()==View.GONE){
			storePager.setVisibility(View.VISIBLE);
			storePager.startAnimation(this.slideInFromBottomAnimation);
		}
	}
	
	private void hideStorePager(){
		if (storePager.getVisibility()==View.VISIBLE){
			storePager.startAnimation(this.slideOutToBottomAnimation);
		}
	}
	

}
