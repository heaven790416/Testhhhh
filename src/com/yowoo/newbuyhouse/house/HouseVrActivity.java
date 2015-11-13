package com.yowoo.newbuyhouse.house;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.BaseActivity;
import com.thinkermobile.sinyi.R;

public class HouseVrActivity extends BaseActivity{
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_house_vr);
		
		//set toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_selector);
        toolbar.setTitleTextColor(Color.GRAY);
//        ActionBar actionBar = getSupportActionBar();
//		actionBar.setTitle(getString(R.string.house_detail));
		
		//set views
		webView = (WebView) this.findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		
		webView.setWebViewClient(new WebViewClient() {
		    public boolean shouldOverrideUrlLoading(WebView view, String url){
		        view.loadUrl(url);
		        return false; 
		   }
		});
		
		//GET EXTRA
		try{
			String houseName = getIntent().getExtras().getString(BHConstants.EXTRA_HOUSE_NAME);
			String houseNO = getIntent().getExtras().getString(BHConstants.EXTRA_HOUSE_NO);
			String vrNO = getIntent().getExtras().getString(BHConstants.EXTRA_HOUSE_VR_NO);//物件實境編號or社區公設實境編號
			ActionBar actionBar = getSupportActionBar();
			actionBar.setTitle(houseName+"("+houseNO+")");
			
			//3D看屋
			//String url = "http://3dvr.sinyi.com.tw/sinyi-"+vrNO+".html";
			
			//2D看屋
			String url = "http://vr.sinyi.com.tw/"+vrNO+"/index.html";
			webView.loadUrl(url);
			
			Log.e("test", "url: "+url);
		}catch (Exception e){
		}

	}
	
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
	

}
