package com.yowoo.newbuyhouse;

public class Constants {

	/* Project Settings */
	public static final String PACKAGE_NAME = "com.thinkermobile.sinyi";
	
	
	public static final String FRAGMENT_ARG_POSITION = "FRAGMENT_ARG_POSITION";
	
	
	//========================Chat Settings=========================//
	public static final Boolean IS_SANDBOX = true;
//	public static final Boolean IS_RELEASE_VERSION = false;//if false: show debug page and item
//	public static final Boolean CRASHLYTICS_ENABLE = false;// Indicate whether we need to report the crash or not.
//	public static final String GCM_APP_ID = "";
//
//	public static final int APP_ROLE_MODE = 1;//0: Staff; 1:customer
//	public static final int ROLE_MODE_STAFF = 0;
//	public static final int ROLE_MODE_CUSTOMER = 1;
//
//	public static final String PARSE_APP_ID = "A6P6Fpldrez0HGaqM4EhFfpG8egk0hjJGsGWysig";
//	public static final String PARSE_CLIENT_KEY = "CX5AqJp3ximMTdjPwegOUqmYGSMveofqyQIFJv4V";
//	public static final String PARSE_REST_KEY = "77shLd4WAl9cReHQMEfd83Bo0xXx5Ey62BGxCj88";

	//public static final String DB_NAME = "newBuyHouseChat.db"; 
	//========================Chat Settings=========================//
	
	public static final String INTERNAL_PATH = "/data/data/"+PACKAGE_NAME+"/";
	public static final String INTERNAL_TEMP_PATH = "/data/data/"+PACKAGE_NAME+"/tmp_";
	public static final String INTERNAL_TEMP_IMAGE_PATH = "/data/data/"+PACKAGE_NAME+"/tmp.jpg";
	public static final int FILE_DOWNLOAD_TIMEOUT = 10;
	
	public static final int HTTP_CONNECTION_TIMEOUT = 15000;//15 secs
	
	public static final long MAX_FILE_CACHE_SIZE = 128000000; // 128MB
	public static final int MAX_BEFORE_TIME = 2147483647;//MAX DAYTIME
	public static final int MAX_MESSAGE_TIMESTAMP = 2147483647;
    
	public static final int LONG_FETCH_PERIOD = 180; // 3 minute
    public static final int SHORT_FETCH_PERIOD = 0; // 0 minute
    
    /* Max Fetch Default */
    public static final int MAX_FETCHED_MESSAGE_COUNT = 1000;//1000: parse����
	
    /* IMAGE */
	public static final int IMAGE_PIXELS = 1000000;
	public static final int IMAGE_THUMBNAIL_PIXELS = 50000;
	public static final int PROFILE_PICTURE_PIXELS = 360000;
	public static final int PROFILE_PICTURE_THUMBNAIL_PIXELS = 50000;
	
	public static final int FILE_UPLOAD_MB_LIMIT = 10;//10M
	public static final String IMAGELOADER_DISK_FILE_URL_PREFIX = "file://";
    
    /* Community Tab */
	public static final int MAIN_TAB_NEWS = 0;
	public static final int MAIN_TAB_CHAT = 1;
	public static final int MAIN_TAB_BUILDING = 2;
	public static final int MAIN_TAB_INVITE = 3;
	public static final String EXTRA_SELECT_TAB = "EXTRA_SELECT_TAB";
	
	/* Community Broadcast */
	public static final String BROADCAST_KEYBOARD_EVENT = "BROADCAST_KEYBOARD_EVENT";
	public static final String EXTRA_KEYBOARD_IS_SHOWN = "EXTRA_KEYBOARD_IS_SHOWN";
	
	/* Network State */
	public static final int NETWORK_NONE = 0;
	public static final int NETWORK_3G = 1;
	public static final int NETWORK_WIFI = 2;
	
	/* Device Type */
	public static final String DEVICE_TYPE_IOS = "ios";
	public static final String DEVICE_TYPE_ANDROID = "Android";
	
	/* Parse Cloud Code: Function Name */
	public static final String CLOUD_CODE_ASSIGN_NEW_STAFF = "assignNewStaff";
	public static final String CLOUD_CODE_PARAM_USEROBJECTID = "userObjectId";

	/* Parse REST API */
	public static final String REST_SEND_MESSAGE_URL = "https://api.parse.com/1/classes/Message";
	public static final String REST_GET_CHATROOM_URL = "https://api.parse.com/1/classes/ChatRoom";
    public static final String REST_GET_USERINFOS_URL = "https://api.parse.com/1/users";
    public static final String REST_GET_MESSAGE_URL = "https://api.parse.com/1/classes/Message";
	public static final String REST_UPDATE_USER_URL = "https://api.parse.com/1/users/";
	public static final String REST_GET_MUSIC_URL = "https://api.parse.com/1/classes/Music";
	public static final String REST_GET_PLAYLIST_URL = "https://api.parse.com/1/classes/Playlist";
    
