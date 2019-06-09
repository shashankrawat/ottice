package com.ottice.ottice.services;

/**
 * TODO: Add a class header comment!
 */
public class ServiceConstants {

    // Service Urls
    private static final String MAIN_URL                     = "http://132.148.87.196:8022/api";
    public static final String GET_CITY_URL                 =  MAIN_URL + "/UserDashBoard/GetProviderCity";
    public static final String PROVIDER_SPACE_INFO_URL      =  MAIN_URL + "/UserDashBoard/GetProvidersSpaceInfo";
    public static final String SIGN_UP_URL                  =  MAIN_URL + "/Profile/Register";
    public static final String VERIFICATION_URL             =  MAIN_URL + "/Profile/Verify";
    public static final String LOGIN_URL                    =  MAIN_URL + "/profile/Login";
    public static final String FORGET_PASSWORD_URL          =  MAIN_URL + "/Profile/ForgotPassword";
    public static final String RESET_PASSWORD_URL           =  MAIN_URL + "/Profile/ResetPassword";
    public static final String SPACE_TYPE_URL               =  MAIN_URL + "/Search/GetOfficeType";
    public static final String FILTER_SPACES_URL            =  MAIN_URL + "/Search/GetProviderFilterData";
    public static final String INVOICE_URL                  =  MAIN_URL + "/Transaction/BookSpace";
    public static final String SOCIAL_LOGIN_URL             =  MAIN_URL + "/Profile/SocialLoginFB";
    public static final String PAYU_HASH_GENERATION_URL     =  MAIN_URL + "/Payumoney/GetPayuMoneyHash";
    public static final String GET_AMENITIES_URL            =  MAIN_URL + "/Search/GetAmenities";
    public static final String GET_USER_DETAILS_URL         =  MAIN_URL + "/Profile/GetUserDetails";
    public static final String SPACE_PAYMENT_URL            =  MAIN_URL + "/Transaction/SpacePayment";
    public static final String UPDATE_USER_DETAILS_URL      =  MAIN_URL + "/Profile/UpdateUserDetails";
    public static final String CHANGE_PASSWORD_URL          =  MAIN_URL + "/Profile/ChangedPassword";
    public static final String MY_BOOKINGS_URL              =  MAIN_URL + "/Search/MyBookings";
    public static final String BOOK_A_SERVICE_URL           =  MAIN_URL + "/Transaction/BookaTour";
    public static final String MY_BOOKING_DETAILS_URL       =  MAIN_URL + "/Search/BookingDetails";
    public static final String REVIEW_RATING                =  MAIN_URL + "/Search/ReviewAndRating";
    public static final String SEARCH_USER_BY_MOBILE_NO_URL =  MAIN_URL + "/Profile/SearchUserByMobileNo";
    public static final String MOBILE_LOGIN_URL             =  MAIN_URL + "/Profile/MobileLogin";

    public static final String PRIVACY_POLICY_URL           =  "";


    // Service Key constants
    public static final String KEY_header = "header";
    public static final String KEY_DeviceId = "DeviceId";
    public static final String KEY_Platform = "Platform";

    public static final String KEY_data = "data";
    public static final String KEY_Latitude = "Latitude";
    public static final String KEY_Longitude = "Longitude";

    // Dashboard service DATA Key constants
    public static final String KEY_Name = "Name";
    public static final String KEY_Address = "Address";
    public static final String KEY_ProviderId = "ProviderId";
    public static final String KEY_State = "State";
    public static final String KEY_City = "City";
    public static final String KEY_OfficeType = "OfficeType";
    public static final String KEY_imagedata = "imagedata";
    public static final String KEY_SpaceName = "SpaceName";
    public static final String KEY_PriceRange = "PriceRange";
    public static final String KEY_SpaceTypeId = "SpaceTypeId";
    public static final String KEY_CityId = "CityId";
    public static final String KEY_Capacity = "Capacity";
    public static final String KEY_ProviderList = "ProviderList";
    public static final String KEY_Space_Plan = "Space_Plan";
    public static final String KEY_Price = "Price";
    public static final String KEY_Seat = "Seat";
    public static final String KEY_Duration = "Duration";
    public static final String KEY_StartDateTime = "StartDateTime";
    public static final String KEY_amenities = "amenities";
    public static final String KEY_status = "status";
    public static final String KEY_amountpaid = "AmountPaid";
    public static final String KEY_iscompleted = "iscompleted";

    // Service Response Key constants
    public static final String KEY_response = "response";
    public static final String KEY_ResponseCode = "ResponseCode";
    public static final String KEY_ResponseMessage = "ResponseMessage";
    public static final String KEY_Token = "Token";

