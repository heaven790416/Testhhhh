package com.yowoo.newbuyhouse.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.yowoo.newbuyhouse.BaseActivity;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.model.Area;
import com.yowoo.newbuyhouse.model.City;
import com.yowoo.newbuyhouse.network.StoreService;
import com.yowoo.newbuyhouse.network.StoreService.AreaCallback;
import com.yowoo.newbuyhouse.network.StoreService.CityCallback;

public class StoreCityWheelView extends DynamicWheelView{

	Context context;
	
	public StoreCityWheelView(Context context) {
		super(context);
		this.context = context;
	}
	
	public StoreCityWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public StoreCityWheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	@Override
	public void initWheelOne(final ItemCallback callback) {
		StoreService.getStoreCitys(new CityCallback(){

			@Override
			public void onResult(Boolean success, ArrayList<City> citys) {
				if (success){
					String[] tempItems = new String[citys.size()];
					String[] tempValues = new String[citys.size()];
					for (int i=0; i<citys.size(); i++){
						tempItems[i] = citys.get(i).cityName;
						tempValues[i] = citys.get(i).cityId;
					}
					callback.onResult(true, tempItems, tempValues);
				}else{
					callback.onResult(false, null, null);
					((BaseActivity)context).showToast(R.string.no_network_please_check);
				}
			}
		});
		
	}

	@Override
	public void initWheelTwo(int index1, final ItemCallback callback) {
		
		if (index1==0){
			callback.onResult(true, new String[]{getResources().getString(R.string.not_restrict)}, new String[]{""});
		}else{
			StoreService.getStoreAreas(getWheelOneValue(index1), new AreaCallback(){

				@Override
				public void onResult(Boolean success, ArrayList<Area> areas) {
					if (success){
						String[] tempItems = new String[areas.size()];
						String[] tempValues = new String[areas.size()];
						for (int i=0; i<areas.size(); i++){
							tempItems[i] = areas.get(i).name;
							tempValues[i] = areas.get(i).zipCode;
						}
						callback.onResult(true, tempItems, tempValues);
					}else{
						callback.onResult(false, null, null);
						((BaseActivity)context).showToast(R.string.no_network_please_check);
					}
				}
			});
		}
		
	}

}
