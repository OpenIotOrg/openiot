package org.openiot.gsn.wrappers.tinyos;

import java.util.Arrays;

public class SensorScope2Packet {
    public byte[] bytes;
    long timestamp;

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SensorScope2Packet that = (SensorScope2Packet) o;

        if (timestamp != that.timestamp) return false;
        if (!Arrays.equals(bytes, that.bytes)) return false;

        return true;
    }

    public int hashCode() {
        int result = bytes != null ? Arrays.hashCode(bytes) : 0;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }

    public String toString() {

        StringBuilder hex_sb = new StringBuilder();

        hex_sb.append(timestamp).append(" : ");

        for (int i = 0; i < bytes.length; i++) {
            hex_sb.append(String.format("%02x", bytes[i])).append(" ");
        }

        return hex_sb.toString() + " (" + String.format("%2d", bytes.length) + ")";
    }

    public SensorScope2Packet(long timestamp, byte[] bytes) {
        this.timestamp = timestamp;
        this.bytes = bytes;
    }
}
