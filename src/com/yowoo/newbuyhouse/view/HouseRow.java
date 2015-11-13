package com.yowoo.newbuyhouse.view;

import java.util.Locale;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.model.House;

public class HouseRow extends RelativeLayout{

	public interface HouseRowCallback {
		public void onClickHouse(String houseNO);
		public void onClickPrevArrow();
		public void onClickNextArrow();
	}
	
	private HouseRowCallback houseRowCallback;
	
	public ImageView thumbImageView;
	public TextView nameTextView;
	public TextView priceFirstTextView, priceTextView, addressTextView, typeTextView;
	public Button discountButton;
	public RelativeLayout prevContainer, nextContainer;
	
	String houseNO = "";
	
	public static DisplayImageOptions houseThumbDisplayImageOptions = new DisplayImageOptions.Builder()
	 .cacheInMemory(true)
	 .cacheOnDisc(true)
	 .showImageOnLoading(R.drawable.placeholder_photo).build();
	
	public HouseRow(Context context) {
		super(context);
		init(context);
	}

	public HouseRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public HouseRow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	

	protected void init(Context context) {
		View root = LayoutInflater.from(context).inflate(R.layout.house, this);

		root.setBackgroundResource(R.drawable.bg_house_bottom_bar);
		//root.setBackgroundColor(getResources().getColor(R.color.white_color));
		
		thumbImageView = (ImageView) findViewById(R.id.thumbImageView);
		nameTextView = (TextView) findViewById(R.id.nameTextView);
		priceFirstTextView = (TextView) findViewById(R.id.priceFirstTextView);
		priceTextView = (TextView) findViewById(R.id.priceTextView);
		discountButton = (Button) findViewById(R.id.discountButton);
		addressTextView = (TextView) findViewById(R.id.addressTextView);
		typeTextView = (TextView) findViewById(R.id.typeTextView);
		
		prevContainer = (RelativeLayout) findViewById(R.id.prevContainer);
		nextContainer = (RelativeLayout) findViewById(R.id.nextContainer);
		
		thumbImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		priceFirstTextView.setPaintFlags(priceFirstTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		
		root.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (houseRowCallback!=null){
					houseRowCallback.onClickHouse(houseNO);
				}
			}
		});
		
		prevContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("test", "click prev!");
				if (houseRowCallback!=null){
					houseRowCallback.onClickPrevArrow();
				}
			}
		});
		
		nextContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("test", "click next!");
				if (houseRowCallback!=null){
					houseRowCallback.onClickNextArrow();
				}
			}
		});
		
    }
	
	public void reloadCell(House houseObject) {
		
		this.houseNO = houseObject.NO;
		
		nameTextView.setText(houseObject.name);
		
		if (houseObject.priceFirst>0){
			String formattedPriceFirst = String.format(Locale.US, "%,d", houseObject.priceFirst);
			this.priceFirstTextView.setText(formattedPriceFirst+"萬");
			this.priceFirstTextView.setVisibility(View.VISIBLE);
		}else{
			this.priceFirstTextView.setVisibility(View.GONE);
		}
		
		String formattedPrice = String.format(Locale.US, "%,d", houseObject.price);
		this.priceTextView.setText(formattedPrice+"萬");
		
		if (houseObject.discount>0){
			this.discountButton.setText("-"+houseObject.discount+"%");
			this.discountButton.setVisibility(View.VISIBLE);
		}else{
			this.discountButton.setText("");
			this.discountButton.setVisibility(View.GONE);
		}
		
		this.addressTextView.setText(houseObject.address);
		this.typeTextView.setText(houseObject.type);

		//load image
		
		String imageViewTag = "";
		
		try {
			imageViewTag = (String) thumbImageView.getTag();
		} catch (Exception e) {
		}
		
		if(imageViewTag!=houseObject.name) {
			thumbImageView.setTag(houseObject.name);

			//imageloader
			if (!houseObject.imgDefault.equals("")){
				ImageLoader.getInstance().displayImage(
						houseObject.imgDefault,
						thumbImageView, 
						houseThumbDisplayImageOptions);
			} else {
				thumbImageView.setImageResource(R.drawable.placeholder_photo);
			}
		}
		
	}

	public void setHouseRowCallback(HouseRowCallback houseRowCallback){
		this.houseRowCallback = houseRowCallback;
	}
	
	public void reloadArrow(int position, int houseCount){
		if (position==0){
			this.prevContainer.setVisibility(View.GONE);
		}else{
			this.prevContainer.setVisibility(View.VISIBLE);
		}
		
		if (position==houseCount-1){
			this.nextContainer.setVisibility(View.GONE);
		}else{
			this.nextContainer.setVisibility(View.VISIBLE);
		}
	}
	


}
