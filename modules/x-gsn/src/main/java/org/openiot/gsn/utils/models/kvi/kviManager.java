package gsn.utils.models.kvi;

import java.io.*;
import java.util.*;

public class kviManager {

	public kviIndexCol kviCol;
	public kviMRproCol kviqueryCol;
	public String idxtabname;
	public scanPro tabscan;
	public long tduration;
	public double numtup;
	public String tupRangeL, tupRangeR;
	public long inmem, randacc, mracc;
	public long schlevel;
	public double parall;
	public double mapout, mapcnt;
	public String strk, edrk;

	public kviManager(String tabname, int sign) throws IOException {// 0: temp
																	// 1:val
		// kvi = new kviIndex(tabname);
		kviCol = new kviIndexCol(tabname);

		// if (startIdx == true) {

		kviCol.iniIdx();
		// } else {

		// kviCol.iniQueryRes();
		// }

		kviqueryCol = new kviMRproCol(0, tabname, sign);

		idxtabname = tabname;
		tabscan = new scanPro(0);
		tduration = 0;
		schlevel = 0;

		numtup = 0.0;
		tupRangeL = tupRangeR = "";
		inmem = randacc = mracc = 0;

		mapout = 0.0;
		mapcnt = 0.0;

		parall = 3.0;

		strk = new String();
		edrk = new String();

	}

	public boolean kvi_insert(double l, double r, double modinfo[], int order,
			double assocl, double assocr, double timecons[], String qual[],
			String assoQual[]) throws IOException {

		// ...........test.....................//
//		if (qual[0] == "st") {
//			System.out.printf("segment left %f, right %f\n", l, r);
//		}
		// ....................................//

		double[] timecnt = new double[4];
		if (kviCol.insert(l, r, modinfo, order, assocl, assocr, timecnt, qual,
				assoQual) == true) {

			timecons[0] = timecnt[0];
			timecons[1] = timecnt[1];
			
			
			FileWriter fstream;
			fstream = new FileWriter("./"+idxtabname+"Tree.txt", false);
			BufferedWriter out = new BufferedWriter(fstream);
			String str = Double.toString(kviCol.sroot);
			out.write(str);
			out.write("\n");
			out.close();

			

			return true;
		} else {
			return false;
		}
		
		
	
	
	}

	// public boolean kvi_pointSearch(double val, String qual[], int qrytype)
	// {// 0:
	// // temp
	// // 1:
	// // value
	// String[][] indi = new String[100][2];
	// long stcnt = 0, edcnt = 0;
	//
	// stcnt = System.nanoTime();
	// int resnum = kviCol.pointSearch_res(indi, val);
	//
	// // ..........modification..........................//
	//
	// numtup = tabscan.pointSearch_scan(idxtabname, indi, val, resnum,
	// qual,
	// qrytype);
	//
	// edcnt = System.nanoTime();
	//
	// // ..........metric.......................//
	// tduration = (long) ((edcnt - stcnt) / parall);
	//
	// return true;
	// }

	
	public void paraLoad() throws IOException
	{
		double[] para = new double[3];
		FileReader confr;
		confr = new FileReader("./"+idxtabname+"Tree.txt");
		BufferedReader confbr = new BufferedReader(confr);
		String confstr = confbr.readLine();
		StringTokenizer confst = new StringTokenizer(confstr, ",");
		// confst.nextToken();

		para[0] = Double.parseDouble(confst.nextToken());
		

		kviCol.sroot=para[0];
		kviCol.sdel=para[0]/2.0;
	}
	
