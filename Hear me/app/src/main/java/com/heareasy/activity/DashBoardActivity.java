package com.heareasy.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.heareasy.R;
import com.heareasy.fragment.RecordFragment;
import com.heareasy.others.WelcomePref;
import com.heareasy.common_classes.API_LIST;
import com.heareasy.common_classes.MethodFactory;
import com.heareasy.common_classes.MyStateView;
import com.heareasy.common_classes.MyToast;
import com.heareasy.common_classes.OnClearFromRecentService;
import com.heareasy.common_classes.SessionManager;
import com.heareasy.fragment.BluetoothDeviceFragment;
import com.heareasy.fragment.DashBoardFragment;
import com.heareasy.fragment.InputDeviceFragment;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.heareasy.common_classes.MethodFactory.deleteCache;

public class DashBoardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MyStateView.ProgressClickListener {
    static {
        System.loadLibrary("Mp3Codec");
    }

    public static final String MY_PREFS_NAME = "TargetView";
    private static final String TAG = "DashBoardActivity";
    Context context = DashBoardActivity.this;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    public static BottomNavigationView mbottomNavigationView;
    private Toolbar toolbar_dashBoard;
    public static TextView tv_toolbar_tittle;
    private AlertDialog dialog;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefTarget;
    private MyStateView mLoadingBar;
    private SessionManager sessionManager;
    private FingerprintManagerCompat managerCompat;
    WelcomePref welcomePref;
    private String tag = "";
    private boolean doubleBackToExitPressedOnce = true;
    private ImageView bnav_setting;
    private File directory;
    private int check = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //checkPermission();
        managerCompat = FingerprintManagerCompat.from(this);
        sessionManager = new SessionManager(this);
        welcomePref = new WelcomePref(this);
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));

        if (getIntent().getStringExtra("tag") != null) {
            tag = getIntent().getStringExtra("tag");
        }


        if (sessionManager.isFirstRun()) {
            sessionManager.setFirstRun(false);
            if (managerCompat.isHardwareDetected() && managerCompat.hasEnrolledFingerprints()) {
                if (sessionManager.isFingerPrint()) {
                    showFingerPrintDialog();
                }
            }
        }

        setContentView(R.layout.drawer);
        init();
        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
            View headerView = mNavigationView.getHeaderView(0);
            CircleImageView profile_image = headerView.findViewById(R.id.iv_nav_header_profile_image);
            TextView name = headerView.findViewById(R.id.tv_header_name);
            TextView email = headerView.findViewById(R.id.tv_header_email);

            Picasso.get().load(API_LIST.BASE_IMAGE_URL + sessionManager.getUserImage()).placeholder(R.drawable.ic_account_circle_24).into(profile_image);
            name.setText(sessionManager.getUserFirstName() + " " + sessionManager.getUserLastName());
            email.setText(sessionManager.getUserEmail());
            ImageButton iBtn_nav_close = headerView.findViewById(R.id.iBtn_nav_close);
            iBtn_nav_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
