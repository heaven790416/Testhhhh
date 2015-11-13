package com.yowoo.newbuyhouse.view;

import org.json.JSONArray;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.Singleton;

public class MRTInfoRow extends RelativeLayout{

	public InfoRow mrtInfoRow;
	public RelativeLayout showContainer;
	public LinearLayout stationContainer;
	public View root;
	public TextView showTextView;
	Context context;
	
	public MRTInfoRow(Context context) {
		super(context);
		init(context);
	}

	public MRTInfoRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		
	}

	public MRTInfoRow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		
	}
	

	protected void init(Context context) {
		this.context = context;
		
		root = LayoutInflater.from(context).inflate(R.layout.mrt_info_row, this);

		mrtInfoRow = (InfoRow) findViewById(R.id.mrtTitleInfoRow);
		showContainer = (RelativeLayout) findViewById(R.id.showContainer);
		showTextView = (TextView) this.findViewById(R.id.showTextView);
		stationContainer = (LinearLayout) findViewById(R.id.stationContainer);
		
		mrtInfoRow.detailTextView.setText("");
		mrtInfoRow.container.setBackgroundColor(getResources().getColor(R.color.white_color));
    }
	
	public void reloadView(JSONArray mrtInfos){
		//如果沒有捷運資料，隱藏整個捷運資訊列
		if (mrtInfos.length()==0){
			root.setVisibility(View.GONE);
			Log.e("test", "test1");
			return;
		}
		
		Log.e("test", "test2");
		
		
		//載入捷運資料
		try{
			StationInfoRow stationInfoRow;
			for (int i=0; i<mrtInfos.length(); i++){
				stationInfoRow = new StationInfoRow(context);
				stationInfoRow.reloadView(mrtInfos.getJSONObject(i));
				stationContainer.addView(stationInfoRow);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//顯示更多button & 隱藏顯示捷運資訊動作
		if (mrtInfos.length()==1){
			showContainer.setVisibility(View.GONE);
		}else{
			showContainer.setVisibility(View.VISIBLE);
			showTextView.setText(Singleton.resources.getString(R.string.show_more));
			showOrHideStations(View.GONE);
			
			showContainer.setTag("HIDE");
			showContainer.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (((String)v.getTag()).equals("HIDE")){
						//目前是hide,要show資訊
						v.setTag("SHOW");
						showTextView.setText(Singleton.resources.getString(R.string.hide_info));
						showOrHideStations(View.VISIBLE);
					}else{
						//目前是show,要hide資訊
						v.setTag("HIDE");
						showTextView.setText(Singleton.resources.getString(R.string.show_more));
						showOrHideStations(View.GONE);
					}
				}
			});
		}
		
	}
	
	private void showOrHideStations(int visibility){
		for (int i=1; i<stationContainer.getChildCount(); i++){
			stationContainer.getChildAt(i).setVisibility(visibility);
		}
	}



}
