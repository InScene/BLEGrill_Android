package com.mertens_photography.blegrill;

import java.text.DecimalFormat;

/**
 * Created by InScene on 17.05.2015.
 */
public class TemperatureSensor {

    private double temperature;
    private double highBorderTemperature;
    private double lowBorderTemperature;
    private boolean isEnabled;
    private SENSOR_TYPE type;
    private ALARM_TYPE alarmType;
    private ALARM_STATE currAlarm;
    public final int undefTemperature = 0xFFFF;

    public enum ALARM_TYPE{
        NO_ALARM,
        HIGH_ALARM,
        LOW_ALARM,
        HIGH_LOW_ALARM
    }

    public enum ALARM_STATE{
        NO_ALARM,
        HIGH_ALARM,
        LOW_ALARM,
        NO_SENSOR_ALARM,
        MEASURE_ERROR_ALARM
    }

    public enum SENSOR_TYPE{
        ACURITE,
        FANTAST,
        ROSENSTEIN,
        MAVERICK
    }

    public TemperatureSensor( final boolean isEnabled, final SENSOR_TYPE aType){
        this.isEnabled = isEnabled;
        this.type = aType;
        this.temperature = undefTemperature;
        this.highBorderTemperature = 380;
        this.lowBorderTemperature = 0;
        this.alarmType = ALARM_TYPE.HIGH_LOW_ALARM;
        this.currAlarm = ALARM_STATE.NO_ALARM;
    }

    public TemperatureSensor(final byte[] data, final boolean isEnabled, final SENSOR_TYPE aType){
        this( isEnabled, aType);
        this.setTempMeasureData(data);
    }

    public void setConfig(final byte[] data)
    {
        if( (data == null) || (data.length != 2))
            return;

        if(data[0] == 1)
            this.isEnabled = true;
        else
            this.isEnabled = false;

        this.type = convertByteToSensorTyp(data[1]);
    }

    public void setAlarmSettings(final byte[] data)
    {
        if( (data == null) || (data.length != 6))
            return;

        this.currAlarm = convertByteToAlarmState(data[0]);
        this.alarmType = convertByteToAlarmType(data[1]);

        this.highBorderTemperature =
                convertTwoBytesIntoTemperatureValue(data[2], data[3]);
        this.lowBorderTemperature =
                convertTwoBytesIntoTemperatureValue(data[4], data[5]);
    }

    public void setEnabled(final boolean isEnabled) { this.isEnabled = isEnabled; }

    public void setTempMeasureData(byte[] data)
    {
        if( (data == null) || (data.length != 4))
            return;

        this.temperature = convertTwoBytesIntoTemperatureValue(data[1], data[2]);
    }

    public void setHighBorderTemperature(double temperature) { this.highBorderTemperature = temperature; }

    public void setLowBorderTemperature(double temperature) { this.lowBorderTemperature = temperature; }

    public double getTemperature()
    {
        return this.temperature;
    }

    public byte[] getConfig()
    {
        byte[] data = new byte[2];

        if(this.isEnabled)
            data[0]=1;
        else
            data[0]=0;

        data[1] = convertSensorTypToByte(this.type);

        return data;
    }

    public byte[] getAlarmSettings()
    {
        byte[] data = new byte[6];
        byte[] dataHighBorder = convertTemperatureValueIntoTwoBytes(this.highBorderTemperature);
        byte[] dataLowBorder = convertTemperatureValueIntoTwoBytes(this.lowBorderTemperature);

        data[0] = convertAlarmStateToByte(this.currAlarm);
        data[1] = convertAlarmTypeToByte(this.alarmType);
        data[2] = dataHighBorder[0];
        data[3] = dataHighBorder[1];
        data[4] = dataLowBorder[0];
        data[5] = dataLowBorder[1];

        return data;
    }

    public boolean isEnabled() { return this.isEnabled; }

    public boolean isTemperatureValid() { if(this.temperature != this.undefTemperature) return true; else return false; }

    public SENSOR_TYPE getType() { return this.type; }

