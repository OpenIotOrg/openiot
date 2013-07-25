package org.openiot.gsn.beans.windowing;

public class WindowingUtil {

    public static long GCD(long a, long b) {
        if (a == 0 || b == 0) {
            return 0;
        }
        return GCDHelper(a, b);
    }

    private static long GCDHelper(long a, long b) {
        if (b == 0) {
            return a;
        }
        return GCDHelper(b, a % b);
    }
}
