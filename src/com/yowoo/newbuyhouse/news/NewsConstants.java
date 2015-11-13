package com.yowoo.newbuyhouse.news;

public class NewsConstants {

	public static final String NEWS_DATABASE_NAME = "news.db";
	
	public static final String KEY_ID = "_id";
    public static final String NEWS_ID_COLUMN = "ID";
    public static final String TITLE_COLUMN = "title";
    public static final String IMGSRC_COLUMN = "imgSrc";
    public static final String PMNAME_COLUMN = "pmName";
    public static final String START_DATETIME_COLUMN = "startDatetime";
    public static final String CREATE_DATETIME_COLUMN = "createDatetime";
    public static final String CONTENT_COLUMN = "conHtml";
    
    public static String[] NEWS_OBJECT_KEYS = {
    		NEWS_ID_COLUMN, 
    		TITLE_COLUMN, 
    		IMGSRC_COLUMN,
    		PMNAME_COLUMN,
    		START_DATETIME_COLUMN,
    		CREATE_DATETIME_COLUMN,
    		CONTENT_COLUMN
    	};
	
	public static final String JSON_VALUE_OK = "ok";
	public static final String JSON_KEY_STATUS = "status";
	public static final String JSON_KEY_NEWS = "news";
	
	public static final int FETCH_NEWS_COUNT = 20;
	
	
}
