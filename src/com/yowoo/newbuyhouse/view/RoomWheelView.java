package com.yowoo.newbuyhouse.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.OnWheelScrollListener;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;

import com.yowoo.newbuyhouse.BHConstants;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.SearchInfo;
import com.yowoo.newbuyhouse.model.Area;

public class RoomWheelView extends RelativeLayout{
	
	public AbstractWheel lowWheel, highWheel;
	public Context context;
	public Button cancelButton, okButton;
	
	private RoomWheelListener wheelListener;
	
	public interface RoomWheelListener{
		void onClickOk(int lowIndex, int highIndex, String lowParam, String highParam);
		void onClickCancel();
	}
	
	private String lowItems[] = new String[]{"不指定","1房","2房","3房","4房"};
	private String highItems[] = new String[]{"1房","2房","3房","4房","不指定"};
	private String lowParams[] = new String[]{"","1","2","3","4"};
	private String highParams[] = new String[]{"1","2","3","4",""};
	
	public RoomWheelView(Context context) {
		super(context);
		init(context);
	}

	public RoomWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		
		
	}

	public RoomWheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		
	}
	

	protected void init(Context context) {
		this.context = context;
		View root = LayoutInflater.from(context).inflate(R.layout.wheel_view, this);
		cancelButton = (Button) this.findViewById(R.id.cancelButton);
		okButton = (Button) this.findViewById(R.id.okButton);
		
		lowWheel = (AbstractWheel) findViewById(R.id.wheel1);
		highWheel = (AbstractWheel) findViewById(R.id.wheel2);
		lowWheel.setVisibleItems(3);
		highWheel.setVisibleItems(3);
		lowWheel.setCurrentItem(0);
		highWheel.setCurrentItem(highItems.length-1);//不限
		initWheels();
    }
	
	
	/* Wheel */
    // Scrolling flag
    private boolean scrolling = false;
	
	public void initWheels(){
		lowWheel.setViewAdapter(new ArrayWheelAdapter<String>(context, lowItems));
		highWheel.setViewAdapter(new ArrayWheelAdapter<String>(context, highItems));
		
        highWheel.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                if (!scrolling) {
	                	if (newValue<(lowWheel.getCurrentItem()-1)){
	                		highWheel.setCurrentItem(lowWheel.getCurrentItem()-1);
	                	}
                }
            }
        });

        lowWheel.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                if (!scrolling) {
                    updateHighWheel(newValue);
                }
            }
        });
        
        lowWheel.addScrollingListener( new OnWheelScrollListener() {
            public void onScrollingStarted(AbstractWheel wheel) {
                scrolling = true;
            }
            public void onScrollingFinished(AbstractWheel wheel) {
                scrolling = false;
                updateHighWheel(lowWheel.getCurrentItem());
            }
        });
        
        //cancel, ok button
        cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RoomWheelView.this.setVisibility(View.GONE);
				if (wheelListener!=null){
					wheelListener.onClickCancel();
				}
			}
		});
        
        okButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String lowParam = lowParams[lowWheel.getCurrentItem()];
				String highParam = highParams[highWheel.getCurrentItem()];
				RoomWheelView.this.setVisibility(View.GONE);
				
				if (wheelListener!=null){
					wheelListener.onClickOk(lowWheel.getCurrentItem(), highWheel.getCurrentItem(),
											lowParam, highParam);
				}
			}
		});

	}
	
	/**
     * Updates the high spinnerwheel
     */
    private void updateHighWheel(int lowIndex) {
	    	if (highWheel.getCurrentItem()<lowIndex-1){
	    		highWheel.setCurrentItem(lowIndex-1);
	    }
    }
    
    public void reloadWheelsToSelected(String lowParam, String highParam){
    		
    		//lowParam -> "" -> 不指定
    		int lowIndex = 0;
    		for (int i=0; i<lowParams.length; i++){
    			if (lowParam.equals(lowParams[i])){
    				lowIndex = i;
    				break;
    			}
    		}
    		
    		//highParam -> "" -> 不限
    		int highIndex = 0; 
    		for (int i=0; i<highParams.length; i++){
    			if (highParam.equals(highParams[i])){
    				highIndex = i;
    				break;
    			}
    		}
    		
    		setCurrentItem(lowIndex, highIndex);
    }
	
    public void setCurrentItem(int lowIndex, int highIndex){
    		lowWheel.setCurrentItem(lowIndex);
    		highWheel.setCurrentItem(highIndex);
    }
    
    public void setWheelListener(RoomWheelListener listener){
    		this.wheelListener = listener;
    }
    

}
