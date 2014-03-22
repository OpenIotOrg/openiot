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
 * @author Timotee Maret
*/

package org.openiot.gsn.tests.performance;

import org.openiot.gsn.beans.DataField;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class Queries {

    /**
     * Nb of queries per Thread.
     */
    private int nbQueries;

    /**
     * Max Nb of Threads executing the queries.
     */
    private int nbThreads;

    /**
     * Max Nb of Element per query
     */
    private int maxQuerySize = -1;

    /**
     * URL of the GSN instance being evaluated.
     */
    private String gsnUrl = "http://localhost:22001";

    private HashMap<String, ArrayList<DataField>> mapping;

    private ExecutorService executor;

    private SAXParserFactory saxParserFactory;

    public Queries(int nbQueries, int nbThreads) {
        this.nbQueries = nbQueries;
        this.nbThreads = nbThreads;
        executor = Executors.newFixedThreadPool(nbThreads);
        saxParserFactory = SAXParserFactory.newInstance();
        mapping = new HashMap<String, ArrayList<DataField>>();
    }

    public Queries setMaxQuerySize(int maxQuerySize) {
        this.maxQuerySize = maxQuerySize;
        return this;
    }

    public Queries setGsnUrl(String gsnUrl) {
        this.gsnUrl = gsnUrl;
        return this;
    }

    private void runQueries() {

        ArrayList<Future<QueryResult>> futures = new ArrayList<Future<QueryResult>>();

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < nbQueries; i++) {
            // Pick the next vs
            //int index = (int) (Math.random() * mapping.size());
            int index = i;
            Map.Entry<String, ArrayList<DataField>> entry = (Map.Entry<String, ArrayList<DataField>>) mapping.entrySet().toArray()[index];
            String vsName = entry.getKey();
            int nbFields = entry.getValue().size();
            // Build the query
            StringBuilder query = new StringBuilder(gsnUrl)
                    .append("/multidata?vs[0]=")
                    .append(vsName)
                    .append("&field[0]=All")
                    .append("&download_mode=inline")
                    .append("&download_format=csv")
                    .append("&nb=SPECIFIED&nb_value=")
                    .append(maxQuerySize);
            // TODO Extend with Conditions, aggregations.
            futures.add(executor.submit(new QueryTask(query.toString(), nbFields, vsName)));
        }

        // Go through the results and compute the stats
        SummaryStatistics execTime = new SummaryStatistics();
        SummaryStatistics tuples = new SummaryStatistics();
        SummaryStatistics fields = new SummaryStatistics();
        SummaryStatistics datas = new SummaryStatistics();
        SummaryStatistics tuplesRate = new SummaryStatistics();
        SummaryStatistics fieldsRate = new SummaryStatistics();
        SummaryStatistics datasRate = new SummaryStatistics();


        //int rowsSum = 0;
        //int fieldsSum = 0;
        //int dataSizeSum = 0;
        for (Future<QueryResult> result : futures) {
            try {
                QueryResult r = result.get();
                double deltaInSec = r.delta / 1000.0d;
                double dataInMeg = r.dataSize * 8.0d / (1024.0d * 1024.0d);
                System.out.println(r);
                execTime.addValue(deltaInSec); // s
                tuples.addValue(r.rows);
                fields.addValue(r.nbFields);
                datas.addValue(dataInMeg); // MB
                //
                tuplesRate.addValue(r.rows / deltaInSec); // tuple/s
                fieldsRate.addValue(r.nbFields / deltaInSec); // field/s
                datasRate.addValue(dataInMeg / deltaInSec); //MB/s
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        long endTime = System.currentTimeMillis();
        //

        //
        float evalDuration = (endTime - startTime) / 1000.0f;   // s
        System.out.println(new StringBuilder()
                .append("\n------ GSN Queries Result --------").append("\n")
                .append("| URL: ").append(gsnUrl).append("\n")
                .append("| Eval duration: ").append(format(evalDuration)).append(" [s]\n")
                .append("| Nb Queries   : ").append(nbQueries).append("\n")
                .append("| Tuples       : ").append(printStats(tuples, "no unit")).append("\n")
                .append("| Fields       : ").append(printStats(fields, "no unit")).append("\n")
                .append("| Raw Data     : ").append(printStats(datas, "MB")).append("\n")
                .append("| Download time: ").append(printStats(execTime, "s")).append("\n")
                .append("| Tuple Rate   : ").append(printStats(tuplesRate, "tuple/s")).append("\n")
                .append("| Field Rate   : ").append(printStats(fieldsRate, "field/s")).append("\n")
                .append("| Data Rate    : ").append(printStats(datasRate, "MB/s")).append("\n")
                .append("-----------------------------------\n"));

        executor.shutdown();
    }

    private String printStats(SummaryStatistics stats, String unit) {
        return new StringBuilder()
                .append("sum:")
                .append(format(stats.getSum()))
                .append(", min:")
                .append(format(stats.getMin()))
                .append(", max:")
                .append(format(stats.getMax()))
                .append(", mean:")
                .append(format(stats.getMean()))
                .append(", var:")
                .append(format(stats.getVariance()))
                .append(" [")
                .append(unit)
                .append("]")
                .toString();
    }

    private String format(Number value) {
        return new DecimalFormat("###.000").format(value);
    }

    private class QueryTask implements Callable<QueryResult> {
        private String query;
        private int nbFields;
        private String vsName;

        public QueryTask(String query, int nbFields, String vsName) {
            this.query = query;
            this.nbFields = nbFields;
            this.vsName = vsName;
        }

        public QueryResult call() {
            int rows = 0;
            int dataSize = 0;
            long startTime = System.currentTimeMillis();
            //
            HttpGet getOp = new HttpGet(query);
            HttpResponse response = null;
            try {
                response = new DefaultHttpClient().execute(getOp);
                int status = response.getStatusLine().getStatusCode();
                if (status == HttpURLConnection.HTTP_OK) {

                    BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        if (!line.startsWith("#")) {
                            rows++;
                            dataSize += line.length();
                        }
                    }
                } else
                    System.out.println("Failed to get the data for query: " + query + ", status: " + status);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            //
            long stopTime = System.currentTimeMillis();
            long delta = stopTime - startTime;

            QueryResult rq = new QueryResult(delta, rows, dataSize, nbFields, vsName);

            return rq;
        }
    }

    private class QueryResult {
        private long delta;
        private int rows;
        private int dataSize;
        private int nbFields;
        private String vsName;

        public QueryResult(long delta, int rows, int dataSize, int nbFields, String vsName) {
            this.delta = delta;
            this.rows = rows;
            this.dataSize = dataSize;
            this.nbFields = nbFields;
            this.vsName = vsName;
        }

        public String toString() {
            return new StringBuilder()
                    .append("vsname: ")
                    .append(vsName)
                    .append(", time: ")
                    .append(Long.toString(delta))
                    .append(", rows: ")
                    .append(rows)
                    .append(", data: ")
                    .append(dataSize)
                    .append(", nbFields: ")
                    .append(nbFields)
                    .append(", nbItems: ")
                    .append(rows * nbFields)
                    .append(", items/s: ")
                    .append((double) (rows * nbFields * 1000.0) / delta)
                            //.append(", query: ")
                            //.append(query)
                    .toString();
        }
    }

    public static void main(String[] args) {
        Queries queries = new Queries(
                Integer.parseInt(System.getProperty("nbQueries")),
                Integer.parseInt(System.getProperty("nbThreads"))
        );
        //
        String op; // optional parameter
        if ((op = System.getProperty("maxQuerySize")) != null)
            queries.setMaxQuerySize(Integer.parseInt(op));
        if ((op = System.getProperty("gsnUrl")) != null)
            queries.setGsnUrl(op);
        //
        queries.updateListOfVirtualSensors();
        //
        queries.runQueries();

    }

    private void updateListOfVirtualSensors() {
        HttpGet getOp = new HttpGet(gsnUrl + "/gsn");
        HttpResponse response = null;
        try {
            response = new DefaultHttpClient().execute(getOp);
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpURLConnection.HTTP_OK) {
                saxParserFactory.newSAXParser().parse(response.getEntity().getContent(), new ContainerInfoHandler(mapping));
            } else
                System.out.println("Failed to get the list of virtual sensors from: " + gsnUrl + ", status: " + status);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private class ContainerInfoHandler extends DefaultHandler {
        HashMap<String, ArrayList<DataField>> mapping;
        String vsName;
        ArrayList<DataField> fields;

        public ContainerInfoHandler(HashMap<String, ArrayList<DataField>> mapping) {
            this.mapping = mapping;
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if ("virtual-sensor".equalsIgnoreCase(qName)) {
                vsName = attributes.getValue("name");
                fields = new ArrayList<DataField>();
            } else if ("field".equalsIgnoreCase(qName)) {
                if (attributes.getValue("name") != null && attributes.getValue("type") != null) {
                    fields.add(new DataField(
                            attributes.getValue("name"),
                            attributes.getValue("type")
                    ));
                }
            }
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("virtual-sensor".equalsIgnoreCase(qName)) {
                mapping.put(vsName, fields);
            }
        }
    }
}
