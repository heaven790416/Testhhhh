package com.yowoo.newbuyhouse.network;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.james.easyinternet.RequestHTTP;
import com.james.easyinternet.RequestHTTP.OnExceptionListener;
import com.yowoo.newbuyhouse.Constants;
import com.yowoo.newbuyhouse.Singleton;

public class ConnectService {

	public interface RequestCallback {
		public void onResult(boolean success, HashMap<String, Object> data);
	}
	
	public interface HttpPostRequestDelegate {
		public void didGetResponse(String url, String response, Header[] headers);
	}
	
	public interface HttpGetRequestDelegate {
		public void didGetResponse(String url, String response);
	}
	
	//post
//	public static void sendPostRequest(final String url, final JSONObject jsonObject, final HttpRequestDelegate delegate) {
//		sendPostRequest(url, jsonObject, null, delegate);
//	}
	
	//Post
//	public static void sendPostRequest(final String url, final JSONObject jsonObject, final HashMap<String,String> headers, final HttpRequestDelegate delegate) {
//		final Handler handler = new Handler(Looper.getMainLooper());
//
//		new Thread(new Runnable() {
//			public void run() {
//				// Create a new HttpClient and Post Header
//				final HttpParams httpParams = new BasicHttpParams();
//				HttpConnectionParams.setConnectionTimeout(httpParams, Constants.HTTP_CONNECTION_TIMEOUT);
//				HttpConnectionParams.setSoTimeout(httpParams, Constants.HTTP_CONNECTION_TIMEOUT);
//				HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
//				HttpProtocolParams.setHttpElementCharset(httpParams, HTTP.UTF_8);
//				httpParams.setBooleanParameter("http.protocol.expect-continue", false);
//
//				HttpClient httpclient = new DefaultHttpClient(httpParams);
//				HttpPost httppost = new HttpPost(url);
//				
//				//set headers
//				//httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
//				httppost.setHeader("Content-Type", "application/json");
//				//httppost.setHeader("X-Parse-Application-Id", Constants.PARSE_APP_ID);
//				//httppost.setHeader("X-Parse-REST-API-Key", Constants.PARSE_REST_KEY);
//				
//				if (headers != null){
//					for(Entry<String, String> entry : headers.entrySet()) {
//					    String key = entry.getKey();
//					    String value = entry.getValue();
//					    httppost.setHeader(key, value);
//					}
//				}
//				httpclient.getParams().setParameter("http.protocol.content-charset", HTTP.UTF_8);
//				httpclient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
//
//				try {
//
//					//passes the results to a string builder/entity
//					StringEntity se = new StringEntity(jsonObject.toString(), HTTP.UTF_8);
//
//					//sets the post request as the resulting string
//					httppost.setEntity(se);
//					Singleton.log("SEND POST REQUEST: " + url);
//					Singleton.log("params: "+jsonObject.toString());
//
//					// Execute HTTP Post Request
//					HttpResponse httpResponse = httpclient.execute(httppost);
//					HttpEntity entity = httpResponse.getEntity();
//					int statusCode = httpResponse.getStatusLine().getStatusCode();
//
//					if(statusCode!=200 && statusCode!=201 && statusCode!=304) {
//						Singleton.log("statusCode: "+statusCode);
//						throw new Exception("SERVER RESPONSE ERROR");
//					}
//
//					if (entity != null) {
//						InputStream instream = entity.getContent();
//
//						final String response = convertStreamToString(instream);
//
//						handler.post(new Runnable() {
//							public void run() {
//								Singleton.log("Received: " + response);
//								delegate.didGetResponse(url, response);
//							}
//						});
//					} else {
//						handler.post(new Runnable() {
//							public void run() {
//								delegate.didGetResponse(url, null);
//							}
//						});
//					}
//				} catch (Exception e) {
//					Singleton.log("POST REQUEST EXCEPTION: " + e.toString());
//
//					handler.post(new Runnable() {
//						public void run() {
//							try {
//								delegate.didGetResponse(url, null);
//							} catch (Exception e) {
//
//							}
//						}
//					});
//				}
//			}
//		}).start();
//	}
	
	public static void sendPostRequest(final String url, final HashMap<String, Object> params, final HttpPostRequestDelegate delegate) {
		HashMap<String, String> headers = new HashMap<String, String>();
		sendPostRequest(url, headers, params, delegate);
	}
	
