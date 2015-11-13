package com.yowoo.newbuyhouse.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.Constants;
import com.yowoo.newbuyhouse.MainActivity;
import com.yowoo.newbuyhouse.login.LoginInfo;
import com.yowoo.newbuyhouse.login.UserConstants;
import com.yowoo.newbuyhouse.model.House;
import com.yowoo.newbuyhouse.model.HouseDetail;
import com.yowoo.newbuyhouse.network.HouseService;
import com.yowoo.newbuyhouse.network.HouseService.DetailHouseCallback;
import com.yowoo.newbuyhouse.network.HouseService.HouseListCallback;
import com.yowoo.newbuyhouse.network.HouseService.SimpleHouseCallback;
import com.yowoo.newbuyhouse.network.TrackService;
import com.yowoo.newbuyhouse.network.TrackService.SubscribeCallback;
import com.yowoo.newbuyhouse.slider.AutoScrollViewPager;
import com.yowoo.newbuyhouse.slider.CollectPagerAdapter;
import com.yowoo.newbuyhouse.slider.MyAutoScrollViewPager;
import com.yowoo.newbuyhouse.track.TrackCollectActivity;
import com.yowoo.newbuyhouse.track.TrackSearchActivity;
import com.yowoo.newbuyhouse.track.TrackSubscribeActivity;
import com.yowoo.newbuyhouse.track.TrackVisitActivity;
import com.yowoo.newbuyhouse.view.CollectRow;
import com.yowoo.newbuyhouse.view.HouseRow;
import com.yowoo.newbuyhouse.view.HouseRow.HouseRowCallback;
import com.yowoo.newbuyhouse.view.TrackNoResultRow;

public class MainTrackFragment extends MyFragment{

	public static Fragment newInstance(Context context) {
		MainTrackFragment f = new MainTrackFragment();

		return f;
	}

	private MyAutoScrollViewPager viewPager;
	private List<HouseDetail> houseList = new ArrayList<HouseDetail>();
	private CollectPagerAdapter collectPagerAdapter;

	//
	private RelativeLayout subscribeContainer, visitContainer, searchContainer, collectContainer, collectNoResultContainer, collectMoreContainer;
	private TextView subscribeCountTextView, visitCountTextView, searchCountTextView;
	private Button subscribeArrowButton, visitArrowButton, searchArrowButton;
	private HouseRow subscribeHouseRow, visitHouseRow, searchHouseRow;
	private TrackNoResultRow subscribeTrackNoResultRow, visitTrackNoResultRow, searchTrackNoResultRow;
	private TextView collectNoResultTextView;
	private CollectRow collectRow;

	private List<House> visitHouseList = new ArrayList<House>();

	//tag for check whether it need re-fetch data or not
	private String history_collect = "";
	private String history_visit = "";
	private int history_subscribe_count = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_main_track, null);

		viewPager = (MyAutoScrollViewPager)root.findViewById(R.id.collectViewPager);

		viewPager.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("test", "viewPager: onClick()");
		           
