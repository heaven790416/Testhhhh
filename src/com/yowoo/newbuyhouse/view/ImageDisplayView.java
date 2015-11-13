package com.yowoo.newbuyhouse.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yowoo.newbuyhouse.BHConstants;
import com.thinkermobile.sinyi.R;

public class ImageDisplayView extends RelativeLayout{

	public interface ImageDisplayViewListener {
		public void onClickImage(int position);
	}
	
	private ImageDisplayViewListener listener;
	
	ViewPager imageViewPager;
	ArrayList<String> images = new ArrayList<String>();
	Button prevButton, nextButton;
	
	Context context;
	Boolean hasLargeMem = false;
	
	//maxMemory較小的手機，使用較低color config
	public static DisplayImageOptions mediumImageDisplayOptions = new DisplayImageOptions.Builder()
	.cacheInMemory(true)
	.cacheOnDisc(true)
	.bitmapConfig(Bitmap.Config.RGB_565)
	.showImageOnLoading(R.drawable.placeholder_bigimg).build();
	
	//maxMemory較大的手機，使用較高color config
	public static DisplayImageOptions bigImageDisplayOptions = new DisplayImageOptions.Builder()
	.cacheInMemory(true)
	.cacheOnDisc(true)
	.showImageOnLoading(R.drawable.placeholder_bigimg).build();
	
	public ImageDisplayView(Context context) {
		super(context);
		init(context);
	}

	public ImageDisplayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ImageDisplayView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	

	protected void init(Context context) {
		this.context = context;
		
		View root = LayoutInflater.from(context).inflate(R.layout.image_display_view, this);

		this.imageViewPager = (ViewPager) this.findViewById(R.id.imageViewPager);
		this.prevButton = (Button) this.findViewById(R.id.prevButton);
		this.nextButton = (Button) this.findViewById(R.id.nextButton);
		
		imageViewPager.setAdapter(imagePagerAdapter);
		imageViewPager.setOffscreenPageLimit(3);
		
		imageViewPager.setOnPageChangeListener(new OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				Log.e("test", "position: "+position+" imageSize:"+images.size());
	            //set prev & next button
	            reloadNavigationButton(position);
	            
	            Log.e("test", "prev:"+prevButton.getVisibility());
	            Log.e("test", "next:"+nextButton.getVisibility());
			}
		});
		
		//check mem & image loader config
		Runtime rt = Runtime.getRuntime();
        long maxMemory = rt.maxMemory();
        if (maxMemory > (1024*1024*BHConstants.IMAGE_LOADER_CONFIG_MEMORY_LIMIT)){
        		this.hasLargeMem = true;
        }
		
		prevButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int currentIndex = imageViewPager.getCurrentItem();
				if (currentIndex>0){
					imageViewPager.setCurrentItem(currentIndex-1);
				}
			}
		});
		
		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int currentIndex = imageViewPager.getCurrentItem();
				if (currentIndex<(images.size()-1)){
					imageViewPager.setCurrentItem(currentIndex+1);
				}
			}
		});
		
		
    }
	
	public void reloadImageViews(ArrayList<String> images){
		this.images = images;
		imagePagerAdapter.notifyDataSetChanged();
		
		if (images.size()==0){
			prevButton.setVisibility(View.GONE);
			nextButton.setVisibility(View.GONE);
		}else{
			imageViewPager.setCurrentItem(0);
			this.reloadNavigationButton(0);
		}
	}
	
	
	/* House Adapter */
    PagerAdapter imagePagerAdapter = new PagerAdapter() {

        public Object instantiateItem(View container, int position) {
            final ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            
            //set image
            imageViewPager.addView(imageView);
            final String imgUrl = images.get(position);
            
            if (hasLargeMem){
                	ImageLoader.getInstance().displayImage(
	            			imgUrl,
	            			imageView,
	            			bigImageDisplayOptions);
            }else{
                	ImageLoader.getInstance().displayImage(
	            			imgUrl,
	            			imageView,
	            			mediumImageDisplayOptions);
            }
            
            //set listener
            imageView.setOnClickListener(new View.OnClickListener() {
            		@Override
            		public void onClick(View v) {
            			Log.e("test", "test click!");
            			if (listener!=null){
            				listener.onClickImage(imageViewPager.getCurrentItem());
            			}
            		}	
            });
            
            return imageView;
        };

        public void destroyItem(View container, int position, Object childView) {
            ((ViewPager) container).removeView((View) childView);
        }

        public boolean isViewFromObject(View container, Object childView) {
            return container == ((View) childView);
        }

//        public Parcelable saveState() {
//            return null;
//        }

        public int getCount() {
            return images.size();
        }
        
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    };
    
    private void reloadNavigationButton(int position){
	    	if (position==0){
	    		prevButton.setVisibility(View.GONE);
	    	}else{
	    		prevButton.setVisibility(View.VISIBLE);
	    	}
	
	    	if (position==images.size()-1){
	    		nextButton.setVisibility(View.GONE);
	    	}else{
	    		nextButton.setVisibility(View.VISIBLE);
	    	}
    }
	
	public void setListener(ImageDisplayViewListener listener){
		this.listener = listener;
	}
	


}
