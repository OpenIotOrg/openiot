package gsn.utils.models.kvi;

import java.io.*;
import java.util.*;

public class kviIndexCol {

	public HBaseOp idxop = new HBaseOp();
	public String tabname;
	public double sroot, sdel, regnode;
	public int maxh;
	public String prekey;
	String[] keys = new String[3];

	// ..........lca query...........//
	double stval = 1269039600.0;
	double[] stvalpath = new double[50];

	double edval = 1597971100.0;// 1597968896.0;
	double[] edvalpath = new double[50];

	int stcnt, edcnt;

	double vlval = 1.0;
	double[] vlvalpath = new double[50];

	double vrval = 680.0;
	double[] vrvalpath = new double[50];

	int vlcnt, vrcnt;

	// ............................//

	public kviIndexCol(String idxname) {
		tabname = idxname;

		regnode = 0.0;

		sroot = 8.0;
		sdel = 4.0;

		maxh = 100;
		prekey = "abcdefghijklmnopqrstuvwxyz";

		stvalpath = new double[50];
		stcnt = 0;

		edvalpath = new double[50];
		edcnt = 0;
		// stPathRecord();

		return;
	}

	public void iniIdx() throws IOException {

		// modification

		idxop.delTab(tabname);
		idxop.creTab(tabname);
		idxop.iniTabOperation(tabname);

		idxop.delTab(tabname + "Res");
		idxop.creTab(tabname + "Res");

		return;
	}

	// public void iniQueryRes() {
	// idxop.delTab(tabname + "Res");
	// idxop.creTab(tabname + "Res");
	// }

	public void insert_rootAdj(double l, double r, double para[])// para[0]:
																	// root
																	// para[1]:del
	{
		double tmp = Math.log10(r + 1) / Math.log10(2); // search starting point
														// optimization
		int h = (int) tmp + 1;
		para[0] = (Math.pow(2.0, h)) / 2.0;
		para[1] = Math.pow(2.0, h - 2);

		return;
	}

	public boolean insert(double l, double r, double modinfo[], int order,
			double assocl, double assocr, double timecnt[], String lrQual[],
			String assoQual[]) {// timecnt: 0
		// search time,1
		// insertion
		// time

		double para[] = new double[2];
		long regval = 0;
		double tl = l, tr = r;

		long st = 0, mid = 0, ed = 0;
		st = System.nanoTime();

		if (Math.abs(r - 1) < 1) {

			int ttl = (int) l, ttr = (int) r; // optimization special case: when
												// |tr-tl|<1
			if ((ttl & 1) == ttl) {
				tl = ttl;
			} else if ((ttr & 1) == ttr) {
				tr = ttr;
			}
			// tl=(int)l; tr=(int)r+1; // comparison method
		}

		if (dynaIniSchT(tl, tr, para) == true) // optimization, needs proof
		{
			if (tl <= sroot) {
				regval = (long) sroot;
			} else {
				regval = (long) findRegsVal(tl, tr, para[0], para[1]);
			}
		} else {

			insert_rootAdj(tl, tr, para);
			// System.out.print("starting node:"+Double.toString(para[0])+"    ");

			regval = (long) findRegsVal(tl, tr, para[0], para[1]);
		}
		mid = System.nanoTime();

		rkeyCon(regval, l, r, assocl, assocr, keys);
		String rkey = regvalKeyCon(regval) + "," + "0";

		if (idxop.get(tabname, rkey, "model", "coef0") == true)

		{
			rkey = keys[0];// subkey = keys[1];

			// idxop.put(tabname, rkey, "attri", "primIntev", Double.toString(l)
			// + "," + Double.toString(r) + "," + Double.toString(assocl)
			// + "," + Double.toString(assocr));

		} else {

			// idxop.put(tabname, rkey, "attri", "primIntev", Double.toString(l)
			// + "," + Double.toString(r) + "," + Double.toString(assocl)
			// + "," + Double.toString(assocr));

		}
		idxop.put(tabname, rkey, "attri", lrQual[0], Double.toString(l));
		idxop.put(tabname, rkey, "attri", lrQual[1], Double.toString(r));
		idxop.put(tabname, rkey, "attri", assoQual[0], Double.toString(assocl));
		idxop.put(tabname, rkey, "attri", assoQual[1], Double.toString(assocr));
		// for (int i = 0; i < order + 1; ++i) {

		idxop.put(tabname, rkey, "model", "coef" + Integer.toString(0),
				Double.toString(modinfo[0]));
		// }

		// .....in memory VS IO........//
		ed = System.nanoTime();
		regnode = (double) regval;
		timecnt[0] = (mid - st) / 1000000000.0;
		timecnt[1] = (ed - mid) / 1000000000.0;

		return true;
	}

