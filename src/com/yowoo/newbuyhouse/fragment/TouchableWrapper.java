package com.yowoo.newbuyhouse.fragment;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchableWrapper extends FrameLayout {
	
	Boolean mMapIsTouched = false;
	
	public TouchableWrapper(Context context) {
		super(context);
		
	}
	
	public TouchableWrapper(Context context, AttributeSet attrs) {
		super(context, attrs);
    }

    public TouchableWrapper(Context context, AttributeSet attrs, int defStyleAttr) {
    		super(context, attrs, defStyleAttr);
    }


	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mMapIsTouched = true;
			break;

		case MotionEvent.ACTION_UP:
			mMapIsTouched = false;
			break;
		}

		Log.d("test", "mMapIsTouched: "+mMapIsTouched);
		
		return super.dispatchTouchEvent(ev);
	}
}