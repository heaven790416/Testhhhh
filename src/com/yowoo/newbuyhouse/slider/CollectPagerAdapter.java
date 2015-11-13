package com.yowoo.newbuyhouse.slider;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.yowoo.newbuyhouse.model.House;
import com.yowoo.newbuyhouse.model.HouseDetail;
import com.yowoo.newbuyhouse.track.TrackCollectActivity;
import com.yowoo.newbuyhouse.view.CollectRow;

public class CollectPagerAdapter extends RecyclingPagerAdapter {

    private Context       context;
    private List<HouseDetail> imageIdList;

    private int           size;
    private boolean       isInfiniteLoop;

    	public CollectPagerAdapter(Context context, List<HouseDetail> imageIdList) {
    	        this.context = context;
        this.imageIdList = imageIdList;
        this.size = ListUtils.getSize(imageIdList);
        isInfiniteLoop = false;
    }

    @Override
    public int getCount() {
        // Infinite loop
        return isInfiniteLoop ? Integer.MAX_VALUE : ListUtils.getSize(imageIdList);
    }

    /**
     * get really position
     * 
     * @param position
     * @return
     */
    private int getPosition(int position) {
        return isInfiniteLoop ? position % size : position;
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {

        if(view==null||view.getClass().equals(CollectRow.class)==false) {
			view = new CollectRow(context);
		}

        CollectRow collectRow = (CollectRow) view;
		
		int modePos = getPosition(position);
		final HouseDetail house = imageIdList.get(modePos);
		Log.e("test", "modePos:"+modePos+" url:"+house.imgDefault+" name:"+house.name);
		collectRow.reloadCell(house);
        
		collectRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.e("test", "collectRow: onClick");
				Intent intent = new Intent(context, TrackCollectActivity.class);
				context.startActivity(intent);
			}
		});
        
        return view;
    }


    /**
     * @return the isInfiniteLoop
     */
    public boolean isInfiniteLoop() {
        return isInfiniteLoop;
    }

    /**
     * @param isInfiniteLoop the isInfiniteLoop to set
     */
    public CollectPagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
        this.isInfiniteLoop = isInfiniteLoop;
        return this;
    }
}