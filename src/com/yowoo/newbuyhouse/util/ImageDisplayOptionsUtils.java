package com.yowoo.newbuyhouse.util;

import android.graphics.Bitmap;
import android.util.Log;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.thinkermobile.sinyi.R;

public class ImageDisplayOptionsUtils {

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

	private static Boolean hasChecked = false;
	private static Boolean hasLargeMem = false;
	
	private static void checkMem(int memoryLimit){
		//check mem & image loader config
		Runtime rt = Runtime.getRuntime();
		long maxMemory = rt.maxMemory();
		if (maxMemory > (1024*1024*memoryLimit)){
			hasLargeMem = true;
		}
		hasChecked = true;
	}
	
	public static DisplayImageOptions getProperOptionsByMemory(int memoryLimit){
		if (!hasChecked){
			checkMem(memoryLimit);
		}
		
		if (hasLargeMem){
			return bigImageDisplayOptions;
		}else{
			return mediumImageDisplayOptions;
		}
	}
	
}
