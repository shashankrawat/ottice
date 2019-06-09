package com.ottice.ottice.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * TODO: Add a class header comment!
 */
public class Common {
    static final String KEY_PREFERENCES_FILE = "com.ottice.ottice.PREFERENCES";

    // static but not final variables
    public static String deviceId;
    public static int duration = 1;
    public static String startDateTime;
    public static String minPrice;
    public static String maxPrice;
    public static String spaceTypeId;
    public static String capacity;
    public static ArrayList<String> amenitiesList;
    public static boolean resetApplied;
    public static String startDateStr;
    public static String startTimeStr;
    public static Date currentDate;
    public static String[] dateArray;
    public static int remainingDays;

    // intent passing values key constants
    public static final String INTENT_KEY_CityId = "CityId";
    public static final String INTENT_KEY_Latitude = "Latitude";
    public static final String INTENT_KEY_Longitude = "Longitude";
    public static final String INTENT_KEY_CityName = "CityName";
    public static final String INTENT_KEY_MinPrice = "MinPrice";
    public static final String INTENT_KEY_MaxPrice = "MaxPrice";
    public static final String INTENT_KEY_SpaceTypeId = "SpaceTypeId";
    public static final String INTENT_KEY_Capacity = "Capacity";
    public static final String INTENT_KEY_Amenities = "Amenities";
    public static final String INTENT_KEY_Reset = "Reset";
    public static final String INTENT_KEY_PlanType = "PlanType";
    public static final String INTENT_KEY_TotalSeats = "TotalSeats";
    public static final String INTENT_KEY_Price = "Price";
    public static final String INTENT_KEY_SpaceId = "SpaceId";
    public static final String INTENT_KEY_userId = "userId";
    public static final String INTENT_KEY_userToken = "userToken";
    public static final String INTENT_KEY_firstName = "firstName";
    public static final String INTENT_KEY_lastName = "lastName";
    public static final String INTENT_KEY_userEmail = "userEmail";
    public static final String INTENT_KEY_paymentResult = "paymentResult";
    public static final String INTENT_KEY_StartScreen = "startScreen";
    public static final String INTENT_KEY_TransactionId = "Transactionid";
    public static final String RATING = "Rating";

    // other constants
    public static final String USER_FIRST_NAME = "userFirstName";
    public static final String USER_LAST_NAME = "userLastName";
    public static final String USER_EMAIL = "user_email";
    public static final String USERID = "user_id";
    public static final String inputPattern  = "dd-MMM-yyyy hh:mm a";
    public static final String outputPattern = "yyyy-MM-dd HH:mm:ss";
    public static final String shownDateFormat = "dd-MMM-yyyy";
    public static final String shownTimeFormat = "hh:mm a";
    public static final String TOKEN = "token";
    public static final String CITY_ID = "cityId";
    public static final String CITY_NAME = "cityName";
    public static final String FIRST_TIME_INSTALLED = "firstTimeInstalled";
    public static final String DASHBOARD_FIRST_TIME_VISITED = "dashboardFirstTimeVisited";
    public static final String FIREBASE_NOTIFICATION_TOKEN = "firebaseNotificationToken";
    public static final String TRANSACTION_ID="Transactionid";

    // navigated screens id
    public static final int HOME_SCREEN_ID = 201;
    public static final int INFO_SCREEN_ID = 202;
    public static final int START_SCREEN_ID = 203;

    // start activity intent requests constants
    public static final int SEARCH_CITY_REQUEST = 101;
    public static final int FILTER_REQUEST = 103;
    public static final int CURRENT_LOCATION_ACCESS_REQUEST = 102;
    public static final int SIGN_IN_REQUEST = 104;
    public static final int SIGN_UP_REQUEST = 105;
    public static final int BOOKING_REQUEST = 106;
    public static final int INTRODUCTION_REQUEST = 107;
    public static final int SPACE_BOOK_REQUEST = 108;
    public static final int PAYMENT_SUCCESS_FAILURE_ACTIVITY_REQUEST = 109;
    public static final int GALLERY_REQUEST = 109;
    public static final int CAMERA_REQUEST = 110;
    public static final int CAMERA_EXTERNAL_STORAGE_PERMISSION = 111;
    public static final int CHANGE_PASSWORD_REQUEST = 112;
    public static final int UPDATE_PROFILE_REQUEST = 113;
    public static final int BOOK_TOUR_SIGN_IN_REQUEST = 114;
    public static final int BOOK_TOUR__SUCCESFUL_BOOKING=116;


    // date and time format static objects
    public static SimpleDateFormat showingDateFormat = new SimpleDateFormat(Common.shownDateFormat, Locale.ENGLISH);
    public static SimpleDateFormat showingTimeFormat = new SimpleDateFormat(Common.shownTimeFormat, Locale.ENGLISH);
    public static SimpleDateFormat appDateFormat = new SimpleDateFormat(Common.inputPattern, Locale.ENGLISH);
    public static SimpleDateFormat serviceDateFormat = new SimpleDateFormat(Common.outputPattern, Locale.ENGLISH);


    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    // broadcast receiver intent filters
    public static final String NOTIFICATION_DATA_RECEIVER = "notificationDataReceiver";
    public static final String PUSH_NOTIFICATION = "pushNotification";
}
