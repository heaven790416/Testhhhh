package com.yowoo.newbuyhouse.login;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.BaseActivity;
import com.yowoo.newbuyhouse.Singleton;
import com.yowoo.newbuyhouse.network.LoginService;
import com.yowoo.newbuyhouse.network.LoginService.LoginCallback;
import com.yowoo.newbuyhouse.network.LoginService.MemberCallback;
import com.yowoo.newbuyhouse.network.LoginService.ProfileCallback;
import com.yowoo.newbuyhouse.util.DatePickerFragment;
import com.yowoo.newbuyhouse.util.DatePickerFragment.DatePickerListener;

public class ProfileActivity extends BaseActivity{

	EditText nicknameEditText, nameEditText;
	TextView birthTextView;
	RadioGroup genderGroup;
	RadioButton maleRadioButton, femaleRadioButton;
	EditText phoneEditText, backPhoneEditText;
	EditText houseTelCodeEditText, houseTelEditText;
	EditText officeTelCodeEditText, officeTelEditText, officeTelExtEditText;
	EditText emailEditText, backEmailEditText;
	EditText addressEditText;
	LinearLayout identityContainer;
	
	TextView phoneVerifyTextView, emailVerifyTextView;
	Button saveButton;
	
	//for verify
	String mobileServerData = "";
	String emailServerData = "";
	
