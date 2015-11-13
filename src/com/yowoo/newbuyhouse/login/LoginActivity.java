package com.yowoo.newbuyhouse.login;


import java.io.IOException;
import java.util.Arrays;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.BaseActivity;
import com.yowoo.newbuyhouse.network.LoginService;
import com.yowoo.newbuyhouse.network.LoginService.LoginCallback;

public class LoginActivity extends BaseActivity implements 
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener{

	EditText accountEditText, passwordEditText;
	Button loginButton, registerButton, forgetPwButton;
	Button yahooButton, googleButton;
	Button viewButton;
	
	/* Facebook Login */
	Button fbButton;
	//LoginButton fbButton;
	CallbackManager callbackManager;
//	ProfileTracker profileTracker;
//	AccessTokenTracker accessTokenTracker;
	AccessToken accessToken;
	
	/* Google+ Login */
	private static final String TAG  = "test";

	private static final int REQUEST_CODE_SIGN_IN = 1;
	private static final int REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES = 2;
    private static final int REQUEST_CODE_TOKEN_AUTH = 3;

    /* Yahoo Login */
    private static final int REQUEST_CODE_YAHOO = 4;
    
	private boolean mIntentInProgress;
	private boolean signedInUser=false;
    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;
    private String googleId = "";
    private String googleAccount = "";
    
	LoginInfo loginInfo;
	
	String fromAction = "";
	private Menu mMenu;
	
	private TextView contactServiceTextView;
	
	//Login success action controls
	private LoginStatus loginStatusAction;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		this.setToolbarView(getString(R.string.member_login));
		
		
		setViews();
		
		//reloadViews();
		
		//set login status action
		loginStatusAction = new LoginStatusAction(this);
		
		setListener();
		
		//set fbButton
		setFacebookListener();
		
		//set googleButton
		mGoogleApiClient = buildGoogleApiClient(false);
		
		
		//register broadcast receiver
		LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
		broadcastManager.registerReceiver(yahooLoginBroadcastReceiver, new IntentFilter(BHConstants.BROADCAST_LOGIN_YAHOO));

		
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		if (LoginInfo.getInstance().isLogined()){
			finish();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    
	    //facebook
	    callbackManager.onActivityResult(requestCode, resultCode, data);
	    
	    Log.e("test", "requestCode:"+requestCode+" resultCode:"+resultCode);
	    
	    //google
	    if (requestCode==REQUEST_CODE_SIGN_IN){
	    		if (resultCode == RESULT_OK) {
	    			Log.e("test", "0");
	    			if (!mGoogleApiClient.isConnected()) {
	    				Log.e("test","1 Previous resolution completed successfully, try connecting again");
	    				mGoogleApiClient.reconnect();
	    			}
	    		}else{
	    			signedInUser = false;
	    		}
	    		mIntentInProgress = false;
	    		
	    		Log.e("test", "2");
	    }

        else if (requestCode== REQUEST_CODE_TOKEN_AUTH){
        		if (resultCode == RESULT_OK){
        			getGoogleTokenAction();
        			Log.e("test", "4");
        		}else{
        			Log.e("test", "REQUEST_CODE_TOKEN_AUTH:resultCode != ok!");
        			Log.e("test", "5");
        		}
        }
	    
	    //for yahoo: for some advice, singleTask activity cannot send result(always return RESULT_CANCELED)
	    //so, use broadcast instead of this
//        else if (requestCode== REQUEST_CODE_YAHOO){
//        		if (resultCode == RESULT_OK){
//        			String yahooAccount = data.getExtras().getString("email");
//        			String yahoooId = data.getExtras().getString("id");
//        			String token = data.getExtras().getString("token");
//    				
//        			sinyiOpenIdLoginAction(yahooAccount, yahoooId, token, "y", "", data.getExtras());
//        		}
//        }
	    
	    Log.e("test", "3");
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    //profileTracker.stopTracking();
	    //accessTokenTracker.stopTracking();
	    
		LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
		broadcastManager.unregisterReceiver(yahooLoginBroadcastReceiver);
	}
	
	private void setViews(){
		accountEditText = (EditText)findViewById(R.id.accountEditText);
		passwordEditText = (EditText)findViewById(R.id.passwordEditText);
		loginButton = (Button)findViewById(R.id.loginButton);
		registerButton = (Button)this.findViewById(R.id.registerButton);
		forgetPwButton = (Button)this.findViewById(R.id.forgetPwButton);
		
		fbButton = (Button)this.findViewById(R.id.fbButton);
		yahooButton = (Button)this.findViewById(R.id.yahooButton);
		googleButton = (Button)this.findViewById(R.id.googleButton);
		
		viewButton = (Button)this.findViewById(R.id.viewButton);
		
		contactServiceTextView = (TextView)this.findViewById(R.id.contactServiceTextView);
		
	}
	
	
	
	private void setListener(){
		
		
		
		loginButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String userID = accountEditText.getEditableText().toString().toLowerCase();
				String password = passwordEditText.getEditableText().toString();
				
				LoginService.login(userID, password, new LoginCallback(){

					@Override
					public void onResult(boolean success, String debubMessage) {
						Log.e(TAG, "login:"+success+" debug:"+debubMessage);
						
						if (!success) {
							showToast(debubMessage);
							return;
						}
						
						loginStatusAction.updateTrackAndIsLoginAction();
					}
				});

			}
		});
		
		forgetPwButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent().setClass(LoginActivity.this, ForgetPwActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		
		registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent().setClass(LoginActivity.this, RegisterActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		
		googleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				googlePlusLogin();
			}
		});
		
		yahooButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent().setClass(LoginActivity.this, YahooLoginActivity.class);
				startActivityForResult(intent, REQUEST_CODE_YAHOO);

				//				Intent intent = new Intent().setClass(LoginActivity.this, TestActivity.class);
