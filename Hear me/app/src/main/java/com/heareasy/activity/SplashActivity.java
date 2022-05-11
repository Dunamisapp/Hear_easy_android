package com.heareasy.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.heareasy.R;
import com.heareasy.common_classes.SessionManager;
import com.heareasy.others.WelcomePref;

import java.io.File;


public class SplashActivity extends AppCompatActivity {
    Context context = SplashActivity.this;
    final static String[] PERMISSIONS = { Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.INTERNET,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.BLUETOOTH_SCAN
    };
    private static final int PERMISSION_ALL = 2;
    private WelcomePref prefManager;
    private File[] files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prefManager = new WelcomePref(this);
        new SessionManager(this).setFirstRun(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                if (hasPermissions(SplashActivity.this, PERMISSIONS)) {
                    Intent intent = new Intent(SplashActivity.this, BetaReleaseDoc.class);
                    startActivity(intent);
                    finish();
               /* }
                else { ActivityCompat.requestPermissions(SplashActivity.this, PERMISSIONS, PERMISSION_ALL);
                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();}*/

            }
        }, 3000);
    }


 /*   public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }*/

 /*   @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_ALL: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with all permissions
                perms.put(Manifest.permission.BLUETOOTH, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.BLUETOOTH_ADMIN, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.BLUETOOTH_PRIVILEGED, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both intpermissions
                    if (perms.get(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED

                            && perms.get(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED

                            && perms.get(Manifest.permission.BLUETOOTH_PRIVILEGED) == PackageManager.PERMISSION_GRANTED

                            && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

                            && perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED

                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

                            && perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
                    {
                        Log.d("mylog", "service permission granted");
                        if (hasPermissions(SplashActivity.this, PERMISSIONS))
                        {
                            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                    }
                    // process the normal flow
                } else {
                  MyToast.display(SplashActivity.this, "Please grant all the required permissions");
                }
                return;
            }
        }
    }*/
}