//                    overridePendingTransition(0, 0);
//                    startActivity(getIntent());
//                    overridePendingTransition(0, 0);
//                    finish();
                }
            });
        }

        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar_dashBoard,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        bnav_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //loadFragment(new SettingFragment());
                startActivity(new Intent(DashBoardActivity.this, SettingActivity.class));
            }
        });

        if (tag.equalsIgnoreCase("speaker")) {
            mbottomNavigationView.getMenu().findItem(R.id.bnav_speaker).setChecked(true);
            Fragment ldf = new BluetoothDeviceFragment();
            loadFragment(ldf);
            startActivity(new Intent(DashBoardActivity.this, SettingActivity.class));


        } else {
            mbottomNavigationView.getMenu().findItem(R.id.bnav_home).setChecked(true);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_dashBoard, new DashBoardFragment())
                    .commit();

        }
    }


    // Method for fingerprint dialog
    private void showFingerPrintDialog() {
        FingerprintDialog fragment = new FingerprintDialog();
        fragment.setContext(this);
        fragment.setCancelable(false);
        fragment.show(getSupportFragmentManager(), "");
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        toolbar_dashBoard = findViewById(R.id.toolbar_dashBoard);
        tv_toolbar_tittle = findViewById(R.id.tv_homeToolbar_tittle);
        mbottomNavigationView = findViewById(R.id.bottom_navigation);
        bnav_setting = findViewById(R.id.bnav_setting);
        tv_toolbar_tittle.setText("DashBoard");
        mLoadingBar = new MyStateView(this, null);
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        prefTarget = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        showHomePrompt();
    }


    private void showSpeakerPrompt() {
        showPrompt(R.id.bnav_speaker, "Speaker", "This is Speaker", "speaker");
    }

    private void showNewRecordingPrompt() {
        showPrompt(R.id.nav_new_recording, "New Recording", "This is New Recording", "newRecording");
    }

    private void showHomePrompt() {
        if (!prefTarget.getBoolean("didShowPrompt", false)) {
            showPrompt(R.id.bnav_home, "Home", "This is Home", "home");
        }
    }

    // Method to show instructions
    private void showPrompt(int id, String title, String discription, String check) {
        TapTargetView.showFor(this,
                TapTarget.forView(findViewById(id), title, discription)
                        // All options below are optional
                        .outerCircleColor(R.color.colorPrimary)
                        .outerCircleAlpha(0.96f)
                        .targetCircleColor(R.color.white)
                        .titleTextSize(20)
                        .titleTextColor(R.color.white)
                        .descriptionTextSize(10)
                        .descriptionTextColor(R.color.white)
                        .textColor(R.color.white)
                        .textTypeface(Typeface.SANS_SERIF)
                        .dimColor(R.color.black)
                        .drawShadow(true)
                        .cancelable(false)
                        .tintTarget(true)
                        .transparentTarget(false)
                        .targetRadius(60),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        editor.putBoolean("didShowPrompt", true);
                        editor.apply();
                        if (check.equalsIgnoreCase("home")) {
                            showSpeakerPrompt();
                        } else if (check.equalsIgnoreCase("speaker")) {
                            showNewRecordingPrompt();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

       /* if (tag.equalsIgnoreCase("speaker")) {
            mbottomNavigationView.getMenu().findItem(R.id.bnav_speaker).setChecked(true);
            Fragment ldf = new BluetoothDeviceFragment();
            Bundle args = new Bundle();
            args.putString("data", "1");
            ldf.setArguments(args);
            loadFragment(ldf);
        }
        else {
            mbottomNavigationView.getMenu().findItem(R.id.bnav_home).setChecked(true);
            getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.container_dashBoard, new DashBoardFragment())
            .commit();
        }*/

        mbottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment ldf = new BluetoothDeviceFragment();
                Bundle args = new Bundle();
                switch (item.getItemId()) {
                    case R.id.bnav_home:
                        check = 1;
                        loadFragment(new DashBoardFragment());
                        break;
                    case R.id.bnav_speaker:
                        check = 2;

                        loadFragment(new BluetoothDeviceFragment());
                        break;
                    case R.id.nav_new_recording:
                        check = 3;
                        if (welcomePref.isSubscriptionStatus()) {
                            loadFragment(new RecordFragment());
                        } else if (!sessionManager.getTrialStatus().equalsIgnoreCase("0")) {
                            loadFragment(new RecordFragment());
                        } else {
                            MyToast.display(context, "Please Subscribe A Plan First!");
                        }
//                        startActivity(new Intent(DashBoardActivity.this,RecordActivity.class));
                        break;
                }
                return true;
            }
        });
        mbottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bnav_home:
                        loadFragment(new DashBoardFragment());
                        break;
                    case R.id.bnav_speaker:
                        check = 0;
                        loadFragment(new BluetoothDeviceFragment());
                        break;
                    case R.id.nav_new_recording:
                        check = 3;
                        if (welcomePref.isSubscriptionStatus()) {
                            loadFragment(new RecordFragment());
                        } else if (!sessionManager.getTrialStatus().equalsIgnoreCase("0")) {
                            loadFragment(new RecordFragment());
                        } else {
                            MyToast.display(context, "Please Subscribe A Plan First!");
                        }