//				startActivityForResult(intent, REQUEST_CODE_YAHOO);
				
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
		
		contactServiceTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				contactService();
			}
		});
		
	}
	
	
	private void setFacebookListener(){
		
		fbButton.setOnClickListener(new Button.OnClickListener(){

	        @Override
	        public void onClick(View v) {
	            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email", "user_friends"));
	        }
	    });
		
	    // Callback registration
		callbackManager = CallbackManager.Factory.create();

		LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
	        @Override
	        public void onSuccess(LoginResult loginResult) {
	            // App code
	        		Log.e("test", "loginResult: "+loginResult.getAccessToken());
	        		accessToken = loginResult.getAccessToken();
	        		
	        		GraphRequest request = GraphRequest.newMeRequest(
	        				accessToken,
	        				new GraphRequest.GraphJSONObjectCallback() {

	        					//當RESPONSE回來的時候

	        					@Override
	        					public void onCompleted(final JSONObject object, GraphResponse response) {
	        						
	        						String account = object.optString("email");
	        						String openId = object.optString("id");
	        						String token = accessToken.getToken();
	        						String from = "f";
	        						String field = BHConstants.FACEBOOK_REQUEST_FIELDS;
	        						
	        						sinyiOpenIdLoginAction(account, openId, token, from, field, null);
	        						
	        					}
	        				});

	        		//包入你想要得到的資料 送出request
	        		Bundle parameters = new Bundle();
	        		parameters.putString("fields", BHConstants.FACEBOOK_REQUEST_FIELDS);
	        		request.setParameters(parameters);
	        		request.executeAsync();
	        }

	        @Override
	        public void onCancel() {
	            // App code
	        }

	        @Override
	        public void onError(FacebookException exception) {
	            // App code
	        }
	    });  
		
		
//		profileTracker = new ProfileTracker() {
//	        @Override
//	        protected void onCurrentProfileChanged(
//	                Profile oldProfile,
//	                Profile currentProfile) {
//	            // App code
//	        		String fbId = currentProfile.getId();
//	        		Log.e("test", "fbId: "+fbId);
//	        }
//	    };
//	    
//	    accessTokenTracker = new AccessTokenTracker() {
//	        @Override
//	        protected void onCurrentAccessTokenChanged(
//	            AccessToken oldAccessToken,
//	            AccessToken currentAccessToken) {
//	                // Set the access token using 
//	                // currentAccessToken when it's loaded or set.
//	        		String token = currentAccessToken.getToken();
//	        		Log.e("test", "token: "+token);
//	        }
//	    };
//	    
	    // If the access token is available already assign it.
	    //accessToken = AccessToken.getCurrentAccessToken();
	}
	
	//TODO:TEST_LOGIN_ACTION