	public boolean kvi_pointSearch_mr(double val, String qual[], int qrytype)
			throws Exception {// 0: // for openiot
		// temp
		// 1:
		// value
		
		//...........para load...........................//
		paraLoad();
		//..................................................//
		
		
		String[] bdrow = new String[5];
		double[] runpara = new double[10];
		long stcnt = 0, edcnt = 0;

		String[] tcol = { "st", "ed" }, vcol = { "vl", "vr" };

		stcnt = System.nanoTime();

		kviCol.pointSearch_resMr(bdrow, val, qrytype);

		//...............test...................//
		System.out.printf("+++++++  root:  %f   range:  %s   %s \n", kviCol.sroot,bdrow[0],bdrow[1]);
		
		//......................................//
	
		
		//...................invoke MapReduce query processing..........................//
		
		ProcessBuilder pb = new ProcessBuilder(
				"./comp.sh", idxtabname,
				Integer.toString(qrytype), "point",Double.toString(val),bdrow[0],bdrow[1]);

		 File executorDirectory = new File("/home/guo/xgsn/kviMRoper/");           
		   
		 pb.directory(executorDirectory);
		 Process p = pb.start();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    String line;

	    System.out.println("Output of running is: ");
	    while ((line = br.readLine()) != null) {
	        System.out.println(line);
	    }
		
	    //.............................................................................//
		
		
		
//		if (qrytype == 0) {
//			kviqueryCol.jobPointEvalQuery(val, idxtabname, bdrow[0], bdrow[1],
//					tcol, runpara);
//		} else {
//			kviqueryCol.jobPointEvalQuery(val, idxtabname, bdrow[0], bdrow[1],
//					vcol, runpara);
//		}
		edcnt = System.nanoTime();

		// ..........metric.......................//
		tduration = (long) ((edcnt - stcnt));

		return true;
	}

