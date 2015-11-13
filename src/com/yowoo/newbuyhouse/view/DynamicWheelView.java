package com.yowoo.newbuyhouse.view;

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

public abstract class DynamicWheelView extends RelativeLayout{

	public AbstractWheel wheelOne, wheelTwo;
	public Context context;
	public Button cancelButton, okButton;

	private WheelListener wheelListener;

	public interface WheelListener{
		Boolean onClickOk(int index1, int index2, String item1, String value1, String item2, String value2);
		void onClickCancel();
		void onFinishReload(Boolean success);
	}

	public interface ItemCallback{
		void onResult(Boolean success, String items[], String values[]);
	}

	private int index1 = 0;
	private int index2 = 0;
	private String wheelOneItems[];
	private String wheelTwoItems[];
	private String wheelOneValues[];
	private String wheelTwoValues[];

	public DynamicWheelView(Context context) {
		super(context);
		init(context);
	}

	public DynamicWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);


	}

	public DynamicWheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);

	}


	protected void init(Context context) {
		this.context = context;
		View root = LayoutInflater.from(context).inflate(R.layout.wheel_view, this);
		cancelButton = (Button) this.findViewById(R.id.cancelButton);
		okButton = (Button) this.findViewById(R.id.okButton);

		wheelOne = (AbstractWheel) findViewById(R.id.wheel1);
		wheelTwo = (AbstractWheel) findViewById(R.id.wheel2);
		wheelOne.setVisibleItems(3);
		wheelTwo.setVisibleItems(3);
		wheelOne.setCurrentItem(0);//不指定
		wheelTwo.setCurrentItem(0);//不指定

		wheelOneItems = new String[]{};
		wheelTwoItems = new String[]{};
		wheelOneValues = new String[]{};
		wheelTwoValues = new String[]{};

		//initWheels();
		
		setListener();
	}


	/* Wheel */
	// Scrolling flag
	private boolean scrolling = false;
	
	public static final String ERROR_WHEEL_INDEX = "-1";

	public abstract void initWheelOne(ItemCallback callback);
	public abstract void initWheelTwo(int index1, ItemCallback callback);

	private void setListener(){
		wheelTwo.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				if (!scrolling) {
				}
			}
		});

		wheelOne.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				if (!scrolling) {
				}
			}
		});

		wheelOne.addScrollingListener( new OnWheelScrollListener() {
			public void onScrollingStarted(AbstractWheel wheel) {
				scrolling = true;
			}
			public void onScrollingFinished(AbstractWheel wheel) {
				scrolling = false;
				
				initWheelTwo(wheelOne.getCurrentItem(), new ItemCallback(){
					@Override
					public void onResult(Boolean success, String[] items, String[] values) {
						try{
							if (success){
								updateWheelTwo(items, values, 0);
							}else{
								((BaseActivity)context).showToast(R.string.no_network_please_check);
							}
						}catch(Exception e){
							((BaseActivity)context).showToast(R.string.no_network_please_check);
							e.printStackTrace();
						}
					}
				});
			}
		});

		//cancel, ok button
		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DynamicWheelView.this.setVisibility(View.GONE);
				if (wheelListener!=null){
					wheelListener.onClickCancel();
				}
			}
		});

		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//save current selected city & area to searchInfo
				int index1 = wheelOne.getCurrentItem();
				int index2 = wheelTwo.getCurrentItem();
				
				Boolean needClose = true;
				
				if (wheelListener!=null){
					needClose = wheelListener.onClickOk(index1, index2, wheelOneItems[index1], wheelOneValues[index1], wheelTwoItems[index2], wheelTwoValues[index2]);
				}
				
				if (needClose){
					DynamicWheelView.this.setVisibility(View.GONE);
				}
			}
		});
	}

	public void reloadWheelsToSelected(final int index1, final int index2){

		//get citys
		initWheelOne(new ItemCallback(){

			@Override
			public void onResult(Boolean success, String[] items, String[] values) {
				if (!success){
					if (wheelListener!=null){
						wheelListener.onFinishReload(false);
					}
					return;
				}
				
				updateWheelOne(items, values, index1);
				
				initWheelTwo(index1, new ItemCallback(){

					@Override
					public void onResult(Boolean success, String[] items, String[] values) {
						if (success){
							updateWheelTwo(items, values, index2);
							if (wheelListener!=null){
								wheelListener.onFinishReload(true);
							}
						}else{
							if (wheelListener!=null){
								wheelListener.onFinishReload(false);
							}
						}
					}
				});
			}
		});


	}

	/**
	 * Updates the area spinnerwheel
	 */
	public void updateWheelTwo(String wheelTwoItems[], String wheelTwoValues[], int index) {
		Log.e("test", "updateAreas!");
		this.index2 = index;
		this.wheelTwoItems = wheelTwoItems;
		this.wheelTwoValues = wheelTwoValues;

		ArrayWheelAdapter<String> adapter =
				new ArrayWheelAdapter<String>(context, wheelTwoItems);
		adapter.setTextSize(18);
		wheelTwo.setCurrentItem(index);
		wheelTwo.setViewAdapter(adapter);
	}

	public void updateWheelOne(String wheelOneItems[], String wheelOneValues[], int index) {
		Log.e("test", "updateAreas!");
		this.index1 = index;
		this.wheelOneItems = wheelOneItems;
		this.wheelOneValues = wheelOneValues;

		ArrayWheelAdapter<String> adapter =
				new ArrayWheelAdapter<String>(context, wheelOneItems);
		adapter.setTextSize(18);
		wheelOne.setCurrentItem(index);
		wheelOne.setViewAdapter(adapter);
	}

//	public void reloadWheelsToSelected(int cityIndex, int areaIndex){
//		setCurrentItem(cityIndex, areaIndex);
//	}

	public void setCurrentItem(int cityIndex, int areaIndex){
		wheelOne.setCurrentItem(cityIndex);
		wheelTwo.setCurrentItem(areaIndex);
	}

	public void setWheelListener(WheelListener listener){
		this.wheelListener = listener;
	}

	public String getWheelOneItem(int index){
		if (index >= this.wheelOneItems.length){
			return getResources().getString(R.string.not_restrict);
		}

		return wheelOneItems[index];
	}

	public String getWheelOneValue(int index){
		if (index >= this.wheelOneValues.length){
			return ERROR_WHEEL_INDEX;
		}

		return wheelOneValues[index];
	}

	public String getWheelTwoItem(int index){
		if (index >= this.wheelTwoItems.length){
			return getResources().getString(R.string.not_restrict);
		}

		return wheelTwoItems[index];
	}

	public String getWheelTwoValue(int index){
		if (index >= this.wheelTwoValues.length){
			return ERROR_WHEEL_INDEX;
		}

		return wheelTwoValues[index];
	}




}
