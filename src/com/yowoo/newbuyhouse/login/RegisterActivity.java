package com.yowoo.newbuyhouse.login;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.BaseActivity;
import com.yowoo.newbuyhouse.network.LoginService;
import com.yowoo.newbuyhouse.network.LoginService.LoginCallback;
import com.yowoo.newbuyhouse.network.LoginService.MemberCallback;

public class RegisterActivity extends BaseActivity{

	EditText accountEditText, passwordEditText, confirmEditText;
	Button sendButton;
	
	String fromAction = "";
	
	private Menu mMenu;
	
	ImageView checkImageView;
	Button viewButton;
	TextView lawTextView, contactServiceTextView, LoginServiceTextView;
	
	private LoginStatus loginStatusAction;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register);

		setToolbarView(getString(R.string.register));
		
		//Get Extra
//		Bundle extra = getIntent().getExtras();
//		if (extra.containsKey(SyConstants.EXTRA_FROM_ACTION)){
//			fromAction = extra.getString(SyConstants.EXTRA_FROM_ACTION);
//		}else{
//			//default
//			fromAction = SyConstants.FROM_ACTION_LOGIN;
//		}
		
		setViews();
		
		//set login status action
		loginStatusAction = new LoginStatusAction(this);
		
		reloadViews();
		
		setListener();
		
		
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		if (LoginInfo.getInstance().isLogined()){
			finish();
		}
	}
	
	
	private void setViews(){
		accountEditText = (EditText)findViewById(R.id.accountEditText);
		passwordEditText = (EditText)findViewById(R.id.passwordEditText);
		confirmEditText = (EditText)findViewById(R.id.confirmEditText);
		sendButton = (Button)findViewById(R.id.sendButton);
		
		checkImageView = (ImageView)findViewById(R.id.checkImageView);
		viewButton = (Button)this.findViewById(R.id.viewButton);
		lawTextView = (TextView) this.findViewById(R.id.lawTextView);
		contactServiceTextView = (TextView) this.findViewById(R.id.contactServiceTextView);
		LoginServiceTextView = (TextView) this.findViewById(R.id.LoginServiceTextView);
	}
	
	private void reloadViews(){
		
		String lawText = 
				"<font color=#3b434a>"+"我已詳細閱讀，並同意信義房屋"+"</font>"+
				"<font color=#009038>"+" 網站服務條款"+"</font>"+
				"<font color=#3b434a>"+"以及"+"</font>"+
				"<font color=#009038>"+" 隱私權聲明"+"</font>";
		lawTextView.setText(Html.fromHtml(lawText));	
		
	}
	
	private void setListener(){
		
		lawTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = BHConstants.FORM_LAW_DETAIL_URL;
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
				
				String url_2 = "http://www.sinyi.com.tw/privacy.php";
				Intent i_2 = new Intent(Intent.ACTION_VIEW);
				i_2.setData(Uri.parse(url_2));
				startActivity(i_2);
			}
		});
		
		viewButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//showToast("TYPE:"+passwordEditText.getInputType());
				if (passwordEditText.getInputType()==(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)){
					passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
				}else{
					passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});
		
		checkImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (checkImageView.isSelected()){
					checkImageView.setSelected(false);
					checkImageView.setBackgroundResource(R.drawable.tick_off);
				}else{
					checkImageView.setSelected(true);
					checkImageView.setBackgroundResource(R.drawable.tick_on);
				}
			}
		});
		
		sendButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (!checkImageView.isSelected()){
					showToast(getString(R.string.please_check_law));
					return;
				}
				
				String password = passwordEditText.getEditableText().toString();
				String checkPassword = confirmEditText.getEditableText().toString();
				
				if(password.equals(checkPassword)){
					login();
				}
				else{
					showToast(getString(R.string.pw_not_equal));
				}
			}
		});
		
		contactServiceTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				contactService();
			}
		});
		
		LoginServiceTextView.setOnClickListener(new View.OnClickListener() { //回到登入頁面
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
		
	}
	
	private void login(){
		final String account = accountEditText.getEditableText().toString().toLowerCase();
		final String password = passwordEditText.getEditableText().toString();
		//TODO: 請檢查
		
		
		//註冊
		LoginService.register(account, password, new LoginCallback(){

			@Override
			public void onResult(boolean success, String debubMessage) {
				if (!success){
					showToast(debubMessage);
					return;
				}
				
				//註冊成功，直接幫user登入
				loginAction(account, password);
			}
		});
		
	}
	
	private void loginAction(String account, String password){
		
		LoginService.login(account, password, new LoginCallback(){

			@Override
			public void onResult(boolean success, String debubMessage) {
				if (!success) {
					showToast(debubMessage);
					finish();
					return;
				}
				
				loginStatusAction.updateTrackAndIsLoginAction();
				
				//get user info by isLogin api
//				LoginService.isLogin(new HashMap<String,Object>(), new MemberCallback(){
//					@Override
//					public void onResult(boolean success, JSONObject memberJSONObject,
//							String debubMessage) {
//						if (!success){
//							showToast("islogin:"+success+" debug:"+debubMessage);
//							finish();
//							return;
//						}
//						
//						//check是否已有手機/Email資料
//						//若沒有，邀請使用者增加填寫手機＆email
//						updateUserDataAction();
//						
//						finish();
//					}
//				});
				
			}
		});
	}
	
//	private void updateUserDataAction(){
//		String mobile = LoginInfo.getInstance().getMemberStringData(UserConstants.KEY_MOBILE);
//		String email = LoginInfo.getInstance().getMemberStringData(UserConstants.KEY_EMAIL);
//		
//		if (mobile.equals("")&&email.equals("")){
//			Intent intent = new Intent().setClass(RegisterActivity.this, InputBasicDataActivity.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intent);
//		}
//	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.global, menu);
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
		
		return super.onOptionsItemSelected(item);
	}
	
}
