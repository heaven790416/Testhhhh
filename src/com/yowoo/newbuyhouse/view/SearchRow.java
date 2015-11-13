package com.yowoo.newbuyhouse.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.view.HouseListRow.HouseListRowCallback;
import com.yowoo.newbuyhouse.view.HouseListRow.HouseListRowSelectCallback;

public class SearchRow extends RelativeLayout{

	public interface SearchRowCallback {
		public void onClick();
	}
	
	public interface SearchRowSelectCallback {
		public void onClickSelect();
	}
	
	public TextView titleTextView;
	public TextView badgeTextView;
	
	public RelativeLayout shadowContainer;
	public Button selectButton;
	
	private SearchRowCallback searchRowCallback;
	private SearchRowSelectCallback searchRowSelectCallback;
	
	
	public SearchRow(Context context) {
		super(context);
		init(context);
	}

	public SearchRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SearchRow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	

	protected void init(Context context) {
		View root = LayoutInflater.from(context).inflate(R.layout.search_row, this);

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		badgeTextView = (TextView) findViewById(R.id.badgeTextView);
		
		shadowContainer =(RelativeLayout) findViewById(R.id.shadowContainer);
		selectButton = (Button) findViewById(R.id.selectButton);
	
		root.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (searchRowCallback!=null){
					searchRowCallback.onClick();
				}
			}
		});
		
		selectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (searchRowSelectCallback!=null){
					searchRowSelectCallback.onClickSelect();
				}
			}
		});
		
    }
	
	public void reloadCell(String params, int count){
		titleTextView.setText(params);
		if (count>0){
			badgeTextView.setText(String.valueOf(count));
			badgeTextView.setVisibility(View.VISIBLE);
		}else{
			badgeTextView.setVisibility(View.GONE);
		}
		
	}

	
	//for select
	public void setSelectCallback(SearchRowSelectCallback searchRowSelectCallback){
		this.searchRowSelectCallback = searchRowSelectCallback;
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

}
