package com.heareasy.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.heareasy.R;
import com.heareasy.common_classes.SessionManager;

public class SettingActivity extends AppCompatActivity {
    private TextView tv_change_password, tv_my_subscription;
    private CardView card_change_password, card_my_subscription,cardKaraoke;
    public static Switch btnSwitch;
    private SessionManager sessionManager;
    private boolean isBtnChecked;
    Toolbar toolbar;
    private CardView cardSwitch;
    private FingerprintManagerCompat managerCompat;


    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        DashBoardActivity.tv_toolbar_tittle.setText("Settings");
        sessionManager = new SessionManager(getActivity());
        managerCompat = FingerprintManagerCompat.from(getActivity());

        tv_change_password = view.findViewById(R.id.tv_change_password);
        tv_my_subscription = view.findViewById(R.id.tv_my_subscription);
        card_change_password = view.findViewById(R.id.card_change_password);
        card_my_subscription = view.findViewById(R.id.card_my_subscription);
        cardSwitch = view.findViewById(R.id.cardSwitch);
        btnSwitch = view.findViewById(R.id.btnSwitch);
        modeRadioGroup = view.findViewById(R.id.rg_mode);
        btnKaraoke = view.findViewById(R.id.btnKaraoke);
        btnLecture = view.findViewById(R.id.btnLecture);

        btnSwitch.setChecked(sessionManager.isFingerPrint());

        if (sessionManager.isKaraokeMode()) {
            btnKaraoke.setChecked(true);
        }else {
            btnLecture.setChecked(true);
        }

        if (managerCompat.isHardwareDetected() && managerCompat.hasEnrolledFingerprints()) {
            cardSwitch.setVisibility(View.VISIBLE);
        }else {
            cardSwitch.setVisibility(View.GONE);
        }

        card_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
            }
        });
        card_my_subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), MySubscriptionActivity.class));
            }
        });
        btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sessionManager.setFingerPrint(true);
                } else {
                    sessionManager.setFingerPrint(false);
                }
            }
        });



        view.findViewById(R.id.btnKaraoke).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclickbuttonMethod(view);
            }
        });
        view.findViewById(R.id.btnLecture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclickbuttonMethod(view);
            }
        });

        return view;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setting);

//        DashBoardActivity.tv_toolbar_tittle.setText("Settings");
        sessionManager = new SessionManager(this);
        managerCompat = FingerprintManagerCompat.from(this);


        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tv_change_password = findViewById(R.id.tv_change_password);
        tv_my_subscription = findViewById(R.id.tv_my_subscription);
        card_change_password = findViewById(R.id.card_change_password);
        card_my_subscription = findViewById(R.id.card_my_subscription);
        cardSwitch = findViewById(R.id.cardSwitch);
        btnSwitch = findViewById(R.id.btnSwitch);

        btnSwitch.setChecked(sessionManager.isFingerPrint());

        if (managerCompat.isHardwareDetected() && managerCompat.hasEnrolledFingerprints()) {
            cardSwitch.setVisibility(View.VISIBLE);
        }else {
            cardSwitch.setVisibility(View.GONE);
        }


        card_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(SettingActivity.this, ChangePasswordActivity.class));
            }
        });
        card_my_subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(SettingActivity.this, MySubscriptionActivity.class));
            }
        });
        btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sessionManager.setFingerPrint(true);
                } else {
                    sessionManager.setFingerPrint(false);
                }
            }
        });

    }


}