//                        startActivity(new Intent(DashBoardActivity.this,RecordActivity.class));
                        break;
                }
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //  mDrawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.nav_home:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                loadFragment(new DashBoardFragment());
                break;
            case R.id.nav_audioList:
                startActivity(new Intent(context, AudioRecordingListActivity.class));
                break;
            case R.id.nav_profile:
                startActivity(new Intent(context, ProfileActivity.class));
                break;
            case R.id.nav_new_recording:
                if (welcomePref.isSubscriptionStatus()) {
                    startActivity(new Intent(context, RecordFragment.class));
                } else if (!sessionManager.getTrialStatus().equalsIgnoreCase("0")) {
                    startActivity(new Intent(context, RecordFragment.class));
                } else {
                    MyToast.display(context, "Please Subscribe A Plan First!");
                }
                break;
            case R.id.nav_input_devices:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                loadFragment(new InputDeviceFragment());
                break;
            case R.id.nav_updates:
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                break;
            case R.id.nav_contact_us:
                startActivity(new Intent(context, ContactUsActivity.class));
                break;
            case R.id.nav_about_us:
                startActivity(new Intent(context, AboutUsActivity.class));
                break;
            case R.id.nav_customer_support:
                startActivity(new Intent(context, CustomerSupportActivity.class));
                break;
            case R.id.nav_faq:
                startActivity(new Intent(context, FaqActivity.class));
                break;
            case R.id.nav_eula:
                startActivity(new Intent(context, EulaActivity.class));
                break;
            case R.id.nav_logout:
                logout();
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        } else if (mbottomNavigationView.getSelectedItemId() != R.id.bnav_home) {
            mbottomNavigationView.setSelectedItemId(R.id.bnav_home);
            mbottomNavigationView.getChildAt(0).setSelected(true);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                deleteCache(this);
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            MyToast.display(this, "Please click BACK again to Exit");

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    // Logout user from app
    public void logout() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        final View dialogView = getLayoutInflater().inflate(R.layout.logout_dialog, null);
        alert.setView(dialogView);
        Button yes = dialogView.findViewById(R.id.btn_yes_logout);
        Button no = dialogView.findViewById(R.id.btn_no_logout);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callLogOutAPI();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog = alert.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    // Logout API
    private void callLogOutAPI() {
        dialog.dismiss();
        mLoadingBar.showLoading();
        AndroidNetworking.post(API_LIST.LOGOUT)
                .addHeaders("Authorization", sessionManager.getAuthToken())
                .addHeaders("id", sessionManager.getUserID())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse:>>>>>>>>>" + response.toString());
                        try {
                            if (response.getString("responseCode").equalsIgnoreCase("0")) {
                                mLoadingBar.showContent();
                                directory = ContextCompat.getExternalFilesDirs(DashBoardActivity.this, Environment.DIRECTORY_MUSIC)[0];
                                //   File dir = getApplicationContext().getExternalFilesDir("/Hear Me");
                            /*    if (directory.isDirectory()) {
                                    String[] children = directory.list();
                                    for (int i = 0; i < children.length; i++) {
                                        new File(directory, children[i]).delete();
                                    }
                                }*/
                                sessionManager.setloggedin(false);
                                sessionManager.logoutUser();
                                startActivity(new Intent(DashBoardActivity.this, SignInActivity.class));
                                finishAffinity();

                                MyToast.display(context, "" + response.getString("responseMessage"));
                            } else if (response.getString("responseCode").equalsIgnoreCase("401")) {
                                mLoadingBar.showContent();
                                MethodFactory.forceLogoutDialog(DashBoardActivity.this);
                            } else {
                                mLoadingBar.showContent();
                                MyToast.display(context, "" + response.getString("responseMessage"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e(TAG, "onError:>>>>" + error.getMessage());
                        mLoadingBar.showContent();
                    }
                });
    }

    // Method to load fragment
    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_dashBoard, fragment)
                .commit();
    }

    @Override
    public void onRetryClick() {

    }


    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
    }
}