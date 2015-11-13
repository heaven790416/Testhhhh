package com.yowoo.newbuyhouse.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thinkermobile.sinyi.R;



public class SelectRow extends RelativeLayout{

	public ImageView iconImageView;
	public TextView titleTextView;
	public RelativeLayout container;
	public ImageView moreImageView;
	//DisplayImageOptions displayImageOptions;//for imageloader
	
//	final static int iconImageResId[] = {
//		R.drawable.ic_house_selector,
//		R.drawable.ic_store_selector
//	};
	
	//TODO:完整選單
	final static int iconImageResId[] = {
		R.drawable.ic_house_selector,
		R.drawable.ic_price_selector,
		R.drawable.ic_track_selector,
		R.drawable.ic_news_selector,
		R.drawable.ic_store_selector,
		R.drawable.ic_chat_selector,
		R.drawable.ic_message_selector,
		R.drawable.ic_message_sell_selector
	};

	public SelectRow(Context context) {
		super(context);

		init(context);
		
	}

	public SelectRow(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context);
	}

	public SelectRow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init(context);
	}

	private void init(Context context){

		LayoutInflater.from(context).inflate(R.layout.select_row, this);

		iconImageView = (ImageView) findViewById(R.id.iconImageView);
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		moreImageView = (ImageView) findViewById(R.id.moreImageView);
		container = (RelativeLayout) findViewById(R.id.container);
		
		iconImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	}
	
	public void reloadCell(int position, String titleName, Boolean isSelected) {
		
		iconImageView.setImageResource(iconImageResId[position]);
		iconImageView.setSelected(isSelected);
		
		titleTextView.setText(titleName);
		
		if (isSelected){
			//container.setBackgroundColor(getResources().getColor(R.color.drawer_bg_select_color));
			titleTextView.setTextColor(getResources().getColor(R.color.drawer_item_select_color));
		}else{
			//container.setBackgroundColor(getResources().getColor(R.color.drawer_bg_color));
			titleTextView.setTextColor(getResources().getColor(R.color.drawer_item_color));
		}
		
		if (position==0 || position==4){
			moreImageView.setVisibility(View.GONE);
		}else{
			moreImageView.setVisibility(View.VISIBLE);
		}
		
		
	}



}
