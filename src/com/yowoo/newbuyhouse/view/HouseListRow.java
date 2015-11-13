package com.yowoo.newbuyhouse.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.Singleton;
import com.yowoo.newbuyhouse.model.House;
import com.yowoo.newbuyhouse.view.HouseRow.HouseRowCallback;

public class HouseListRow extends RelativeLayout{

	public interface HouseListRowCallback {
		public void onClickHouse();
	}
	
	public interface HouseListRowSelectCallback {
		public void onClickSelect();
	}
	
	private HouseListRowCallback houseListRowCallback;
	private HouseListRowSelectCallback houseListRowSelectCallback;
	
	public RelativeLayout houseCountContainer;
	public TextView totalTextView;
	public HouseRow houseRow;
	public TextView moreTextView;
	
	public RelativeLayout shadowContainer;
	public Button selectButton;
	//public Boolean isSelected = false;
	
	//for track subscribe house
	public RelativeLayout newContainer;
	
	public HouseListRow(Context context) {
		super(context);

		View root = LayoutInflater.from(context).inflate(R.layout.house_list_row, this);

		houseCountContainer = (RelativeLayout) findViewById(R.id.houseCountContainer);
		totalTextView = (TextView) findViewById(R.id.totalTextView);
		houseRow = (HouseRow) findViewById(R.id.houseRow);
		moreTextView = (TextView) findViewById(R.id.moreTextView);
		
		shadowContainer =(RelativeLayout) findViewById(R.id.shadowContainer);
		selectButton = (Button) findViewById(R.id.selectButton);
		
		newContainer =(RelativeLayout) findViewById(R.id.newContainer);
		
		//custome leftMargin for imageView
		houseRow.setBackgroundResource(R.drawable.bg_list_top);
		RelativeLayout.LayoutParams params = (LayoutParams) houseRow.thumbImageView.getLayoutParams();
		params.setMargins(Singleton.dpToPixel(12), 0, 0, 0);
		houseRow.thumbImageView.setLayoutParams(params);
		
		root.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (houseListRowCallback!=null){
					houseListRowCallback.onClickHouse();
				}
			}
		});
		
		houseRow.setHouseRowCallback(new HouseRowCallback() {
			
			@Override
			public void onClickPrevArrow() {
			}
			
			@Override
			public void onClickNextArrow() {
			}
			
			@Override
			public void onClickHouse(String houseNO) {
				if (houseListRowCallback!=null){
					houseListRowCallback.onClickHouse();
				}
			}
		});
		
		selectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (houseListRowSelectCallback!=null){
					houseListRowSelectCallback.onClickSelect();
				}
			}
		});
		
	}

	public HouseListRow(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HouseListRow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	

	public void reloadCell(int position, int total, House houseObject) {//TODO:
		
		if (position==0){
			totalTextView.setText(String.valueOf(total));
			houseCountContainer.setVisibility(View.VISIBLE);
		}else{
			houseCountContainer.setVisibility(View.GONE);
		}

		houseRow.reloadCell(houseObject);
		
		String moreText = "";
		if (houseObject.areaBuilding>0){
			moreText += houseObject.areaBuilding+" "+Singleton.resources.getString(R.string.area_building)+"  ";
		}
		
		if (!houseObject.layout.equals("")){
			moreText += houseObject.layout+"  ";
		}
		
		moreText += houseObject.age+" "+Singleton.resources.getString(R.string.year);
		moreTextView.setText(moreText);		
	}

	public void setHouseListRowCallback(HouseListRowCallback houseListRowCallback){
		this.houseListRowCallback = houseListRowCallback;
	}
	
	
	//for track: collection
	public void setHouseListRowSelectCallback(HouseListRowSelectCallback houseListRowSelectCallback){
		this.houseListRowSelectCallback = houseListRowSelectCallback;
	}
	
	public void setSelectViewVisibility(int visibility){
		shadowContainer.setVisibility(visibility);
	}
	
	public void reloadSelectView(Boolean isSelected){
		//this.isSelected = isSelected;
		if (isSelected){
			selectButton.setBackgroundResource(R.drawable.tick_collect_selected);
		}else{
			selectButton.setBackgroundResource(R.drawable.tick_collect_normal);
		}
	}
	
	//for track: subscribe
	public void reloadNewHouseView(Boolean isNew){
		if (isNew){
			newContainer.setVisibility(View.VISIBLE);
		}else{
			newContainer.setVisibility(View.GONE);
		}
	}


}
