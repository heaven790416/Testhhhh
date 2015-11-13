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

public class PriceWheelView extends RelativeLayout{
	
	public AbstractWheel lowWheel, highWheel;
	public Context context;
	public Button cancelButton, okButton;
	
	private PriceWheelListener wheelListener;
	
	public interface PriceWheelListener{
		void onClickOk(int lowPrice, int highPrice);
		void onClickCancel();
	}
	
	private String lowPriceItems[] = new String[]{"0萬","200萬","500萬","800萬","1000萬","1200萬","1500萬","2000萬","3000萬","3500萬","4000萬","4500萬","5000萬"};
	private String highPriceItems[] = new String[]{"200萬","500萬","800萬","1000萬","1200萬","1500萬","2000萬","3000萬","3500萬","4000萬","4500萬","5000萬","不指定"};
	private int lowPrices[] = new int[]{0,200,500,800,1000,1200,1500,2000,3000,3500,4000,4500,5000};
	private int highPrices[] = new int[]{200,500,800,1000,1200,1500,2000,3000,3500,4000,4500,5000,BHConstants.MAX_PRICE};
	
	public PriceWheelView(Context context) {
		super(context);
		init(context);
	}

	public PriceWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		
		
	}

	public PriceWheelView(Context context, AttributeSet attrs, int defStyle) {
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
		lowWheel.setCurrentItem(0);//0萬
		highWheel.setCurrentItem(highPrices.length-1);//不限
		initWheels();
    }
	
	
	/* Wheel */
    // Scrolling flag
    private boolean scrolling = false;
	
	public void initWheels(){
		lowWheel.setViewAdapter(new ArrayWheelAdapter<String>(context, lowPriceItems));
		highWheel.setViewAdapter(new ArrayWheelAdapter<String>(context, highPriceItems));
		
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
				PriceWheelView.this.setVisibility(View.GONE);
				if (wheelListener!=null){
					wheelListener.onClickCancel();
				}
			}
		});
        
        okButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int lowPrice = lowPrices[lowWheel.getCurrentItem()];
				int highPrice = highPrices[highWheel.getCurrentItem()];
				PriceWheelView.this.setVisibility(View.GONE);
				
				if (wheelListener!=null){
					wheelListener.onClickOk(lowPrice, highPrice);
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
    
    public void reloadWheelsToSelected(int lowPrice, int highPrice){
    		
    		int lowIndex = 0;
    		for (int i=0; i<lowPrices.length; i++){
    			if (lowPrice<=lowPrices[i]){
    				lowIndex = i;
    				break;
    			}
    		}
    		
    		int highIndex = 0;
    		for (int i=0; i<highPrices.length; i++){
    			if (highPrice<=highPrices[i]){
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
    
    public void setWheelListener(PriceWheelListener listener){
    		this.wheelListener = listener;
    }
    

}
