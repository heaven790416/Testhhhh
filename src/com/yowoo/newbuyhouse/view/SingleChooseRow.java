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
import com.yowoo.newbuyhouse.model.House;

public class SingleChooseRow extends RelativeLayout{

	public TextView titleTextView, selectedTextView;
	public ImageView arrowImageView;
	
	public SingleChooseRow(Context context) {
		super(context);
		init(context);
	}

	public SingleChooseRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		
		TypedArray a = context.getTheme().obtainStyledAttributes(
				attrs,
				R.styleable.filterRow,
				0, 0);

		try {
			String titleText = a.getString(R.styleable.filterRow_titleText);
			this.titleTextView.setText(titleText);
			
			Boolean showMoreImage = a.getBoolean(R.styleable.filterRow_showMoreImage, true);
			if (!showMoreImage){
				this.arrowImageView.setVisibility(View.INVISIBLE);
			}
			
			Boolean showDetailText = a.getBoolean(R.styleable.filterRow_showDetailText, true);
			if (!showDetailText){
				this.selectedTextView.setVisibility(View.INVISIBLE);
			}
		} finally {
			a.recycle();
		}
		
	}

	public SingleChooseRow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		
	}
	

	protected void init(Context context) {
		View root = LayoutInflater.from(context).inflate(R.layout.single_choose_view, this);

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		selectedTextView = (TextView) findViewById(R.id.selectedTextView);
		arrowImageView = (ImageView) findViewById(R.id.arrowImageView);
    }
	
	public void setSelectedText(String text){
		selectedTextView.setText(text);
	}
	



}
