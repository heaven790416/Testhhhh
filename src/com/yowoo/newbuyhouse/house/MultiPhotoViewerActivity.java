package com.yowoo.newbuyhouse.house;


import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.viewpagerindicator.CirclePageIndicator;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.BaseActivity;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.Singleton;
import com.yowoo.newbuyhouse.view.MyViewPager;
import com.yowoo.newbuyhouse.view.ScalableImageView;

public class MultiPhotoViewerActivity extends BaseActivity {

    private MyViewPager viewPager;
    //private TextView titleTextView;
    //private Button backButton;
    
    private ArrayList<String> photoPathArray;

    private DisplayImageOptions largeImageOptions;
    private int currentPosition = 0;
    Boolean mediaLoaded = false; //for control progressBar

    CirclePageIndicator mIndicator;
    TextView mTitleTextView;
    
    protected void onCreate(Bundle savedInstanceState) {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_photo_viewer);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        Bundle extra = getIntent().getExtras();

        if(extra.containsKey(BHConstants.EXTRA_INITIAL_IMAGE_POSTION)) {
            currentPosition = extra.getInt(BHConstants.EXTRA_INITIAL_IMAGE_POSTION);
        }
        
        photoPathArray = extra.getStringArrayList(BHConstants.EXTRA_MEDIA_FILE_PATH_ARRAY);

        //set toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.multi_viwer_bg_color));
        toolbar.setNavigationIcon(R.drawable.ic_back_selector);
        toolbar.setTitleTextColor(getResources().getColor(R.color.title_text_color));
        mTitleTextView = (TextView)toolbar.findViewById(R.id.mTitleTextView);
        mTitleTextView.setVisibility(View.VISIBLE);
        ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("");
		
        viewPager = (MyViewPager) findViewById(R.id.viewPager);
        //titleTextView = (TextView) findViewById(R.id.titleTextView);
        //backButton = (Button) findViewById(R.id.backButton);
        
        //get memory
        Runtime rt = Runtime.getRuntime();
        long maxMemory = rt.maxMemory();
        Log.e("test", "maxMemory: "+maxMemory);
        
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
        .cacheInMemory(true)
		.cacheOnDisc(true)
		.showImageOnFail(R.drawable.placeholder_bigimg);
        
        if (maxMemory > (1024*1024*BHConstants.IMAGE_LOADER_CONFIG_MEMORY_LIMIT)){
	        	//maxMemory較大的手機，使用較高color config
	        	largeImageOptions = builder.build();
        }else{
        		//maxMemory較小的手機，使用較低color config
        		largeImageOptions = builder.bitmapConfig(Bitmap.Config.RGB_565).build();
        }	
        
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(currentPosition, false);
            }
        }, 50L);

        reloadTitleBar();

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(1);
//        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
//            public void onPageSelected(int position) {
//                currentPosition = position;
//                reloadTitleBar();
//                Singleton.log("onPageSelected: positoin: "+position);
//            }
//
//            public void onPageScrolled(int currentPosition, float scrollPercentage, int arg2) {
//            		//Singleton.log("onPageScrolled: "+scrollPercentage);
//            }
//
//            public void onPageScrollStateChanged(int arg0) {
//
//            }
//        });
        
