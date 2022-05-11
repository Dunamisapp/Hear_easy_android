package com.heareasy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heareasy.others.CustomPopUpMenu;
import com.heareasy.R;
import com.heareasy.model.BluetoothSearchModel;

import java.util.ArrayList;

public class BluetoothSearchAdapter extends RecyclerView.Adapter<BluetoothSearchAdapter.BluetoothSearchViewHolder> {
    Context context;
    ArrayList<BluetoothSearchModel> list;

    public BluetoothSearchAdapter(Context context, ArrayList<BluetoothSearchModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public BluetoothSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bluetooth_search_item,parent,false);
        return new BluetoothSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothSearchViewHolder holder, int position) {

        BluetoothSearchModel item = list.get(position);
        holder.b_name.setText(item.getB_name());
        holder.b_address.setText(item.getB_address());
        holder.iv_setting_deviceIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CustomPopUpMenu(context, holder.iv_setting_deviceIcon,"");
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BluetoothSearchViewHolder extends RecyclerView.ViewHolder {
        TextView b_name, b_address;
        ImageView iv_setting_deviceIcon;
        public BluetoothSearchViewHolder(@NonNull View itemView) {
            super(itemView);

            b_name = itemView.findViewById(R.id.tv_name_bluetoothSearchItem);
            iv_setting_deviceIcon = itemView.findViewById(R.id.iv_setting_deviceIcon);
//            b_address = itemView.findViewById(R.id.tv_address_bluetoothSearchItem);
        }
    }
}
