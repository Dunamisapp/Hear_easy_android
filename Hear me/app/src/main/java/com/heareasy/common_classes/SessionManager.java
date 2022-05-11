package com.heareasy.common_classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String USER_ID = "user_id";
    private static final String PASSWD = "password";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String OTP_TAG = "otp";
    private static final String ID = "id";
    private static final String EMAIL = "email";
    private static final String MOBILE = "mobile";
    private static final String NAME = "name";
    private static final String IMAGE = "user_image";
    private static final String ANROID_ID = "android_id";
    private static final String NOTIFICATION_STATUS = "notification_status";
    private static final String MERCHANT_NAME = "merchant_name";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "last_name";
    private static final String MOBILE_NUMBER = "mobile_number";
    private static final String AUTH_TOKEN = "auth_token";
    private static final String USER_EMAIL = "user_email";
    private static final String MERCHANT_ID = "merchant_id";
    private static final String CURRENT_POINTS = "current_points";
    private static final String MERCHANT_LOGO = "merchant_logo";
    private static final String CARD_NUMBER = "card_number";
    private static final String JOINING_DATE = "joining_date";
    private static final String EARNED_POINTS = "earned_points";
    private static final String REDEEM_POINTS = "redeem_pts";
    private static final String total_visits = "total_visits";
    private static final String QR_CODE = "qr_code";
    private static final String OLD_MERCHANT_ID = "old_merchant_id";
    private static final String BIRTHDAY = "birthday";
    private static final String USER_NAME = "user_name";
    private static final String SUBSCRIPTION_STATUS = "subscription_status";
    private static final String IS_FINGER_PRINT = "isFingerPrint";
    private static final String trialstartddate = "trialstartddate";
    private static final String TrialsDays = "TrialsDays";


    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    // shared pref mode
    private int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "hear_me.session";

    private static final String IS_LOGGED_IN = "IS_LOGGED_IN";
    private static final String LOGGED_ID = "LOGGED_ID";
    private static final String Auth_Token = "Auth_Token";
    private static final String Todays_Punch = "Todays_Punch";
    private static final String RESET_PASSWORD_TOKEN = "reset_password_token";
    private static final String IS_FIRST_RUN = "firstrun";
    private static final String TRIAL_STATUS = "trialStatus";
    private static final String LAST_PLAN_ID = "";
    private static final String PAYBLE_AMOUNT = "payable_amount";
    private static final String TOTAL_DISCOUNT = "total_discount";
    private static final String PACKAGE_AMOUNT = "package_amount";
    private static final String EXPIRY_DATE = "expiry_date";
    private static final String IS_KARAOKE_MODE = "is_karaoke_mode";


    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setloggedin(boolean isloggedin) {
        editor.putBoolean(IS_LOGGED_IN, isloggedin);
        editor.commit();
    }

    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_LOGGED_IN, false);
    }

    public void setUserID(String user_id) {
        editor.putString(USER_ID, user_id);
        editor.commit();
    }

    public String getUserID() {
        return pref.getString(USER_ID, "");
    }


    public void setID(String user_id) {
        editor.putString(ID, user_id);
        editor.commit();
    }




    public void setUserImage(String user_image) {
        editor.putString(IMAGE, user_image);
        editor.commit();
    }

    public String getUserImage() {
        return pref.getString(IMAGE, "");
    }


    public void setTrialstartddate(String trialstartddate) {
        editor.putString(this.trialstartddate, trialstartddate);
        editor.commit();
    }

    public String getTrialstartddate() {
        return pref.getString(trialstartddate, "");
    }


    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

    public void setAndroidID(String android_id) {
        editor.putString(ANROID_ID, android_id);
        editor.commit();
    }

    public String getAndroidID() {
        return pref.getString(ANROID_ID, "");
    }

    public void setNotificationStatus(boolean android_id) {
        editor.putBoolean(NOTIFICATION_STATUS, android_id);
        editor.commit();
    }

    public boolean isNotificationTurnedON() {

        return pref.getBoolean(NOTIFICATION_STATUS, true);

    }


    public void setUserFirstName(String firstName) {
        editor.putString(FIRST_NAME, firstName);
        editor.commit();
    }

    public String getUserFirstName() {
        return pref.getString(FIRST_NAME, "");
    }

    public void setTrailsDays(int TrialsDays) {
        editor.putInt(this.TrialsDays, TrialsDays);
        editor.commit();
    }

    public int getTialDays() {
        return pref.getInt(TrialsDays, 0);
    }

    public void setUserLastName(String lastName) {
        editor.putString(LAST_NAME, lastName);
        editor.commit();

    }

    public String getUserLastName() {
        return pref.getString(LAST_NAME, "");
    }

    public void setUserMobileNumber(String mobileNumber) {
        editor.putString(MOBILE_NUMBER, mobileNumber);
        editor.commit();
    }

    public String getUserMobileNumber() {
        return pref.getString(MOBILE_NUMBER, "");
    }


    public void setAuthToken(String authorizationKey) {
        editor.putString(AUTH_TOKEN, authorizationKey);
        editor.commit();

    }

    public String getAuthToken() {
        return pref.getString(AUTH_TOKEN, "");
    }
    public void setResetPasswordToken(String reset_password_token) {
        editor.putString(RESET_PASSWORD_TOKEN, reset_password_token);
        editor.commit();

    }

    public String getResetPasswordToken() {
        return pref.getString(RESET_PASSWORD_TOKEN, "");
    }


    public void setUserEmail(String email) {
        editor.putString(USER_EMAIL, email);
        editor.commit();

    }

    public String getUserEmail() {
        return pref.getString(USER_EMAIL, "");
    }

    public void setUserName(String userName) {
        editor.putString(USER_NAME, userName);
        editor.commit();

    }

    public String getUserName() {
        return pref.getString(USER_NAME, "");
    }


    public void setSubscriptionStatus(String status) {
        editor.putString(SUBSCRIPTION_STATUS, status);
        editor.commit();

    }

    public String getSubscriptionStatus() {
        return pref.getString(SUBSCRIPTION_STATUS, "");
    }


    public void setTrialStatus(String trialStatus) {
        editor.putString(TRIAL_STATUS, trialStatus);
        editor.commit();

    }

    public String getTrialStatus() {
        return pref.getString(TRIAL_STATUS, "");
    }

    public void setLastPlanId(String planId) {
        editor.putString(LAST_PLAN_ID, planId);
        editor.commit();

    }
    public String getLastPlanId() {
        return pref.getString(LAST_PLAN_ID, "");
    }


    public void setPackageAmount(String package_amount) {
        editor.putString(PACKAGE_AMOUNT, package_amount);
        editor.commit();

    }
    public String getPackageAmount() {
        return pref.getString(PACKAGE_AMOUNT, "");
    }

    public void setPayableAmount(String payable_amount) {
        editor.putString(PAYBLE_AMOUNT, payable_amount);
        editor.commit();

    }
    public String getPayableAmount() {
        return pref.getString(PAYBLE_AMOUNT, "");
    }

    public void setTotalDiscount(String total_discount) {
        editor.putString(TOTAL_DISCOUNT, total_discount);
        editor.commit();

    }
    public String getTotalDiscount() {
        return pref.getString(TOTAL_DISCOUNT, "");
    }

    public void setExpiryDate(String expiry_date) {
        editor.putString(EXPIRY_DATE, expiry_date);
        editor.commit();

    }
    public String getExpiryDate() {
        return pref.getString(EXPIRY_DATE, "");
    }


    public void setFingerPrint(boolean isFingerPrint) {
        editor.putBoolean(IS_FINGER_PRINT, isFingerPrint);
        editor.commit();
    }

    public boolean isFingerPrint() {

        return pref.getBoolean(IS_FINGER_PRINT, false);
    }
    public void setKaraokeMode(boolean is_karaoke_mode) {
        editor.putBoolean(IS_KARAOKE_MODE, is_karaoke_mode);
        editor.commit();
    }

    public boolean isKaraokeMode() {

        return pref.getBoolean(IS_KARAOKE_MODE, false);
    }

    public void setFirstRun(boolean firstrun) {
        editor.putBoolean(IS_FIRST_RUN, firstrun);
        editor.commit();
    }

    public boolean isFirstRun() {
        return pref.getBoolean(IS_FIRST_RUN, false);
    }

}