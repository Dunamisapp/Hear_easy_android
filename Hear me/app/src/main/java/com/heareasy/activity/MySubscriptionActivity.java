package com.heareasy.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.google.gson.Gson;
import com.heareasy.R;
import com.heareasy.others.WelcomePref;
import com.heareasy.common_classes.API_LIST;
import com.heareasy.common_classes.MethodFactory;
import com.heareasy.common_classes.MyStateView;
import com.heareasy.common_classes.MyToast;
import com.heareasy.common_classes.SessionManager;
import com.heareasy.model.MySubscriptionModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class MySubscriptionActivity extends AppCompatActivity implements MyStateView.ProgressClickListener {
    private static final String TAG = "MySubscriptionActivity";
    private Context context = MySubscriptionActivity.this;
    private Toolbar toolbar_mySubscription;
    private Button btn_cancel;
    private Button btn_renew, btn_buyNewPlan;
    private TextView tv_title, tv_ammount, tv_expiryDate, tv_startDate, tvDescription;
    private AlertDialog dialog;
    private MyStateView mLoadingBar;
    private SessionManager sessionManager;
    private WelcomePref welcomePref;
    private MySubscriptionModel mySubscription;
    private int status = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_subscription);
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load initial data of subscription list
        callMySubscriptionAPI();
    }

    private void init() {
        toolbar_mySubscription = findViewById(R.id.toolbar_mySubscription);
        btn_cancel = findViewById(R.id.btn_cancel_my_subscription);
        tv_startDate = findViewById(R.id.tv_startDate);
        tv_expiryDate = findViewById(R.id.tv_expiryDate);
        tv_title = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tv_ammount = findViewById(R.id.tv_ammount);
        btn_renew = findViewById(R.id.btn_renew_my_subscription);
        btn_buyNewPlan = findViewById(R.id.btn_buyNewPlan_my_subscription);
        toolbar_mySubscription.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar_mySubscription.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mLoadingBar = new MyStateView(this, null);
        sessionManager = new SessionManager(this);
        welcomePref = new WelcomePref(this);


        btn_buyNewPlan.setOnClickListener(view -> {
            startActivity(new Intent(context, SubscriptionPlanActivity.class));
            finish();
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MySubscriptionActivity.this);
                View mView1 = getLayoutInflater().inflate(R.layout.dialog_subscription_remian, null);
                Button btn_yes = mView1.findViewById(R.id.btn_yes);
                Button btn_no = mView1.findViewById(R.id.btn_no);

                btn_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callCancelSubscriptionAPI();
                    }
                });
                btn_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                mBuilder.setView(mView1);
                dialog = mBuilder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });
        btn_renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* AlertDialog.Builder mBuilder = new AlertDialog.Builder(MySubscriptionActivity.this);
                View mView1 = getLayoutInflater().inflate(R.layout.dialog_renew, null);
                Button btn_okay = mView1.findViewById(R.id.btn_okay);

                btn_okay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(TAG, "getPayableAmount: >>>>>>>>>>>"+sessionManager.getPayableAmount());

