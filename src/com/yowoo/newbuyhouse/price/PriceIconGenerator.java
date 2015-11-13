package com.yowoo.newbuyhouse.price;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thinkermobile.sinyi.R;


public class PriceIconGenerator {
	private final Context mContext;

    private ViewGroup mContainer;
    private TextView countTextView;
    private TextView unitPriceTextView;
    private ImageView imageView;
    
    
    /**
     * Creates a new IconGenerator with the default style.
     */
    public PriceIconGenerator(Context context) {
        mContext = context;
        mContainer = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.price_marker, null);
        countTextView = (TextView) mContainer.findViewById(R.id.countTextView);
        unitPriceTextView = (TextView) mContainer.findViewById(R.id.unitPriceTextView);
        imageView = (ImageView) mContainer.findViewById(R.id.imageView);
    }
    
    /**
     * Sets the text content, then creates an icon with the current style.
     *
     * @param text the text content to display inside the icon.
     */
    public Bitmap makeIcon(String count, String unitPrice) {
        if (countTextView != null) {
        		countTextView.setText(count);
        }
        
        if (unitPriceTextView != null){
        		unitPriceTextView.setText(unitPrice);
        }

        return makeIcon();
    }

    
    /* Generate bigmap from layout */
	// Convert a view to bitmap
	public Bitmap makeIcon(){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		mContainer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
		mContainer.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
		mContainer.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
		mContainer.buildDrawingCache();
		Bitmap bitmap = Bitmap.createBitmap(mContainer.getMeasuredWidth(), mContainer.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmap);
		mContainer.draw(canvas);

		return bitmap;
	}
	
	public void setBackgroundDrawable(int resId){
		imageView.setBackgroundResource(resId);
	}
	
	public void setTextColor(int resId){
		countTextView.setTextColor(resId);
	}

}