	/* Push Notification */
	public static final String NEW_SUBSCRIBE_HOUSE = "NEW_SUBSCRIBE_HOUSE";
	public static final int NEW_SUBSCRIBE_HOUSE_NOTIFICATION = 100;
//	public static final String NEW_MESSAGE = "NEW_MESSAGE";
//	public static final String NEW_CUSTOMER = "NEW_CUSTOMER";
//	public static final String UPDATE_PLAYLIST = "UPDATE_PLAYLIST";
//	public static final String DEMAND_MUSIC = "DEMAND_MUSIC";
//	public static final int NEW_MESSAGE_NOTIFICATION = 100;
//	public static final int NEW_CUSTOMER_NOTIFICATION = 200;
//	public static final int UPDATE_PLAYLIST_NOTIFICATION = 300;
	public static final String LAST_NOTIFICATION_SOUND_AND_VIBRATE_TIME = "LAST_NOTIFICATION_SOUND_AND_VIBRATE_TIME";
	
	/* Send Message Type */
	public static final int SEND_MODE_NOTSENT = 0;
	public static final int SEND_MODE_ISSENT = 1;
	public static final int SEND_MODE_SENDING = 2;
	public static final int SEND_MODE_ERROR = 3;
	public static final int SEND_MODE_UPLOAD_ERROR = 4;
	
	/* Chat Message Mode */
	public static final int CHAT_MESSAGE_MODE_TEXT = 0;
	public static final int CHAT_MESSAGE_MODE_STICKER = 1;
	public static final int CHAT_MESSAGE_MODE_MEDIA = 2;
	public static final int CHAT_MESSAGE_MODE_AUDIO = 3;
	
	/* Media Action Type */
	public static final int MEDIA_ACTION_TAKE_PICTURE = 100;
	public static final int MEDIA_ACTION_CHOOSE_PICTURE = 200;
	public static final int MEDIA_ACTION_TAKE_VIDEO = 300;
	public static final int MEDIA_ACTION_CHOOSE_VIDEO = 400;
	public static final int MEDIA_ACTION_RECORD_VOICE = 500;
	
	/* Get Content FileType */
	public static final int FILE_TYPE_ALL = 0;
	public static final int FILE_TYPE_ONLY_IMAGE = 1;
	public static final int FILE_TYPE_ONLY_VIDEO = 2;
	
	/* JSON Key */
	public static final String JSON_ACTION = "action";
	public static final String JSON_MESSAGE = "message";
	public static final String JSON_TALK_TO_USER_OBJECT_ID = "fromUserObjectId";
	public static final String JSON_ROOM_ID = "roomId";
	public static final String JSON_ALERT = "alert";
	public static final String JSON_MUSIC_OBJECTID = "musicObjectId";
	
	
	/* Ebook Fetch Data Status */
	public static final int DATA_FETCH_FROM_DATABASE = 0;
	public static final int DATA_FETCH_FROM_SERVER = 1;
	
	/* Ebook Category */
	public static final String EBOOK_DATA_CAT_NEWS = "1";
	public static final String EBOOK_DATA_CAT_ABOUT = "6";
	public static final String EBOOK_DATA_CAT_PANORAMA720 = "8";
	
	/* BroadCast Receiver */
	public static final String NEW_MESSAGE_BROADCAST = "NEW_MESSAGE_BROADCAST";
	public static final String NEW_CUSTOMER_BROADCAST = "NEW_CUSTOMER_BROADCAST";
	public static final String UPDATE_PLAYLIST_BROADCAST = "UPDATE_PLAYLIST_BROADCAST";
	
	/* Request Song Key */
	public static final String PUSH_MUSIC_SERVICE = "service";
	
	/* Preference Key */
	public static final String USER_ID = "USER_ID";
	public static final String GENDER = "GENDER";
	public static final String LATITUDE = "LATITUDE";
	public static final String LONGITUDE = "LONGITUDE";
	public static final String LAST_TAB = "LAST_TAB";
	public static final String MY_STAFF_OBJECT_ID = "MY_STAFF_OBJECT_ID";
	public static final String MY_ROOMID_WITH_STAFF = "MY_ROOMID_WITH_STAFF";
	
