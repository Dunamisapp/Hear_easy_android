package com.heareasy.others;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.webkit.WebView;

import com.heareasy.R;
import com.heareasy.common_classes.API_LIST;

public class TermsAndConditionsActivity extends AppCompatActivity {

    Toolbar toolbar;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        toolbar = findViewById(R.id.toolbar_TermsConditions);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        webView = findViewById(R.id.webTerms);
        webView.loadUrl(API_LIST.TERMS);
    }
}