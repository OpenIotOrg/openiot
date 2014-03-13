package gsn.utils.models.kvi;

import java.io.IOException;

import gsn.utils.models.*;
import gsn.beans.DataTypes;
import gsn.beans.StreamElement;

public class kviSegManager extends AbstractModel {

	// private static final transient Logger logger = Logger
	// .getLogger(kviSegManager.class);

	private int BUFFER_SIZE = 200;

	// private double DIST_THRESHOLD = 200;

	private String tsid = new String();

	private StreamElement[] buffer;
	private int ptr = 0;
	int cnt = 0;

	// ........by tian................//

	modelManager segManager;

	// ..............................//

	@Override
	public boolean initialize() {
		buffer = new StreamElement[BUFFER_SIZE];

		System.out.printf("Initialization\n");

		return true;
	}

	@Override
	public StreamElement[] query(StreamElement params) {
		
		
		//query types:  0 tp, 1 vp, 2 tr, 3 vr,
		
		int qtype=(Integer) params.getData("qtype");
		double pointval=0.0,st=0.0,ed=0.0;
		StreamElement[] ret = new StreamElement[3];

		System.out.printf("Receive one query and in processing ... \n");
		
		if(qtype==0)
		{
			pointval = (Double) params.getData("point");
		
			 try {
				segManager.tpQry(pointval);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(qtype==1)
		{
			pointval = (Double) params.getData("point");
			try {
				segManager.vpQry(pointval);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(qtype==2)
		{
			st = (Double) params.getData("st");
			ed = (Double) params.getData("ed");
			try {
				segManager.tiQry(st, ed);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else if(qtype==3)
		{
			st = (Double) params.getData("st");
			ed = (Double) params.getData("ed");
			try {
				segManager.viQry(st, ed);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		System.out.printf("Query processing is done. Check the results in database! \n");
		
		//....................test.........................//
//		 try {
//		
//		 segManager.tpQry(15906);
//		 segManager.vpQry(267);
//		 segManager.tiQry(15906, 16011);
//		
//		 segManager.viQry(267, 312);
//		
//		 } catch (Exception e) {
//		 // TODO Auto-generated catch block
//		 e.printStackTrace();
//		 }
		//..................................................//
		

		return ret;
	}

	@Override
	public StreamElement pushData(StreamElement se) {

		double val = (Double) se.getData("val");
		double tstamp = (Double) se.getData("tstamp");

		System.out.printf("sensor value at time %d: %f %f\n", cnt++, tstamp,
				val);

		try {
			segManager.kviSegLoad(0.2, val, tstamp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return se;
	}

	@Override
	public void setParam(String k, String string) {

		if (k.equals("tsid")) {
			try {
				tsid = string;
			} catch (Exception e) {
			}
		}
		segManager = new modelManager(tsid); // needs modification
		System.out.printf("setParam: %s \n", string);

		// ................test.....................//
		// try {
		//
		// segManager.tpQry(15906);
		// segManager.vpQry(267);
		// segManager.tiQry(15906, 16011);
		//
		// segManager.viQry(267, 312);
		//
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.out.printf("-------------- query completed! \n", string);
		// .........................................//

	}

}
