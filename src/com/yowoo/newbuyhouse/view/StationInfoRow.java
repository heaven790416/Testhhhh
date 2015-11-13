package com.yowoo.newbuyhouse.view;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yowoo.newbuyhouse.BHConstants;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.Singleton;

public class StationInfoRow extends RelativeLayout{

	public TextView stationTextView, walkTextView;
	public LinearLayout lineContainer;
	
	private HashMap<String,Integer> mrtResourceIdHashMap = new HashMap<String,Integer>();
	private static final String mrtColors[] = {
		 "C48D32",//棕
		 "FFCC33",//黃
		 "E3002D",//紅
		 "0070BC",//深藍
		 "018659",//綠
		 "1E90FF",//淺藍
		 "800080",//紫
		 "dde01f"//怪綠verdant
	};
	
	private static final int mrtResourceIds[] = {
		R.drawable.mrt_brown,
		R.drawable.mrt_yellow,
		R.drawable.mrt_red,
		R.drawable.mrt_blue,
		R.drawable.mrt_green,
		R.drawable.mrt_blue,
		R.drawable.mrt_purple,
		R.drawable.mrt_verdant
	};
	
	Context context;

	public StationInfoRow(Context context) {
		super(context);
		init(context);
	}

	public StationInfoRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

	}

	public StationInfoRow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);

	}


	protected void init(Context context) {
		this.context = context;
		View root = LayoutInflater.from(context).inflate(R.layout.station_info_row, this);

		stationTextView = (TextView) findViewById(R.id.stationTextView);
		walkTextView = (TextView) findViewById(R.id.walkTextView);
		lineContainer = (LinearLayout) findViewById(R.id.lineContainer);
	
		for (int i=0; i<mrtColors.length; i++){
			mrtResourceIdHashMap.put(mrtColors[i], mrtResourceIds[i]);
		}
	}


	public void reloadView(JSONObject stationObject){
		try {
			String station = stationObject.getString(BHConstants.JSON_KEY_STATION);

			JSONObject exitObject = stationObject.getJSONObject(BHConstants.JSON_KEY_EXIT);
			String exitName = exitObject.getString(BHConstants.JSON_KEY_NAME);
			String exitDuration = exitObject.getString(BHConstants.JSON_KEY_DURATION);
			String exitDistance = exitObject.getString(BHConstants.JSON_KEY_DISTANCE);

			stationTextView.setText(station+" "+exitName);
			walkTextView.setText(exitDuration+" "+exitDistance);
			
			//add lines
			addLines(stationObject.getJSONArray(BHConstants.JSON_KEY_LINE));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void addLines(JSONArray lineJSONArray){
		try{
			for (int i=0; i<lineJSONArray.length(); i++){
				JSONObject lineObject = lineJSONArray.getJSONObject(i);
				String lineName = lineObject.getString(BHConstants.JSON_KEY_NAME);
				String lineColor = lineObject.getString(BHConstants.JSON_KEY_COLOR);
				
				//get line image resourceId
				Integer resId = mrtResourceIdHashMap.get(lineColor);
				if (resId==null) resId = mrtResourceIds[0];
				
				ImageView iv = new ImageView(context);
				iv.setBackgroundResource(resId);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Singleton.dpToPixel(20), Singleton.dpToPixel(20));
				iv.setLayoutParams(params);
				
				TextView tv = new TextView(context);
				tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				tv.setTypeface(Typeface.DEFAULT_BOLD);
				tv.setTextColor(getResources().getColor(R.color.detail_info_text_color));
				tv.setText(lineName);
				params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 0, Singleton.dpToPixel(10), 0);
				tv.setLayoutParams(params);
				
				lineContainer.addView(iv);
				lineContainer.addView(tv);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}


}
