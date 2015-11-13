package com.yowoo.newbuyhouse.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.model.Price;
import com.yowoo.newbuyhouse.model.Store;

public class StoreRow extends RelativeLayout{

	public interface StoreRowCallback {
		public void onClickCall(String tel);
	}
	
	private StoreRowCallback storeRowCallback;
	
	public ImageView thumbImageView;
	public TextView nameTextView, telTextView, addressTextView;
	RelativeLayout callContainer;
	
	public static DisplayImageOptions houseThumbDisplayImageOptions = new DisplayImageOptions.Builder()
	 .cacheInMemory(true)
	 .cacheOnDisc(true)
	 .showImageOnLoading(R.drawable.placeholder_photo).build();
	
	public StoreRow(Context context) {
		super(context);
		init(context);
	}

	public StoreRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public StoreRow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	

	protected void init(Context context) {
		View root = LayoutInflater.from(context).inflate(R.layout.store, this);

		root.setBackgroundResource(R.drawable.bg_house_bottom_bar);
		
		thumbImageView = (ImageView) findViewById(R.id.thumbImageView);
		nameTextView = (TextView) findViewById(R.id.nameTextView);
		telTextView = (TextView) findViewById(R.id.telTextView);
		addressTextView = (TextView) findViewById(R.id.addressTextView);
		callContainer = (RelativeLayout) findViewById(R.id.callContainer);
		
		thumbImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		
    }
	
	//for 預覽房屋物件viewPager
	public void reloadCell(final Store storeObject) {
		
		nameTextView.setText(storeObject.name);
		
		this.telTextView.setText(storeObject.tel1);
		this.addressTextView.setText(storeObject.address);

		//load image
		String imageViewTag = "";
		
		try {
			imageViewTag = (String) thumbImageView.getTag();
		} catch (Exception e) {
		}
		
		if(imageViewTag!=storeObject.name) {
			thumbImageView.setTag(storeObject.name);

			//imageloader
			if (!storeObject.img.equals("")){
				ImageLoader.getInstance().displayImage(
						storeObject.img,
						thumbImageView, 
						houseThumbDisplayImageOptions);
			} else {
				thumbImageView.setImageResource(R.drawable.placeholder_photo);
			}
		}
		
		callContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (storeRowCallback!=null){
					storeRowCallback.onClickCall(storeObject.tel1);
				}
			}
		});
		
	}
	
	public void setStoreRowCallback(StoreRowCallback storeRowCallback){
		this.storeRowCallback = storeRowCallback;
	}
	
	//TODO: test for price , need delete it
	public void reloadForPriceTest(Price price){
		nameTextView.setText(price.getStringData(BHConstants.JSON_KEY_ADDRESS));
		
	}

}
