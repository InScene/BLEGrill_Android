/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mertens_photography.blegrill;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given Bluetooth LE device.
 */
public class RBLService extends Service {
	private final static String TAG = RBLService.class.getSimpleName();

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;
	private BluetoothGatt mBluetoothGatt;

	private Queue<BluetoothGattDescriptor> descriptorWriteQueue = new LinkedList<BluetoothGattDescriptor>();
	private Queue<BluetoothGattCharacteristic> characteristicReadQueue = new LinkedList<BluetoothGattCharacteristic>();
	private Queue<BluetoothGattCharacteristic> characteristicWriteQueue = new LinkedList<BluetoothGattCharacteristic>();


	public final static String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_GATT_RSSI = "ACTION_GATT_RSSI";
	public final static String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";
	public final static String UART_DATA = "UART_DATA";
	public final static String SENSOR1_TEMPERATURE = "SENS1_TEMP";
	public final static String SENSOR1_CONFIG = "SENS1_CONFIG";
	public final static String SENSOR1_ALARM_SETTINGS = "SENS1_ALARM_SET";
	public final static String SENSOR2_TEMPERATURE = "SENS2_TEMP";
	public final static String SENSOR2_CONFIG = "SENS2_CONFIG";
	public final static String SENSOR2_ALARM_SETTINGS = "SENS2_ALARM_SET";
	public final static String SENSOR3_TEMPERATURE = "SENS3_TEMP";
	public final static String SENSOR3_CONFIG = "SENS3_CONFIG";
	public final static String SENSOR3_ALARM_SETTINGS = "SENS3_ALARM_SET";
	public final static String SENSOR4_TEMPERATURE = "SENS4_TEMP";
	public final static String SENSOR4_CONFIG = "SENS4_CONFIG";
	public final static String SENSOR4_ALARM_SETTINGS = "SENS4_ALARM_SET";
	public final static String HARDWARE_STATES = "HARDWARE_STATES";
	public final static String MEASURE_INTERVALL = "MEASURE_INTV";
	public final static String NOTIFY_INTERVALL = "NOTIFY_INTV";
	public final static String ALARM_INDICATION = "ALARM_INDIC";
	public final static String QUITT_ALARM = "QUITT_ALARM";

	/* UART */
	public final static UUID UUID_BLE_GRILL_UART_SERVICE = UUID
			.fromString(RBLGattAttributes.BLE_Grill_Uart_Service);
	public final static UUID UUID_BLE_GRILL_UART_TX = UUID
			.fromString(RBLGattAttributes.BLE_Grill_Uart_TX);
	public final static UUID UUID_BLE_GRILL_UART_RX = UUID
			.fromString(RBLGattAttributes.BLE_Grill_Uart_RX);

	/* Temperature Sensor 1 */
	public final static UUID UUID_BLE_GRILL_TEMPSENSOR1_SERVICE = UUID
			.fromString(RBLGattAttributes.BLE_Grill_TempSensor1_Service);
	public final static UUID UUID_BLE_GRILL_TEMPSENSOR1_ALARM = UUID
			.fromString(RBLGattAttributes.BLE_Grill_TempSensor1_Alarm_Settings);
	public final static UUID UUID_BLE_GRILL_TEMPSENSOR1_CONFIG = UUID
			.fromString(RBLGattAttributes.BLE_Grill_TempSensor1_Sensor_Config);
	public final static UUID UUID_BLE_GRILL_TEMPSENSOR1_MEASURE = UUID
			.fromString(RBLGattAttributes.BLE_Grill_TempSensor1_Temp_Measurement);

		/* Temperature Sensor 2 */
	public final static UUID UUID_BLE_GRILL_TEMPSENSOR2_SERVICE = UUID
			.fromString(RBLGattAttributes.BLE_Grill_TempSensor2_Service);
	public final static UUID UUID_BLE_GRILL_TEMPSENSOR2_ALARM = UUID
			.fromString(RBLGattAttributes.BLE_Grill_TempSensor2_Alarm_Settings);
	public final static UUID UUID_BLE_GRILL_TEMPSENSOR2_CONFIG = UUID
			.fromString(RBLGattAttributes.BLE_Grill_TempSensor2_Sensor_Config);
	public final static UUID UUID_BLE_GRILL_TEMPSENSOR2_MEASURE = UUID
			.fromString(RBLGattAttributes.BLE_Grill_TempSensor2_Temp_Measurement);