//                        if (!mySubscription.getData().getId().equals(null)) {
////                            callReNewSubscriptionAPI();
//
//                        }
                    }
                });
                mBuilder.setView(mView1);
                dialog = mBuilder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();*/
                Log.e(TAG, "subscription_package_id:>>>>>>>>> " + sessionManager.getLastPlanId());
                Log.e(TAG, "package_amount:>>>>>>>>> " + sessionManager.getPackageAmount());
                Log.e(TAG, "payable_amount:>>>>>>>>> " + sessionManager.getPayableAmount());
                Log.e(TAG, "total_discount:>>>>>>>>> " + sessionManager.getTotalDiscount());
                Log.e(TAG, "expiry_date:>>>>>>>>> " + sessionManager.getExpiryDate());
                Intent intent = new Intent(context, PaymentActivity.class);
                intent.putExtra("plan_status", "renew");
                intent.putExtra("subscription_package_id", sessionManager.getLastPlanId());
                intent.putExtra("package_amount", sessionManager.getPackageAmount());
                intent.putExtra("payable_amount", sessionManager.getPayableAmount());
                intent.putExtra("total_discount", sessionManager.getTotalDiscount());
                intent.putExtra("expiry_date", sessionManager.getExpiryDate());
                startActivity(intent);
            }
        });
    }

    // Load subscription API data
    private void callMySubscriptionAPI() {
        Log.e("Authorization", sessionManager.getAuthToken());
        Log.e("id", sessionManager.getUserID());
        mLoadingBar.showLoading();
        AndroidNetworking.get(API_LIST.MY_SUBSCRIPTION)
                .addHeaders("Authorization", sessionManager.getAuthToken())
                .addHeaders("id", sessionManager.getUserID())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse:>>>>>>>>>" + response.toString());
                        Gson gson = new Gson();
                        mySubscription = gson.fromJson(response.toString(), MySubscriptionModel.class);
                        try {
                            if (response.getString("responseCode").equalsIgnoreCase("200")) {
                                mLoadingBar.showContent();
                                welcomePref.setSubscriptionStatus(true);
                                updateUI(mySubscription);
                                checkPlan();
                                btn_cancel.setVisibility(View.VISIBLE);
//                            btn_renew.setVisibility(View.GONE);
                                btn_buyNewPlan.setVisibility(View.GONE);

                            } else if (response.getString("responseCode").equalsIgnoreCase("401")) {
                                mLoadingBar.showContent();
                                MethodFactory.forceLogoutDialog(MySubscriptionActivity.this);
                            } else {
                                mLoadingBar.showContent();
                                btn_cancel.setVisibility(View.GONE);
//                            btn_renew.setVisibility(View.VISIBLE);
                                btn_buyNewPlan.setVisibility(View.VISIBLE);
                                welcomePref.setSubscriptionStatus(false);
                               // checkPlan();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e(TAG, "onError:>>>>" + error.getMessage());
                        status = 1;
                        mLoadingBar.showContent();
                    }
                });
    }

    // Cancel current subscription plan API
    private void callCancelSubscriptionAPI() {
        mLoadingBar.showLoading();
        AndroidNetworking.post(API_LIST.CANCEL_SUBSCRIPTION)
                .addHeaders("Authorization", sessionManager.getAuthToken())
                .addHeaders("id", sessionManager.getUserID())
                .addBodyParameter("subscription_id", mySubscription.getData().getId() + "")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse:>>>>>>>>>" + response.toString());
                        try {
                            dialog.dismiss();
                            if (response.getString("responseCode").equalsIgnoreCase("200")) {
                                mLoadingBar.showContent();
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
                        status = 2;
                        mLoadingBar.showRetry();
                    }
                });
    }

    private void callReNewSubscriptionAPI() {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Log.e(TAG, "date: >>>>>>>" + date);
        mLoadingBar.showLoading();
        AndroidNetworking.post(API_LIST.ADD_SUBSCRIPTION)
                .addHeaders("Authorization", sessionManager.getAuthToken())
                .addHeaders("id", sessionManager.getUserID())
                .addBodyParameter("subscription_package_id", sessionManager.getLastPlanId())
                .addBodyParameter("starting_date", date + "")
                .addBodyParameter("package_amount", mySubscription.getData().getPackageAmount() + "")
                .addBodyParameter("payable_amount", mySubscription.getData().getPayableAmount() + "")
                .addBodyParameter("total_discount", mySubscription.getData().getTotalDiscount() + "")
                .addBodyParameter("expiry_date", mySubscription.getData().getExpiryDate() + "")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse:>>>>>>>>>" + response.toString());
                        try {
                            dialog.dismiss();
                            if (response.getString("responseCode").equalsIgnoreCase("200")) {
                                mLoadingBar.showContent();
                                MyToast.display(context, "" + response.getString("responseMessage"));
                                finish();
                            } else if (response.getString("responseCode").equalsIgnoreCase("401")) {
                                mLoadingBar.showContent();
                                MethodFactory.forceLogoutDialog(MySubscriptionActivity.this);
                            } else {
                                mLoadingBar.showContent();
                                MyToast.display(context, "" + response.getString("responseMessage"));
                                Log.e(TAG, ">>>>>>>: " + response.getString("responseMessage"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e(TAG, "onError:>>>>" + error.getMessage());
                        status = 3;
                        mLoadingBar.showRetry();
                    }
                });
    }

    // Save subscription plan data to preferences
    private void updateUI(MySubscriptionModel mySubscription) {
        sessionManager.setLastPlanId(mySubscription.getData().getSubscriptionPackageId() + "");
        sessionManager.setPackageAmount("" + SubscriptionPlanActivity.round(mySubscription.getData().getPackageAmount(), 2));
        sessionManager.setPayableAmount(SubscriptionPlanActivity.round(mySubscription.getData().getPayableAmount(), 2) + "");
        sessionManager.setTotalDiscount(mySubscription.getData().getTotalDiscount() + "");
        sessionManager.setExpiryDate(mySubscription.getData().getExpiryDate() + "");
      /*  if (welcomePref.isSubscriptionStatus()){
            btn_renew.setVisibility(View.VISIBLE);
            btn_buyNewPlan.setVisibility(View.VISIBLE);
        }else {
            btn_renew.setVisibility(View.VISIBLE);
            btn_buyNewPlan.setVisibility(View.VISIBLE);
        }*/

    }

    @Override
    public void onRetryClick() {
        if (status == 1) {
            callMySubscriptionAPI();
        } else if (status == 2) {
            callCancelSubscriptionAPI();
        } else {
//            callReNewSubscriptionAPI();
        }
    }

    // Method to update subscription plan UI
    public void checkPlan() {
        if (welcomePref.isSubscriptionStatus()) {
            Log.e(TAG, "isSubscriptionStatus:>>>>>>>>>" + welcomePref.isSubscriptionStatus());
            if (mySubscription.getData().getSubscription().getTitle() != null) {
                tv_title.setText(mySubscription.getData().getSubscription().getTitle() + "");
            } else {
                tv_title.setVisibility(View.GONE);
            }
            tvDescription.setText(getResources().getString(R.string.description));
            tv_ammount.setText("$" + SubscriptionPlanActivity.round(mySubscription.getData().getPayableAmount(), 2));

            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat out = new SimpleDateFormat("dd-MMM-yyyy");

            Date date = null;
            Date date2 = null;
            try {
                date = in.parse(mySubscription.getData().getStartingDate());
                date2 = in.parse(mySubscription.getData().getExpiryDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String result = out.format(date);
            String result2 = out.format(date2);

            tv_startDate.setText(result);
            tv_expiryDate.setText(result2);
        } else if (!sessionManager.getTrialStatus().equalsIgnoreCase("0")) {

            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat out = new SimpleDateFormat("dd-MMM-yyyy");

            Date date1 = null;
            try {
                date1 = in.parse(sessionManager.getTrialstartddate().replace("/", "-"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String result = out.format(date1);

            tv_startDate.setText(result);
            final DateTimeFormatter OLD_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            final DateTimeFormatter NEW_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

            String oldString = sessionManager.getTrialstartddate().replace("/", "-");
            Log.e("oldString", ">>>>>>>>>" + oldString);
            LocalDate date = LocalDate.parse(oldString, OLD_FORMATTER);
            String newString = date.format(NEW_FORMATTER);
            Log.e("result", ">>>>>>>" + newString);
            LocalDate localdate = LocalDate.parse(newString);
            LocalDate enddate = localdate.plusDays(30);
            final DateTimeFormatter ExpireOLD_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            final DateTimeFormatter ExpireNEW_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
            LocalDate finaldate = LocalDate.parse(enddate.toString(), ExpireOLD_FORMATTER);

            tv_expiryDate.setText(finaldate.format(ExpireNEW_FORMATTER));
            Log.e(TAG, "getTrialStatus:>>>>>>>>>" + sessionManager.getTrialStatus());
            tv_title.setText("Trial subscription");
            tvDescription.setText(" \"Listen and Be Heard LLC is pleased to offer a trial subscription for you to try out the app. This is offered only once for each registered customer. If you like the app, please purchase a subscription and we offer a variety of subscriptions to suit your needs\".");
        } else {
            Log.e(TAG, "No Active plan:>>>>>>>>>");
            tv_title.setText("No Active plan");
            tvDescription.setText(" \"Listen and Be Heard LLC is pleased to offer a trial subscription for you to try out the app. This is offered only once for each registered customer. If you like the app, please purchase a subscription and we offer a variety of subscriptions to suit your needs\".");
        }
    }
}
