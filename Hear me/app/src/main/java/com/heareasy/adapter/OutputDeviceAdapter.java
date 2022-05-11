package com.heareasy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heareasy.R;
import com.heareasy.model.DeviceListModel;

import java.util.ArrayList;

public class OutputDeviceAdapter extends RecyclerView.Adapter<OutputDeviceAdapter.OutputViewHolder> {
    Context context;
    private ArrayList<DeviceListModel> device;
    private int lastCheckedPosition = -1;

    public OutputDeviceAdapter(Context context,ArrayList<DeviceListModel> device) {
        this.context = context;
        this.device = device;
    }

    @NonNull
    @Override
    public OutputViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OutputViewHolder(LayoutInflater.from(context).inflate(R.layout.available_devices,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull OutputViewHolder holder, int position) {

        holder.tv_name_bluetoothSearchItem.setText(device.get(position).getName());

        holder.rBtn_device_item.setChecked(position == lastCheckedPosition);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastCheckedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });
        holder.rBtn_device_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastCheckedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class OutputViewHolder extends RecyclerView.ViewHolder {
        RadioButton rBtn_device_item;
        TextView tv_name_bluetoothSearchItem;
        public OutputViewHolder(@NonNull View itemView) {
            super(itemView);
            rBtn_device_item = itemView.findViewById(R.id.rBtn_available_device);
            tv_name_bluetoothSearchItem = itemView.findViewById(R.id.tv_name_available_device);
        }
    }
}
