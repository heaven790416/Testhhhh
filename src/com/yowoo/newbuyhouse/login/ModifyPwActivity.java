package com.yowoo.newbuyhouse.login;

import java.util.HashMap;

import org.json.JSONObject;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.BaseActivity;
import com.yowoo.newbuyhouse.network.LoginService;
import com.yowoo.newbuyhouse.network.LoginService.LoginCallback;
import com.yowoo.newbuyhouse.network.LoginService.MemberCallback;

public class ModifyPwActivity extends BaseActivity{

	EditText passwordEditText, checkPasswordEditText;
	Button sendButton, viewButton;
	
	
	String fromAction = "";
	//String account = "";
	
	private Menu mMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_modify_pw);

		setToolbarView(getString(R.string.pw_setting));
		
		//Get Extra
//		Bundle extra = getIntent().getExtras();
//		if (extra.containsKey(BHConstants.EXTRA_ACCOUNT)){
//			account = extra.getString(BHConstants.EXTRA_ACCOUNT);
//		}
		
		//checkLogin();
		if (!LoginInfo.getInstance().isLogined()){
			finish();
			return;
		}
		
		setViews();
		
		//reloadViews();
		
		setListener();
		
		
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
		passwordEditText = (EditText)findViewById(R.id.passwordEditText);
		checkPasswordEditText = (EditText)findViewById(R.id.checkPasswordEditText);
		sendButton = (Button)findViewById(R.id.sendButton);
		viewButton = (Button)this.findViewById(R.id.viewButton);
		
	}
	
	private void setListener(){
		
		sendButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String password = passwordEditText.getEditableText().toString();
				String checkPassword = checkPasswordEditText.getEditableText().toString();
				
				if(password.equals(checkPassword)){
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put(BHConstants.PARAM_MEMBER_ID, LoginInfo.getInstance().getMemberID());
					params.put(BHConstants.PARAM_PWD_NEW, password);
					
					LoginService.setProfile(params, new LoginCallback(){

						@Override
						public void onResult(boolean success, String debugMessage) {
							Log.e(TAG, "setProfile:"+success+" msg:"+debugMessage);
							if (success){
								showToast(getString(R.string.update_success));
								finish();
							}else{
								showToast(debugMessage);
							}
						}
					});
				}
				else{
					showToast(getString(R.string.pw_not_equal));
				}
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
	}
	
	
	
//	private void checkLogin(){
//		//check if logined
//		if (!LoginInfo.getInstance().isLogined()){
//			finish();
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
