package com.heareasy.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.heareasy.R;
import com.heareasy.activity.DashBoardActivity;


public class InputDeviceFragment extends Fragment {
    private RadioButton rBtn_device_item;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_device, container, false);
        DashBoardActivity.tv_toolbar_tittle.setText("Select Input Device");
        rBtn_device_item = view.findViewById(R.id.rBtn_device_item);
        rBtn_device_item.setChecked(true);
        return view;
    }
}