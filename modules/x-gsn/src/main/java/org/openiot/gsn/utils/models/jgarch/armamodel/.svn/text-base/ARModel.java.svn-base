package gsn.utils.models.jgarch.armamodel;

import gsn.utils.models.jgarch.wrappers.REngineManager;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class ARModel {
	private double[] arResiduals;
	
	private double[] tSeries;
	
	private int arOrder = 2;
	
	private double[] arPreds;
	
	private int predStep = 1;
	
	private int initialOffset = arOrder + 1;	
	
	
	public double[] getArPreds() {
		return arPreds;
	}	

	public void setPredStep(int predStep) {
		this.predStep = predStep;
	}
	
	

	public double[] getArResiduals() {
		return arResiduals;
	}

	public ARModel(double[] tSeries, int arOrder, int predStep){
		this.tSeries = tSeries;		
		this.arOrder = arOrder;
		this.initialOffset = arOrder+1;
		this.predStep = predStep;
		
	}
	
	public ARModel(double[] tSeries){
		this.tSeries = tSeries;		
	}
	
    public void run() {
    	
    	REngineManager rengineManager = REngineManager.getInstance();
    	Rengine re = rengineManager.getREngine();	
		
		try {						
			
			REXP RarResiduals;
			REXP RarPreds;
			re.assign("valseries", tSeries);
						
			re.eval("valseries.ar=ar.mle(valseries, aic=FALSE, order.max="+ arOrder + ")");
			RarResiduals=re.eval("valseries.ar$resid["+initialOffset + ":length(valseries)]");
			RarResiduals=re.eval("valseries.ar$resid["+initialOffset + ":length(valseries)]");
			
			this.arResiduals = RarResiduals.asDoubleArray();
			re.eval("valpred=predict(valseries.ar,n.ahead="+predStep+")");
			RarPreds=re.eval("valpred$pred");
			
			this.arPreds = RarPreds.asDoubleArray(); 
			
		} catch (Exception e) {
			System.out.println("EX:"+e);
			e.printStackTrace();
			rengineManager.endEngine();
		} 
	
    }
}
