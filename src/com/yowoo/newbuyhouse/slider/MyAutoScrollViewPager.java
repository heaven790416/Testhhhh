package com.yowoo.newbuyhouse.slider;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class MyAutoScrollViewPager extends AutoScrollViewPager {

    private boolean isPagingEnabled = true;

    private float mDownX;
    private float mDownY;
    private final float SCROLL_THRESHOLD = 10;
    private boolean isOnClick=false;
    
    public MyAutoScrollViewPager(Context context) {
        super(context);
    }

    public MyAutoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //加入以下兩個的效果跟直接加「fakeDrag」差不多...
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    		//return this.isPagingEnabled && super.onTouchEvent(event);
    		
    		if ((!isPagingEnabled)&&(!isOnClick)){
    			Log.e("test", "onTouchEvent1:true event:"+event.getAction());
    			return true;
    		}
    		
    		Boolean test = 	super.onTouchEvent(event);
    		Log.e("test", "onTouchEvent2:"+test+" event:"+event.getAction());
    		
    		return test;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    		Boolean test = super.onInterceptTouchEvent(ev);
    		Log.e("test", "onInterceptTouchEvent:"+test+" event:"+ev.getAction()+" enable:"+isPagingEnabled);
        //return this.isPagingEnabled && super.onInterceptTouchEvent(event);
        //return super.onInterceptTouchEvent(event);
    		
    		switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                isOnClick = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (isOnClick) {
                    Log.i("test", "onClick ");
                    //TODO onClick code
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isOnClick && (Math.abs(mDownX - ev.getX()) > SCROLL_THRESHOLD || Math.abs(mDownY - ev.getY()) > SCROLL_THRESHOLD)) {
                    Log.i("test", "movement detected");
                    isOnClick = false;
                }
                break;
            default:
                break;
        }
    		Log.e("test", "onInterceptTouchEvent:isOnClick:"+isOnClick);
            
    		return test;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
    		Boolean test = super.dispatchTouchEvent(ev);
    		if (!isPagingEnabled){
    			this.stopAutoScroll();
    		}
    		return test;
    }
    
    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }
    
//    @Override
//    public boolean performClick() {
//    			// Calls the super implementation, which generates an AccessibilityEvent
//           // and calls the onClick() listener on the view, if any
//           super.performClick();
//
//           // Handle the action for the custom click here
//           Log.e("test", "MyAutoSrollViewPager: performClick()");
//           return true;
//    }
}