package com.yowoo.newbuyhouse;

import org.json.JSONArray;

public class BHConstants {

	/* IS_API_TEST:
	 * 大多api已設為用外網測試，但某些表單api一定要用內網測試
	 * 若正式發佈一定要更改此設定
	 * true:測試階段使用內網  false:正式發佈使用外網
	 * */
	public static final Boolean IS_API_TEST = true;
	
	public static final String BASE_URL = "http://rest.sinyi.com.tw/";
	public static final String BASE_RC_URL = "http://rest.rc.sinyi.com.tw/";
	public static final String HOUSE_REST_URL_MAP_SEARCH = "search/mapSearch.json";
	public static final String HOUSE_REST_URL_GET_HOUSE_DETAIL = "search/getHouseDetail.json";
	public static final String HOUSE_REST_URL_LIST_SEARCH = "search/listSearch.json";
	public static final String STORE_REST_URL_LIST_SEARCH = "Storeinfo/StoreList.json";
	public static final String STORE_REST_URL_GET_CITY = "area/getSinyiCity.json";
	public static final String STORE_REST_URL_GET_AREA = "area/getSinyiArea.json";
	public static final String SINYI_REST_URL_SEND_FORM = "Message/setMessageData.json";
	public static final String SINYI_REST_URL_GET_ROADS = "area/getRoads.json";
	
	public static final String LOGIN_REST_URL_LOGIN = "member/login.json";
	public static final String LOGIN_REST_URL_LOGOUT = "member/logout.json";
	public static final String LOGIN_REST_URL_IS_LOGIN = "member/isLogin.json";
	public static final String LOGIN_REST_URL_FORGET_PW = "member/forgetPwd.json";
	public static final String LOGIN_REST_URL_REGISTER = "member/register.json";
	public static final String LOGIN_REST_URL_SET_MEMBER_DATA = "member/setMemberData.json";
	public static final String LOGIN_REST_URL_GET_MEMBER_PROFILE = "member/getMemberProfile.json";
	public static final String LOGIN_REST_URL_SET_PROFILE = "member/setProfile.json";		
	public static final String LOGIN_REST_URL_VERIFY_ACCOUNT = "member/verifyAccount.json";		
	public static final String LOGIN_REST_URL_OPENID_LOGIN = "member/openidLogin.json";
	
	public static final String TRACK_REST_URL_TRACK_HOUSE = "user/trackHouse.json";
	public static final String TRACK_REST_URL_REMOVE_TRACK_HOUSE = "user/removeTrackHouse.json";
	
	public static final String PRICE_REST_URL_MAP_INFO = "trade/mapInfo.json";
	public static final String PRICE_REST_URL_LIST_INFO = "trade/listInfo.json";
	public static final String PRICE_REST_URL_GET_DETAIL = "trade/getTradeDetail.json";
	
	public static final String NEWS_REST_URL_GET_NEWS = "News/getNewsData.json";
	public static final String NEWS_REST_URL_GET_NEWS_DETAIL = "News/getNewsDetail.json";
	
	public static final String TRACK_REST_URL_SUBSCRIBE_SEARCH = "match/addPushMatch.json";
	public static final String TRACK_REST_URL_GET_SUBSCRIBES = "match/getPushMatchByMemberID.json";
	public static final String TRACK_REST_URL_REMOVE_SUBSCRIBE = "match/removePushMatch.json";
	public static final String TRACK_REST_URL_GET_SUBSCRIBE_HOUSE = "match/listSearch.json";
	public static final String TRACK_REST_URL_UPDATE_SUBSCRIBE = "match/updatePushMatch.json";
	public static final String TRACK_REST_URL_LOGIN_GCM_TOKEN = "match/loginApp.json";
	public static final String TRACK_REST_URL_LOGOUT_GCM_TOKEN = "match/logoutApp.json";
	
	public static final String HOUSE_DETAIL_MAP_URL_FORMAT = "http://img.sinyi.com/u/map/15_540_460_%f_%f.png";
	