	/* Temperature Sensor 3 */
	public final static UUID UUID_BLE_GRILL_TEMPSENSOR3_SERVICE = UUID
			.fromString(RBLGattAttributes.BLE_Grill_TempSensor3_Service);
	public final static UUID UUID_BLE_GRILL_TEMPSENSOR3_ALARM = UUID
			.fromString(RBLGattAttributes.BLE_Grill_TempSensor3_Alarm_Settings);
	public final static UUID UUID_BLE_GRILL_TEMPSENSOR3_CONFIG = UUID
			.fromString(RBLGattAttributes.BLE_Grill_TempSensor3_Sensor_Config);
	public final static UUID UUID_BLE_GRILL_TEMPSENSOR3_MEASURE = UUID
			.fromString(RBLGattAttributes.BLE_Grill_TempSensor3_Temp_Measurement);

	/* Temperature Sensor 4 */
	public final static UUID UUID_BLE_GRILL_TEMPSENSOR4_SERVICE = UUID
			.fromString(RBLGattAttributes.BLE_Grill_TempSensor4_Service);
	public final static UUID UUID_BLE_GRILL_TEMPSENSOR4_ALARM = UUID
			.fromString(RBLGattAttributes.BLE_Grill_TempSensor4_Alarm_Settings);
	public final static UUID UUID_BLE_GRILL_TEMPSENSOR4_CONFIG = UUID
			.fromString(RBLGattAttributes.BLE_Grill_TempSensor4_Sensor_Config);
	public final static UUID UUID_BLE_GRILL_TEMPSENSOR4_MEASURE = UUID
			.fromString(RBLGattAttributes.BLE_Grill_TempSensor4_Temp_Measurement);

	/* Device Settings */
	public final static UUID UUID_BLE_GRILL_DEVICE_SETTING_SERVICE = UUID
			.fromString(RBLGattAttributes.BLE_Grill_DeviceSettings_Service);
	public final static UUID UUID_BLE_GRILL_HARDWARE_STATES = UUID
			.fromString(RBLGattAttributes.BLE_Grill_DeviceSettings_HardwarStates);
	public final static UUID UUID_BLE_GRILL_MEASURE_INTERVALL = UUID
			.fromString(RBLGattAttributes.BLE_Grill_DeviceSettings_MeasureIntervall);
	public final static UUID UUID_BLE_GRILL_NOTIFY_INTERVALL = UUID
			.fromString(RBLGattAttributes.BLE_Grill_DeviceSettings_NotifyIntervall);

	/* Alarm Notifier */
	public final static UUID UUID_BLE_GRILL_ALARM_NOTIFIER_SERVICE = UUID
			.fromString(RBLGattAttributes.BLE_Grill_AlarmNotifier_Service);
	public final static UUID UUID_BLE_GRILL_ALARM_INDICATION = UUID
			.fromString(RBLGattAttributes.BLE_Grill_AlarmNotifier_Indication);
	public final static UUID UUID_BLE_GRILL_QUITT_ALARM = UUID
			.fromString(RBLGattAttributes.BLE_Grill_AlarmNotifier_QuitAlarm);

	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			String intentAction;

