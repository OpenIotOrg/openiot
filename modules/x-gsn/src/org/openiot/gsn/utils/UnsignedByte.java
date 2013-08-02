package org.openiot.gsn.utils;

public class UnsignedByte {
    private byte byteValue;
    private int intValue;

    public UnsignedByte() {
        byteValue = 0;
        intValue = 0;
    }

    public UnsignedByte(byte b) {
        byteValue = b;
        intValue = (int) b & 0xff;
    }

    public UnsignedByte(int i) {
        i = i & 0xff;
        byteValue = (byte) i;
        intValue = i;
    }

    public UnsignedByte setValue(byte b) {
        byteValue = b;
        intValue = (int) b & 0xff;
        return this;
    }

    public UnsignedByte setValue(int i) {
        i = i & 0xff;
        byteValue = (byte) i;
        intValue = i;
        return this;
    }

    public int getInt() {
        return intValue;
    }

    public byte getByte() {
        return byteValue;
    }

    public String toString() {
        return "(byte:" + getByte() + ", int:" + getInt() + ")";
    }

    public static byte[] UnsignedByteArray2ByteArray(UnsignedByte[] uba) {
        int length = uba.length;
        byte[] ba = new byte[length];
        for (int i = 0; i < length; i++)
            ba[i] = uba[i].getByte();
        return ba;
    }

    public static UnsignedByte[] ByteArray2UnsignedByteArray(byte[] ba) {
        int length = ba.length;
        UnsignedByte[] uba = new UnsignedByte[length];
        for (int i = 0; i < length; i++)
            uba[i].setValue(ba[i]);
        return uba;
    }
}