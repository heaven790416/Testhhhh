package com.yowoo.newbuyhouse.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.model.House;

public class InfoRow extends RelativeLayout{

	public TextView titleTextView, detailTextView;
	public RelativeLayout container;
	
	public InfoRow(Context context) {
		super(context);
		init(context);
	}

	public InfoRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		
		TypedArray a = context.getTheme().obtainStyledAttributes(
				attrs,
				R.styleable.filterRow,
				0, 0);

		try {
			String titleText = a.getString(R.styleable.filterRow_titleText);
			this.titleTextView.setText(titleText);
			
		} finally {
			a.recycle();
		}
		
	}

	public InfoRow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		
	}
	

	protected void init(Context context) {
		View root = LayoutInflater.from(context).inflate(R.layout.info_row, this);

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		detailTextView = (TextView) findViewById(R.id.detailTextView);
		container = (RelativeLayout) findViewById(R.id.container);
    }
	
	public void setDetailText(String text){
		detailTextView.setText(text);
	}
	



}
