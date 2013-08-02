package org.openiot.gsn.metadata.LSM;

public class LSMFieldMetaData {
    public String getGsnFieldName() {
        return gsnFieldName;
    }

    public void setGsnFieldName(String gsnFieldName) {
        this.gsnFieldName = gsnFieldName;
    }

    private String gsnFieldName;
    private String lsmPropertyName;

    @Override
    public String toString() {
        return "LSMFieldMetaData{" +
                "gsnFieldName='" + gsnFieldName + '\'' +
                ", lsmPropertyName='" + lsmPropertyName + '\'' +
                ", unit='" + lsmUnit + '\'' +
                '}';
    }

    private String lsmUnit;

    public String getLsmPropertyName() {
        return lsmPropertyName;
    }

    public void setLsmPropertyName(String propertyName) {
        this.lsmPropertyName = propertyName;
    }

    public String getLsmUnit() {
        return lsmUnit;
    }

    public void setLsmUnit(String lsmUnit) {
        this.lsmUnit = lsmUnit;
    }
}
