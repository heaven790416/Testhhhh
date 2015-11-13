package com.yowoo.newbuyhouse.track;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.BaseActivity;
import com.yowoo.newbuyhouse.SearchInfo;
import com.yowoo.newbuyhouse.house.HouseDetailActivity;
import com.yowoo.newbuyhouse.login.LoginInfo;
import com.yowoo.newbuyhouse.model.House;
import com.yowoo.newbuyhouse.network.HouseService;
import com.yowoo.newbuyhouse.network.HouseService.HouseListCallback;
import com.yowoo.newbuyhouse.network.HouseService.SimpleHouseCallback;
import com.yowoo.newbuyhouse.network.TrackService;
import com.yowoo.newbuyhouse.network.TrackService.TrackCallback;
import com.yowoo.newbuyhouse.view.HouseListRow;
import com.yowoo.newbuyhouse.view.HouseListRow.HouseListRowCallback;
import com.yowoo.newbuyhouse.view.HouseListRow.HouseListRowSelectCallback;

public class TrackSearchHouseActivity extends BaseActivity{

	PullToRefreshListView houseListView;
	ArrayList<House> houseList = new ArrayList<House>();
	int currentPage = 0;
	int totalHouse = 0;
	int totalPage = 0;
	
	RelativeLayout houseCountContainer, noResultContainer;
	
	private String filterParams = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_track_search_house);

		//set toolbar
		setToolbarView(getString(R.string.recent_search));
		
		//GET EXTRA
		try{
			filterParams = getIntent().getExtras().getString(BHConstants.EXTRA_FILTER_PARAMS);
		}catch (Exception e){
			filterParams = "";
		}
		
		houseCountContainer = (RelativeLayout)findViewById(R.id.houseCountContainer);
        noResultContainer = (RelativeLayout)findViewById(R.id.noResultContainer);
        
        houseListView = (PullToRefreshListView)findViewById(R.id.houseListview);
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
        
    
		
		
	}
	
	@Override
	protected void onResume(){
		super.onResume();
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
				rowView = new HouseListRow(TrackSearchHouseActivity.this);
			}

			HouseListRow houseListRow = (HouseListRow) rowView;
			final House house = houseList.get(position);
			houseListRow.reloadCell(position, totalHouse, house);
			
			houseListRow.setHouseListRowCallback(new HouseListRowCallback(){
				@Override
				public void onClickHouse() {
					Intent intent = new Intent(TrackSearchHouseActivity.this, HouseDetailActivity.class);
					intent.putExtra(BHConstants.EXTRA_HOUSE_NO, house.NO);
					startActivity(intent);
				}
			});
			
			return rowView;
		}
	};
	
	
	
	private void fetchData(final Boolean refresh){
		
		int newPage = (refresh)? 0 : currentPage+1;
		if (newPage>totalPage) return;
		
		int limit = BHConstants.HOUSE_LIST_FETCH_LIMIT;
		
		
		//showProgressDialog
		if (newPage==0){
			showProgressDialog();
		}
		
		HouseService.getHouseList(newPage, limit, filterParams, "", new HouseListCallback(){

			@Override
			public void onResult(boolean success, ArrayList<House> houses,
					int newTotalHouse, int newPage, int newTotalPage) {
				
				//hideProgressDialog
				hideProgressDialog();
				
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
					Log.e("test", "getHouseList fail!");
					showToast(R.string.no_network_please_check);
				}
			}
		});
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home){
			finish();
		}
		
		return super.onOptionsItemSelected(item);
	}
	
}
