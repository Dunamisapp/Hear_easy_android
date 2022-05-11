package com.heareasy.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;
import com.heareasy.R;
import com.heareasy.common_classes.API_LIST;
import com.heareasy.common_classes.MethodFactory;
import com.heareasy.common_classes.MyStateView;
import com.heareasy.common_classes.MyToast;
import com.heareasy.common_classes.SessionManager;
import com.heareasy.model.UserProfileModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class ProfileActivity extends AppCompatActivity implements MyStateView.ProgressClickListener {
    private static final String TAG = "ProfileActivity";
    private final Context context = ProfileActivity.this;
    private ImageButton ib_camera_profile;
    private EditText edt_f_name_profile, edt_l_name_profile;
    private EditText edt_email_profile;
    private EditText edt_mobile_profile;
    private Toolbar toolbar_profile;
    private ImageButton btn_edit_profile;
    private ImageView iv_profile;
    private Button btn_save_profile;
    private MyStateView mLoadingBar;
    private SessionManager sessionManager;
    private File imgFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        callGetProfileAPI();
    }


    private void init() {
        toolbar_profile = findViewById(R.id.toolbar_profile);
        edt_f_name_profile = findViewById(R.id.edt_f_name_profile);
        edt_l_name_profile = findViewById(R.id.edt_l_name_profile);
        edt_email_profile = findViewById(R.id.edt_email_profile);
        edt_mobile_profile = findViewById(R.id.edt_mobile_profile);
        btn_edit_profile = findViewById(R.id.btn_edit_profile);
        iv_profile = findViewById(R.id.iv_profile);
        btn_save_profile = findViewById(R.id.btn_save_profile);
        ib_camera_profile = findViewById(R.id.ib_camera_profile);

        toolbar_profile.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar_profile.setNavigationOnClickListener(view -> onBackPressed());
        sessionManager = new SessionManager(this);
        mLoadingBar = new MyStateView(this, null);

        btn_save_profile.setEnabled(false);

        btn_edit_profile.setOnClickListener(view -> {


            btn_edit_profile.setVisibility(View.GONE);
            btn_save_profile.setEnabled(true);
            ib_camera_profile.setVisibility(View.VISIBLE);
            btn_save_profile.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_border));

            edt_f_name_profile.setFocusable(true);
            edt_f_name_profile.setFocusableInTouchMode(true);
            edt_f_name_profile.setClickable(true);
            edt_f_name_profile.setCursorVisible(true);

            edt_l_name_profile.setFocusable(true);
            edt_l_name_profile.setFocusableInTouchMode(true);
            edt_l_name_profile.setClickable(true);
            edt_l_name_profile.setCursorVisible(true);

            edt_email_profile.setFocusable(true);
            edt_email_profile.setFocusableInTouchMode(true);
            edt_email_profile.setClickable(true);
            edt_email_profile.setCursorVisible(true);

            edt_mobile_profile.setFocusable(true);
            edt_mobile_profile.setFocusableInTouchMode(true);
            edt_mobile_profile.setClickable(true);
            edt_mobile_profile.setCursorVisible(true);

            iv_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImagePicker.Companion.with(ProfileActivity.this)
                            .crop()
                            .galleryOnly()//Crop image(Optional), Check Customization for more option
                            .compress(1024)            //Final image size will be less than 1 MB(Optional)
                            .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                            .start();
                }
            });

        });

        // Get profile data
        callGetProfileAPI();

        btn_save_profile.setOnClickListener(view -> {
            // Update profile data
            callUpdateProfileAPI();
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Log.e(TAG, "onActivityResult: >>>>>>>" + data.getData());
            Uri imgUri = data.getData();
            imgFile = new File(imgUri.getPath());
            iv_profile.setImageURI(imgUri);
        }
    }

    // Update UI
    private void updateUI(UserProfileModel profileModel) {

        Picasso.get().load(API_LIST.BASE_IMAGE_URL + profileModel.getData().getImage() + "").placeholder(R.drawable.profile).into(iv_profile);
        edt_f_name_profile.setText(profileModel.getData().getFirstName());
        edt_l_name_profile.setText(profileModel.getData().getLastName());
        edt_email_profile.setText(profileModel.getData().getEmail());
        edt_mobile_profile.setText(profileModel.getData().getMobileNumber());
        sessionManager.setUserFirstName(profileModel.getData().getFirstName());
        sessionManager.setUserLastName(profileModel.getData().getLastName());
        sessionManager.setUserEmail(profileModel.getData().getEmail());
        sessionManager.setUserImage(profileModel.getData().getImage() + "");
    }

    // Profile data API
    private void callGetProfileAPI() {
        mLoadingBar.showLoading();
        AndroidNetworking.get(API_LIST.GET_PROFILE)
                .addHeaders("Authorization", sessionManager.getAuthToken())
                .addHeaders("id", sessionManager.getUserID())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse:>>>>>>>>>" + response.toString());
                        Gson gson = new Gson();
                        UserProfileModel profileModel = gson.fromJson(response.toString(), UserProfileModel.class);
                        try {
                            if (response.getString("responseCode").equalsIgnoreCase("200")) {
                                mLoadingBar.showContent();
                                updateUI(profileModel);
                            } else if (response.getString("responseCode").equalsIgnoreCase("401")) {
                                mLoadingBar.showContent();
                                MethodFactory.forceLogoutDialog(ProfileActivity.this);
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
                        mLoadingBar.showRetry();
                    }
                });
    }

    // Update profile data
    private void callUpdateProfileAPI() {
        mLoadingBar.showLoading();
        AndroidNetworking.upload(API_LIST.UPDATE_PROFILE)
                .addHeaders("Authorization", sessionManager.getAuthToken())
                .addHeaders("id", sessionManager.getUserID())
                .addMultipartParameter("first_name", edt_f_name_profile.getText().toString())
                .addMultipartParameter("last_name", edt_l_name_profile.getText().toString())
                .addMultipartParameter("email", edt_email_profile.getText().toString())
                .addMultipartParameter("mobileNumber", edt_mobile_profile.getText().toString())
                .addMultipartFile("profile_pic", imgFile)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse:>>>>>>>>>" + response.toString());
                        try {
                            if (response.getString("responseCode").equalsIgnoreCase("200")) {
                                mLoadingBar.showContent();
                                callGetProfileAPI();
                                MyToast.display(context, "" + response.getString("responseMessage"));
                                finish();
                                startActivity(getIntent());
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
                        mLoadingBar.showRetry();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        startActivity(new Intent(context,DashBoardActivity.class));
//        finish();
    }

    @Override
    public void onRetryClick() {

    }
}