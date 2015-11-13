package com.yowoo.newbuyhouse.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.signpost.OAuthConsumer;
import com.parse.signpost.OAuthProvider;
import com.parse.signpost.commonshttp.CommonsHttpOAuthConsumer;
import com.parse.signpost.commonshttp.CommonsHttpOAuthProvider;
import com.parse.signpost.exception.OAuthCommunicationException;
import com.parse.signpost.exception.OAuthExpectationFailedException;
import com.parse.signpost.exception.OAuthMessageSignerException;
import com.parse.signpost.exception.OAuthNotAuthorizedException;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.BaseActivity;
import com.yowoo.newbuyhouse.house.HouseFilterActivity;

public class YahooLoginActivity extends BaseActivity {
	private static final String REQUEST_TOKEN_ENDPOINT_URL ="https://api.login.yahoo.com/oauth/v2/get_request_token";
	private static final String AUTHORIZE_WEBSITE_URL   ="https://api.login.yahoo.com/oauth/v2/request_auth";
	private static final String ACCESS_TOKEN_ENDPOINT_URL ="https://api.login.yahoo.com/oauth/v2/get_token";

	private String oAuthVerifier;

	CommonsHttpOAuthConsumer mainConsumer;
	CommonsHttpOAuthProvider mainProvider;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yahoo_login);

		this.setToolbarView(getString(R.string.yahoo_login));
		
		this.mainConsumer = new CommonsHttpOAuthConsumer(BHConstants.YAHOO_CONSUMER_KEY, BHConstants.YAHOO_CONSUMER_SECRET);
		this.mainProvider = new CommonsHttpOAuthProvider(REQUEST_TOKEN_ENDPOINT_URL, ACCESS_TOKEN_ENDPOINT_URL, AUTHORIZE_WEBSITE_URL);
		//this.mainConsumer.setSigningStrategy(new YahooAuthorizationHeaderSigningStrategy());

		// It turns out this was the missing thing to making standard Activity launch mode work
		//this.mainProvider.setOAuth10a(true);

		//get request token
		this.showProgressDialog(new OnCancelListener(){
			@Override
			public void onCancel(DialogInterface dialog) {
				showToast(getString(R.string.login_cancel));
				finish();
			}
		});
		new OAuthRequestTokenTask(getApplicationContext(),mainConsumer,mainProvider).execute();


	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.e("test", "onNewIntent");
		
		//當yahoo驗證頁面call callback url時，會開啟此activity, 進入此處執行
		Uri uriData = intent.getData();
		if (uriData != null && uriData.toString().startsWith(BHConstants.YAHOO_CALLBACK_URL)) {
			setVerifier(uriData.getQueryParameter("oauth_verifier"));
			//get verifier, go to get token
			new OAuthGetAccessTokenTask().execute();
		}
		super.onNewIntent(intent);
	}

	class OAuthRequestTokenTask extends AsyncTask<Void, Void, String> {

		final String TAG = getClass().getName();
		private Context context;
		private OAuthProvider provider;
		private OAuthConsumer consumer;

		public OAuthRequestTokenTask(Context context,OAuthConsumer consumer,OAuthProvider provider) {
			this.context = context;
			this.consumer = consumer;
			this.provider = provider;
		}

		@Override
		protected String doInBackground(Void... params) {

			try {
				Log.i(TAG, "Retrieving request token from Google servers");
				final String url = provider.retrieveRequestToken(consumer, BHConstants.YAHOO_CALLBACK_URL);
				Log.i(TAG, "Popping a browser with the authorize URL : " + url);
				
				return url;
			} catch (Exception e) {
				Log.e(TAG, "Error during OAUth retrieve request token", e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i(TAG, "onPostExecute result : " + result);
			super.onPostExecute(result);

			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
			startActivity(intent);

		} 
	}

	public class OAuthGetAccessTokenTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... arg0) {
			try {
				//oAuthVerifier = editText1.getText().toString();
				mainProvider.retrieveAccessToken(mainConsumer, oAuthVerifier);
				return  "ok";
			} catch (OAuthMessageSignerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthNotAuthorizedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			//super.onPostExecute(result);
			showToken();
			
			if (result!=null){
				getProfile();
			}else{
				showToast(getString(R.string.login_error_occured));
				finish();
			}
		}
	}

	public void setVerifier(String verifier)
	{
		this.oAuthVerifier = verifier;
		Log.d("setVerifier", verifier);

		this.showToken();
	}

	public void showToken()
	{
		String str = 
				"verifier = " + this.oAuthVerifier + "<br>" + 
						"Token = " + mainConsumer.getToken() + "<br>" + 
						"secret = " + mainConsumer.getTokenSecret() + "<br>" + 
						"oauth_expires_in = " + mainProvider.getResponseParameters().getFirst("oauth_expires_in") + "<br>" +
						"oauth_session_handle = " + mainProvider.getResponseParameters().getFirst("oauth_session_handle") + "<br>" +
						"oauth_authorization_expires_in = " + mainProvider.getResponseParameters().getFirst("oauth_authorization_expires_in") + "<br>" + 
						"xoauth_yahoo_guid = " + mainProvider.getResponseParameters().getFirst("xoauth_yahoo_guid") + "<br>";
		Log.i("YahooScreen", "str : " + str);
	}


	private void doGet(String url, final RequestCallback callback) {
		OAuthConsumer consumer = this.mainConsumer;

		final HttpGet request = new HttpGet(url);
		Log.i("doGet","Requesting URL : " + url);
		try {
			consumer.sign(request);
			Log.i("YahooScreen", "request url : " + request.getURI());
			new Thread(new Runnable() {

				@Override
				public void run() {
					DefaultHttpClient httpclient = new DefaultHttpClient();
					HttpResponse response;
					try {
						response = httpclient.execute((HttpUriRequest) request);
						Log.i("doGet","Statusline : " + response.getStatusLine());
						InputStream data = response.getEntity().getContent();
						BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(data));
						String responeLine;
						StringBuilder responseBuilder = new StringBuilder();
						while ((responeLine = bufferedReader.readLine()) != null) {
							responseBuilder.append(responeLine);
						}
						Log.i("doGet","Response : " + responseBuilder.toString());
						//return responseBuilder.toString();
						final String responseString = responseBuilder.toString();
						
						final Handler handler = new Handler(Looper.getMainLooper());
						handler.post(new Runnable() {
							public void run() {
								if (callback!=null){
									callback.onResult(true, responseString);
								}
							}
						});
						
						return;
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					//處理未成功的狀況
					if (callback!=null){
						callback.onResult(false, "");
					}
				}
			}).start();
			
			return;

		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
		
		//TODO: 處理未成功的狀況
		if (callback!=null){
			callback.onResult(false, "");
		}
	}

	public interface RequestCallback {
		public void onResult(boolean success, String response);
	}
	
	public void getProfile()
	{
		String guid = mainProvider.getResponseParameters().getFirst("xoauth_yahoo_guid");
		String url = "https://social.yahooapis.com/v1/user/" + guid + "/profile?format=json";
		
		this.doGet(url, new RequestCallback(){

			@Override
			public void onResult(boolean success, String response) {
				if (success){
					parseDataAndSendResult(response);
				}else{
					showToast(getString(R.string.login_error_occured));
					finish();
				}
			}
		});
	}
	
	private void parseDataAndSendResult(String response){
		try {
			JSONObject resObject = new JSONObject(response);
			JSONObject profileObject = resObject.getJSONObject("profile");
			String guid = profileObject.getString("guid");
			JSONArray emails = profileObject.getJSONArray("emails");
			
			if (emails.length()!=0){
				String email = "";
				for (int i=0; i<emails.length(); i++){
					email = emails.getJSONObject(i).getString("handle");
					if (emails.getJSONObject(i).has("primary")){
						if (emails.getJSONObject(i).getBoolean("primary")){
							break;
						}
					}
				}
				
				Intent intent = new Intent();
				intent.setAction(BHConstants.BROADCAST_LOGIN_YAHOO);
				intent.putExtra("email", email);
				intent.putExtra("id", guid);
				intent.putExtra("token", mainConsumer.getToken());
				intent.putExtra(BHConstants.PARAM_TOKEN_SECRET, mainConsumer.getTokenSecret());
				intent.putExtra(BHConstants.PARAM_COMSUMER_KEY, BHConstants.YAHOO_CONSUMER_KEY);
				intent.putExtra(BHConstants.PARAM_COMSUMER_SECRET, BHConstants.YAHOO_CONSUMER_SECRET);
				LocalBroadcastManager.getInstance(YahooLoginActivity.this).sendBroadcast(intent);
				
				finish();
				return;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		//TODO: 無法獲取email資料
		showToast(getString(R.string.login_error_no_email));
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.global, menu);
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
	
	@Override
	public void onPause(){
		super.onPause();
		
		Log.e("test", "YLoginActivity: onPause");
	}
	
	@Override
	public void onStop(){
		super.onStop();
		
		Log.e("test", "YLoginActivity: onStop");
	}
}