//	private void updateUserDataAction(){
//		String mobile = LoginInfo.getInstance().getMemberStringData(UserConstants.KEY_MOBILE);
//		String email = LoginInfo.getInstance().getMemberStringData(UserConstants.KEY_EMAIL);
//		
//		if (mobile.equals("")&&email.equals("")){
//			Intent intent = new Intent().setClass(LoginActivity.this, InputBasicDataActivity.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intent);
//		}
//	}

	/* Google+ Login */
	@Override
    public void onStart() {
        super.onStart();
        Log.e("G+","Activity onStart, starting connecting GoogleApiClient");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
    		Log.e("G+","Activity onStop, disconnecting GoogleApiClient");
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void resolveSignInError() {
    		Log.e("test", "in resolveSignInError 0");
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, REQUEST_CODE_SIGN_IN);
				Log.e("test", "in resolveSignInError 1");
			} catch (SendIntentException e) {
				e.printStackTrace();
				mIntentInProgress = false;
				mGoogleApiClient.connect();
				Log.e("test", "in resolveSignInError 2");
			}
		}
	}
    
    private void getGoogleProfileInforAction() {
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
				String personName = currentPerson.getDisplayName();
				String openId = currentPerson.getId();
				String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
				
				Log.e(TAG, "Google: name:"+personName+"  email:"+email);
				
				// update profile frame with new info about Google Account
				// profile
				updateProfile(true);
				
				//temp save user profile
				this.googleId = openId;
				this.googleAccount = email;
				
				//get token
				getGoogleTokenAction();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    private void updateProfile(boolean isSignedIn) {
		if (isSignedIn) {
//			signinFrame.setVisibility(View.GONE);
//			profileFrame.setVisibility(View.VISIBLE);
			//showToast(getString(R.string.login_success));
		} else {
//			signinFrame.setVisibility(View.VISIBLE);
//			profileFrame.setVisibility(View.GONE);
			//showToast(getString(R.string.logout_success));
		}
	}
    

	private void googlePlusLogin() {
		if (!mGoogleApiClient.isConnecting()) {
			signedInUser = true;
			resolveSignInError();
		}
	}

	private void googlePlusLogout() {
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			mGoogleApiClient.connect();
			updateProfile(false);
		}
	}

    @Override
	public void onConnected(Bundle arg0) {
    		
    		//tricky: 如果進到這個頁面(此app應處登出狀態)，卻發現google仍是登入（之前登出沒清乾淨）
    		//需將google登出，等使用者實際按下登入按鈕，才可執行connect之後該有的動作
    		if (!signedInUser){
    			Log.e(TAG, "Google: Connected, but need logout!");
    			googlePlusLogout();
    			return;
    		}
    		
    		//使用者有按下google login按鈕的狀態（signedInUser=true）
    		signedInUser = false;
    		Log.e(TAG, "Google: Connected");
		getGoogleProfileInforAction();
	}

    @Override
	public void onConnectionSuspended(int cause) {
		mGoogleApiClient.connect();
		updateProfile(false);
		Log.e("test", "13");
	}

    @Override
	public void onConnectionFailed(ConnectionResult result) {
		
//    		if (!result.hasResolution()) {
//			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
//			return;
//		}

    		Log.e(TAG, String.format("GoogleApiClient onConnectionFailed, error code: %d, with " +
                "resolution: %b", result.getErrorCode(), result.hasResolution()));
        
    	
		if (!mIntentInProgress) {
			// store mConnectionResult
			mConnectionResult = result;

			Log.e(TAG, "signedInUser:"+signedInUser);
			if (signedInUser) {
				resolveSignInError();
			}
			
		}else{
			Log.e(TAG,"Intent already in progress, ignore the new failure");
		}
	}

    

	private GoogleApiClient buildGoogleApiClient(boolean useProfileScope) {
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this);

//        String serverClientId = getString(R.string.server_client_id);
//
//        if (!TextUtils.isEmpty(serverClientId)) {
//            builder.requestServerAuthCode(serverClientId, this);
//        }

        if (useProfileScope) {
            builder.addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_PROFILE);
        } else {
            builder.addApi(Plus.API, Plus.PlusOptions.builder()
                            .addActivityTypes(MomentUtil.ACTIONS).build())
                    .addScope(Plus.SCOPE_PLUS_LOGIN);
        }

        return builder.build();
    }

	
	private void getGoogleTokenAction(){
		AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String token = null;
		        Object[] SCOPES = {"profile","email"};
		        try {
		        		token = GoogleAuthUtil.getToken(LoginActivity.this,
		        				Plus.AccountApi.getAccountName(mGoogleApiClient),
		        				"oauth2:" + TextUtils.join(" ", SCOPES));
		          
		        		Log.e("test", "accessToken:"+token);
				
				} catch (IOException transientEx) {
					// Network or server error, try later
					Log.e(TAG, transientEx.toString());
				} catch (UserRecoverableAuthException e) {
					// Recover (with e.getIntent())
					Log.e(TAG, e.toString());
					Intent recover = e.getIntent();
					startActivityForResult(recover, REQUEST_CODE_TOKEN_AUTH);
				} catch (GoogleAuthException authEx) {
					// The call is not ever expected to succeed
					// assuming you have already verified that 
					// Google Play services is installed.
					Log.e(TAG, authEx.toString());
				}

				return token;
			}

			@Override
			protected void onPostExecute(String token) {
				Log.e(TAG, "Access token retrieved:" + token);
				
				//若google登入資料＆token都拿到，可進行sinyiLogin動作
				if ((token!=null)&&(!token.equals(""))&&
					(!googleId.equals(""))&&(!googleAccount.equals(""))){
					
					sinyiOpenIdLoginAction(googleAccount, googleId, token, "g", "", null);
				}else{
					Log.e("test", "token,account,id not complete");
				}
				
			}

		};
		task.execute();
	}
	
	
	private void sinyiOpenIdLoginAction(
			final String account, final String openId, final String token, 
			final String from, final String field, final Bundle extraData){
		
		LoginService.getCookie(new LoginCallback(){
			@Override
			public void onResult(boolean success, String debugMessage) {
				if (success){
					
					LoginService.openidLogin(account, openId, token, from, field, extraData, new LoginCallback(){
						@Override
						public void onResult(boolean success, String debugMessage) {
							Log.e(TAG, "openIdLogin:"+success+" msg:"+debugMessage);
							if (success){
								//call isLogin update data
								loginStatusAction.updateTrackAndIsLoginAction();
							}else{
								//show toast
								showToast(debugMessage);
							}
						}
					});
				}else{
					showToast(debugMessage);
				}
				
			}
		});
		
	}
	
	//TODO:TEST_LOGIN_ACTION
