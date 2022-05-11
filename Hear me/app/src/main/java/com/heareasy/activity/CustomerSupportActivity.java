package com.heareasy.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.heareasy.common_classes.AppConstants;
import com.heareasy.R;
import com.heareasy.common_classes.API_LIST;
import com.heareasy.common_classes.MethodFactory;
import com.heareasy.common_classes.MySnackbar;
import com.heareasy.common_classes.MyStateView;
import com.heareasy.common_classes.MyToast;
import com.heareasy.common_classes.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomerSupportActivity extends AppCompatActivity implements MyStateView.ProgressClickListener {
    private static final String TAG = "CustomerSupportActivity";
    private Toolbar toolbar_customer_support;
    private EditText edt_name, edt_mobile, edt_email, edt_problem;
    private Button btn_send;
    private MyStateView mLoadingBar;
    private Context context = CustomerSupportActivity.this;
    private SessionManager sessionManager;
    private String sdk_version_number;
    private String brand;
    private String model;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_support);
        init();
    }

    private void init() {
         model = Build.MODEL;
         brand = Build.BRAND;
        sdk_version_number = "Android " + Build.VERSION.SDK_INT;
        Log.e("version", ">>>>>>" +brand  + " " + model + " " + sdk_version_number);
        edt_name = findViewById(R.id.edt_name);
        edt_email = findViewById(R.id.edt_email);
        edt_mobile = findViewById(R.id.edt_mobile);
        edt_problem = findViewById(R.id.edt_problem);
        btn_send = findViewById(R.id.btn_send);
        toolbar_customer_support = findViewById(R.id.toolbar_customer_support);
        toolbar_customer_support.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar_customer_support.setNavigationOnClickListener(view -> {
            onBackPressed();

        });

        mLoadingBar = new MyStateView(this, null);
        sessionManager = new SessionManager(this);

        edt_name.setText(sessionManager.getUserFirstName() + " " + sessionManager.getUserLastName());
        edt_email.setText(sessionManager.getUserEmail());
        edt_mobile.setText(sessionManager.getUserMobileNumber());

        btn_send.setOnClickListener(view -> {
            if (TextUtils.isEmpty(edt_name.getText()) || TextUtils.isEmpty(edt_mobile.getText()) ||
                    TextUtils.isEmpty(edt_email.getText()) || TextUtils.isEmpty(edt_problem.getText())) {
                MySnackbar.show(context, view, "Please fill all the fields");
            } else if (!AppConstants.isValidEmail(edt_email.getText())) {
                MySnackbar.show(context, view, "Please enter valid Email");
            } else {
                // Call customer support API
                callCustomerSupportAPI(view);
            }
        });
    }

    private void callCustomerSupportAPI(View view) {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Log.e(TAG, "date: >>>>>>>" + date);
        Log.e(TAG, "sdk_version_number: >>>>>>>" + brand  + " " + model + " " + sdk_version_number);
        mLoadingBar.showLoading();
        AndroidNetworking.post(API_LIST.CUSTOMER_SUPPORT)
                .addHeaders("Authorization", sessionManager.getAuthToken())
                .addHeaders("id", sessionManager.getUserID())
                .addBodyParameter("name", edt_name.getText().toString())
                .addBodyParameter("email", edt_email.getText().toString())
                .addBodyParameter("mobile_number", edt_mobile.getText().toString())
                .addBodyParameter("query", edt_problem.getText().toString())
                .addBodyParameter("created_at", date + "")
                .addBodyParameter("android_version",brand  + " " + model + " " + sdk_version_number)
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
                                finish();
                            } else if (response.getString("responseCode").equalsIgnoreCase("401")) {
                                mLoadingBar.showContent();
                                MethodFactory.forceLogoutDialog(CustomerSupportActivity.this);
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
    public void onRetryClick() {

    }
}