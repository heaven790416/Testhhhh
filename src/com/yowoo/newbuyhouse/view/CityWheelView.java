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

public class CityWheelView extends RelativeLayout{
	
	public AbstractWheel cityWheel, areaWheel;
	public Context context;
	public Button cancelButton, okButton;
	
	private WheelListener wheelListener;
	
	public interface WheelListener{
		void onClickOk(int cityIndex, int areaIndex);
		void onClickCancel();
	}
	
	private String citys[];
	private String areas[][];
	
	public CityWheelView(Context context) {
		super(context);
		init(context);
	}

	public CityWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		
		
	}

	public CityWheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		
	}
	

	protected void init(Context context) {
		this.context = context;
		View root = LayoutInflater.from(context).inflate(R.layout.wheel_view, this);
		cancelButton = (Button) this.findViewById(R.id.cancelButton);
		okButton = (Button) this.findViewById(R.id.okButton);
		
		cityWheel = (AbstractWheel) findViewById(R.id.wheel1);
		areaWheel = (AbstractWheel) findViewById(R.id.wheel2);
		cityWheel.setVisibleItems(3);
		areaWheel.setVisibleItems(3);
		cityWheel.setCurrentItem(0);//不指定
		areaWheel.setCurrentItem(0);//不指定
		initWheels();
    }
	
	
	/* Wheel */
    // Scrolling flag
    private boolean scrolling = false;
    
    private int mActiveCountry=0;
	
	public void initWheels(){
		
		//get citys
		int cityCount = SearchInfo.getInstance().cityList.size();
		citys = new String[cityCount];
		for (int i=0; i<cityCount; i++){
			citys[i] = SearchInfo.getInstance().cityList.get(i).cityName;
			Log.e("test", "citys["+i+"]:"+citys[i]);
		}
		
        
        cityWheel.setViewAdapter(new ArrayWheelAdapter<String>(context, citys));

        //get areas
        areas = new String[cityCount][];
        ArrayList<Area> areaArray;
        for (int i=0; i<cityCount; i++){
        		areaArray = SearchInfo.getInstance().cityList.get(i).areas;
        		int areaSize = areaArray.size();
        		areas[i] = new String[areaSize];
        		for (int j=0; j<areaSize; j++){
        			areas[i][j] = areaArray.get(j).name;
        			Log.e("test", "area["+i+"]["+j+"]:"+areas[i][j]);
        		}
        }
        Log.e("test", "areas:"+areas.toString());
        updateAreas(areaWheel, areas, 0);
        
        areaWheel.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                if (!scrolling) {
                    //mActiveCities[mActiveCountry] = newValue;
                }
            }
        });

        cityWheel.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                if (!scrolling) {
                    updateAreas(areaWheel, areas, newValue);
                }
            }
        });
        
        cityWheel.addScrollingListener( new OnWheelScrollListener() {
            public void onScrollingStarted(AbstractWheel wheel) {
                scrolling = true;
            }
            public void onScrollingFinished(AbstractWheel wheel) {
                scrolling = false;
                updateAreas(areaWheel, areas, cityWheel.getCurrentItem());
            }
        });
        
        //cancel, ok button
        cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CityWheelView.this.setVisibility(View.GONE);
				if (wheelListener!=null){
					wheelListener.onClickCancel();
				}
			}
		});
        
        okButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//save current selected city & area to searchInfo
				int cityIndex = cityWheel.getCurrentItem();
				int areaIndex = areaWheel.getCurrentItem();
				CityWheelView.this.setVisibility(View.GONE);
				
				if (wheelListener!=null){
					wheelListener.onClickOk(cityIndex, areaIndex);
				}
			}
		});

	}
	
	/**
     * Updates the area spinnerwheel
     */
    private void updateAreas(AbstractWheel areaWheel, String areas[][], int index) {
        Log.e("test", "updateAreas!");
    		mActiveCountry = index;
    		for (int i=0;i<areas[index].length;i++){
    			Log.e("test", "area[0]: "+i+": "+areas[index][i]);
    		}
        ArrayWheelAdapter<String> adapter =
            new ArrayWheelAdapter<String>(context, areas[index]);
        adapter.setTextSize(18);
        areaWheel.setCurrentItem(0);
        areaWheel.setViewAdapter(adapter);
    }
    
    public void reloadWheelsToSelected(int cityIndex, int areaIndex){
//    		int cityIndex = SearchInfo.getInstance().selectedCity;
//    		int areaIndex = SearchInfo.getInstance().selectedArea;
    		
    		updateAreas(areaWheel, areas, cityIndex);
    		
    		setCurrentItem(cityIndex, areaIndex);
    }
	
    public void setCurrentItem(int cityIndex, int areaIndex){
    		cityWheel.setCurrentItem(cityIndex);
    		areaWheel.setCurrentItem(areaIndex);
    }
    
    public void setWheelListener(WheelListener listener){
    		this.wheelListener = listener;
    }
    

}
