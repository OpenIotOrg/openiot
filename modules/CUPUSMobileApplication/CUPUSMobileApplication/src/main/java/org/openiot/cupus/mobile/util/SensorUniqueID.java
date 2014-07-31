package org.openiot.cupus.mobile.util;

import android.hardware.Sensor;
import android.os.Build;

import java.text.DecimalFormat;

/**
 * Created by Kristijan on 15.01.14..
 */
public class SensorUniqueID {

    public static String deviceID = "" +
            Build.BOARD.length()%10+ Build.BRAND.length()%10 +
            Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
            Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
            Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
            Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
            Build.TAGS.length()%10 + Build.TYPE.length()%10 +
            Build.USER.length()%10 ; //13 digits

    public static String getSensorID(Sensor sensor) {
        DecimalFormat formatter = new DecimalFormat("00");
        String sensorID = formatter.format(sensor.getType());
        return sensorID + deviceID;
    }
}
