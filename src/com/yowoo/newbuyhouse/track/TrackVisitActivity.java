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
import com.yowoo.newbuyhouse.house.HouseDetailActivity;
import com.yowoo.newbuyhouse.login.LoginInfo;
import com.yowoo.newbuyhouse.model.House;
import com.yowoo.newbuyhouse.network.HouseService;
import com.yowoo.newbuyhouse.network.HouseService.SimpleHouseCallback;
import com.yowoo.newbuyhouse.network.TrackService;
import com.yowoo.newbuyhouse.network.TrackService.TrackCallback;
import com.yowoo.newbuyhouse.view.HouseListRow;
import com.yowoo.newbuyhouse.view.HouseListRow.HouseListRowCallback;
import com.yowoo.newbuyhouse.view.HouseListRow.HouseListRowSelectCallback;

public class TrackVisitActivity extends BaseActivity{

	PullToRefreshListView houseListView;
	ArrayList<House> houseList = new ArrayList<House>();
	
	RelativeLayout houseCountContainer, noResultContainer;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_track_collect);

		//set toolbar
		setToolbarView(getString(R.string.recent_visit));
		
		houseCountContainer = (RelativeLayout) findViewById(R.id.houseCountContainer);
        noResultContainer = (RelativeLayout) findViewById(R.id.noResultContainer);
        
        houseListView = (PullToRefreshListView) findViewById(R.id.houseListview);
        houseListView.setMode(Mode.DISABLED);
        
        
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
			
			
			if(rowView==null||rowView.getClass().equals(HouseListRow.class)==false) {
				rowView = new HouseListRow(TrackVisitActivity.this);
			}

			final HouseListRow houseListRow = (HouseListRow) rowView;
			final House house = houseList.get(position);
			
			houseListRow.reloadCell(position, houseList.size(), house);
			
			houseListRow.setHouseListRowCallback(new HouseListRowCallback(){
				@Override
				public void onClickHouse() {
					Intent intent = new Intent(TrackVisitActivity.this, HouseDetailActivity.class);
					intent.putExtra(BHConstants.EXTRA_HOUSE_NO, house.NO);
					startActivity(intent);
				}
			});
			
			return rowView;
		}
	};
	
	
	
	private void fetchData(final Boolean refresh){
		
		String houseNOs = LoginInfo.getInstance().getVisitHouseNOs();
		
		HouseService.getSimplePlusHouse(houseNOs, new SimpleHouseCallback(){
			@Override
			public void onResult(boolean success, ArrayList<House> houses) {
				if (success){
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
					
					houseList = houses;
					houseAdapter.notifyDataSetChanged();
				}else{
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
