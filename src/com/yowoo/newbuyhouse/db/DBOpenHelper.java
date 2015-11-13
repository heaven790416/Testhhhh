package com.yowoo.newbuyhouse.db;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.yowoo.newbuyhouse.news.NewsConstants;

public class DBOpenHelper extends SQLiteOpenHelper {
	 
    public static final String DATABASE_NAME = NewsConstants.NEWS_DATABASE_NAME;
    public static final int VERSION = 1;    
    private static SQLiteDatabase database;
 
    public DBOpenHelper(Context context, String name, CursorFactory factory,
            int version) {
        super(context, name, factory, version);
    }

    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new DBOpenHelper(context, DATABASE_NAME, 
                    null, VERSION).getWritableDatabase();
        }
        
        return database;
    }
    
    public static void dropDatabase() {
    		
    		if (database!=null){
    			Log.e("test", "dbPath: "+database.getPath());
    			//dbpath example: /data/data/com.sinyi.community/databases/community.db
    		}

    		try {
			if (database!=null){
				database.close();
			}
			
			File dbFile = new File(database.getPath());
			dbFile.delete();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database = null;
		}
	}
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create db
    		db.execSQL(NewsDB.CREATE_TABLE);
        
    		//add unique index
    		for (int i = 0; i < NewsDB.CREATE_INDEX_SQL_ARRAY.length; i++) {
    			db.execSQL(NewsDB.CREATE_INDEX_SQL_ARRAY[i]);
    		}
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    		//刪除舊版表格	
    		db.execSQL("DROP TABLE IF EXISTS " + NewsDB.TABLE_NAME);
        //建立新版表格
    		onCreate(db);
    }
 
}