	public String regvalKeyCon(long regval) {
		long tmp = regval;
		int num = 0;
		while (tmp != 0) {
			tmp = tmp / 10;
			num++;
		}
		if (num == 0)
			num = 1;
		return prekey.charAt(num - 1) + "," + Long.toString(regval);

	}

	public void rkeyCon(long regval, double l, double r, double assocl,
			double assocr, String[] rkeys) {// [0]: row key, [1]: sub key

		rkeys[0] = regvalKeyCon(regval) + "," + regvalKeyCon((long) l) + ","
				+ regvalKeyCon((long) r) + "," + regvalKeyCon((long) assocl)
				+ "," + regvalKeyCon((long) assocr);

		// String strreg = regvalKeyCon(regval), strl = regvalKeyCon((long) l),
		// strr = regvalKeyCon((long) r);
		// String strassocl = regvalKeyCon((long) assocl), strassocr =
		// regvalKeyCon((long) assocr);
		//
		// rkeys[0] = strreg + "," + strl + "," + strr + "," + strassocl + ","
		// + strassocr;

		// rkeys[1] = strl + "," + strr + "," + strassocl + "," + strassocr;
		return;
	}

	public boolean dynaIniSchT(double l, double r, double para[]) // para[0]
																	// root,
																	// para[1]
																	// del
	// true: extend the root space; Otherwise:the same
	{

		// consider the case when value interval is in the negative space
		if (r <= 0) {
			l = -1 * l;
			r = -1 * r;
		}
		long tr = 0;
		tr = (long) r;

		para[0] = sroot;
		para[1] = sdel;

		if (tr > (sroot * 2 - 1)) {
			double tmp = Math.log10(tr + 1) / Math.log10(2);
			long h = (long) tmp + 1;
			// tmp = Math.pow(2.0, h);
			sroot = (Math.pow(2.0, h)) / 2.0;
			sdel = Math.pow(2.0, h - 2);

			// sroot = sroot * 2;
			// sdel = sdel * 2;
			para[0] = sroot;
			para[1] = sdel;

			if (r <= 0) {
				para[0] = -1 * para[0];
				para[1] = -1 * para[1];
			}
			// if (r > sroot) {
			// sroot = r;
			// sdel = para[1];
			return true;
			// }
		}
		return false;
	}

	public double findRegsVal(double l, double r, double root, double del) {

		double tmpr = root;
		// int sign = 0;

		while (del >= 0.5) {

			if (r < tmpr) {
				tmpr -= del;

			} else if (l > tmpr) {
				tmpr += del;

			} else {
				break;

			}
			del = del / 2.0;
		}
		return tmpr;
	}

	public double boundSearch(double val, double regpnt, double regdel, int sign)// sign
																					// 0:
																					// left
																					// 1:right
	{
		double exvall = sroot * 2 + 2, exvalr = 0.0;
		double cur = regpnt, del = regdel;
		while (del >= 1) {

			if (cur < exvall)
				exvall = cur;
			if (cur > exvalr)
				exvalr = cur;

			if (Math.abs(val - cur) <= 1e-2) {
				break;
			} else if (val > cur) {
				cur += del;

				if (sign == 0)
					break; // search optimization

			} else {
				cur -= del;

				if (sign == 1)
					break; // search optimization
			}
			// System.out.printf("%f  %f\n", cur, del);
			del /= 2.0;

			// if(del==2)
			// del=2;

		}
		if (cur < exvall)
			exvall = cur;
		if (cur > exvalr)
			exvalr = cur;

		if (sign == 0)
			return exvall;
		else
			return exvalr;
	}

