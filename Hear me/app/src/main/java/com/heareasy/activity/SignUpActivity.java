package com.heareasy.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.provider.Settings.Secure;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.heareasy.common_classes.MethodFactory;
import com.heareasy.common_classes.SessionManager;
import com.heareasy.model.MySubscriptionModel;
import com.heareasy.model.UserModel;
import com.heareasy.others.PrivacyPolicyActivity;
import com.heareasy.others.TermsAndConditionsActivity;
import com.heareasy.common_classes.AppConstants;
import com.heareasy.R;
import com.heareasy.common_classes.API_LIST;
import com.heareasy.common_classes.MySnackbar;
import com.heareasy.common_classes.MyStateView;
import com.heareasy.common_classes.MyToast;
import com.heareasy.others.TimeAgo;
import com.heareasy.others.WelcomePref;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, MyStateView.ProgressClickListener {
    private static final String TAG = "SignUpActivity";
    private Context context = SignUpActivity.this;
    private TextView tv_sing_in, tvPrivacy, tvTerms;
    private EditText first_name, last_name, email, mobile_no, password, c_password;
    private Button btn_signUp;
    private Toolbar toolbar_signUp;
    private CountryCodePicker ccp;
    private String device_id;
    private MyStateView mLoadingBar;
    private CheckBox cbTrems_Condition;
    private UserModel userModel;
    private SessionManager sessionManager;
    private MySubscriptionModel mySubscription;
    private WelcomePref welcomePref;
    private ArrayList<String> audioListModelArrayList;
    private AlertDialog dialog;
    private File directory;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        device_id = Secure.getString(this.getContentResolver(),
                Secure.ANDROID_ID);
        Log.e("deviceid",">>>>>>>>>>"+device_id);
        init();
        getFlieList();

    }

    private void init() {
        sessionManager = new SessionManager(this);
        welcomePref = new WelcomePref(getApplicationContext());
        toolbar_signUp = findViewById(R.id.toolbar_signUp);
        tv_sing_in = findViewById(R.id.tv_sing_in_signUP);
        btn_signUp = findViewById(R.id.btn_signUp);
        first_name = findViewById(R.id.edt_f_name_signUp);
        last_name = findViewById(R.id.edt_l_name_signUp);
        email = findViewById(R.id.edt_email_signUp);
        mobile_no = findViewById(R.id.edt_mobile_signUp);
        password = findViewById(R.id.edt_password_signUp);
        c_password = findViewById(R.id.edt_confirm_password_signUp);
        ccp = findViewById(R.id.ccp);
        tvTerms = findViewById(R.id.tvTerms);
        tvPrivacy = findViewById(R.id.tvPrivacy);

        cbTrems_Condition = findViewById(R.id.cbTrems_Condition);

        btn_signUp.setOnClickListener(this);
        tv_sing_in.setOnClickListener(this);
        mLoadingBar = new MyStateView(this, null);

        toolbar_signUp.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar_signUp.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tvPrivacy.setOnClickListener(view -> {
            startActivity(new Intent(context, PrivacyPolicyActivity.class));
        });
        tvTerms.setOnClickListener(view -> {
            startActivity(new Intent(context, TermsAndConditionsActivity.class));
        });

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
        if (view.getId() == R.id.show_c_pass_btn) {

            if (c_password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) (view)).setImageResource(R.drawable.show_password);

                //Show Password
                c_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (view)).setImageResource(R.drawable.hide_password);

                //Hide Password
                c_password.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_signUp:
                if (TextUtils.isEmpty(first_name.getText()) || TextUtils.isEmpty(last_name.getText()) ||
                        TextUtils.isEmpty(mobile_no.getText()) || TextUtils.isEmpty(password.getText()) ||
                        TextUtils.isEmpty(c_password.getText())) {
                    MySnackbar.show(context, view, "Please fill all the fields");
                } else if (!AppConstants.isValidEmail(email.getText())) {
                    MySnackbar.show(context, view, "Please enter valid Email");
                } else if (!cbTrems_Condition.isChecked()) {
                    MySnackbar.show(context, view, "Please Check Terms & Condition");
                } else {
                    // Call user registration API
                    callRegistrationAPI();
                }
                break;
            case R.id.tv_sing_in_signUP:
                startActivity(new Intent(context, SignInActivity.class));
                finishAffinity();
                break;

        }
    }

    private void deviceVerificationAPI() {
        mLoadingBar.showLoading();
        AndroidNetworking.post(API_LIST.device_id_verification)
                .addBodyParameter("device_id", device_id)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse:>>>>>>>>>" + response.toString());
                        try {
                            if (response.getString("responseCode").equalsIgnoreCase("200")) {
                                mLoadingBar.showContent();
                                MyToast.display(context, "" + response.getString("responseMessage"));
//                            startActivity(new Intent(context, SignInActivity.class));
                                callLoginAPI();
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

    private void callRegistrationAPI() {
        mLoadingBar.showLoading();
        AndroidNetworking.post(API_LIST.REGISTER)
                .addBodyParameter("first_name", first_name.getText().toString())
                .addBodyParameter("last_name", last_name.getText().toString())
                .addBodyParameter("email", email.getText().toString())
                .addBodyParameter("device_id", device_id)
                .addBodyParameter("mobileNumber", mobile_no.getText().toString())
                .addBodyParameter("password", password.getText().toString())
                .addBodyParameter("confirm-password", c_password.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse:>>>>>>>>>" + response.toString());
                        try {
                            if (response.getString("responseCode").equalsIgnoreCase("200")) {
                                mLoadingBar.showContent();
                                MyToast.display(context, "" + response.getString("responseMessage"));
//                            startActivity(new Intent(context, SignInActivity.class));

                                if (audioListModelArrayList.isEmpty()) {
                                    callLoginAPI();
                                } else {
                                    deleteDataDialog();
                                }
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
                            } else if (response.getString("responseCode").equalsIgnoreCase("401")) {
                                mLoadingBar.showContent();
                                MethodFactory.forceLogoutDialog(SignUpActivity.this);
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

    private void callMySubscriptionAPI() {
        mLoadingBar.showLoading();
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
                                } else if (response.getString("responseCode").equalsIgnoreCase("401")) {
                                    mLoadingBar.showContent();
                                    MethodFactory.forceLogoutDialog(SignUpActivity.this);
                                } else {
                                    if (!sessionManager.getTrialStatus().equalsIgnoreCase("0")) {
                                        Log.e(TAG, "getTrialStatus: " + sessionManager.getTrialStatus());
                                        startActivity(new Intent(context, TrialActivity.class));
                                        finishAffinity();
                                    } else {
                                        startActivity(new Intent(context, DashBoardActivity.class));
                                        finishAffinity();
                                    }
                                }

                            } else {
                                startActivity(new Intent(context, TrialActivity.class));
                                finishAffinity();
                            }
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getFlieList() {
        audioListModelArrayList = new ArrayList<>();
//        String path = this.getExternalFilesDir("/").getAbsolutePath();
//        Log.e("Files", "Path: " + path);
//        directory = new File(path);
//        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
//        directory = getApplicationContext().getExternalFilesDir("");
        directory =  ContextCompat.getExternalFilesDirs(SignUpActivity.this, Environment.DIRECTORY_MUSIC)[0];
        if (directory.listFiles() != null) {
            File[] files = directory.listFiles();
            if (files.length >= 0) {
                Log.e("Files", "Size: " + files.length);
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
                    String name = files[i].getName();
                    Log.e("Files", "FileName:" + name);
//                    String duration = getDuration(files[i]);
                 //   Log.e("TAG", "duration:>>>>>>>>> " + duration);
//                Date lastModDate = new Date(files[i].lastModified());
//                String date1 = DateFormat.getDateInstance(DateFormat.MEDIUM).format(lastModDate);
//                    Log.e("TAG", "date:>>>>>>>>>> " + date1);

                    String date = new TimeAgo().getTimeAgo(files[i].lastModified());
                    Log.e("TAG", "date:>>>>>>>>>> " + date);

                    if (name.equalsIgnoreCase("download")) {
                        audioListModelArrayList.remove(name);
                    } else if (name.equalsIgnoreCase(".thumbnails")) {
                        audioListModelArrayList.remove(name);
                    } else if (name.contains("Recording_")) {
                        audioListModelArrayList.add(name);
//                        Collections.reverse(audioListModelArrayList);
                    }

                }
            }
        }
    }

    @Override
    public void onRetryClick() {
        callRegistrationAPI();
    }

    public void deleteDataDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        final View dialogView = getLayoutInflater().inflate(R.layout.logout_dialog, null);
        alert.setView(dialogView);
        Button yes = dialogView.findViewById(R.id.btn_yes_logout);
        Button no = dialogView.findViewById(R.id.btn_no_logout);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView textView = dialogView.findViewById(R.id.textView);
        tvTitle.setText("Previous Recording");
        textView.setText("This Device has Hear me audio recordings. To protect user privacy, the audio recording will be Deleted ?");
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //File dir = getApplicationContext().getExternalFilesDir("");
                if (directory.isDirectory()) {
                    String[] children = directory.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(directory, children[i]).delete();
                    }
                }
                callLoginAPI();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                callLoginAPI();
            }
        });
        dialog = alert.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

}