    // device type value for service
    public static final String Platform_Value = "Gcm";

    // service response codes
    public static final int user_type = 2;
    public static final int success_code = 109;
    public static final int error_code = 500;
    public static final int change_password_success_code = 114;
    public static final int forgot_password_error_code = 902;
    public static final int forgot_password_success_code = 900;
    public static final int reset_password_success_code = 113;
    public static final int reset_password_error_code = 105;
    public static final int amenities_success_code = 115;
    public static final int no_user_found_code = 107;

    // city service data key constants
    public static final String KEY_ProviderCity = "ProviderCity";
    public static final String KEY_id = "id";
    public static final String KEY_CityName = "CityName";
    public static final String KEY_TotalLocation = "TotalLocation";

    // provider description data key constants
    public static final String KEY_ProviderSpaceImages = "ProviderSpaceImages";
    public static final String KEY_ImageData = "ImageData";
    public static final String KEY_PhoneNo = "PhoneNo";
    public static final String KEY_Title = "Title";
    public static final String KEY_Description = "Description";
    public static final String KEY_TotalSeat = "TotalSeat";
    public static final String KEY_Amenities = "Amenities";
    public static final String KEY_Id = "Id";
    public static final String KEY_GoodFor = "GoodFor";
    public static final String KEY_AverageRating="AverageRating";
    public static final String KEY_Total_Reviews="TotalReviews";
    public static final String KEY_Reviewrating="ReviewRating";

    // sign up data key constants
    public static final String KEY_FirstName = "FirstName";
    public static final String KEY_LastName = "LastName";
    public static final String KEY_Email = "Email";
    public static final String KEY_Password = "Password";
    public static final String KEY_UserId = "UserId";
    public static final String KEY_UserName = "UserName";
    public static final String KEY_ConfirmCode = "ConfirmCode";
    public static final String KEY_ConfirmPassword = "ConfirmPassword";
    public static final String KEY_UserType = "UserType";
    public static final String KEY_ReviewText = "ReviewText";
    public static final String KEY_Rating = "Rating";


    // verificaton data key constants
    public static final String KEY_Code = "Code";

    // booking data key constants
    public static final String KEY_spaceID = "spaceID";
    public static final String KEY_TransactionID = "TransactionID";
    public static final String KEY_Net_Total = "Net_Total";
    public static final String KEY_Service_Tax = "Service_Tax";
    public static final String KEY_SBCess = "SBCess";
    public static final String KEY_KKCess = "KKCess";
    public static final String KEY_Total = "Total";
    public static final String KEY_paymentmode="PaymentMode";
    public static final String KEY_BOOKEDBY="BookedBy";
    public static final String KEY_spaceid="SpaceId";


    // fb Login service data key constants
    public static final String KEY_FBID = "FBID";
    public static final String KEY_Lastname = "Lastname";


    // space payment key constants
    public static final String KEY_transactionID = "transactionID";
    public static final String KEY_Total_Price = "Total_Price";
    public static final String KEY_Payment_Type = "Payment_Type";


    // profile service key constants
    public static final String KEY_ImageString = "ImageString";
    public static final String KEY_Imagedata = "Imagedata";
    public static final String KEY_MiddleName = "MiddleName";


    // change password service key constants
    public static final String KEY_OldPassword = "OldPassword";
    public static final String KEY_NewPassword = "NewPassword";

    // my bookings service key constants
    public static final String KEY_Status = "Status";
    public static final String KEY_FromDate = "FromDate";
    public static final String KEY_ToDate = "ToDate";

    // submit booking details key constant
    public static final String KEY_mobileno="MobileNumber";
    public static final String KEY_datetime="DateTime";


    // OTP generations key constants
    public static final String URL = "https://control.msg91.com/api/sendotp.php?";
    public static String VERIFY_URL = "https://control.msg91.com/api/verifyRequestOTP.php?";
    public static String AUTH_KEY = "150038Am35Psq9n58fd9d68";
    public static String SENDER_ID = "Ottice";
    public static String route = "default";
    public static final String KEY_application = "mGdzHc5u_bgj-z0zkmuYnzXKi0Rrmd4fiAGEhHAYKrbqb-TGx1uJaiXVgk9QVDxSxyZ28BYkWGe2M4M5jPeL8173MnFvxbO4j_ZChIluzLLWPPv1aV11U4HV1uQeC1kzppFk2bEO9L3pSZQZk54gRQ==";

}