	public int intervalSearch(double l, double r, double res[]) {

		double cur = sroot, del = sdel;

		// .......test.............//
		// System.out.printf("%f  %f\n", cur, del);
		// ........................//

		int pathcnt = 0;
		while (del >= 1) {
			res[pathcnt++] = cur;
			// if (l <= cur && cur <= r) {
			// break;
			if (r < cur) {
				cur -= del;
			} else if (l > cur) {
				cur += del;
			} else {
				// del /= 2.0;
				break;
			}
			del /= 2.0;
		}

		double lbound = boundSearch(l, cur, del, 0);
		double rbound = boundSearch(r, cur, del, 1);
		res[pathcnt - 1] = lbound;

		res[pathcnt++] = rbound;

		return pathcnt;
	}

	public double boundSearchlca(double val, double regpnt, double regdel,
			int sign, int dep)// sign
	// 0:
	// left
	// 1:right
	{
		double exvall = sroot * 2 + 2, exvalr = 0.0;
		double cur = regpnt, del = regdel;

		double lcal = 0.0, lcar = 0.0;
		int islcal = 0, islcar = 0;

		while (del >= 1) {

			if (cur < exvall)
				exvall = cur;
			if (cur > exvalr)
				exvalr = cur;

			if (stvalpath[dep] == cur) {
				lcal = cur;
				islcal = 1;

				// .....modification..........//
				exvall = Math.max(lcal, exvall);

			}
			if (edvalpath[dep] == cur) {
				lcar = cur;
				islcar = 1;

				// .....modification..........//
				exvalr = Math.min(lcar, exvalr);
			}

			if (Math.abs(val - cur) <= 1e-2) {
				break;
			} else if (val > cur) {
				cur += del;

				// if (sign == 0)
				// break; // search optimization

			} else {
				cur -= del;

				// if (sign == 1)
				// break; // search optimization
			}
			// System.out.printf("%f  %f\n", cur, del);
			del /= 2.0;

			// if(del==2)
			// del=2;

		}
		if (cur < exvall)
			exvall = cur;
		if (cur > exvalr)
			exvalr = cur;

		// .......modification......//
		// if (sign == 0 && islcal==1)
		// exvall= Math.max(exvall, lcal);
		// else if (sign == 1 && islcar==1)
		// exvalr=Math.min(exvalr, lcar);
		// ........................//

		if (sign == 0)
			return exvall;
		else
			return exvalr;
	}

	public int intervalSearchT(double l, double r, double res[]) {

		double cur = sroot, del = sdel;

		// long st=System.nanoTime();
		// test//
		// System.out.printf("%f  %f\n", cur, del);

		// double path[] = new double[maxh];
		double lcal = 0.0, lcar = 0.0;
		double midl = 0.0, midr = 0.0;

		double lb = 10000000000.0, rb = 0.0;
		int pathcnt = 0;
		while (del >= 1) {

			res[pathcnt] = cur;
			if (stvalpath[pathcnt] == cur) {
				lcal = cur;
			}

			if (edvalpath[pathcnt] == cur) {
				lcar = cur;
			}

			pathcnt++;

			if (cur > rb)
				rb = cur;
			if (cur < lb)
				lb = cur;

			if (r < cur) {
				cur -= del;
			} else if (l > cur) {
				cur += del;
			} else {
				// del /= 2.0;
				break;
			}
			del /= 2.0;
		}

		double lbound = boundSearchlca(l, cur, del, 0, pathcnt);
		double rbound = boundSearchlca(r, cur, del, 1, pathcnt);

		midl = Math.max(lcal, lb);
		midr = Math.min(lcar, rb);
		// res[pathcnt - 1] = lbound;
		// // res[pathcnt++] = lbound;
		// res[pathcnt++] = rbound;

		// long ed=System.nanoTime();
		// tduratoin=(ed-st)

		res[0] = Math.min(lbound, midl);
		res[1] = Math.max(rbound, midr);

		return pathcnt;
	}