	private Menu mMenu;
	boolean is_phoneVerify=false;
	boolean is_emailVerify=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_profile);

		setToolbarView(getString(R.string.profile_setting));
		
		//checkLogin();
		if (!LoginInfo.getInstance().isLogined()){
			finish();
			return;
		}
		
		setViews();
		
		setListener();
		
		fetchData();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		//checkLogin();
		if (!LoginInfo.getInstance().isLogined()){
			finish();
			return;
		}
	}
	
	
	private void setViews(){
		
		nicknameEditText = (EditText)findViewById(R.id.nicknameEditText);
		nameEditText = (EditText)findViewById(R.id.nameEditText);
		birthTextView = (TextView)findViewById(R.id.birthTextView);
		
		genderGroup = (RadioGroup) findViewById(R.id.genderGroup);
		maleRadioButton = (RadioButton) findViewById(R.id.maleRadioButton);
		femaleRadioButton = (RadioButton) findViewById(R.id.femaleRadioButton);
		
		phoneEditText = (EditText)findViewById(R.id.phoneEditText_1);
		backPhoneEditText = (EditText)findViewById(R.id.backPhoneEditText);
		houseTelCodeEditText = (EditText)findViewById(R.id.houseTelCodeEditText);
		houseTelEditText = (EditText)findViewById(R.id.houseTelEditText);
		houseTelCodeEditText = (EditText)findViewById(R.id.houseTelCodeEditText);
		officeTelCodeEditText = (EditText)findViewById(R.id.officeTelCodeEditText);
		officeTelEditText = (EditText)findViewById(R.id.officeTelEditText);
		officeTelExtEditText = (EditText)findViewById(R.id.officeTelExtEditText);
		emailEditText = (EditText)findViewById(R.id.emailEditText_1);
		backEmailEditText = (EditText)findViewById(R.id.backEmailEditText);
		addressEditText = (EditText)findViewById(R.id.addressEditText);

		phoneVerifyTextView = (TextView)findViewById(R.id.phoneVerifyTextView_1);
		emailVerifyTextView = (TextView)findViewById(R.id.emailVerifyTextView_1);
		
		identityContainer = (LinearLayout)this.findViewById(R.id.identifyContainer);
		
		//saveButton = (Button)this.findViewById(R.id.saveButton);
	}
	
	private void fetchData(){
		int memberID = LoginInfo.getInstance().getMemberID();
		String account = LoginInfo.getInstance().getAccount();
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_ACCOUNT, account);
		params.put(BHConstants.PARAM_MEMBER_ID, memberID);
		LoginService.getMemberProfile(params, new ProfileCallback(){
			@Override
			public void onResult(boolean success, JSONObject profileJSONObject,
					String debubMessage) {
				Log.e(TAG, "getMemberProfile:"+success+":"+debubMessage);
				//不管有沒有成功，都可從loginInfo撈取資訊reloadView
				reloadViews();
				
				//更新isLogin資料
				LoginService.isLogin(new HashMap<String,Object>(), new MemberCallback(){
					@Override
					public void onResult(boolean success, JSONObject memberJSONObject,
							String debubMessage) {
						Log.e(TAG, "islogin:"+success);
						reloadAdvancedViews();
					}
				});
			}
		});
	}
	
	private void reloadViews(){
		nicknameEditText.setText(LoginInfo.getInstance().getProfileStringData(UserConstants.KEY_NICK));
		nameEditText.setText(LoginInfo.getInstance().getProfileStringData(UserConstants.KEY_NAME));
		
		Log.e("test!", LoginInfo.getInstance().getProfileStringData(UserConstants.KEY_BIRTHDAY));
		birthTextView.setText(LoginInfo.getInstance().getProfileStringData(UserConstants.KEY_BIRTHDAY));
		
		String sex = LoginInfo.getInstance().getProfileStringData(UserConstants.KEY_SEX);
		if (sex.equals("0")){
			maleRadioButton.setChecked(true);
		}else if (sex.equals("1")){
			femaleRadioButton.setChecked(true);
		}else{
			maleRadioButton.setChecked(false);
			femaleRadioButton.setChecked(false);
		}
		
		phoneEditText.setText(LoginInfo.getInstance().getProfileStringData(UserConstants.KEY_MOBILE));
		backPhoneEditText.setText(LoginInfo.getInstance().getProfileStringData(UserConstants.KEY_BACK_MOBILE));
		houseTelCodeEditText.setText(LoginInfo.getInstance().getProfileStringData(UserConstants.KEY_HOUSE_TEL_CODE));
		houseTelEditText.setText(LoginInfo.getInstance().getProfileStringData(UserConstants.KEY_HOUSE_TEL));
		officeTelCodeEditText.setText(LoginInfo.getInstance().getProfileStringData(UserConstants.KEY_OFFICE_TEL_CODE));
		officeTelEditText.setText(LoginInfo.getInstance().getProfileStringData(UserConstants.KEY_OFFICE_TEL));
		officeTelExtEditText.setText(LoginInfo.getInstance().getProfileStringData(UserConstants.KEY_OFFICE_TEL_EXT));
		emailEditText.setText(LoginInfo.getInstance().getProfileStringData(UserConstants.KEY_EMAIL));
		backEmailEditText.setText(LoginInfo.getInstance().getProfileStringData(UserConstants.KEY_BACK_EMAIL));
		addressEditText.setText(LoginInfo.getInstance().getProfileStringData(UserConstants.KEY_ADDRESS));
		
		//save for verify
		mobileServerData = phoneEditText.getText().toString();
		emailServerData = emailEditText.getText().toString();
		
		reloadAdvancedViews();
	}
	
	/* refresh跟isLogin裡面資料有關的views */
	private void reloadMobileVerifyView(){
		String verify = LoginInfo.getInstance().getMemberStringData(UserConstants.KEY_MOBILE_VERIFY);
		Log.e("test", "phone: "+phoneEditText.getText().toString());
		if (phoneEditText.getText().toString().equals("")){
			phoneVerifyTextView.setVisibility(View.GONE);
		}else{
			if (verify.equals("") || verify.equals("0")){				
				phoneVerifyTextView.setBackgroundResource(R.drawable.btn_check_off);
				phoneVerifyTextView.setText(getString(R.string.not_verified));
				phoneVerifyTextView.setTextColor(Color.parseColor("#FFFFFF"));
				is_phoneVerify = false;
			}else{
				if (phoneEditText.getText().toString().equals(mobileServerData)){
					phoneVerifyTextView.setBackgroundResource(R.drawable.btn_check_on);
					phoneVerifyTextView.setText(getString(R.string.verified));
					phoneVerifyTextView.setTextColor(Color.parseColor("#5cc790"));
					is_phoneVerify = true;
					
				}else{
					phoneVerifyTextView.setBackgroundResource(R.drawable.btn_check_off);
					phoneVerifyTextView.setText(getString(R.string.not_verified));
					phoneVerifyTextView.setTextColor(Color.parseColor("#FFFFFF"));
					is_phoneVerify = false;
				}
			}
			phoneVerifyTextView.setVisibility(View.VISIBLE);
		}
	}
	
	private void reloadEmailVerifyView(){
		String verify = LoginInfo.getInstance().getMemberStringData(UserConstants.KEY_EMAIL_VERIFY);
		if (emailEditText.getText().toString().equals("")){
			emailVerifyTextView.setVisibility(View.GONE);
		}else{
			if (verify.equals("") || verify.equals("0")){
				emailVerifyTextView.setBackgroundResource(R.drawable.btn_check_off);
				emailVerifyTextView.setText(getString(R.string.not_verified));
				emailVerifyTextView.setTextColor(Color.parseColor("#FFFFFF"));
				is_emailVerify=false;
			}else{
				if (emailEditText.getText().toString().equals(emailServerData)){
					emailVerifyTextView.setBackgroundResource(R.drawable.btn_check_on);
					emailVerifyTextView.setText(getString(R.string.verified));
					emailVerifyTextView.setTextColor(Color.parseColor("#5cc790"));
					is_emailVerify=true;
				}else{
					emailVerifyTextView.setBackgroundResource(R.drawable.btn_check_off);
					emailVerifyTextView.setText(getString(R.string.not_verified));
					emailVerifyTextView.setTextColor(Color.parseColor("#FFFFFF"));
					is_emailVerify=false;
				}
			}
			emailVerifyTextView.setVisibility(View.VISIBLE);
		}
	}
	
	private void reloadAdvancedViews(){
		//verify phone
		reloadMobileVerifyView();

		//verify phone
		reloadEmailVerifyView();

		//identity
		try {
			JSONArray identities = LoginInfo.getInstance().getMemberIdentities();
			this.identityContainer.removeAllViews();
			for (int i=0; i<identities.length(); i++){
				TextView identityTextView_1 = new TextView(this);
				identityTextView_1.setText(" ");
				
				TextView identityTextView = new TextView(this);
				identityTextView.setText(identities.getString(i));
				identityTextView.setTextColor(getResources().getColor(R.color.white_color));
				identityTextView.setBackgroundResource(R.drawable.ic_identity);
				identityTextView.setPadding(10, 10, 10, 10);

				this.identityContainer.addView(identityTextView_1);
				this.identityContainer.addView(identityTextView);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void setListener(){
		
		birthTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String birth = birthTextView.getText().toString();
				if (!birth.equals("")){
					try {
						String[] birthDayArray = birth.split("-");
						int year = Integer.valueOf(birthDayArray[0]);
						int month = Integer.valueOf(birthDayArray[1])-1;
						int day = Integer.valueOf(birthDayArray[2]);
						showDatePickerDialog(year, month, day);
						return;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				//init date
			    final Calendar c = Calendar.getInstance();
				showDatePickerDialog(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
			}
		});
		
		this.phoneEditText.addTextChangedListener(new TextWatcher(){
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				reloadMobileVerifyView();
			}
		});
		
		this.emailEditText.addTextChangedListener(new TextWatcher(){
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				reloadEmailVerifyView();
			}
		});
		
		this.phoneVerifyTextView.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				//TODO: 驗證手機格式是否正確
				
				if(!is_phoneVerify){
					//如果此verify可顯示，代表目前使用者輸入的值未驗證，直接進入驗證頁面
					String verifyData = phoneEditText.getText().toString();
					sendVerifyCode(verifyData, "mobile");
				}
			}
		});
		
		this.emailVerifyTextView.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				//TODO: 驗證手機格式是否正確
				
				if(!is_emailVerify){
					//如果此verify可顯示，代表目前使用者輸入的值未驗證，直接進入驗證頁面
					String verifyData = emailEditText.getText().toString();
					sendVerifyCode(verifyData, "email");
				}
			}
		});
		
	}
	
	private void sendVerifyCode(final String verifyData, final String verifyType){
		//如果目前要驗證的資料，跟目前登入狀態的userID一樣
		//例如：註冊或登入的userID為mobile,如果要驗證的mobile跟userID一樣，
		//不要setProfile重送驗證碼，直接進入填寫驗證碼之頁面
		if (verifyData.equals(LoginInfo.getInstance().getUserID())){
			goToInputVerifyCodeAction(verifyData, verifyType);
			return;
		}
		
		Log.e(TAG, "setProfile: ready to send new verify code!");
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_ACCOUNT_MERGE, verifyData);
		LoginService.setProfile(params, new LoginCallback(){
			@Override
			public void onResult(boolean success, String debugMessage) {
				Log.e(TAG, "setProfile:"+success+" msg:"+debugMessage);
				if (success){
					//送驗證碼成功
					showToast(getString(R.string.send_verify_code_success));
					goToInputVerifyCodeAction(verifyData, verifyType);
				}else{
					showToast(getString(R.string.send_verify_code_error)+" : "+debugMessage);
				}
			}
		});
	}
	
	private void goToInputVerifyCodeAction(String verifyData, String verifyType){
		Intent intent = new Intent().setClass(ProfileActivity.this, InputVerifyCodeActivity.class);
		intent.putExtra(BHConstants.EXTRA_ACCOUNT, verifyData);
		intent.putExtra(BHConstants.EXTRA_VERIFY_TYPE, verifyType);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, BHConstants.ACTIVITY_REQUEST_VERIFY);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode==BHConstants.ACTIVITY_REQUEST_VERIFY&&resultCode==RESULT_OK) {
			fetchData();
		}
	}
	
	public void showDatePickerDialog(int year, int month, int day) {
		DatePickerFragment datePickerFragment = new DatePickerFragment();
	    datePickerFragment.setListener(datePickerListener);
	    datePickerFragment.initDate(year, month, day);
	    datePickerFragment.show(getSupportFragmentManager(), "datePicker"); //change function
	}
	
	private DatePickerListener datePickerListener = new DatePickerListener(){

		@Override
		public void onDateSet(DatePicker view, int newYear, int newMonth, int newDay) {

			String dateText = newYear+"-"+(newMonth+1)+"-"+newDay;
			birthTextView.setText(dateText);
		}
	};
	
