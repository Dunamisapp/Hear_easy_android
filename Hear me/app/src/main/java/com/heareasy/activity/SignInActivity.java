package com.heareasy.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.heareasy.common_classes.AppConstants;
import com.heareasy.R;
import com.heareasy.others.WelcomePref;
import com.heareasy.common_classes.API_LIST;
import com.heareasy.common_classes.MethodFactory;
import com.heareasy.common_classes.MySnackbar;
import com.heareasy.common_classes.MyStateView;
import com.heareasy.common_classes.MyToast;
import com.heareasy.common_classes.SessionManager;
import com.heareasy.model.MySubscriptionModel;
import com.heareasy.model.UserModel;

import org.json.JSONException;
import org.json.JSONObject;


public class SignInActivity extends AppCompatActivity implements View.OnClickListener, MyStateView.ProgressClickListener {
    private static final String TAG = "SignInActivity";
    private Context context = SignInActivity.this;
    private TextView tv_sing_up;
    private TextView tv_login_forgotPassword;
    private EditText email, password;
    private Button btn_sign_in;
    private MyStateView mLoadingBar;
    private UserModel userModel;
    private SessionManager sessionManager;
    private WelcomePref welcomePref;
    private FingerprintManagerCompat managerCompat;
    private MySubscriptionModel mySubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoadingBar = new MyStateView(this, null);
        welcomePref = new WelcomePref(getApplicationContext());
        sessionManager = new SessionManager(this);
        if (sessionManager.isUserLoggedIn()) {

            if (welcomePref.isSubscriptionStatus()) {
                startActivity(new Intent(context, DashBoardActivity.class));
                finishAffinity();
            } else {
                startActivity(new Intent(context, TrialActivity.class));
                finishAffinity();
              /*  if (sessionManager.getTrialStatus().equalsIgnoreCase("0")){
                    Log.e(TAG, "getTrialStatus: "+sessionManager.getTrialStatus() );
                    startActivity(new Intent(context, TrialActivity.class));
                    finishAffinity();
                }
                else if (sessionManager.getTrialStatus().equalsIgnoreCase("1")){
                    Log.e(TAG, "getTrialStatus: "+sessionManager.getTrialStatus() );
                    startActivity(new Intent(context, DashBoardActivity.class));
                    finishAffinity();
                }*/
            }
        }

        setContentView(R.layout.activity_sign_in);
        init();

