package com.yowoo.newbuyhouse.login;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.BaseActivity;
import com.yowoo.newbuyhouse.network.LoginService;
import com.yowoo.newbuyhouse.network.LoginService.LoginCallback;
import com.yowoo.newbuyhouse.network.LoginService.MemberCallback;

public class ForgetPwActivity extends BaseActivity{

	EditText accountEditText;
	Button sendButton;
	
	TextView introTextView;
	
	String fromAction = "";
	private Menu mMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_forget_pw);

		//set toolbar
		setToolbarView(getString(R.string.forget_password));
		
		//Get Extra
//		Bundle extra = getIntent().getExtras();
//		if (extra.containsKey(SyConstants.EXTRA_FROM_ACTION)){
//			fromAction = extra.getString(SyConstants.EXTRA_FROM_ACTION);
//		}else{
//			//default
//			fromAction = SyConstants.FROM_ACTION_LOGIN;
//		}
		
		setViews();
		
		reloadViews();
		
		setListener();
		
		
	}
	
	@Override
	protected void onResume(){
		super.onResume();
	}
	
	
	private void setViews(){
		introTextView = (TextView) findViewById(R.id.introTextView);
		accountEditText = (EditText)findViewById(R.id.accountEditText);
		sendButton = (Button)findViewById(R.id.sendButton);
		
	}
	
	private void reloadViews(){
		
		String introText = 
				"<font color=#3b434a>"+"請輸入您註冊的帳號，"+"<br/>"+"臨時密碼將發送至您的"+"</font>"+
				"<font color=#009038>"+"手機號碼"+"</font>"+
				"<font color=#3b434a>"+"或者"+"</font>"+
				"<font color=#009038>"+"電子信箱"+"</font>";
		introTextView.setText(Html.fromHtml(introText));	
		
	}
	
	private void setListener(){
		
		sendButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				final String account = accountEditText.getEditableText().toString();
				
				LoginService.forgetPw(account, new LoginCallback(){

					@Override
					public void onResult(boolean success, String debubMessage) {
						if (!success) {
							//資料不正確，無法重送密碼
							showToast(debubMessage);
							return;
						}
						
						//資料正確，已送臨時密碼，切到「輸入臨時密碼」
						Intent intent = new Intent().setClass(ForgetPwActivity.this, InputPwActivity.class);
						intent.putExtra(BHConstants.EXTRA_ACCOUNT, account);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						finish();
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
