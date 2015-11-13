package com.yowoo.newbuyhouse.track;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

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
import com.yowoo.newbuyhouse.login.LoginInfo;
import com.yowoo.newbuyhouse.login.UserConstants;
import com.yowoo.newbuyhouse.model.House;
import com.yowoo.newbuyhouse.network.HouseService;
import com.yowoo.newbuyhouse.network.HouseService.SimpleHouseCallback;
import com.yowoo.newbuyhouse.network.TrackService;
import com.yowoo.newbuyhouse.network.TrackService.TrackCallback;
import com.yowoo.newbuyhouse.view.HouseListRow.HouseListRowCallback;
import com.yowoo.newbuyhouse.view.HouseListRow.HouseListRowSelectCallback;
import com.yowoo.newbuyhouse.view.SearchRow;

public class TrackSearchActivity extends BaseActivity{

	PullToRefreshListView searchListView;
	ArrayList<JSONObject> searchList = new ArrayList<JSONObject>();
	
	RelativeLayout noResultContainer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_track_search);

		//set toolbar
		setToolbarView(getString(R.string.recent_search));
		
        noResultContainer = (RelativeLayout) findViewById(R.id.noResultContainer);
        
        searchListView = (PullToRefreshListView) findViewById(R.id.searchListview);
        searchListView.setMode(Mode.DISABLED);
        
        
        //set adapter
        searchListView.setAdapter(searchAdapter);
        
        //set data and reload
        reloadViews();
        
		
		
	}
	
	@Override
	protected void onResume(){
		super.onResume();
	}
	
	private void reloadViews(){
		this.searchList = LoginInfo.getInstance().searchArrayList;
        
		if (this.searchList.size()==0){
			noResultContainer.setVisibility(View.VISIBLE);
			searchListView.setVisibility(View.GONE);
		}else{
			searchListView.setVisibility(View.VISIBLE);
			searchAdapter.notifyDataSetChanged();
		}
	}
	
	
	BaseAdapter searchAdapter = new BaseAdapter() {
		@Override
		public int getCount() {
			return searchList.size();
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
			
			
			if(rowView==null||rowView.getClass().equals(SearchRow.class)==false) {
				rowView = new SearchRow(TrackSearchActivity.this);
			}

			final SearchRow searchRow = (SearchRow) rowView;
			final JSONObject searchObject = searchList.get(position);
			String params = "";
			try {
				params = searchObject.getString(UserConstants.KEY_DISPLAY_PARAMS);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			searchRow.reloadCell(params, 0);
			
			searchRow.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String filterParams = "";
					try {
						filterParams = searchObject.getString(UserConstants.KEY_FILTER_PARAMS);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					Intent intent = new Intent(TrackSearchActivity.this, TrackSearchHouseActivity.class);
					intent.putExtra(BHConstants.EXTRA_FILTER_PARAMS, filterParams);
					startActivity(intent);
				}
			});
			
			
			return rowView;
		}
	};
	
	
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
