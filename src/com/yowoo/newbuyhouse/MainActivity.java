package com.yowoo.newbuyhouse;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.NavigationDrawerFragment.NavigationDrawerCallbacks;
import com.yowoo.newbuyhouse.fragment.MainHouseFragment;
import com.yowoo.newbuyhouse.fragment.MainPriceFragment;
import com.yowoo.newbuyhouse.fragment.MainStoreFragment;
import com.yowoo.newbuyhouse.house.HouseFilterActivity;
import com.yowoo.newbuyhouse.login.LoginInfo;
import com.yowoo.newbuyhouse.network.SinyiService;
import com.yowoo.newbuyhouse.network.SinyiService.TokenCallback;
import com.yowoo.newbuyhouse.network.TrackService;
import com.yowoo.newbuyhouse.network.TrackService.TrackCallback;
import com.yowoo.newbuyhouse.price.PriceFilterActivity;
import com.yowoo.newbuyhouse.store.StoreFilterActivity;


public class MainActivity extends BaseActivity implements NavigationDrawerCallbacks{

	
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	//SetUp Fragments
//	final String[] fragments ={
//            "com.yowoo.newbuyhouse.fragment.MainHouseFragment",
//            "com.yowoo.newbuyhouse.fragment.MainStoreFragment"
//        };
	
	//TODO:完整fragment
	final String[] fragments ={
            "com.yowoo.newbuyhouse.fragment.MainHouseFragment",
            "com.yowoo.newbuyhouse.fragment.MainPriceFragment",
            "com.yowoo.newbuyhouse.fragment.MainTrackFragment",
            "com.yowoo.newbuyhouse.fragment.MainNewsFragment",
            "com.yowoo.newbuyhouse.fragment.MainStoreFragment",
            "com.yowoo.newbuyhouse.fragment.MainChatFragment",
            "com.yowoo.newbuyhouse.fragment.MainLeaveMessageFragment",
            "com.yowoo.newbuyhouse.fragment.MainLeaveMessageFragment_sell"
        };
	