	public static final String LAST_FETCH_MESSAGE_TIME = "LAST_FETCH_MESSAGE_TIME";
	public static final String FETCH_MESSAGE_MAX_TIMESTAMP = "FETCH_MESSAGE_MAX_TIMESTAMP";
	
	/* Extra Key */
	public static final String SELECTED_TAB = "SELECTED_TAB";
	public static final String TALK_TO_USER_OBJECT_ID = "TALK_TO_USER_OBJECT_ID";
	public static final String ROOM_ID = "ROOM_ID";
	public static final String FROM_LOGIN_LAUNCH = "FROM_LOGIN_LAUNCH";
	public static final String VIDEO_PATH = "VIDEO_PATH";
	
	/* test */
	public final static String PREF_FILE = "PREF_FILE";

	public final static String PREF_USERNAME = "PREF_USERNAME";
	
	public final static String EXTRA_USEROBJECTID = "EXTRA_USEROBJECTID";
	public final static String EXTRA_USERNAME = "EXTRA_USERNAME";
	
	public final static String PARSECLASS_FRIEND = "Friend";
	
	public final static int FRIENDTYPE_NONE = 0;
	public final static int FRIENDTYPE_INVITE = 1;
	public final static int FRIENDTYPE_BEINVITED = 2;
	public final static int FRIENDTYPE_FRIEND = 3;

	public final static String DEFAULT_USER_PASSWORD = "0000";
	
	
	/* Parse Table Column: Message */
	public final static String col_content = "content";
	public final static String col_senderID = "senderId";
	public final static String col_receiverID = "receiverId";
	public final static String col_type = "type";
	public final static String col_isSent = "isSent";
	public final static String col_isRead = "isRead";
	public final static String col_messageID = "messageId";
	public final static String col_createdAt = "createdAt";
	public final static String col_updatedAt = "updatedAt";
	public final static String col_roomId = "roomId";
	public final static String col_timestamp = "timestamp";
	public final static String col_needUpload = "needUpload";
	
	/* Parse Table Column: ChatRoom */
	public final static String col_chatRoom_userObjectId2 = "userObjectId2";//customer
	public final static String col_chatRoom_userObjectId1 = "userObjectId1";//staff
	public final static String col_chatRoom_roomName = "roomName";
	public final static String col_chatRoom_badge = "badge";//only for sqlite
	public final static String col_objectId = "objectId";
	
	/* Parse Table Column: User */
	public final static String col_user_username = "username";
	public final static String col_user_userType = "userType";
	public final static String col_user_staffObjectId = "staffObjectId";
	public final static String col_user_nickname = "nickname";
	public final static String col_user_deviceType = "deviceType";
	public final static String col_user_pictureUrl = "pictureUrl";
	public final static String col_user_email = "email";
	public final static String col_user_phone = "phone";
	
	/* Parse Table Column: Installation */
	public final static String col_installation_userobjectid = "userobjectid";
	public final static String col_installation_deviceType = "deviceType";
	public final static String col_installation_username = "username";
	public final static String col_installation_pushMusicSession = "pushMusicSession";
	public final static String col_installation_pushMusic = "pushMusic";
	
	/* Parse Table Column: Music */
	public final static String col_music_objectId = "objectId";
	public final static String col_music_title = "title";
	public final static String col_music_description = "description";
	public final static String col_music_album = "album";
	public final static String col_music_artist = "artist";
	public final static String col_music_pictureUrl = "pictureUrl";
	public final static String col_music_duration = "duration";
	public final static String col_playlist_sequence = "sequence";
	
	public static final int WRAP_CONTENT = -2;
	public static final int MATCH_PARENT = -1;

	/* Message Type */
	public static final String MESSAGE_TYPE_TEXT = "text";
	public static final String MESSAGE_TYPE_IMAGE = "image";
	public static final String MESSAGE_TYPE_EMOJI = "emoji";
	public static final String MESSAGE_TYPE_VIDEO = "video";
	public static final String MESSAGE_TYPE_AUDIO = "audio";
	
	public static final String THUMBNAIL_PREFIX = "THUMBNAIL_";
	public static final int THUMBNAIL_SIZE = 100;
	public static final int CHAT_IMAGE_WIDTH = 100;
	
	/* User Type */
	public static final String USER_TYPE_STAFF = "staff";
	public static final String USER_TYPE_CUSTOMER = "customer";
	
	/* Databse */
    public final static String CREATE_USER_TABLE_SQL = "CREATE TABLE IF NOT EXISTS `User` "
            + "(`id` INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "`objectId` TEXT NOT NULL DEFAULT '',"
            + "`username` TEXT NOT NULL DEFAULT '',"
            + "`nickname` TEXT NOT NULL DEFAULT '',"
            + "`pictureUrl` TEXT NOT NULL DEFAULT '',"
            + "`deviceType` TEXT NOT NULL DEFAULT '',"
            + "`userType` TEXT NOT NULL DEFAULT '');";
    
