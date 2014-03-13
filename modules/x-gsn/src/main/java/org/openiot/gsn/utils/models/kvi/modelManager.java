package gsn.utils.models.kvi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

//import basetool.MRScan;
//import basetool.filterHbase;
//import basetool.filterScan;
//import kviMRproCol;
//import kviManager;
//import basetool.rawQuery;

public class modelManager {

	// public double qrysel = 1;

	public kviManager kviTemp;// =new kviManager("tempIdxmod");
	public kviManager kviVal;// =new kviManager("valIdxmod");

	// ..................current model information...//

	double csum, cmax, cmin, stpoint, edpoint;
	int cnt;

	int modelOrder = 0;

	double[] modinfor = new double[modelOrder + 5];

	String[] vqual = { "vl", "vr" };
	String[] tqual = { "st", "ed" };

	double[] tmpT = { 0.0, 0.0 };

	double errtimes = 4.0;

	// ..............................................//

	public modelManager(String tsid){
		// rawn = 0;
		// segn = 0;
		//
		// precmx = precmi = 0;
		// prebkmx = prebkmi = 0;
		// presegsum = 0.0;
		// presegpcnt = 0;
		// preBegStmp = new String();
		//
		// loadini = 1;
		// isend = 0;
		//
		// errpre = 0.1;
		//
		// sttime = edtime = 0.0;
		// maxval = minval = 0.0;

		// if (format == true) {

		// fhb.formatHbase();

		try {
			kviTemp = new kviManager(tsid+"Time", 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			kviVal = new kviManager(tsid+"Val", 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		csum = 0.0;
		cmax = 0.0;
		cmin = 0.0;

		stpoint = 0.0;
		edpoint = 0.0;

		cnt = 1;

		// test
		// String str1 = kviTemp.idxtabname;

		// } else {
		// kviTemp = new kviManager("tempIdxmodCol", false, 0);
		//
		// // // test
		// // String str = kviTemp.idxtabname;
		//
		// kviVal = new kviManager("valIdxmodCol", false, 1);
		//
		// }

		// scanfilter = new filterScan();
		// mrscanner = new MRScan(0);
		// rawscanner = new rawQuery();
		// mrtest = new MRtest1(0);
		// filterHb = new filterHbase(0);
		//
		// // filter=new filterHbase(0);
		// ministamp = new String();
		// maxstamp = new String();
		//
		// stcnt = 0;
		// edcnt = 0;

		//
	}

	// public void tiExp(int num) throws Exception {
	// double[] inte = new double[10];
	// // comCheck();
	// double[] tmpstat = new double[15];
	// double[][] stat = new double[15][10];
	// String[] tuprange = new String[15];
	//
	// int tnum = 0;
	// double qualnum = 0.0;
	//
	// // textRecordExp_clear("./result/tiExpMapT.txt");
	//
	// String[] qual = { "st", "ed" };
	// qrysel = 0.1;
	//
	// for (int i = 0; i < num; ++i) {
	//
	// if (i % 10 == 0) {
	// qrysel = qrysel + 0.1;
	// }
	//
	// // qryGen_tempRange(inte);
	// tnum = 0;
	//
	// System.out.printf("%f    %f\n", inte[0], inte[1]);
	//
	// // kviTemp.kvi_rangeSearch(inte[0], inte[1], qual,0);
	// // kviTemp.kvi_metricOutput(tmpstat, tuprange);
	// // stat[tnum][0] = tmpstat[0] - 1.0; // tdur
	// // stat[tnum][1] = tmpstat[1];// inmem
	// // stat[tnum][2] = tmpstat[2];// randacc
	// // stat[tnum][3] = tmpstat[3];// mracc
	// // stat[tnum][4] = tmpstat[4];// # tuples
	// // stat[tnum][5] = tmpstat[5];// search level
	// // stat[tnum][6] = tmpstat[6];// # map output
	// // stat[tnum++][7] = tmpstat[7];// #map
	//
	// // System.out.printf("%f  \n", kviTemp.kvi_timeCnt());
	//
	// // kviTemp.conve_kvi_rangeSearchConsec(inte[0], inte[1], qual,0);
	// // kviTemp.kvi_metricOutput(tmpstat);
	// // stat[tnum][0] = tmpstat[0];
	// // stat[tnum++][1] = tmpstat[1];
	// // System.out.printf("%f  \n", kviTemp.kvi_timeCnt());
	//
	// // MRScan.gTabNum = 0;
	// // startCnt();
	// // qualnum = mrscanner.jobTempRange(inte[0], inte[1], "tempIdxmodCol");
	// // stat[tnum][0] = stopCnt();
	// // stat[tnum++][1] = 100;
	// //
	// // startCnt();
	// // stat[tnum][1] = filterHb.rangeQuery("tempIdxmodCol", inte[0],
	// // inte[1], "attri", qual);
	// // stat[tnum++][0] = stopCnt();
	//
	// // startCnt();
	// // stat[tnum][1] = rawscanner.rangeQryTemp(inte[0], inte[1],
	// // "tempRaw_guo");
	// // stat[tnum++][0] = stopCnt();
	//
	// // stat[tnum][0] = kviMRproCol.numMapIn;
	// // stat[tnum++][1] = qualnum;
	//
	// // for no-gridding process
	// // kviTemp.kvi_rangeSearchNoGrid(inte[0], inte[1], qual);
	// // kviTemp.kvi_metricOutput(tmpstat, tuprange);
	// // stat[tnum++][0] = tmpstat[0] - 1.0; // tdur
	// // System.out.printf("%f  \n", kviTemp.kvi_timeCnt());
	//
	// // textRecordExp("./result/tiExpMapT.txt", stat, tuprange, tnum,
	// // inte[0], inte[1]);
	// }
	//
	// return;
	// }
	//
	// public void tpExp(int num) throws Exception {
	// double[] inte = new double[10];
	// // comCheck();
	//
	// double[] tmpstat = new double[15];
	// double[][] stat = new double[15][10];
	// String[] tuprange = new String[15];
	//
	// int tnum = 0;
	// double qualnum = 0.0;
	// String[] qual = { "st", "ed" };
	//
	// // textRecordExp_clear("./result/tpExpRaw1.txt");
	// // fhb.creTab("tempM1_guoRes");
	//
	// for (int i = 0; i < num; ++i) {
	// // qryGen_tempPoint(inte);
	// tnum = 0;
	//
	// System.out.printf("%f    \n", inte[0]);
	//
	//
	// // kviTemp.kvi_pointSearch(inte[0], qual, 0);
	// // kviTemp.kvi_metricOutput(tmpstat);
	// // stat[tnum][0] = tmpstat[0];
	// // stat[tnum++][1] = tmpstat[1]; // tdur
	//
	// // kviTemp.kvi_pointSearch(inte[0], qual, 0);
	// // // kviTemp.kvi_metricOutput(tmpstat, tuprange);
	// // stat[tnum][0] = tmpstat[0]; // tdur
	// // stat[tnum++][1] = tmpstat[4];// # tuples
	// //
	// // System.out.printf("%f  \n", kviTemp.kvi_timeCnt());
	// //
	// // startCnt();
	// // // qualnum = mrscanner.jobTempPoint(inte[0], "tempIdxmodCol");
	// // stat[tnum][0] = stopCnt();
	// // stat[tnum++][1] = 100;
	// //
	// // startCnt();
	// // // stat[tnum][1] = filterHb.pointQuery("tempIdxmodCol", inte[0],
	// // // "attri", qual);
	// // stat[tnum++][0] = stopCnt();
	// //
	//
	//
	// // stat[tnum][0] = kviMRproCol.numMapIn;// from kvi
	// // stat[tnum++][1] = qualnum;// from pure mapreduce
	//
	// // startCnt();
	// // stat[tnum][1] = rawscanner.pointQryTemp(inte[0], "tempRaw_guo");
	// // stat[tnum++][0] = stopCnt();
	//
	// // textRecordExp("./result/tpExpRaw1.txt", stat, tuprange, tnum,
	// // inte[0]);
	// }
	//
	// return;
	// }

	public void kviSegLoad(double errperc, double sval, double timestamp) throws IOException {
		double terr = 0.0, tmpmax = 0.0, tmpmin = 0.0;

		if (sval > cmax) {
			tmpmax = sval;
		} else if (sval < cmin) {
			tmpmin = sval;
		}

		terr = errperc * (double) (csum + sval) / (cnt + 1);

		if (Math.abs(tmpmax - tmpmin) > Math.abs(errtimes * terr)) {

			modinfor[0] = (double) csum / cnt;

			kviTemp.kvi_insert(stpoint, edpoint, modinfor, 0, cmin, cmax, tmpT,
					tqual, vqual);

			kviVal.kvi_insert(cmin, cmax, modinfor, 0, stpoint, edpoint, tmpT,
					vqual, tqual);

			csum = sval;
			cnt = 1;
			cmax = sval;
			cmin = sval;
			// tmpmax=cmax;
			// tmpmin=cmin;

			stpoint = timestamp-1;
			edpoint = timestamp;
		} else {
			cmax = tmpmax;
			cmin = tmpmin;
			csum += sval;
			cnt++;

			edpoint = timestamp;
		}
		return;
	}

	public void viQry(double vl, double vr) throws Exception {
		double[] inte = new double[10];

		// comCheck();
		double[] tmpstat = new double[15];
		double[][] stat = new double[15][10];
		String[] tuprange = new String[15];

		int tnum = 0;
		// double qualnum = 0.0;
		String[] qual = { "vl", "vr" };

		// textRecordExp_clear("./result/viExpMrkHs2.txt");

		// qrysel = 0.0;
		// for (int i = 0; i < num; ++i) {

		// if (i % 10 == 0) {
		// qrysel = qrysel + 0.05;
		// }

		// qryGen_valRange(inte);
		tnum = 0;

		System.out.printf("%f    %f\n", vl, vr);

		kviVal.kvi_rangeSearchMrkvi(vl, vr, qual, 1);
		kviVal.kvi_metricOutput(tmpstat, tuprange);
		stat[tnum][0] = tmpstat[0]; // tdur
		stat[tnum][1] = tmpstat[1];// inmem
		stat[tnum][2] = tmpstat[2];// randacc
		stat[tnum][3] = tmpstat[3];// mracc
		stat[tnum][4] = tmpstat[4];// # tuples
		stat[tnum][5] = tmpstat[5];// search level

		stat[tnum][6] = tmpstat[6];// map out
		stat[tnum++][7] = tmpstat[7];// map cnt

		System.out.printf("MRK %f  \n", kviVal.kvi_timeCnt());

		// textRecordExpKVIMRK("./result/viExpMrkHs2.txt", stat, tuprange, tnum,
		// inte[0], inte[1]);
		// }

		return;
	}

	public void vpQry(double vp) throws Exception {
		double[] inte = new double[10];
		// comCheck();

		double[] tmpstat = new double[15];
		double[][] stat = new double[15][10];
		String[] tuprange = new String[15];

		int tnum = 0;
		// double qualnum = 0.0;

		// textRecordExp_clear("./result/vpExpMrk.txt");
		// fhb.creTab("tempM1_guoRes");
		String[] qual = { "vl", "vr" };

		// for (int i = 0; i < num; ++i) {
		// qryGen_valPoint(inte);
		tnum = 0;

		System.out.printf("%f    \n", vp);

		kviVal.kvi_pointSearch_mr(vp, qual, 1);
		kviVal.kvi_metricOutput(tmpstat);
		stat[tnum++][0] = tmpstat[0]; // tdur

		System.out.printf("%f  \n", kviVal.kvi_timeCnt());

		// textRecordExp("./result/vpExpMrk.txt", stat, tuprange, tnum,
		// inte[0]);
		// }
		return;
	}

	public void tiQry(double tl, double tr) throws Exception {
		double[] inte = new double[10];

		// comCheck();
		double[] tmpstat = new double[15];
		double[][] stat = new double[15][10];
		String[] tuprange = new String[15];

		int tnum = 0;
		// double qualnum = 0.0;
		String[] qual = { "tl", "tr" };

		// textRecordExp_clear("./result/viExpMrkHs2.txt");

		// qrysel = 0.0;
		// for (int i = 0; i < num; ++i) {

		// if (i % 10 == 0) {
		// qrysel = qrysel + 0.05;
		// }

		// qryGen_valRange(inte);
		tnum = 0;

		System.out.printf("%f    %f\n", tl, tr);

		kviTemp.kvi_rangeSearchMrkvi(tl, tr, qual, 0);
		kviTemp.kvi_metricOutput(tmpstat, tuprange);
		stat[tnum][0] = tmpstat[0]; // tdur
		stat[tnum][1] = tmpstat[1];// inmem
		stat[tnum][2] = tmpstat[2];// randacc
		stat[tnum][3] = tmpstat[3];// mracc
		stat[tnum][4] = tmpstat[4];// # tuples
		stat[tnum][5] = tmpstat[5];// search level

		stat[tnum][6] = tmpstat[6];// map out
		stat[tnum++][7] = tmpstat[7];// map cnt

		System.out.printf("MRK %f  \n", kviVal.kvi_timeCnt());

		// textRecordExpKVIMRK("./result/viExpMrkHs2.txt", stat, tuprange, tnum,
		// inte[0], inte[1]);
		// }

		return;
	}

	public void tpQry(double tp) throws Exception {
		double[] inte = new double[10];
		// comCheck();

		double[] tmpstat = new double[15];
		double[][] stat = new double[15][10];
		String[] tuprange = new String[15];

		int tnum = 0;
		// double qualnum = 0.0;

		// textRecordExp_clear("./result/vpExpMrk.txt");
		// fhb.creTab("tempM1_guoRes");
		String[] qual = { "tl", "tr" };

		// for (int i = 0; i < num; ++i) {
		// qryGen_valPoint(inte);
		tnum = 0;

		System.out.printf("%f    \n", tp);

		kviTemp.kvi_pointSearch_mr(tp, qual, 0);
		kviTemp.kvi_metricOutput(tmpstat);
		stat[tnum++][0] = tmpstat[0]; // tdur

		System.out.printf("Process time: %f  \n", kviVal.kvi_timeCnt());

		// textRecordExp("./result/vpExpMrk.txt", stat, tuprange, tnum,
		// inte[0]);
		// }
		return;
	}

	// ....................................old
	// version...........................//
	public void viExp(int num) throws Exception {
		double[] inte = new double[10];

		// comCheck();
		double[] tmpstat = new double[15];
		double[][] stat = new double[15][10];
		String[] tuprange = new String[15];

		int tnum = 0;
		double qualnum = 0.0;
		String[] qual = { "vl", "vr" };

		// textRecordExp_clear("./result/viExpMrkHs2.txt");

		// qrysel = 0.0;
		for (int i = 0; i < num; ++i) {

			// if (i % 10 == 0) {
			// qrysel = qrysel + 0.05;
			// }

			// qryGen_valRange(inte);
			tnum = 0;

			System.out.printf("%f    %f\n", inte[0], inte[1]);

			kviVal.kvi_rangeSearchMrkvi(inte[0], inte[1], qual, 1);
			kviVal.kvi_metricOutput(tmpstat, tuprange);
			stat[tnum][0] = tmpstat[0]; // tdur
			stat[tnum][1] = tmpstat[1];// inmem
			stat[tnum][2] = tmpstat[2];// randacc
			stat[tnum][3] = tmpstat[3];// mracc
			stat[tnum][4] = tmpstat[4];// # tuples
			stat[tnum][5] = tmpstat[5];// search level

			stat[tnum][6] = tmpstat[6];// map out
			stat[tnum++][7] = tmpstat[7];// map cnt

			System.out.printf("MRK %f  \n", kviVal.kvi_timeCnt());

			// textRecordExpKVIMRK("./result/viExpMrkHs2.txt", stat, tuprange,
			// tnum,
			// inte[0], inte[1]);
		}

		return;
	}

	public void vpExp(int num) throws Exception {
		double[] inte = new double[10];
		// comCheck();

		double[] tmpstat = new double[15];
		double[][] stat = new double[15][10];
		String[] tuprange = new String[15];

		int tnum = 0;
		double qualnum = 0.0;

		// textRecordExp_clear("./result/vpExpMrk.txt");
		// fhb.creTab("tempM1_guoRes");
		String[] qual = { "vl", "vr" };

		for (int i = 0; i < num; ++i) {
			// qryGen_valPoint(inte);
			tnum = 0;

			System.out.printf("%f    \n", inte[0]);

			kviVal.kvi_pointSearch_mr(inte[0], qual, 1);
			kviVal.kvi_metricOutput(tmpstat);
			stat[tnum++][0] = tmpstat[0]; // tdur

			System.out.printf("%f  \n", kviVal.kvi_timeCnt());

			// textRecordExp("./result/vpExpMrk.txt", stat, tuprange, tnum,
			// inte[0]);
		}
		return;
	}

}
