package com.yowoo.newbuyhouse.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;

import com.yowoo.newbuyhouse.news.News;
import com.yowoo.newbuyhouse.news.NewsConstants;
 
public class NewsDB {
    
	public static final String TABLE_NAME = "news";
 
    public static final String CREATE_TABLE = 
            "CREATE TABLE " + TABLE_NAME + " (" + 
            NewsConstants.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NewsConstants.NEWS_ID_COLUMN + " INTEGER NOT NULL, " + 
            NewsConstants.TITLE_COLUMN + " TEXT NOT NULL, " +
            NewsConstants.IMGSRC_COLUMN + " TEXT, " +
            NewsConstants.PMNAME_COLUMN + " TEXT, " +
            NewsConstants.START_DATETIME_COLUMN + " UNSIGNED BIGINT NOT NULL, " +
            NewsConstants.CREATE_DATETIME_COLUMN + " UNSIGNED BIGINT NOT NULL, " +
            //NewsConstants.TIMESTAMP_COLUMN + " INTEGER NOT NULL, " +
            NewsConstants.CONTENT_COLUMN + " TEXT)";
    
    //unique index for replace into
    public final static String[] CREATE_INDEX_SQL_ARRAY = { 
        "CREATE UNIQUE INDEX `news_id_index` ON News("+NewsConstants.NEWS_ID_COLUMN+");"};
    

    private SQLiteDatabase db;
    private Context context;
    
    /* Threads */
	public static HandlerThread dbHandlerThread;
	public static Handler dbHandler;
 
    public NewsDB(Context context) {
        db = DBOpenHelper.getDatabase(context);
        this.context = context;
        
        //db thread
        dbHandlerThread = new HandlerThread("DB_THREAD");
		dbHandlerThread.start();
		dbHandler = new Handler(dbHandlerThread.getLooper());
    }
 
    public void close() {
        db.close();
    }
    
    private void checkDB(){
    		if (db==null){
    			db = DBOpenHelper.getDatabase(this.context);
    		}
    }
 
    public News replace(News news) {
    		checkDB();
    		
        ContentValues cv = new ContentValues();     
        
        cv.put(NewsConstants.NEWS_ID_COLUMN, news.getID());
        cv.put(NewsConstants.TITLE_COLUMN, news.getTitle());
        cv.put(NewsConstants.IMGSRC_COLUMN, news.getImgSrc());
        cv.put(NewsConstants.PMNAME_COLUMN, news.getPmName());
        cv.put(NewsConstants.START_DATETIME_COLUMN, news.getStartDate().getTime());
        cv.put(NewsConstants.CREATE_DATETIME_COLUMN, news.getCreateDate().getTime());
        cv.put(NewsConstants.CONTENT_COLUMN, news.getContent());
 
        long id = db.replace(TABLE_NAME, null, cv);
 
        return news;
    }
    
    public void replaceAllNews(final ArrayList<News> newsList){
    		checkDB();
    	
    		dbHandler.post(new Runnable(){
    			@Override
    			public void run() {
    				for (int i=0; i<newsList.size(); i++){
    					replace(newsList.get(i));
    				}
    			}
    		});
    }
 
//    public boolean delete(long id){
//        String where = KEY_ID + "=" + id;
//        return db.delete(TABLE_NAME, where , null) > 0;
//    }
 
    public ArrayList<News> getNews(int offset, int fetchCount) {
    		checkDB();
    	
        ArrayList<News> result = new ArrayList<News>();
        Cursor cursor = db.rawQuery(
        			"SELECT * FROM "+TABLE_NAME+" "+
        			"ORDER BY "+NewsConstants.START_DATETIME_COLUMN+" DESC LIMIT ?, ?",
				new String[]{Integer.toString(offset), Integer.toString(fetchCount)});
 
        while (cursor.moveToNext()) {
            result.add(cursorToNewsObject(cursor));
        }
 
        cursor.close();
        return result;
    }
    
    public News getSingleNews(String newsId){
    		checkDB();
    	
    		News news = null;
    		Cursor cursor = db.rawQuery(
    			"SELECT * FROM "+TABLE_NAME+" "+
    			"WHERE "+NewsConstants.NEWS_ID_COLUMN+"=? "+
    			"ORDER BY "+NewsConstants.START_DATETIME_COLUMN+" DESC",
			new String[]{newsId});
    		
    		if (cursor.getCount()>0){
    			cursor.moveToFirst();
    			news = cursorToNewsObject(cursor);
    		}
    		
    		return news;
    }
    
    public int updateSingleNewsContent(String newsId, String content){
    		checkDB();
    	
    		ContentValues cv = new ContentValues();
        cv.put(NewsConstants.CONTENT_COLUMN, content);
    		return db.update(TABLE_NAME, cv, NewsConstants.NEWS_ID_COLUMN+"="+newsId, null);
    }
    
    public static News cursorToNewsObject(Cursor c) {
	
    		String ID = c.getString(c.getColumnIndex(NewsConstants.NEWS_ID_COLUMN));
    		String title = c.getString(c.getColumnIndex(NewsConstants.TITLE_COLUMN));
		String imgUrl = c.getString(c.getColumnIndex(NewsConstants.IMGSRC_COLUMN));
		String pmName = c.getString(c.getColumnIndex(NewsConstants.PMNAME_COLUMN));
		long startDatetime = c.getLong(c.getColumnIndex(NewsConstants.START_DATETIME_COLUMN));
		long ceateDatetime = c.getInt(c.getColumnIndex(NewsConstants.CREATE_DATETIME_COLUMN));
		String content = c.getString(c.getColumnIndex(NewsConstants.CONTENT_COLUMN));
			
		News n = new News(ID, title, imgUrl, pmName, startDatetime, ceateDatetime, content);
		
		return n;
	}
 
    
    public int getCount() {
    		checkDB();
    	
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
 
        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
 
        return result;
    }
 
 
}