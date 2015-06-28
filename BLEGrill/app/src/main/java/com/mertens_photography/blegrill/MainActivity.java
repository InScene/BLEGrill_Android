package com.mertens_photography.blegrill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;



public class MainActivity extends ActionBarActivity {

    private final static String TAG = MainActivity.class.getSimpleName();

    private Button connectBtn = null;
    private TextView rssiValue = null;
    private TextView Sensor1TempView = null;
    private TextView Sensor2TempView = null;
    private ToggleButton sensor1ToggleButton = null;
    private ToggleButton sensor2ToggleButton = null;
    private EditText upperLimitEditText = null;
    private EditText lowerLimitEditText = null;
    private Button setSettingsBtn = null;
    private Spinner spnSensorSelected = null;

    private BluetoothGattCharacteristic characteristicTx = null;
    private BluetoothGattCharacteristic characteristicTemp1Meas = null;
    private RBLService mBluetoothLeService;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice = null;
    private String mDeviceAddress;

    private boolean flag = true;
    private boolean connState = false;
    private boolean scanFlag = false;
    private boolean initialSettingDone = false;

    private Ringtone alertRingTone;
    private BLEGrillDevice bleDevice;
    private byte[] data = new byte[3];
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 2000;

    final private static char[] hexArray = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((RBLService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (RBLService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Toast.makeText(getApplicationContext(), "Disconnected",
                        Toast.LENGTH_SHORT).show();
                setButtonDisable();
            } else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                Toast.makeText(getApplicationContext(), "Connected",
                        Toast.LENGTH_SHORT).show();

                getGattService();
            } else if (RBLService.ACTION_DATA_AVAILABLE.equals(action)) {
                processReceivedData(intent);

            } else if (RBLService.ACTION_GATT_RSSI.equals(action)) {
                // RSSI Wert
                displayData(intent.getStringExtra(RBLService.UART_DATA));
            }
        }
    };

    private void displayData(String data) {
        if (data != null) {
            rssiValue.setText(data);
        }
    }

    private void processReceivedData(Intent intent){
        /* Temperature Sensor 1 */
        if(intent.hasExtra(RBLService.SENSOR1_TEMPERATURE))
        {
            data = intent.getByteArrayExtra(RBLService.SENSOR1_TEMPERATURE);
            TemperatureSensor sensor = bleDevice.getTemperatureSensor(0);
            sensor.setTempMeasureData(data);
        }
        else if(intent.hasExtra(RBLService.SENSOR1_CONFIG))
        {
            data = intent.getByteArrayExtra(RBLService.SENSOR1_CONFIG);
            TemperatureSensor sensor = bleDevice.getTemperatureSensor(0);
            sensor.setConfig(data);
        }
        else if(intent.hasExtra(RBLService.SENSOR1_ALARM_SETTINGS))
        {
            data = intent.getByteArrayExtra(RBLService.SENSOR1_ALARM_SETTINGS);
            TemperatureSensor sensor = bleDevice.getTemperatureSensor(0);
            sensor.setAlarmSettings(data);
        }
        /* Temperature Sensor 2 */
        else if(intent.hasExtra(RBLService.SENSOR2_TEMPERATURE))
        {
            data = intent.getByteArrayExtra(RBLService.SENSOR2_TEMPERATURE);
            TemperatureSensor sensor = bleDevice.getTemperatureSensor(1);
            sensor.setTempMeasureData(data);
        }
        else if(intent.hasExtra(RBLService.SENSOR2_CONFIG))
        {
            data = intent.getByteArrayExtra(RBLService.SENSOR2_CONFIG);
            TemperatureSensor sensor = bleDevice.getTemperatureSensor(1);
            sensor.setConfig(data);
        }
        else if(intent.hasExtra(RBLService.SENSOR2_ALARM_SETTINGS))
        {
            data = intent.getByteArrayExtra(RBLService.SENSOR2_ALARM_SETTINGS);
            TemperatureSensor sensor = bleDevice.getTemperatureSensor(1);
            sensor.setAlarmSettings(data);
        }
        /* Temperature Sensor 3 */
        else if(intent.hasExtra(RBLService.SENSOR3_TEMPERATURE))
        {
            data = intent.getByteArrayExtra(RBLService.SENSOR3_TEMPERATURE);
            TemperatureSensor sensor = bleDevice.getTemperatureSensor(2);
            sensor.setTempMeasureData(data);
        }
        else if(intent.hasExtra(RBLService.SENSOR3_CONFIG))
        {
            data = intent.getByteArrayExtra(RBLService.SENSOR3_CONFIG);
            TemperatureSensor sensor = bleDevice.getTemperatureSensor(2);
            sensor.setConfig(data);
        }
        else if(intent.hasExtra(RBLService.SENSOR3_ALARM_SETTINGS))
        {
            data = intent.getByteArrayExtra(RBLService.SENSOR3_ALARM_SETTINGS);
            TemperatureSensor sensor = bleDevice.getTemperatureSensor(2);
            sensor.setAlarmSettings(data);
        }
        /* Temperature Sensor 4 */
        else if(intent.hasExtra(RBLService.SENSOR4_TEMPERATURE))
        {
            data = intent.getByteArrayExtra(RBLService.SENSOR4_TEMPERATURE);
            TemperatureSensor sensor = bleDevice.getTemperatureSensor(3);
            sensor.setTempMeasureData(data);
        }
        else if(intent.hasExtra(RBLService.SENSOR4_CONFIG))
        {
            data = intent.getByteArrayExtra(RBLService.SENSOR4_CONFIG);
            TemperatureSensor sensor = bleDevice.getTemperatureSensor(3);
            sensor.setConfig(data);
        }
        else if(intent.hasExtra(RBLService.SENSOR4_ALARM_SETTINGS))
        {
            data = intent.getByteArrayExtra(RBLService.SENSOR3_ALARM_SETTINGS);
            TemperatureSensor sensor = bleDevice.getTemperatureSensor(3);
            sensor.setAlarmSettings(data);
        }
        /* Device Settings */
        else if(intent.hasExtra(RBLService.HARDWARE_STATES))
        {
            data = intent.getByteArrayExtra(RBLService.HARDWARE_STATES);
            bleDevice.setHardwareStates(data);
        }
        else if(intent.hasExtra(RBLService.MEASURE_INTERVALL))
        {
            data = intent.getByteArrayExtra(RBLService.MEASURE_INTERVALL);
            bleDevice.setMeasureIntervall(data);
        }
        else if(intent.hasExtra(RBLService.NOTIFY_INTERVALL))
        {
            data = intent.getByteArrayExtra(RBLService.NOTIFY_INTERVALL);
            bleDevice.setNotifyIntervall(data);
        }
        /* Alarm Notifier */
        else if(intent.hasExtra(RBLService.ALARM_INDICATION))
        {
            data = intent.getByteArrayExtra(RBLService.ALARM_INDICATION);
            if(data[0] == 1) {
                setNotification("Da gibt es einen Alarm!");
                startAlarmSound();
                setAlertDialog();
            }
        }

        updateView();
    }

    private void setNotification(String text)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.barbecue_ldpi);
        mBuilder.setContentTitle("Temp Alarm");
        mBuilder.setContentText("Temperature is to high: "+ text + "°C");
        // Remove notification when user click on it
        mBuilder.setAutoCancel(true);

        //Creates an explicit intent for an Activity in your App
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that start the Activiy to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resulPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        mBuilder.setContentIntent(resulPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        int mId = 1;
        mNotificationManager.notify(mId, mBuilder.build());
    };

    private void setAlertDialog(){
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.alert_dialog_message)
                .setTitle(R.string.alert_dialog_title);

        // Add the buttons
        builder.setPositiveButton(R.string.alert_dialog_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                stopAlarmSound();
                sendQuittAlarm();
            }
        });
        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startAlarmSound(){
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if(alert == null){
            // alert is null, using backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // I can't see this ever being null (as always have a default notification)
            // but just incase
            if(alert == null) {
                // alert backup is null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }

        if(alertRingTone == null)
            alertRingTone = RingtoneManager.getRingtone(getApplicationContext(), alert);

        alertRingTone.play();
    }

    private void stopAlarmSound(){
        if(alertRingTone != null) {
            alertRingTone.stop();
        }
    }

    private void setButtonEnable() {
        flag = true;
        connState = true;

        sensor1ToggleButton.setActivated(true);
        sensor2ToggleButton.setActivated(true);
        sensor1ToggleButton.setChecked(true);
        sensor2ToggleButton.setChecked(true);
        setSettingsBtn.setActivated(true);
        connectBtn.setText("Disconnect");
    }

    private void setButtonDisable() {
        flag = false;
        connState = false;

        resetViewValues();
        connectBtn.setText("Connect");
        initialSettingDone = false;
    }

    private void startReadRssi() {
        new Thread() {
            public void run() {

                while (flag) {
                    mBluetoothLeService.readRssi();
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
        }.start();
    }

    private void enableNotifications() {
        if (mBluetoothLeService == null)
            return;

        /* Temperature Measurement 1 */
        BluetoothGattService gattService = mBluetoothLeService
                .getSupportedGattService(RBLService.UUID_BLE_GRILL_TEMPSENSOR1_SERVICE);
        if(gattService != null){

            BluetoothGattCharacteristic characteristic = gattService
                    .getCharacteristic(RBLService.UUID_BLE_GRILL_TEMPSENSOR1_MEASURE);
            mBluetoothLeService.setCharacteristicNotification(characteristic,
                    true);
        }

        /* Temperature Measurement 2 */
        gattService = mBluetoothLeService
                .getSupportedGattService(RBLService.UUID_BLE_GRILL_TEMPSENSOR2_SERVICE);
        if(gattService != null){

            BluetoothGattCharacteristic characteristic = gattService
                    .getCharacteristic(RBLService.UUID_BLE_GRILL_TEMPSENSOR2_MEASURE);
            mBluetoothLeService.setCharacteristicNotification(characteristic,
                    true);
        }

        /* Temperature Measurement 3 */
        gattService = mBluetoothLeService
                .getSupportedGattService(RBLService.UUID_BLE_GRILL_TEMPSENSOR3_SERVICE);
        if(gattService != null){

            BluetoothGattCharacteristic characteristic = gattService
                    .getCharacteristic(RBLService.UUID_BLE_GRILL_TEMPSENSOR3_MEASURE);
            mBluetoothLeService.setCharacteristicNotification(characteristic,
                    true);
        }

        /* Temperature Measurement 4 */
        gattService = mBluetoothLeService
                .getSupportedGattService(RBLService.UUID_BLE_GRILL_TEMPSENSOR4_SERVICE);
        if(gattService != null){

            BluetoothGattCharacteristic characteristic = gattService
                    .getCharacteristic(RBLService.UUID_BLE_GRILL_TEMPSENSOR4_MEASURE);
            mBluetoothLeService.setCharacteristicNotification(characteristic,
                    true);
        }

        /* Alarm Notifier */
        gattService = mBluetoothLeService
                .getSupportedGattService(RBLService.UUID_BLE_GRILL_ALARM_NOTIFIER_SERVICE);
        if(gattService != null){

            BluetoothGattCharacteristic characteristic = gattService
                    .getCharacteristic(RBLService.UUID_BLE_GRILL_ALARM_INDICATION);
            mBluetoothLeService.setCharacteristicNotification(characteristic,
                    true);
        }
    }

    private void readAllCharacteristics() {
        if (mBluetoothLeService == null)
            return;

        /* Temperature Measurement 1 */
        BluetoothGattService gattService = mBluetoothLeService
                .getSupportedGattService(RBLService.UUID_BLE_GRILL_TEMPSENSOR1_SERVICE);
        if(gattService != null){

            BluetoothGattCharacteristic characteristic = gattService
                    .getCharacteristic(RBLService.UUID_BLE_GRILL_TEMPSENSOR1_MEASURE);
            mBluetoothLeService.readCharacteristic(characteristic);
            characteristic = gattService
                    .getCharacteristic(RBLService.UUID_BLE_GRILL_TEMPSENSOR1_CONFIG);
            mBluetoothLeService.readCharacteristic(characteristic);
            characteristic = gattService
                    .getCharacteristic(RBLService.UUID_BLE_GRILL_TEMPSENSOR1_ALARM);
            mBluetoothLeService.readCharacteristic(characteristic);
        }

        /* Temperature Measurement 2 */
        gattService = mBluetoothLeService
                .getSupportedGattService(RBLService.UUID_BLE_GRILL_TEMPSENSOR2_SERVICE);
        if(gattService != null){

            BluetoothGattCharacteristic characteristic = gattService
                    .getCharacteristic(RBLService.UUID_BLE_GRILL_TEMPSENSOR2_MEASURE);
            mBluetoothLeService.readCharacteristic(characteristic);
            characteristic = gattService
                    .getCharacteristic(RBLService.UUID_BLE_GRILL_TEMPSENSOR2_CONFIG);
            mBluetoothLeService.readCharacteristic(characteristic);
            characteristic = gattService
                    .getCharacteristic(RBLService.UUID_BLE_GRILL_TEMPSENSOR2_ALARM);
            mBluetoothLeService.readCharacteristic(characteristic);
        }

        /* Temperature Measurement 3 */
        gattService = mBluetoothLeService
                .getSupportedGattService(RBLService.UUID_BLE_GRILL_TEMPSENSOR3_SERVICE);
        if(gattService != null){

            BluetoothGattCharacteristic characteristic = gattService
                    .getCharacteristic(RBLService.UUID_BLE_GRILL_TEMPSENSOR3_MEASURE);
            mBluetoothLeService.readCharacteristic(characteristic);
            characteristic = gattService
                    .getCharacteristic(RBLService.UUID_BLE_GRILL_TEMPSENSOR3_CONFIG);
            mBluetoothLeService.readCharacteristic(characteristic);
            characteristic = gattService
                    .getCharacteristic(RBLService.UUID_BLE_GRILL_TEMPSENSOR3_ALARM);
            mBluetoothLeService.readCharacteristic(characteristic);
        }

        /* Temperature Measurement 4 */
        gattService = mBluetoothLeService
                .getSupportedGattService(RBLService.UUID_BLE_GRILL_TEMPSENSOR4_SERVICE);
        if(gattService != null){

            BluetoothGattCharacteristic characteristic = gattService
                    .getCharacteristic(RBLService.UUID_BLE_GRILL_TEMPSENSOR4_MEASURE);
            mBluetoothLeService.readCharacteristic(characteristic);
            characteristic = gattService
                    .getCharacteristic(RBLService.UUID_BLE_GRILL_TEMPSENSOR4_CONFIG);
            mBluetoothLeService.readCharacteristic(characteristic);
            characteristic = gattService
                    .getCharacteristic(RBLService.UUID_BLE_GRILL_TEMPSENSOR4_ALARM);
            mBluetoothLeService.readCharacteristic(characteristic);
        }

        /* Device Settings */
        gattService = mBluetoothLeService
                .getSupportedGattService(RBLService.UUID_BLE_GRILL_DEVICE_SETTING_SERVICE);
        if(gattService != null){

            BluetoothGattCharacteristic characteristic = gattService
                    .getCharacteristic(RBLService.UUID_BLE_GRILL_HARDWARE_STATES);
            mBluetoothLeService.readCharacteristic(characteristic);
            characteristic = gattService
                    .getCharacteristic(RBLService.UUID_BLE_GRILL_MEASURE_INTERVALL);
            mBluetoothLeService.readCharacteristic(characteristic);
            characteristic = gattService
                    .getCharacteristic(RBLService.UUID_BLE_GRILL_NOTIFY_INTERVALL);
            mBluetoothLeService.readCharacteristic(characteristic);
        }

        /* Alarm Notifier */
        gattService = mBluetoothLeService
                .getSupportedGattService(RBLService.UUID_BLE_GRILL_ALARM_INDICATION);
        if(gattService != null){

            BluetoothGattCharacteristic characteristic = gattService
                    .getCharacteristic(RBLService.UUID_BLE_GRILL_ALARM_INDICATION);
            mBluetoothLeService.readCharacteristic(characteristic);
        }
    }

    private void getGattService() {
        setButtonEnable();
        startReadRssi();

        enableNotifications();
        readAllCharacteristics();
        /* UART */
//        characteristicTx = gattService
//                .getCharacteristic(RBLService.UUID_BLE_Grill_UART_TX);
//
//        BluetoothGattCharacteristic characteristicRx = gattService
//                .getCharacteristic(RBLService.UUID_BLE_Grill_UART_RX);
//        mBluetoothLeService.setCharacteristicNotification(characteristicRx,
//                true);
//        mBluetoothLeService.readCharacteristic(characteristicRx);


    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(RBLService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(RBLService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(RBLService.ACTION_GATT_RSSI);

        return intentFilter;
    }

    private void scanLeDevice() {
        new Thread() {

            @Override
            public void run() {
                mBluetoothAdapter.startLeScan(mLeScanCallback);

                try {
                    Thread.sleep(SCAN_PERIOD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }.start();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             final byte[] scanRecord) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    byte[] serviceUuidBytes = new byte[16];
//                    String serviceUuid = "";
//                    for (int i = 32, j = 0; i >= 17; i--, j++) {
//                        serviceUuidBytes[j] = scanRecord[i];
//                    }
                    /* Check if the device has the BLEGrill appearance packet */
                    RBLAdvertisePacket appearancePacket = RBLAdvertisePacket.bleGrill_Appearance_Packet();
                    ArrayList<RBLAdvertisePacket> listPackets = RBLAdvertisePacket.convertBufferToAdvPackets(scanRecord);
//                    serviceUuid = bytesToHex(serviceUuidBytes);
                    if (listPackets.contains(appearancePacket)) {
                        mDevice = device;
                    }
                   // }

//                    if (stringToUuidString(serviceUuid).equals(
//                            RBLGattAttributes.BLE_SHIELD_SERVICE
//                                    .toUpperCase(Locale.ENGLISH))) {
//                        mDevice = device;
//                    }
                }
            });
        }
    };

    private String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private String stringToUuidString(String uuid) {
        StringBuffer newString = new StringBuffer();
        newString.append(uuid.toUpperCase(Locale.ENGLISH).substring(0, 8));
        newString.append("-");
        newString.append(uuid.toUpperCase(Locale.ENGLISH).substring(8, 12));
        newString.append("-");
        newString.append(uuid.toUpperCase(Locale.ENGLISH).substring(12, 16));
        newString.append("-");
        newString.append(uuid.toUpperCase(Locale.ENGLISH).substring(16, 20));
        newString.append("-");
        newString.append(uuid.toUpperCase(Locale.ENGLISH).substring(20, 32));

        return newString.toString();
    }


    private void updateView(){
        if(mBluetoothLeService.isCharacteristicReadQueueEmpty()) {
            /* TODO: This is only done, because there is no View to do settings */
            if(!initialSettingDone){
                bleDevice.setNotifyIntervall(2);

                BluetoothGattService gattService = mBluetoothLeService
                        .getSupportedGattService(RBLService.UUID_BLE_GRILL_DEVICE_SETTING_SERVICE);
                if(gattService != null){

                    BluetoothGattCharacteristic characteristic = gattService
                            .getCharacteristic(RBLService.UUID_BLE_GRILL_NOTIFY_INTERVALL);
                    characteristic.setValue(bleDevice.getNotifyIntervallAsByte());
                    mBluetoothLeService.writeCharacteristic(characteristic);

                    initialSettingDone = true;
                }

            }
            TemperatureSensor sensor1 = bleDevice.getTemperatureSensor(0);
            TemperatureSensor sensor2 = bleDevice.getTemperatureSensor(1);

            if (sensor1.isTemperatureValid()) {
                Sensor1TempView.setText(sensor1.getTemperatureAsString());
                sensor1ToggleButton.setActivated(true);
            }
            if (sensor2.isTemperatureValid()) {
                Sensor2TempView.setText(sensor2.getTemperatureAsString());
                sensor2ToggleButton.setActivated(true);
            }
        }
    }

    private void resetViewValues(){
        Sensor1TempView.setText("--,--");
        Sensor2TempView.setText("--,--");
        rssiValue.setText("--");
        sensor1ToggleButton.setActivated(false);
        sensor2ToggleButton.setActivated(false);
        setSettingsBtn.setActivated(false);
    }

    private void sendQuittAlarm(){
        /* Send quitt Alarm */
        BluetoothGattService gattService = mBluetoothLeService
                .getSupportedGattService(RBLService.UUID_BLE_GRILL_ALARM_NOTIFIER_SERVICE);
        if(gattService != null) {
            byte[] data = new byte[1];
            data[0]= 0x01;
            BluetoothGattCharacteristic characteristic = gattService
                    .getCharacteristic(RBLService.UUID_BLE_GRILL_QUITT_ALARM);
            characteristic.setValue(data);
            mBluetoothLeService.writeCharacteristic(characteristic);
        }
    }

    private void setSettings(){
        String lowerLimit = lowerLimitEditText.getText().toString();
        String upperLimit = upperLimitEditText.getText().toString();


        /* Convert text do double and set high temperature border */
        if(upperLimit != null && !upperLimit.isEmpty())
        {
            double temp = Double.parseDouble(upperLimit);
            for(int i=0; i<bleDevice.getNbOfTempSensors(); i++)
            {
                bleDevice.getTemperatureSensor(i).setHighBorderTemperature(temp);
            }
        }

        /* Convert text do double and set low temperature border */
        if(lowerLimit != null && !lowerLimit.isEmpty())
        {
            double temp = Double.parseDouble(lowerLimit);
            for(int i=0; i<bleDevice.getNbOfTempSensors(); i++)
            {
                bleDevice.getTemperatureSensor(i).setLowBorderTemperature(temp);
            }
        }

        if(spnSensorSelected.getSelectedItemId() == 0) {
            /* Set Temperature Sensor1 Alarm settings */
            BluetoothGattService gattService = mBluetoothLeService
                    .getSupportedGattService(RBLService.UUID_BLE_GRILL_TEMPSENSOR1_SERVICE);
            if (gattService != null) {

                TemperatureSensor sensor = bleDevice.getTemperatureSensor(0);
                BluetoothGattCharacteristic characteristic = gattService
                        .getCharacteristic(RBLService.UUID_BLE_GRILL_TEMPSENSOR1_ALARM);
                characteristic.setValue(sensor.getAlarmSettings());
                mBluetoothLeService.writeCharacteristic(characteristic);

                initialSettingDone = true;
            }
        }
        else if(spnSensorSelected.getSelectedItemId() == 1) {
            /* Set Temperature Sensor2 Alarm settings */
            BluetoothGattService gattService = mBluetoothLeService
                    .getSupportedGattService(RBLService.UUID_BLE_GRILL_TEMPSENSOR2_SERVICE);
            if (gattService != null) {

                TemperatureSensor sensor = bleDevice.getTemperatureSensor(1);
                BluetoothGattCharacteristic characteristic = gattService
                        .getCharacteristic(RBLService.UUID_BLE_GRILL_TEMPSENSOR2_ALARM);
                characteristic.setValue(sensor.getAlarmSettings());
                mBluetoothLeService.writeCharacteristic(characteristic);

                initialSettingDone = true;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bleDevice = new BLEGrillDevice();

        rssiValue = (TextView) findViewById(R.id.rssiValue);

        Sensor1TempView = (TextView) findViewById(R.id.textViewSensor1Temp);
        Sensor2TempView = (TextView) findViewById(R.id.textViewSensor2Temp);
        lowerLimitEditText = (EditText) findViewById(R.id.lowerLimitEditText);
        upperLimitEditText = (EditText) findViewById(R.id.upperLimitEditText);
        setSettingsBtn = (Button) findViewById(R.id.settingsButton);
        setSettingsBtn.setActivated(false);
        setSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSettings();
            }
        });
        sensor1ToggleButton = (ToggleButton) findViewById(R.id.toggleButtonEnableSensor1);
        sensor1ToggleButton.setActivated(false);
        sensor1ToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connState == true) {
                    TemperatureSensor sensor = bleDevice.getTemperatureSensor(0);
                    if (sensor1ToggleButton.isChecked()) {
                        sensor.setEnabled(true);
                    } else {
                        sensor.setEnabled(false);
                    }

                    BluetoothGattService gattService = mBluetoothLeService
                            .getSupportedGattService(RBLService.UUID_BLE_GRILL_TEMPSENSOR1_SERVICE);
                    if (gattService != null) {

                        BluetoothGattCharacteristic characteristic = gattService
                                .getCharacteristic(RBLService.UUID_BLE_GRILL_TEMPSENSOR1_CONFIG);

                        characteristic.setValue(sensor.getConfig());
                        mBluetoothLeService.writeCharacteristic(characteristic);
                    }
                }
            }
        });

        sensor2ToggleButton = (ToggleButton) findViewById(R.id.toggleButtonEnableSensor2);
        sensor2ToggleButton.setActivated(false);
        sensor2ToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connState == true) {
                    TemperatureSensor sensor = bleDevice.getTemperatureSensor(1);
                    if (sensor2ToggleButton.isChecked()) {
                        sensor.setEnabled(true);
                    } else {
                        sensor.setEnabled(false);
                    }

                    BluetoothGattService gattService = mBluetoothLeService
                            .getSupportedGattService(RBLService.UUID_BLE_GRILL_TEMPSENSOR2_SERVICE);
                    if (gattService != null) {

                        BluetoothGattCharacteristic characteristic = gattService
                                .getCharacteristic(RBLService.UUID_BLE_GRILL_TEMPSENSOR2_CONFIG);

                        characteristic.setValue(sensor.getConfig());
                        mBluetoothLeService.writeCharacteristic(characteristic);
                    }
                }
            }
        });

        spnSensorSelected = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a defualt spinner layout
        ArrayAdapter<CharSequence> spnAdapter = ArrayAdapter.createFromResource(this,
                R.array.sensor_nbs, android.R.layout.simple_spinner_item);
        //Specify the layout tu use when the list of choices appiers
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // apply the adapter to the spinner
        spnSensorSelected.setAdapter(spnAdapter);

        connectBtn = (Button) findViewById(R.id.connect);
        connectBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (scanFlag == false) {
                    scanLeDevice();

                    Timer mTimer = new Timer();
                    mTimer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            if (mDevice != null) {
                                mDeviceAddress = mDevice.getAddress();
                                mBluetoothLeService.connect(mDeviceAddress);
                                scanFlag = true;
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast toast = Toast
                                                .makeText(
                                                        MainActivity.this,
                                                        "Couldn't search BLEGrill device!",
                                                        Toast.LENGTH_SHORT);
                                        toast.setGravity(0, 0, Gravity.CENTER);
                                        toast.show();
                                    }
                                });
                            }
                        }
                    }, SCAN_PERIOD);
                }

                System.out.println(connState);
                if (connState == false) {
                    mBluetoothLeService.connect(mDeviceAddress);
                } else {
                    mBluetoothLeService.disconnect();
                    mBluetoothLeService.close();
                    setButtonDisable();
                }
            }
        });


        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT)
                    .show();
            finish();
        }

        final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        Intent gattServiceIntent = new Intent(MainActivity.this,
                RBLService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        resetViewValues();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onStop() {
        super.onStop();

        flag = false;

        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mServiceConnection != null)
            unbindService(mServiceConnection);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT
                && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
