package org.openiot.gsn.metadata.LSM;

import org.openiot.gsn.utils.PropertiesReader;
import org.apache.log4j.Logger;

public class LSMSchema {

    private static final transient Logger logger = Logger.getLogger(LSMSchema.class);

    private String metaGraph;
    private String dataGraph;

    public boolean initFromConfigFile(String fileName) {
        try {
            this.setMetaGraph(PropertiesReader.readProperty(fileName, "metaGraph"));
            this.setDataGraph(PropertiesReader.readProperty(fileName, "dataGraph"));

        } catch (NullPointerException e) {
            logger.warn("Error while reading properties file: " + fileName);
            logger.warn(e);
            return false;
        }


        return true;
    }

    @Override
    public String toString() {
        return "LSMSchema{" +
                "metaGraph='" + metaGraph + '\'' +
                ", dataGraph='" + dataGraph + '\'' +
                '}';
    }

    public String getMetaGraph() {
        return metaGraph;
    }

    public void setMetaGraph(String metaGraph) {
        this.metaGraph = metaGraph;
    }

    public String getDataGraph() {
        return dataGraph;
    }

    public void setDataGraph(String dataGraph) {
        this.dataGraph = dataGraph;
    }
}