	public static final String FORM_LAW_DETAIL_URL = "http://www.sinyi.com.tw/tos.php";
	
	public static final String GOOGLE_API_GET_DATA_BY_ADDR = "http://maps.googleapis.com/maps/api/geocode/json";
	
	/* Yahoo登入 */
	public static final String YAHOO_CALLBACK_URL = "yahoo://callback";
	//obvyah
//	public static final String YAHOO_CONSUMER_KEY = "dj0yJmk9MUpzbTRNbE5ISG1oJmQ9WVdrOVlWQldibEJpTlRZbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD0wMA--";
//	public static final String YAHOO_CONSUMER_SECRET = "1325a65264dde316e00b16ca233b86d06fdfde98";
	//sinyi.yowoo@yahoo.com
	public static final String YAHOO_CONSUMER_KEY = "dj0yJmk9VExtMGE5UHlieEdQJmQ9WVdrOVpXZFBOVmd4Tm04bWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD04Ng--";
	public static final String YAHOO_CONSUMER_SECRET = "2410306f643e59778fc96135e3f98eb05c9f9623";
	
	public static final int DISPLAY_MODE_MAP = 0;
	public static final int DISPLAY_MODE_LIST = 1;
	
	public static final int FILTER_MODE_AREA = 0;//區域搜尋
	public static final int FILTER_MODE_MRT = 1;//區域搜尋
	
	public static final int SEARCH_MODE_LATLNG = 0;//採用目前經緯度位置資訊
	public static final int SEARCH_MODE_LOCATION = 1;//採用目前filter的位置資訊
	
	public static final String LATEST_LOCATION_LAT = "LATEST_LOCATION_LAT";
	public static final String LATEST_LOCATION_LNG = "LATEST_LOCATION_LNG";
	
	public static final String FORM_MESSAGE_TYPE_RESERVATION = "C";
	public static final String FORM_MESSAGE_TYPE_SALESMAN = "G";
	
	/* GCM */
	public static final String GCM_TOKEN = "GCM_TOKEN";
	//public static final String GCM_APP_ID = "359918754579";//testGCM
	public static final String GCM_APP_ID = "639235998202";//sinyi
	
	/* Menu */
	public static final int MENU_HOUSE_POSITION = 0;
	public static final int MENU_PRICE_POSTION = 1;
	public static final int MENU_TRACK_POSITION = 2;
	public static final int MENU_NEWS_POSITION = 3;
	public static final int MENU_STORE_POSITION = 4;
	public static final int MENU_CHAT_POSITION = 5;
	public static final int MENU_BUY_MESSAGE_POSITION = 6;
	public static final int MENU_SELL_MESSAGE_POSITION = 7;
	
	/* imageloader記憶體管控:
	 * 當app可使用的memory小於此大小時，需設定較差的圖片設定以減輕記憶體使用量 
	 */
	public static final int IMAGE_LOADER_CONFIG_MEMORY_LIMIT = 80;//MB
	
	/* Map: Max Zoom Level */
	public static final float HOUSE_MAX_ZOOM = 14.3f;
	
	/* Params */
	public static final String PARAM_PARAMS = "params";
	public static final String PARAM_LATLON = "latlon";
	public static final String PARAM_DISTANCE = "distance";
	public static final String PARAM_NO = "NO";
	public static final String PARAM_RETURN_PARAMS = "returnParams";
	public static final String PARAM_PAGE = "page";
	public static final String PARAM_LIMIT = "limit";
	
	//sinyi
	public static final String PARAM_ZIPCODE = "zipCode";
	
	//form
	public static final String PARAM_MESSAGE_TYPE = "message_type";
	public static final String PARAM_NAME = "name";
	public static final String PARAM_SEX = "sex";
	public static final String PARAM_MOBILE = "mobile";
	public static final String PARAM_EMAIL ="email";
	public static final String PARAM_CONTACT_START_DATE = "contactStartDate";
	public static final String PARAM_HOUSE_NO = "houseNO";
	
	//store
	public static final String PARAM_CITY_ID = "cityId";
	
