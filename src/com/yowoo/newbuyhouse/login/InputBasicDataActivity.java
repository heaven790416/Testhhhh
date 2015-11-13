package com.yowoo.newbuyhouse.login;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
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

public class InputBasicDataActivity extends BaseActivity{

	EditText mobileEditText, emailEditText;
	Button sendButton, skipButton;
	
	String fromAction = "";
	
	private Menu mMenu;
	TextView introTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_input_basic_data);

		//set toolbar
		setToolbarView(getString(R.string.input_basic_data));
		
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
		introTextView = (TextView)findViewById(R.id.introTextView);
		
		mobileEditText = (EditText)findViewById(R.id.mobileEditText);
		emailEditText = (EditText)findViewById(R.id.emailEditText);
		sendButton = (Button)findViewById(R.id.sendButton);
		skipButton = (Button)findViewById(R.id.skipButton);
		
	}
	
	private void reloadViews(){
		
		String introText = 
				"<font color=#3b434a>"+"新增聯絡方式："+"<br/>"+"</font>"+
				"<font color=#3b434a>"+"忘記密碼時，系統可自動發"+"</font>"+
				"<font color=#009038>"+"臨時密碼"+"</font>"+
				"<font color=#3b434a>"+"給您"+"</font>";
		introTextView.setText(Html.fromHtml(introText));	
		
	}
	
	private void setListener(){
		
		sendButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				final String mobile = mobileEditText.getEditableText().toString();
				final String email = emailEditText.getEditableText().toString();
				
				//TODO: 請檢查
				
				
				//set member data
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put(BHConstants.PARAM_MOBILE, mobile);
				params.put(BHConstants.PARAM_EMAIL, email);
				
				LoginService.setMemberData(params, new LoginCallback(){

					@Override
					public void onResult(boolean success, String debubMessage) {
						if (!success){
							showToast(debubMessage);
							return;
						}
						
						showToast(getString(R.string.update_success));
						finish();
					}
				});

			}
		});
		
		skipButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();

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
