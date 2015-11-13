package com.yowoo.newbuyhouse.track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.BaseActivity;
import com.yowoo.newbuyhouse.house.HouseDetailActivity;
import com.yowoo.newbuyhouse.model.House;
import com.yowoo.newbuyhouse.network.TrackService;
import com.yowoo.newbuyhouse.network.TrackService.SubscribeHouseListCallback;
import com.yowoo.newbuyhouse.network.TrackService.TrackCallback;
import com.yowoo.newbuyhouse.view.HouseListRow;
import com.yowoo.newbuyhouse.view.HouseListRow.HouseListRowCallback;

public class TrackSubscribeHouseActivity extends BaseActivity{

	PullToRefreshListView houseListView;
	ArrayList<House> houseList = new ArrayList<House>();
	int currentPage = 0;
	int totalHouse = 0;
	int totalPage = 0;
	
	RelativeLayout houseCountContainer, noResultContainer;
	
	private String filterParams = "";
	private String subscribeId = "";
	private Date createDate = new Date();
	private Date afterDate = new Date();
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_track_search_house);

		//set toolbar
		setToolbarView(getString(R.string.recent_search));
		
		//GET EXTRA
		try{
			Bundle extra = getIntent().getExtras();
			filterParams = extra.getString(BHConstants.EXTRA_FILTER_PARAMS);
			subscribeId = extra.getString(BHConstants.EXTRA_SUBSCRIBE_ID);
			
			String createDateString = extra.getString(BHConstants.EXTRA_CREATE_DATE);
			if ((createDateString==null)||(createDateString.equals(""))){
				createDate = new Date();
			}else{
				createDate = sdf.parse(createDateString);
			}
			Log.e(TAG, "extra: createDate:"+sdf.format(createDate));
				
			String afterDateString = extra.getString(BHConstants.EXTRA_AFTER_DATE);
			if ((afterDateString==null)||(afterDateString.equals(""))){
				afterDate = new Date();
			}else{
				afterDate = sdf.parse(afterDateString);
			}
			Log.e("test", "extra: afterDate:"+sdf.format(afterDate));
			
		}catch (Exception e){
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
				rowView = new HouseListRow(TrackSubscribeHouseActivity.this);
			}

			HouseListRow houseListRow = (HouseListRow) rowView;
			final House house = houseList.get(position);
			houseListRow.reloadCell(position, totalHouse, house);
			
			//check date
			Log.e("test", "createDate:"+sdf.format(house.createDate)+" afterDate:"+sdf.format(afterDate));
			Log.e("test", "isNew:"+house.createDate.after(afterDate));
			Boolean isNew = (createDate.compareTo(afterDate)==0) ? true : house.createDate.after(afterDate);
			houseListRow.reloadNewHouseView(isNew);
			
			//TODO: TEST
			houseListRow.setHouseListRowCallback(new HouseListRowCallback(){
				@Override
				public void onClickHouse() {
					Intent intent = new Intent(TrackSubscribeHouseActivity.this, HouseDetailActivity.class);
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
		
		TrackService.getSubscribeHouseList(newPage, limit, filterParams, "", new SubscribeHouseListCallback(){

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
					
					//update subscribe last read time in background
					if (refresh){
						updateSubscribeReadTime();
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
	
	private void updateSubscribeReadTime(){
		TrackService.updateSubscirbe(subscribeId, true, new HashMap<String, Object>(), new TrackCallback(){
			@Override
			public void onResult(boolean success, String debugMessage) {
				Log.e(TAG, "update time:"+success+" msg:"+debugMessage);
			}
		});
	}
	
}
