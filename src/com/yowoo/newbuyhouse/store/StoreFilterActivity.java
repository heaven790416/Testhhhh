package com.yowoo.newbuyhouse.store;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.BaseActivity;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.Singleton;
import com.yowoo.newbuyhouse.view.DynamicWheelView.WheelListener;
import com.yowoo.newbuyhouse.view.SingleChooseRow;
import com.yowoo.newbuyhouse.view.StoreCityWheelView;

public class StoreFilterActivity extends BaseActivity{

	LinearLayout areaContainer;
	Button clearButton, searchButton;
	SingleChooseRow cityRow, areaRow;
	RelativeLayout shadow;
	ScrollView filterScrollView;
	
	//for area mode
	public int selectedCity = 0;
	public int selectedArea = 0;
	
	//wheel
	StoreCityWheelView cityWheelView;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_store_filter);

        //set toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_selector);
        toolbar.setTitleTextColor(getResources().getColor(R.color.title_text_color));
        TextView mTitleTextView = (TextView)toolbar.findViewById(R.id.mTitleTextView);
        mTitleTextView.setText(getString(R.string.drawer_section_store));
        mTitleTextView.setVisibility(View.VISIBLE);
        ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("");
		
        shadow = (RelativeLayout) this.findViewById(R.id.shadow);
        
        areaContainer = (LinearLayout) this.findViewById(R.id.areaContainer);
        
        clearButton = (Button) this.findViewById(R.id.clearButton);
        searchButton = (Button) this.findViewById(R.id.searchButton);
        
        cityWheelView = (StoreCityWheelView) this.findViewById(R.id.aeraWheelView);
        
        cityRow = (SingleChooseRow) this.findViewById(R.id.cityRow);//縣市
        areaRow = (SingleChooseRow) this.findViewById(R.id.areaRow);//行政區
        
        filterScrollView = (ScrollView) this.findViewById(R.id.filterScrollView);
        
        reloadSearchInfoStatus();
        
        //reload views
        reloadViews();
        
        //set listener
        setListener();
        
        //register broadcast receiver of network status
        this.registerReceiver(this.mConnReceiver,
        		new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

       
        
	}
	
	private void reloadViews(){
		//reload區域資訊
		cityWheelView.reloadWheelsToSelected(selectedCity, selectedArea);
		
	}
	
	
	private void reloadAreaViews(String item1, String item2){
		cityRow.setSelectedText(item1);
		areaRow.setSelectedText(item2);
	}
	
	private void setListener(){
		
		View.OnClickListener areaRowListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (cityWheelView.getVisibility()==View.GONE){
					//如果無網路＆無法撈到資訊
					if (!Singleton.isNetworkAvailable()){
						showToast(R.string.no_network_please_check);
						return;
					}
					
					String zipCode = cityWheelView.getWheelTwoValue(selectedArea);
					if (zipCode.equals(StoreCityWheelView.ERROR_WHEEL_INDEX)){
						showToast(R.string.no_network_please_check);
						return;
					}
					
					hideAllWheels();
					shadow.setVisibility(View.VISIBLE);
					cityWheelView.reloadWheelsToSelected(selectedCity, selectedArea);
					cityWheelView.setVisibility(View.VISIBLE);
				}
			}
		};
		
		areaRow.setOnClickListener(areaRowListener);
		cityRow.setOnClickListener(areaRowListener);
		
		cityWheelView.setWheelListener(new WheelListener(){
			@Override
			public Boolean onClickOk(int index1, int index2, String item1, String value1, String item2, String value2){
				if ((index1!=0)&&(index2==0)){
					showToast(R.string.please_select_area);
					return false;
				}
				selectedCity = index1;
				selectedArea = index2;
				
				reloadAreaViews(item1, item2);
				shadow.setVisibility(View.GONE);
				 
				return true;
			}

			@Override
			public void onClickCancel() {
				shadow.setVisibility(View.GONE);
			}

			@Override
			public void onFinishReload(Boolean success) {
				if (success){
					String item1 = cityWheelView.getWheelOneItem(selectedCity);
					String item2 = cityWheelView.getWheelTwoItem(selectedArea);
					reloadAreaViews(item1, item2);
				}else{
					hideWheelsAndShadow();
					showToast(R.string.no_network_please_check);
				}
			}
		});
		
		
		searchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!Singleton.isNetworkAvailable()){
					showToast(R.string.no_network_please_check);
					return;
				}
				
				//save all status to StoreSearchInfo
				StoreSearchInfo.getInstance().selectedCity = selectedCity;
				StoreSearchInfo.getInstance().selectedArea = selectedArea;
				
				//前往搜尋
				if ((selectedCity==0)&&(selectedArea==0)){
					//區域搜尋沒有設定位置條件，searchMode為經緯度位置
					//center設為當前位置
					StoreSearchInfo.getInstance().centerPos = getMyCurrentLocation();
					Log.e("test", "StoreFilter: centerPos:設為當前位置");
					
				}else{
					//區域搜尋有設定位置條件，searchMode為區域位置
					//center設為此area的定點經緯度
					
					//Check: 網路無連線，或抓不到行政區列表
					String zipCode = cityWheelView.getWheelTwoValue(selectedArea);
					if (zipCode.equals(StoreCityWheelView.ERROR_WHEEL_INDEX)){
						showToast(R.string.no_network_please_check);
						return;
					}
					
					StoreSearchInfo.getInstance().centerPos = StoreSearchInfo.getInstance().getLatLngByZipCode(zipCode);
					Log.e("test", "StoreFilter: centerPos:設為目前選擇行政區的位置");
				}
				
				searchAction();	
					
			}
		});
		
		clearButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StoreSearchInfo.getInstance().clearStatus();
				reloadSearchInfoStatus();
				reloadViews();
			}
		});
		
		shadow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hideWheelsAndShadow();
			}
		});
		
	}
	
	/* filter */
	private void reloadSearchInfoStatus(){
		//load previous status
        this.selectedCity = StoreSearchInfo.getInstance().selectedCity;
        this.selectedArea = StoreSearchInfo.getInstance().selectedArea;
        
	}
	
	
	
	private void hideAllWheels(){
		cityWheelView.setVisibility(View.GONE);
	}
	
	@Override
	protected void onDestroy(){
		
		//unregister receiver of network status
		this.unregisterReceiver(this.mConnReceiver);
		
		super.onDestroy();
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
	
	private void searchAction(){
		Intent intent = new Intent();
		intent.setAction(BHConstants.BROADCAST_STORE_SEARCH_REFRESH_MAP);
		LocalBroadcastManager.getInstance(StoreFilterActivity.this).sendBroadcast(intent);
		
		finish();
	}
	
	private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if(currentNetworkInfo.isConnected()){
            		//Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
            		reloadViews();
            }else{
            		showToast(R.string.no_network_please_check);
            		hideWheelsAndShadow();
            }
        }
    };
	
    private void hideWheelsAndShadow(){
    		shadow.setVisibility(View.GONE);
		hideAllWheels();
    }
	
}
