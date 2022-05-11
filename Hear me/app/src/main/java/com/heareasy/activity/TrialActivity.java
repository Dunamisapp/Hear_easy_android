package com.heareasy.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.heareasy.R;
import com.heareasy.common_classes.API_LIST;
import com.heareasy.common_classes.MethodFactory;
import com.heareasy.common_classes.MyStateView;
import com.heareasy.common_classes.MyToast;
import com.heareasy.common_classes.SessionManager;
import com.heareasy.model.MySubscriptionModel;

import org.json.JSONException;
import org.json.JSONObject;

public class TrialActivity extends AppCompatActivity implements MyStateView.ProgressClickListener {
    private static final String TAG = "TrialActivity";
    private Context context = TrialActivity.this;
    private Button btn_buy_home;
    private TextView tvDaysLeft;
    private Button btn_startTrial_trail;
    private MySubscriptionModel mySubscription;
    private SessionManager sessionManager;
    private MyStateView mLoadingBar;
    private TextView tvLoginAnother;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trial);
        init();
        callTrialAPI();

        btn_buy_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, SubscriptionPlanActivity.class));
            }
        });

        btn_startTrial_trail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, DashBoardActivity.class));
                finishAffinity();
            }
        });

        tvLoginAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MethodFactory.forceLogoutDialog(TrialActivity.this);
                startActivity(new Intent(TrialActivity.this, SignInActivity.class));
                finish();
            }
        });
    }

    private void init() {
        btn_buy_home = findViewById(R.id.btn_buy_trail);
        btn_startTrial_trail = findViewById(R.id.btn_startTrial_trail);
        tvDaysLeft = findViewById(R.id.tvDaysLeft);
        tvLoginAnother = findViewById(R.id.tvLoginAnother);

        mLoadingBar = new MyStateView(this, null);
        sessionManager = new SessionManager(this);
    }

    // Check user trail status
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
                                tvDaysLeft.setText(response.getString("daya_left"));
                                sessionManager.setTrialstartddate(response.getString("trial_date"));
                                Log.e("created_at", ">>>>>>>>>>>" + sessionManager.getTrialstartddate());
                                if (sessionManager.getTrialStatus().equalsIgnoreCase("0")) {
                                    btn_startTrial_trail.setVisibility(View.GONE);
                                }

                            } else if (response.getString("responseCode").equalsIgnoreCase("0")) {
                                mLoadingBar.showContent();
//                            MyToast.display(context, "" + response.getString("responseMessage"));
                                sessionManager.setTrialStatus(response.getString("trial"));
                                tvDaysLeft.setText(response.getString("daya_left"));
                             /*   sessionManager.setTrialstartddate(response.getString("created_at"));
                                Log.e("created_at", ">>>>>>>>>>>" + sessionManager.getTrialstartddate());*/
                                if (sessionManager.getTrialStatus().equalsIgnoreCase("0")) {
                                    btn_startTrial_trail.setVisibility(View.GONE);
                                }

                            } else if (response.getString("responseCode").equalsIgnoreCase("401")) {
                                mLoadingBar.showContent();
                                MethodFactory.forceLogoutDialog(TrialActivity.this);
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