	//login
	public static final String PARAM_ACCOUNT = "account";
	public static final String PARAM_PW = "pw";
	public static final String PARAM_MEMBER_ID = "member_ID";
	public static final String PARAM_ACCOUNT_MERGE = "account_merge";
	public static final String PARAM_VERIFY_CODE = "verifyCode";
	public static final String PARAM_OPEN_UID = "open_uid";
	public static final String PARAM_TOKEN = "token";
	public static final String PARAM_FROM = "from";
	public static final String PARAM_FIELD = "field";
	public static final String PARAM_PWD_NEW = "pwd_new";
	public static final String PARAM_TOKEN_SECRET = "token_secret";
	public static final String PARAM_COMSUMER_KEY = "consumer_key";
	public static final String PARAM_COMSUMER_SECRET = "consumer_secret";
	
	//track
	public static final String PARAM_SALES_ID = "sales_id";
	public static final String PARAM_FAV = "fav";
	public static final String PARAM_MEMBERID = "memberID";
	public static final String PARAM_CRITERIA = "criteria";
	public static final String PARAM_DASH_ID = "_id";
	public static final String PARAM_DEVICE_ID = "device_ID";
	
	//news
	public static final String PARAM_PON = "pon";
	public static final String PARAM_ID = "id";
	
	//google
	public static final String PARAM_ADDRESS = "address";
	public static final String PARAM_SENSOR = "sensor";
	
	
	/* JSON */
	public static final String JSON_KEY_OPT = "OPT";
	public static final String JSON_KEY_STATUS = "status";
	public static final String JSON_KEY_POI = "POI";
	public static final String JSON_KEY_TOTAL = "total";
	public static final String JSON_KEY_LAT = "lat";
	public static final String JSON_KEY_LNG = "lng";
	public static final String JSON_KEY_NO = "NO";
	public static final String JSON_KEY_LABEL = "label";
	
	public static final String JSON_KEY_HOUSE = "House";
	public static final String JSON_KEY_NAME = "name";
	public static final String JSON_KEY_IMG_DEFAULT = "imgDefault";
	public static final String JSON_KEY_PRICE = "price";
	public static final String JSON_KEY_PRICE_FIRST = "priceFirst";
	public static final String JSON_KEY_DISCOUNT = "discount";
	public static final String JSON_KEY_TYPE = "type";
	public static final String JSON_KEY_ADDRESS = "address";
	public static final String JSON_KEY_AREA_BUILDING = "areaBuilding";
	public static final String JSON_KEY_LAYOUT = "layout";
	public static final String JSON_KEY_AGE = "age";
	public static final String JSON_KEY_PAGE = "page";
	public static final String JSON_KEY_TOTAL_PAGE = "totalPage";
	public static final String JSON_KEY_LIST = "List";
	public static final String JSON_KEY_STATION = "station";
	public static final String JSON_KEY_EXIT = "exit";
	public static final String JSON_KEY_DURATION = "duration";
	public static final String JSON_KEY_DISTANCE = "distance";
	public static final String JSON_KEY_LINE = "line";
	public static final String JSON_KEY_COLOR = "color";
	
