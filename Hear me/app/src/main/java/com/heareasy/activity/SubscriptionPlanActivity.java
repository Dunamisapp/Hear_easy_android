package com.heareasy.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseInfo;
import com.google.gson.Gson;
import com.heareasy.R;
import com.heareasy.others.WelcomePref;
import com.heareasy.adapter.SubscriptionAdapter;
import com.heareasy.common_classes.API_LIST;
import com.heareasy.common_classes.MethodFactory;
import com.heareasy.common_classes.MyStateView;
import com.heareasy.common_classes.MyToast;
import com.heareasy.common_classes.SessionManager;
import com.heareasy.model.GetSubscriptionListModel;
import com.heareasy.model.SubscriptionListResponser;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

public class SubscriptionPlanActivity extends AppCompatActivity implements MyStateView.ProgressClickListener,
    SubscriptionAdapter.OnItemClick, BillingProcessor.IBillingHandler {
    private static final String TAG = "SubscriptionPlanActivit";
    private Context context = SubscriptionPlanActivity.this;
    private Toolbar toolbar_subscriptionPlan;
    private RecyclerView rv_subscription;
    private Button btn_buyNow;
    private ImageView img_arrow;
    private ImageView img_arrowUp;
    private TextView tv_cutPrice, tv_cutPrice1, tv_cutPrice2,tv_note;
    private ConstraintLayout clayout;
    private LinearLayout llayout, llayout2, llayout3;
    private SubscriptionAdapter adapter;
    private MyStateView mLoadingBar;
    private SessionManager sessionManager;
    private WelcomePref welcomePref;
    private int adapterPosition;
    private SubscriptionListResponser getSubscription;
    private int status = -1;
    private boolean isSelected = false;
    private LinearLayoutManager mLayoutManager;
    private List<GetSubscriptionListModel.Datum> subscriptionList;
    private BillingProcessor bp;
   // private TransactionDetails purchaseTransactionDetails = null;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_plan);
        init();


        bp = new BillingProcessor(this, getResources().getString(R.string.play_console_license), this);
        bp.initialize();
        Animation aniSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);


    }

    @Override
    protected void onResume() {
        super.onResume();
        callGetSubscriptionAPI();
        tv_note.setVisibility(View.GONE);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init() {
        toolbar_subscriptionPlan = findViewById(R.id.toolbar_subscriptionPlan);
        rv_subscription = findViewById(R.id.rv_subscription);
        btn_buyNow = findViewById(R.id.btn_buyNow_subscriptionPlan);
        img_arrow = findViewById(R.id.img_arrow);
        tv_note = findViewById(R.id.tv_note);
        img_arrowUp = findViewById(R.id.img_arrowUp);
        mLoadingBar = new MyStateView(this, null);
        sessionManager = new SessionManager(this);
        welcomePref = new WelcomePref(this);
        toolbar_subscriptionPlan.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar_subscriptionPlan.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mLayoutManager = new LinearLayoutManager(this);
        rv_subscription.setLayoutManager(mLayoutManager);


        btn_buyNow.setOnClickListener(view -> {
            if (isSelected) {
//                bp.subscribe(SubscriptionPlanActivity.this, "YOUR SUBSCRIPTION ID FROM GOOGLE PLAY CONSOLE HERE");
                if (Double.valueOf(getSubscription.getData().get(adapterPosition).getOfferPrice())<=0){
                    MyToast.display(context, "Invalid Amount");

                }
                else if (getSubscription.getData().get(adapterPosition).getMonths().equals("1")){
                    if (bp.isSubscriptionUpdateSupported()) {
                        bp.subscribe(this, getResources().getString(R.string.premium));
                    } else {
                        Log.d("MainActivity", "onBillingInitialized: Subscription updated is not supported");
                    }

                }else {
                    Intent intent = new Intent(context, PaymentActivity.class);
                    intent.putExtra("plan_status", "buynew");
                    intent.putExtra("subscription_package_id", getSubscription.getData().get(adapterPosition).getId() + "");
                    intent.putExtra("package_amount", getSubscription.getData().get(adapterPosition).getOriginalPrice() + "");
                    intent.putExtra("payable_amount", getSubscription.getData().get(adapterPosition).getOfferPrice() + "");
                    intent.putExtra("total_discount", getSubscription.getData().get(adapterPosition).getDiscountPrice() + "");
                    intent.putExtra("discount_type", getSubscription.getData().get(adapterPosition).getDiscountType() + "");
                    intent.putExtra("expiry_date", getSubscription.getData().get(adapterPosition).getExpiryDate() + "");
                    startActivity(intent);
                }
            } else {
               MyToast.display(context, "Please Select Any Plan");
            }
        });


    }

    // Load subscription plan list data
    private void callGetSubscriptionAPI() {
        mLoadingBar.showLoading();
        AndroidNetworking.get(API_LIST.GET_SUBSCRIPTION)
            .addHeaders("Authorization", sessionManager.getAuthToken())
            .addHeaders("id", sessionManager.getUserID())
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(new JSONObjectRequestListener() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.e(TAG, "onResponse:>>>>>>>>>" + response.toString());
                    Gson gson = new Gson();
                    getSubscription = gson.fromJson(response.toString(), SubscriptionListResponser.class);

                    try {
                        mLoadingBar.showContent();
                        if (response.getString("responseCode").equalsIgnoreCase("200")) {

                            adapter = new SubscriptionAdapter(context,getSubscription.getData(),SubscriptionPlanActivity.this::onClick);
                            rv_subscription.setAdapter(adapter);


                        }
                        else if (response.getString("responseCode").equalsIgnoreCase("401")) {
                            mLoadingBar.showContent();
                            MethodFactory.forceLogoutDialog(SubscriptionPlanActivity.this);
                        }
                        else {
                            mLoadingBar.showEmpty();
                            MyToast.display(context, "" + response.getString("responseMessage"));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    Log.e(TAG, "onError:>>>>" + anError.getMessage());
                    status = 1;
                    mLoadingBar.showRetry();
                }
            });
    }



    @Override
    public void onRetryClick() {
    callGetSubscriptionAPI();

    }

    @Override
    public void onClick(int position) {
        adapterPosition = position;
        isSelected = true;
        if (getSubscription.getData().get(adapterPosition).getOfferPrice()==
            getSubscription.getData().get(adapterPosition).getOriginalPrice()){
            tv_note.setVisibility(View.VISIBLE);
        }else {
            tv_note.setVisibility(View.GONE);
        }

    }

    public static String round(double number, int decimals) {
        StringBuilder sb = new StringBuilder(decimals + 2);
//        sb.append("#.");
//        for (int i = 0; i < decimals; i++) {
//            sb.append("0");
//        }
        return  new DecimalFormat(sb.toString()).format(number);
    }


    private boolean hasSubscription() {
       /* if (purchaseTransactionDetails != null) {
            return purchaseTransactionDetails.purchaseInfo != null;
        }*/
        return false;
    }

 /*   @Override
    public void onProductPurchased(String productId, TransactionDetails details) {

    }*/

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable PurchaseInfo details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

        Log.d("MainActivity", "onBillingInitialized: ");


      //  purchaseTransactionDetails = bp.get(getResources().getString(R.string.premium));

      //  bp.loadOwnedPurchasesFromGoogle();


        if (hasSubscription()) {
            Log.e(TAG, "Status: Premium" );
        } else {
            Log.e(TAG, "Status: Free" );
        }

    }

 /*   @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        *//*if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }*//*
    }
*/
    /*@RequiresApi(api = Build.VERSION_CODES.O)
    boolean compareDate(String date){
        String localDate = String.valueOf(LocalDate.now());
        Log.e(TAG, "Date1 : " +localDate);
        Log.e(TAG, "Date2 : " + date);
        SimpleDateFormat objSDF = new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat out = new SimpleDateFormat("dd-mm-yyyy");
        Date dt_1 = null;
        Date dt_2 = null;
        try {
            dt_1 = objSDF.parse(localDate);
            dt_2 = objSDF.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
      *//*  String d1 = out.format(dt_1);
        String d2 = out.format(dt_2);

        Date dt_3 = null;
        Date dt_4 = null;
        try {
            dt_3 = out.parse("18-05-2021");
            dt_4 = out.parse("20-04-2021");
        } catch (ParseException e) {
            e.printStackTrace();
        }*//*

        Log.e(TAG, "Date1 : " + objSDF.format(dt_1));
        Log.e(TAG, "Date2 : " + objSDF.format(dt_2));


        if (dt_1.after(dt_2)) {
            Log.e(TAG, "Date1 is after Date2");
            return true;
        }
        return false;
    }*/
    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }
}