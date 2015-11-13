package com.yowoo.newbuyhouse.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.Singleton;
import com.yowoo.newbuyhouse.model.House;

public class TitleRow extends RelativeLayout{

	public ImageView iconImageView;
	public TextView titleTextView;
	public ImageView moreImageView;
	public RelativeLayout container;
	
	public int mode=0; //0:close 1:open
	
	public TitleRow(Context context) {
		super(context);
		init(context);
	}

	public TitleRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		
		TypedArray a = context.getTheme().obtainStyledAttributes(
				attrs,
				R.styleable.filterRow,
				0, 0);

		try {
			
			//basic
			String titleText = a.getString(R.styleable.filterRow_titleText);
			int iconResourceId = a.getResourceId(R.styleable.filterRow_iconResource, 0);
			Boolean showMoreImage = a.getBoolean(R.styleable.filterRow_showMoreImage, false);
			
			this.titleTextView.setText(titleText);
			this.iconImageView.setBackgroundResource(iconResourceId);
			
			if (showMoreImage){
				this.moreImageView.setVisibility(View.VISIBLE);
			}else{
				this.moreImageView.setVisibility(View.GONE);
			}
			
			//advance attribute
			
			// show/hide icon
			Boolean showIcon = a.getBoolean(R.styleable.filterRow_showIcon, true);
			if (!showIcon){
				iconImageView.setVisibility(View.GONE);
			}
			
			// title color
			int titleTextColorId = a.getResourceId(R.styleable.filterRow_titleTextColor, R.color.filter_gray_color);
			titleTextView.setTextColor(getResources().getColor(titleTextColorId));
			
			// row background : color
			int titleBackgroundColorId = a.getResourceId(R.styleable.filterRow_titleBackground, R.color.filter_title_bg_light_color);
			container.setBackgroundColor(getResources().getColor(titleBackgroundColorId));
			
			// row background : drawable
			int titleBackgroundDrawableId = a.getResourceId(R.styleable.filterRow_titleBackgroundDrawable, 0);
			if (titleBackgroundDrawableId!=0){
				container.setBackgroundResource(titleBackgroundDrawableId);
			}
			
			// set row hight
			int pxHeight = a.getInteger(R.styleable.filterRow_rowHeight, 50);
			RelativeLayout.LayoutParams temp = (RelativeLayout.LayoutParams) container.getLayoutParams();
			temp.height = Singleton.dpToPixel(pxHeight);
			container.setLayoutParams(temp);
			
                    
		} finally {
			a.recycle();
		}
	}

	public TitleRow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		
	}
	

	protected void init(Context context) {
		View root = LayoutInflater.from(context).inflate(R.layout.title_view, this);

		iconImageView = (ImageView) findViewById(R.id.iconImageView);
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		moreImageView = (ImageView) findViewById(R.id.moreImageView);
		
		container = (RelativeLayout) findViewById(R.id.container);
		
    }
	
	public void reloadCell(Boolean isOpen) {
		if (!isOpen){
			moreImageView.setBackgroundResource(R.drawable.title_arrow_open);
		}else{
			moreImageView.setBackgroundResource(R.drawable.title_arrow_close);
		}
	}



}
