package org.openiot.ui.xgsnWebInterface.controller;

import org.apache.metamodel.DataContext;
import org.apache.metamodel.DataContextFactory;
import org.apache.metamodel.convert.Converters;
import org.apache.metamodel.convert.TypeConverter;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.Schema;
import org.apache.metamodel.schema.Table;
import org.openiot.ui.xgsnWebInterface.SensorXMLBuilder;
import org.openiot.ui.xgsnWebInterface.model.SensorDetailsBean;
import org.openiot.ui.xgsnWebInterface.model.SensorInputBean;
import org.openiot.ui.xgsnWebInterface.model.SensorOutputBean;
import org.openiot.ui.xgsnWebInterface.sensor.CSVPredicateData;
import org.openiot.ui.xgsnWebInterface.sensor.SensorOutput;
import org.openiot.ui.xgsnWebInterface.sensor.VirtualSensor;
import org.openiot.ui.xgsnWebInterface.service.WrapperService;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.primefaces.push.EventBusFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2011-2014, OpenIoT
 * <p/>
 * This file is part of OpenIoT.
 * <p/>
 * OpenIoT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * <p/>
 * OpenIoT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Contact: OpenIoT mailto: info@openiot.eu
 * @author Luke Herron
 */

@ManagedBean
@ViewScoped
public class CreateSensorController implements Serializable {

    private VirtualSensor sensor;

    @ManagedProperty("#{sensorDetailsBean}")
    private SensorDetailsBean detailsBean;

    @ManagedProperty("#{sensorInputBean}")
    private SensorInputBean inputBean;

    @ManagedProperty("#{sensorOutputBean}")
    private SensorOutputBean outputBean;

    @ManagedProperty("#{wrapperService}")
    private WrapperService wrapperService;

    @PostConstruct
    public void init() {
        sensor = new VirtualSensor();
    }