	// public boolean kvi_rangeSearch(double l, double r, String qual[], int
	// sign)// 0:
	// // temp,
	// // 1:value
	// throws Exception {
	//
	// String[][] indi = new String[100][2];
	// String[][] parallel = new String[100][2];
	// long stcnt = 0, edcnt = 0, midcnt = 0, inmemCnt;
	// double[] runpara = { 0.0, 0.0 };
	//
	// // ..test..//
	// double tmp = 0.0;
	//
	// stcnt = System.nanoTime();
	//
	// int resnum = kviCol.intervalSearch_res(indi, parallel, l, r);
	//
	// kviMRproCol.numMapIn = 0;
	//
	// inmemCnt = System.nanoTime();
	//
	// tmp = kviqueryCol.jobIntervalEvalQuery(l, r, idxtabname,
	// parallel[0][0], parallel[1][1], qual, runpara);
	//
	// midcnt = System.nanoTime();
	//
	// // modification
	// numtup = tmp;
	//
	// if (sign == 0) {
	// numtup = tabscan.rangeSearch_scan(idxtabname, indi, l, r, resnum,
	// qual) + numtup;
	// }
	//
	// edcnt = System.nanoTime();
	//
	// // .....test...........//
	// // System.out.printf("%f", numtup-tmp);
	//
	// tupRangeL = parallel[0][0];
	// tupRangeR = parallel[1][1];
	//
	// // ......metric and running time record............//
	// tduration = Math.max(midcnt - stcnt, edcnt - midcnt);
	// inmem = inmemCnt - stcnt;
	// mracc = midcnt - inmemCnt;
	// randacc = edcnt - midcnt;
	// schlevel = resnum;
	//
	// mapout = runpara[0];
	// mapcnt = runpara[1];
	//
	// return true;
	// }
	//
	// public boolean kvi_rangeSearchTest(double l, double r, String qual[],
	// int sign)// 0: temp, 1:value
	// throws Exception {
	//
	// String[] parallel = new String[5];
	// long stcnt = 0, edcnt = 0, midcnt = 0, inmemCnt;
	// double[] runpara = { 0.0, 0.0 };
	//
	// // ..test..//
	// double tmp = 0.0;
	//
	// stcnt = System.nanoTime();
	//
	// int resnum = kviCol.intervalSearch_resTest(parallel, l, r, sign);
	//
	// kviMRproCol.numMapIn = 0;
	//
	// inmemCnt = System.nanoTime();
	//
	// tmp = kviqueryCol.jobIntervalEvalQuery(l, r, idxtabname, parallel[0],
	// parallel[1], qual, runpara);
	//
	// midcnt = System.nanoTime();
	//
	// // modification
	// numtup = tmp;
	// edcnt = System.nanoTime();
	//
	// // ......metric and running time record............//
	// tduration = Math.max(midcnt - stcnt, edcnt - midcnt);
	// inmem = inmemCnt - stcnt;
	// mracc = midcnt - inmemCnt;
	// randacc = edcnt - midcnt;
	// schlevel = resnum;
	//
	// strk = parallel[0];
	// edrk = parallel[1];
	// mapout = runpara[0];
	// mapcnt = runpara[1];
	//
	// return true;
	// }
	public boolean kvi_rangeSearchMrkvi(double l, double r, String qual[], // for
																			// openiot
			int sign)// 0: temp, 1:value
			throws Exception {

		String[] parallel = new String[5];
		long stcnt = 0, edcnt = 0, midcnt = 0, inmemCnt;
		double[] runpara = { 0.0, 0.0 };

		
		//...........para load...........................//
		paraLoad();
		//..................................................//
		
		
		// ..test..//
		double tmp = 0.0;

		stcnt = System.nanoTime();

		int resnum = kviCol.intervalSearch_resMrkvi(parallel, l, r, sign);
		kviMRproCol.numMapIn = 0;
		inmemCnt = System.nanoTime();

	
		//...............test...................//
		System.out.printf("+++++++  root:  %f   range:  %s   %s \n", kviCol.sroot,parallel[0],parallel[1]);
		
		//......................................//
	
		
		//...................invoke MapReduce query processing..........................//
		
		ProcessBuilder pb = new ProcessBuilder(
				"./comp.sh", idxtabname,
				Integer.toString(sign), "range",Double.toString(l),Double.toString(r),parallel[0],parallel[1]);

		 File executorDirectory = new File("/home/guo/xgsn/kviMRoper/");           
		   
		 pb.directory(executorDirectory);
		 Process p = pb.start();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    String line;

	    System.out.println("Output of running is: ");
	    while ((line = br.readLine()) != null) {
	        System.out.println(line);
	    }
		
	    //.............................................................................//

		midcnt = System.nanoTime();

		edcnt = System.nanoTime();

		// ......metric and running time record............//
		numtup = tmp;
		tduration = Math.max(midcnt - stcnt, edcnt - stcnt);
		inmem = inmemCnt - stcnt;
		mracc = midcnt - inmemCnt;
		randacc = edcnt - midcnt;
		schlevel = resnum;

		// strk = parallel[0];
		// edrk = parallel[1];
		mapout = runpara[0];
		mapcnt = runpara[1];

		return true;
	}

	public double kvi_timeCnt() {
		return tduration / 1000000000.0;
	}

	public void kvi_metricOutput(double stat[], String rowrange[]) {
		stat[0] = tduration / 1000000000.0;
		stat[1] = inmem / 1000000000.0;
		stat[2] = randacc / 1000000000.0;
		stat[3] = mracc / 1000000000.0;
		stat[4] = numtup;
		stat[5] = schlevel;

		stat[6] = mapout;
		stat[7] = mapcnt;

		rowrange[0] = tupRangeL;
		rowrange[1] = tupRangeR;
		return;
	}

	public void kvi_metricOutput(double stat[]) {
		stat[0] = tduration / 1000000000.0;
		stat[1] = numtup;
		return;
	}

	// .....................................................................//
	public int paraOut(double para[]) {// change to kviCol class
		double[] param = new double[100];
		int num = kviCol.paraOutput(param);
		for (int i = 0; i < num; ++i) {
			para[i] = param[i];
		}
		return num;
	}

	public void paraSetup(int num, double para[]) {
		kviCol.paramConf(num, para);
		return;
	}

}
