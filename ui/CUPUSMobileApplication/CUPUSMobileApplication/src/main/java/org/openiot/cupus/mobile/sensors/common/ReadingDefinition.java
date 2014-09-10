package org.openiot.cupus.mobile.sensors.common;

/**
 * Created by Kristijan on 27.01.14..
 */
public class ReadingDefinition {

    private String name;

    public ReadingDefinition(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReadingDefinition that = (ReadingDefinition) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
