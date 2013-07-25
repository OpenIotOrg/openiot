/*
* DEPRECATED don't Use
* TODO: cleanup
* */

package org.openiot.gsn.metadata.LSM.depecated;

import java.util.Date;

public class SensorObservation {
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    String unit;
    String propertyName;
    double value;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    Date time;


    public String toString() {
        return new StringBuilder("SensorObservation{")
                .append("unit='")
                .append(unit)
                .append('\'')
                .append(", propertyName='")
                .append(propertyName)
                .append('\'')
                .append(", value=")
                .append(value)
                .append('}').toString();
    }
}