    public String getTemperatureAsString()
    {
        DecimalFormat f = new DecimalFormat("#0.00");
        return f.format(this.temperature) + "";
    }

    private SENSOR_TYPE convertByteToSensorTyp(final byte aByte)
    {
        SENSOR_TYPE type;
        switch (aByte)
        {
            case 0:
                type = SENSOR_TYPE.ACURITE;
                break;
            case 1:
                type = SENSOR_TYPE.FANTAST;
                break;
            case 2:
                type = SENSOR_TYPE.ROSENSTEIN;
                break;
            case 3:
                type = SENSOR_TYPE.MAVERICK;
                break;
            default:
                type = SENSOR_TYPE.ACURITE;
                break;
        }

        return type;
    }

    private byte convertSensorTypToByte(final SENSOR_TYPE type)
    {
        byte aByte = 0;
        switch (type)
        {
            case ACURITE:
                aByte=0;
                break;
            case FANTAST:
                aByte=1;
                break;
            case ROSENSTEIN:
                aByte=2;
                break;
            case MAVERICK:
                aByte=3;
                break;
        }

        return aByte;
    }

    private ALARM_TYPE convertByteToAlarmType(final byte aByte)
    {
        ALARM_TYPE type;
        switch (aByte)
        {
            case 0:
                type = ALARM_TYPE.NO_ALARM;
                break;
            case 1:
                type = ALARM_TYPE.HIGH_ALARM;
                break;
            case 2:
                type = ALARM_TYPE.LOW_ALARM;
                break;
            case 3:
                type = ALARM_TYPE.HIGH_LOW_ALARM;
                break;
            default:
                type = ALARM_TYPE.NO_ALARM;
                break;
        }

        return type;
    }

    private byte convertAlarmTypeToByte(final ALARM_TYPE aType)
    {
        byte aByte = 0;
        switch (aType)
        {
            case NO_ALARM:
                aByte = 0;
                break;
            case HIGH_ALARM:
                aByte = 1;
                break;
            case LOW_ALARM:
                aByte = 2;
                break;
            case HIGH_LOW_ALARM:
                aByte = 3;
                break;
        }

        return aByte;
    }

    private ALARM_STATE convertByteToAlarmState(final byte aByte)
    {
        ALARM_STATE state;
        switch (aByte)
        {
            case 0:
                state = ALARM_STATE.NO_ALARM;
                break;
            case 1:
                state = ALARM_STATE.HIGH_ALARM;
                break;
            case 2:
                state = ALARM_STATE.LOW_ALARM;
                break;
            case 3:
                state = ALARM_STATE.NO_SENSOR_ALARM;
                break;
            case 4:
                state = ALARM_STATE.MEASURE_ERROR_ALARM;
                break;
            default:
                state = ALARM_STATE.NO_ALARM;
                break;
        }

        return state;
    }

    private byte convertAlarmStateToByte(final ALARM_STATE aState)
    {
        byte aByte = 0;
        switch (aState)
        {
            case NO_ALARM:
                aByte = 0;
                break;
            case HIGH_ALARM:
                aByte = 1;
                break;
            case LOW_ALARM:
                aByte = 2;
                break;
            case NO_SENSOR_ALARM:
                aByte = 3;
                break;
            case MEASURE_ERROR_ALARM:
                aByte = 4;
                break;
        }

        return aByte;
    }

    private double convertTwoBytesIntoTemperatureValue(final byte lowByte, final byte highByte)
    {
        /* Because byte are signed use & 0xFF to make it unsigned */
        int value = (lowByte & 0xFF);
        value += ((highByte & 0xFF) << 8);

        return ((double)value) /100;
    }

    private byte[] convertTemperatureValueIntoTwoBytes(final double temperature)
    {
        byte[] data = new byte[2];

        /* Shift comma to get int value */
        int tempVal = (int)(temperature * 100);
        data[0] = (byte)tempVal;
        data[1] = (byte)(tempVal >> 8);

        return data;
    }
}