			if (newState == BluetoothProfile.STATE_CONNECTED) {
				intentAction = ACTION_GATT_CONNECTED;
				broadcastUpdate(intentAction);
				Log.i(TAG, "Connected to GATT server.");
				// Attempts to discover services after successful connection.
				Log.i(TAG, "Attempting to start service discovery:"
						+ mBluetoothGatt.discoverServices());
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				intentAction = ACTION_GATT_DISCONNECTED;
				Log.i(TAG, "Disconnected from GATT server.");
				broadcastUpdate(intentAction);
			}
		}

		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_GATT_RSSI, rssi);
			} else {
				Log.w(TAG, "onReadRemoteRssi received: " + status);
			}
		};

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
			} else {
				Log.w(TAG, "onServicesDiscovered received: " + status);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {

			characteristicReadQueue.remove();
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			}
			else{
				Log.d(TAG, "onCharacteristicRead error: " + status);
			}

			if(characteristicReadQueue.size() > 0)
				mBluetoothGatt.readCharacteristic(characteristicReadQueue.element());

			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
		}

		public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.d(TAG, "Callback: Wrote GATT Descriptor successfully.");
			}
			else{
				Log.d(TAG, "Callback: Error writing GATT Descriptor: "+ status);
			}
			descriptorWriteQueue.remove();  //pop the item that we just finishing writing
			//if there is more to write, do it!
			if(descriptorWriteQueue.size() > 0)
				mBluetoothGatt.writeDescriptor(descriptorWriteQueue.element());
			else if(characteristicReadQueue.size() > 0)
				mBluetoothGatt.readCharacteristic(characteristicReadQueue.element());
			else if(characteristicWriteQueue.size() > 0)
				mBluetoothGatt.writeCharacteristic(characteristicWriteQueue.element());
		}

		public void onCharacteristicWrite(BluetoothGatt gatt,
										  BluetoothGattCharacteristic characteristic,
										  int status){
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.d(TAG, "Callback: Wrote Characteristic successfully.");
			}
			else{
				Log.d(TAG, "Callback: Error writing Characteristic: "+ status);
			}
			characteristicWriteQueue.remove();  //pop the item that we just finishing writing
			//if there is more to write, do it!
			if(characteristicWriteQueue.size() > 0)
				mBluetoothGatt.writeCharacteristic(characteristicWriteQueue.element());
		}
	};

	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action, int rssi) {
		final Intent intent = new Intent(action);
		intent.putExtra(UART_DATA, String.valueOf(rssi));
		sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action,
			final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);

		/* Identify characteristic and set data with characteristic ident string */

		if (UUID_BLE_GRILL_UART_SERVICE.equals(characteristic.getService().getUuid())) {
			final byte[] bytes = characteristic.getValue();
			if(UUID_BLE_GRILL_UART_RX.equals(characteristic.getUuid())){
				intent.putExtra(UART_DATA, bytes);
			}
		}
		else if (UUID_BLE_GRILL_TEMPSENSOR1_SERVICE.equals(characteristic.getService().getUuid())) {
			final byte[] bytes = characteristic.getValue();
			if(UUID_BLE_GRILL_TEMPSENSOR1_MEASURE.equals(characteristic.getUuid())){
				intent.putExtra(SENSOR1_TEMPERATURE, bytes);
			}
			else if(UUID_BLE_GRILL_TEMPSENSOR1_CONFIG.equals(characteristic.getUuid())){
				intent.putExtra(SENSOR1_CONFIG, bytes);
			}
			else if(UUID_BLE_GRILL_TEMPSENSOR1_ALARM.equals(characteristic.getUuid())){
				intent.putExtra(SENSOR1_ALARM_SETTINGS, bytes);
			}
		}
		else if (UUID_BLE_GRILL_TEMPSENSOR2_SERVICE.equals(characteristic.getService().getUuid())) {
			final byte[] bytes = characteristic.getValue();
			if(UUID_BLE_GRILL_TEMPSENSOR2_MEASURE.equals(characteristic.getUuid())){
				intent.putExtra(SENSOR2_TEMPERATURE, bytes);
			}
			else if(UUID_BLE_GRILL_TEMPSENSOR2_CONFIG.equals(characteristic.getUuid())){
				intent.putExtra(SENSOR2_CONFIG, bytes);
			}
			else if(UUID_BLE_GRILL_TEMPSENSOR2_ALARM.equals(characteristic.getUuid())){
				intent.putExtra(SENSOR2_ALARM_SETTINGS, bytes);
			}
		}
		else if (UUID_BLE_GRILL_TEMPSENSOR3_SERVICE.equals(characteristic.getService().getUuid())) {
			final byte[] bytes = characteristic.getValue();
			if(UUID_BLE_GRILL_TEMPSENSOR3_MEASURE.equals(characteristic.getUuid())){
				intent.putExtra(SENSOR3_TEMPERATURE, bytes);
			}
			else if(UUID_BLE_GRILL_TEMPSENSOR3_CONFIG.equals(characteristic.getUuid())){
				intent.putExtra(SENSOR3_CONFIG, bytes);
			}
			else if(UUID_BLE_GRILL_TEMPSENSOR3_ALARM.equals(characteristic.getUuid())){
				intent.putExtra(SENSOR3_ALARM_SETTINGS, bytes);
			}
		}
		else if (UUID_BLE_GRILL_TEMPSENSOR4_SERVICE.equals(characteristic.getService().getUuid())) {
			final byte[] bytes = characteristic.getValue();
			if(UUID_BLE_GRILL_TEMPSENSOR4_MEASURE.equals(characteristic.getUuid())){
				intent.putExtra(SENSOR4_TEMPERATURE, bytes);
			}
			else if(UUID_BLE_GRILL_TEMPSENSOR4_CONFIG.equals(characteristic.getUuid())){
				intent.putExtra(SENSOR4_CONFIG, bytes);
			}
			else if(UUID_BLE_GRILL_TEMPSENSOR4_ALARM.equals(characteristic.getUuid())){
				intent.putExtra(SENSOR4_ALARM_SETTINGS, bytes);
			}
		}
		else if (UUID_BLE_GRILL_DEVICE_SETTING_SERVICE.equals(characteristic.getService().getUuid())) {
			final byte[] bytes = characteristic.getValue();
			if(UUID_BLE_GRILL_HARDWARE_STATES.equals(characteristic.getUuid())){
				intent.putExtra(HARDWARE_STATES, bytes);
			}
			else if(UUID_BLE_GRILL_MEASURE_INTERVALL.equals(characteristic.getUuid())){
				intent.putExtra(MEASURE_INTERVALL, bytes);
			}
			else if(UUID_BLE_GRILL_NOTIFY_INTERVALL.equals(characteristic.getUuid())){
				intent.putExtra(NOTIFY_INTERVALL, bytes);
			}
		}
		else if (UUID_BLE_GRILL_ALARM_NOTIFIER_SERVICE.equals(characteristic.getService().getUuid())) {
			final byte[] bytes = characteristic.getValue();
			if(UUID_BLE_GRILL_ALARM_INDICATION.equals(characteristic.getUuid())){
				intent.putExtra(ALARM_INDICATION, bytes);
			}
		}

		sendBroadcast(intent);
	}

	public class LocalBinder extends Binder {
		RBLService getService() {
			return RBLService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// After using a given device, you should make sure that
		// BluetoothGatt.close() is called
		// such that resources are cleaned up properly. In this particular
		// example, close() is
		// invoked when the UI is disconnected from the Service.
		close();
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 * 
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter
		// through
		// BluetoothManager.
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 * 
	 * @param address
	 *            The device address of the destination device.
	 * 
	 * @return Return true if the connection is initiated successfully. The
	 *         connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	public boolean connect(final String address) {
		if (mBluetoothAdapter == null || address == null) {
			Log.w(TAG,
					"BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		// Previously connected device. Try to reconnect.
		if (mBluetoothDeviceAddress != null
				&& address.equals(mBluetoothDeviceAddress)
				&& mBluetoothGatt != null) {
			Log.d(TAG,
					"Trying to use an existing mBluetoothGatt for connection.");
			if (mBluetoothGatt.connect()) {
				return true;
			} else {
				return false;
			}
		}

		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(address);
		if (device == null) {
			Log.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		// We want to directly connect to the device, so we are setting the
		// autoConnect
		// parameter to false.
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		Log.d(TAG, "Trying to create a new connection.");
		mBluetoothDeviceAddress = address;

		return true;
	}

	/**
	 * Disconnects an existing connection or cancel a pending connection. The
	 * disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.disconnect();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 * 
	 * @param characteristic
	 *            The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}

		//put the characteristic into the read queue
		characteristicReadQueue.add(characteristic);
		//if there is only 1 item in the queue, then read it.  If more than 1, we handle asynchronously in the callback above
		//GIVE PRECEDENCE to descriptor writes.  They must all finish first.
		if((characteristicReadQueue.size() == 1) && (descriptorWriteQueue.size() == 0))
			mBluetoothGatt.readCharacteristic(characteristic);
	}

	public boolean isCharacteristicReadQueueEmpty(){
		return characteristicReadQueue.isEmpty();
	}

	public void readRssi() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}

		mBluetoothGatt.readRemoteRssi();
	}

	public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}

		//put the characteristic into the write queue
		characteristicWriteQueue.add(characteristic);
		//if there is only 1 item in the queue, then write it.  If more than 1, we handle asynchronously in the callback above
		//GIVE PRECEDENCE to descriptor writes.  They must all finish first.
		if((characteristicWriteQueue.size() == 1) && (descriptorWriteQueue.size() == 0))
			mBluetoothGatt.writeCharacteristic(characteristic);

	}

	/**
	 * Enables or disables notification on a give characteristic.
	 * 
	 * @param characteristic
	 *            Characteristic to act on.
	 * @param enabled
	 *            If true, enable notification. False otherwise.
	 */
	public void setCharacteristicNotification(
			BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		if(!mBluetoothGatt.setCharacteristicNotification(characteristic, enabled)){
			Log.w(TAG, "Notification for characteristic not set. UUID:" + characteristic.getUuid());
		}


		BluetoothGattDescriptor descriptor = characteristic
				.getDescriptor(UUID
						.fromString(RBLGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
		descriptor
				.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

		//put the descriptor into the write queue
		descriptorWriteQueue.add(descriptor);
		//if there is only 1 item in the queue, then write it.  If more than 1, we handle asynchronously in the callback above
		if(descriptorWriteQueue.size() == 1){
			mBluetoothGatt.writeDescriptor(descriptor);
		}

	}

	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null)
			return null;

		return mBluetoothGatt.getServices();
	}

	public BluetoothGattService getSupportedGattService(UUID uuid) {
		if (mBluetoothGatt == null)
			return null;

		return mBluetoothGatt.getService(uuid);
	}
}
