package com.yowoo.newbuyhouse.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.LatLng;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.model.Store;
import com.yowoo.newbuyhouse.view.StoreListRow;
import com.yowoo.newbuyhouse.view.StoreListRow.StoreListRowCallback;

public class MainStoreListFragment extends MyFragment{

	public static Fragment newInstance(Context context) {
		MainStoreListFragment f = new MainStoreListFragment();
 
        return f;
    }
 
	PullToRefreshListView storeListView;
	ArrayList<Store> storeList = new ArrayList<Store>();
	int currentPage = 0;
	int totalHouse = 0;
	int totalPage = 0;
	
	RelativeLayout storeCountContainer, noResultContainer;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_store_list, null);
        
        storeCountContainer = (RelativeLayout) rootView.findViewById(R.id.storeCountContainer);
        noResultContainer = (RelativeLayout) rootView.findViewById(R.id.noResultContainer);
        
        storeListView = (PullToRefreshListView) rootView.findViewById(R.id.storeListview);
        storeListView.setMode(Mode.PULL_FROM_START);
//        storeListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
//            @Override
//            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//            		
//            		Log.e("test", "currentMode: "+ storeListView.getCurrentMode().toString());
//            		//pull down to refresh
//            		fetchData(true);
//            		
//            }
//            
//        });
        
        //set adapter
        storeListView.setAdapter(storeAdapter);
        
        //get data
        //fetchData(true);
        
        return rootView;
    }
    
    @Override
	public void onResume() {
		super.onResume();
		
		Log.e("test", "MainStoreListFragment: onResume!");
    
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
    
    BaseAdapter storeAdapter = new BaseAdapter() {
		@Override
		public int getCount() {
			return storeList.size();
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
			
			if(rowView==null||rowView.getClass().equals(StoreListRow.class)==false) {
				rowView = new StoreListRow(getActivity());
			}

			StoreListRow storeListRow = (StoreListRow) rowView;
			final Store store = storeList.get(position);
			storeListRow.reloadCell(position, storeList.size(), store);
			storeListRow.setStoreListRowCallback(new StoreListRowCallback(){
				@Override
				public void onClickStore() {
				}

				@Override
				public void onClickCall(String tel) {
					Intent intentDial = new Intent("android.intent.action.CALL",Uri.parse("tel:"+tel));
				    startActivity(intentDial);
				}
			});
			
			return rowView;
		}
	};
	
	public void reloadData(ArrayList<Store> storeList){
		this.storeList = storeList;
		
		//Show or hide : listview, houseCount, noResult
		if (storeList.size()==0){
			storeListView.setVisibility(View.GONE);
			storeCountContainer.setVisibility(View.VISIBLE);
			noResultContainer.setVisibility(View.VISIBLE);
			
		}else{
			storeListView.setVisibility(View.VISIBLE);
			storeCountContainer.setVisibility(View.GONE);
			noResultContainer.setVisibility(View.GONE);

			this.storeAdapter.notifyDataSetChanged();

			storeListView.post(new Runnable() {

				@Override
				public void run() {
					storeListView.getRefreshableView().setSelection(0);
				}
			});
		}
	}
	
	
	public LatLng getCurrentFirstHouseLatLng(){
//		int position = storeListView.getRefreshableView().getFirstVisiblePosition();
//		
//		//如果搜尋為0筆，回傳目前最新center資訊
//		if ((position<0)||(storeList.size()==0)){
//			return SearchInfo.getInstance().centerPos;
//		}
//		
//		//取出可視區域第一筆房屋的經緯度
//		House house = storeList.get(position);
//		if (house.lat==0 || house.lng==0){
//			return SearchInfo.getInstance().centerPos;
//		}
//		
//		Log.e("test", "取得可視區域第一筆house: "+house.name+" 經緯度:"+house.lat+","+house.lng);			
//		return new LatLng(house.lat,house.lng);
		
		//temp
		return null;
	}
    
    
    
}