	//for detail
	public static final String JSON_KEY_BIG_IMG = "bigImg";
	public static final String JSON_KEY_LAYOUT_IMG = "layoutImg";
	public static final String JSON_KEY_DESCRIPTION = "description";
	public static final String JSON_KEY_COMMUNITY = "community";
	public static final String JSON_KEY_AREA_LAND = "areaLand";
	public static final String JSON_KEY_PING_DETAIL = "pingDetail";
	public static final String JSON_KEY_PING = "ping";
	public static final String JSON_KEY_AREA_PUBLIC = "areaPublic";
	public static final String JSON_KEY_AREA_GARAGE = "areaGarage";
	public static final String JSON_KEY_FLOOR = "floor";
	public static final String JSON_KEY_FAMILY = "family";
	public static final String JSON_KEY_LIFT = "lift";
	public static final String JSON_KEY_HOUSE_FRONT = "houseFront";
	public static final String JSON_KEY_BUILDING_FRONT = "buildingFront";
	public static final String JSON_KEY_WINDOW_FRONT = "windowFront";
	public static final String JSON_KEY_SECURITY = "security";
	public static final String JSON_KEY_MONTHLY_FEE = "monthlyFee";
	public static final String JSON_KEY_SF_SIDE = "sfside";
	public static final String JSON_KEY_SF_DARKROOM = "sfdarkroom";
	public static final String JSON_KEY_BUILDING_STRUCTURE = "buildingStructure";
	public static final String JSON_KEY_WALL_STRUCTURE = "wallStructure";
	public static final String JSON_KEY_PARKING = "parking";
	public static final String JSON_KEY_PRIMARY_SCHOOL = "primarySchool";
	public static final String JSON_KEY_JUNIOR_SCHOOL = "juniorSchool";
	public static final String JSON_KEY_MARKET = "market";
	public static final String JSON_KEY_GARDEN = "garden";
	public static final String JSON_KEY_MRT_INFO = "MRTInfo";
	public static final String JSON_KEY_STORE = "store";
	public static final String JSON_KEY_STORE_TEL = "storetel";
	public static final String JSON_KEY_STORE_ADDRESS = "storeAddress";
	public static final String JSON_KEY_VR = "vr";
	public static final String JSON_KEY_VR_COM = "vrCom";
	
	//city & area
	public static final String JSON_KEY_CITY_ID = "cityId";
	public static final String JSON_KEY_CITY_NAME = "cityName";
	public static final String JSON_KEY_CITY_CODE = "cityCode";
	public static final String JSON_KEY_AREAS = "areas";
	public static final String JSON_KEY_ZIPCODE = "zipCode";
	
	//store
	public static final String JSON_KEY_DATA = "data";
	public static final String JSON_KEY_STORE_NO = "storeNO";
	public static final String JSON_KEY_CITY = "city";
	public static final String JSON_KEY_COUNTY = "county";
	public static final String JSON_KEY_TEL_1 = "tel1";
	public static final String JSON_KEY_TEL_2 = "tel2";
	public static final String JSON_KEY_TRAFFIC = "traffic";
	public static final String JSON_KEY_XPOINT = "xPoint";
	public static final String JSON_KEY_YPOINT = "yPoint";
	public static final String JSON_KEY_TITLE = "title";
	public static final String JSON_KEY_CONTENT_TEXT = "contentText";
	public static final String JSON_KEY_IMG = "img";
	public static final String JSON_KEY_CITYS = "citys";
	
	//form
	public static final String JSON_KEY_MESSAGE = "Message";

	//login
	public static final String JSON_KEY_DEBUG_MESSAGE = "DEBUG_MESSAGE";
	public static final String JSON_KEY_MESSAGE_LOWER_CASE = "message";
	
	//track
	public static final String JSON_KEY_HOUSE_NO = "houseNO";
	public static final String JSON_KEY_PARAMS = "params";
	public static final String JSON_KEY_CRITERIA = "criteria";
	public static final String JSON_KEY_ID = "id";
	public static final String JSON_KEY_CREATE_DATETIME = "createDatetime";
	public static final String JSON_KEY_CREATE_DATE = "create_date";
	public static final String JSON_KEY_AFTER_DATE = "after_date";
	public static final String JSON_KEY_TRADE = "trade";
	
	//price
	public static final String JSON_KEY_UNIT_PRICE = "unitPrice";
	public static final String JSON_KEY_HAS_GARAGE = "hasGarage";
	public static final String JSON_KEY_BUILDING_TYPE = "buildingType";
	public static final String JSON_KEY_SOLD_DATE = "soldDate";
	public static final String JSON_KEY_OUTLIER = "outlier";
	
	//google
	public static final String JSON_KEY_RESULTS = "results";
	
	/* Activity Request */
	public static final int ACTIVITY_REQUEST_VERIFY = 100;
	
