package com.yowoo.newbuyhouse.view;

import java.util.HashMap;

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

import com.yowoo.newbuyhouse.BaseActivity;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.SearchInfo;
import com.yowoo.newbuyhouse.Singleton;

public class MRTWheelView extends RelativeLayout{
	
	public AbstractWheel locationWheel, lineWheel, stationWheel;
	public Context context;
	public Button cancelButton, okButton;
	
	private MRTWheelListener wheelListener;
	
	public interface MRTWheelListener{
		void onClickOk(int locationIndex, int lineIndex, int stationIndex);
		void onClickCancel();
	}
	
	private String locations[];
	public HashMap<String, String[]> locLineHashMap = new HashMap<String, String[]>();
	public HashMap<String, String[]> lineStationHashMap = new HashMap<String, String[]>();
	
	public MRTWheelView(Context context) {
		super(context);
		init(context);
	}

	public MRTWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		
		
	}

	public MRTWheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		
	}
	

	protected void init(Context context) {
		Log.e("test", "MRTWheelView: init()");
		this.context = context;
		View root = LayoutInflater.from(context).inflate(R.layout.mrt_wheel_view, this);
		cancelButton = (Button) this.findViewById(R.id.cancelButton);
		okButton = (Button) this.findViewById(R.id.okButton);
		
		locationWheel = (AbstractWheel) findViewById(R.id.wheel1);
		lineWheel = (AbstractWheel) findViewById(R.id.wheel2);
		stationWheel = (AbstractWheel) findViewById(R.id.wheel3);
		locationWheel.setVisibleItems(3);
		lineWheel.setVisibleItems(3);
		stationWheel.setVisibleItems(3);
		locationWheel.setCurrentItem(0);//不指定
		lineWheel.setCurrentItem(0);//不指定
		stationWheel.setCurrentItem(0);//不指定
		initWheels();
    }
	
	
	/* Wheel */
    // Scrolling flag
    private boolean scrolling = false;
    private String mActiveLocationKey = "";
    private String mActiveLineKey = "";
	
	public void initWheels(){
		Log.e("test", "MRTWheelView: initWheels()");
		
		//get locations
		this.locations = SearchInfo.getInstance().mrtLocations;
		locationWheel.setViewAdapter(new ArrayWheelAdapter<String>(context, locations));

        //get lines
		String notRestrictText = Singleton.resources.getString(R.string.not_restrict);
        this.locLineHashMap = SearchInfo.getInstance().locLineHashMap;
        this.updateLines(lineWheel, locLineHashMap, notRestrictText);

        //get stations
        this.lineStationHashMap = SearchInfo.getInstance().lineStationHashMap;
        this.updateStations(stationWheel, lineStationHashMap, notRestrictText);
        
        //todo
        locationWheel.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                if (!scrolling) {
                		updateLines(lineWheel, locLineHashMap, locations[newValue]);
                }
            }
        });
        
        locationWheel.addScrollingListener( new OnWheelScrollListener() {
            public void onScrollingStarted(AbstractWheel wheel) {
                scrolling = true;
            }
            public void onScrollingFinished(AbstractWheel wheel) {
                scrolling = false;
                updateLines(lineWheel, locLineHashMap, locations[locationWheel.getCurrentItem()]);
            }
        });
        
        lineWheel.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                if (!scrolling) {
                		String[] currentLines = locLineHashMap.get(locations[locationWheel.getCurrentItem()]);
                		String lineKey = currentLines[newValue];
                		updateStations(stationWheel, lineStationHashMap, lineKey);
                }
            }
        });
       
        lineWheel.addScrollingListener( new OnWheelScrollListener() {
            public void onScrollingStarted(AbstractWheel wheel) {
                scrolling = true;
            }
            public void onScrollingFinished(AbstractWheel wheel) {
                scrolling = false;
                String[] currentLines = locLineHashMap.get(locations[locationWheel.getCurrentItem()]);
                String lineKey = currentLines[lineWheel.getCurrentItem()];
                updateStations(stationWheel, lineStationHashMap, lineKey);
            }
        });
        
        //cancel, ok button
        cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MRTWheelView.this.setVisibility(View.GONE);
				if (wheelListener!=null){
					wheelListener.onClickCancel();
				}
			}
		});
        
        okButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int locationIndex = locationWheel.getCurrentItem();
				int lineIndex = lineWheel.getCurrentItem();
				int stationIndex = stationWheel.getCurrentItem();
				
				//check：如果有選擇北區中區南區，至少需選擇一個捷運線
				if ((locationIndex>0)&&(lineIndex==0)){
					try{
					((BaseActivity)context).showShortToast(getResources().getString(R.string.hint_need_select_mrt_line));
					}catch(Exception e){}
					
					return;
				}
				
				MRTWheelView.this.setVisibility(View.GONE);
				
				if (wheelListener!=null){
					wheelListener.onClickOk(locationIndex, lineIndex, stationIndex);
				}
			}
		});

	}
	
	/**
     * Updates the line spinnerwheel
     */
    private void updateLines(AbstractWheel lineWheel, HashMap<String, String[]> hashMap, String locationKey) {
        Log.e("test", "updateLines!");
    		this.mActiveLocationKey = locationKey;
    		
        ArrayWheelAdapter<String> adapter =
            new ArrayWheelAdapter<String>(context, hashMap.get(locationKey));
        adapter.setTextSize(18);
        lineWheel.setCurrentItem(0);
        lineWheel.setViewAdapter(adapter);
    }
    
    private void updateStations(AbstractWheel stationWheel, HashMap<String, String[]> hashMap, String lineKey) {
        Log.e("test", "updateStations!");
    		this.mActiveLineKey = lineKey;
    		
        ArrayWheelAdapter<String> adapter =
            new ArrayWheelAdapter<String>(context, hashMap.get(lineKey));
        adapter.setTextSize(18);
        stationWheel.setCurrentItem(0);
        stationWheel.setViewAdapter(adapter);
    }
    
    public void reloadWheelsToSelected(int locationIndex, int lineIndex, int stationIndex){
//    		int locationIndex = SearchInfo.getInstance().selectedLocation;
//    		int lineIndex = SearchInfo.getInstance().selectedLine;
//    		int stationIndex = SearchInfo.getInstance().selectedStation;
    		
    		String locationKey = this.locations[locationIndex];
    		this.updateLines(lineWheel, locLineHashMap, locationKey);
    		
    		//get line key
    		Log.e("test", "locationKey:"+locationKey);

//    		for(Entry<String, String[]> entry : lineStationHashMap.entrySet()) {
//    		    String key = entry.getKey();
//    		    String[] value = entry.getValue();
//    		    Log.e("test", "lineStationKey: "+key);
//    		}
//    		
//    		if (lineStationHashMap==null) Log.e("test", "lineStationHashMap==null");
//    		if (lineStationHashMap.get(locationKey)==null) Log.e("test", "lineStationHashMap.get(locationKey)==null");
    		String lineKey = locLineHashMap.get(locationKey)[lineIndex];
    		this.updateStations(stationWheel, lineStationHashMap, lineKey);
    		
    		setCurrentItem(locationIndex, lineIndex, stationIndex);
    }
	
    public void setCurrentItem(int locationIndex, int lineIndex, int stationIndex){
    		locationWheel.setCurrentItem(locationIndex);
    		lineWheel.setCurrentItem(lineIndex);
    		stationWheel.setCurrentItem(stationIndex);
    }
    
    public void setWheelListener(MRTWheelListener listener){
    		this.wheelListener = listener;
    }
    

}
