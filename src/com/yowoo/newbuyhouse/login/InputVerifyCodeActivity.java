package com.yowoo.newbuyhouse.login;

import java.util.HashMap;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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

public class InputVerifyCodeActivity extends BaseActivity{

	EditText codeEditText;
	Button sendButton, resendButton;
	
	
	String fromAction = "";
	String account = "";
	String verifyType = "";
	
	private Menu mMenu;
	
	TextView introTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_input_verify_code);
		
		this.setToolbarView(getString(R.string.input_verify_code));
		
		//Get Extra
		Bundle extra = getIntent().getExtras();
		if (extra.containsKey(BHConstants.EXTRA_ACCOUNT)){
			account = extra.getString(BHConstants.EXTRA_ACCOUNT);
		}
		
		if (extra.containsKey(BHConstants.EXTRA_VERIFY_TYPE)){
			verifyType = extra.getString(BHConstants.EXTRA_VERIFY_TYPE);
		}
		
		
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
		
		codeEditText = (EditText)findViewById(R.id.codeEditText);
		sendButton = (Button)findViewById(R.id.sendButton);
		resendButton = (Button)findViewById(R.id.resendButton);
		
	}
	
	private void reloadViews(){
		
		if(verifyType.equals("email")){
			String introText = 
					"<font color=#3b434a>"+"已將驗證碼發送至電子信箱"+"<br/>"+"</font>"+
					"<font color=#009038>"+account+"</font>";
			introTextView.setText(Html.fromHtml(introText));	
		}
		else if(verifyType.equals("mobile")){
			String introText = 
					"<font color=#3b434a>"+"已將驗證碼發送至手機號碼"+"<br/>"+"</font>"+
					"<font color=#009038>"+account+"</font>";
			introTextView.setText(Html.fromHtml(introText));	
		}
	}
	
	private void setListener(){
		
		sendButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String userID = LoginInfo.getInstance().getUserID();
				String code = codeEditText.getEditableText().toString();
				
				LoginService.verifyAccount(userID, code, new LoginCallback(){
					@Override
					public void onResult(boolean success, String debugMessage) {
						Log.e(TAG, "verifyAccount: "+success+ "msg:"+debugMessage);
						
						if (success){
							showToast(getString(R.string.verify_success));
							setResult(RESULT_OK);
							finish();
						}else{
							showToast(debugMessage);
						}
					}
				});

			}
		});
		
		resendButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendVerifyCode(account, verifyType);
			}
		});
	}
	
	
	private void sendVerifyCode(final String verifyData, final String verifyType){
		
		Log.e("test", "setProfile: ready to send new verify code!");
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(BHConstants.PARAM_ACCOUNT_MERGE, verifyData);
		LoginService.setProfile(params, new LoginCallback(){
			@Override
			public void onResult(boolean success, String debugMessage) {
				Log.e(TAG, "setProfile:"+success+" msg:"+debugMessage);
				
				if (success){
					showToast(getString(R.string.send_verify_code_success));
				}else{
					showToast(getString(R.string.send_verify_code_fail));
				}
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