	//Current Fragment
	private Fragment currentFragment;
	private int currentPos = 0;
	private HashMap<Integer, Fragment> fragmentHashMap;
	Toolbar toolbar;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("test", "MainActivity: onCreate");
        //Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        
        //使用Toolbar元件取代過往的ActionBar元件
        //Check here: http://blog.xamarin.com/android-tips-hello-toolbar-goodbye-action-bar/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_selector);
        toolbar.setTitleTextColor(Color.GRAY);
        ActionBar actionBar = getSupportActionBar();

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

    }


    @Override
	public void onNavigationDrawerItemSelected(int position) {

		// update the main content by replacing fragments
    		switchFragments(position);
	}
    
    public void onSectionAttached(int number) {
		switch (number) {

		case 0:
			mTitle = getString(R.string.drawer_section_house);
			break;
		case 1:
			mTitle = getString(R.string.drawer_section_price);
			break;
		case 2:
			mTitle = getString(R.string.drawer_section_track);
			break;
		case 3:
			mTitle = getString(R.string.drawer_section_news);
			break;
		case 4:
			mTitle = getString(R.string.drawer_section_store);
			break;
		case 5:
			mTitle = getString(R.string.drawer_section_chat);
			break;
		case 6:
			mTitle = getString(R.string.drawer_section_leave_message);
			break;
		case 7:
			mTitle = getString(R.string.drawer_section_leave_message_sell);
			break;
		}
		
	}
    
    private void switchFragments(int position){
    		Fragment fragment = Fragment.instantiate(MainActivity.this, fragments[position]);
		Bundle args = new Bundle();
		args.putInt(Constants.FRAGMENT_ARG_POSITION, position);
		fragment.setArguments(args);
		
		FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.container, fragment);
        tx.commit();
        
        //set Current Fragment
        this.currentFragment = fragment;
        this.currentPos = position;
    }

	public void restoreActionBar() {
		//set center title or image
		if (toolbar!=null){
			TextView mTitleTextView = (TextView)toolbar.findViewById(R.id.mTitleTextView);
	        ImageView mLogoImageView = (ImageView)toolbar.findViewById(R.id.mLogoImageView);
			
	        if (this.currentPos==BHConstants.MENU_HOUSE_POSITION){
	        		//買屋搜尋（顯示logo圖示）
	        		mTitleTextView.setVisibility(View.GONE);
	        		mLogoImageView.setVisibility(View.VISIBLE);
	        }else{
	        		//其他（顯示title文字）
	        		mLogoImageView.setVisibility(View.GONE);
	        		mTitleTextView.setText(mTitle);
		        mTitleTextView.setVisibility(View.VISIBLE);
	        }
	        ActionBar actionBar = getSupportActionBar();
			actionBar.setTitle("");
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this
			// screen
			// if the drawer is not showing. Otherwise, let the
			// drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			
			//設定右上角icom是否顯示
			if ((currentPos==BHConstants.MENU_HOUSE_POSITION)||
				(currentPos==BHConstants.MENU_STORE_POSITION)||
				(currentPos==BHConstants.MENU_PRICE_POSTION)){
				menu.getItem(0).setVisible(true);
			}else{
				menu.getItem(0).setVisible(false);
			}
			
			//設定actionbar顯示圖示或字
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		if (id == R.id.action_filter) {
			if (currentFragment!=null){
				if (currentFragment instanceof MainHouseFragment){ 
					Intent intent = new Intent(MainActivity.this, HouseFilterActivity.class);
					slideInToStartActivity(intent);
					//startActivity(intent);
				}else if (currentFragment instanceof MainStoreFragment){
					Intent intent = new Intent(MainActivity.this, StoreFilterActivity.class);
					startActivity(intent);
				}else if (currentFragment instanceof MainPriceFragment){
					Intent intent = new Intent(MainActivity.this, PriceFilterActivity.class);
					slideInToStartActivity(intent);
					//startActivity(intent);
				}
			}
			return true;
		}
		
		//滑開drawer時，先將keyboard收起
		if (item.getItemId() == android.R.id.home){
			//this.hideKeyboard();
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/* Broadcast Receiver */
	@Override
	public void onResume(){
		super.onResume();
		
		//register broadcast receiver
		//LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
		//broadcastManager.registerReceiver(uploadImageBroadcastReceiver, new IntentFilter(UserConstants.BROADCAST_USER_UPLOAD_IMAGE));

		//check if need to get gcm token
		checkGCMTokenAndRegToSinyi();
	}
	
	@Override
	public void onPause(){
		super.onPause();

		//LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
		//broadcastManager.unregisterReceiver(uploadImageBroadcastReceiver);
	}
	
	private void checkGCMTokenAndRegToSinyi(){
		
		if (LoginInfo.getInstance().isLogined()){
			String gcmToken = Singleton.preferences.getString(BHConstants.GCM_TOKEN, "");
			if (gcmToken.equals("")){
				SinyiService.getGCMToken(getApplicationContext(), BHConstants.GCM_APP_ID, new TokenCallback(){
					@Override
					public void onResult(boolean success, final String token) {
						if (success){
							Log.e(TAG, "token:"+token);
							
							//sent to sinyi
							TrackService.loginAppGCMToken(token, new TrackCallback(){
								@Override
								public void onResult(boolean success, String debugMessage) {
									//成功記錄到此memberID後才存gcmToken到pref
									Log.e(TAG, "loginAppGCMToken: "+success+" msg:"+debugMessage);
									if (success){
										Singleton.preferenceEditor.putString(BHConstants.GCM_TOKEN, token)
										.commit();
									}
								}
							});
						}
					}
				});
			}else{
				Log.e(TAG, "exist token:"+gcmToken);
			}
		}
		
	}
	
	/* For Back Control */
	Boolean hasPressBack = false;
	Timer timer = new Timer(true);
	
	public class BackTimerTask extends TimerTask{
		public void run(){
			hasPressBack = false;
		}
	};
	
	@Override
	public void onBackPressed(){
		
		//如果Menu開啟，關閉Menu
		if (this.mNavigationDrawerFragment!=null){
			if (mNavigationDrawerFragment.isDrawerOpen()){
				mNavigationDrawerFragment.triggerDrawer();
				return;
			}
		}
		
		//如果還未點過back, 提示
		if (!hasPressBack){
			hasPressBack = true;
			Toast.makeText(this, getString(R.string.back_again_to_exit), Toast.LENGTH_SHORT).show();
			//設定時間點刪去back記錄
			timer.schedule(new BackTimerTask(), 2500);
			return;
		}
		
		//已點過back, 退出
		timer.cancel();
		super.onBackPressed();
	}
    
	
	
}
