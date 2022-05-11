package com.heareasy.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.heareasy.R;
import com.heareasy.common_classes.MyStateView;

import org.json.JSONException;
import org.json.JSONObject;

public class EulaActivity extends AppCompatActivity implements MyStateView.ProgressClickListener {

    private WebView mWebview;
    private Toolbar toolbar;
    private MyStateView mLoadingBar;

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eula);

        mLoadingBar = new MyStateView(this, null);

        toolbar = findViewById(R.id.toolbar);
        mWebview = findViewById(R.id.webview);
        mWebview.getSettings().setBuiltInZoomControls(true);
        mWebview.getSettings().setDisplayZoomControls(false);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getPdfDoc();
    }

    private void getPdfDoc() {
        mLoadingBar.showLoading();
        AndroidNetworking.get("http://labhllc.com/admin/public/api/agreement")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("SetJavaScriptEnabled")
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("agreement", "onResponse:>>>>>>>>>" + response.toString());
                        try {
                            if (response.getString("responseCode").equalsIgnoreCase("200")) {
                                mLoadingBar.showContent();
                                String docPath = response.getString("path");
                                String path = "http://labhllc.com/admin/public/" + docPath;
                                Log.e("path", ">>>>>>>>>>>: " + path);
                              //  mWebview.getSettings().setJavaScriptEnabled(true);
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
                                startActivity(browserIntent);
                                finish();

                               // mWebview.loadUrl(path);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("TAG", "onError:>>>>" + error.getMessage());

                    }
                });
    }

    @Override
    public void onRetryClick() {

    }
}