//	private void checkLogin(){
//		//check if logined
//		if (!LoginInfo.getInstance().isLogined()){
//			finish();
//		}
//	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_profile_activity, menu);
		this.mMenu = menu;
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		if (id == android.R.id.home){
			finish();
			return true;
		}
		
		if (id == R.id.action_save) {
			String sex = "";
			if (maleRadioButton.isChecked()){
				sex = "0";
			}else if (femaleRadioButton.isChecked()){
				sex = "1";
			}
				
			final HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(UserConstants.KEY_NICK, nicknameEditText.getText().toString());
			params.put(UserConstants.KEY_NAME, nameEditText.getText().toString());
			params.put(UserConstants.KEY_BIRTHDAY, birthTextView.getText().toString());
			params.put(UserConstants.KEY_SEX, sex);
			params.put(UserConstants.KEY_MOBILE, phoneEditText.getText().toString());
			params.put(UserConstants.KEY_BACK_MOBILE, backPhoneEditText.getText().toString());
			params.put(UserConstants.KEY_HOUSE_TEL_CODE, houseTelCodeEditText.getText().toString());
			params.put(UserConstants.KEY_HOUSE_TEL, houseTelEditText.getText().toString());
			params.put(UserConstants.KEY_OFFICE_TEL_CODE, officeTelCodeEditText.getText().toString());
			params.put(UserConstants.KEY_OFFICE_TEL, officeTelEditText.getText().toString());
			params.put(UserConstants.KEY_OFFICE_TEL_EXT, officeTelExtEditText.getText().toString());
			params.put(UserConstants.KEY_EMAIL, emailEditText.getText().toString());
			params.put(UserConstants.KEY_BACK_EMAIL, backEmailEditText.getText().toString());
			params.put(UserConstants.KEY_ADDRESS, addressEditText.getText().toString());
			
			LoginService.setMemberData(params, new LoginCallback(){
				@Override
				public void onResult(boolean success, String debugMessage) {
					Log.e(TAG, "setMemberData:"+success+" msg:"+debugMessage);
					if (success){
						LoginService.isLogin(new HashMap<String,Object>(), new MemberCallback(){
							@Override
							public void onResult(boolean success, JSONObject memberJSONObject,
									String debubMessage) {
								Log.e(TAG, "islogin:"+success+" debug:"+debubMessage);

								if (success){
									showToast(getString(R.string.update_success));
									finish();
								}else{
									showToast(getString(R.string.update_fail));
								}
							}
						});
					}else{
						showToast(getString(R.string.update_fail));
					}
				}
			});
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
