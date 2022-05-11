package com.heareasy.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.heareasy.R;
import com.heareasy.common_classes.API_LIST;
import com.heareasy.common_classes.MethodFactory;
import com.heareasy.common_classes.MyStateView;

import org.json.JSONException;
import org.json.JSONObject;

public class ContactUsActivity extends AppCompatActivity implements MyStateView.ProgressClickListener {
    private static final String TAG = "ContactUsActivity";
    private Context context = ContactUsActivity.this;
    private Toolbar toolbar_contact_us;
    private TextView tvAddress,tvEmail,tvName,tvNumber;
    private MyStateView mLoadingBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        tvAddress = findViewById(R.id.tvAddress);
        tvEmail = findViewById(R.id.tvEmail);
        tvName = findViewById(R.id.tvName);
        tvNumber = findViewById(R.id.tvNumber);
        toolbar_contact_us = findViewById(R.id.toolbar_contact_us);
        toolbar_contact_us.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar_contact_us.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mLoadingBar = new MyStateView(this,null);
        // Call contact us API
        callContactUsAPI();
    }

    private void callContactUsAPI() {
        mLoadingBar.showLoading();
        AndroidNetworking.get(API_LIST.CONTACT_US)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(TAG, "onResponse:>>>>>>>>>" + response.toString());
                    try {
                        if (response.getString("responseCode").equalsIgnoreCase("200")) {
                            mLoadingBar.showContent();
                            JSONObject data = response.getJSONObject("data");
                            String firstName = data.getString("first_name");
                            String lastName = data.getString("last_name");
                            String contactNumber = data.getString("contact_number");
                            String email = data.getString("email");
                            String address = data.getString("address");

                            // Update UI
                            updateUI(firstName,lastName,contactNumber,email,address);
                        }
                        else if (response.getString("responseCode").equalsIgnoreCase("401")) {
                            mLoadingBar.showContent();
                            MethodFactory.forceLogoutDialog(ContactUsActivity.this);
                        }
                        else {
                            mLoadingBar.showContent();
                            Toast.makeText(context, ""+response.getString("responseMessage"), Toast.LENGTH_SHORT).show();
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

    private void updateUI(String firstName, String lastName, String contactNumber, String email, String address) {
        tvName.setText(firstName+" "+lastName);
        tvNumber.setText(contactNumber);
        tvEmail.setText(email);
        tvAddress.setText(address);
    }

    @Override
    public void onRetryClick() {

    }
}