	public int intervalSearchV(double l, double r, double res[]) {

		double cur = sroot, del = sdel;

		// long st=System.nanoTime();
		// test//
		// System.out.printf("%f  %f\n", cur, del);

		// double path[] = new double[maxh];
		double lcal = 0.0, lcar = 0.0;

		double lb = 10000000000.0, rb = 0.0;
		int pathcnt = 0;
		while (del >= 1) {

			res[pathcnt] = cur;
			if (vlvalpath[pathcnt] == cur) {
				lcal = cur;
			}

			if (vrvalpath[pathcnt] == cur) {
				lcar = cur;
			}

			pathcnt++;

			if (cur > rb)
				rb = cur;
			if (cur < lb)
				lb = cur;

			if (r < cur) {
				cur -= del;
			} else if (l > cur) {
				cur += del;
			} else {
				// del /= 2.0;
				break;
			}
			del /= 2.0;
		}

		// double lbound = boundSearch(l, cur, del, 0);
		// double rbound = boundSearch(r, cur, del, 1);
		// res[pathcnt - 1] = lbound;
		// // res[pathcnt++] = lbound;
		// res[pathcnt++] = rbound;

		// long ed=System.nanoTime();
		// tduratoin=(ed-st)

		res[0] = Math.max(lcal, lb);
		res[1] = Math.min(lcar, rb);

		return pathcnt;
	}

	public int intervalSearch_res(String[][] indi, String[][] parallel,
			double l, double r) {
		double[] res = new double[maxh * 2];
		int resnum = intervalSearch(l, r, res);

		String prekey = "";

		for (int i = 0; i < resnum - 2; ++i) {
			// indi[i]=res[i];
			prekey = prekey_cons((long) res[i]);
			// tmp = (long) res[i];
			indi[i][0] = prekey + "," + "0";
			indi[i][1] = prekey_cons((long) (res[i]) + 1) + "," + "0";

			// st = idxop.getQual(tabBdname, Long.toString(tmp), "attri",
			// "upper");
			// if (st != "noqual") {
			// indi[i][0] = prekey + st;
			// }
			// ed = idxop.getQual(tabBdname, Long.toString(tmp), "attri",
			// "lower");
			// if (st != "noqual") {
			// indi[i][1] = prekey + ed;
			// } else {
			// indi[i][1] = indi[i][0];
			// }

		}
		for (int i = resnum - 2; i < resnum; ++i) {
			prekey = prekey_cons((long) res[i]);
			// tmp = (long) res[i];

			parallel[i - (resnum - 2)][0] = prekey + ",0";
			parallel[i - (resnum - 2)][1] = prekey_cons((long) (res[i]) + 1)
					+ "," + "0";

			// st = idxop.getQual(tabBdname, Long.toString(tmp), "attri",
			// "upper");
			// if (st != "noqual") {
			// parallel[i - (resnum - 2)][0] = prekey + st;
			// }
			// ed = idxop.getQual(tabBdname, Long.toString(tmp), "attri",
			// "lower");
			// if (st != "noqual") {
			// parallel[i - (resnum - 2)][1] = prekey + ed;
			// } else {
			// parallel[i - (resnum - 2)][1] = parallel[i - (resnum - 2)][0];
			// }
		}
		return resnum - 2;
	}

	public int intervalSearch_resMrkvi(String[] bound, double l, double r,
			int sign) {
		double[] res = new double[maxh * 2];
		int resnum = intervalSearch(l, r, res);

		String prekey = "";

		double glbd = 0.0, grbd = 0.0;

		if (sign == 0) {
			glbd = stval;
			grbd = edval;
		} else {
			glbd = vlval;
			grbd = vrval;
		}

		// ....test.......................//
		// System.out.printf("search path: ");

		double lbd = res[resnum - 2], rbd = res[resnum - 1];
		for (int i = 0; i < resnum - 2; ++i) {

			if (res[i] >= glbd) {
				lbd = Math.min(res[i], lbd);
			}
			if (res[i] <= grbd) {
				rbd = Math.max(res[i], rbd);
			}

			// ....test.......................//
			// System.out.printf("%f   ", res[i]);

		}

		// ....test.......................//
		// System.out.printf("\n");

		bound[0] = prekey_cons((long) (lbd)) + "," + "0";
		bound[1] = prekey_cons((long) (rbd) + 1) + "," + "0";
		return resnum;
	}

