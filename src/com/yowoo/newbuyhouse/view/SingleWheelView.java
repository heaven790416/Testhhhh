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

import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.SearchInfo;
import com.yowoo.newbuyhouse.model.Area;

public class SingleWheelView extends RelativeLayout{
	
	public AbstractWheel wheel;
	public Context context;
	public Button cancelButton, okButton;
	
	private SingleWheelListener wheelListener;
	
	public interface SingleWheelListener{
		void onClickOk(int selectedIndex);
		void onClickCancel();
	}
	
	private String chooseItems[];
	
	public SingleWheelView(Context context) {
		super(context);
		init(context);
	}

	public SingleWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		
		
	}

	public SingleWheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		
	}
	

	protected void init(Context context) {
		this.context = context;
		View root = LayoutInflater.from(context).inflate(R.layout.single_wheel_view, this);
		cancelButton = (Button) this.findViewById(R.id.cancelButton);
		okButton = (Button) this.findViewById(R.id.okButton);
		
		wheel = (AbstractWheel) findViewById(R.id.wheel);
		wheel.setVisibleItems(3);
		wheel.setCurrentItem(0);//不指定
    }
	
	
	/* Wheel */
    // Scrolling flag
    private boolean scrolling = false;
    
    private int mActiveCountry=0;
	
	public void initWheels(String[] chooseItems, int selectedIndex){
		
		this.chooseItems = chooseItems;
        wheel.setViewAdapter(new ArrayWheelAdapter<String>(context, chooseItems));
        wheel.setCurrentItem(selectedIndex);
        
        wheel.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                if (!scrolling) {
                    //TODO
                }
            }
        });
        
        wheel.addScrollingListener( new OnWheelScrollListener() {
            public void onScrollingStarted(AbstractWheel wheel) {
                scrolling = true;
            }
            public void onScrollingFinished(AbstractWheel wheel) {
                scrolling = false;
                //TODO
            }
        });
        
        //cancel, ok button
        cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SingleWheelView.this.setVisibility(View.GONE);
				if (wheelListener!=null){
					wheelListener.onClickCancel();
				}
			}
		});
        
        okButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SingleWheelView.this.setVisibility(View.GONE);
				
				if (wheelListener!=null){
					wheelListener.onClickOk(wheel.getCurrentItem());
				}
			}
		});

	}
	
    
    public void reloadWheelsToSelected(int selectedIndex){
//    		int cityIndex = SearchInfo.getInstance().selectedCity;
//    		int areaIndex = SearchInfo.getInstance().selectedArea;
    		
    		setCurrentItem(selectedIndex);
    }
	
    public void setCurrentItem(int selectedIndex){
    		wheel.setCurrentItem(selectedIndex);
    }
    
    public void setWheelListener(SingleWheelListener listener){
    		this.wheelListener = listener;
    }
    

}
