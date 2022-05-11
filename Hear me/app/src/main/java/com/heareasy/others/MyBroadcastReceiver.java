package com.heareasy.others;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.heareasy.fragment.RecordFragment;

import static com.heareasy.fragment.RecordFragment.checkConnectedBluetooth;

public class MyBroadcastReceiver extends BroadcastReceiver {
    public static BluetoothDevice device;
    public static boolean connectedBluetooth;
    public static boolean bluetooth_on ;


    public MyBroadcastReceiver() {
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        WelcomePref welcomePref = new WelcomePref(context);


        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            connectedBluetooth = true;

             device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            SharedPreferences pref = context.getApplicationContext().getSharedPreferences("MyDevice", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("deviceConnectedName", device.getName());
            editor.putBoolean("deviceConnected",true);
            editor.apply();


            welcomePref.setConnectedDevice(true);

            Log.e("TAG", "connectedDevice>>>>>: "+device.getName());
        }
        else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
          /*  RecordFragment.toStopSpeaker();
            if (RecordFragment.timer!=null) {
                RecordFragment.timer.stop();

            }*/
            connectedBluetooth = false;
            bluetooth_on = false;
            checkConnectedBluetooth = false;
            welcomePref.setConnectedDevice(false);


            SharedPreferences pref = context.getApplicationContext().getSharedPreferences("MyDevice", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.apply();
        }
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            switch (state){
                case BluetoothAdapter.STATE_ON:
                    bluetooth_on = true;
                    break;
                case BluetoothAdapter.STATE_OFF:
                    RecordFragment.toStopSpeaker();
                    if (RecordFragment.timer!=null) {
                        RecordFragment.timer.stop();

                    }

                    bluetooth_on = false;
                    checkConnectedBluetooth = false;
                    connectedBluetooth = false;
                    welcomePref.setConnectedDevice(false);
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    bluetooth_on = false;
                    break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        bluetooth_on = false;
                    break;
            }
        }
    }
}
