package gsn.utils.models.kvi;

public class scanPro {

	public HBaseOp tabOper = new HBaseOp();
	public int modorder;
	public String prekey;
	public int constr=1;
	

	public scanPro(int modord) {
		modorder = modord;
		prekey = "abcdefghijklmnopqrstuvwxyz";
	}
	public void valPaser_kvi(String col, double res[])// res[0]|:left
	{
		int len = col.length();
		int st = 0;
		for (int i = 0; i < len; ++i) {
			if (col.charAt(i) == ',' && st == 0) {
				res[0] = Double.parseDouble(col.substring(st, i));
				st = i + 1;
			} else if (col.charAt(i) == ',') {
				res[1] = Double.parseDouble(col.substring(st, i));
				break;
			}

		}
		return;
	}
	public void model_grid(double domain[], String[] model) {

		double step = (domain[1] - domain[0]) / 20.0;
		double l = domain[0], r = domain[1];

		if (step > 0) {
			for (double i = l; i <= r; i += step) {
			}

			// // unit test//
			// System.out.printf("%f  %s\n", i, model[0]);
			// // ..........//
		}
		return;

	}
	public double pointSearch_scan(String tabname, String[][] indi, double val,
			int pointnum, String qual[], int sign) {//0: temp 1: value

		String str = "";

		double[] bound = new double[3];
		String[] modinfor = new String[3];
		double resnum = 0.0;
		
		if(sign==1)
		{
			pointnum=1; //modification
		}
		
		for (int i = 0; i < pointnum; ++i) {

			// tabOper.scanIni(tabname, indi[i][0], indi[i][1]);
			tabOper.scanIni(tabname, indi[i][0], indi[i][1], qual);

			str = tabOper.scanNext("attri", qual[0]);

			while (str != "NO") {
				// valPaser_kvi(str, bound);
				resnum++;

				bound[0] = Double.parseDouble(str);
				
				str = tabOper.scanGet("attri", qual[1]);
				bound[1] = Double.parseDouble(str);

				if (val >= bound[0] && val <= bound[1]) {
					str = tabOper.scanGet("model", "coef0");
					modinfor[0] = str;
					model_grid(bound, modinfor);

					// .......unit test......//
					// System.out.printf("\n%f    %f    %s \n   ", bound[0],
					// bound[1], str);
					// // ......................//

				}
				str = tabOper.scanNext("attri", qual[0]);
			}
		}
		// System.out.printf("%d\n",resnum);
		return resnum;
	}

	public double rangeSearch_scan(String tabname, String[][] indi, double l,
			double r, int pointnum, String qual[]) {

		String str = "";
		double[] bound = new double[3];
		String[] modinfor = new String[3];
		double num = 0.0;
		
	//	pointnum=1; //modification
		for (int i = 0; i < pointnum; ++i) {

			// tabOper.scanIni(tabname, indi[i][0], indi[i][1]);
			tabOper.scanIni(tabname, indi[i][0], indi[i][1], qual);
			str = tabOper.scanNext("attri", qual[0]);

			while (str != "NO") {
				// valPaser_kvi(str, bound);
				num++;

				bound[0] = Double.parseDouble(str);
				
				str = tabOper.scanGet("attri", qual[1]);
				bound[1] = Double.parseDouble(str);

				if ((r < bound[0] || l > bound[1])) {
				} else {
					str = tabOper.scanGet("model", "coef0");
					modinfor[0] = str;
					model_grid(bound, modinfor);

					// .......unit test......//
					//
//					 System.out.printf("\n%f    %f    %s \n   ", bound[0],
//					 bound[1], str);
					// // ......................//
				}
				str = tabOper.scanNext("attri", qual[0]);
			}
			 //System.out.printf("%d \n   ", num);
		}
		// System.out.printf("%d\n", num);
		return num;
	}

	public double rangeSearch_scanNoGrid(String tabname, String[][] indi,
			double l, double r, int pointnum, String qual[]) {

		String str = "";
		double[] bound = new double[3];
		String[] modinfor = new String[3];
		double num = 0.0;

		pointnum=1; //modification
		
		for (int i = 0; i < pointnum; ++i) {

			// tabOper.scanIni(tabname, indi[i][0], indi[i][1]);
			tabOper.scanIni(tabname, indi[i][0], indi[i][1], qual);

			str = tabOper.scanNext("attri", qual[0]);

			while (str != "NO") {
				// valPaser_kvi(str, bound);
				num++;

				bound[0] = Double.parseDouble(str);
				str = tabOper.scanGet("attri", qual[1]);
				bound[1] = Double.parseDouble(str);

				if ((r < bound[0] || l > bound[1])) {
				} else {
					// str = tabOper.scanGet("model", "coef0");
					// modinfor[0] = str;
					// model_grid(bound, modinfor);

					// .......unit test......//
					//
					// System.out.printf("\n%f    %f    %s \n   ", bound[0],
					// bound[1], str);
					// // ......................//
				}
				str = tabOper.scanNext("attri", qual[0]);
			}
		}
		// System.out.printf("%d\n", num);
		return num;
	}

	public String prekey_cons(long regval) {
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

	// public double rangeSearch_interRangeScan(String tabname, double l, double
	// r) {
	//
	// String str = "";
	// long tmpi = 0;
	// double[] bound = new double[3];
	// String[] modinfor = new String[3];
	// double num = 0.0;
	// for (double i = l; i <= r; ++i) {
	//
	// tmpi = (long) i;
	// tabOper.scanIni(tabname, prekey_cons(tmpi) + "," + "0",
	// prekey_cons(tmpi + 1) + "," + "0");
	// str = tabOper.scanNext("attri", "primIntev");
	// while (str != "NO") {
	// valPaser_kvi(str, bound);
	// num++;
	//
	// if ((r < bound[0] || l > bound[1])) {
	// } else {
	// str = tabOper.scanGet("model", "coef0");
	//
	// // // .......unit test......//
	//
	// // System.out.printf("%f    %f    %s\n", bound[0], bound[1],
	// // str);
	// // // ......................//
	//
	// modinfor[0] = str;
	// model_grid(bound, modinfor);
	// }
	// str = tabOper.scanNext("attri", "primIntev");
	// }
	// }
	// // System.out.printf("%d\n", num);
	// return num;
	// }

	public double rangeSearch_interRangeScanConsec(String tabname, double l,
			double r, String qual[]) {

		String str = "";
		long tmpl = 0, tmpr = 0;
//		double[] bound = new double[3];
//		String[] modinfor = new String[3];
		double num = 0.0;

		// ...need consideration....//
		tmpl = (long) l;
		tmpr = (long) r + 1;

		tabOper.scanIni(tabname, prekey_cons(tmpl) + "," + "0",
				prekey_cons(tmpr) + "," + "0", qual);

	//	str = tabOper.scanNext("attri", qual[0]);
		str = tabOper.scanNext("model", "coef0");

		while (str != "NO") {
			// valPaser_kvi(str, bound);
			num++;

//			bound[0] = Double.parseDouble(str);
//			
//			str = tabOper.scanGet("attri", qual[1]);
//			bound[1] = Double.parseDouble(str);
//
//			if ((r < bound[0] || l > bound[1])) {
//			} else {
				// str = tabOper.scanGet("model", "coef0");
				// modinfor[0] = str;
				// model_grid(bound, modinfor);

				// .......unit test......//

				//
				// System.out.printf("\n%f    %f    %s \n   ", bound[0],
				// bound[1], str);
				// // ......................//

//			}
			str = tabOper.scanNext("model", "coef0");
			//str = tabOper.scanNext("attri", qual[0]);
		}
		return num;
	}
}