//	private void updateTrackAndIsLoginAction(){
//		
//		int localFavCount = LoginInfo.getInstance().favJSONArray.length();
//		if (localFavCount==0){
//			//local沒有收藏物件，直接更新isLogin
//			updateIsLoginAction();
//		}else{
//			//local有收藏物件，先詢問是否合併
//			//若選是，先合併再isLogin
//			//若選否，直接isLogin
//			showFavMergeDialogAction();
//		}
//	}
	
	//TODO:TEST_LOGIN_ACTION
//	private void updateIsLoginAction(){
//		HashMap<String, Object> params = new HashMap<String, Object>();
//		params.put(BHConstants.PARAM_FAV, 1);
//		
//		LoginService.isLogin(params, new MemberCallback(){
//			@Override
//			public void onResult(boolean success,JSONObject memberJSONObject,
//					String debugMessage) {
//				if (success){
//					Log.e(TAG, "isLogin:"+success+" member:"+memberJSONObject.toString());
//					Log.e(TAG, "fav count:"+LoginInfo.getInstance().favJSONArray.length());
//					//check是否已有手機/Email資料
//					//若沒有，邀請使用者增加填寫手機＆email
//					updateUserDataAction();
//					finish();
//				}else{
//					showToast(getString(R.string.prompt_update_user_data_fail));
//				}
//			}
//		});
//	}
	
	//TODO:TEST_LOGIN_ACTION
//	private void showFavMergeDialogAction(){
//		int localFavCount = LoginInfo.getInstance().favJSONArray.length();
//		String msg = String.format(getString(R.string.prompt_merge_fav_house),localFavCount);
//		String status = "success"; 
//		
//		showYesNoDialog_TrackCollect_Login("登入成功！", msg, status, new OkOnClickListener(){
//			@Override
//			public void didClickOk() {
//				//TODO: yes, 要合併
//				String houseNOs = LoginInfo.getInstance().getFavHouseNOs();
//				if (!houseNOs.equals("")){
//
//					Log.e("test", "houseNOs:"+houseNOs);
//					TrackService.trackHouse(houseNOs, "", new TrackCallback(){
//						@Override
//						public void onResult(boolean success, String debugMessage) {
//							Log.e(TAG, "trackHouse:"+success+" msg:"+debugMessage);
//							//不管trackhouse的merge有沒有成功，都繼續進行登入後更新資料動作
//							updateIsLoginAction();
//						}
//					});
//				}else{
//					updateIsLoginAction();
//				}
//			}
//
//			@Override
//			public void didClickCancel() {
//				//TODO: no, 不合併
//				showConfirmDialogAction();
//			}
//		});
//	}
	
	//TODO:TEST_LOGIN_ACTION
//	private void showConfirmDialogAction(){
//		String status = "confirm";
//		this.showYesNoDialog_TrackCollect_Login("", getString(R.string.prompt_delete_fav), status,  new OkOnClickListener(){
//
//			@Override
//			public void didClickOk() {
//				//確定要刪除local favs，略過合併功能
//				updateIsLoginAction();
//			}
//
//			@Override
//			public void didClickCancel() {
//				//不直接刪除，退回上一步驟，詢問是否合併
//				showFavMergeDialogAction();
//			}
//		});
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
	
	/* Broadcast Receiver: for yahoo */
	private BroadcastReceiver yahooLoginBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("test", "LoginActivity: get broadcast receiver!");

			try {
				String action = intent.getAction();
				if(BHConstants.BROADCAST_LOGIN_YAHOO.equals(action)) {
					String yahooAccount = intent.getExtras().getString("email");
					String yahoooId = intent.getExtras().getString("id");
					String token = intent.getExtras().getString("token");

					sinyiOpenIdLoginAction(yahooAccount, yahoooId, token, "y", "", intent.getExtras());
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	};
}
