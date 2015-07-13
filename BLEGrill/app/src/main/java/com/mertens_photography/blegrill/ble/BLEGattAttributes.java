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

package com.mertens_photography.blegrill.ble;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for
 * demonstration purposes.
 */
public class BLEGattAttributes {
	private static HashMap<String, String> attributes = new HashMap<String, String>();
	public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
	public static String BLE_SHIELD_TX = "713d0003-503e-4c75-ba94-3148f18d941e";
	public static String BLE_SHIELD_RX = "713d0002-503e-4c75-ba94-3148f18d941e";
	public static String BLE_SHIELD_SERVICE = "713d0000-503e-4c75-ba94-3148f18d941e";

	/* UART Service and Characteristic */
	public static String BLE_Grill_Uart_Service = "713d0000-503e-4c75-ba94-3148f18d941e";
	public static String BLE_Grill_Uart_TX = "713d0003-503e-4c75-ba94-3148f18d941e";
	public static String BLE_Grill_Uart_RX = "713d0002-503e-4c75-ba94-3148f18d941e";

	/* Device_Settings Service and Characteristic */
	public static String BLE_Grill_DeviceSettings_Service = "00000020-0000-1000-8000-00805F9B34FB";
	public static String BLE_Grill_DeviceSettings_HardwarStates = "00000021-0000-1000-8000-00805F9B34FB";
	public static String BLE_Grill_DeviceSettings_MeasureIntervall = "00002a21-0000-1000-8000-00805F9B34FB";
	public static String BLE_Grill_DeviceSettings_NotifyIntervall = "00000023-0000-1000-8000-00805F9B34FB";

	/* Alarm_Notifier Service and Characteristic */
	public static String BLE_Grill_AlarmNotifier_Service = "00000050-0000-1000-8000-00805F9B34FB";
	public static String BLE_Grill_AlarmNotifier_Indication = "00000051-0000-1000-8000-00805F9B34FB";
	public static String BLE_Grill_AlarmNotifier_QuitAlarm = "00000052-0000-1000-8000-00805F9B34FB";

	/* Temperature_Sensor1 Service and Characteristic */
	public static String BLE_Grill_TempSensor1_Service = "00000001-0000-1000-8000-00805F9B34FB";
	public static String BLE_Grill_TempSensor1_Temp_Measurement = "00002a1c-0000-1000-8000-00805F9B34FB";
	public static String BLE_Grill_TempSensor1_Sensor_Config = "00000010-0000-1000-8000-00805F9B34FB";
	public static String BLE_Grill_TempSensor1_Alarm_Settings = "00000041-0000-1000-8000-00805F9B34FB";

	/* Temperature_Sensor2 Service and Characteristic */
	public static String BLE_Grill_TempSensor2_Service = "00000002-0000-1000-8000-00805F9B34FB";
	public static String BLE_Grill_TempSensor2_Temp_Measurement = "00002a1c-0000-1000-8000-00805F9B34FB";
	public static String BLE_Grill_TempSensor2_Sensor_Config = "00000010-0000-1000-8000-00805F9B34FB";
	public static String BLE_Grill_TempSensor2_Alarm_Settings = "00000041-0000-1000-8000-00805F9B34FB";

	/* Temperature_Sensor3 Service and Characteristic */
	public static String BLE_Grill_TempSensor3_Service = "00000003-0000-1000-8000-00805F9B34FB";
	public static String BLE_Grill_TempSensor3_Temp_Measurement = "00002a1c-0000-1000-8000-00805F9B34FB";
	public static String BLE_Grill_TempSensor3_Sensor_Config = "00000010-0000-1000-8000-00805F9B34FB";
	public static String BLE_Grill_TempSensor3_Alarm_Settings = "00000041-0000-1000-8000-00805F9B34FB";

	/* Temperature_Sensor4 Service and Characteristic */
	public static String BLE_Grill_TempSensor4_Service = "00000004-0000-1000-8000-00805F9B34FB";
	public static String BLE_Grill_TempSensor4_Temp_Measurement = "00002a1c-0000-1000-8000-00805F9B34FB";
	public static String BLE_Grill_TempSensor4_Sensor_Config = "00000010-0000-1000-8000-00805F9B34FB";
	public static String BLE_Grill_TempSensor4_Alarm_Settings = "00000041-0000-1000-8000-00805F9B34FB";

	/* Temperatures_Broadcast Service and Characteristic */
	public static String BLE_Grill_TempBroadcast_Service = "00000005-0000-1000-8000-00805F9B34FB";
	public static String BLE_Grill_TempBroadcast_AllTemp = "00001005-0000-1000-8000-00805F9B34FB";

	static {
		/* BLE Uart Services. */
		attributes.put(BLE_Grill_Uart_Service, "UART over BLE");
		/* BLE Uart Characteristics. */
		attributes.put(BLE_Grill_Uart_TX, "UART RX");
		attributes.put(BLE_Grill_Uart_RX, "UART TX");

		/* BLE DeviceSettings Services. */
		attributes.put(BLE_Grill_DeviceSettings_Service, "Device Settings");
		/* BLE DeviceSettings Characteristics. */
		attributes.put(BLE_Grill_DeviceSettings_HardwarStates, "Hardware States");
		attributes.put(BLE_Grill_DeviceSettings_MeasureIntervall, "Measurement Interval");
		attributes.put(BLE_Grill_DeviceSettings_NotifyIntervall, "Notify Interval");

		/* BLE TemperatureSensor1 Services. */
		attributes.put(BLE_Grill_TempSensor1_Service, "Temperature Sensor1");
		/* BLE TemperatureSensor1 Characteristics. */
		attributes.put(BLE_Grill_TempSensor1_Temp_Measurement, "Temperature Measurement");
		attributes.put(BLE_Grill_TempSensor1_Sensor_Config, "Sensor Config");
		attributes.put(BLE_Grill_TempSensor1_Alarm_Settings, "Alarm Settings");
	}

	public static String lookup(String uuid, String defaultName) {
		String name = attributes.get(uuid);
		return name == null ? defaultName : name;
	}
}