//				Intent intent = new Intent(getActivity(), TrackCollectActivity.class);
//				startActivity(intent);
			}
		});
		
		viewPager.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.e("test", "viewPager:onTouch");
				return false;
			}
		});

		setViews(root);

		//set Listener
		setListener();

		//fetchDataOnCreate();

		return root;
	}

	private void setViews(ViewGroup root){
		subscribeContainer = (RelativeLayout)	root.findViewById(R.id.subscribeContainer);	
		subscribeCountTextView = (TextView)	root.findViewById(R.id.subscribeCountTextView);	
		subscribeArrowButton = (Button)	root.findViewById(R.id.subscribeArrowButton);	
		subscribeHouseRow = (HouseRow)	root.findViewById(R.id.subscribeHouseRow);	
		subscribeTrackNoResultRow = (TrackNoResultRow)	root.findViewById(R.id.subscribeTrackNoResultRow);	

		visitContainer = (RelativeLayout)	root.findViewById(R.id.visitContainer);	
		visitCountTextView = (TextView)	root.findViewById(R.id.visitCountTextView);	
		visitArrowButton = (Button)	root.findViewById(R.id.visitArrowButton);	
		visitHouseRow = (HouseRow)	root.findViewById(R.id.visitHouseRow);	
		visitTrackNoResultRow = (TrackNoResultRow)	root.findViewById(R.id.visitTrackNoResultRow);	

		searchContainer = (RelativeLayout)	root.findViewById(R.id.searchContainer);	
		searchCountTextView = (TextView)	root.findViewById(R.id.searchCountTextView);	
		searchArrowButton = (Button)	root.findViewById(R.id.searchArrowButton);	
		searchHouseRow = (HouseRow)	root.findViewById(R.id.searchHouseRow);	
		searchTrackNoResultRow = (TrackNoResultRow)	root.findViewById(R.id.searchTrackNoResultRow);	

		collectContainer = (RelativeLayout)	root.findViewById(R.id.collectContainer);	
		collectNoResultContainer = (RelativeLayout)	root.findViewById(R.id.collectNoResultContainer);	
		collectNoResultTextView = (TextView) root.findViewById(R.id.collectNoResultTextView);
		collectMoreContainer = (RelativeLayout)	root.findViewById(R.id.collectMoreContainer);	
		collectRow = (CollectRow) root.findViewById(R.id.collectRow);
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(Constants.FRAGMENT_ARG_POSITION));
	}


	@Override
	public void onResume() {
		super.onResume();
		Log.e("test", "MainTrackFragment: onResume");
		
		//fetch data
		fetchDataOnResume();

		//register broadcast receiver
		LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		broadcastManager.registerReceiver(userStatusBroadcastReceiver, new IntentFilter(BHConstants.BROADCAST_USER_STATUS));

	}
	
	@Override
	public void onPause() {
		super.onPause();
		//TODO:需要停嗎？ stop auto scroll when onPause
		//viewPager.stopAutoScroll();
		Log.e("test", "MainTrackFragment: onPause");
		
		LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		broadcastManager.unregisterReceiver(userStatusBroadcastReceiver);
				
	}

	private void fetchDataOnCreate(){
		fetchSearchData();

	}

	private void fetchDataOnResume(){
		//test
		fetchSearchData();
		
		fetchCollectData();
		fetchSubscribeData();
		fetchVisitData();
	}

	private void fetchSubscribeData(){
		//check login
		if (!LoginInfo.getInstance().isLogined()){
			reloadSubscribeNoResult(false, getString(R.string.track_notify_no_record));
			return;
		}

		if (history_subscribe_count==0){
			reloadSubscribeNoResult(false, getString(R.string.track_notify_no_record));
		}

		TrackService.getSubscribes(new SubscribeCallback(){
			@Override
			public void onResult(boolean success, String debugMessage, final JSONArray subscribes) {
				Log.e("test","getSubscribe:"+success);
				try{
					if (!success){
						reloadSubscribeNoResult(false, getString(R.string.track_error_no_result));
						return;
					}

					//成功fetch, 但無訂閱條件
					if (subscribes.length()==0){
						history_subscribe_count = 0;
						reloadSubscribeNoResult(false, getString(R.string.track_notify_no_record));
						return;
					}

					//有訂閱條件
					JSONObject lastSubObject = subscribes.getJSONObject(0);
					String filterParams = lastSubObject.getString(BHConstants.JSON_KEY_PARAMS);

					//用訂閱條件，抓取第一個房屋物件
					HouseService.getHouseList(0, 5, filterParams, "", new HouseListCallback(){

						@Override
						public void onResult(boolean success, ArrayList<House> houses,
								int total, int page, int totalPage) {
							try{
								if (!success){
									reloadSubscribeNoResult(false, getString(R.string.track_error_no_result));
								}else{
									if (houses.size()==0){
										//雖然有抓到瀏覽物件id, 但去抓資料時，卻沒有抓到，沒有房屋資料可顯示
										//reloadSubscribeNoResult(false, getString(R.string.track_search_no_match_house));
										reloadSubscribeView(false, true, true, getString(R.string.track_search_no_match_house));
									}else{
										//有房屋資料可顯示！
										subscribeHouseRow.reloadCell(houses.get(0));

										//reload view
										history_subscribe_count = subscribes.length();
										reloadSubscribeNoResult(true, "");
										subscribeCountTextView.setText(String.valueOf(subscribes.length()));
									}
								}
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
					reloadSubscribeNoResult(false, getString(R.string.track_error_no_result));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}


	private void fetchCollectData(){
		final String favHouseNos = 
				LoginInfo.getInstance().getFavHouseNOs(0, UserConstants.MAX_SHOW_COLLECT_HOUSE);
		
		Log.e("test", "test1:"+favHouseNos);
		
		//check: 沒有收藏物件,不需抓資訊
		if (favHouseNos.equals("")){
			Log.e("test", "test2:"+favHouseNos);
			reloadCollectViews(0, getString(R.string.track_collect_no_record));
			return;
		}

		//check: 收藏物件若沒有變更，不需重抓資訊reload
		if (history_collect.equals(favHouseNos)){
			Log.e("test", "test3: history:"+history_collect);
			reloadCollectViews(houseList.size(), "");
			return;
		}

		String returnParams = 
				"NO,lat,lng,name,price,priceFirst,discount,address,type,imgDefault,areaBuilding,layout,age"
						+",bigImg";

		HouseService.getCustomHouse(favHouseNos, returnParams, new DetailHouseCallback(){
			@Override
			public void onResult(boolean success, ArrayList<HouseDetail> houses) {
				try{
					if (success){
						houseList = houses;
						history_collect = favHouseNos;
					}

					if (houseList.size()>0){
						//set viewpager & adpater
						new Handler(Looper.getMainLooper()).postDelayed(new Runnable(){
							@Override
							public void run() {
								setRecycleViewPager();
								if (houseList.size()!=1){
									viewPager.startAutoScroll();
								}else{
									viewPager.stopAutoScroll();
									collectRow.reloadCell(houseList.get(0));
								}

								reloadCollectViews(houseList.size(), "");
							}
						}, 500);
					}else{
						//顯示無收藏物件的提醒
						Log.e("test","無收藏物件"); 
						reloadCollectViews(0, getString(R.string.track_collect_no_record));
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});

	}

	private void fetchVisitData(){

		//check是否有瀏覽物件，若沒有，直接顯示hint
		int count = LoginInfo.getInstance().visitArrayList.size();
		if (count==0){
			reloadVisitNoResult(false, getString(R.string.track_visit_no_record));
			return;
		}

		//有瀏覽物件
		final String visitHouseNOs = LoginInfo.getInstance().getVisitHouseNOs();

		//check: 收藏物件若沒有變更，不需重抓資訊reload
		if (history_visit.equals(visitHouseNOs)){
			return;
		}

		HouseService.getSimplePlusHouse(visitHouseNOs, new SimpleHouseCallback(){
			@Override
			public void onResult(boolean success, ArrayList<House> houses) {
				try{
					if (!success){
						reloadVisitNoResult(false, getString(R.string.track_error_no_result));
					}else{
						history_visit = visitHouseNOs;

						if (houses.size()==0){
							//雖然有抓到瀏覽物件id, 但去抓資料時，卻沒有抓到，沒有房屋資料可顯示
							reloadVisitNoResult(false, getString(R.string.track_visit_no_record));
						}else{
							//有房屋資料可顯示！
							visitHouseList = houses;
							visitHouseRow.reloadCell(houses.get(0));

							//reload view
							reloadVisitNoResult(true, "");
							visitCountTextView.setText(String.valueOf(houses.size()));

						}
					}

				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	private void fetchSearchData(){

		//check是否有搜尋條件，若沒有，直接顯示hint
		int count = LoginInfo.getInstance().searchArrayList.size();
		searchCountTextView.setText(String.valueOf(count));
		if (count==0){
			reloadSearchNoResult(false, getString(R.string.track_search_no_record));
			return;
		}

		//有搜尋條件，立馬搜第一個
		String filterParams = LoginInfo.getInstance().getSearchFilterParams(0);

		//在等待資料回傳時，先顯示預設畫面
		reloadSearchView(false, true, true, getString(R.string.track_search_no_match_house));

		//抓取資料
		HouseService.getHouseList(0, 10, filterParams, "", new HouseListCallback(){

			@Override
			public void onResult(boolean success, ArrayList<House> houses,
					int total, int page, int totalPage) {
				try{
					if (!success){
						reloadSearchNoResult(false, getString(R.string.track_error_no_result));
					}else{
						if (houses.size()==0){
							//雖然有抓到瀏覽物件id, 但去抓資料時，卻沒有抓到，沒有房屋資料可顯示
							reloadSearchView(false, true, true, getString(R.string.track_search_no_match_house));
							//reloadSearchNoResult(false, getString(R.string.track_search_no_match_house));
						}else{
							//有房屋資料可顯示！
							searchHouseRow.reloadCell(houses.get(0));

							//reload view
							reloadSearchNoResult(true, "");
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}

			}
		});
	}

	private void reloadSubscribeNoResult(Boolean hasResult, String hint){
		int visibleType1, visibleType2;

		if (hasResult){
			visibleType1 = View.VISIBLE;
			visibleType2 = View.GONE;
		}else{
			visibleType1 = View.GONE;
			visibleType2 = View.VISIBLE;
		}

		subscribeHouseRow.setVisibility(visibleType1);
		subscribeTrackNoResultRow.setVisibility(visibleType2);
		subscribeArrowButton.setVisibility(visibleType1);
		subscribeCountTextView.setVisibility(visibleType1);

		subscribeTrackNoResultRow.setHint(hint);

	}

	private void reloadSubscribeView(Boolean showHouseRow, Boolean showNoResultRow, Boolean showCount, String hint){
		subscribeHouseRow.setVisibility(showHouseRow ? View.VISIBLE : View.GONE);
		subscribeTrackNoResultRow.setVisibility(showNoResultRow? View.VISIBLE : View.GONE);
		subscribeArrowButton.setVisibility(showCount ? View.VISIBLE : View.GONE);
		subscribeCountTextView.setVisibility(showCount ? View.VISIBLE : View.GONE);
		subscribeTrackNoResultRow.setHint(hint);
	}

	private void reloadVisitNoResult(Boolean hasResult, String hint){
		int visibleType1, visibleType2;

		if (hasResult){
			visibleType1 = View.VISIBLE;
			visibleType2 = View.GONE;
		}else{
			visibleType1 = View.GONE;
			visibleType2 = View.VISIBLE;
		}

		visitHouseRow.setVisibility(visibleType1);
		visitTrackNoResultRow.setVisibility(visibleType2);
		visitArrowButton.setVisibility(visibleType1);
		visitCountTextView.setVisibility(visibleType1);

		visitTrackNoResultRow.setHint(hint);

	}

	private void reloadSearchView(Boolean showHouseRow, Boolean showNoResultRow, Boolean showCount, String hint){
		searchHouseRow.setVisibility(showHouseRow ? View.VISIBLE : View.GONE);
		searchTrackNoResultRow.setVisibility(showNoResultRow? View.VISIBLE : View.GONE);
		searchArrowButton.setVisibility(showCount ? View.VISIBLE : View.GONE);
		searchCountTextView.setVisibility(showCount ? View.VISIBLE : View.GONE);
		searchTrackNoResultRow.setHint(hint);
	}

	private void reloadSearchNoResult(Boolean hasResult, String hint){
		int visibleType1, visibleType2;

		if (hasResult){
			visibleType1 = View.VISIBLE;
			visibleType2 = View.GONE;
		}else{
			visibleType1 = View.GONE;
			visibleType2 = View.VISIBLE;
		}

		searchHouseRow.setVisibility(visibleType1);
		searchTrackNoResultRow.setVisibility(visibleType2);
		searchArrowButton.setVisibility(visibleType1);
		searchCountTextView.setVisibility(visibleType1);

		searchTrackNoResultRow.setHint(hint);

	}

	private void reloadCollectViews(int collectCount, String hint){
		if (collectCount==0){
			collectNoResultTextView.setHint(hint);
			collectNoResultContainer.setVisibility(View.VISIBLE);
			collectMoreContainer.setVisibility(View.GONE);
			viewPager.setVisibility(View.GONE);
			collectRow.setVisibility(View.GONE);
			viewPager.stopAutoScroll();
		}else if (collectCount==1){
			collectNoResultContainer.setVisibility(View.GONE);
			collectMoreContainer.setVisibility(View.VISIBLE);
			viewPager.setVisibility(View.GONE);
			collectRow.setVisibility(View.VISIBLE);
		}else{
			collectNoResultContainer.setVisibility(View.GONE);
			collectMoreContainer.setVisibility(View.VISIBLE);
			viewPager.setVisibility(View.VISIBLE);
			collectRow.setVisibility(View.GONE);
		}
		
	}


	private void goVisitListAction(){
		if (LoginInfo.getInstance().visitArrayList.size()>0){
			Intent intent = new Intent(getActivity(), TrackVisitActivity.class);
			startActivity(intent);
		}else{
			showActivityToast(R.string.track_visit_no_record);
		}
	}

	private void goSearchListAction(){
		if (LoginInfo.getInstance().searchArrayList.size()>0){
			Intent intent = new Intent(getActivity(), TrackSearchActivity.class);
			startActivity(intent);
		}else{
			showActivityToast(R.string.track_search_no_record);
		}
	}

	private void goSubscribeListAction(){
		if (history_subscribe_count>0){
			Intent intent = new Intent(getActivity(), TrackSubscribeActivity.class);
			startActivity(intent);
		}else{
			showActivityToast(R.string.track_notify_no_record);
		}
	}


	private void setListener(){
		subscribeContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goSubscribeListAction();
			}
		});

		subscribeHouseRow.setHouseRowCallback(new HouseRowCallback(){
			@Override
			public void onClickHouse(String houseNO) {
				goSubscribeListAction();
			}

			@Override
			public void onClickPrevArrow() {}

			@Override
			public void onClickNextArrow() {}
		});

		visitContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goVisitListAction();
			}
		});

		visitHouseRow.setHouseRowCallback(new HouseRowCallback(){
			@Override
			public void onClickHouse(String houseNO) {
				goVisitListAction();
			}

			@Override
			public void onClickPrevArrow() {}

			@Override
			public void onClickNextArrow() {}
		});

		searchContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goSearchListAction();
			}
		});

		searchHouseRow.setHouseRowCallback(new HouseRowCallback(){
			@Override
			public void onClickHouse(String houseNO) {
				goSearchListAction();
			}

			@Override
			public void onClickPrevArrow() {}

			@Override
			public void onClickNextArrow() {}
		});
		
		collectRow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), TrackCollectActivity.class);
				startActivity(intent);
			}
		});

	}

	private void setRecycleViewPager(){
		collectPagerAdapter = new CollectPagerAdapter(getActivity(), houseList).setInfiniteLoop(true);
		viewPager.setAdapter(collectPagerAdapter);
		viewPager.setInterval(2000);
		viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_TO_PARENT);
	}
	
	/* Broadcast */
	private BroadcastReceiver userStatusBroadcastReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("test", "MainTrackFragment: onReceive");
			
			//已登出，將此頁local tag進行reset
			resetTrackLocalHistory();
			
			//reload data
			fetchDataOnResume();
		}
	};
	
	private void resetTrackLocalHistory(){
		history_collect = "";
		history_visit = "";
		history_subscribe_count = 0;
	}
	

}
