package gsn.utils.models;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

public class ModelLoader {
	
	
	private Classifier classifier = null;

    public ModelLoader(String model) {
    	try{
    		classifier = (Classifier) weka.core.SerializationHelper.read(model);
    	}catch(Exception e)
    	{}
	}

	public Double predict(Instance i) {
		try{
		return new Double((classifier.classifyInstance(i)+4)*100);
		}catch(Exception e){
			return null;
		}
	}
}
