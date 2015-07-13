package com.mertens_photography.blegrill.ble;

import java.util.Arrays;
import java.util.ArrayList;

/**
 * Created by InScene on 09.06.2015.
 */

public class BLEAdvertisePacket {

    static public BLEAdvertisePacket bleGrill_Appearance_Packet(){
        byte[] data = new byte[] {(byte)0xF6, (byte)0x9A};
        return new BLEAdvertisePacket((byte)0x19, data);
    }

    final static public byte BLE_DATA_TYPE_SERVICE_DATA = (byte)0x16;
    final static public byte[] BLE_TEMPERATURE_SERVICE_ID = new byte[] {(byte)0x05, (byte)0x00};

    final static private byte BLE_ADVERTISE_TEMP1_LOW = (byte)0x03;
    final static private byte BLE_ADVERTISE_TEMP1_HIGH = (byte)0x04;
    final static private byte BLE_ADVERTISE_TEMP2_LOW = (byte)0x05;
    final static private byte BLE_ADVERTISE_TEMP2_HIGH = (byte)0x06;
    final static private byte BLE_ADVERTISE_TEMP3_LOW = (byte)0x07;
    final static private byte BLE_ADVERTISE_TEMP3_HIGH = (byte)0x08;
    final static private byte BLE_ADVERTISE_TEMP4_LOW = (byte)0x09;
    final static private byte BLE_ADVERTISE_TEMP4_HIGH = (byte)0x0A;

    static public ArrayList<BLEAdvertisePacket> convertBufferToAdvPackets(final byte[] buffer)
    {
        ArrayList<BLEAdvertisePacket> listAdvPackets = new ArrayList<BLEAdvertisePacket>();
        int size;
        byte[] dataBuffer;

        for(int i=0; i <buffer.length;) {
            size = buffer[i];

            /* Size ==0 indicates the end, although buffer end is not reached */
            if(size == 0)
                break;

            /* Get data for this advertise packet */
            dataBuffer = getAdBufferData(buffer, i);

            if(dataBuffer != null) {
                BLEAdvertisePacket packet = new BLEAdvertisePacket();
                /* When data set was valid, place it in list */
                if (packet.setAdvertiseData(dataBuffer)) {
                    listAdvPackets.add(packet);
                }
            }

            /* Set position for next adPacket */
            i =i+size+1;
        }

        return listAdvPackets;
    }

    static private byte[] getAdBufferData(final byte[] buffer, int startPos)
    {
        byte[] adBufferData = null;

        final int size = buffer[startPos];

        /* Buffer is not always complete filled with data, so size 0 is possible */
        if(size > 0) {
            final int endPos = startPos + size + 1;

            /* Copy data only if in buffer bounds */
            if (buffer.length > endPos) {
                adBufferData = Arrays.copyOfRange(buffer, startPos, endPos);
            }
        }

        return adBufferData;
    }

    public BLEAdvertisePacket(){}

    private BLEAdvertisePacket(final byte type, final byte[] data){
        this.type = type;
        if(data != null)
            this.data = Arrays.copyOf(data,data.length );
    }

    public boolean equals(Object o)
    {
        if(o==null || !(o instanceof BLEAdvertisePacket))
            return false;

        BLEAdvertisePacket packet = (BLEAdvertisePacket)o;

        if( (packet.getType() == this.type) &&
            (Arrays.equals(packet.getData(), this.data )) )
            return true;

        return false;
    }

    public boolean setAdvertiseData(final byte[] buffer)
    {
        boolean isValid = false;
        final int dataStartPos = 2;

        if( (buffer != null) &&
            (buffer.length > dataStartPos) )
        {
            final int size = buffer[0];
            final int dataSize = size - dataStartPos + 1;
            this.type = 0;

            /* Size must have dataStartPos bytes to be valid */
            if ( (dataSize > 0) &&
                 (dataSize < buffer.length ) ){
                this.type = buffer[1];
                this.data = new byte[dataSize];

                /* copy buffer into data */
                for (int i = dataStartPos; i <= size; i++) {
                    int posDataArr = i - dataStartPos;
                    this.data[posDataArr] = buffer[i];
                    isValid = true;
                }
            }
        }

        return isValid;
    }

    public byte getType()
    {
        return type;
    }

    public byte[] getData()
    {
        return data;
    }

    public boolean isTemperatureData(){
        boolean isTempData = false;

        if( (this.type == BLE_DATA_TYPE_SERVICE_DATA) &&
                (data.length > 2) ){
            if( (data[0] == BLE_TEMPERATURE_SERVICE_ID[0]) &&
                (data[1] == BLE_TEMPERATURE_SERVICE_ID[1]) ){
                isTempData = true;
            }
        }

        return isTempData;
    }

    public double getTemperatureData(byte sensorId){
        double temp = 0xFFFF;

        /* Converting only possible if enough data */
        if(data.length >= BLE_ADVERTISE_TEMP4_HIGH ) {
            switch (sensorId) {
                case 0:
                    temp = convertTwoBytesIntoTemperatureValue(data[BLE_ADVERTISE_TEMP1_LOW],
                            data[BLE_ADVERTISE_TEMP1_HIGH]);
                    break;
                case 1:
                    temp = convertTwoBytesIntoTemperatureValue(data[BLE_ADVERTISE_TEMP2_LOW],
                            data[BLE_ADVERTISE_TEMP2_HIGH]);
                    break;
                case 2:
                    temp = convertTwoBytesIntoTemperatureValue(data[BLE_ADVERTISE_TEMP3_LOW],
                            data[BLE_ADVERTISE_TEMP3_HIGH]);
                    break;
                case 3:
                    temp = convertTwoBytesIntoTemperatureValue(data[BLE_ADVERTISE_TEMP4_LOW],
                            data[BLE_ADVERTISE_TEMP4_HIGH]);
                    break;
            }
        }

        return temp;
    }

    private double convertTwoBytesIntoTemperatureValue(final byte lowByte, final byte highByte)
    {
        /* Because byte are signed use & 0xFF to make it unsigned */
        int value = (lowByte & 0xFF);
        value += ((highByte & 0xFF) << 8);

        return ((double)value) /100;
    }


    private byte type;
    private byte[] data;

}
