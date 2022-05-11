package com.heareasy.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.heareasy.R;
import com.heareasy.others.WelcomePref;
import com.heareasy.common_classes.API_LIST;
import com.heareasy.common_classes.MethodFactory;
import com.heareasy.common_classes.MyStateView;
import com.heareasy.common_classes.MyToast;
import com.heareasy.common_classes.SessionManager;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class PaymentActivity extends AppCompatActivity implements MyStateView.ProgressClickListener {

    TextView Back;
    Button pay;
    RelativeLayout toolbar;
    TextView title, expired_date;
    EditText amount, card_holder, card_number, zip_code;
    private static final String TAG = "MainActivity";
    TextInputEditText Date;
    DatePickerDialog.OnDateSetListener date;
    Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    TextInputLayout nameLayout, amountLayout, zipLayout, dateLayout, numberLayout;
    private MyStateView myStateview;
    private SessionManager myPreferences;
    private CardMultilineWidget card_input_widget;
    private Card card;
    public static final int STRIPE_TOKEN_CODE = 212;
    private int START_ALIPAY_REQUEST = 100;
    public static final String STRIPE_TOKEN_ID = "token_id";
  //  public static final String PUBLISHABLE_KEY = "pk_test_I71hW1HMRNeKcsF2IRuQk3ga00ZtdU01e5";
//   public static final String PUBLISHABLE_KEY = "pk_live_nGIPnvmYzpXAWYyewBFT3rca00IWCuUYIv";
   public static final String PUBLISHABLE_KEY = "pk_test_51HmZaCKfB2n3mjF4fPBwcJZFzmUMl7ga2v6hw6kRGi7Vffuw8l56t0WlzpC5m7WYRakE4NKkVp9kBxR2v2qyrnJz00S13tWhnk";
    private String event_id;
    private String token_id;
    private Context context = PaymentActivity.this;
    private WelcomePref welcomePref;
    private String subscription_package_id="";
    private String package_amount="";
    private String payable_amount="";
    private String total_discount="";
    private String expiry_date="";
    private String plan_status="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        myStateview = new MyStateView(this, null);
        myPreferences = new SessionManager(this);
        welcomePref = new WelcomePref(this);

        if (getIntent() != null) {
            plan_status = getIntent().getStringExtra("plan_status");
            subscription_package_id = getIntent().getStringExtra("subscription_package_id");
            package_amount = getIntent().getStringExtra("package_amount");
            payable_amount = getIntent().getStringExtra("payable_amount");
            total_discount = getIntent().getStringExtra("total_discount");
            expiry_date = getIntent().getStringExtra("expiry_date");
        }

        card_input_widget = findViewById(R.id.card_input_widget);
        Back = (TextView) findViewById(R.id.home);
        title = (TextView) findViewById(R.id.title);
        pay = (Button) findViewById(R.id.payment);
        toolbar = (RelativeLayout) findViewById(R.id.toolbar);
        amount = (EditText) findViewById(R.id.amount);
        card_holder = (EditText) findViewById(R.id.holder);
        card_number = (EditText) findViewById(R.id.card_number);
        expired_date = (TextView) findViewById(R.id.expiry_date);
        zip_code = (EditText) findViewById(R.id.zip);
        numberLayout = (TextInputLayout) findViewById(R.id.card_numberLyout);
        nameLayout = (TextInputLayout) findViewById(R.id.nameLyout);
        amountLayout = (TextInputLayout) findViewById(R.id.amountLyout);
        zipLayout = (TextInputLayout) findViewById(R.id.ziplayout);
        dateLayout = (TextInputLayout) findViewById(R.id.datelayout);
        amount.setText(payable_amount);
        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (isvalidationAccount()) {
                        card = new Card(
                                card_input_widget.getCard().getNumber(), //card number
                                card_input_widget.getCard().getExpMonth(), //expMonth
                                card_input_widget.getCard().getExpYear(),//expYear
                                card_input_widget.getCard().getCVC()//cvc
                        );
                        // Make payment
                        buy();
                    }
            }
        });

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });
        expired_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(PaymentActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }


    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        expired_date.setText(sdf.format(myCalendar.getTime()));
    }

    private void buy() {
        boolean validation = card.validateCard();
        if (validation) {
            myStateview.showLoading();
            //  ProgressBarDialog.showProgressBar(StripeActivity.this, "");
            new Stripe(PaymentActivity.this).createToken(
                    card,
                    PUBLISHABLE_KEY,
                    new TokenCallback() {
                        @Override
                        public void onError(Exception error) {
                            myStateview.showContent();
                            Log.e("Stripe", "------ERROR----" + error.toString());
                            MyToast.display(PaymentActivity.this, error.getMessage());

                        }

                        @Override
                        public void onSuccess(Token token) {
                            myStateview.showContent();
                            Log.e("Stripe", "------SUCESS----" + token.getType());
                            Log.e("Message", "Id-------" + token.getId());
                            Log.e("Message", "Type-------" + token.getType());
                            Log.e("Message", "Account-------" + token.getBankAccount());
                            Log.e("Message", "Card-------" + token.getCard());
                            Log.e("Message", "Created-------" + token.getCreated());
                            Log.e("Message", "Livemode-------" + token.getLivemode());
                            Log.e("Message", "Used-------" + token.getUsed());
                            Log.e("token_id", ">>>>>>>>>>>>>>>>" + token.getId());
                            token_id = token.getId();
//                            donate(token.getId());
                            if (plan_status.equalsIgnoreCase("renew")){
                                callReNewSubscriptionAPI();
                            }else {
                                callBuyNowSubscriptionAPI();
                            }
                        }
                    });

        } else if (!card.validateNumber()) {
            myStateview.showContent();

            Log.e("Stripe", "The card number that you entered is invalid");
        } else if (!card.validateExpiryDate()) {
            myStateview.showContent();

            Log.e("Stripe", "The expiration date that you entered is invalid");
        } else if (!card.validateCVC()) {
            myStateview.showContent();

            Log.e("Stripe", "The CVC code that you entered is invalid");
        } else {
            myStateview.showContent();
            Log.e("Stripe", "The card details that you entered are invalid");
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_ALIPAY_REQUEST) {
            if (resultCode == Activity.RESULT_CANCELED) {
                // Do not use the source

            } else {
                // The source was approved.

            }
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    private boolean isvalidationAccount() {
        if (amount.getText().toString().isEmpty()) {
            MyToast.display(PaymentActivity.this, "Please Enter Amount");
            return false;
        } else if (amount.getText().toString().equalsIgnoreCase("0")) {
            MyToast.display(PaymentActivity.this, "Amount should not be 0");
            return false;
        } else if (card_holder.getText().toString().isEmpty()) {
            MyToast.display(PaymentActivity.this, "Please Enter Card Holder Name");
            return false;
        }else if(card_input_widget.getCard()==null){
            MyToast.display(PaymentActivity.this, "Please Enter Card Details");
            return false;
        }
        return true;
    }

    // Buy a new subscription
    private void callBuyNowSubscriptionAPI() {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Log.e(TAG, "date: >>>>>>>"+date );
        myStateview.showLoading();
        AndroidNetworking.post(API_LIST.ADD_SUBSCRIPTION)
            .addHeaders("Authorization", myPreferences.getAuthToken())
            .addHeaders("id", myPreferences.getUserID())
            .addBodyParameter("subscription_package_id",subscription_package_id)
            .addBodyParameter("starting_date", date+"")
            .addBodyParameter("package_amount",package_amount)
            .addBodyParameter("payable_amount",payable_amount)
            .addBodyParameter("total_discount",total_discount)
            .addBodyParameter("expiry_date",expiry_date)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(TAG, "onResponse:>>>>>>>>>" + response.toString());
                    try {
                        if (response.getString("responseCode").equalsIgnoreCase("200")) {
                            myStateview.showContent();
                            MyToast.display(context, "" + response.getString("responseMessage"));
                            welcomePref.setSubscriptionStatus(true);
                            startActivity(new Intent(context,DashBoardActivity.class));
                            finishAffinity();
                        }
                        else if (response.getString("responseCode").equalsIgnoreCase("401")) {
                            myStateview.showContent();
                            MethodFactory.forceLogoutDialog(PaymentActivity.this);
                        }
                        else {
                            myStateview.showContent();
                            MyToast.display(context, "" + response.getString("responseMessage"));
                            Log.e(TAG, ">>>>>>>: "+ response.getString("responseMessage"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ANError error) {
                    Log.e(TAG, "onError:>>>>" + error.getMessage());
                    myStateview.showRetry();
                }
            });
    }

    private void callReNewSubscriptionAPI() {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Log.e(TAG, "date: >>>>>>>" + date);
        myStateview.showLoading();
        AndroidNetworking.post(API_LIST.ADD_SUBSCRIPTION)
            .addHeaders("Authorization", myPreferences.getAuthToken())
            .addHeaders("id", myPreferences.getUserID())
            .addBodyParameter("subscription_package_id", subscription_package_id)
            .addBodyParameter("starting_date", date + "")
            .addBodyParameter("package_amount", package_amount)
            .addBodyParameter("payable_amount", payable_amount)
            .addBodyParameter("total_discount", total_discount)
            .addBodyParameter("expiry_date", expiry_date)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(TAG, "onResponse:>>>>>>>>>" + response.toString());
                    try {

                        if (response.getString("responseCode").equalsIgnoreCase("200")) {
                            myStateview.showContent();
                            MyToast.display(context, "" + response.getString("responseMessage"));
                            startActivity(new Intent(context,DashBoardActivity.class));
                            finishAffinity();
                        }
                        else if (response.getString("responseCode").equalsIgnoreCase("401")) {
                            myStateview.showContent();
                            MethodFactory.forceLogoutDialog(PaymentActivity.this);
                        }
                        else {
                            myStateview.showContent();
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
                    myStateview.showRetry();
                }
            });
    }

    @Override
    public void onRetryClick() {
        callBuyNowSubscriptionAPI();
    }
}
