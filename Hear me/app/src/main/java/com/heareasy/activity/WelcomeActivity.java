package com.heareasy.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.heareasy.R;
import com.heareasy.others.WelcomePref;
import com.heareasy.adapter.WelcomeAdapter;
import com.heareasy.common_classes.MyToast;
import com.heareasy.common_classes.SessionManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;


@RequiresApi(api = Build.VERSION_CODES.S)
public class WelcomeActivity extends AppCompatActivity {

    ViewPager vp_welcome;
    TextView tv_skip_welcome;
    TextView tv_start_welcome;
    WelcomePref prefManager;

    Context context = WelcomeActivity.this;

    final static String[] PERMISSIONS = { Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
    };
    private static final int PERMISSION_ALL = 2;

    private static final Integer[] LAYOUTS = {R.layout.welcome_screen3,R.layout.welcome_screen1,R.layout.welcome_screen2};
    private ArrayList<Integer> LayoutArray;
    private SessionManager sessionManager;
    private File[] files;
    private File directory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = new WelcomePref(this);
        Log.e("TAG", "isFirstTimeLaunch: "+prefManager.isFirstTimeLaunch() );
        if (!prefManager.isFirstTimeLaunch()) {
            launchLoginScreen();
        }


        sessionManager = new SessionManager(this);

        setContentView(R.layout.activity_main);


        hasPermissions(WelcomeActivity.this, PERMISSIONS);
        ActivityCompat.requestPermissions(WelcomeActivity.this, PERMISSIONS, PERMISSION_ALL);

        vp_welcome = findViewById(R.id.vp_welcome);
        tv_skip_welcome = findViewById(R.id.tv_skip_welcome);
        tv_start_welcome = findViewById(R.id.tv_start_welcome);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        LayoutArray = new ArrayList<Integer>();

        for (int i = 0; i < LAYOUTS.length; i++) {
            LayoutArray.add(LAYOUTS[i]);
        }

        vp_welcome.setAdapter(new WelcomeAdapter(
                this, LayoutArray));

        CircleIndicator indicator_main = findViewById(R.id.indicator_main);
        indicator_main.setViewPager(vp_welcome);

        vp_welcome.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == vp_welcome.getAdapter().getCount() - 1) {

                    tv_skip_welcome.setVisibility(View.GONE);
                    tv_start_welcome.setVisibility(View.VISIBLE);
                    tv_start_welcome.setText("Start");
                    tv_start_welcome.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            launchLoginScreen();
                        }
                    });
                } else {
                    tv_start_welcome.setText("Next");
                    tv_skip_welcome.setVisibility(View.VISIBLE);
                    tv_skip_welcome.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            launchLoginScreen();
                        }
                    });
                    tv_start_welcome.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int current = getItem(+1);
                            if (current < LAYOUTS.length) {
                                // move to next screen
                                vp_welcome.setCurrentItem(current);
                            }

                        }
                    });
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        directory =  ContextCompat.getExternalFilesDirs(WelcomeActivity.this, Environment.DIRECTORY_MUSIC)[0];
        if (directory.listFiles() != null) {
            files = directory.listFiles();



            if (files.length >= 0) {
                Log.e("Files", "Size: " + files.length);
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    if (file.getName().contains("Recording_"))
                    file.delete();


                }
            }
        }
    }

    private int getItem(int i) {
        return vp_welcome.getCurrentItem() + i;
    }

    private void launchLoginScreen() {
        prefManager.setFirstTimeLaunch(false);
            startActivity(new Intent(WelcomeActivity.this, SignInActivity.class));
            finishAffinity();

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
        } else {
//            checkPermission();
        }
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
                        if (hasPermissions(WelcomeActivity.this, PERMISSIONS))
                        {
                            Intent intent = new Intent(WelcomeActivity.this, WelcomeActivity.class);
                            startActivity(intent);
                        }
                    }
                    // process the normal flow
                } else {
                    MyToast.display(WelcomeActivity.this, "Please grant all the required permissions");
                }
                return;
            }
        }
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,}, 1);
            }
        }
    }



}