package gsn.utils.models.helper;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;

/**
 * some useful tools for working with the results of a classifier
 * @author jeberle
 *
 */
public class Tools {
	
	/**
	 * get the list of classification errors for each instance in the dataset
	 * @param c the classifier
	 * @param i the dataset
	 * @return the list of errors
	 * @throws Exception
	 */
	public static double[] get_errors(Classifier c, Instances i) throws Exception{
		double[] computed = new double[i.numInstances()];
		for(int m = 0;m<computed.length;m++){
			double s = c.classifyInstance(i.instance(m));
			double r = i.instance(m).value(i.classAttribute());
			computed[m] = (r-s)*(r-s); 
		}
		return computed;
	}
	
	/**
	 * get the average error of the classifier over the given dataset
	 * @param c the classifier
	 * @param i the dataset
	 * @return the average error
	 * @throws Exception
	 */
	public static double get_avg_error(Classifier c, Instances i) throws Exception{
		double computed = 0;
		for(int m = 0;m<i.numInstances();m++){
			double s = c.classifyInstance(i.instance(m));
			double r = i.instance(m).value(i.classAttribute());
			computed += (r-s)*(r-s); 
		}
		return computed/i.numInstances();
	}
	
	/**
	 * add a new feature in the dataset containing the predicted values by the classifier
	 * @param c the classifier
	 * @param i the dataset
	 * @throws Exception
	 */
	public static void add_predictions(Classifier c, Instances i) throws Exception{
		
		double[] computed = new double[i.numInstances()];
		for(int m = 0;m<computed.length;m++){
			computed[m] = c.classifyInstance(i.instance(m)); 
		}
		Attribute a = new Attribute("interpolate");
		int num = i.numAttributes();
		i.insertAttributeAt(a, num);
		for(int m = 0;m<computed.length;m++){
			i.instance(m).setValue(num, computed[m]);
		}
	}
	
	/**
	 * get a classifier by its name.
	 * This function can be used to set the parameter of the classifiers
	 * @param name
	 * @return
	 */
	public static Classifier getClassifierById(int id){
		Classifier c = null;
		if(id == 0){
			LibSVM sv = new LibSVM();
			sv.setSVMType(new SelectedTag(LibSVM.SVMTYPE_EPSILON_SVR,LibSVM.TAGS_SVMTYPE));
			sv.setCost(Math.pow(2, 2));
			sv.setGamma(Math.pow(2, 1));
			sv.setEps(0.00001);
			c=sv;
		}
		else if(id == 1){
			c = new LinearRegression();
		}
		return c;
	}
	
	/**
	 * pre-process the data be normalizing and removing unused attributes
	 * @param i
	 * @return
	 */
		public static Instances prepareInstances(Instances i){
			
			//select features to use
			i.setClassIndex(9);
			i.deleteAttributeAt(8);
			i.deleteAttributeAt(7);
			i.deleteAttributeAt(6);
			i.deleteAttributeAt(2);
			i.deleteAttributeAt(1);
			
			//scale the values
			for(int k=0;k<i.numInstances();k++){
				Instance j = i.instance(k);
				j.setValue(0, j.value(0)/1400.0);
				j.setValue(2, j.value(2)/50);
				j.setValue(3, j.value(3)/100.0);
				j.setValue(4, j.value(4)/100.0 - 4);			
			}
			
			return i;
		}


}
