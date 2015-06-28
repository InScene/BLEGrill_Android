package com.mertens_photography.blegrill;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by InScene on 12.06.2015.
 */
public class BLEGrillDevice {

    private boolean buzzerEnabled;
    private boolean buzzerState;
    private boolean alarmLedState;
    private boolean statusLedState;
    private int measureIntervall;
    private int notifyIntervall;
    private boolean alarmState;

    private List<TemperatureSensor> temperatureSensors;

    public BLEGrillDevice(){
        this.buzzerEnabled = true;
        this.buzzerState = true;
        this.alarmLedState = true;
        this.statusLedState = true;
        this.measureIntervall = 2;
        this.notifyIntervall = 10;
        this.alarmState = false;

        temperatureSensors = new ArrayList<TemperatureSensor>();

        createTempSensors(4);
    }

    public boolean isBuzzerEnabled() { return buzzerEnabled; }

    public boolean getBuzzerState() { return buzzerState; }

    public boolean getAlarmLedState() { return alarmLedState; }

    public boolean getSTatusLedState() { return statusLedState; }

    public int getMeasureIntervall() { return measureIntervall; }

    public int getNotifyIntervall() { return notifyIntervall; }

    public void setBuzzerEnabled( final boolean isEnabled ) { buzzerEnabled = isEnabled; }

    public void setBuzzerState( final boolean state ) { buzzerState = state; }

    public void setAlarmLedState( final boolean state ) { alarmLedState = state; }

    public void setStatusLedState( final boolean state ) { statusLedState = state; }

    public void setMeasureIntervall(final int intervall) { measureIntervall = intervall; }

    public void setNotifyIntervall(final int intervall) { notifyIntervall = intervall; }

    public void setHardwareStates(final byte[] data)
    {
        if( (data == null) || (data.length != 1) )
            return;

        if( isBitSet(data[0], 0) )
            this.buzzerEnabled = true;
        else
            this.buzzerEnabled = false;

        if( isBitSet(data[0], 1) )
            this.buzzerState = true;
        else
            this.buzzerState = false;

        if( isBitSet(data[0], 2) )
            this.alarmLedState = true;
        else
            this.alarmLedState = false;

        if( isBitSet(data[0], 3) )
            this.statusLedState = true;
        else
            this.statusLedState = false;
    }

    public byte getHardwareStates()
    {
        byte data = 0;

        if(this.buzzerEnabled)
            data |= 1 << 0;

        if(this.buzzerState)
            data |= 1 << 1;

        if(this.alarmLedState)
            data |= 1 << 2;

        if(this.statusLedState)
            data |= 1 << 3;

        return data;
    }

    public void setMeasureIntervall(final byte[] data)
    {
        if( (data == null) || (data.length != 2) )
            return;

        this.measureIntervall = convertTwoBytesIntoInt(data[0], data[1]);
    }

    public byte[] getMeasureIntervallAsByte()
    {
        return convertIntIntoTwoBytes(this.measureIntervall);
    }

    public void setNotifyIntervall(final byte[] data)
    {
        if( (data == null) || (data.length != 2) )
            return;

        this.notifyIntervall = convertTwoBytesIntoInt(data[0], data[1]);
    }

    public byte[] getNotifyIntervallAsByte()
    {
        return convertIntIntoTwoBytes(this.notifyIntervall);
    }

    public void setAlarmState(byte[] data)
    {
        if( (data == null) || (data.length != 1) )
            return;

        if(data[0] == 1)
            this.alarmState = true;
        else
            this.alarmState = false;
    }

    public boolean isAlarmActive() { return this.alarmState; }

    public int getNbOfTempSensors() { return this.temperatureSensors.size(); }

    public TemperatureSensor getTemperatureSensor(final int sensorNb)
    {
        if(sensorNb < this.temperatureSensors.size()){
            return this.temperatureSensors.get(sensorNb);
        }

        return null;
    }


    private void createTempSensors(final int nbSensors)
    {
        for(int i=0; i<nbSensors; i++) {
            TemperatureSensor tempSensor = new TemperatureSensor(true, TemperatureSensor.SENSOR_TYPE.MAVERICK);
            this.temperatureSensors.add(tempSensor);
        }
    }

    private boolean isBitSet(final byte data, final int bitPos)
    {
        return ((data >> bitPos & 1) == 1);
    }

    private int convertTwoBytesIntoInt(final byte lowByte, final byte highByte)
    {
        /* Because byte are signed use & 0xFF to make it unsigned */
        int value = (lowByte & 0xFF);
        value += ((highByte & 0xFF) << 8);

        return value;
    }

    private byte[] convertIntIntoTwoBytes(final int value)
    {
        byte[] data = new byte[2];

        data[0] = (byte)value;
        data[1] = (byte)(value >> 8);

        return data;
    }
}
