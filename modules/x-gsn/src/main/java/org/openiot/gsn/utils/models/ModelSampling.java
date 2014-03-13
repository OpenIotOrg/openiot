package gsn.utils.models;

import gsn.utils.models.helper.Segmenter;
import gsn.utils.models.helper.Tools;
import weka.classifiers.Classifier;
import weka.classifiers.SegmentedClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.DummyFilter;
import weka.filters.unsupervised.instance.ErrorBased;
import weka.filters.unsupervised.instance.RandomSample;
import weka.filters.unsupervised.instance.SubSample;

public class ModelSampling {
	
	public static final int BINARY = 0;
	public static final int BINARY_PLUS = 1;
	public static final int HEURISTIC = 2;
	public static final int HEURISTIC_PLUS = 3;
	public static final int UNIFORM = 0;
	public static final int ERROR_BASED = 1;
	public static final int RANDOM = 2;

    final public static String SEGMENT_NAMES[] = {"BINARY","BINARY_PLUS","HEURISTIC","HEURISTIC_PLUS"};
	final public static String SAMPLING_NAMES[] = {"UNIFORM","ERROR_BASED","RANDOM"};
    final public static String MODEL_NAMES[] = {"SVM","LINEAR"};
    
    private int seg_method = 0;
	private int samp_method = 0;
	private int model = 0;
	private int seg_num = 1;
	private int samp_ratio = 1;
	private Classifier classifier = null;

    public ModelSampling(int model, int segment_method, int segment_num,
			int sampling_method, int sampling_ratio) {
    	seg_method = segment_method;
    	samp_method = sampling_method;
    	this.model = model;
    	seg_num = segment_num;
    	samp_ratio = sampling_ratio;
	}

	/*
    * Returns the id, given a string
    * comparison is case insensitive
    * */
    public static int getIdFromString(String[] array, String strModel) {

        int result = -1;

        if (strModel.matches("\\d")) {  // model given as number
            result = Integer.parseInt(strModel);
            return result;
        }

        for (int i = 0; i < array.length; i++) {
            if (array[i].toUpperCase().equals(strModel.toUpperCase())) {
                result = i;
                break;
            }
        }
        return result;
    }

	public Double predict(Instance i) {
		try{
		return new Double(classifier.classifyInstance(i));
		}catch(Exception e){
			return null;
		}
	}
	
	public int train(Instances training_set,int model, int segment_method, int segment_num,
			int sampling_method, int sampling_ratio) {
    	seg_method = segment_method;
    	samp_method = sampling_method;
    	this.model = model;
    	seg_num = segment_num;
    	samp_ratio = sampling_ratio;
		return train(training_set);
	}

	public int train(Instances training_set) {
		try{
			Segmenter s = new Segmenter(seg_method,model);
			Double[] seg = s.getSegments(seg_num,training_set);
			if(seg == null){return 0;}
			s.computeErrors(training_set, seg);
			Filter f = null;
			if(samp_method == UNIFORM){
				SubSample ss = new SubSample();
			    ss.setInputFormat(training_set);
			    ss.setRatio(samp_ratio);
			    ss.setM_index(0);
			    f=ss;
			}else if(samp_method == ERROR_BASED){
			    ErrorBased ss = new ErrorBased();
			    ss.setInputFormat(training_set);
			    ss.setM_ratio(samp_ratio);
			    ss.setM_errors(s.Pred_errors);
			    f=ss;
			}else if(samp_method == RANDOM)
				{
			    RandomSample ss = new RandomSample();
			    ss.setInputFormat(training_set);
			    ss.setM_ratio(samp_ratio);
			    f=ss;
			}else{
				DummyFilter ss = new DummyFilter();
				ss.setInputFormat(training_set);
				f = ss;
			}
			classifier = new SegmentedClassifier(Tools.getClassifierById(model), 1, seg,f);
			classifier.buildClassifier(training_set);
			return 1;
		}catch(Exception e){
			return 0;
		}
	}
}
