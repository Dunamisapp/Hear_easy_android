package com.heareasy.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.heareasy.others.CustomPopUpMenu;
import com.heareasy.R;
import com.heareasy.others.WelcomePref;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenuItem;

import java.lang.reflect.Method;
import java.util.ArrayList;


public class DeviceRecyclerViewAdapter
        extends RecyclerView.Adapter<DeviceRecyclerViewAdapter.ViewHolder> {
    Context context;
    /**
     * The devices shown in this {@link RecyclerView}.
     */
    private ArrayList<BluetoothDevice> devices;
    //    private  ArrayList<DeviceListModel> devices;
    private int lastCheckedPosition = -1;

    /**
     * Callback for handling interaction events.
     */
    private final ListInteractionListener<BluetoothDevice> listener;

    /**
     * Controller for Bluetooth functionalities.
     */
    private BluetoothController bluetooth;

    private boolean pairedDevice;

    private WelcomePref welcomePref;

    /**
     * Instantiates a new DeviceRecyclerViewAdapter.
     *
     * @param listener an handler for interaction events.
     */
    public DeviceRecyclerViewAdapter(Context context, ArrayList<BluetoothDevice> devices, ListInteractionListener<BluetoothDevice> listener,
                                     boolean pairedDevice) {
        this.context = context;
        this.listener = listener;
        this.devices = devices;
        this.pairedDevice = pairedDevice;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bluetooth_search_item, parent, false);
        welcomePref = new WelcomePref(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mItem = devices.get(position);
//        holder.mImageView.setImageResource(R.drawable.ic_baseline_bluetooth_24);
        holder.mDeviceNameView.setText(devices.get(position).getName());
//       holder.mDeviceAddressView.setText(devices.get(position).getClass());
        Log.e("deviceName", "deviceName: >>>>>>>>>"+devices.get(position).getName() );

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    lastCheckedPosition = holder.getAdapterPosition();
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    listener.onItemClick(holder.mItem,position);
                    //    `  listener.onItemLongClick(holder.mItem);


                }
            }
        });
//        holder.mDeviceNameView.setText(devices.get(position).getName());
        holder.rBtn_device_item.setChecked(position == lastCheckedPosition);
        holder.rBtn_device_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastCheckedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();
                notifyItemChanged(position);
            }
        });
        if (pairedDevice) {
            SharedPreferences pref = context.getApplicationContext().getSharedPreferences("MyDevice", 0);
            String connectedDeviceName = pref.getString("deviceConnectedName","");
         /*   assert connectedDeviceName != null;
            if (connectedDeviceName.equalsIgnoreCase(devices.get(position).getName())) {
                holder.rBtn_device_item.setChecked(true);
                welcomePref.setConnectedDevice(true);
            } else {
                holder.rBtn_device_item.setChecked(false);
                welcomePref.setConnectedDevice(false);
            }*/
            holder.iv_setting_deviceIcon.setVisibility(View.VISIBLE);
            holder.iv_setting_deviceIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new CustomPopUpMenu(context, holder.iv_setting_deviceIcon, "");
                    CustomPopUpMenu.powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
                        @Override
                        public void onItemClick(int menuposition, PowerMenuItem item) {
                            switch (menuposition) {
                                case 0:
                                    listener.unpair(devices.get(position),position);
//                                    unpairDevice(devices.get(position));
                                    break;
                                default:
                                    break;
                            }
                            CustomPopUpMenu.powerMenu.dismiss();
                        }
                    });
                }
            });
        } else {
            holder.iv_setting_deviceIcon.setVisibility(View.INVISIBLE);
        }

    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
            notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("TAG", e.getMessage());
        }
    }


    private int getDeviceIcon(BluetoothDevice device) {
        if (bluetooth.isAlreadyPaired(device)) {
            return R.drawable.ic_baseline_bluetooth_connected;
        } else {
            return R.drawable.ic_baseline_bluetooth_24;
        }
    }


    @Override
    public int getItemCount() {
        return devices.size();
    }


    /*@Override
    public void onDeviceDiscovered(BluetoothDevice device) {
        listener.endLoading(true);
        if (device.getName()!=null) {
            devices.add(device);

            Log.e("TAG", "onDeviceDiscovered: >>>>>>>"+device.getType() );
        }
        notifyDataSetChanged();
    }


    @Override
    public void onDeviceDiscoveryStarted() {
        cleanView();
        BluetoothDevice device = bluetooth.getBoundingDevice();
        listener.startLoading(device);
    }

    *//**
     * Cleans the view.
     *//*
    public void cleanView() {
        devices.clear();
        notifyDataSetChanged();
    }


    @Override
    public void setBluetoothController(BluetoothController bluetooth) {
        this.bluetooth = bluetooth;
    }


    @Override
    public void onDeviceDiscoveryEnd() {
        listener.endLoading(false);
    }


    @Override
    public void onBluetoothStatusChanged() {
        // Notifies the Bluetooth controller.
        bluetooth.onBluetoothStatusChanged();
    }


    @Override
    public void onBluetoothTurningOn() {
        BluetoothDevice device = bluetooth.getBoundingDevice();
        listener.startLoading(device);
    }


    @Override
    public void onDevicePairingEnded() {
        if (bluetooth.isPairingInProgress()) {
            BluetoothDevice device = bluetooth.getBoundingDevice();
            switch (bluetooth.getPairingDeviceStatus()) {
                case BluetoothDevice.BOND_BONDING:
                    // Still pairing, do nothing.
                    break;
                case BluetoothDevice.BOND_BONDED:
                    // Successfully paired.
                    listener.endLoadingWithDialog(false, device);

                    // Updates the icon for this element.
                    notifyDataSetChanged();
                    break;
                case BluetoothDevice.BOND_NONE:
                    // Failed pairing.
                    listener.endLoadingWithDialog(true, device);
                    break;
            }
        }
    }*/

    /**
     * ViewHolder for a BluetoothDevice.
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * The inflated view of this ViewHolder.
         */
        final View mView;

        /**
         * The icon of the device.
         */
        ImageView mImageView;

        /**
         * The name of the device.
         */
        final TextView mDeviceNameView;

        /**
         * The MAC address of the BluetoothDevice.
         */
//        final TextView mDeviceAddressView;

        /**
         * The item of this ViewHolder.
         */
        BluetoothDevice mItem;
        RadioButton rBtn_device_item;
        ImageView iv_setting_deviceIcon;

        /**
         * Instantiates a new ViewHolder.
         *
         * @param view the inflated view of this ViewHolder.
         */
        ViewHolder(View view) {
            super(view);
            mView = view;
            iv_setting_deviceIcon = view.findViewById(R.id.iv_setting_deviceIcon);
            mDeviceNameView = view.findViewById(R.id.tv_name_bluetoothSearchItem);
            rBtn_device_item = view.findViewById(R.id.rBtn_device_item);
//          mDeviceAddressView = view.findViewById(R.id.tv_address_bluetoothSearchItem);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + BluetoothController.deviceToString(mItem) + "'";
        }
    }
}

