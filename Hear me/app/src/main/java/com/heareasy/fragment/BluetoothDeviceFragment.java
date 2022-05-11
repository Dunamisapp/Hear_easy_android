package com.heareasy.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.heareasy.others.MyBroadcastReceiver;
import com.heareasy.R;
import com.heareasy.activity.DashBoardActivity;
import com.heareasy.adapter.BluetoothSearchAdapter;
import com.heareasy.bluetooth.BluetoothController;
import com.heareasy.bluetooth.BluetoothDiscoveryDeviceListener;
import com.heareasy.bluetooth.DeviceRecyclerViewAdapter;
import com.heareasy.bluetooth.ListInteractionListener;
import com.heareasy.others.WelcomePref;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class BluetoothDeviceFragment extends Fragment implements ListInteractionListener<BluetoothDevice>,
        BluetoothDiscoveryDeviceListener {
    Context context = getActivity();
    private RecyclerView rv_pairedDevice;
    private RecyclerView rv_availableDevice;
    private ArrayList<BluetoothDevice> pairedList;
    private ArrayList<BluetoothDevice> availableList;
    private BluetoothSearchAdapter bluetoothSearchAdapter;
    private FloatingActionButton faBtn_search;
    private BluetoothController bluetooth;
    private DeviceRecyclerViewAdapter recyclerViewAdapter;
    private ProgressDialog bondingProgressDialog;
    private ListInteractionListener<BluetoothDevice> listListener;

    private BluetoothDevice pairedDevice;
    private TextView tv_connectedDevice;
    private TextView tvTitle;
    private TextView tvMessage;
    private String connectedDeviceName;
    private ProgressBar progress_bar;
    private ProgressDialog pd;
    public static boolean checkConnectedBluetooth = false;
    WelcomePref welcomePref;
    private View view;
    private LinearLayout llConnected;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bluetooth_device, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFunc();

    }


    private void initFunc() {

        welcomePref = new WelcomePref(getActivity());
        init(view);

        DashBoardActivity.tv_toolbar_tittle.setText("Select Audio Output");

        boolean hasBluetooth = getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
        if (!hasBluetooth) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
            dialog.setTitle(getString(R.string.bluetooth_not_available_title));
            dialog.setMessage(getString(R.string.bluetooth_not_available_message));
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Closes the dialog and terminates the activity.
                            dialog.dismiss();
                        }
                    });
            dialog.setCancelable(false);
            dialog.show();
        }

        // Sets up the bluetooth controller.
        this.bluetooth = new BluetoothController(getActivity(), BluetoothAdapter.getDefaultAdapter(), this);

        discoveringDevice(view);

        faBtn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discoveringDevice(view);
            }
        });

        checkConnected();
        getPairedDevices();
    }

    @Override
    public void onResume() {
        super.onResume();

        this.bluetooth = new BluetoothController(getActivity(), BluetoothAdapter.getDefaultAdapter(), this);
        checkConnected();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (getActivity() == null)
                    return;

                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        pairedList.clear();
                        getPairedDevices();
                    }
                });

            }
        }, 0, 1000);

        SharedPreferences pref = getActivity().getSharedPreferences("MyDevice", 0);
        String connectedDeviceName = pref.getString("deviceConnectedName", "");
        if (connectedDeviceName != null && !connectedDeviceName.equals("")) {
            llConnected.setVisibility(View.VISIBLE);
            tvMessage.setVisibility(View.GONE);
            tvTitle.setVisibility(View.VISIBLE);
            tv_connectedDevice.setText(connectedDeviceName);
        } else {
            llConnected.setVisibility(View.GONE);
            tvMessage.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.GONE);
        }
    }

    private void discoveringDevice(View view) {


        // If the bluetooth is not enabled, turns it on.
        if (!this.bluetooth.isBluetoothEnabled()) {
//            Snackbar.make(view, R.string.enabling_bluetooth, Snackbar.LENGTH_SHORT).show();
            bluetooth.turnOnBluetoothAndScheduleDiscovery();
        } else {
            //Prevents the user from spamming the button and thus glitching the UI.
            if (!this.bluetooth.isDiscovering()) {
                // Starts the discovery.
//                Snackbar.make(view, R.string.device_discovery_started, Snackbar.LENGTH_SHORT).show();
                this.bluetooth.startDiscovery();
            } else {
//                Snackbar.make(view, R.string.device_discovery_stopped, Snackbar.LENGTH_SHORT).show();
                this.bluetooth.cancelDiscovery();
            }
        }
    }

    private void init(View view) {
        rv_pairedDevice = view.findViewById(R.id.rv_pairedDevice_bluetooth_device);
        rv_availableDevice = view.findViewById(R.id.rv_availableDevice_bluetooth_device);
        faBtn_search = view.findViewById(R.id.faBtn_search);
        tv_connectedDevice = view.findViewById(R.id.tv_connectedDevice);
        progress_bar = view.findViewById(R.id.progress_bar);
        llConnected = view.findViewById(R.id.llConnected);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvMessage = view.findViewById(R.id.tvMessage);


        availableList = new ArrayList<>();
        pairedList = new ArrayList<>();


    }


    @Override
    public void onItemClick(BluetoothDevice device, int position) {
        if (this.bluetooth.isAlreadyPaired(device)) {
//            startActivity(new Intent(getActivity(), RecordActivity.class));
//            MyToast.display(getActivity(), "Device already paired");

          /*  if (!MyBroadcastReceiver.connectedBluetooth) {
                Log.e(TAG, "connectedBluetooth:>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " );
                this.bluetooth.unpairDevice(device);
                device.createBond();
            }*/

        } else {
            Log.d(TAG, "Device not paired. Pairing.");
            boolean outcome = bluetooth.pair(device);
            String deviceName = BluetoothController.getDeviceName(device);
            if (outcome) {
                Log.d(TAG, "Showing pairing dialog");
                bondingProgressDialog = ProgressDialog.show(getActivity(), "", "Pairing with device " + deviceName + "...", true, false);
                SharedPreferences pref = getActivity().getSharedPreferences("MyDevice", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("deviceConnectedName", device.getName());
                editor.apply();
                welcomePref.setConnectedDevice(true);
                setDevices(device);
                onResume();


            } else {
                Log.d(TAG, "Error while pairing with device " + deviceName + "!");
//                MyToast.display(getActivity(), "Error while pairing with device " + deviceName + "!");
            }
        }

    }

    @Override
    public void startLoading() {

    }

    @Override
    public void endLoading(boolean partialResults) {

    }

    // Unpair device
    @Override
    public void unpair(BluetoothDevice device, int position) {
        SharedPreferences pref = getActivity().getSharedPreferences("MyDevice", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();

        this.bluetooth.unpairDevice(device);
        this.bluetooth.unpairDevice(MyBroadcastReceiver.device);
//        MyBroadcastReceiver.bluetooth_on = false;
        MyBroadcastReceiver.connectedBluetooth = false;
        checkConnectedBluetooth = false;
        welcomePref.setConnectedDevice(false);
        this.bluetooth.cancelDiscovery();
        onResume();
        initFunc();
    }

    public void Reload() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(BluetoothDeviceFragment.this).attach(BluetoothDeviceFragment.this).commit();
    }

    @Override
    public void onDeviceAlreadyConnected(BluetoothDevice connectedDevice) {
        Log.e("DeviceAlreadyConnected", "DeviceAlreadyConnected>>>>>>>>>>>>>>" + connectedDevice);
        if (this.bondingProgressDialog != null) {
            this.bondingProgressDialog.dismiss();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    getPairedDevices();
                }
            }, 3000);
        }

    }

    @Override
    public void endLoadingWithDialog(boolean error, BluetoothDevice device) {
        if (this.bondingProgressDialog != null) {
            String message = null;
            String deviceName = BluetoothController.getDeviceName(device);
            String deviceAddress = BluetoothController.getDeviceAddress(device);

            // Gets the message to print.
            if (error) {
                message = "Failed pairing with device " + deviceName + "!";
            } else {
                message = "Successfully paired with device " + deviceName + "!";

               /* FragmentTransaction ft = this.getChildFragmentManager().beginTransaction();
                ft.detach(BluetoothDeviceFragment.this).attach(BluetoothDeviceFragment.this).commit();*/
            }

//          Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            this.bondingProgressDialog.dismiss();

            // Cleans up state.
            this.bondingProgressDialog = null;

        }


    }

    @Override
    public void onDeviceDiscovered(BluetoothDevice device) {
        Log.e("DeviceDiscovered", ">>>>>>>>>>>>>>" + device.toString());
        if (device.getName() != null) {
            // Set device into list
            setDevices(device);
        }
        endLoading(true);

    }

    public void setDevices(BluetoothDevice device) {
    /*    if (this.bluetooth.isAlreadyPaired(device)) {
            Log.e("bluetooth.isAlreadyPaired(device)", ">>>>>>>>>"+this.bluetooth.isAlreadyPaired(device) );
            pairedList.add(device);
            Set<BluetoothDevice> set = new HashSet<>(pairedList);
            pairedList.clear();
            pairedList.addAll(set);
            Log.e("pairedList-size", ">>>>>>>>>>>>>>"+pairedList.size() );
            recyclerViewAdapter = new DeviceRecyclerViewAdapter(getActivity(), pairedList, this, true);
            rv_pairedDevice.setLayoutManager(new LinearLayoutManager(getActivity()));
            rv_pairedDevice.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.notifyDataSetChanged();

        }*/
        getPairedDevices();

        availableList.add(device);
        Set<BluetoothDevice> set = new HashSet<>(availableList);
        availableList.clear();
        availableList.addAll(set);
        Log.e("availableList-size", ">>>>>>>>>>>>>>" + availableList.size());
        recyclerViewAdapter = new DeviceRecyclerViewAdapter(getActivity(), availableList, this, false);
        rv_availableDevice.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_availableDevice.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

        checkConnected();

        Log.e("TAG", "devices: >>>>>>>" + device.getBondState());
    }

    public void getPairedDevices() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedList.add(device);
                Set<BluetoothDevice> set = new HashSet<>(pairedList);
                pairedList.clear();
                pairedList.addAll(set);
                Log.e("pairedList-size", ">>>>>>>>>>>>>>" + pairedList.size());
                recyclerViewAdapter = new DeviceRecyclerViewAdapter(getActivity(), pairedList, this, true);
                rv_pairedDevice.setLayoutManager(new LinearLayoutManager(getActivity()));
                rv_pairedDevice.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        this.bluetooth.cancelDiscovery();
    }

    @Override
    public void onDeviceDiscoveryStarted() {
        progress_bar.setVisibility(View.VISIBLE);
        startLoading();
    }

    @Override
    public void setBluetoothController(BluetoothController bluetooth) {
        this.bluetooth = bluetooth;
    }

    @Override
    public void onDeviceDiscoveryEnd() {
        progress_bar.setVisibility(View.GONE);
        endLoading(false);
    }

    @Override
    public void onBluetoothStatusChanged() {
        bluetooth.onBluetoothStatusChanged();
    }

    @Override
    public void onBluetoothTurningOn() {
        startLoading();
    }

    @Override
    public void onDevicePairingEnded() {
        if (this.bluetooth.isPairingInProgress()) {
            BluetoothDevice device = bluetooth.getBoundingDevice();
            switch (this.bluetooth.getPairingDeviceStatus()) {
                case BluetoothDevice.BOND_BONDING:
                    // Still pairing, do nothing.
                    break;
                case BluetoothDevice.BOND_BONDED:
                    // Successfully paired.
                    endLoadingWithDialog(false, device);
                    break;
                case BluetoothDevice.BOND_NONE:
                    // Failed pairing.
                    endLoadingWithDialog(true, device);
                    break;

            }
        }
    }

    public void checkConnected() {

        Log.e("checkConnected", ">>>>>>>>");

        // true == headset connected && connected headset is support hands free
        int state = BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(BluetoothProfile.HEADSET);
        Log.e("checkConnected", ">>>>>>>>" + state);
        if (state != BluetoothProfile.STATE_CONNECTED)
            return;
        Log.e("checkConnected", ">>>>>>>>" + BluetoothProfile.STATE_CONNECTED);

        try {
            BluetoothAdapter.getDefaultAdapter().getProfileProxy(getActivity(), serviceListener, BluetoothProfile.HEADSET);
            Log.e("serviceListener", ">>>>>>>>");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceDisconnected(int profile) {
            checkConnectedBluetooth = false;
            Log.e("ServiceDisconnected", ">>>>>>>>");
        }

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            checkConnectedBluetooth = true;
            SharedPreferences pref = getActivity().getSharedPreferences("MyDevice", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("deviceConnected", true);
            editor.apply();
            for (BluetoothDevice device : proxy.getConnectedDevices()) {
                Log.e(TAG, "checkConnected: >>>>>>>>>>>>>>>" + device);

               /* pairedList.add(device);
                Set<BluetoothDevice> set = new HashSet<>(pairedList);
                pairedList.clear();
                pairedList.addAll(set);
                recyclerViewAdapter = new DeviceRecyclerViewAdapter(getActivity(), pairedList, BluetoothDeviceFragment.this, true);
                rv_pairedDevice.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerViewAdapter.notifyDataSetChanged();
                rv_pairedDevice.setAdapter(recyclerViewAdapter);*/
                Log.e("onServiceConnected>>>>>", "|" + device.getName() + " | " + device.getAddress() + " | " + proxy.getConnectionState(device) + "(connected = "
                        + BluetoothProfile.STATE_CONNECTED + ")");
                editor.putString("deviceConnectedName", device.getName());
                editor.apply();
                welcomePref.setConnectedDevice(true);

            }

            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy);
        }
    };
}