	// public void stedPathRecord() {
	// double cur = sroot, del = sdel;
	// stcnt = 0;
	// while (del >= 1) {
	//
	// stvalpath[stcnt++] = cur;
	//
	// // ..test...//
	// // System.out.printf("st search %f  %f\n", sroot, cur);
	// // ........//
	//
	// if (stval < cur) {
	// cur -= del;
	// } else if (stval > cur) {
	// cur += del;
	// } else {
	// // del /= 2.0;
	// break;
	// }
	// del /= 2.0;
	// }
	//
	// cur = sroot;
	// del = sdel;
	// edcnt = 0;
	//
	// while (del >= 1) {
	// edvalpath[edcnt++] = cur;
	//
	// // ..test...//
	// // System.out.printf("ed search  %f  %f\n", sroot, cur);
	// // ........//
	//
	// if (edval < cur) {
	// cur -= del;
	// } else if (edval > cur) {
	// cur += del;
	// } else {
	// // del /= 2.0;
	// break;
	// }
	// del /= 2.0;
	// }
	// return;
	// }

	public int intervalSearch_resTest(String[] bound, double l, double r,
			int sign) {
		double[] res = new double[maxh * 2];

		int resnum = 0;

		if (sign == 0) {
			resnum = intervalSearchT(l, r, res);

		} else {

			resnum = intervalSearchV(l, r, res);
		}

		String prekey = "";

		bound[0] = prekey_cons((long) res[0]) + ",0";
		bound[1] = prekey_cons((long) res[1] + 1) + ",0";

		return resnum;
	}

	public void vlvrPathRecord() {
		double cur = sroot, del = sdel;
		while (del >= 1) {

			vlvalpath[vlcnt++] = cur;

			// ..test...//
			// System.out.printf("%f  %f\n",sroot, cur);
			// ........//

			if (vlval < cur) {
				cur -= del;
			} else if (vlval > cur) {
				cur += del;
			} else {
				// del /= 2.0;
				break;
			}
			del /= 2.0;
		}

		cur = sroot;
		del = sdel;
		vrcnt = 0;

		// ..test...//
		// System.out.printf("%f  %f\n",sroot, cur);
		// ........//

		while (del >= 1) {
			vrvalpath[vrcnt++] = cur;
			if (vrval < cur) {
				cur -= del;
			} else if (vrval > cur) {
				cur += del;
			} else {
				// del /= 2.0;
				break;
			}
			del /= 2.0;
		}
		return;
	}

	public int pointSearch(double val, double res[]) {

		double cur = sroot, del = sdel;
		int pathcnt = 0;

		double extrl = sroot, extrr = 0.0;

		while (del >= 0.5) {

			if (cur < extrl)
				extrl = cur;
			if (cur > extrr)
				extrr = cur;

			res[pathcnt++] = cur;
			// if (l <= cur && cur <= r) {
			// break;
			if (val < cur) {
				cur -= del;
			} else if (val > cur) {
				cur += del;
			} else if (Math.abs(val - cur) <= 1e-2) {
				break;
			}
			del /= 2.0;
		}

		if (cur < extrl)
			extrl = cur;
		if (cur > extrr)
			extrr = cur;
		return pathcnt;
	}

	public int pointSearchT(double val, double res[]) {

		double cur = sroot, del = sdel;
		int pathcnt = 0;

		double lcal = 0.0, lcar = 0.0;
		double midl = 0.0, midr = 0.0;

		double lb = 10000000000.0, rb = 0.0;

		while (del >= 1) {

			res[pathcnt] = cur;
			if (stvalpath[pathcnt] == cur) {
				lcal = cur;
			}

			if (edvalpath[pathcnt] == cur) {
				lcar = cur;
			}

			pathcnt++;

			if (cur > rb)
				rb = cur;
			if (cur < lb)
				lb = cur;

			if (val < cur) {
				cur -= del;
			} else if (val > cur) {
				cur += del;
			} else {
				// del /= 2.0;
				break;
			}
			del /= 2.0;
		}

		// midl = Math.max(lcal, lb);
		// midr = Math.min(lcar, rb);
		// res[pathcnt - 1] = lbound;
		// // res[pathcnt++] = lbound;
		// res[pathcnt++] = rbound;

		// long ed=System.nanoTime();
		// tduratoin=(ed-st)

		res[0] = Math.max(lb, lcal);
		res[1] = Math.min(rb, lcar);

		return pathcnt;
	}

