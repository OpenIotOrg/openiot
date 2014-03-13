package gsn.utils.models;

import gsn.utils.models.jgarch.util.ArrayUtils;
import gsn.utils.models.jgarch.wrappers.REngineManager;

import java.io.*;
import java.util.Vector;

public class ModelFitting {
    final public static int CONSTANT = 0;
    final public static int LINEAR = 1;
    final public static int QUADRATIC = 2;
    final public static int CHEBYSHEV_DEG1 = 3;
    final public static int CHEBYSHEV_DEG2 = 4;
    final public static int CHEBYSHEV_DEG3 = 5;
    final public static int ARMA_GARCH = 6;
    final public static String MODEL_NAMES[] = {"constant",
            "linear",
            "quadratic",
            "chebyschev_deg1",
            "chebyschev_deg2",
            "chebyschev_deg3",
            "arma_garch"
    };

    /*
    * Returns the model id, given a string
    * comparison is case insensitive
    * */
    public static int getModelIdFromString(String strModel) {

        int result = -1;

        if (strModel.matches("\\d")) {  // model given as number
            result = Integer.parseInt(strModel);
            return result;
        }

        for (int i = 0; i < MODEL_NAMES.length; i++) {
            if (MODEL_NAMES[i].toUpperCase().equals(strModel.toUpperCase())) {
                result = i;
                break;
            }
        }
        return result;
    }

    public static boolean FitAndMarkDirty(int model, double errorBound, int windowSize, double[] stream, long[] timestamps, double[] processed, double[] dirtyness, double[] quality) {


        long[] _timestamps = new long[timestamps.length];
        //decode model
        for (int i = 0; i < timestamps.length; i++) {
            _timestamps[i] -= timestamps[i] - timestamps[0];
        }

        //could fail
        IModel m;

        switch (model) {
            case CONSTANT:
                m = new ChebyshevPolynomialModel(0, windowSize, errorBound, _timestamps, stream);
                break;
            case LINEAR:
                m = new PolynomialModel(1, windowSize, errorBound, _timestamps, stream);
                break;
            case QUADRATIC:
                m = new PolynomialModel(2, windowSize, errorBound, _timestamps, stream);
                break;
            case CHEBYSHEV_DEG1:
                m = new ChebyshevPolynomialModel(1, windowSize, errorBound, _timestamps, stream);
                break;
            case CHEBYSHEV_DEG2:
                m = new ChebyshevPolynomialModel(2, windowSize, errorBound, _timestamps, stream);
                break;
            case CHEBYSHEV_DEG3:
                m = new ChebyshevPolynomialModel(3, windowSize, errorBound, _timestamps, stream);
                break;
            case ARMA_GARCH:
                m = new ArmaGarchModel(windowSize, errorBound, stream);
                break;
            default:
                return false;
        }

        //fit
        boolean result = m.FitAndMarkDirty(processed, dirtyness, quality);

        return result;
    }

    public static Vector<Double> load_doubles(String fileName) throws IOException {
        File file = new File(fileName);
        BufferedReader bufRdr = new BufferedReader(new FileReader(file));
        String line = null;
        int row = 0;
        Vector<Double> v = new Vector<Double>();
        while ((line = bufRdr.readLine()) != null) {
            v.add(Double.parseDouble(line));
            row++;
        }
        System.out.println("data rows => " + v.size());
        bufRdr.close();
        return v;
        /*
        array = new double[v.size()];
        for (int i=0;i<v.size();i++) {
            array[i] = v.get(i);
        }
        */

    }

    public static Vector<Long> load_longs(String fileName) throws IOException {
        File file = new File(fileName);
        BufferedReader bufRdr = new BufferedReader(new FileReader(file));
        String line = null;
        int row = 0;
        Vector<Long> v = new Vector<Long>();
        while ((line = bufRdr.readLine()) != null) {
            v.add(Long.parseLong(line));
            row++;
        }
        System.out.println("timed rows => " + v.size());
        bufRdr.close();
        return v;
    }

    // data file, model, window, error, duration, rmse, anomalies
    static void appendOutputFile(String outputFile,
                                 String datafile, int model, int window, double error,
                                 double rmse, long duration, long anomalies) throws IOException {
        FileWriter fstream = new FileWriter(outputFile, true);
        BufferedWriter out = new BufferedWriter(fstream);
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(datafile).append("\", ")
                .append(model).append(", ").append(window).append(", ").append(error).append(", ")
                .append(rmse).append(", ").append(duration).append(", ").append(anomalies).append("\n");

        System.out.println(sb.toString());

        out.write(sb.toString());
        out.close();
    }


    public static void main(String[] argv) throws IOException {
        //System.out.println(">>>>>>>> " + argv.length);
        if (argv.length < 6) {
            System.out.println("Usage :");
            System.out.println(argv[0]);
            System.exit(1);
        }
        String datafile = argv[0];
        String timedfile = argv[1];
        String str_model = argv[2];
        String str_window = argv[3];
        String str_error = argv[4];
        String outfile = argv[5];

        int model = Integer.parseInt(str_model);
        int window = Integer.parseInt(str_window);
        double error = Double.parseDouble(str_error);

        System.out.println("data file => " + datafile);
        System.out.println("timed file => " + timedfile);
        System.out.println("model => " + model);
        System.out.println("window => " + window);
        System.out.println("error => " + error);
        System.out.println("outfile => " + outfile);

        boolean use_ARMA_GARCH = false;

        if (model == ARMA_GARCH) {
            use_ARMA_GARCH = true;
        }

        Vector<Double> v_stream = load_doubles(datafile);
        Vector<Long> v_timestamps = load_longs(timedfile);

        int stream_size = v_stream.size();

        int cursor = 0;
        int n_iterations = stream_size / window;

        System.out.println("Iterations => " + n_iterations);

        long startTime = System.currentTimeMillis();

        double RMSE = 0;
        long n_anomalies = 0;

        for (long iteration = 0; iteration < n_iterations; iteration++) {
            //System.out.println("Iteration: " + iteration);
            //System.out.println(">> " + cursor);


            double[] stream = new double[window];
            long[] timestamps = new long[window];
            for (int i = 0; i < window; i++) {
                stream[i] = v_stream.get(cursor + i);
                timestamps[i] = v_timestamps.get(cursor + i);
                //System.out.println(i + " " + timestamps[i] + ":" + stream[i]);
            }

            double[] processed = new double[window];
            double[] dirtyness = new double[window];
            double[] quality = new double[window];

            if (use_ARMA_GARCH)
                FitAndMarkDirty(model, error, window - 1, stream, timestamps, processed, dirtyness, quality);
            else
                FitAndMarkDirty(model, error, window, stream, timestamps, processed, dirtyness, quality);

            cursor += window;

            for (int i = 0; i < processed.length; i++) {
                RMSE += (stream[i] - processed[i]) * (stream[i] - processed[i]);
                if (dirtyness[i] > 0)
                    n_anomalies++;
                //System.out.println(i + " : " + stream[i] + " => " + processed[i] + "(" + dirtyness[i] + ")");
            }
        }

        startTime = System.currentTimeMillis() - startTime;

        RMSE = Math.sqrt(RMSE / stream_size);

        System.out.println("Duration = " + startTime);
        System.out.println("RMSE = " + RMSE);
        System.out.println("Anomalies = " + n_anomalies);

        appendOutputFile(outfile, datafile, model, window, error, RMSE, startTime, n_anomalies);

        System.exit(0); // force exit, necessary for REngine
    }
}
