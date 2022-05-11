package com.heareasy.common_classes;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Nimish Nandwana on 27/01/2021.
 * Description -
 */

public class OnClearFromRecentService  extends Service {
    SessionManager sessionManager;

    private static final int START_NOT_STICKY = 1;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sessionManager = new SessionManager(getBaseContext());
        Log.d("ClearFromRecentService", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("ClearFromRecentService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        sessionManager.setFirstRun(true);
        Log.e("ClearFromRecentService", "END"+sessionManager.isFirstRun());
        //Code here
        stopSelf();
    }
}