package org.openiot.gsn.metadata.LSM;

import org.openiot.gsn.utils.PropertiesReader;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class LSMSensorMetaData {

    private static final transient Logger logger = Logger.getLogger(LSMSensorMetaData.class);

    public String getSensorName() {
        return sensorName;
    }

    public String getAuthor() {
        return author;
    }

    public String getSensorType() {
        return sensorType;
    }

    public String getInformation() {
        return information;
    }

    public String getSourceType() {
        return sourceType;
    }

    private String sensorName;
    private String author;
    private String sensorType;
    private String information;
    private String sourceType;
    private String source;
    private boolean registeredToLSM;

    public String[] getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(String[] fieldNames) {
        this.fieldNames = fieldNames;
    }

    private String fieldNames[];
    private Map<String, LSMFieldMetaData> fields = new HashMap<String, LSMFieldMetaData>();


    public Map<String, LSMFieldMetaData> getFields() {
        return fields;
    }

    public void setFields(Map<String, LSMFieldMetaData> fields) {
        this.fields = fields;
    }

    public boolean isRegisteredToLSM() {
        return registeredToLSM;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    public String getSensorID() {
        return sensorID;
    }

    public void setSensorID(String sensorID) {
        this.sensorID = sensorID;
    }

    String sensorID;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LSMSensorMetaData{")
                .append("\nsensorName  =").append(sensorName)
                .append("\nauthor      =").append(author)
                .append("\nsensorType  =").append(sensorType)
                .append("\ninformation =").append(information)
                .append("\nsourceType  =").append(sourceType)
                .append("\nsource      =").append(source)
                .append("\nsensorID    =").append(sensorID)
                .append("\nfields =>\n");
        for (int i=0;i<fieldNames.length;i++) {
            sb.append("\t").append(fieldNames[i]).append(" : ").append(fields.get(fieldNames[i])).append("\n");
        }
        return sb.toString();
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public boolean initFromConfigFile(String fileName) {

        try {
            //TODO: optimization: read properties file once, then scan for each field
            this.setSensorName(PropertiesReader.readProperty(fileName, "sensorName"));
            this.setAuthor(PropertiesReader.readProperty(fileName, "author"));
            this.setInformation(PropertiesReader.readProperty(fileName, "information"));
            this.setSensorType(PropertiesReader.readProperty(fileName, "sensorType"));
            this.setSourceType(PropertiesReader.readProperty(fileName, "sourceType"));
            this.setSource(PropertiesReader.readProperty(fileName, "source"));
            this.setSensorID(PropertiesReader.readProperty(fileName, "sensorID"));
            String registeredToLSMString = PropertiesReader.readProperty(fileName, "registered");
            if (registeredToLSMString.equalsIgnoreCase("true"))
                registeredToLSM = true;
            else
                registeredToLSM = false;
            String listOfFieldsString = PropertiesReader.readProperty(fileName, "fields");
            fieldNames = listOfFieldsString.trim().split(",");
            for (int i = 0; i < fieldNames.length; i++) {
                String fieldName = fieldNames[i];
                logger.info(i + " : " + fieldName);
                LSMFieldMetaData lsmFieldMetaData = new LSMFieldMetaData();
                lsmFieldMetaData.setGsnFieldName(fieldName);
                lsmFieldMetaData.setLsmPropertyName(PropertiesReader.readProperty(fileName, "field." + fieldName + "." + "propertyName"));
                lsmFieldMetaData.setLsmUnit(PropertiesReader.readProperty(fileName, "field." + fieldName + "." + "unit"));
                fields.put(fieldName, lsmFieldMetaData);
                logger.info(fields.get(fieldName));
            }

        } catch (NullPointerException e) {
            logger.warn("Error while reading properties file: " + fileName);
            logger.warn(e);
            return false;
        }

        return true;
    }

    public boolean updateSensorIDInConfigFile(String fileName, String sensorID) {
        return PropertiesReader.writeProperty(fileName, "sensorID", sensorID);
    }

    public boolean setSensorAsRegistered(String fileName) {
        return PropertiesReader.writeProperty(fileName, "registered", "true");
    }

}
