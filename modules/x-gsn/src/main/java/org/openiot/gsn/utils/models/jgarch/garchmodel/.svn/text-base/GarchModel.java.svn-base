package gsn.utils.models.jgarch.garchmodel;

import gsn.utils.models.jgarch.wrappers.REngineManager;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class GarchModel {
	private double predUVar;
	
	private double predLVar;
	
	private double[] tSeries;
	
	private int garchOrder = 1;
	private int archOrder = 1;
	private int initialOffset = (archOrder > garchOrder) ? archOrder + 1: garchOrder +1;	
	
	public double getPredUVar() {
		return predUVar;
	}

	public double getPredLVar() {
		return predLVar;
	}

	public GarchModel(double[] tSeries, int archOrder, int garchOrder){
		this.tSeries = tSeries;
		this.garchOrder = garchOrder;
		this.archOrder = archOrder;
		initialOffset = (archOrder > garchOrder) ? archOrder + 1: garchOrder +1;
	}
	
	public GarchModel(double[] tSeries){
		this.tSeries = tSeries;		
	}
	
    public void run() {
	// just making sure we have the right version of everything
    	REngineManager rengineManager = REngineManager.getInstance();
    	Rengine re = rengineManager.getREngine();
	
		
		try {					
			
			REXP RpredUVar;
			REXP RpredLVar;
			
			//re.eval("library(tseries)"); // not required since REngineManager.initREngine() calls it once when the REngine is created
			
			re.assign("nyse", tSeries);
			re.eval("nyse.g=garch(nyse, order=c("+ garchOrder + ","+ archOrder + "))");
			re.eval("u=tseries:::predict.garch(nyse.g,genuine=TRUE)");
			
			//System.out.println(RpredUVar=re.eval("u["+ initialOffset + ":dim(u)[1],1]"));
			//System.out.println(RpredLVar=re.eval("u["+ initialOffset + ":dim(u)[1],2]"));
			
			RpredUVar=re.eval("u[dim(u)[1],1]");
			RpredLVar=re.eval("u[dim(u)[1],2]");
			
			predUVar = RpredUVar.asDouble();
			predLVar = RpredLVar.asDouble();
			
			
		} catch (Exception e) {
			System.out.println("EX:" + e);
			e.printStackTrace();
			rengineManager.endEngine();
		} 
	
    }
}
