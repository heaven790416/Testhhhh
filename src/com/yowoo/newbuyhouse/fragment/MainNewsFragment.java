package com.yowoo.newbuyhouse.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.BHConstants;
import com.yowoo.newbuyhouse.Constants;
import com.yowoo.newbuyhouse.MainActivity;
import com.yowoo.newbuyhouse.Singleton;
import com.yowoo.newbuyhouse.network.NewsService;
import com.yowoo.newbuyhouse.network.NewsService.NewsListCallback;
import com.yowoo.newbuyhouse.news.News;
import com.yowoo.newbuyhouse.news.NewsConstants;
import com.yowoo.newbuyhouse.news.NewsDetailActivity;
import com.yowoo.newbuyhouse.view.NewsRow;

public class MainNewsFragment extends MyFragment{

	public static Fragment newInstance(Context context) {
		MainNewsFragment f = new MainNewsFragment();
 
        return f;
    }
	
 
	PullToRefreshListView newsListView;
	ArrayList<News> newsList = new ArrayList<News>();
	
	Activity activity;
	private int dataFetchStatus = Constants.DATA_FETCH_FROM_SERVER;
	
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_news, null);
        
        //set views
        newsListView = (PullToRefreshListView) rootView.findViewById(R.id.newsListview);
        newsListView.setMode(Mode.PULL_FROM_START);
        newsListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
	            	Log.e("test", "currentMode: "+ newsListView.getCurrentMode().toString());
	            	updateDataStatusByNetwork();
	            	fetchDataDependOnNetwork(true);

            }
            
        });
        
        newsListView.setAdapter(newsAdapter);
        if (newsAdapter==null){
        		Log.e("test", "newsAdapter==null!!");
        }
        
        
        //fetch Data
        updateDataStatusByNetwork();
		fetchDataDependOnNetwork(true);
        
        
        return rootView;
    }
    
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		this.activity = activity;
		((MainActivity) activity).onSectionAttached(getArguments().getInt(Constants.FRAGMENT_ARG_POSITION));
	}
    
    @Override
	public void onResume() {
		super.onResume();
		
		
    }
    
    
    
    private void updateDataStatusByNetwork() {

		// make sure singleton is exist
		Singleton.getInstance(getActivity());

		// Detect network status
		if (Singleton.isNetworkAvailable()) {
			dataFetchStatus = Constants.DATA_FETCH_FROM_SERVER;
		} else {
			dataFetchStatus = Constants.DATA_FETCH_FROM_DATABASE;
			showActivityToast(R.string.no_network_please_check);
		}
	}
    
    private void fetchDataDependOnNetwork(Boolean isRefresh){
    		if (dataFetchStatus == Constants.DATA_FETCH_FROM_SERVER) {
    			fetchDataFromServer(isRefresh);
    		} else {
    			retrieveDataFromDatabase(isRefresh);
    		}
    }
    
    private void fetchDataFromServer(final Boolean isRefresh){
    		
    		//信義api是用page來抓, page從1開始...
    		int page = (isRefresh)? 1 : ((newsList.size()/NewsConstants.FETCH_NEWS_COUNT)+1);
    	
    		NewsService.getNewsList(page, NewsConstants.FETCH_NEWS_COUNT, new NewsListCallback(){

    			@Override
    			public void onResult(boolean success, ArrayList<News> newsArray) {
    				if (newsListView != null) {
            			Log.e("test", "onRefreshComplete!");
            			newsListView.onRefreshComplete();
    				}
    				
    				if (success){
    					Log.e("test","get news success!");
    					if (isRefresh)  newsList.clear();
    					newsList.addAll(newsArray);
    					newsAdapter.notifyDataSetChanged();
    					
    					//store to local cache
    					storeDataToDatabase(newsList);
    				}else{
    					Log.e("test","get news fail!");
    				}
    			}
    		});
    		
    		
    	
    }
    
    private void storeDataToDatabase(ArrayList<News> newsList){
    		if (newsList.size()==0){
    			return;
    		}
    	
    		Singleton.log("store data from database.......");
		Singleton.newsDB.replaceAllNews(newsList);
    }
    
    private void retrieveDataFromDatabase(final Boolean isRefresh) {
		Singleton.log("retrieve data from database.......");
		
		int start = (isRefresh) ? 0 : newsList.size();
		if (isRefresh){
			newsList.clear();
		}
		
		newsList.addAll(Singleton.newsDB.getNews(start, NewsConstants.FETCH_NEWS_COUNT));
		
		newsAdapter.notifyDataSetChanged();
		Singleton.log("newsList count: "+ newsList.size() +" finish retrieve data from database.......");
		
		if (getActivity()!=null){
			new Handler(getActivity().getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					newsListView.onRefreshComplete();
				}
			});
		}
	}
    
    
    
    BaseAdapter newsAdapter = new BaseAdapter() {
		@Override
		public int getCount() {
			return newsList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View rowView, ViewGroup parent) {
			
			if(rowView==null||rowView.getClass().equals(NewsRow.class)==false) {
				rowView = new NewsRow(getActivity());
			}

			//chech if need loadmore
			if (position==newsList.size()-3){
				//check是否已經沒有data
		    		if (newsList.size()%NewsConstants.FETCH_NEWS_COUNT==0){
		    			//可能還有
		    			fetchDataDependOnNetwork(false);
		    		}
			}
			
			NewsRow newsRow = (NewsRow) rowView;
			final News news = newsList.get(position);
			newsRow.reloadCell(news);
			
			Log.e("test", "reload: pos:"+position);
			
			newsRow.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					//get newsIds from newsList
					ArrayList<String> newsIdArray = new ArrayList<String>();
					for (int i=0; i<newsList.size(); i++){
						newsIdArray.add(newsList.get(i).getID());
					}
					
					Intent intent = new Intent().setClass(getActivity(), NewsDetailActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra(BHConstants.EXTRA_NEWSID_ARRAY, newsIdArray);
					intent.putExtra(BHConstants.EXTRA_NEWS_POSITION, position);
					
					startActivity(intent);
					
					
				}
			});
			
			return rowView;
		}
	};
    
    
}
