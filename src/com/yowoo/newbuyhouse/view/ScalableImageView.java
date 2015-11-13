package com.yowoo.newbuyhouse.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.thinkermobile.sinyi.R;


public class ScalableImageView extends RelativeLayout {

	public ProgressBar lineProgressBar;
	public TouchImageView imageView;

	public ScalableImageView(Context context) {
		super(context);
		initViews();
	}

	public ScalableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews();
	}

	public ScalableImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initViews();
	}

	private void initViews() {
		View.inflate(getContext(), R.layout.scalable_image, this);

		imageView = (TouchImageView) findViewById(R.id.imageView); 
		imageView.setMaxZoom(3.0f);
		lineProgressBar = (ProgressBar) findViewById(R.id.lineProgressBar);
	}

//	public void loadImageByPath(final String imagePath) {
//		final int rotation = BitmapHelper.getImageRotation(imagePath);
//
//		asyncProcessImage(imagePath, rotation);
//	}

//	private void asyncProcessImage(final String imageFilePath, final int rotation) {
//        final Handler handler = new Handler();
//
//		// get full screen image to display!
//		if(circleProgressBar!=null) {
//			circleProgressBar.setVisibility(View.VISIBLE);
//		}
//		
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					final Bitmap bitmap = BitmapHelper.getBitmap(imageFilePath, 2);
//
//					handler.post(new Runnable() {
//						public void run() {
//							imageView.setImageBitmap(bitmap);
//							circleProgressBar.setVisibility(View.GONE);
//						}
//					});
//				} catch (Exception e) {
//					Singleton.log("LOAD BITMAP EXCEPTION: "+e.toString());
//				}
//			}
//		}).start();
//	}

//	public void reset() {
//		imageView.reset();
//	}

	public void clearMemory() {
		// need to recycle imageView's Bitmap
		Drawable drawable = imageView.getDrawable();

		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			Bitmap bitmap = bitmapDrawable.getBitmap();

			if(bitmap!=null) { 
				bitmap.recycle();
				bitmap = null;
			}
		}
		
		imageView.setImageBitmap(null);
	}
}
