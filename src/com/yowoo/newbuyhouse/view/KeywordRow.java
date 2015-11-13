package com.yowoo.newbuyhouse.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thinkermobile.sinyi.R;

public class KeywordRow extends RelativeLayout{

	public TextView titleTextView;
	public EditText keywordEditText;
	
	public KeywordRow(Context context) {
		super(context);
		init(context);
	}

	public KeywordRow(Context context, AttributeSet attrs) {
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

	public KeywordRow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		
	}
	

	protected void init(Context context) {
		View root = LayoutInflater.from(context).inflate(R.layout.keyword_row, this);

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		keywordEditText = (EditText) findViewById(R.id.keywordEditText);
		
    }
	
	public String getKeywordText(){
		return this.keywordEditText.getText().toString();
	}
	
	public void setKeywordText(String text){
		this.keywordEditText.setText(text);
	}
	
	



}
