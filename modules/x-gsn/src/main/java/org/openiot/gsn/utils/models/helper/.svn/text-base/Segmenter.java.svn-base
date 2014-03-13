package gsn.utils.models.helper;

import gsn.utils.models.ModelSampling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


import weka.classifiers.Classifier;
import weka.classifiers.SegmentedClassifier;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.DummyFilter;
import weka.filters.unsupervised.instance.ErrorBased;
import weka.filters.unsupervised.instance.RandomSample;
import weka.filters.unsupervised.instance.SubSample;

public class Segmenter {

	
	public double[] Pred_errors = new double[1400];
	private int seg_method=0;
	private int model = 0;
	
	
	public Segmenter(int seg_method,int model) {
		this.seg_method = seg_method;
		this.model = model;
	}
	
	public Double[] getSegments(int seg_num, Instances training_set) {
		
		Double[] seg = {};
		try{
		//init the modeling errors
		computeErrors(training_set,new Double[]{});
		
		//number of segments to test
		for (int j = 0; j < seg_num; j++) {
			if (seg_method == ModelSampling.BINARY || seg_method == ModelSampling.BINARY_PLUS){
			double[] errors = binarySegments(seg,training_set);
			seg = splitMax(seg, errors,0/1400.0,23*60/1400.0);
			}else if (seg_method == ModelSampling.HEURISTIC){
			seg = heuristicSegments(seg,training_set);
			}else if (seg_method == ModelSampling.HEURISTIC_PLUS){
			seg = randSegments(seg, training_set);
			/*}else if (seg_method == ModelSampling.HEURISTIC_BINARY){
				seg = mixedSegments(seg,training_set);*/
			}
		}
		}catch(Exception e){
			return null;
		}
		return seg;
	}

	private double[] binarySegments(Double[] seg2,Instances i) throws Exception{
		
		SegmentedClassifier sc = computeErrors(i, seg2);
		
		double[] errors = new double[seg2.length+1];
		for (int j = 0; j < seg2.length+1; j++) {
			Instances seg = sc.getSegment(i,j);
			errors[j] = Tools.get_avg_error(sc, seg);
		}

		return errors;
	}
	
private Double[] randSegments(Double[] seg,Instances i) throws Exception{
		
	    computeErrors(i, seg);
			
		double[] dif = Pred_errors.clone();
		Arrays.sort(dif);
		double median = dif[dif.length/2];
		Random r = new Random();
		int current = 0;
		int count = 0;
		int max_s = 0;
		int max_v = 0;
		int max_e = 0;
		for (int j = 0; j < Pred_errors.length; j++) {
			if (Pred_errors[j] > median){
				count++;
			}
			else{
				max_e = r.nextBoolean() ? current : j; 
				if (count > max_v && !is_near_segment(seg,i.instance(max_e).value(0),0.01)){
					max_s = max_e;
					max_v = count;
				}
				current = j;
				count = 0;
			}
		}
		Double[] ret = Arrays.copyOf(seg, seg.length+1);
		ret[ret.length-1] = i.instance(max_s).value(0);
		
		Arrays.sort(ret);
		
		return ret;
		
	}




private Double[] heuristicSegments(Double[] seg,Instances i) throws Exception{
	
	computeErrors(i, seg);

	int window_size = 10;
	int max_s = 0;
	double max_v = 0;
	double[] error_rel = new double[Pred_errors.length-window_size];
	for(int n=window_size/2; n<Pred_errors.length-window_size/2;n++){
		double s=0;
		for(int o = -window_size/2;o<window_size/2;o++){
			s += Pred_errors[n+o];
		}
		s /= window_size;
		error_rel[n-window_size/2]=Math.abs(Pred_errors[n]-s);
		if(max_v < Math.abs(Pred_errors[n]-s) && !is_near_segment(seg,i.instance(n).value(0),0.005)){
			max_v = Math.abs(Pred_errors[n]-s);
			max_s = n;
		}
	}
	
	Double[] ret = Arrays.copyOf(seg, seg.length+1);
	ret[ret.length-1] = i.instance(max_s).value(0);
	
	Arrays.sort(ret);
	
	return ret;
}

private boolean is_near_segment(Double[] seg, double val, double tol){
	if(seg.length > 0){
		int i = 0;
		while(i<seg.length && val > seg[i]){
			i++;
		}
		if(i==0){
			return seg[i]-val < tol;
		}
		if(i==seg.length){
			return val - seg[i-1] < tol;
		}
		return seg[i]-val < tol || val - seg[i-1] < tol;
	}
	return false;	
}

private Double[] splitMax(Double[] seg,double[] errors,double min_,double max_){
	int argmax = 0;
	ArrayList<Double> s = new ArrayList<Double>();
	for (Double d : seg) {
		s.add(d);
	}
	double val = Double.MIN_VALUE;
	for(int i=0;i<errors.length;i++){
		if(val<errors[i] && (seg_method == 0 || i==0 || i== seg.length || seg[i] - seg[i-1] > 0.01)){
			val = errors[i];
			argmax = i;
		}
	}
	double value = (min_+ max_)/2;
	if (seg.length != 0)
	{
		value = (min_+ seg[0])/2;
		if (argmax > 0 && argmax < seg.length){
			value = (seg[argmax-1]+seg[argmax])/2;
		}else if (argmax == seg.length){
			value = (seg[argmax-1]+max_)/2;
		}
	}
	s.add(argmax, value);
	Double[] seg2 = new Double[seg.length+1];
	return s.toArray(seg2);
	
}


public SegmentedClassifier computeErrors(Instances i,Double[] seg) throws Exception{
	Classifier cl = Tools.getClassifierById(model);
	Filter f = new DummyFilter();
	f.setInputFormat(i);
	SegmentedClassifier sc = new SegmentedClassifier(cl, 1, seg,f);
	sc.buildClassifier(i);	
	i.sort(0);
	Pred_errors = Tools.get_errors(sc, i); 
	return sc;

}




}
