package com.yowoo.newbuyhouse.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.model.Price;
import com.yowoo.newbuyhouse.model.Store;

public class DealPriceRow extends RelativeLayout{

	
	public TextView priceTextView, unitPriceTextView, addressTextView;
	public TextView contentTextView1, contentTextView2, contentTextView3, contentTextView4;
	public RelativeLayout nextContainer, prevContainer;
	
	public DealPriceRow(Context context) {
		super(context);
		init(context);
	}

	public DealPriceRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DealPriceRow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	

	protected void init(Context context) {
		View root = LayoutInflater.from(context).inflate(R.layout.deal_price_row, this);
		
		priceTextView = (TextView) findViewById(R.id.priceTextView);
		unitPriceTextView = (TextView) findViewById(R.id.unitPriceTextView);
		addressTextView = (TextView) findViewById(R.id.addressTextView);
		contentTextView1 = (TextView) findViewById(R.id.contentTextView1);
		contentTextView2 = (TextView) findViewById(R.id.contentTextView2);
		contentTextView3 = (TextView) findViewById(R.id.contentTextView3);
		contentTextView4 = (TextView) findViewById(R.id.contentTextView4);
		
		prevContainer = (RelativeLayout) findViewById(R.id.prevContainer);
		nextContainer = (RelativeLayout) findViewById(R.id.nextContainer);
		
    }
	
	public void reloadCell(final Price price) {
		
		//price
		priceTextView.setText(String.valueOf(price.getIntData(BHConstants.JSON_KEY_PRICE))+"萬");
		
		//unitPrice
		float unitPrice = price.getFloatData(BHConstants.JSON_KEY_UNIT_PRICE);
		String unitPriceString = (unitPrice == (int) unitPrice)?
			String.valueOf((int)unitPrice) : String.format("%s",unitPrice);
		unitPriceString += "萬";
		if (price.getBooleanData(BHConstants.JSON_KEY_OUTLIER)) unitPriceString+="*";
		unitPriceTextView.setText(unitPriceString);
		
		//address
		this.addressTextView.setText(price.getStringData(BHConstants.JSON_KEY_ADDRESS));
		
		//sold date
		int soldDate = price.getIntData(BHConstants.JSON_KEY_SOLD_DATE);
		String year = String.valueOf(soldDate/100);
		String month = String.valueOf(soldDate%100);
		this.contentTextView1.setText(year+"年"+month+"月");
		
		//area
		this.contentTextView2.setText(String.valueOf(price.getFloatData(BHConstants.JSON_KEY_AREA_BUILDING))+"坪");
		
		//age
		this.contentTextView3.setText(String.valueOf(price.getFloatData(BHConstants.JSON_KEY_AGE))+"年");
		
		//type
		String buildingType = price.getStringData(BHConstants.JSON_KEY_BUILDING_TYPE);
		
		int hasGarage = price.getIntData(BHConstants.JSON_KEY_HAS_GARAGE);
		String hasGarageString = (hasGarage==1)? "有車位":"無車位";
		
		this.contentTextView4.setText(buildingType+"\n"+hasGarageString);
		
	}
	
	public void reloadArrow(int position, int houseCount){
		if (position==0){
			this.prevContainer.setVisibility(View.GONE);
		}else{
			this.prevContainer.setVisibility(View.VISIBLE);
		}
		
		if (position==houseCount-1){
			this.nextContainer.setVisibility(View.GONE);
		}else{
			this.nextContainer.setVisibility(View.VISIBLE);
		}
	}
	
}
