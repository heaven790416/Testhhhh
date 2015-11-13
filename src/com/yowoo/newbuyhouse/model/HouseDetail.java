package com.yowoo.newbuyhouse.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.yowoo.newbuyhouse.BHConstants;
import com.thinkermobile.sinyi.R;
import com.yowoo.newbuyhouse.Singleton;

public class HouseDetail {

	public String NO = "";
	public String name="";
	public String imgDefault = "";
	public int price = 0;
	public int priceFirst = 0;
	public float discount = 0.0f;
	public String type = "";
	public String address = "";
	public float lat = 0.0f;
	public float lng = 0.0f;
	
	public float areaBuilding = 0.0f;//建坪
	public String layout = "";//幾廳幾房
	public float age = 0.0f;//屋齡
	
	//for detail
	public ArrayList<String> bigImg = new ArrayList<String>();//大圖
	public String layoutImg = "";
	public ArrayList<String> description = new ArrayList<String>();
	public String community = "";//社區名稱
	public float areaLand = 0.0f;//地坪
	public JSONArray pingDetail = new JSONArray();//主建物.雨遮.防空避難...
	public float areaPublic = 0.0f;//公設坪數
	public float areaGarage = 0.0f;//車位
	
	public String floor = "";//出售樓層/總樓層
	public String family = "";//每層戶數
	public int lift = 0;//電梯數
	public String houseFront = "";//物件朝向
	public String buildingFront="";//大樓朝向
	public String windowFront = "";//落地窗朝向
	public String security = "";//警衛管理
	public int monthlyFee = 0;//月管理費
	
	public int sfside = 0;//邊間 TODO:不確定型別
	public int sfdarkroom = 0;//暗房 TODO：不確定型別
	public String buildingStructure = "";//建物材質
	public String wallStructure = "";//外牆結構
	public String parking = "";//車位
	
	//生活機能
	public String primarySchool = "";//鄰近小學
	public String juniorSchool = "";//鄰近中學
	public String market = "";//鄰近市場
	public String garden = "";//鄰近公園
	public JSONArray MRTInfo = new JSONArray();//捷運資訊
	
	//物件地圖
	public String mapUrl = "";
	
	//業務聯絡
	public String store = "";//所屬分店
	public String storetel = "";//電話
	public String storeAddress = "";//住址
	
	//VR虛擬實境
	public String vr = "1";//互動看屋 0:無 1:有
	public String vrCom = "";//社區公設編號
	
	//NO,name,age,floor,family,lift,description,address,
	//wallStructure,price,priceFirst,discount,areaPublic,
	//areaBuilding,pingDetail,areaLand,ues,type,layout,
	//imgDefault,bigImg,areaGarage,VRScene,VRScene,store,
	//buildingStructure,MRTInfo,imgCount,vr,vrCom,communityNO,
	//storeAddress,security,monthlyFee,storetel,sfside,sfdarkroom,
	//inc,parking,primarySchool,juniorSchool,market,garden,layoutImg,
	//lng,lat,community,roomplus,hallplus,bathroomplus,openroomplus,
	//houseFront,buildingFront,windowFront
	
	public HouseDetail(){
		super();
	}
	