    public final static String CREATE_MESSAGE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS `Message` "
            + "(`id` INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "`objectId` TEXT NOT NULL DEFAULT '',"
            + "`roomId` TEXT NOT NULL DEFAULT '',"
            + "`content` TEXT NOT NULL DEFAULT '',"
            + "`type` TEXT NOT NULL DEFAULT '',"
            + "`senderId` TEXT NOT NULL DEFAULT '',"
            + "`receiverId` TEXT NOT NULL DEFAULT '',"
            + "`messageId` TEXT NOT NULL DEFAULT '',"
            + "`isSent` INTEGER NOT NULL DEFAULT '0',"
            + "`needUpload` INTEGER NOT NULL DEFAULT '0',"
            + "`isRead` INTEGER NOT NULL DEFAULT '0',"
            + "`timestamp` INTEGER NOT NULL DEFAULT '0');";
    
    public final static String CREATE_PENDING_MESSAGE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS `PendingMessage` "
            + "(`id` INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "`userName` TEXT NOT NULL DEFAULT '',"
            + "`content` TEXT NOT NULL DEFAULT '');";
    
    public final static String CREATE_CHAT_ROOM_TABLE_SQL = "CREATE TABLE IF NOT EXISTS `ChatRoom` "
            + "(`id` INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "`objectId` TEXT NOT NULL DEFAULT '',"
            + "`roomName` TEXT NOT NULL DEFAULT '',"
            + "`userObjectId1` TEXT NOT NULL DEFAULT '',"
            + "`userObjectId2` TEXT NOT NULL DEFAULT '',"
            + "`badge` INTEGER NOT NULL DEFAULT '0',"
            + "`messageId` TEXT NOT NULL DEFAULT '',"
            + "`timestamp` INTEGER NOT NULL DEFAULT '0');";
    
    public final static String CREATE_NEWS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS `News` "
            + "(`id` INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "`title` TEXT NOT NULL DEFAULT '',"
            + "`excerpt` TEXT NOT NULL DEFAULT '',"
            + "`guid` TEXT NOT NULL DEFAULT '',"
            + "`imgurl` TEXT NOT NULL DEFAULT '',"
            + "`cat` TEXT NOT NULL DEFAULT '',"
            + "`timestamp` INT NOT NULL DEFAULT '0');";
    
    public final static String CREATE_MUSIC_TABLE_SQL = "CREATE TABLE IF NOT EXISTS `Music` "
            + "(`id` INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "`objectId` TEXT NOT NULL DEFAULT '',"
            + "`title` TEXT NOT NULL DEFAULT '',"
            + "`description` TEXT NOT NULL DEFAULT '',"
            + "`album` TEXT NOT NULL DEFAULT '',"
            + "`artist` TEXT NOT NULL DEFAULT '',"
            + "`pictureUrl` TEXT NOT NULL DEFAULT '',"
            + "`duration` INT NOT NULL DEFAULT '0');";
    
    public final static String[] CREATE_INDEX_SQL_ARRAY = { 
        "CREATE UNIQUE INDEX `user_id_index` ON User(objectId);",
        "CREATE UNIQUE INDEX `message_id_index` ON Message(messageId);",
        "CREATE UNIQUE INDEX `chat_id_index` ON ChatRoom(objectId);",
        "CREATE UNIQUE INDEX `news_id_index` ON News(guid);",
        "CREATE UNIQUE INDEX `music_id_index` ON Music(objectId);",
        //"CREATE UNIQUE INDEX `pending_message_index` ON PendingMessage(userID);",
        "CREATE INDEX `message_sender_id_index` ON Message(senderId);",
        "CREATE INDEX `message_receiver_id_index` ON Message(receiverId);",
        "CREATE INDEX `message_timestamp_index` ON Message(timestamp);",
        "CREATE INDEX `message_is_sent_index` ON Message(isSent);",
        "CREATE INDEX `message_is_uploaded_index` ON Message(needUpload);",
        "CREATE INDEX `message_is_read_index` ON Message(isRead);",
        "CREATE INDEX `message_type_index` ON Message(type);"};
    
    /* PullToRefresh */
    public static final String RELOAD = "reload";
    public static final String LOAD_MORE = "loadmore";
    public static final String LOAD_MORE_EMPTY = "LoadMoreEmpty";
    public static final String RESULT_OK = "OK";
    public static final String RESULT_EXCEPTION = "Exception";
    
	
}
