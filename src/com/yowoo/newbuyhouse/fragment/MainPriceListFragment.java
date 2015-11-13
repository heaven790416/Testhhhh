package com.yowoo.newbuyhouse.fragment;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
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
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.model.Price;
import com.yowoo.newbuyhouse.network.PriceService;
import com.yowoo.newbuyhouse.network.PriceService.PriceListPageCallback;
import com.yowoo.newbuyhouse.price.PriceSearchInfo;
import com.yowoo.newbuyhouse.view.DealPriceRow;

public class MainPriceListFragment extends MyFragment{

	public static Fragment newInstance(Context context) {
		MainPriceListFragment f = new MainPriceListFragment();
 
        return f;
    }
 
	PullToRefreshListView houseListView;
	ArrayList<Price> houseList = new ArrayList<Price>();
	int currentPage = 1;
	int totalHouse = 0;
	int totalPage = 0;
	
	RelativeLayout noResultContainer, remarkContainer;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_price_list, null);
        
        noResultContainer = (RelativeLayout) rootView.findViewById(R.id.noResultContainer);
        remarkContainer = (RelativeLayout) rootView.findViewById(R.id.remarkContainer);
        
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
        //fetchData(true);
        
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
			
			if(rowView==null||rowView.getClass().equals(DealPriceRow.class)==false) {
				rowView = new DealPriceRow(getActivity());
			}

			DealPriceRow dealPriceRow = (DealPriceRow) rowView;
			final Price price = houseList.get(position);
			dealPriceRow.reloadCell(price);
			
			
			return rowView;
		}
	};
	
	public void reloadData(){
		fetchData(true);
	}
	
	private void fetchData(final Boolean refresh){
		
		int newPage = (refresh)? 1 : currentPage+1;
		if ((!refresh)&&(newPage>totalPage)) return;
		int limit = BHConstants.HOUSE_LIST_FETCH_LIMIT;
		
		//根據filterMode/SearchMode狀態處理參數
		String filterParams="", boundLatLngString;
		if (PriceSearchInfo.getInstance().currentFilterMode==BHConstants.FILTER_MODE_AREA){
			filterParams = PriceSearchInfo.getInstance().getFilterParams(true);
			boundLatLngString = PriceSearchInfo.getInstance().boundLatLngString;
		}else{
			if (PriceSearchInfo.getInstance().currentSearchMode==BHConstants.SEARCH_MODE_LATLNG){
				//範圍經緯度mode
				filterParams = PriceSearchInfo.getInstance().getFilterParams(true);
				boundLatLngString = PriceSearchInfo.getInstance().boundLatLngString;
			}else{
				//捷運資訊mode
//				if (PriceSearchInfo.getInstance().isForKeyword){
//					filterParams = PriceSearchInfo.getInstance().getFilterParams(true);
//				}else{
//					filterParams = PriceSearchInfo.getInstance().getFilterParams(true);
//				}
				filterParams = PriceSearchInfo.getInstance().getFilterParams(true);
				boundLatLngString = "";
			}
		}
		
		Log.e("test", "MainHouseListFragment: currentFilterMode:"+PriceSearchInfo.getInstance().currentFilterMode);
		Log.e("test", "MainHouseListFragment: currentSearchMode:"+PriceSearchInfo.getInstance().currentSearchMode);
		Log.e("test", "MainHouseListFragment: boundLatLng: "+boundLatLngString);
		Log.e("test", "MainHouseListFragment: filterParams: "+filterParams);
		
		//showProgressDialog
		if (newPage==1){
			showActivityProgressDialog();
		}
		
//		String filterParams = "";
//		String boundLatLngString = "";
		PriceService.getPriceList(newPage, limit, filterParams, boundLatLngString, new PriceListPageCallback(){

			@Override
			public void onResult(boolean success, ArrayList<Price> prices,
					int newTotalHouse, int newPage, int newTotalPage) {
				
				//hideProgressDialog
				hideActivityProgressDialog();
				
				houseListView.onRefreshComplete();
				
				//TODO: FOR TEST請刪除===============
//				if (PriceSearchInfo.getInstance().currentFilterMode==BHConstants.FILTER_MODE_MRT){
//					String testString = "{\"NO\":\"12504G\",\"address\":\"台北市松山區市民大道五段151-199號\",\"soldDate\":10409,\"buildingType\":\"華廈\",\"hasGarage\":1,\"floor\":\"2 /共5層\",\"age\":20.2,\"areaBuilding\":38.41,\"areaLand\":4.96,\"inc\":\"2\",\"lat\":25.048134,\"lng\":121.564828,\"price\":1520,\"unitPrice\":39.6,\"layout\":\"2房/1廳/1衛/0室\",\"outlier\":false}";
//					Price testPrice = new Price();
//					try {
//						testPrice = new Price(new JSONObject(testString));
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//					prices = new ArrayList<Price>();
//					prices.add(testPrice);
//					success = true;
//				}
				//================================
				
				
				if (success){
					Log.e("test", "getHouseList success! page:"+newPage+" count:"+prices.size());
					//更新資料
					totalHouse = newTotalHouse;
					currentPage = newPage;
					totalPage = newTotalPage;
					
					if (refresh) {
						houseList.clear();
						
						//Show or hide : listview, houseCount, noResult
						if (prices.size()==0){
							houseListView.setVisibility(View.GONE);
							remarkContainer.setVisibility(View.GONE);
							noResultContainer.setVisibility(View.VISIBLE);
						}else{
							houseListView.setVisibility(View.VISIBLE);
							remarkContainer.setVisibility(View.VISIBLE);
							noResultContainer.setVisibility(View.GONE);
						}
					}
					
					houseList.addAll(prices);
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
					houseListView.setVisibility(View.GONE);
					remarkContainer.setVisibility(View.GONE);
					noResultContainer.setVisibility(View.VISIBLE);
					showActivityToast(R.string.no_network_please_check);
				}
			}
		});
		
	}
	
	public LatLng getCurrentFirstHouseLatLng(){
		int position = houseListView.getRefreshableView().getFirstVisiblePosition();
		
		//如果搜尋為0筆，回傳目前最新center資訊
		if ((position<0)||(houseList.size()==0)){
			return PriceSearchInfo.getInstance().centerPos;
		}
		
		//取出可視區域第一筆房屋的經緯度
		Price price = houseList.get(position);
		Float lat = price.getFloatData(BHConstants.JSON_KEY_LAT);
		Float lng = price.getFloatData(BHConstants.JSON_KEY_LNG);
		if (lat==0 || lng==0){
			return PriceSearchInfo.getInstance().centerPos;
		}
		
		Log.e("test", "取得可視區域第一筆house: "+" 經緯度:"+lat+","+lng);			
		return new LatLng(lat,lng);
		
	}
    
    
    
}