        if (welcomePref.isFirstTimeLaunch()) {
            welcomePref.setFirstTimeLaunch(false);
            startActivity(new Intent(SignInActivity.this, WelcomeActivity.class));
            finish();
        }

    }


    private void init() {
        tv_sing_up = findViewById(R.id.tv_sing_up_signIn);
        tv_login_forgotPassword = findViewById(R.id.tv_login_forgotPassword);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        email = findViewById(R.id.edt_email_login);
        password = findViewById(R.id.edt_password_login);
        btn_sign_in.setOnClickListener(this);
        tv_sing_up.setOnClickListener(this);
        tv_login_forgotPassword.setOnClickListener(this);
        mLoadingBar = new MyStateView(this, null);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sign_in:
                if (TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(password.getText())) {
                    MySnackbar.show(context, view, "Fields can't be empty");
                } else if (!AppConstants.isValidEmail(email.getText())) {
                    MySnackbar.show(context, view, "Please enter valid Email");
                } else {
                    // Call login API
                    callLoginAPI();
                }
                break;
            case R.id.tv_sing_up_signIn:
                startActivity(new Intent(context, SignUpActivity.class));
                break;
            case R.id.tv_login_forgotPassword:
                startActivity(new Intent(context, ForgotPasswordActivity.class));
                break;
        }
    }

    public void ShowHidePass(View view) {
        if (view.getId() == R.id.show_pass_btn) {
            if (password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) (view)).setImageResource(R.drawable.show_password);
                //Show Password
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (view)).setImageResource(R.drawable.hide_password);
                //Hide Password
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    // Login User
    private void callLoginAPI() {
        mLoadingBar.showLoading();
        AndroidNetworking.post(API_LIST.LOGIN)
                .addBodyParameter("email", email.getText().toString())
                .addBodyParameter("password", password.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse:>>>>>>>>>" + response.toString());
                        Gson gson = new Gson();
                        userModel = gson.fromJson(response.toString(), UserModel.class);
                        try {
                            if (response.getString("responseCode").equalsIgnoreCase("200")) {
                                mLoadingBar.showContent();
                                MyToast.display(context, "" + response.getString("responseMessage"));
                                sessionManager.setAuthToken(userModel.getData().getAuthorizationKey());
                                sessionManager.setUserID(userModel.getData().getId() + "");
                                sessionManager.setAndroidID(userModel.getData().getDeviceId() + "");
                                sessionManager.setUserFirstName(userModel.getData().getFirstName());
                                sessionManager.setUserLastName(userModel.getData().getLastName());
                                sessionManager.setUserEmail(userModel.getData().getEmail());
                                sessionManager.setUserMobileNumber(userModel.getData().getMobileNumber());
                                sessionManager.setUserImage(userModel.getData().getImage() + "");
                                sessionManager.setUserName(userModel.getData().getUsername() + "");
                                sessionManager.setResetPasswordToken(userModel.getData().getResetPasswordToken() + "");
                                sessionManager.setloggedin(true);
                                callMySubscriptionAPI();
                                callTrialAPI();
                            } else {
                                MyToast.display(context, "" + response.getString("responseMessage"));
                                mLoadingBar.showContent();
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

    private void callTrialAPI() {
        mLoadingBar.showLoading();
        Log.e("Authorization", ">>>>>>>>" + sessionManager.getAuthToken());
        Log.e("id", ">>>>>>>>" + sessionManager.getUserID());
        AndroidNetworking.get(API_LIST.TRAIL_DAYS)
                .addHeaders("Authorization", sessionManager.getAuthToken())
                .addHeaders("id", sessionManager.getUserID())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "Trial>>onResponse:>>>>>>>>>" + response.toString());
                        try {
                            if (response.getString("responseCode").equalsIgnoreCase("200")) {
                                mLoadingBar.showContent();
//                            MyToast.display(context, "" + response.getString("responseMessage"));
                                sessionManager.setTrialStatus(response.getString("trial"));
                                sessionManager.setTrialstartddate(response.getString("trial_date"));
                                Log.e("created_at", ">>>>>>>>>>>" + sessionManager.getTrialstartddate());
                                if (sessionManager.getTrialStatus().equalsIgnoreCase("0")) {
                                }

                            } else if (response.getString("responseCode").equalsIgnoreCase("0")) {
                                mLoadingBar.showContent();
//                            MyToast.display(context, "" + response.getString("responseMessage"));
                                sessionManager.setTrialStatus(response.getString("trial"));
                             /*   sessionManager.setTrialstartddate(response.getString("created_at"));
                                Log.e("created_at", ">>>>>>>>>>>" + sessionManager.getTrialstartddate());*/
                                if (sessionManager.getTrialStatus().equalsIgnoreCase("0")) {
                                }

                            } else if (response.getString("responseCode").equalsIgnoreCase("401")) {
                                mLoadingBar.showContent();
                                MethodFactory.forceLogoutDialog(SignInActivity.this);
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


    private void callMySubscriptionAPI() {
        mLoadingBar.showLoading();
        Log.e("Authorization", ">>>>>>>>" + sessionManager.getAuthToken());
        Log.e("id", ">>>>>>>>" + sessionManager.getUserID());
        AndroidNetworking.get(API_LIST.MY_SUBSCRIPTION)
                .addHeaders("Authorization", sessionManager.getAuthToken())
                .addHeaders("id", sessionManager.getUserID())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("TAG", "onResponse:>>>>>>>>>" + response.toString());
                        Gson gson = new Gson();
                        mySubscription = gson.fromJson(response.toString(), MySubscriptionModel.class);
                        try {
                            if (response.getString("responseCode").equalsIgnoreCase("200")) {
                                mLoadingBar.showContent();
                                welcomePref.setSubscriptionStatus(true);
                                if (mySubscription.getData().getStatus().equalsIgnoreCase("1")) {
                                    startActivity(new Intent(context, DashBoardActivity.class));
                                    finishAffinity();
                                }

                            } else if (response.getString("responseCode").equalsIgnoreCase("401")) {
                                mLoadingBar.showContent();
                                MethodFactory.forceLogoutDialog(SignInActivity.this);
                            } else {
                                if (!sessionManager.getTrialStatus().equalsIgnoreCase("0")) {
                                    Log.e(TAG, "getTrialStatus: " + sessionManager.getTrialStatus());
                                    welcomePref.setSubscriptionStatus(false);
                                    startActivity(new Intent(context, TrialActivity.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(context, DashBoardActivity.class));
                                    finish();
                                }
                            }
//                        else {
//                            startActivity(new Intent(context, TrialActivity.class));
//                            finishAffinity();
//                        }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        mLoadingBar.showRetry();
                        Log.e("TAG", "onError:>>>>" + error.getMessage());
                    }
                });
    }

    @Override
    public void onRetryClick() {
        callLoginAPI();
    }
}