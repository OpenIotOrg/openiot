package gsn.utils.models;

import gsn.utils.models.jgarch.armamodel.ARModel;
import gsn.utils.models.jgarch.garchmodel.GarchModel;
import gsn.utils.models.jgarch.util.ArrayUtils;
import gsn.utils.models.jgarch.wrappers.REngineManager;
import org.apache.log4j.Logger;

import java.lang.Math;

import java.util.List;

public class ArmaGarchModel implements IModel {

    private final transient Logger logger = Logger.getLogger(ArmaGarchModel.class);
    private double[] stream;

    private int windowSize;

    private double errorBound = 3;

    private double minVar = 1E-4;

    ArmaGarchModel(int windowSize, double errorBound, double[] stream) {
        this.stream = stream;
        this.windowSize = windowSize;
        this.errorBound = errorBound;
    }


    public boolean FitAndMarkDirty(double[] processed, double[] dirtyness, double[] quality) {
        boolean allClean = true;
        //double [] predUVar = new double[stream.length+1];
        //double [] predLVar = new double[stream.length+1];
        //double [] predValue = new double[stream.length+1];

        // will them with NaNs until the windowSize is reached
        for (int i = 0; i < windowSize; i++) {
            //predUVar[i] = Double.NaN;
            //predLVar[i] = Double.NaN;
            //predValue[i] = Double.NaN;
            processed[i] = stream[i];
            dirtyness[i] = 0;
        }

        // Sliding Window
        double[] tseries = new double[windowSize];

        for (int i = 0; i <= (stream.length - windowSize - 1); i++) {
            int currIdx = i + windowSize;

            System.arraycopy(stream, i, tseries, 0, windowSize);

            /*
            for (double t : tseries) {
                System.out.print(t + ",");
            }
            */

            //System.out.println();
            //System.out.println(i + windowSize);


            // create and execute AR model
            ARModel ar = new ARModel(tseries);
            ar.run();

            // predict next value from AR model
            double[] arPred = ar.getArPreds();
            double predValue = arPred[0];     // estimated

            // Get residuals from AR model and give them to GARCH model
            double[] arResid = ar.getArResiduals();
            GarchModel gm = new GarchModel(arResid);
            gm.run();

            // Predict +ve and -ve variance from GARCH model.
            double predUVar = gm.getPredUVar();  // sigma
            double predLVar = gm.getPredLVar();

            //System.out.println(gm.getPredUVar());
            //System.out.println(gm.getPredLVar());

            double quality_metric = 0;
            if (predUVar != 0.0)
                quality_metric = 1 / Math.sqrt(2 * Math.PI * predUVar * predUVar) * Math.exp(-((stream[currIdx] - predValue) * (stream[currIdx] - predValue)) / (2 * predUVar * predUVar));

            quality[currIdx] = quality_metric;

            logger.warn("quality : " + currIdx + " : " + quality_metric + "U-var: " + predUVar + "L-var: " + predLVar);

            if (predUVar > minVar) {
                if ((stream[currIdx] <= predValue + errorBound * Math.sqrt(predUVar)) &&
                        (stream[currIdx] >= predValue + errorBound * Math.sqrt(predLVar))) {
                    processed[currIdx] = stream[currIdx];
                    dirtyness[currIdx] = 0;

                } else {
                    processed[currIdx] = predValue;
                    dirtyness[currIdx] = 1;
                    allClean = false;
                }

            } else {
                processed[currIdx] = stream[currIdx];
                dirtyness[currIdx] = 0;
            }
        }

        return allClean;
    }

}