//        backButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});
        
        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(viewPager);

        //We set this on the indicator, NOT the pager
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            		currentPosition = position;
                reloadTitleBar();
                Singleton.log("onPageSelected: positoin: "+position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        
        
    }

   
    private void reloadTitleBar() {
    		mTitleTextView.setText((currentPosition+1) + "/" +  photoPathArray.size());
    }
    

    PagerAdapter pagerAdapter = new PagerAdapter() {
    	
    		public Object instantiateItem(View container, final int position) {
        		
    			Singleton.log("instantiateItem: position: "+position);
    			final ScalableImageView scalableImageView = new ScalableImageView(MultiPhotoViewerActivity.this);
    			final ProgressBar lineProgressBar = (ProgressBar) scalableImageView.findViewById(R.id.lineProgressBar);

    			scalableImageView.imageView.viewPager = viewPager; // this is important! let image view toggle viewPager
    			scalableImageView.lineProgressBar = lineProgressBar;
    			scalableImageView.lineProgressBar.setVisibility(View.GONE);

    			viewPager.addView(scalableImageView);
    			
    			//要可以顯示傳送成功或傳送失敗的圖片
    			//if send success: http://......
    			//if send fail:    file:///mnt/sdcard/....
            final String fileUrl = photoPathArray.get(position);
            Singleton.log("PhotoViewer: fileUrl: "+fileUrl);
            
            LayoutParams mediaLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //mediaLoaded = false;
            
            ImageLoader.getInstance().displayImage(fileUrl, scalableImageView.imageView, largeImageOptions, new SimpleImageLoadingListener() {
            		@Override
            		public void onLoadingStarted(String imageUri, View view) {
            			scalableImageView.lineProgressBar.setVisibility(View.VISIBLE);
            		}

            		@Override
            		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            			scalableImageView.lineProgressBar.setVisibility(View.GONE);
            			Singleton.log("load image complete! position: "+position);
            		}

            		@Override
            		public void onLoadingCancelled(String imageUri, View view) {
            			scalableImageView.lineProgressBar.setVisibility(View.GONE);
            		}

            		@Override
            		public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            			scalableImageView.lineProgressBar.setVisibility(View.GONE);
            			//showToast(failReason.getType().toString());
            		}
            }, new ImageLoadingProgressListener() {
            		@Override
            		public void onProgressUpdate(String imageUri, View view, int current, int total) {
            			//Singleton.log("load progress: "+String.valueOf(current*100/total));
            			scalableImageView.lineProgressBar.setProgress(current*100/total);
            		}
            });
            
        		return scalableImageView;
        	
        };

        public void destroyItem(View container, int position, Object childView) {
//        	  ScalableImageView scalableImageView = (ScalableImageView) childView;
//            scalableImageView.clearMemory();
        		Singleton.log("destroyItem: position:"+position);
            ((ViewPager) container).removeView((View) childView);
        }

        public boolean isViewFromObject(View container, Object childView) {
            return container == ((View) childView);
        }

        public Parcelable saveState() {
            return null;
        }

        public int getCount() {
            return photoPathArray.size();
        }
    };
  
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home){
			finish();
		}
		
		return super.onOptionsItemSelected(item);
	}
    
    
//    View.OnClickListener saveListener = new OnClickListener(){
//		@Override
//		public void onClick(View v) {
//			
//			//若是local filePath(上傳不成功的圖片)
//			String currentFileUrl = photoPathArray.get(currentPosition);
//			if (!Singleton.isHttpUrl(currentFileUrl)){
//				String localfilePath = Singleton.getExternalMediaFolderPath()+Singleton.getFileNameFromFilePath(currentFileUrl);
//	            
//				if (new File(localfilePath).exists()){
//					//檔案已存在於app資料夾
//	            		showImageToast(getString(R.string.prompt_has_saved_in_folder),R.drawable.happy);
//	            		return;
//	            }else{
//	            		//檔案不存在，無法儲存
//	            		showImageToast(getString(R.string.prompt_file_not_exist_cannot_save),R.drawable.happy);
//	            		return;
//	            }
//			}
//			
//			//是Url, 開始下載
//			showProgressDialog();
//			
//			Singleton.startDownload(currentFileUrl, new FileDownloadInterface(){
//
//				@Override
//				public void downloadProgressCallback(String filename, double progress) {
//				}
//
//				@Override
//				public void downloadCompletedCallback(String filename) {
//					hideProgressDialog();
//					showImageToast(getString(R.string.prompt_has_saved_in_folder),R.drawable.happy);
//					Singleton.log("load image complete!");
//				}
//
//				@Override
//				public void downloadFailedCallback(String filename, boolean fileNotExist) {
//					hideProgressDialog();
//					showToast("fileExist:"+fileNotExist);
//				}
//			});
//		}
//	};
    
}
