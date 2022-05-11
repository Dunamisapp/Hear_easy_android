package com.heareasy.activity;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.heareasy.R;
import com.heareasy.adapter.FaqAdapter;
import com.heareasy.common_classes.API_LIST;
import com.heareasy.common_classes.MethodFactory;
import com.heareasy.common_classes.MyStateView;
import com.heareasy.model.ChildInfo;
import com.heareasy.model.FaqModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public class FaqActivity extends AppCompatActivity implements MyStateView.ProgressClickListener {
    private static final String TAG = "FaqActivity";
    private  Context context = FaqActivity.this;
    private ExpandableListView expandableListView_faq;
    private Toolbar toolbar_faq;
    private boolean isClicked = true;

    private LinkedHashMap<String, FaqModel> subjects = new LinkedHashMap<String, FaqModel>();
    private ArrayList<FaqModel> deptList = new ArrayList<FaqModel>();
    private FaqAdapter faqAdapter;
    private MyStateView mLoadingBar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        toolbar_faq = findViewById(R.id.toolbar_faq);
        toolbar_faq.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar_faq.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        expandableListView_faq = findViewById(R.id.expandableListView_faq);


        mLoadingBar = new MyStateView(this,null);

        // Call FAQ API
        callFaqAPI();

        expandableListView_faq.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                FaqModel headerInfo = deptList.get(groupPosition);
                ChildInfo detailInfo = headerInfo.getProductList().get(childPosition);
        /*        Toast.makeText(getBaseContext(), " Clicked on :: " + headerInfo.getName()
                        + "/" + detailInfo.getName(), Toast.LENGTH_LONG).show();*/
                return false;
            }
        });

        expandableListView_faq.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                //get the group header
                FaqModel headerInfo = deptList.get(groupPosition);
                return false;
            }
        });

    }

    //method to expand all groups
    private void expandAll() {
        int count = faqAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            expandableListView_faq.expandGroup(i);
        }
    }

    //method to collapse all groups
    private void collapseAll() {
        int count = faqAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            expandableListView_faq.collapseGroup(i);
        }
    }

    //load some initial data into out list
    private void callFaqAPI() {
        mLoadingBar.showLoading();
        AndroidNetworking.get(API_LIST.FAQ)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(TAG, "onResponse:>>>>>>>>>" + response.toString());
                    try {
                        if (response.getString("responseCode").equalsIgnoreCase("200")) {
                            mLoadingBar.showContent();
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                String question = object.getString("question");
                                String answer = object.getString("answer");
                                addProduct(question,answer);
                                Log.e(TAG, ": >>>>>>>"+question+" "+answer );
                            }
                            faqAdapter = new FaqAdapter(FaqActivity.this, deptList);
                            expandableListView_faq.setAdapter(faqAdapter);

                        }
                        else if (response.getString("responseCode").equalsIgnoreCase("401")) {
                            mLoadingBar.showContent();
                            MethodFactory.forceLogoutDialog(FaqActivity.this);
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


    //here we maintain our products in various departments
    private int addProduct(String department, String product) {

        int groupPosition = 0;

        //check the hash map if the group already exists
        FaqModel headerInfo = subjects.get(department);
        //add the group if doesn't exists
        if (headerInfo == null) {
            headerInfo = new FaqModel();
            headerInfo.setName(department);
            subjects.put(department, headerInfo);
            deptList.add(headerInfo);
        }

        //get the children for the group
        ArrayList<ChildInfo> productList = headerInfo.getProductList();
        //size of the children list
        int listSize = productList.size();
        //add to the counter
        listSize++;

        //create a new child and add that to the group
        ChildInfo detailInfo = new ChildInfo();
        detailInfo.setSequence(String.valueOf(listSize));
        detailInfo.setName(product);
        productList.add(detailInfo);
        headerInfo.setProductList(productList);

        //find the group position inside the list
        groupPosition = deptList.indexOf(headerInfo);
        return groupPosition;
    }


    @Override
    public void onRetryClick() {

    }
}