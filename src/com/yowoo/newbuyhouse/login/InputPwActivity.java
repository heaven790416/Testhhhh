package com.yowoo.newbuyhouse.login;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.BaseActivity;
import com.yowoo.newbuyhouse.network.LoginService;
import com.yowoo.newbuyhouse.network.LoginService.LoginCallback;
import com.yowoo.newbuyhouse.network.LoginService.MemberCallback;

public class InputPwActivity extends BaseActivity{

	EditText passwordEditText;
	Button sendButton, resendButton;
	
	
	String fromAction = "";
	String account = "";
	
	private Menu mMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_input_pw);

		setToolbarView(getString(R.string.input_temp_pw));
		
		//Get Extra
		Bundle extra = getIntent().getExtras();
		if (extra.containsKey(BHConstants.EXTRA_ACCOUNT)){
			account = extra.getString(BHConstants.EXTRA_ACCOUNT);
		}
		
		setViews();
		
		//reloadViews();
		
		setListener();
		
	}
	
	@Override
	protected void onResume(){
		super.onResume();
	}
	
	
	private void setViews(){
		passwordEditText = (EditText)findViewById(R.id.passwordEditText);
		sendButton = (Button)findViewById(R.id.sendButton);
		resendButton = (Button)findViewById(R.id.resendButton);
		
	}
	
	private void setListener(){
		
		sendButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String password = passwordEditText.getEditableText().toString();
				
				LoginService.login(account, password, new LoginCallback(){

					@Override
					public void onResult(boolean success, String debubMessage) {
						Log.e(TAG, "login:"+success+" debug:"+debubMessage);
						
						if (!success) {
							showToast(debubMessage);
							return;
						}
						
						//get user info by isLogin api
						LoginService.isLogin(new HashMap<String,Object>(), new MemberCallback(){
							@Override
							public void onResult(boolean success, JSONObject memberJSONObject,
									String debubMessage) {
								Log.e(TAG, "islogin:"+success+" debug:"+debubMessage);
								if (!success){
									showToast(debubMessage);
								}else{
									showToast(getString(R.string.login_success));
									finish();
								}
							}
						});
						
					}
				});

			}
		});
		
		resendButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
//				final String account = accountEditText.getEditableText().toString();
				
				LoginService.forgetPw(account, new LoginCallback(){

					@Override
					public void onResult(boolean success, String debubMessage) {
						if (!success) {
							//資料不正確，無法重送密碼
							showToast(debubMessage);
							return;
						}
						
						showToast(getString(R.string.send_temp_pw_success));
					}
				});

			}
		});
	}
	
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
