package com.heareasy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.heareasy.R;
import com.heareasy.model.ChildInfo;
import com.heareasy.model.FaqModel;

import java.util.ArrayList;

public class FaqAdapter  extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<FaqModel> deptList;

    public FaqAdapter(Context context, ArrayList<FaqModel> deptList) {
        this.context = context;
        this.deptList = deptList;
    }

   @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<ChildInfo> productList = deptList.get(groupPosition).getProductList();
        return productList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View view, ViewGroup parent) {

        ChildInfo detailInfo = (ChildInfo) getChild(groupPosition, childPosition);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.faq_child_item, null);
        }


//        TextView sequence = (TextView) view.findViewById(R.id.tv_sequence);
//        sequence.setText(detailInfo.getSequence().trim() + ". ");
        TextView childItem = (TextView) view.findViewById(R.id.tv_childItem_faq);
        childItem.setText(detailInfo.getName().trim());

        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        ArrayList<ChildInfo> productList = deptList.get(groupPosition).getProductList();
        return productList.size();

    }

    @Override
    public Object getGroup(int groupPosition) {
        return deptList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return deptList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view,
                             ViewGroup parent) {

        FaqModel headerInfo = (FaqModel) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.faq_item, null);
        }

        TextView heading =  view.findViewById(R.id.tv_faq);
        heading.setText(headerInfo.getName().trim());
        ImageView image_arrow =  view.findViewById(R.id.image_arrow);


        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}