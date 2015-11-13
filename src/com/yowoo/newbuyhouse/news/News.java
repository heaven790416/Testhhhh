package com.yowoo.newbuyhouse.news;



import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.apache.http.impl.cookie.DateParseException;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.yowoo.newbuyhouse.util.CommonUtils;


public class News implements Serializable {

	private static final long serialVersionUID = 4607210446681744168L;

	public String ID="";
	public String title="";
	private String imgSrc="";
	private String pmName="";
	private Date startDate=new Date();
	private Date createDate=new Date();
	private int timestamp = 0;
	private String content = "";
	
	public News(JSONObject newsObject) throws JSONException, DateParseException,
			ParseException {
		super();
		this.ID = newsObject.has(NewsConstants.NEWS_ID_COLUMN) ? 
				newsObject.getString(NewsConstants.NEWS_ID_COLUMN) : "";
		this.title = newsObject.has(NewsConstants.TITLE_COLUMN) ? 
				newsObject.getString(NewsConstants.TITLE_COLUMN) : "";
		this.imgSrc = newsObject.has(NewsConstants.IMGSRC_COLUMN) ? 
				newsObject.getString(NewsConstants.IMGSRC_COLUMN) : "";
		this.pmName = newsObject.has(NewsConstants.PMNAME_COLUMN) ? 
						newsObject.getString(NewsConstants.PMNAME_COLUMN) : "";
				
		this.startDate = newsObject.has(NewsConstants.START_DATETIME_COLUMN) ? 
				CommonUtils.parseCommonDate(newsObject.getString(NewsConstants.START_DATETIME_COLUMN)) : new Date();

		this.createDate = newsObject.has(NewsConstants.CREATE_DATETIME_COLUMN) ? 
				CommonUtils.parseCommonDate(newsObject.getString(NewsConstants.CREATE_DATETIME_COLUMN)) : new Date();

		this.content = newsObject.has(NewsConstants.CONTENT_COLUMN)? 
				newsObject.getString(NewsConstants.CONTENT_COLUMN) : "";
		
		//Log.e("test", "news: "+ title +"|"+ID);
	}
	
	public News(String ID, String title, String imgSrc, String pmName, long startTimestamp, long createTimestamp, String content){
		super();
		
		this.ID = (ID==null)? "" : ID;
		this.title = title;
		this.imgSrc = (imgSrc==null)? "" : imgSrc;
		this.pmName = (pmName==null)? "" : pmName;
		this.startDate = new Date(startTimestamp);
		this.createDate = new Date(createTimestamp);
		this.content = (content==null)? "" : content;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getID() {
		return ID;
	}

	public String getTitle() {
		return title;
	}

	public String getImgSrc() {
		return imgSrc;
	}
	
	public String getPmName() {
		return pmName;
	}
	
	public Boolean hasImgUrl(){
		if (this.imgSrc==null) return false;
		if (this.imgSrc.equals("")) return false;
		return true;
	}

	public Date getStartDate() {
		return startDate;
	}
	
	public Date getCreateDate() {
		return createDate;
	}
	
	
	public String getStartDateDisplay(){
		return CommonUtils.timestampToDateString((int)startDate.getTime());
	} 
	
	public String getContent(){
		if (content.equals("null")) content = "";
		return content;
	}
	
	public Boolean hasContent(){
		if (this.content==null) return false;
		if (this.content.equals("")) return false;
		return true;
	}
	
	
	

}
