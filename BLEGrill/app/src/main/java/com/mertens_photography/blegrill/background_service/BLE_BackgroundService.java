package com.mertens_photography.blegrill.background_service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.mertens_photography.blegrill.ble.BLEAdvertisePacket;

import java.util.ArrayList;

/**
 * Created by InScene on 30.06.2015.
 */
public class BLE_BackgroundService extends Service {

    private BluetoothAdapter mBluetoothAdapter;
    private static final long SCAN_PERIOD = 3000;   // 3 Sekunden

    public IBinder onBind(Intent intent) {
        // Fuer dieses Tutorial irrelevant. Gehoert zu bounded Services.
        return null;
    }

    @Override
    public void onCreate() {
        Log.v("BLEGrill", System.currentTimeMillis()
                + ": BackgroundService erstellt.");

        final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("BLEGrill", System.currentTimeMillis()
                + ": BackgroundService gestartet.");

        /* Starte BLE Scan um Advertise Daten zu empfangen */
        scanLeDevice();

        // Nachdem unsere Methode abgearbeitet wurde, soll sich der Service
        // selbst stoppen.

        // Um den Service laufen zu lassen, bis er explizit gestoppt wird,
        // geben wir "START_STICKY" zurueck.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.v("BLEGrill", System.currentTimeMillis()
                + ": BackgroundService zerstoert.");
    }

    private void scanLeDevice() {
        new Thread() {

            @Override
            public void run() {
                // Starte Scan
                mBluetoothAdapter.startLeScan(mLeScanCallback);

                // Warte etwas um Daten empfangen zu koennen
                try {
                    Thread.sleep(SCAN_PERIOD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Stoppe Scan
                mBluetoothAdapter.stopLeScan(mLeScanCallback);

                stopSelf();
            }
        }.start();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             final byte[] scanRecord) {

            BLEAdvertisePacket appearancePacket = BLEAdvertisePacket.bleGrill_Appearance_Packet();
            ArrayList<BLEAdvertisePacket> listPackets = BLEAdvertisePacket.convertBufferToAdvPackets(scanRecord);

            if (listPackets.contains(appearancePacket)) {
                Log.v("BLEGrill", System.currentTimeMillis()
                        + ": Daten vom BLEGrill empfangen");

                for(int i=0; i<listPackets.size(); i++)
                {
                    BLEAdvertisePacket packet = listPackets.get(i);
                    if( packet.isTemperatureData())
                    {
                        Log.v("BLEGrill", System.currentTimeMillis()
                                + ": Temp1:" + packet.getTemperatureData((byte)0)
                                + ", Temp2:" + packet.getTemperatureData((byte)1)
                                + ", Temp3:" + packet.getTemperatureData((byte)2)
                                + ", Temp4:" + packet.getTemperatureData((byte)3) );
                    }
                }

            }

        }
    };
}
