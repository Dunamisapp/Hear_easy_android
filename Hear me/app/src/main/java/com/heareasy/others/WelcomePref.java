package com.heareasy.others;

import android.content.Context;
import android.content.SharedPreferences;

public class WelcomePref {
    private static final String SUBSCRIPTION_STATUS = "SUBSCRIPTION_STATUS";
    private static final String CONNETED_DEVICE = "connected_device";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "welcome";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public WelcomePref(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.apply();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setSubscriptionStatus(boolean isStatus) {
        editor.putBoolean(SUBSCRIPTION_STATUS, isStatus);
        editor.commit();
    }

    public boolean isSubscriptionStatus() {
        return pref.getBoolean(SUBSCRIPTION_STATUS, false);
    }

    public void setConnectedDevice(boolean isConnected) {
        editor.putBoolean(CONNETED_DEVICE, isConnected);
        editor.commit();
    }

    public boolean isConnectedDevice() {
        return pref.getBoolean(CONNETED_DEVICE, false);
    }
}
