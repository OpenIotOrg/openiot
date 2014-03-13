//import gsn.utils.models.kvi.kviMRproCol;

//import gsn.utils.models.kvi.kviMRproCol;

public class queryRece {

	kviMRproCol qryPro;

	public queryRece() {

	}

	public static void main(String[] args) throws Exception { // 0: temp; 1:
																// value

		// String signStr;
		// int expnum = 0;

		queryRece qryRec = new queryRece();
		
		String tabname = args[0];
		int sign = Integer.valueOf(args[1]);

		qryRec.qryPro = new kviMRproCol(0, tabname, sign);

		String type = args[2];
		double vl = 0.0, vr = 0.0, tmp = 0.0;
		String bdl = new String(), bdr = new String();
		double[] runpara = new double[10];

		String[] tcol = { "st", "ed" }, vcol = { "vl", "vr" };

		if (type.compareTo("point") == 0) {
			vl = Double.valueOf(args[3]);

			bdl = args[4];
			bdr = args[5];

			if (sign == 0) {
				qryRec.qryPro.jobPointEvalQuery(vl, tabname, bdl, bdr, tcol,
						runpara);
			} else {
				qryRec.qryPro.jobPointEvalQuery(vl, tabname, bdl, bdr, vcol,
						runpara);
			}

		} else if (type.compareTo("range") == 0) {
			vl = Double.valueOf(args[3]);
			vr = Double.valueOf(args[4]);

			bdl = args[5];
			bdr = args[6];

			if (sign == 0) {
				tmp = qryRec.qryPro.jobIntervalEvalQuery(vl, vr, tabname, bdl,
						bdr, tcol, runpara);
			} else {
				tmp = qryRec.qryPro.jobIntervalEvalQuery(vl, vr, tabname, bdl,
						bdr, vcol, runpara);
			}
		}

	}

}
