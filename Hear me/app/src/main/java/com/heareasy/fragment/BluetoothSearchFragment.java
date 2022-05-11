package com.heareasy.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.heareasy.R;
import com.heareasy.adapter.BluetoothSearchAdapter;
import com.heareasy.model.BluetoothSearchModel;

import java.util.ArrayList;


public class BluetoothSearchFragment extends Fragment {

    private RecyclerView rv_bluetoothSearch;
    private ArrayList<BluetoothSearchModel> list;
    private BluetoothSearchAdapter bluetoothSearchAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetooth_search, container, false);
        init(view);

        list = new ArrayList<>();
        list.add(new BluetoothSearchModel("Alobha Tech","80:6B:72:59:2F:EA"));
        list.add(new BluetoothSearchModel("Galaxy A70","9C:6B:72:59:2F:EA"));

        rv_bluetoothSearch.setLayoutManager(new LinearLayoutManager(getActivity()));
        bluetoothSearchAdapter = new BluetoothSearchAdapter(getActivity(),list);
        rv_bluetoothSearch.setAdapter(bluetoothSearchAdapter);


        return view;
    }

    private void init(View view) {

        rv_bluetoothSearch = view.findViewById(R.id.rv_bluetoothSearch);
    }
}