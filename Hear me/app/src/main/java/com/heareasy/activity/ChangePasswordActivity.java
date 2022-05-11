package com.heareasy.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.heareasy.R;
import com.heareasy.common_classes.API_LIST;
import com.heareasy.common_classes.MethodFactory;
import com.heareasy.common_classes.MySnackbar;
import com.heareasy.common_classes.MyStateView;
import com.heareasy.common_classes.MyToast;
import com.heareasy.common_classes.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordActivity extends AppCompatActivity implements MyStateView.ProgressClickListener {
    private static final String TAG = "ChangePasswordActivity";
    private Context context = ChangePasswordActivity.this;
    private Toolbar toolbar_change_password;
    private EditText oldPassword,newPassword,confirmPassword;
    private Button btnSave;
    private MyStateView mLoadingBar;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        init();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(oldPassword.getText())||TextUtils.isEmpty(newPassword.getText())||
                TextUtils.isEmpty(confirmPassword.getText())){
                    MySnackbar.show(context,view,"Fields can't be empty");
                }else {
                    // Call change password API
                    callChangePasswordAPI();
                }
            }
        });
    }

    public void ShowHidePass(View view){

        if(view.getId()==R.id.show_pass_btn){

            if(oldPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.show_password);

                //Show Password
                oldPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.hide_password);

                //Hide Password
                oldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }
        if(view.getId()==R.id.show_pass_btn2){

            if(newPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.show_password);

                //Show Password
                newPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.hide_password);

                //Hide Password
                newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }
        if(view.getId()==R.id.show_pass_btn3){

            if(confirmPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.show_password);

                //Show Password
                confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.hide_password);

                //Hide Password
                confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }
    }

    private void callChangePasswordAPI() {
        mLoadingBar.showLoading();
        AndroidNetworking.get(API_LIST.RESET_PASSWORD)
                .addHeaders("Authorization",sessionManager.getAuthToken())
                .addHeaders("id",sessionManager.getUserID())
                .addQueryParameter("oldpassword", oldPassword.getText().toString())
                .addQueryParameter("password", newPassword.getText().toString())
                .addQueryParameter("confirm-password", confirmPassword.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse:>>>>>>>>>" + response.toString());
                        try {
                            if (response.getString("responseCode").equalsIgnoreCase("200")) {
                                mLoadingBar.showContent();
                                MyToast.display(context, ""+response.getString("responseMessage"));
                                finish();

                            }
                            else if (response.getString("responseCode").equalsIgnoreCase("401")) {
                                mLoadingBar.showContent();
                                MethodFactory.forceLogoutDialog(ChangePasswordActivity.this);
                            }
                            else {
                                mLoadingBar.showContent();
                                MyToast.display(context, ""+response.getString("responseMessage"));
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

    private void init() {
        sessionManager = new SessionManager(this);
        toolbar_change_password= findViewById(R.id.toolbar_change_password);
        toolbar_change_password.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar_change_password.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        oldPassword = findViewById(R.id.edt_old_password_change_password);
        newPassword = findViewById(R.id.edt_new_password_change_password);
        confirmPassword = findViewById(R.id.edt_confirm_password_change_password);
        btnSave = findViewById(R.id.btn_save_change_password);

        mLoadingBar = new MyStateView(this, null);
    }

    @Override
    public void onRetryClick() {

    }
}
