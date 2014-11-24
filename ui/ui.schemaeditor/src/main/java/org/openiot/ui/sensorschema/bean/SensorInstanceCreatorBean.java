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
 * 
 * 	   @author Prem Jayaraman
 */
package org.openiot.ui.sensorschema.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.openiot.ui.sensorschema.register.AbstractSensorRegistrarFactory;
import org.openiot.ui.sensorschema.register.SensorRegistratFactoryLSM;
import org.openiot.ui.sensorschema.utils.OpeniotVocab;
import org.openiot.ui.sensorschema.utils.Utils;
import org.openiot.ui.sensorschema.utils.XGSNMetaDataCreator;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean
@SessionScoped
public class SensorInstanceCreatorBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8413634927811902093L;
	private List<FieldMetaDataBean> sensorproperties;
	private List<String> sensortypelist;
	private String selectedSensor;
	private boolean show;
	private String outputText;
	private AbstractSensorRegistrarFactory factory;	
	private transient DataModel<FieldMetaDataBean> model;
	private boolean fileready = false;
	
	private String sensorID;
	private boolean registered;
	
	//to enable download of metadata file
	private StreamedContent file;
	
	private boolean showsensorinstance;
	
	//this is the container of the sensor description and properties
	private SensorMetaDataBean sensormeatadata;
		
	
	@PostConstruct
    public void init() {       
                
		showsensorinstance = false;
        show = false;
        sensormeatadata = new SensorMetaDataBean();
        sensorproperties = new ArrayList<FieldMetaDataBean>();  
        registered = false;
        
        
    	//factory singleton pattern to connect to corresponding cloud server implementation
    	//http://java.dzone.com/articles/design-patterns-abstract-factory
    	//http://howtodoinjava.com/2012/10/22/singleton-design-pattern-in-java/#enum_singleton
		factory = new SensorRegistratFactoryLSM();
		
		//get avaialble sensor types
		sensortypelist = factory.getSensorRegistrar().getSensorList();                 		
    }

	
	public void generateMetaDataFile(){
		//Get metadata in xGSN format from the XGSN utility file
		//This will be updated if xGSN metadata format changes.
		
		//create the field data for each property		
		//populatemetadata();
		
		XGSNMetaDataCreator xgsn = new XGSNMetaDataCreator();		
		sensormeatadata.setSensorID(sensorID);		
		
		Utils utils = new Utils();
		String gsnoutput = utils.writetoTemplate(sensormeatadata);
		InputStream stream = new ByteArrayInputStream(gsnoutput.getBytes(StandardCharsets.UTF_8));
		file = new DefaultStreamedContent(stream, "text/plain", sensormeatadata.getSensorName() + ".metadata");
		fileready = true;
		
		
		//To download the schmea in RDF turtle format
		//currently not used. we use simple metadata decsription format
		//String rdfmetadata = xgsn.createMetadata(sensormeatadata);
		
		//System.out.println(rdfmetadata);
		
		//creating a file download option for the user
		//InputStream stream = new ByteArrayInputStream(rdfmetadata.getBytes(StandardCharsets.UTF_8));
		//file = new DefaultStreamedContent(stream, "text/plain", sensormeatadata.getSensorName() + ".metadata");
		//fileready = true;
		
	}
	public void handleLogout() throws IOException {
		//sessionBean.setUserId(null);
		//FacesContext.getCurrentInstance().getExternalContext().dispose();

//		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		FacesContext.getCurrentInstance().getExternalContext().redirect("logout?faces-redirect=true");

	}
	
	public void registersensorinstance(){
		populatemetadata();
		
		if (!factory.getSensorRegistrar().checkSensorInstanceRegistrationbyName(sensormeatadata.getSensorName())){
			
			sensorID = factory.getSensorRegistrar().registerSensorInstance(sensormeatadata);
			if(sensorID!=null){
				registered = true;
				addMessage("Registration Successful. Click Download XGSN Metadata to download metadata file");
			}
			else
				addMessage("Error Registering the Sensor. Please check LSM configurations");
		}
		else{
			System.out.println("Sensor already exisits");
			addMessage("Sensor Instance with same name exists. Please use different sensor name");
		}
	}
	
	public void populatemetadata(){
		sensormeatadata.setSensorType(selectedSensor);
		for (FieldMetaDataBean fmdbean: sensorproperties){
			sensormeatadata.getFields().put(fmdbean.getGsnFieldName(), fmdbean);
		}
	}
	
    public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary,  null);
        FacesContext.getCurrentInstance().addMessage("registersensorgrowl", message);
    }
	
	public void getdescription(){
		StringBuilder out = new StringBuilder();
		
		show = true;
		
		ArrayList<String> sensordesc = factory.getSensorRegistrar().getSensorDescription(selectedSensor);
		
		
		out.append( "The Properties Observed By <br /> <b>" + selectedSensor + "</b> <br /> Are <br />");
		for (String s: sensordesc)
			out.append(s);
			
		outputText = out.toString();

		if (sensordesc!= null && sensordesc.size() != 0){
        	for (String val: sensordesc){
        		FieldMetaDataBean bean = new FieldMetaDataBean();
        		bean.setLsmPropertyName(val);
        		sensorproperties.add(bean);	
        	}
        	model = new ListDataModel<FieldMetaDataBean>(sensorproperties);
        }
	}

	public String getSelectedSensor() {
		return selectedSensor;
	}



	public void setSelectedSensor(String selectedSensor) {
		this.selectedSensor = selectedSensor;
	}



	public boolean isShow() {
		return show;
	}



	public void setShow(boolean show) {
		this.show = show;
	}



	public String getOutputText() {
		return outputText;
	}



	public void setOutputText(String outputText) {
		this.outputText = outputText;
	}
	    
	
	public void clearSession(){

		//clear the session for new sensor type creation
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		try {
			
			
			FacesContext.getCurrentInstance().getExternalContext().redirect(OpeniotVocab.SCHEMAEDITOR_URI + "/sensorinstanceeditor.jsf");
		} catch (IOException e) {
			e.printStackTrace();
		}
		show = false;
		fileready = false;
		showsensorinstance = false;
	}
	
	public void showSensorInstance(){
		setShowsensorinstance(true);
	}



	public boolean isShowsensorinstance() {
		return showsensorinstance;
	}



	public void setShowsensorinstance(boolean showsensorinstance) {
		this.showsensorinstance = showsensorinstance;
	}



	public SensorMetaDataBean getSensormeatadata() {
		return sensormeatadata;
	}



	public void setSensormeatadata(SensorMetaDataBean sensormeatadata) {
		this.sensormeatadata = sensormeatadata;
	}



	public DataModel<FieldMetaDataBean> getModel() {
        if (model == null) {
            model = new ListDataModel<FieldMetaDataBean>(sensorproperties);
        }

        return model;
	}



	public void setModel(DataModel<FieldMetaDataBean> model) {
		this.model = model;
	}


	public List<String> getSensortypelist() {
		return sensortypelist;
	}


	public void setSensortypelist(List<String> sensortypelist) {
		this.sensortypelist = sensortypelist;
	}
	
	public List<FieldMetaDataBean> getSensorproperties() {
		return sensorproperties;
	}


	public StreamedContent getFile() {
		return file;
	}


	public boolean isFileready() {
		return fileready;
	}


	public void setFileready(boolean fileready) {
		this.fileready = fileready;
	}


	public boolean isRegistered() {
		return registered;
	}


	public void setRegistered(boolean registered) {
		this.registered = registered;
	}


	
}