	public HouseDetail(JSONObject houseObject) throws JSONException{
		super();

		this.NO = houseObject.getString(BHConstants.JSON_KEY_NO);
		
		this.name = houseObject.getString(BHConstants.JSON_KEY_NAME);

		try{
			this.imgDefault = houseObject.getString(BHConstants.JSON_KEY_IMG_DEFAULT);
		}catch(Exception e){}
		
		this.price = houseObject.getInt(BHConstants.JSON_KEY_PRICE);
		
		try{
			this.priceFirst = houseObject.getInt(BHConstants.JSON_KEY_PRICE_FIRST);
		}catch(Exception e){}
		
		try{
			this.discount = (float) houseObject.getDouble(BHConstants.JSON_KEY_DISCOUNT);
		}catch(Exception e){}
		
		try{
			this.type = houseObject.getString(BHConstants.JSON_KEY_TYPE);
		}catch(Exception e){}
		
		try{
			this.address = houseObject.getString(BHConstants.JSON_KEY_ADDRESS);
		}catch(Exception e){}
		
		try{
			this.lat = (float) houseObject.getDouble(BHConstants.JSON_KEY_LAT);
		}catch(Exception e){}
		
		try{
			this.lng = (float) houseObject.getDouble(BHConstants.JSON_KEY_LNG);
		}catch(Exception e){}
		
		
		//For house list row
		try{
			this.areaBuilding = (float) houseObject.getDouble(BHConstants.JSON_KEY_AREA_BUILDING);
		}catch(Exception e){}
		
		try{
			this.layout = houseObject.getString(BHConstants.JSON_KEY_LAYOUT);
		}catch(Exception e){}
		
		try{
			this.age = (float) houseObject.getDouble(BHConstants.JSON_KEY_AGE);
		}catch(Exception e){}
		
		//for detail
		try{
			JSONArray bigImgJSONArray = houseObject.getJSONArray(BHConstants.JSON_KEY_BIG_IMG);
			this.bigImg = jsonArrayToArrayList(bigImgJSONArray);
		}catch(Exception e){}
		
		try{
			JSONArray layoutImgJSONArray = houseObject.getJSONArray(BHConstants.JSON_KEY_LAYOUT_IMG);
			this.layoutImg = layoutImgJSONArray.getString(0);
		}catch(Exception e){}
		
		try{
			JSONArray descriptionJSONArray = houseObject.getJSONArray(BHConstants.JSON_KEY_DESCRIPTION);
			this.description = jsonArrayToArrayList(descriptionJSONArray);
		}catch(Exception e){}
		
		try{
			if (!houseObject.isNull(BHConstants.JSON_KEY_COMMUNITY)){
				this.community = houseObject.getString(BHConstants.JSON_KEY_COMMUNITY);
			}
		}catch(Exception e){}
		
		try{
			this.areaLand = (float) houseObject.getDouble(BHConstants.JSON_KEY_AREA_LAND);
		}catch(Exception e){}
		
		try{
			this.pingDetail = houseObject.getJSONArray(BHConstants.JSON_KEY_PING_DETAIL);
		}catch(Exception e){}
		
		try{
			this.areaPublic = (float) houseObject.getDouble(BHConstants.JSON_KEY_AREA_PUBLIC);
		}catch(Exception e){}
		
		try{
			this.areaGarage = (float) houseObject.getDouble(BHConstants.JSON_KEY_AREA_GARAGE);
		}catch(Exception e){}
		
		try{
			this.floor = houseObject.getString(BHConstants.JSON_KEY_FLOOR);
		}catch(Exception e){}
		
		try{
			this.family = houseObject.getString(BHConstants.JSON_KEY_FAMILY);
		}catch(Exception e){}
		
		try{
			this.lift = (int) houseObject.getInt(BHConstants.JSON_KEY_LIFT);
		}catch(Exception e){}
		
		
		try{
			this.houseFront = houseObject.getString(BHConstants.JSON_KEY_HOUSE_FRONT);
		}catch(Exception e){}
		
		try{
			this.buildingFront = houseObject.getString(BHConstants.JSON_KEY_BUILDING_FRONT);
		}catch(Exception e){}
		
		try{
			this.windowFront = houseObject.getString(BHConstants.JSON_KEY_WINDOW_FRONT).trim();
		}catch(Exception e){}
		
		try{
			this.security = houseObject.getString(BHConstants.JSON_KEY_SECURITY);
		}catch(Exception e){}
		
		try{
			this.monthlyFee = houseObject.getInt(BHConstants.JSON_KEY_MONTHLY_FEE);
		}catch(Exception e){}
		
		try{
			this.sfside = houseObject.getInt(BHConstants.JSON_KEY_SF_SIDE);
		}catch(Exception e){}
		
		try{
			this.sfdarkroom = houseObject.getInt(BHConstants.JSON_KEY_SF_DARKROOM);
		}catch(Exception e){}
		
		try{
			this.buildingStructure = houseObject.getString(BHConstants.JSON_KEY_BUILDING_STRUCTURE);
		}catch(Exception e){}
		
		try{
			this.wallStructure = houseObject.getString(BHConstants.JSON_KEY_WALL_STRUCTURE).trim().replace("　", "");
		}catch(Exception e){}
		
		try{
			this.parking = houseObject.getString(BHConstants.JSON_KEY_PARKING);
		}catch(Exception e){}
		
		//生活機能
		try{
			if (!houseObject.isNull(BHConstants.JSON_KEY_PRIMARY_SCHOOL)){
				this.primarySchool = houseObject.getString(BHConstants.JSON_KEY_PRIMARY_SCHOOL).trim().replace("　", "");
			}
		}catch(Exception e){}
		
		try{
			if (!houseObject.isNull(BHConstants.JSON_KEY_JUNIOR_SCHOOL)){
				this.juniorSchool = houseObject.getString(BHConstants.JSON_KEY_JUNIOR_SCHOOL).trim().replace("　", "");
			}
		}catch(Exception e){}
		
		try{
			if (!houseObject.isNull(BHConstants.JSON_KEY_MARKET)){
				this.market = houseObject.getString(BHConstants.JSON_KEY_MARKET).trim().replace("　", "");
			}
		}catch(Exception e){}
		
		try{
			if (!houseObject.isNull(BHConstants.JSON_KEY_GARDEN)){
				this.garden = houseObject.getString(BHConstants.JSON_KEY_GARDEN).trim().replace("　", "");
			}
		}catch(Exception e){}
		
		try{
			if (!houseObject.isNull(BHConstants.JSON_KEY_MRT_INFO)){
				this.MRTInfo = houseObject.getJSONArray(BHConstants.JSON_KEY_MRT_INFO);
			}
		}catch(Exception e){}
		
		//物件地圖
		this.mapUrl = String.format(BHConstants.HOUSE_DETAIL_MAP_URL_FORMAT, lat, lng);
		
		//業務聯絡
		try{
			this.store = houseObject.getString(BHConstants.JSON_KEY_STORE);
		}catch(Exception e){}
		
		try{
			this.storetel = houseObject.getString(BHConstants.JSON_KEY_STORE_TEL);
		}catch(Exception e){}
		
		try{
			this.storeAddress = houseObject.getString(BHConstants.JSON_KEY_STORE_ADDRESS);
		}catch(Exception e){}
		
		//互動看屋/社區公設
		try{
			this.vr = houseObject.getString(BHConstants.JSON_KEY_VR);
		}catch(Exception e){}
		
		try{
			if (!houseObject.isNull(BHConstants.JSON_KEY_VR_COM)){
				this.vrCom = houseObject.getString(BHConstants.JSON_KEY_VR_COM);
			}
		}catch(Exception e){}
		
	}
	
	
	public ArrayList<String> jsonArrayToArrayList(JSONArray jsonArray){
		
		ArrayList<String> arrayList = new ArrayList<String>();
		if (jsonArray==null) return arrayList;
		
		try {
			for (int i=0; i<jsonArray.length(); i++){
				arrayList.add(jsonArray.getString(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return arrayList;
	}
	
	
	public String getDescriptionText(){
		String text = "";
		
		for (int i=0; i<description.size();i++){
			text += description.get(i);
			
			if (i!=description.size()-1){
				text += "\n";
			}
		}
		
		return text;
	}
	
	public String getFamilyLiftText(){
		String text = "";
		text += "該層"+this.family+"戶; ";
		text += "共用"+this.lift+"部電梯";
		return text;
	}
	
	public String getFaceText(){
		String text = "";
		
		if (!buildingFront.equals("")){
			text += Singleton.resources.getString(R.string.building)+"："+buildingFront;
		}
		
		if (!houseFront.equals("")){
			if (!text.equals("")) text+="; ";
			text += Singleton.resources.getString(R.string.house)+"："+houseFront;
		}
		
		if (!windowFront.equals("")){
			if (!text.equals("")) text+="; ";
			text += Singleton.resources.getString(R.string.window)+"："+windowFront;
		}
		
		return text;
	}
	
	public String getSideDarkText(){
		String text = "";
		
		text += (sfside==0)? 
				Singleton.resources.getString(R.string.no):
				Singleton.resources.getString(R.string.yes);
		
		text +="/";
		
		text += (this.sfdarkroom==0)?"-":Singleton.resources.getString(R.string.has);
		
		return text;
	}
	
	public String getPingDetailText(int index){
		String text = "";
		try {
			JSONObject pingObject = this.pingDetail.getJSONObject(index);
			text += pingObject.getString(BHConstants.JSON_KEY_NAME).trim().replace("　", "")+"："
					+pingObject.getString(BHConstants.JSON_KEY_PING)
					+Singleton.resources.getString(R.string.unit_pyeong);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return text;
	}
	
	
	
}
