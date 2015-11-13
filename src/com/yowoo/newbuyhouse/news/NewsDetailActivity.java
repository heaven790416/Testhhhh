package com.yowoo.newbuyhouse.news;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.BaseActivity;
import com.yowoo.newbuyhouse.Singleton;
import com.yowoo.newbuyhouse.network.NewsService;
import com.yowoo.newbuyhouse.network.NewsService.NewsListCallback;
import com.yowoo.newbuyhouse.network.NewsService.SingleNewsCallback;

public class NewsDetailActivity extends BaseActivity{

	ArrayList<String> newsIdArray = new ArrayList<String>();
	int currentNewsPosition = 0;
	News currentNews;

	WebView webView;
	RelativeLayout leftContainer, rightContainer;
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_news_detail);

		setToolbarView(getString(R.string.house_news));

		//Get Extra
		Bundle extra = getIntent().getExtras();
		newsIdArray = extra.getStringArrayList(BHConstants.EXTRA_NEWSID_ARRAY);
		currentNewsPosition = extra.getInt(BHConstants.EXTRA_NEWS_POSITION);

		setViews();

		//reloadViews();

		setListener();

		getSingleNews(newsIdArray.get(currentNewsPosition));
	}

	@Override
	protected void onResume(){
		super.onResume();
	}


	private void setViews(){
		webView = (WebView)findViewById(R.id.webView);
		webView.setWebViewClient(mWebViewClient);
		webView.getSettings().setJavaScriptEnabled(true);

        leftContainer = (RelativeLayout)findViewById(R.id.leftContainer);
        rightContainer = (RelativeLayout)findViewById(R.id.rightContainer);
		
	}

	WebViewClient mWebViewClient = new WebViewClient() {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	};


	private void setListener(){

		//瀏覽更新的文章
		leftContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("test", "press left");
				if (currentNewsPosition==0){
					showToast(R.string.no_more_lastest_news);
					return;
				}
				Log.e("test", "currentNewsPosition1:"+currentNewsPosition);
				getSingleNews(newsIdArray.get(--currentNewsPosition));
				Log.e("test", "currentNewsPosition2:"+currentNewsPosition);
			}
		});

		//瀏覽更舊的文章
		rightContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("test", "press right");
				if (currentNewsPosition < newsIdArray.size()-1){
					getSingleNews(newsIdArray.get(++currentNewsPosition));
					return;
				}


				//需要去server上撈更早之前的newsIds
				//計算page
				final int page = ((newsIdArray.size()/NewsConstants.FETCH_NEWS_COUNT)+1);
		    		Log.e("test", "ready to get page:"+page);
				
				NewsService.getNewsList(page, NewsConstants.FETCH_NEWS_COUNT, new NewsListCallback(){
					@Override
					public void onResult(boolean success, ArrayList<News> data) {
						Log.e("test", "get news success, page:"+page+" size:"+data.size());
						if (success){
							if (data.size()==0){
								//已無更舊資料
								showToast(R.string.no_more_older_news);
								return;
							}

							//有抓到更早新聞
							//先存入db
							Singleton.newsDB.replaceAllNews(data);

							//處理成Id，放入data
							ArrayList<String> moreNewsIds = new ArrayList<String>();
							for (int i=0; i<data.size(); i++){
								moreNewsIds.add(data.get(i).getID());
							}
							newsIdArray.addAll(moreNewsIds);

							//抓取上一則顯示出來
							getSingleNews(newsIdArray.get(++currentNewsPosition));
						}else{
							Log.e("test", "no netword!");
							showToast(R.string.no_network_please_check);
						}
					}
				});
			}
		});
	}

	private void displayNews(){
		if (currentNews == null) return;

		String headHtml = "<html><body  style='padding:20px'>";
		
		//Set Title
		String title = currentNews.getTitle();
		String titleHtml = "<strong><font color='#5cc790' size='5'>"+title+"</font></strong><br/>";
		
		//Set date
		String dateString = Singleton.news_DateFormatter.format(currentNews.getStartDate());
		String pmName = currentNews.getPmName();
		if (!pmName.equals("null")){
			dateString += " | "+pmName;
		}
		String dateHtml = "<p><font color='#c6c6c6' size='2.5'>"+dateString+"</font></p><br/>";
		
		
		String conHtml = "<p style='line-height: 130%'>"+currentNews.getContent()+"</p>";
		
		String bottomHtml = "</body></html>";
		
		String totalHtml = headHtml+titleHtml+dateHtml+currentNews.getContent()+bottomHtml;
		Log.e("test", totalHtml);
		
		
		webView.loadDataWithBaseURL("", totalHtml, "text/html", "UTF-8", "");
		

	}

	private void getSingleNews(String newsId){

		//先從db撈取單筆資料看看
		currentNews = getSingleNewsFromDB(newsId);
		if ((currentNews != null) && (currentNews.hasContent())){
			Log.e("test", "get Content from DB");
			displayNews();
			return;
		}

		//如果DB沒有單筆完整資料，從網路撈取 //TODO:目前無法通！
		getSingleNewsFromServer(newsId, new SingleNewsCallback(){
			@Override
			public void onResult(boolean success, News data) {
				if (success){
					Log.e("test", "getSingleNews success!");
					currentNews = data;
					displayNews();
					updateSingleNewsContent(currentNews);
				}else{
					Log.e("test", "getSingleNews fail!");
					showToast(R.string.no_network_please_check);
					displayNews();
				}
			}
		});
	}

	private News getSingleNewsFromDB(String newsId){
		return Singleton.newsDB.getSingleNews(newsId);
	}

	private void getSingleNewsFromServer(String newsId, SingleNewsCallback callback){
		NewsService.getNewsDetail(newsId, callback);
	}

	private void updateSingleNewsContent(News news){
		int count = Singleton.newsDB.updateSingleNewsContent(news.getID(), news.getContent());
		Log.e("test", "update news content success! count:"+count);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		return super.onCreateOptionsMenu(menu);
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