	public String prekey_cons(long regval) {
		long tmp = regval;
		int num = 0;
		while (tmp != 0) {
			tmp = tmp / 10;
			num++;
		}
		return prekey.charAt(num - 1) + "," + Long.toString(regval);
	}

	public int pointSearch_res(String[][] indi, double val) {
		double[] res = new double[maxh];
		int resnum = pointSearch(val, res);
		String prekey = "";
		for (int i = 0; i < resnum; ++i) {
			// indi[i]=res[i];
			prekey = prekey_cons((long) res[i]);
			// tmp = (long) res[i];

			indi[i][0] = prekey + "," + "0";
			indi[i][1] = prekey_cons((long) (res[i]) + 1) + "," + "0";

			// st = idxop.getQual(tabBdname, Long.toString(tmp), "attri",
			// "upper");
			// if (st != "noqual") {
			// indi[i][0] = prekey + st;
			// }
			// ed = idxop.getQual(tabBdname, Long.toString(tmp), "attri",
			// "lower");
			// if (st != "noqual") {
			// indi[i][1] = prekey + ed;
			// }
		}
		return resnum;
	}

	public int pointSearch_resMr(String[] bdrow, double val, int qrytype) {
		double[] res = new double[maxh];

		int resnum = pointSearch(val, res);
		// String prekey = "";

		// double glbd = 0.0, grbd = 0.0;
		// if (qrytype == 0) {
		// glbd = stval;
		// grbd = edval;
		// } else {
		// glbd = vlval;
		// grbd = vrval;
		//
		// }

		double lbd = res[0], rbd = res[0];

		for (int i = 1; i < resnum; ++i) {

			lbd = Math.min(res[i], lbd);
			rbd = Math.max(res[i], rbd);

			// if (res[i] >= glbd) {
			// lbd = Math.min(res[i], lbd);
			// }
			// if (res[i] <= grbd) {
			// rbd = Math.max(res[i], rbd);
			// }
		}

		bdrow[0] = prekey_cons((long) (lbd)) + "," + "0";
		bdrow[1] = prekey_cons((long) (rbd) + 1) + "," + "0";
		return resnum;
	}

	public int pointSearch_resT(String[] indi, double val) {
		double[] res = new double[maxh];
		int resnum = pointSearchT(val, res);
		// String prekey = "";
		// for (int i = 0; i < resnum; ++i) {
		// // indi[i]=res[i];
		// prekey = prekey_cons((long) res[i]);
		// // tmp = (long) res[i];

		indi[0] = prekey_cons((long) (res[0])) + "," + "0";
		indi[1] = prekey_cons((long) (res[1]) + 1) + "," + "0";

		// st = idxop.getQual(tabBdname, Long.toString(tmp), "attri",
		// "upper");
		// if (st != "noqual") {
		// indi[i][0] = prekey + st;
		// }
		// ed = idxop.getQual(tabBdname, Long.toString(tmp), "attri",
		// "lower");
		// if (st != "noqual") {
		// indi[i][1] = prekey + ed;
		// }
		// }
		return resnum;
	}

	public int paraOutput(double para[]) {
		para[0] = sroot;
		para[1] = sdel;
		return 2;
	}

	public void paramConf(int num, double param[]) {
		sroot = param[0];
		sdel = param[1];
		return;
	}

	// ..............conventional way for query process...............//

