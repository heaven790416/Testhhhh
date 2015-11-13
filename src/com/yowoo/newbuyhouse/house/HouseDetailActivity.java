package com.yowoo.newbuyhouse.house;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.BaseActivity;
import com.yowoo.newbuyhouse.login.LoginInfo;
import com.yowoo.newbuyhouse.model.HouseDetail;
import com.yowoo.newbuyhouse.network.HouseService;
import com.yowoo.newbuyhouse.network.HouseService.SingleDetailHouseCallback;
import com.yowoo.newbuyhouse.network.LoginService;
import com.yowoo.newbuyhouse.network.LoginService.MemberCallback;
import com.yowoo.newbuyhouse.network.TrackService;
import com.yowoo.newbuyhouse.network.TrackService.TrackCallback;
import com.yowoo.newbuyhouse.view.ImageDisplayView;
import com.yowoo.newbuyhouse.view.ImageDisplayView.ImageDisplayViewListener;
import com.yowoo.newbuyhouse.view.InfoRow;
import com.yowoo.newbuyhouse.view.MRTInfoRow;


public class HouseDetailActivity extends BaseActivity{
	
	String houseNO = "";
	HouseDetail house = new HouseDetail();
	ArrayList<String> houseImages = new ArrayList<String>();
	
	//多張大圖
	ImageDisplayView imageDisplayView;
	//虛擬實境
	Button vrInteractButton, vrPublicButton;
	//簡介
	TextView nameTextView, priceFirstTextView, priceTextView;
	Button discountButton, callButton;
	//Button chatButton;
	//格局圖
	ImageView layoutImageView;	
	//物件描述
	TextView descriptionTextView;
	//基本資料
	InfoRow communityInfoRow, addressInfoRow, layoutInfoRow, areaBuildingInfoRow,
		areaLandInfoRow, areaPublicInfoRow, ageInfoRow,
		floorInfoRow, familyInfoRow, faceInfoRow,
		typeInfoRow, securityInfoRow, monthlyFeeInfoRow,
		sideDarkInfoRow, buildingStructureInfoRow, wallStructureInfoRow,
		parkingInfoRow;
	LinearLayout pingDetailContainer;
	//生活機能
	InfoRow primarySchoolInfoRow, juniorSchoolInfoRow, marketInfoRow,
		gardenInfoRow;
	MRTInfoRow mrtInfoRow;
	//物件地圖
	ImageView mapImageView;
	//業務聯絡
	InfoRow storeInfoRow, storeTelInfoRow, storeAddressInfoRow;
	//bottom功能
	Button favButton, sendSalesmanButton, reservationButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_house_detail);
		
		//set toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_selector);
        toolbar.setTitleTextColor(Color.GRAY);
        TextView mTitleTextView = (TextView)toolbar.findViewById(R.id.mTitleTextView);
        mTitleTextView.setText(getString(R.string.house_detail));
        mTitleTextView.setVisibility(View.VISIBLE);
        ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("");
		
		//set views
		setViews();
		
		//GET EXTRA
		try{
			houseNO = getIntent().getExtras().getString(BHConstants.EXTRA_HOUSE_NO);
		}catch (Exception e){
			houseNO = "";
		}
		
		fetchData(houseNO);
		
		setListener();
		
		//save browse record to local
		LoginInfo.getInstance().addVisitHouse(true, houseNO);
		
	}
	
	private void fetchData(String houseNO){
		if (houseNO.equals("")) return;
		
		HouseService.getSingleDetailHouse(houseNO, new SingleDetailHouseCallback(){

			@Override
			public void onResult(boolean success, HouseDetail houseDetail) {
				if (success){
					Log.e("test", "getDetailHouse success! "+houseDetail.name);
					house = houseDetail;
					reloadViews(houseDetail);
				}else{
					Log.e("test", "getDetailHouse fail!");
					showToast(R.string.network_not_stable);
				}
			}
			
		});
	}
	
	private void reloadViews(HouseDetail house){
		//大圖
		this.houseImages = house.bigImg;
		this.imageDisplayView.reloadImageViews(houseImages);
		
		//虛擬實境
		if (!house.vr.equals("1")){
			this.vrInteractButton.getBackground().setAlpha(128);//50% opacity
			this.vrInteractButton.setTextColor(getResources().getColor(R.color.detail_disable_text_color));
			this.vrInteractButton.setEnabled(false);
		}
		
		if (house.vrCom.equals("")){
			this.vrPublicButton.getBackground().setAlpha(128);//50% opacity
			this.vrPublicButton.setTextColor(getResources().getColor(R.color.detail_disable_text_color));
			this.vrPublicButton.setEnabled(false);
		}
		
		//簡介
		this.nameTextView.setText(house.name+"("+house.NO+")");
		if (house.priceFirst>0){
			String formattedPriceFirst = String.format(Locale.US, "%,d", house.priceFirst);
			this.priceFirstTextView.setText(formattedPriceFirst+"萬");
			this.priceFirstTextView.setVisibility(View.VISIBLE);
		}else{
			this.priceFirstTextView.setVisibility(View.GONE);
		}
		
		String formattedPrice = String.format(Locale.US, "%,d", house.price);
		this.priceTextView.setText(formattedPrice+"萬");
		
		if (house.discount>0){
			this.discountButton.setText("-"+house.discount+"%");
			this.discountButton.setVisibility(View.VISIBLE);
		}else{
			this.discountButton.setText("");
			this.discountButton.setVisibility(View.GONE);
		}
		
		//格局圖
		ImageLoader.getInstance().displayImage(
				house.layoutImg,
				layoutImageView, 
				layoutDisplayImageOptions);
		
		//物件描述
		this.descriptionTextView.setText(house.getDescriptionText());
		
		//基本資料
		if (house.community.equals("")){
			this.communityInfoRow.setVisibility(View.GONE);
		}else{
			this.communityInfoRow.setDetailText(house.community);
			this.communityInfoRow.setVisibility(View.VISIBLE);
		}
		this.addressInfoRow.setDetailText(house.address);
		this.layoutInfoRow.setDetailText(house.layout);
		this.areaBuildingInfoRow.setDetailText(String.valueOf(house.areaBuilding)+getString(R.string.unit_pyeong));
		this.areaLandInfoRow.setDetailText(String.valueOf(house.areaLand)+getString(R.string.unit_pyeong));
		this.areaPublicInfoRow.setDetailText(String.valueOf(house.areaPublic)+getString(R.string.unit_pyeong));
		this.ageInfoRow.setDetailText(String.valueOf(house.age)+getString(R.string.unit_year));
		this.floorInfoRow.setDetailText(house.floor+getString(R.string.unit_floor));
		this.familyInfoRow.setDetailText(house.getFamilyLiftText());
		this.faceInfoRow.setDetailText(house.getFaceText());
		this.typeInfoRow.setDetailText(house.type);
		this.securityInfoRow.setDetailText(house.security);
		String formattedMonthlyFee = String.format(Locale.US, "%,d", house.monthlyFee);
		this.monthlyFeeInfoRow.setDetailText(formattedMonthlyFee+getString(R.string.unit_dollar));
		this.sideDarkInfoRow.setDetailText(house.getSideDarkText());
		this.buildingStructureInfoRow.setDetailText(house.buildingStructure);
		this.wallStructureInfoRow.setDetailText(house.wallStructure);
		this.parkingInfoRow.setDetailText(house.parking);
		
		//坪數細節
		try {
			if (house.pingDetail.length()>0){
				InfoRow tempInfoRow;
				for (int i=0; i<house.pingDetail.length();i++){
					//濾掉「公共設施」坪數細節
					String pingTypeName = house.pingDetail.getJSONObject(i).getString(BHConstants.JSON_KEY_NAME);
					if (pingTypeName.equals(getString(R.string.area_public))){
						continue;
					}
					
					//其他坪數細節
					tempInfoRow = new InfoRow(HouseDetailActivity.this);
					tempInfoRow.titleTextView.setVisibility(View.GONE);
					String detail = house.getPingDetailText(i);
					tempInfoRow.setDetailText(detail);
					tempInfoRow.container.setBackgroundColor(getResources().getColor(R.color.white_color));
					this.pingDetailContainer.addView(tempInfoRow);
				}
				areaLandInfoRow.container.setBackgroundColor(getResources().getColor(R.color.white_color));

				//最後一個坪數細節加入底線
				int count  = pingDetailContainer.getChildCount();
				if (count>0){
					tempInfoRow = (InfoRow) pingDetailContainer.getChildAt(count-1);
					tempInfoRow.container.setBackgroundResource(R.drawable.bg_filter_item);
				}
			}
		}catch(Exception e){}
		
		//生活機能
		this.primarySchoolInfoRow.setDetailText(house.primarySchool);
		this.juniorSchoolInfoRow.setDetailText(house.juniorSchool);
		this.marketInfoRow.setDetailText(house.market);
		this.gardenInfoRow.setDetailText(house.garden);
		this.mrtInfoRow.reloadView(house.MRTInfo);
		
		//物件地圖
		ImageLoader.getInstance().displayImage(
				house.mapUrl,
				mapImageView, 
				layoutDisplayImageOptions);
		
		//業務聯絡
		this.storeInfoRow.setDetailText(house.store);
		this.storeTelInfoRow.setDetailText(house.storetel);
		this.storeAddressInfoRow.setDetailText(house.storeAddress);
		
		//Button
		reloadFavButton();
	}
	
	private void reloadFavButton(){
		Boolean hasAddFav = LoginInfo.getInstance().hasFavHouse(houseNO);
		if (hasAddFav){
			this.favButton.setText("取消收藏");
		}else{
			this.favButton.setText("加入收藏");
		}
	}
	
	private void setViews(){
		//大圖
		imageDisplayView = (ImageDisplayView) this.findViewById(R.id.imageDisplayView);
		//虛擬實境
		vrInteractButton = (Button) this.findViewById(R.id.vrInteractButton);
		vrPublicButton = (Button) this.findViewById(R.id.vrPublicButton);
		//簡介
		nameTextView = (TextView) this.findViewById(R.id.nameTextView);
		priceFirstTextView = (TextView) this.findViewById(R.id.priceFirstTextView);
		priceFirstTextView.setPaintFlags(priceFirstTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		priceTextView = (TextView) this.findViewById(R.id.priceTextView);
		discountButton = (Button) this.findViewById(R.id.discountButton);
		//chatButton = (Button) this.findViewById(R.id.chatButton);
		callButton = (Button) this.findViewById(R.id.callButton);
		//格局圖
		layoutImageView = (ImageView) this.findViewById(R.id.layoutImageView);
		//物件描述
		descriptionTextView = (TextView) this.findViewById(R.id.descriptionTextView);
		//基本資料
		communityInfoRow = (InfoRow) this.findViewById(R.id.communityInfoRow);
		addressInfoRow = (InfoRow) this.findViewById(R.id.addressInfoRow);
		layoutInfoRow = (InfoRow) this.findViewById(R.id.layoutInfoRow);
		areaBuildingInfoRow = (InfoRow) this.findViewById(R.id.areaBuildingInfoRow);
		areaLandInfoRow = (InfoRow) this.findViewById(R.id.areaLandInfoRow);
		areaPublicInfoRow = (InfoRow) this.findViewById(R.id.areaPublicInfoRow);
		ageInfoRow = (InfoRow) this.findViewById(R.id.ageInfoRow);
		floorInfoRow = (InfoRow) this.findViewById(R.id.floorInfoRow);
		familyInfoRow = (InfoRow) this.findViewById(R.id.familyInfoRow);
		faceInfoRow = (InfoRow) this.findViewById(R.id.faceInfoRow);
		typeInfoRow = (InfoRow) this.findViewById(R.id.typeInfoRow);
		securityInfoRow = (InfoRow) this.findViewById(R.id.securityInfoRow);
		monthlyFeeInfoRow = (InfoRow) this.findViewById(R.id.monthlyFeeInfoRow);
		sideDarkInfoRow = (InfoRow) this.findViewById(R.id.sideDarkInfoRow);
		buildingStructureInfoRow = (InfoRow) this.findViewById(R.id.buildingStructureInfoRow);
		wallStructureInfoRow = (InfoRow) this.findViewById(R.id.wallStructureInfoRow);
		parkingInfoRow = (InfoRow) this.findViewById(R.id.parkingInfoRow);
		pingDetailContainer = (LinearLayout) this.findViewById(R.id.pingDetailContainer);
		//生活機能
		primarySchoolInfoRow = (InfoRow) this.findViewById(R.id.primarySchoolInfoRow);
		juniorSchoolInfoRow = (InfoRow) this.findViewById(R.id.juniorSchoolInfoRow);
		marketInfoRow = (InfoRow) this.findViewById(R.id.marketInfoRow);
		gardenInfoRow = (InfoRow) this.findViewById(R.id.gardenInfoRow);
		mrtInfoRow = (MRTInfoRow) this.findViewById(R.id.mrtInfoRow);
		//物件地圖
		mapImageView = (ImageView) this.findViewById(R.id.mapImageView);
		//業務聯絡
		storeInfoRow = (InfoRow) this.findViewById(R.id.storeInfoRow);
		storeTelInfoRow = (InfoRow) this.findViewById(R.id.storeTelInfoRow);
		storeAddressInfoRow = (InfoRow) this.findViewById(R.id.storeAddressInfoRow);
		//bottom功能
		favButton = (Button) this.findViewById(R.id.favButton);
		sendSalesmanButton = (Button) this.findViewById(R.id.sendSalesmanButton);
		reservationButton = (Button) this.findViewById(R.id.reservationButton);
	}
	
	private void setListener(){
		
		vrInteractButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (house.NO.equals("")) return;
				if (!house.vr.equals("1")) return;
				
				Intent intent = new Intent(HouseDetailActivity.this, HouseVrActivity.class);
				intent.putExtra(BHConstants.EXTRA_HOUSE_NO, house.NO);
				intent.putExtra(BHConstants.EXTRA_HOUSE_NAME, house.name);
				intent.putExtra(BHConstants.EXTRA_HOUSE_IMG, house.bigImg.get(0));
				intent.putExtra(BHConstants.EXTRA_HOUSE_VR_NO, house.NO);
				startActivity(intent);
				overridePendingTransition(0, 0);
			}
		});
		
		vrPublicButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (house.NO.equals("")) return;
				if (house.vrCom.equals("")) return;
				
				Intent intent = new Intent(HouseDetailActivity.this, HouseVrActivity.class);
				intent.putExtra(BHConstants.EXTRA_HOUSE_NO, house.NO);
				intent.putExtra(BHConstants.EXTRA_HOUSE_NAME, house.name);
				intent.putExtra(BHConstants.EXTRA_HOUSE_IMG, house.bigImg.get(0));
				intent.putExtra(BHConstants.EXTRA_HOUSE_VR_NO, house.vrCom);
				startActivity(intent);
				overridePendingTransition(0, 0);
				
			}
		});
		
		callButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intentDial = new Intent("android.intent.action.CALL",Uri.parse("tel:"+house.storetel));
			    startActivity(intentDial);
			}
		});
		
		sendSalesmanButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HouseDetailActivity.this, HouseFormActivity.class);
				intent.putExtra(BHConstants.EXTRA_HOUSE_NO, houseNO);
				intent.putExtra(BHConstants.EXTRA_HOUSE_NAME, house.name);
				intent.putExtra(BHConstants.EXTRA_FORM_TYPE, BHConstants.FORM_MESSAGE_TYPE_SALESMAN);
				startActivity(intent);  
			}
		});
		
		reservationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HouseDetailActivity.this, HouseFormActivity.class);
				intent.putExtra(BHConstants.EXTRA_HOUSE_NO, houseNO);
				intent.putExtra(BHConstants.EXTRA_HOUSE_NAME, house.name);
				intent.putExtra(BHConstants.EXTRA_FORM_TYPE, BHConstants.FORM_MESSAGE_TYPE_RESERVATION);
				startActivity(intent);
			}
		});
		
		imageDisplayView.setListener(new ImageDisplayViewListener(){
			@Override
			public void onClickImage(int position) {
				Intent intent = new Intent(HouseDetailActivity.this, MultiPhotoViewerActivity.class);
				intent.putExtra(BHConstants.EXTRA_INITIAL_IMAGE_POSTION, position);
				intent.putExtra(BHConstants.EXTRA_MEDIA_FILE_PATH_ARRAY, houseImages);
				startActivity(intent);   
			}
		});
		
		favButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Boolean hasAddFav = LoginInfo.getInstance().hasFavHouse(houseNO);
				if (hasAddFav){
					//執行取消收藏
					removeFavAction();
				}else{
					//執行收藏
					addFavAction();
				}
			}
		});
		
		storeTelInfoRow.detailTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentDial = new Intent("android.intent.action.CALL",Uri.parse("tel:"+house.storetel));
			    startActivity(intentDial);
			}
		});
	}
	
	private void addFavAction(){
		
		try {
			final JSONObject favObject = new JSONObject();
			favObject.put(BHConstants.PARAM_HOUSE_NO, houseNO);
			favObject.put(BHConstants.PARAM_FROM, BHConstants.FROM_ANDROID_APP);
			favObject.put(BHConstants.PARAM_SALES_ID, "");
			favObject.put(BHConstants.JSON_KEY_NAME, house.name);
			favObject.put(BHConstants.JSON_KEY_ADDRESS, house.address);
			favObject.put(BHConstants.JSON_KEY_PRICE, house.price);
			favObject.put(BHConstants.JSON_KEY_IMG_DEFAULT, house.imgDefault);
			favObject.put(BHConstants.JSON_KEY_DISCOUNT, house.discount);
			
			if (LoginInfo.getInstance().isLogined()){
				//已登入，call server
				TrackService.trackHouse(houseNO, "", new TrackCallback(){

					@Override
					public void onResult(boolean success, String debugMessage) {
						Log.e(TAG, "addFav: "+success+" msg:"+debugMessage);
						if (success){
							LoginInfo.getInstance().addFavHouse(true, favObject);
							reloadFavButton();
							showToast(getString(R.string.add_collect_success));
							
							//update local fav by call isLogin
							HashMap<String, Object> params = new HashMap<String, Object>();
							params.put(BHConstants.PARAM_FAV, 1);
							LoginService.isLogin(params, new MemberCallback(){
								@Override
								public void onResult(boolean success,JSONObject memberJSONObject, String debugMessage) {
									Log.e(TAG, "favCount:"+LoginInfo.getInstance().favJSONArray.length());
									
								}
							});
						}else{
							showToast(getString(R.string.add_collect_fail)+" : "+debugMessage);
						}
					}
				});
			}else{
				//未登入，存入local
				LoginInfo.getInstance().addFavHouse(true, favObject);
				reloadFavButton();
				Log.e(TAG, "favCount:"+LoginInfo.getInstance().favJSONArray.length());
				showToast(getString(R.string.add_collect_success));
				
			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private void removeFavAction(){

		if (LoginInfo.getInstance().isLogined()){
			//已登入，call server
			TrackService.removeTrackHouse(houseNO, "", new TrackCallback(){

				@Override
				public void onResult(boolean success, String debugMessage) {
					Log.e(TAG, "removeFav: "+success+" msg:"+debugMessage);
					if (success){
						LoginInfo.getInstance().removeFavHouse(true, houseNO);
						reloadFavButton();
						showToast(getString(R.string.remove_collect_success));

						//update local fav by call isLogin
						HashMap<String, Object> params = new HashMap<String, Object>();
						params.put(BHConstants.PARAM_FAV, 1);
						LoginService.isLogin(params, new MemberCallback(){
							@Override
							public void onResult(boolean success,JSONObject memberJSONObject, String debugMessage) {
								Log.e(TAG, "favCount:"+LoginInfo.getInstance().favJSONArray.length());
							}
						});
					}else{
						showToast(getString(R.string.remove_collect_fail)+" : "+debugMessage);
					}
				}
			});
		}else{
			//未登入，存入local
			LoginInfo.getInstance().removeFavHouse(true, houseNO);
			reloadFavButton();
			Log.e(TAG, "favCount:"+LoginInfo.getInstance().favJSONArray.length());
			showToast(getString(R.string.remove_collect_success));
		}
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home){
			slideOutToFinish();
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		
		slideOutToFinish();
	}
	
	public static DisplayImageOptions layoutDisplayImageOptions = new DisplayImageOptions.Builder()
	 .cacheInMemory(true)
	 .cacheOnDisc(true)
	 .showImageOnLoading(R.drawable.placeholder_bigimg).build();
	
}
