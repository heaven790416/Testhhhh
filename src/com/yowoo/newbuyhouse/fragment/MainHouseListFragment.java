package com.yowoo.newbuyhouse.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.LatLng;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.yowoo.newbuyhouse.BHConstants;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.SearchInfo;
import com.yowoo.newbuyhouse.house.HouseDetailActivity;
import com.yowoo.newbuyhouse.model.House;
import com.yowoo.newbuyhouse.network.HouseService;
import com.yowoo.newbuyhouse.network.HouseService.HouseListCallback;
import com.yowoo.newbuyhouse.view.HouseListRow;
import com.yowoo.newbuyhouse.view.HouseListRow.HouseListRowCallback;

public class MainHouseListFragment extends MyFragment{

	public static Fragment newInstance(Context context) {
		MainHouseListFragment f = new MainHouseListFragment();
 
        return f;
    }
 
	PullToRefreshListView houseListView;
	ArrayList<House> houseList = new ArrayList<House>();
	int currentPage = 0;
	int totalHouse = 0;
	int totalPage = 0;
	
	RelativeLayout houseCountContainer, noResultContainer;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_house_list, null);
        
        houseCountContainer = (RelativeLayout) rootView.findViewById(R.id.houseCountContainer);
        noResultContainer = (RelativeLayout) rootView.findViewById(R.id.noResultContainer);
        
        houseListView = (PullToRefreshListView) rootView.findViewById(R.id.houseListview);
        houseListView.setMode(Mode.PULL_FROM_START);
        houseListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
            		
            		Log.e("test", "currentMode: "+ houseListView.getCurrentMode().toString());
            		//pull down to refresh
            		fetchData(true);
            		
            }
            
        });
        
        //set adapter
        houseListView.setAdapter(houseAdapter);
        
        //get data
        fetchData(true);
        
        return rootView;
    }
    
    @Override
	public void onResume() {
		super.onResume();
		
		Log.e("test", "MainHouseListFragment: onResume!");
    
    }
    
    
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		//((MainActivity) activity).onSectionAttached(getArguments().getInt(Constants.FRAGMENT_ARG_POSITION));
		
	}
    
    @Override
    public void onPause() {
		super.onPause();
		
	}
    
    BaseAdapter houseAdapter = new BaseAdapter() {
		@Override
		public int getCount() {
			return houseList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View rowView, ViewGroup parent) {
			
			if (position == houseList.size()-2){
				fetchData(false);
			}
			
			if(rowView==null||rowView.getClass().equals(HouseListRow.class)==false) {
				rowView = new HouseListRow(getActivity());
			}

			HouseListRow houseListRow = (HouseListRow) rowView;
			final House house = houseList.get(position);
			houseListRow.reloadCell(position, totalHouse, house);
			
			//TODO: TEST
			houseListRow.setHouseListRowCallback(new HouseListRowCallback(){
				@Override
				public void onClickHouse() {
					Intent intent = new Intent(getActivity(), HouseDetailActivity.class);
					intent.putExtra(BHConstants.EXTRA_HOUSE_NO, house.NO);
					startActivity(intent);
				}
			});
			
			return rowView;
		}
	};
	
	public void reloadData(){
		fetchData(true);
	}
	
	private void fetchData(final Boolean refresh){
		
		int newPage = (refresh)? 0 : currentPage+1;
		if (newPage>totalPage) return;
		
		int limit = BHConstants.HOUSE_LIST_FETCH_LIMIT;
		
		//根據filterMode/SearchMode狀態處理參數
		String filterParams, boundLatLngString;
		if (SearchInfo.getInstance().currentFilterMode==BHConstants.FILTER_MODE_AREA){
			filterParams = SearchInfo.getInstance().getFilterParams(false, true);
			boundLatLngString = SearchInfo.getInstance().boundLatLngString;
		}else{
			if (SearchInfo.getInstance().currentSearchMode==BHConstants.SEARCH_MODE_LATLNG){
				//範圍經緯度mode
				filterParams = SearchInfo.getInstance().getFilterParams(false, true);
				boundLatLngString = SearchInfo.getInstance().boundLatLngString;
			}else{
				//捷運資訊mode
				if (SearchInfo.getInstance().isForKeyword){
					filterParams = SearchInfo.getInstance().getFilterParams(false, true);
				}else{
					filterParams = SearchInfo.getInstance().getFilterParams(true, true);
				}
				boundLatLngString = "";
			}
		}
		
		Log.e("test", "MainHouseListFragment: currentFilterMode:"+SearchInfo.getInstance().currentFilterMode);
		Log.e("test", "MainHouseListFragment: currentSearchMode:"+SearchInfo.getInstance().currentSearchMode);
		Log.e("test", "MainHouseListFragment: boundLatLng: "+boundLatLngString);
		Log.e("test", "MainHouseListFragment: filterParams: "+filterParams);
		
		//showProgressDialog
		if (newPage==0){
			showActivityProgressDialog();
		}
		
		HouseService.getHouseList(newPage, limit, filterParams, boundLatLngString, new HouseListCallback(){

			@Override
			public void onResult(boolean success, ArrayList<House> houses,
					int newTotalHouse, int newPage, int newTotalPage) {
				
				//hideProgressDialog
				hideActivityProgressDialog();
				
				houseListView.onRefreshComplete();
				
				if (success){
					Log.e("test", "getHouseList success! page:"+newPage+" count:"+houses.size());
					//更新資料
					totalHouse = newTotalHouse;
					currentPage = newPage;
					totalPage = newTotalPage;
					
					if (refresh) {
						houseList.clear();
						
						//Show or hide : listview, houseCount, noResult
						if (houses.size()==0){
							houseListView.setVisibility(View.GONE);
							houseCountContainer.setVisibility(View.VISIBLE);
							noResultContainer.setVisibility(View.VISIBLE);
						}else{
							houseListView.setVisibility(View.VISIBLE);
							houseCountContainer.setVisibility(View.GONE);
							noResultContainer.setVisibility(View.GONE);
						}
					}
					
					houseList.addAll(houses);
					houseAdapter.notifyDataSetChanged();
					
					//set to proper position
					if (refresh){
						houseListView.post(new Runnable() {

							@Override
							public void run() {
								houseListView.getRefreshableView().setSelection(0);
							}
						});
					}
					
				}else{
					houseListView.setVisibility(View.GONE);
					houseCountContainer.setVisibility(View.VISIBLE);
					noResultContainer.setVisibility(View.VISIBLE);
					Log.e("test", "getHouseList fail!");
					showActivityToast(R.string.no_network_please_check);
				}
			}
		});
		
	}
	
	public LatLng getCurrentFirstHouseLatLng(){
		int position = houseListView.getRefreshableView().getFirstVisiblePosition();
		
		//如果搜尋為0筆，回傳目前最新center資訊
		if ((position<0)||(houseList.size()==0)){
			return SearchInfo.getInstance().centerPos;
		}
		
		//取出可視區域第一筆房屋的經緯度
		House house = houseList.get(position);
		if (house.lat==0 || house.lng==0){
			return SearchInfo.getInstance().centerPos;
		}
		
		Log.e("test", "取得可視區域第一筆house: "+house.name+" 經緯度:"+house.lat+","+house.lng);			
		return new LatLng(house.lat,house.lng);
		
	}
    
    
    
}
