/**
*    Copyright (c) 2011-2014, OpenIoT
*   
*    This file is part of OpenIoT.
*
*    OpenIoT is free software: you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation, version 3 of the License.
*
*    OpenIoT is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
*
*     Contact: OpenIoT mailto: info@openiot.eu
 * @author Sofiane Sarni
*/

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