	// public int conve_rangeSearch_res(String indi[][], double l, double r) {
	// double[] path = new double[100];
	// int pathlen = conve_rangeSearch(path, l, r);
	//
	// for (int i = 0; i < pathlen; ++i) {
	// indi[i][0] = prekey_cons((long) (path[i])) + "," + "0";
	// indi[i][1] = prekey_cons((long) (path[i]) + 1) + "," + "0";
	// }
	// return pathlen;
	// }
	//
	// public int conve_rangeSearch(double res[], double l, double r) {
	//
	// double cur = sroot, del = sdel;
	//
	// int pathcnt = 0;
	// while (del >= 0.5) {
	// res[pathcnt++] = cur;
	// // if (l <= cur && cur <= r) {
	// // break;
	// if (r < cur) {
	// cur -= del;
	// } else if (l > cur) {
	// cur += del;
	// } else {
	//
	// break;
	// }
	// del /= 2.0;
	//
	// }
	//
	// int resnum = conve_boundSearch(l, cur, del, pathcnt - 1, res);
	// resnum = conve_boundSearch(r, cur, del, resnum, res);
	//
	// return resnum;
	// }
	//
	// public int conve_boundSearch(double val, double regpnt, double regdel,
	// int rescnt, double res[])// sign
	// // 0:
	// // left
	// // 1:right
	// {
	// double cur = regpnt, del = regdel;
	// while (del >= 1) {
	// res[rescnt++] = cur;
	//
	// if (Math.abs(val - cur) <= 1e-2) {
	// break;
	// } else if (val > cur) {
	// cur += del;
	//
	// } else {
	// cur -= del;
	//
	// }
	//
	// del /= 2.0;
	// }
	// return rescnt;
	//
	// }

	// unit test
	// public static void main(String[] args) {
	//
	// int testrange = 240;
	//
	// double l = 18, r = 21;
	// double del = 0.0;
	// // System.out.print("fadafda");
	// int cnt = 100;
	// double[] modinfor = new double[2];
	// modinfor[0] = 0.3;
	// modinfor[1] = 1.5;
	// kviIndex idx = new kviIndex();
	//
	// // .....insertion test.........//
	//
	// while (cnt > 0) {
	//
	// l = (int) (Math.random() * testrange);
	// del = (int) (Math.random() * testrange);
	// r = (int) (del + l + 1);
	// if (r > testrange)
	// r = testrange;
	//
	// idx.insert(l, r, modinfor, 1);
	// System.out.print(Double.toString(l) + "  " + Double.toString(r) +
	// "  " + Double.toString(idx.regnode) + "\n");
	//
	// if (r < idx.regnode || l > idx.regnode) {
	// System.out.print("wrong  " + Double.toString(l) + "  "
	// + Double.toString(r) + "    "
	// + Double.toString(idx.regnode) + "\n");
	// break;
	// }
	// cnt--;
	// // idx.testOut();
	// }
	// //System.out.print(Double.toString(idx.sroot) + "\n");
	// System.out.print("done  " + Integer.toString(cnt) + "\n");
	//
	// // ....Interval Search test.......................//
	// double[] res = new double[100];
	// int resnum = 0;
	// cnt = 100;
	// while (cnt > 0) {
	//
	// l = (int) (Math.random() * testrange);
	// del = (int) (Math.random() * testrange);
	// r = (int) (del + l + 1);
	// if (r > testrange)
	// r = testrange;
	//
	// resnum = idx.intervalSearch(l, r, res);
	//
	// System.out.print(Double.toString(l) + "  " + Double.toString(r)
	// + ":");
	// for (int i = 0; i < resnum; ++i) {
	// System.out.print(Double.toString(res[i]) + "   ");
	// }
	// System.out.print("\n");
	//
	// if (res[resnum - 2] > l || res[resnum - 1] < r) {
	// System.out.print("wrong  " + Double.toString(l) + "  "
	// + Double.toString(r) + "\n");
	// break;
	// }
	//
	// cnt--;
	// // idx.testOut();
	// }
	// System.out.print("done   search");
	//
	// // ....point Search test.......................//
	// //double[] res = new do8uble[100];
	// //int resnum = 0;
	// cnt = 100;
	// double val;
	// while (cnt > 0) {
	//
	// val= (int) (Math.random() * testrange);
	//
	// resnum = idx.pointSearch(val, res);
	//
	// System.out.print(Double.toString(val) + "  :");
	// for (int i = 0; i < resnum; ++i) {
	// System.out.print(Double.toString(res[i]) + "   ");
	// }
	// System.out.print("\n");
	//
	// cnt--;
	// // idx.testOut();
	// }
	// System.out.print("done   search");
	//
	// }

}