	/* Facebook Related */
	public static final String FACEBOOK_REQUEST_FIELDS = "id,email,first_name,gender,last_name,link,name,timezone,updated_time,verified";
	
	/* House */
	public static final int MARKER_TYPE_SINGLE = 0;
	public static final int MARKER_TYPE_CLUSTERED = 1;
	public static final int HOUSE_MODE_MAP = 0;
	public static final int HOUSE_MODE_LIST = 1;
	public static final int SIMPLE_HOUSE_FETCH_LIMIT = 20;//map跳出來的可滑動的房屋物件數量
	public static final int HOUSE_LIST_FETCH_LIMIT = 30;
	public static final int MAX_PRICE = 100000;
	
	/* Track */
	public static final String FROM_ANDROID_APP = "ANDROID_APP";
	
	/* Map */
	public static final float DEFAULT_LATITUDE = 25.039877f;
    public static final float DEFAULT_LONGITUDE = 121.512736f;
    
    /* Broadcast */
    public static final String BROADCAST_HOUSE_SEARCH_REFRESH_MAP = "BROADCAST_HOUSE_SEARCH_REFRESH_MAP";
    public static final String BROADCAST_HOUSE_SEARCH_REFRESH_LIST = "BROADCAST_HOUSE_SEARCH_REFRESH_LIST";
    public static final String BROADCAST_STORE_SEARCH_REFRESH_MAP = "BROADCAST_STORE_SEARCH_REFRESH_MAP";
    public static final String BROADCAST_USER_STATUS = "BROADCAST_USER_STATUS";
    public static final String BROADCAST_PRICE_SEARCH_REFRESH_MAP = "BROADCAST_PRICE_SEARCH_REFRESH_MAP";
    public static final String BROADCAST_PRICE_SEARCH_REFRESH_LIST = "BROADCAST_PRICE_SEARCH_REFRESH_LIST";
    public static final String BROADCAST_LOGIN_YAHOO = "BROADCAST_LOGIN_YAHOO";
    
    
    /* Extra */
    public static final String EXTRA_HOUSE_NO = "EXTRA_HOUSE_NO";
    public static final String EXTRA_HOUSE_NAME = "EXTRA_HOUSE_NAME";
    public static final String EXTRA_HOUSE_IMG = "EXTRA_HOUSE_IMG";
    public static final String EXTRA_HOUSE_VR_NO = "EXTRA_HOUSE_VR_NO";
    public static final String EXTRA_INITIAL_IMAGE_POSTION = "EXTRA_INITIAL_IMAGE_POSTION";
	public static final String EXTRA_MEDIA_FILE_PATH_ARRAY = "EXTRA_MEDIA_FILE_PATH_ARRAY";
	public static final String EXTRA_FORM_TYPE = "EXTRA_FORM_TYPE";
	
	public static final String EXTRA_ACCOUNT = "EXTRA_ACCOUNT";
	public static final String EXTRA_VERIFY_TYPE = "EXTRA_VERIFY_TYPE";
	
	public static final String EXTRA_FILTER_PARAMS = "EXTRA_FILTER_PARAMS";
	public static final String EXTRA_SUBSCRIBE_ID = "EXTRA_SUBSCRIBE_ID";
	public static final String EXTRA_CREATE_DATE = "EXTRA_CREATE_DATE";
	public static final String EXTRA_AFTER_DATE = "EXTRA_AFTER_DATE";
	public static final String EXTRA_GOTO_FRAGMENT_POS = "EXTRA_GOTO_FRAGMENT_POS";
	public static final String EXTRA_FROM_NOTIFICATION = "EXTRA_FROM_NOTIFICATION";
	
	public static final String EXTRA_NEWSID_ARRAY = "EXTRA_NEWSID_ARRAY";
	public static final String EXTRA_NEWS_POSITION = "EXTRA_NEWS_POSITION";
	
	/* Pref */
	public static final String PREF_HOUSE_SWITCH = "PREF_HOUSE_SWITCH";
	public static final String PREF_PRICE_SWITCH = "PREF_PRICE_SWITCH";
	
}
