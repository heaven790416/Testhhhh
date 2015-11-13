package com.yowoo.newbuyhouse.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thinkermobile.sinyi.R;

public class TrackNoResultRow extends RelativeLayout{

	
	public ImageView thumbImageView;
	public TextView hintTextView;
	
	public TrackNoResultRow(Context context) {
		super(context);
		init(context);
	}

	public TrackNoResultRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TrackNoResultRow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	

	protected void init(Context context) {
		View root = LayoutInflater.from(context).inflate(R.layout.track_no_result_row, this);

		//root.setBackgroundResource(R.drawable.bg_house_bottom_bar);
		//root.setBackgroundColor(getResources().getColor(R.color.white_color));
		
		thumbImageView = (ImageView) findViewById(R.id.thumbImageView);
		hintTextView = (TextView) findViewById(R.id.hintTextView);
		
    }
	
	public void setHint(String hint) {
		
		hintTextView.setText(hint);
		
	}

	


}