    public void submit() {
        sensor.setDetails(detailsBean);
        sensor.setInputProperties(inputBean);
        sensor.setOutputProperties(outputBean);

        try {
            String sensorXMLFile = "/home/openiot/openiot/modules/x-gsn/virtual-sensors/".concat(sensor.getName()).concat(".xml");
            //String sensorXMLFile = "/opt/openiot/modules/x-gsn/virtual-sensors/".concat(sensor.getName()).concat(".xml");
            String sensorXMLData = new SensorXMLBuilder(sensor).build();
            Files.write(Paths.get(sensorXMLFile), sensorXMLData.getBytes());
        } catch (XMLStreamException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Failed", "Failed to create sensor"));
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Failed", "There was an error registering the XML file"));
        }

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_INFO, "Success", "XML file was successfully generated"
        ));
    }

    public void onFileUpload(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        String clientID = event.getComponent().getClientId(FacesContext.getCurrentInstance());

        if (clientID.contains("metaDataFile")) {
            parseMetaData(file, clientID);
        }
        else if (clientID.contains("sensorDataFile")) {
            parseSensorData(file);
        }
    }

    public void onWrapperChange() {
        if (detailsBean.getWrapperChoice() != null && !detailsBean.getWrapperChoice().isEmpty()) {
            inputBean.setSensorData(wrapperService.getWrappers().get(detailsBean.getWrapperChoice()));
        }
        else {
            inputBean.setSensorData(null);
        }
    }

    public void onValueChange(AjaxBehaviorEvent event) {
        if (inputBean.getSensorData() != null && inputBean.getSensorData().canParse()) {
            inputBean.setDataSourceColumns(parseInputDetails(inputBean.getSensorDataFile()));
        }
    }

    public void onCellEdit(CellEditEvent event) {
        // No-op, but left here for convenience
    }

    private void parseMetaData(UploadedFile file, String clientID) {
        if(metadataIsValid(file, clientID)) {
            detailsBean.setMetaDataFile(file.getFileName());
            outputBean.setSensorOutputs(parseOutputDetails(file));
        }
        else {
            FacesContext.getCurrentInstance().validationFailed();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed",
                            "Metadata file invalid. Please review error messages."));
        }
    }

    private void parseSensorData(UploadedFile file) {
        String sensorDataFile = "/home/openiot/openiot/modules/x-gsn/data/".concat(file.getFileName());
        //String sensorDataFile = "/opt/openiot/modules/x-gsn/data/".concat(file.getFileName());
        try {
            Files.write(Paths.get(sensorDataFile), file.getContents());
            inputBean.getPredicateData().setDataFile("data/".concat(file.getFileName()));
            inputBean.setSensorDataFile(file);
            if (inputBean.getSensorData() != null && inputBean.getSensorData().canParse()) {
                inputBean.setDataSourceColumns(parseInputDetails(file));
            }
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Failed", "There was an error registering the sensor data file"));
        }
    }

    private boolean metadataIsValid(UploadedFile file, String clientID) {
        try {
            ProcessBuilder pb = new ProcessBuilder();

            Path fileLocation = Paths.get("/home/openiot/openiot/modules/x-gsn/virtual-sensors/".concat(file.getFileName()));
            //Path fileLocation = Paths.get("/opt/openiot/modules/x-gsn/virtual-sensors/".concat(file.getFileName()));
            File metadataFile = Files.write(fileLocation, file.getContents()).toFile();
            String command = "(. ./lsm-register.sh ".concat(metadataFile.getAbsolutePath()).concat("; wait $!)");
            //String command = "./lsm-register.sh ".concat(metadataFile.getAbsolutePath());

            Process proc = pb.command("/bin/bash", "-c", command)
                    .directory(new File("/home/openiot/openiot/modules/x-gsn/"))
                    //.directory(new File("/opt/openiot/modules/x-gsn/"))
                    .redirectErrorStream(true)
                    .start();

            try(BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = input.readLine()) != null) {
                    EventBusFactory.getDefault().eventBus().publish("/logs", line);
                    if (line.contains("ERROR")) {
                        FacesContext.getCurrentInstance().validationFailed();
                        FacesContext.getCurrentInstance().addMessage(clientID, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", line));
                        return false;
                    }
                    else if (line.contains("DEBUG org.pac4j.core.client.BaseClient - credentials")) {
                        break;
                    }
                }

                FacesContext.getCurrentInstance().addMessage(clientID,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Metadata file successfully registered"));

                return true;
            }
        }
        catch (IOException e) {
            FacesContext.getCurrentInstance().validationFailed();
            FacesContext.getCurrentInstance().addMessage(clientID,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", "Could not write file to server: " +
                            e.getMessage()));

            e.printStackTrace();

            return false;
        }
    }

    private List<SensorOutput> parseOutputDetails(UploadedFile file) {
        List<SensorOutput> outputDetails = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputstream()))) {
            String prefix = "fields=";
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(prefix)) {
                    String match = line.substring(prefix.length()+1, line.length()-1);
                    for (String input: match.split(",")) {
                        outputDetails.add(new SensorOutput(input, "Set Output", "Set Type", "Add Description"));
                    }
                }
            }
        } catch (IOException e) {
            detailsBean.setMetaDataFile(null);
        }

        return outputDetails;
    }

    private List<String> parseInputDetails(UploadedFile file) {
        List<String> inputDetails = new ArrayList<>();

        try (InputStream input = file.getInputstream()) {
            int skipRows = ((CSVPredicateData) inputBean.getPredicateData()).getSkipFirstLines();
            //char delim = ((CSVPredicateData)inputBean.getPredicateData()).getDelimiter().toCharArray()[0];
            //char quote = ((CSVPredicateData)inputBean.getPredicateData()).getStringQuote().toCharArray()[0];
            DataContext dataContext = DataContextFactory.createCsvDataContext(input, ',', '"');
            Schema schema = dataContext.getDefaultSchema();

            List<String> details = Arrays.asList(schema.getTable(0).getColumnNames());
            inputDetails.addAll(details);

            // Attempt to determine the data type of each column
            Table table = schema.getTables()[0];
            Map<Column, TypeConverter<?,?>> converters = Converters.autoDetectConverters(dataContext, table.getColumns(), 1000);
            dataContext = Converters.addTypeConverters(dataContext, converters);

            // Below represents the results of the auto-detect
            // TODO: feed these back as the default selections for each data type
            DataSet ds = dataContext.query().from(table).select(table.getColumns()).execute();
            if (ds.next()) {
                Row row = ds.getRow();
                for(Column column: table.getColumns()) {
                    String columnName = column.getName();
                    String columnType = row.getValue(column).getClass().getSimpleName().toLowerCase();
                    ((CSVPredicateData)inputBean.getPredicateData()).getFieldFormats().put(columnName, columnType);
                }
            }
            ds.close();

        } catch (IOException e) {
            //csvFile = null;
        }

        return inputDetails;
    }

    // Setters for managed beans / properties

    public void setDetailsBean(SensorDetailsBean detailsBean) {
        this.detailsBean = detailsBean;
    }

    public void setInputBean(SensorInputBean inputBean) {
        this.inputBean = inputBean;
    }

    public void setOutputBean(SensorOutputBean outputBean) {
        this.outputBean = outputBean;
    }

    public void setWrapperService(WrapperService wrapperService) {
        this.wrapperService = wrapperService;
    }

    // Getters & Setters
    public VirtualSensor getSensor() {
        return sensor;
    }

    public String getInputPanel() {
        String wrapperChoice = detailsBean.getWrapperChoice();
        if (wrapperChoice != null && !wrapperChoice.isEmpty()) {
            return wrapperChoice;
        }
        else {
            return "empty";
        }
    }
}
