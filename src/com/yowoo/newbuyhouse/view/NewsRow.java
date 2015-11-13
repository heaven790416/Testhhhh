package com.yowoo.newbuyhouse.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.Singleton;
import com.yowoo.newbuyhouse.news.News;

public class NewsRow extends RelativeLayout{

	public ImageView thumbImageView;
	public TextView titleTextView;
	public TextView dateTextView;
	public RelativeLayout container;
	
	public NewsRow(Context context) {
		super(context);

		LayoutInflater.from(context).inflate(R.layout.news, this);

		thumbImageView = (ImageView) findViewById(R.id.thumbImageView);
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		dateTextView = (TextView) findViewById(R.id.dateTextView);
		container = (RelativeLayout) findViewById(R.id.container);
		
		thumbImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		
	}

	public NewsRow(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NewsRow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void reloadCell(News newsObject) {//TODO:
		

//		int imageViewTag = 0;
//		
//		try {
//			imageViewTag = (Integer) thumbImageView.getTag();
//		} catch (Exception e) {
//		}
//		
//		if(imageViewTag!=newsObject.getNewsId()) {
//			thumbImageView.setTag(newsObject.getNewsId());
//
//			//imageloader
//			if (!newsObject.getImgUrl().equals("")){
//				ImageLoader.getInstance().displayImage(
//						newsObject.getImgUrl(),
//						thumbImageView, 
//						Singleton.thumbnailDisplayImageOptions);
//			} else {
//				thumbImageView.setImageResource(R.drawable.ic_drawer);
//			}
//		}
		
		//TODO: 等api開好，請加上tag，避免重load圖片
		String imgUrl = newsObject.getImgSrc();
		if ((imgUrl == null) || (imgUrl.equals(""))|| (!imgUrl.contains(".jpg"))){
			thumbImageView.setVisibility(View.GONE);
		}else{
			ImageLoader.getInstance().displayImage(
				newsObject.getImgSrc(),
				thumbImageView, 
				Singleton.thumbnailDisplayImageOptions);
			thumbImageView.setVisibility(View.VISIBLE);
		}
		
		titleTextView.setText(newsObject.getTitle());
		
		String dateString = Singleton.news_DateFormatter.format(newsObject.getStartDate());
		String pmName = newsObject.getPmName();
		if (!pmName.equals("null")){
			dateString += " | "+pmName;
		}
		dateTextView.setText(dateString);
		
	}



}
