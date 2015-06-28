package com.mertens_photography.blegrill;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Created by InScene on 09.06.2015.
 */

public class RBLAdvertisePacket {

    static public RBLAdvertisePacket bleGrill_Appearance_Packet(){
        byte[] data = new byte[] {(byte)0xF6, (byte)0x9A};
        return new RBLAdvertisePacket((byte)0x19, data);
    }
    static public ArrayList<RBLAdvertisePacket> convertBufferToAdvPackets(final byte[] buffer)
    {
        ArrayList<RBLAdvertisePacket> listAdvPackets = new ArrayList<RBLAdvertisePacket>();
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
                RBLAdvertisePacket packet = new RBLAdvertisePacket();
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

    public RBLAdvertisePacket(){}

    private RBLAdvertisePacket(final byte type, final byte[] data){
        this.type = type;
        if(data != null)
            this.data = Arrays.copyOf(data,data.length );
    }

    public boolean equals(Object o)
    {
        if(o==null || !(o instanceof RBLAdvertisePacket))
            return false;

        RBLAdvertisePacket packet = (RBLAdvertisePacket)o;

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

    private byte type;
    private byte[] data;

}
