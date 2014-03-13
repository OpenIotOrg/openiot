package gsn.utils.models;

import gsn.beans.DataField;
import gsn.beans.StreamElement;
import gsn.vsensor.ModellingVirtualSensor;


/**
 * This class is the base class for all models that need to be linked to a virtual sensor for getting updated in real-time.
 * A reference to the VS allows for accessing the other models if needed.
 * @author jeberle
 *
 */
public abstract class AbstractModel {
	
	protected DataField[] outputfield;
	
	protected ModellingVirtualSensor vs;

	public DataField[] getOutputFields() {
		return outputfield;
	}

	public void setOutputFields(DataField[] outputStructure) {
		outputfield = outputStructure;
		
	}

	public abstract StreamElement pushData(StreamElement streamElement);



	public abstract StreamElement[] query(StreamElement params);
	

	public abstract void setParam(String k, String string);

	public boolean initialize() {
		return true;
	}
	
	public void setVirtualSensor(ModellingVirtualSensor v){
		vs = v;
	}
	
	public ModellingVirtualSensor getVirtualSensor(){
		
		return vs;
	}
	
	
	
}
