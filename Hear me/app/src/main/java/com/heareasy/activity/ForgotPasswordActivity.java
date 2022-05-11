package com.heareasy.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;


public class ForgotPasswordActivity extends AppCompatActivity implements MyStateView.ProgressClickListener {
    private static final String TAG = "ForgotPasswordActivity";
    private Context context = ForgotPasswordActivity.this;
    private Toolbar toolbar_forgotPassword;
    private MyStateView mLoadingBar;
    private EditText email;
    private Button btn_send;
    private View myView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        init();
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myView = view;
                if (TextUtils.isEmpty(email.getText())) {
                    MySnackbar.show(context, view, "Please enter email");
                } else if (!AppConstants.isValidEmail(email.getText())) {
                    MySnackbar.show(context, view, "Please enter valid email");
                } else {
                    //Call forgot password API
                    callForgotAPI(myView);
                }
            }
        });

    }

    private void callForgotAPI(View view) {
        mLoadingBar.showLoading();
        AndroidNetworking.post(API_LIST.FORGOT_PASSWORD)
            .addBodyParameter("email", email.getText().toString())
            .setPriority(Priority.HIGH)
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
                        }
                        else if (response.getString("responseCode").equalsIgnoreCase("401")) {

                            mLoadingBar.showContent();
                            MethodFactory.forceLogoutDialog(ForgotPasswordActivity.this);
                        }
                        else {
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
                    MySnackbar.show(context, view, "");
                    mLoadingBar.showRetry();
                }
            });
    }

    private void init() {
        btn_send = findViewById(R.id.btn_send_forgotPassword);
        email = findViewById(R.id.edtEmailForgotPassword);
        toolbar_forgotPassword = findViewById(R.id.toolbar_forgotPassword);
        toolbar_forgotPassword.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar_forgotPassword.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mLoadingBar = new MyStateView(this, null);
    }

    @Override
    public void onRetryClick() {
        callForgotAPI(myView);
    }
}