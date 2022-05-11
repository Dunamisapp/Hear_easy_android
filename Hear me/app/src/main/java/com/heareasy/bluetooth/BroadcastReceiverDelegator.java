package com.heareasy.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.io.Closeable;

/**
 * Class used to handle communication with the OS about Bluetooth system events.
 *
 * @author Donato Rimenti
 */
public class BroadcastReceiverDelegator extends BroadcastReceiver implements Closeable {

    /**
     * Callback for Bluetooth events.
     */
    private final BluetoothDiscoveryDeviceListener listener;

    /**
     * Tag string used for logging.
     */
    private final String TAG = "BroadcastReceiver";

    /**
     * The context of this object.
     */
    private final Context context;

    /**
     * Instantiates a new BroadcastReceiverDelegator.
     *
     * @param context   the context of this object.
     * @param listener  a callback for handling Bluetooth events.
     * @param bluetooth a controller for the Bluetooth.
     */
    public BroadcastReceiverDelegator(Context context, BluetoothDiscoveryDeviceListener listener, BluetoothController bluetooth) {
        this.listener = listener;
        this.context = context;
        this.listener.setBluetoothController(bluetooth);

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        context.registerReceiver(this, filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG, "Incoming intent : " + action);
        switch (action) {
            case BluetoothDevice.ACTION_FOUND :
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e(TAG, "Device discovered! " + BluetoothController.deviceToString(device));
                listener.onDeviceDiscovered(device);
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED :
                // Discovery has ended.
                Log.e(TAG, "Discovery ended.");
                listener.onDeviceDiscoveryEnd();
                break;
            case BluetoothAdapter.ACTION_STATE_CHANGED :
                // Discovery state changed.
                Log.e(TAG, "Bluetooth state changed.");
                listener.onBluetoothStatusChanged();
                break;
            case BluetoothDevice.ACTION_BOND_STATE_CHANGED :
                // Pairing state has changed.
                Log.e(TAG, "Bluetooth bonding state changed.");
                listener.onDevicePairingEnded();
                break;
                case BluetoothDevice.ACTION_ACL_CONNECTED :
                    BluetoothDevice connectedDevice = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    listener.onDeviceAlreadyConnected(connectedDevice);

                    Log.e(TAG, "Bluetooth device Connected.");

                break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED :
                    Log.e(TAG, "Bluetooth device Disconnected.");
                break;
            default :
                // Does nothing.
                break;
        }
    }

    /**
     * Called when device discovery starts.
     */
    public void onDeviceDiscoveryStarted() {
        listener.onDeviceDiscoveryStarted();
    }

    /**
     * Called when device discovery ends.
     */
    public void onDeviceDiscoveryEnd() {
        listener.onDeviceDiscoveryEnd();
    }

    /**
     * Called when the Bluetooth has been enabled.
     */
    public void onBluetoothTurningOn() {
        listener.onBluetoothTurningOn();
    }


    @Override
    public void close() {
        context.unregisterReceiver(this);
    }
}
