package com.yowoo.newbuyhouse.track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.json.JSONArray;
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
import android.widget.Button;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.BaseActivity;
import com.yowoo.newbuyhouse.MainActivity;
import com.yowoo.newbuyhouse.network.TrackService;
import com.yowoo.newbuyhouse.network.TrackService.SubscribeCallback;
import com.yowoo.newbuyhouse.network.TrackService.TrackCallback;
import com.yowoo.newbuyhouse.view.SearchRow;
import com.yowoo.newbuyhouse.view.SearchRow.SearchRowSelectCallback;

public class TrackSubscribeActivity extends BaseActivity{

	PullToRefreshListView subscribeListView;
	ArrayList<JSONObject> subscribeList = new ArrayList<JSONObject>();
	
	RelativeLayout noResultContainer, bottomContainer;
	Button deleteButton;
	
	Boolean isDeleteMode = false;
	HashSet<String> selectedHashSet = new HashSet<String>();
	private Menu mMenu;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	Boolean fromNotification = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_track_subscribe);

		//get extra
		try{
			Bundle extra = getIntent().getExtras();
			if (extra.containsKey(BHConstants.EXTRA_FROM_NOTIFICATION)){
				fromNotification = extra.getBoolean(BHConstants.EXTRA_FROM_NOTIFICATION);
			}
		}catch(Exception e){}
		
		//set toolbar
		setToolbarView(getString(R.string.recent_search));
		
        noResultContainer = (RelativeLayout) findViewById(R.id.noResultContainer);
        bottomContainer = (RelativeLayout) findViewById(R.id.bottomContainer);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        
        subscribeListView = (PullToRefreshListView) findViewById(R.id.searchListview);
        subscribeListView.setMode(Mode.DISABLED);
        
        //set adapter
        subscribeListView.setAdapter(subscribeAdapter);
        
        setListener();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		fetchData();
		
	}
	
	private void reloadViews(){
		
		if (this.subscribeList.size()==0){
			noResultContainer.setVisibility(View.VISIBLE);
			subscribeListView.setVisibility(View.GONE);
		}else{
			noResultContainer.setVisibility(View.GONE);
			subscribeListView.setVisibility(View.VISIBLE);
		}
	}
	
	
	BaseAdapter subscribeAdapter = new BaseAdapter() {
		@Override
		public int getCount() {
			return subscribeList.size();
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
				rowView = new SearchRow(TrackSubscribeActivity.this);
			}

			final SearchRow searchRow = (SearchRow) rowView;
			final JSONObject searchObject = subscribeList.get(position);
			String params = "";
			String subscribeId = "";
			String createDateString = "";
			String afterDateString = "";
			int unreadCount = 0;
			try {
				params = searchObject.getString(BHConstants.JSON_KEY_CRITERIA);
				subscribeId = searchObject.getString(BHConstants.JSON_KEY_ID);
				unreadCount = searchObject.getInt(BHConstants.JSON_KEY_TOTAL);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			try {
				createDateString = searchObject.getString(BHConstants.JSON_KEY_CREATE_DATE);
				afterDateString = searchObject.getString(BHConstants.JSON_KEY_AFTER_DATE);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
			final String finalParams = params;
			final String finalSubscribeId = subscribeId;
			final String finalCreateDateString = createDateString;
			final String finalafterDateString = afterDateString;
			searchRow.reloadCell(params, unreadCount);
			
			searchRow.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String filterParams = "";
					try {
						filterParams = searchObject.getString(BHConstants.JSON_KEY_PARAMS);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					Intent intent = new Intent(TrackSubscribeActivity.this, TrackSubscribeHouseActivity.class);
					intent.putExtra(BHConstants.EXTRA_FILTER_PARAMS, filterParams);
					intent.putExtra(BHConstants.EXTRA_SUBSCRIBE_ID, finalSubscribeId);
					intent.putExtra(BHConstants.EXTRA_CREATE_DATE, finalCreateDateString);
					intent.putExtra(BHConstants.EXTRA_AFTER_DATE, finalafterDateString);
					startActivity(intent);
				
				}
			});
			
			//reload select view
			if (!isDeleteMode){
				searchRow.setSelectViewVisibility(View.GONE);
			}else{
				searchRow.setSelectViewVisibility(View.VISIBLE);
				if (selectedHashSet.contains(subscribeId)){
					//select
					searchRow.reloadSelectView(true);
				}else{
					//not select
					searchRow.reloadSelectView(false);
				}
				
				searchRow.setSelectCallback(new SearchRowSelectCallback(){
					@Override
					public void onClickSelect() {
						if (selectedHashSet.contains(finalSubscribeId)){
							//select -> not select
							selectedHashSet.remove(finalSubscribeId);
							searchRow.reloadSelectView(false);
						}else{
							//not select -> select
							selectedHashSet.add(finalSubscribeId);
							searchRow.reloadSelectView(true);
						}
					}
				});
			}
			
			return rowView;
		}
	};
	
	
	private void fetchData(){
		
		TrackService.getSubscribes(new SubscribeCallback(){
			@Override
			public void onResult(boolean success, String debugMessage, final JSONArray subscribes) {
				Log.e(TAG, "getSubscribe:"+success);
				if (!success){
					reloadViews();
					return;
				}

				//成功fetch, 但無訂閱條件
				if (subscribes.length()==0){
					subscribeList.clear();
					reloadViews();
					return;
				}

				//有訂閱條件
				try {
					ArrayList<JSONObject> results = new ArrayList<JSONObject>();
					for (int i=0; i<subscribes.length(); i++){
						results.add(subscribes.getJSONObject(i));
					}
					
					subscribeList = results;
					subscribeAdapter.notifyDataSetChanged();
					reloadViews();
				} catch (JSONException e) {
					e.printStackTrace();
					reloadViews();
				}
			}
		});
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_track_collect, menu);
		this.mMenu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		if (id == android.R.id.home){
			if (this.fromNotification){
				Intent intent = new Intent(TrackSubscribeActivity.this, MainActivity.class);
				intent.putExtra(BHConstants.EXTRA_GOTO_FRAGMENT_POS, BHConstants.MENU_TRACK_POSITION);
				startActivity(intent);
			}
			finish();
			return true;
		}
		
		if (id == R.id.action_delete) {
			//action of switch delete mode 
			if (subscribeList.size()>0){
				switchDeleteModeAction();
			}
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void setListener(){
		deleteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//get all selected house no
				String subIds = "";
				Iterator it = selectedHashSet.iterator();
				while(it.hasNext()){
					subIds += (String)it.next()+",";
				}
				
				if (subIds.equals("")){
					showToast(getString(R.string.prompt_no_select_house));
					return;
				}
				
				Log.e(TAG, "last char:"+subIds.substring(subIds.length()-1));
			
				if (subIds.substring(subIds.length()-1).equals(",")){
					subIds = subIds.substring(0, subIds.length()-1);
				}

				Log.e(TAG, "subIds:"+subIds);
				
				TrackService.removeSubscribe(subIds, "", new TrackCallback(){

					@Override
					public void onResult(boolean success, String debugMessage) {
						Log.e(TAG, "removeTrackHouse:"+success+" msg:"+debugMessage);
						if (success){
							switchDeleteModeAction();
							selectedHashSet.clear();
							fetchData();
							showToast(getString(R.string.update_success));
						}else{
							showToast(getString(R.string.update_fail)+" : "+debugMessage);
						}
					}
				});
			}
		});
	}

	private void switchDeleteModeAction(){
		isDeleteMode = !isDeleteMode;
		this.subscribeAdapter.notifyDataSetChanged();
		if (isDeleteMode){
			if (this.mMenu!=null){
				this.mMenu.getItem(0).setTitle(getString(R.string.cancel));
			}
			bottomContainer.setVisibility(View.VISIBLE);
		}else{
			if (this.mMenu!=null){
				this.mMenu.getItem(0).setTitle(getString(R.string.delete));
			}
			bottomContainer.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onBackPressed(){
		
		if (this.fromNotification){
			Intent intent = new Intent(TrackSubscribeActivity.this, MainActivity.class);
			intent.putExtra(BHConstants.EXTRA_GOTO_FRAGMENT_POS, BHConstants.MENU_TRACK_POSITION);
			startActivity(intent);
		}
		
		super.onBackPressed();
	}
	
}
