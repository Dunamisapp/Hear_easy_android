package com.heareasy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.heareasy.R;
import com.heareasy.common_classes.API_LIST;
import com.heareasy.common_classes.MethodFactory;
import com.heareasy.others.WelcomePref;

import java.io.File;


public class BetaReleaseDoc extends AppCompatActivity {


    private Button btnSubmit;
    private RadioGroup groupRadio;
    private RadioButton radioNo;
    private RadioButton radioYes;
    private int acceptedTC = 0;
    private BetaReleaseDoc mActivity;
    private Toolbar toolbar;
    private int REQUEST_ID_MULTIPLE_PERMISSIONS = 11;
    private File file;
    private FloatingActionButton mFabPopup;
    private int dotsCount;
    private ImageView[] dotss;
    private TextView tvSkip;
    private TextView tvHeader;
    private WelcomePref prefManager;
    private TextView tvText;
    private TextView toolbarTitle;
    private RelativeLayout rootRelative;
    private WebView mWebview;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MethodFactory.fullScreen(this);

        prefManager = new WelcomePref(this);


        setContentView(R.layout.activity_beta_release);

        initVariables();
        WebSettings webSetting = mWebview.getSettings();
        webSetting.setBuiltInZoomControls(true);
        mWebview.setWebViewClient(new WebViewClient());
//        mWebview.loadData(data, "text/html", "UTF-8");
        mWebview.loadUrl(API_LIST.BETA_LICENSE);

        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
        }



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (acceptedTC==0) {
                    MethodFactory.showAlertDialog("Please accept beta release licence.", mActivity);
                }
                else if (acceptedTC==1) {
                    finishAffinity();
                } else if (acceptedTC==2) {
                    Intent intent = new Intent(BetaReleaseDoc.this, WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                }


            }
        });

        groupRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                int selectedId = group.getCheckedRadioButtonId();
                RadioButton mRadioButton = (RadioButton) findViewById(selectedId);
                switch (mRadioButton.getId()) {
                    case R.id.radioNo:
                        acceptedTC = 1;
                        btnSubmit.setEnabled(true);
                        break;
                    case R.id.radioYes:
                        acceptedTC = 2;
                        btnSubmit.setEnabled(true);
                        break;

                }


            }
        });


    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);

        startActivity(new Intent(BetaReleaseDoc.this, SignInActivity.class));
        finish();

    }

    private void initVariables() {

        mActivity = BetaReleaseDoc.this;
        btnSubmit = findViewById(R.id.btnSubmit);
        groupRadio = findViewById(R.id.groupRadio);
        radioNo = findViewById(R.id.radioNo);
        radioYes = findViewById(R.id.radioYes);
        mWebview = findViewById(R.id.webview);
        rootRelative = findViewById(R.id.rootRelative);

    }
}
