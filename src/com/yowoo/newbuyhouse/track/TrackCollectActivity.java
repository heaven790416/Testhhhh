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

public class TrackCollectActivity extends BaseActivity{

	PullToRefreshListView houseListView;
	ArrayList<House> houseList = new ArrayList<House>();
	int currentPage = 0;
	int totalHouse = 0;
	int totalPage = 0;
	
	RelativeLayout houseCountContainer, noResultContainer, bottomContainer;
	Button deleteButton;
	
	//String favHouseNos = "";
	Boolean isDeleteMode = false;
	HashSet<String> selectedHashSet = new HashSet<String>();
	private Menu mMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_track_collect);

		//set toolbar
		setToolbarView(getString(R.string.track_collect));
		
		houseCountContainer = (RelativeLayout) findViewById(R.id.houseCountContainer);
        noResultContainer = (RelativeLayout) findViewById(R.id.noResultContainer);
        bottomContainer = (RelativeLayout) findViewById(R.id.bottomContainer);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        
        houseListView = (PullToRefreshListView) findViewById(R.id.houseListview);
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
        //fetchData(true);
        
		setListener();
		
		
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		fetchData(true);
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
				rowView = new HouseListRow(TrackCollectActivity.this);
			}

			final HouseListRow houseListRow = (HouseListRow) rowView;
			final House house = houseList.get(position);
			houseListRow.reloadCell(position, houseList.size(), house);
			
			houseListRow.setHouseListRowCallback(new HouseListRowCallback(){
				@Override
				public void onClickHouse() {
					Intent intent = new Intent(TrackCollectActivity.this, HouseDetailActivity.class);
					intent.putExtra(BHConstants.EXTRA_HOUSE_NO, house.NO);
					startActivity(intent);
				}
			});
			
			//reload select view
			if (!isDeleteMode){
				houseListRow.setSelectViewVisibility(View.GONE);
			}else{
				houseListRow.setSelectViewVisibility(View.VISIBLE);
				if (selectedHashSet.contains(house.NO)){
					//select
					houseListRow.reloadSelectView(true);
				}else{
					//not select
					houseListRow.reloadSelectView(false);
				}
				
				houseListRow.setHouseListRowSelectCallback(new HouseListRowSelectCallback(){
					@Override
					public void onClickSelect() {
						if (selectedHashSet.contains(house.NO)){
							//select -> not select
							selectedHashSet.remove(house.NO);
							houseListRow.reloadSelectView(false);
						}else{
							//not select -> select
							selectedHashSet.add(house.NO);
							houseListRow.reloadSelectView(true);
						}
					}
				});
			}
			
			return rowView;
		}
	};
	
	
	
	private void fetchData(final Boolean refresh){
		//check if no record
        String favHouseNos = LoginInfo.getInstance().getFavHouseNOs();
		if (favHouseNos.equals("")){
			houseList.clear();
			reloadNoResultViews();
			return;
		}
		
		HouseService.getSimplePlusHouse(favHouseNos, new SimpleHouseCallback(){
			@Override
			public void onResult(boolean success, ArrayList<House> houses) {
				if (success){
					houseList = houses;
					houseAdapter.notifyDataSetChanged();
				}else{
					showToast(getString(R.string.network_not_stable));
				}
				
				reloadNoResultViews();
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
			finish();
			return true;
		}
		
		if (id == R.id.action_delete) {
			//action of switch delete mode 
			if (houseList.size()>0){
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
				String houseNOs = "";
				Iterator it = selectedHashSet.iterator();
				while(it.hasNext()){
					houseNOs += (String)it.next()+",";
				}
				
				if (houseNOs.equals("")){
					showToast(getString(R.string.prompt_no_select_house));
					return;
				}
				
				Log.e("test", "last char:"+houseNOs.substring(houseNOs.length()-1));
			
				if (houseNOs.substring(houseNOs.length()-1).equals(",")){
					houseNOs = houseNOs.substring(0, houseNOs.length()-1);
				}

				final String finalHouseNOs = houseNOs;
				
				if (LoginInfo.getInstance().isLogined()){
					//登入狀態，更新server收藏
					TrackService.removeTrackHouse(finalHouseNOs, "", new TrackCallback(){

						@Override
						public void onResult(boolean success, String debugMessage) {
							Log.e("test", "removeTrackHouse:"+success+" msg:"+debugMessage);
							if (success){
								//移除local收藏
								removeLocalCollect(finalHouseNOs);
								//remove house object
								updateDataAndViewsAfterDelete();

								Log.e(TAG, "rm success!left no:"+LoginInfo.getInstance().getFavHouseNOs());
								showToast(getString(R.string.update_success));
							}
						}
					});
				}else{
					//非登入狀況，更新local收藏
					//移除local收藏
					removeLocalCollect(finalHouseNOs);
					//remove house object
					updateDataAndViewsAfterDelete();
					
					Log.e(TAG, "rm success!left no:"+LoginInfo.getInstance().getFavHouseNOs());
					showToast(getString(R.string.update_success));
				}
			}
		});
	}
	
	private void switchDeleteModeAction(){
		isDeleteMode = !isDeleteMode;
		this.houseAdapter.notifyDataSetChanged();
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
	
	private void removeLocalCollect(String houseNOs){
		Iterator it = selectedHashSet.iterator();
		while(it.hasNext()){
			String houseNO = (String)it.next();
			LoginInfo.getInstance().removeFavHouse(false, houseNO);
		}
		LoginInfo.getInstance().storeInfoToPreference();

	}
	
	private void updateDataAndViewsAfterDelete(){
		for (int i=houseList.size()-1; i>=0; i--){
			String NO = houseList.get(i).NO;
			if (selectedHashSet.contains(NO)){
				houseList.remove(i);
			}
		}

		switchDeleteModeAction();
		selectedHashSet.clear();
		
		reloadNoResultViews();
	}
	
	private void reloadNoResultViews(){
		if (this.houseList.size()==0){
			this.noResultContainer.setVisibility(View.VISIBLE);
			this.houseListView.setVisibility(View.GONE);
		}else{
			this.noResultContainer.setVisibility(View.GONE);
			this.houseListView.setVisibility(View.VISIBLE);
		}
	}
	
	
}