	public static void sendPostRequest(final String url, final HashMap<String, String> headers, final HashMap<String, Object> params, final HttpPostRequestDelegate delegate) {
		final Handler handler = new Handler(Looper.getMainLooper());

		new Thread(new Runnable() {
			public void run() {
				// Create a new HttpClient and Post Header
				final HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, Constants.HTTP_CONNECTION_TIMEOUT);
				HttpConnectionParams.setSoTimeout(httpParams, Constants.HTTP_CONNECTION_TIMEOUT);
				HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
				HttpProtocolParams.setHttpElementCharset(httpParams, HTTP.UTF_8);
				httpParams.setBooleanParameter("http.protocol.expect-continue", false);

				HttpClient httpclient = new DefaultHttpClient(httpParams);
				HttpPost httppost = new HttpPost(url);
				httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
				//httppost.setHeader("Content-Type", "application/json");
				httpclient.getParams().setParameter("http.protocol.content-charset", HTTP.UTF_8);
				httpclient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);

				//Set Extra Headers
				for(Entry<String, String> entry : headers.entrySet()) {
				    String key = entry.getKey();
				    String value = entry.getValue();
				    httppost.setHeader(key, value);
				}
				
				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

					Iterator it = params.entrySet().iterator();

					while (it.hasNext()) {
						try {
							Map.Entry pairs = (Map.Entry)it.next();
							String key = pairs.getKey().toString();
							String value = pairs.getValue().toString();

							nameValuePairs.add(new BasicNameValuePair(key, value));
							it.remove(); // avoids a ConcurrentModificationException
							Singleton.log(key+":" + value);
						} catch(Exception e) {

						}
					}

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

					Singleton.log("SEND POST REQUEST: " + url);

					// Execute HTTP Post Request
					HttpResponse httpResponse = httpclient.execute(httppost);
					HttpEntity entity = httpResponse.getEntity();
					int statusCode = httpResponse.getStatusLine().getStatusCode();
					
					//get headers 
					final Header[] headers = httpResponse.getAllHeaders();
					
					if(statusCode!=200&&statusCode!=304) {
						throw new Exception("SERVER RESPONSE ERROR");
					}

					if (entity != null) {
						InputStream instream = entity.getContent();

						final String response = convertStreamToString(instream);

						handler.post(new Runnable() {
							public void run() {
								Singleton.log("Received: " + response);
								delegate.didGetResponse(url, response, headers);
							}
						});
					} else {
						handler.post(new Runnable() {
							public void run() {
								delegate.didGetResponse(url, null, null);
							}
						});
					}
				} catch (Exception e) {
					Singleton.log("POST REQUEST EXCEPTION: " + e.toString());

					handler.post(new Runnable() {
						public void run() {
							try {
								delegate.didGetResponse(url, null, null);
							} catch (Exception e) {
							}
						}
					});
				}
			}
		}).start();
	}
	
	//Get
	public static void sendGetRequest(final String url, final HashMap<String, Object> params, final HttpGetRequestDelegate delegate) {
		final Handler handler = new Handler(Looper.getMainLooper());

		new Thread(new Runnable() {
			public void run() {
				try {
					String urlWithParams = url + "?";

					Iterator it = params.entrySet().iterator();

					while (it.hasNext()) {
						try {
							Map.Entry pairs = (Map.Entry)it.next();
							String key = pairs.getKey().toString();
							String value = pairs.getValue().toString();

							urlWithParams = urlWithParams + key + "=" + value + "&";
							it.remove(); // avoids a ConcurrentModificationException
						} catch (Exception e) {

						}
					}

					urlWithParams = urlWithParams.substring(0, urlWithParams.length()-1);

					Singleton.log("SEND GET REQUEST: " + urlWithParams);

					URL URLRequest = new URL(urlWithParams);
					HttpURLConnection conn = (HttpURLConnection) URLRequest.openConnection();
					conn.setReadTimeout(Constants.HTTP_CONNECTION_TIMEOUT);

					int statusCode = conn.getResponseCode();

					if(statusCode!=200&&statusCode!=304) {
						throw new Exception("SERVER RESPONSE ERROR");
					}

					final String finalResponse = convertStreamToString(conn.getInputStream());

					handler.post(new Runnable() {
						public void run() {
							Singleton.log("Received: " + finalResponse);
							delegate.didGetResponse(url, finalResponse);
						}
					});
				} catch (Exception e) {
					Singleton.log("GET REQUEST EXCEPTION: " + e.toString());

					handler.post(new Runnable() {
						public void run() {
							try {
								delegate.didGetResponse(url, null);
							} catch (Exception e) {

							}
						}
					});
				}
			}
		}).start();
	}
	
	/* helper method */
	private static String convertStreamToString(InputStream is) {
		try {
			if (is != null) {
				Writer writer = new StringWriter();

				char[] buffer = new char[1024];
				try {
					Reader reader = new BufferedReader(
							new InputStreamReader(is, "UTF-8"));
					int n;

					while ((n = reader.read(buffer)) != -1) {
						writer.write(buffer, 0, n);
					}
				} finally {
					is.close();
				}
				return writer.toString();
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	/* Test: It's work, but not used */
	public static void callNewApi(final String url, final ArrayList<NameValuePair> params){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String response = RequestHTTP.httpPost(url, params, new OnExceptionListener(){

					@Override
					public void onError(Exception e) {
						e.printStackTrace();
//						Message msg = new Message();
//						msg.what = API_FAIL;
//						msg.obj = e.toString();
						//callbackHandler.sendMessage(msg);
					}
				});
				if(response == null) return;
				Log.e("test", "callNewApi response: "+response);
				//handleNewResponse(response);
			}
		}).start();
	}
	
}
