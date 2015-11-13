package com.yowoo.newbuyhouse.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.Singleton;
import com.yowoo.newbuyhouse.model.Store;
import com.yowoo.newbuyhouse.view.StoreRow.StoreRowCallback;

public class StoreListRow extends RelativeLayout{

	public interface StoreListRowCallback {
		public void onClickStore();
		public void onClickCall(String tel);
	}
	
	private StoreListRowCallback storeListRowCallback;
	
	public RelativeLayout storeCountContainer;
	public TextView totalTextView;
	public StoreRow storeRow;
	
	public StoreListRow(Context context) {
		super(context);

		View root = LayoutInflater.from(context).inflate(R.layout.store_list_row, this);

		storeCountContainer = (RelativeLayout) findViewById(R.id.storeCountContainer);
		totalTextView = (TextView) findViewById(R.id.totalTextView);
		storeRow = (StoreRow) findViewById(R.id.storeRow);
		
		//custome leftMargin for imageView
		storeRow.setBackgroundResource(R.drawable.bg_list_top);
		RelativeLayout.LayoutParams params = (LayoutParams) storeRow.thumbImageView.getLayoutParams();
		params.setMargins(Singleton.dpToPixel(12), 0, 0, 0);
		storeRow.thumbImageView.setLayoutParams(params);
		
//		root.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (houseListRowCallback!=null){
//					houseListRowCallback.onClickHouse();
//				}
//			}
//		});
		
		storeRow.setStoreRowCallback(new StoreRowCallback(){

			@Override
			public void onClickCall(String tel) {
				if (storeListRowCallback!=null){
					storeListRowCallback.onClickCall(tel);
				}
			}
		});
		
	}

	public StoreListRow(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StoreListRow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	

	public void reloadCell(int position, int total, Store storeObject) {
		
		if (position==0){
			totalTextView.setText(String.valueOf(total));
			storeCountContainer.setVisibility(View.VISIBLE);
		}else{
			storeCountContainer.setVisibility(View.GONE);
		}

		storeRow.reloadCell(storeObject);
	
	}

	public void setStoreListRowCallback(StoreListRowCallback storeListRowCallback){
		this.storeListRowCallback = storeListRowCallback;
	}
	


}
