package com.yowoo.newbuyhouse.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yowoo.newbuyhouse.BHConstants;
import com.thinkermobile.sinyi.R;

public class PriceRow extends RelativeLayout{

	public TextView titleTextView;
	public TextView lowPriceTextView, highPriceTextView;
	public Button modeButton;
	
	//int mode = 0; //0:滾輪 1:自訂價格
	
	private PriceRowListener listener;
	
	public interface PriceRowListener{
		public void onClickLowPrice();
		public void onClickHighPrice();
		public void onClickButton();
	}
	
	public PriceRow(Context context) {
		super(context);
		init(context);
	}

	public PriceRow(Context context, AttributeSet attrs) {
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

	public PriceRow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		
	}
	

	protected void init(Context context) {
		View root = LayoutInflater.from(context).inflate(R.layout.price_view, this);

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		this.lowPriceTextView = (TextView) findViewById(R.id.lowPriceTextView);
		this.highPriceTextView = (TextView) findViewById(R.id.highPriceTextView);
		this.modeButton = (Button) findViewById(R.id.modeButton);
		
		this.lowPriceTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (listener!=null){
					listener.onClickLowPrice();
				}
			}
		});
		
		this.highPriceTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.e("test", "click high price!");
				if (listener!=null){
					listener.onClickHighPrice();
				}
			}
		});
		
		this.modeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (listener!=null){
					listener.onClickButton();
				}
			}
		});
    }
	
	public void setListener(PriceRowListener listener){
		this.listener = listener;
	}
	
	public void reloadRow(int lowPrice, int highPrice){
		
		String lowPriceText = lowPrice+"萬";
		String highPriceText = 
				(highPrice>=BHConstants.MAX_PRICE)?"不指定":highPrice+"萬";
		
		this.lowPriceTextView.setText(lowPriceText);
		this.highPriceTextView.setText(highPriceText);
		
	}
	
//	public void reloadModeButton(int mode){
//		if (mode==0){
//			//目前是滾輪模式
//			this.modeButton.setText("自訂價格");
//		}else{
//			this.modeButton.setText("切回選項");
//		}
//	}
	
	
	



}
