package com.heareasy.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.heareasy.R;
import com.heareasy.common_classes.API_LIST;
import com.heareasy.common_classes.MethodFactory;
import com.heareasy.common_classes.MyStateView;
import com.heareasy.common_classes.MyToast;
import com.heareasy.model.AboutUsModel;

import org.json.JSONException;
import org.json.JSONObject;

public class AboutUsActivity extends AppCompatActivity implements MyStateView.ProgressClickListener {
    private Context context = AboutUsActivity.this;
    private static final String TAG = "AboutUsActivity";
    private Toolbar toolbar_about_us;
    private TextView tvTitle,tvAddress,tvAboutUsTitle,tvDescription;
    private LinearLayout layoutCall,layoutMessage,layoutMail;
    private MyStateView mLoadingBar;
    private TextView tvAddress2,tvEmail,tvName,tvNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        tvTitle = findViewById(R.id.tvTitle);
        tvAddress = findViewById(R.id.tvAddress);
        tvAboutUsTitle = findViewById(R.id.tvAboutUsTitle);
        layoutMail = findViewById(R.id.layoutMail);
        layoutCall = findViewById(R.id.layoutCall);
        layoutMessage = findViewById(R.id.layoutMessage);
        tvDescription = findViewById(R.id.tvDescription);
        tvAddress2 = findViewById(R.id.tvAddress2);
        tvEmail = findViewById(R.id.tvEmail);
        tvName = findViewById(R.id.tvName);
        tvNumber = findViewById(R.id.tvNumber);
        toolbar_about_us = findViewById(R.id.toolbar_about_us);
        toolbar_about_us.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar_about_us.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mLoadingBar = new MyStateView(this,null);
        // Call About Us API
        callAboutUsAPI();
        // Call contact us API
        callContactUsAPI();
    }

    private void callAboutUsAPI() {
        mLoadingBar.showLoading();
        AndroidNetworking.get(API_LIST.ABOUT_US)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(TAG, "onResponse:>>>>>>>>>" + response.toString());
                    Gson gson = new Gson();
                    AboutUsModel aboutUsModel = gson.fromJson(response.toString(),AboutUsModel.class);
                    try {
                        if (response.getString("responseCode").equalsIgnoreCase("200")) {
                            mLoadingBar.showContent();
                            updateUI(aboutUsModel);
                        }
                        else if (response.getString("responseCode").equalsIgnoreCase("401")) {
                            mLoadingBar.showContent();
                            MethodFactory.forceLogoutDialog(AboutUsActivity.this);
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
                    mLoadingBar.showRetry();
                }
            });
    }

    private void updateUI(AboutUsModel aboutUsModel) {
        tvTitle.setText(aboutUsModel.getData().getFirstName()+" "+aboutUsModel.getData().getLastName());
        tvAddress.setText(aboutUsModel.getData().getAddress());
        tvDescription.setText(Html.fromHtml(aboutUsModel.getData().getAboutus()));

        layoutCall.setOnClickListener(view -> {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+aboutUsModel.getData().getContactNumber()));
            startActivity(callIntent);
        });
        layoutMessage.setOnClickListener(view -> {
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
            smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.setData(Uri.parse("sms:" + aboutUsModel.getData().getContactNumber()));
            startActivity(smsIntent);
        });
        layoutMail.setOnClickListener(view -> {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{ aboutUsModel.getData().getEmail()});
            email.setType("message/rfc822");

            startActivity(email);
        });
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
                            MethodFactory.forceLogoutDialog(AboutUsActivity.this);
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
        tvAddress2.setText(address);
    }


    @Override
    public void onRetryClick() {

    }
}