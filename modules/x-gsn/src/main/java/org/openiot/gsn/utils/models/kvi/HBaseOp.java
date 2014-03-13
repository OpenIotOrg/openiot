package gsn.utils.models.kvi;

import java.io.*;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;

public class HBaseOp {

	public Configuration config = HBaseConfiguration.create();

	public ResultScanner rs;
	public Result scanr;

	public ResultScanner[] mulrs = new ResultScanner[3];
	public Result[] mulscanr = new Result[3];

	HTable tableput;
	Put tabput;

	public HBaseAdmin admin;// =new HBaseAdmin(config);

	public HBaseOp() {

		rs = null;
		scanr = null;

		// try {
		//
		// admin = new HBaseAdmin(config);

		//
		// } catch (IOException e) {
		//
		// System.out.println("IOError: cannot hbase admin.");
		// e.printStackTrace();
		// }

		for (int i = 0; i < mulrs.length; i++) {
			mulrs[i] = null;
			mulscanr[i] = null;
		}

	}

	public void iniTabOperation(String tabname) throws IOException {
		tableput = new HTable(config, tabname);
	}

	public void creTab(String tabname) {

		// Try to create a Table with 2 column family (Title, Author)
		HTableDescriptor descriptor = new HTableDescriptor(tabname);
		descriptor.addFamily(new HColumnDescriptor("attri"));
		descriptor.addFamily(new HColumnDescriptor("model"));
		//have room to improve 
		
		//...region size test....//
		descriptor.setMaxFileSize(1024*1024*128);
		
		double regsize=descriptor.getMaxFileSize();
		
		System.out.printf("Region file size:  %f\n",regsize);
		//.......................//

		try {
			// Create a HBaseAdmin
			admin = new HBaseAdmin(config);
			// Create table
			admin.createTable(descriptor);
			System.out.println(tabname+" Table created…");
		} catch (IOException e) {

			System.out.println("IOError: cannot create Table.");
			e.printStackTrace();
		}
	}

	public void formatHbaseM2() {
		try {
			// Create a HBaseAdmin
			HBaseAdmin admin = new HBaseAdmin(config);
			// Create table
			admin.disableTable("tempM2");
			admin.disableTable("tempM2V");

			admin.deleteTable("tempM2");
			admin.deleteTable("tempM2V");

		} catch (IOException e) {

			System.out.println("IOError: cannot format hbase.");
			e.printStackTrace();
		}
	}

	public void enable(String tabname) {
		try {
			// Create a HBaseAdmin
			admin = new HBaseAdmin(config);
			admin.enableTable(tabname);
			// admin.enableTable("tempM2");

		} catch (IOException e) {

			System.out.println("IOError: cannot enable table.");
			e.printStackTrace();
		}
	}

	public void delTab(String tabname) {
		try {
			// Create a HBaseAdmin
			admin = new HBaseAdmin(config);

			admin.disableTable(tabname);
			admin.deleteTable(tabname);

		} catch (IOException e) {

			System.out.println("IOError: cannot format hbase.");
			e.printStackTrace();
		}
		return;
	}

	public void formatHbase() {
		try {

			// Create a HBaseAdmin
			HBaseAdmin admin = new HBaseAdmin(config);

			admin.disableTable("tempIdxmod");
			admin.deleteTable("tempIdxmod");

			admin.disableTable("valIdxmod");
			admin.deleteTable("valIdxmod");

		} catch (IOException e) {

			System.out.println("IOError: cannot format hbase.");
			e.printStackTrace();
		}
	}

	public void ini() {

		// Try to create a Table with 2 column family (Title, Author)
		HTableDescriptor descriptor = new HTableDescriptor("tsTab");
		descriptor.addFamily(new HColumnDescriptor("attri"));
		descriptor.addFamily(new HColumnDescriptor("model"));

		try {
			// Create a HBaseAdmin
			admin = new HBaseAdmin(config);
			// Create table
			admin.createTable(descriptor);
			System.out.println("Table created…");
		} catch (IOException e) {

			System.out.println("IOError: cannot create Table.");
			e.printStackTrace();
		}
	}

	public int put(String tabname, String row, String cf, String qual,
			String val) {
		try {
//			 HTable table = new HTable(config, tabname);
			tabput = new Put(Bytes.toBytes(row));

			// for (int j = 0; j < cfs.length; j++) {
			tabput.add(Bytes.toBytes(cf), Bytes.toBytes(qual),
					Bytes.toBytes(val));
			tableput.put(tabput);
			// }

			return 1;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return 0;
	}

	public void scanPrefixFilter(String tabname, String rowpf) {

		try {
			HTable table = new HTable(config, tabname);
			Scan s = new Scan();
			s.setFilter(new PrefixFilter(Bytes.toBytes(rowpf)));
			rs = null;
			rs = table.getScanner(s);
			scanr = null;
			// return rs;

		} catch (IOException e) {
			System.out.println("scan initialization problem");
			// return null;
		}

	}

	public void scanGloIni(String tabname) {

		try {
			HTable table = new HTable(config, tabname);
			Scan s = new Scan();
			rs = null;
			rs = table.getScanner(s);
			scanr = null;
			// return rs;

		} catch (IOException e) {
			System.out.println("scan initialization problem");
			// return null;
		}

	}

	public void scanIni(String tabname, String strw, String edrw) {

		try {
			HTable table = new HTable(config, tabname);
			Scan s = new Scan();
			s.setStartRow(strw.getBytes());
			s.setStopRow(edrw.getBytes());
			rs = null;
			rs = table.getScanner(s);
			scanr = null;
			// return rs;

		} catch (IOException e) {
			System.out.println("scan initialization problem");
			// return null;
		}

	}

	public void scanIni(String tabname, String strw, String edrw, String qual[]) {

		try {
			HTable table = new HTable(config, tabname);
			Scan s = new Scan();
			s.setStartRow(strw.getBytes());
			s.setStopRow(edrw.getBytes());
			s.addFamily(Bytes.toBytes("model"));
			s.addColumn(Bytes.toBytes("attri"),Bytes.toBytes(qual[0]));
			s.addColumn(Bytes.toBytes("attri"),Bytes.toBytes(qual[1]));
			
			
			rs = null;
			rs = table.getScanner(s);
			scanr = null;
			// return rs;

		} catch (IOException e) {
			System.out.println("scan initialization problem");
			// return null;
		}

	}

	public String scanNext() {
		try {

			Result r = rs.next();

			if (r == null) {
				rs.close();
				return "NO";
			} else {

				return "YES";
			}

		} catch (IOException e) {
			System.out.println("scan next probblem");
			return "NO";
		}
	}

	public void scanMNext() {
		try {
			scanr = rs.next();
			return;
		} catch (IOException e) {
			System.out.println("scan next problem");

		}
	}

	public String scanM(String cf, String[] qual, int qn, String[] res) {

		if (scanr == null) {
			res[0] = "NO";
			rs.close();
			return "NO";
		} else {
			for (int i = 0; i < qn; ++i) {
				String tres = new String(scanr.getValue(cf.getBytes(),
						qual[i].getBytes()));
				res[i] = tres;
			}
			return "Yes";
		}
	}

	public String scanR() {

		if (scanr == null) {
			rs.close();
			return "NO";
		} else {
			String tres = new String(scanr.getRow());
			return tres;
		}
	}

	public String scanNext(String cf, String qual) {
		try {

			Result r = rs.next();
			scanr = r;

			if (r == null) {
				rs.close();
				return "NO";
			} else {
				String res = new String(r.getValue(cf.getBytes(),
						qual.getBytes()));
				return res;
			}

		} catch (IOException e) {
			System.out.println("scan next probblem");
			return "NO";
		}
	}

	public String scanGet(String cf, String qual) {

		if (scanr == null) {
			rs.close();
			return "NO";
		} else {
			String res = new String(scanr.getValue(cf.getBytes(),
					qual.getBytes()));
			return res;
		}
	}

	// ........get test......................................................//

	public void getTestRow(String tabname, String rkey) {

		// throws IOException {
		// String res = "NO";
		try {
			HTable table = new HTable(config, tabname);
			Get g = new Get(rkey.getBytes());
			// g.addColumn(cf.getBytes(), qual.getBytes());

			Result rs = table.get(g);

			rs.getValue("attri".getBytes(), "vl".getBytes());
			rs.getValue("attri".getBytes(), "vr".getBytes());

			rs.getValue("model".getBytes(), "cof1".getBytes());

			return;

		} catch (IOException e) {
			// System.out.println("get probblem");
			return;
		}
	}

	// ....................................................................//

	public void get(String tabname, String rkey, String cf, String[] qual,
			String[] res) {

		// throws IOException {
		// String res = "NO";
		try {
			HTable table = new HTable(config, tabname);
			Get g = new Get(rkey.getBytes());
			// g.addColumn(cf.getBytes(), qual.getBytes());

			Result rs = table.get(g);

			for (int i = 0, j = qual.length; i < j; ++i) {

				// tmp= Byte[]{};
				if (rs.getValue(cf.getBytes(), qual[i].getBytes()) == null)
					// if(tmp.length()== 0)
					return;
				else {
					String tmp = new String(rs.getValue(cf.getBytes(),
							qual[i].getBytes()));
					res[i] = tmp;
				}
			}
			return;

		} catch (IOException e) {
			// System.out.println("get probblem");
			return;
		}
	}

	public boolean get(String tabname, String rkey, String cf, String qual) {

		// throws IOException {
		// String res = "NO";
		try {
			HTable table = new HTable(config, tabname);
			Get g = new Get(rkey.getBytes());
			// g.addColumn(cf.getBytes(), qual.getBytes());

			Result rs = table.get(g);

			if (rs.getValue(cf.getBytes(), qual.getBytes()) == null)
				// if(tmp.length()== 0)
				return false;
			else {
				return true;
			}
			// }
			// return true;

		} catch (IOException e) {
			// System.out.println("get probblem");
			return false;
		}
	}

	public String getQual(String tabname, String rkey, String cf, String qual) {

		// throws IOException {
		// String res = "NO";
		try {
			HTable table = new HTable(config, tabname);
			Get g = new Get(rkey.getBytes());
			// g.addColumn(cf.getBytes(), qual.getBytes());

			Result rs = table.get(g);

			if (rs.getValue(cf.getBytes(), qual.getBytes()) == null)
				// if(tmp.length()== 0)
				return "noqual";
			else {

				return new String(rs.getValue(cf.getBytes(), qual.getBytes()));
			}
			// }
			// return true;

		} catch (IOException e) {
			// System.out.println("get probblem");
			return "noqual";
		}
	}

	public void multiScanIni(String tabname, int ptno) {

		try {
			HTable table = new HTable(config, tabname);
			Scan s = new Scan();
			mulrs[ptno] = null;
			mulrs[ptno] = table.getScanner(s);
			mulscanr[ptno] = null;
			// return rs;

		} catch (IOException e) {
			System.out.println("scan initialization problem");
			// return null;
		}

	}

	public String multiScanR(int ptno) {

		if (mulscanr[ptno] == null) {
			return "NO";
		} else {
			String tres = new String(mulscanr[ptno].getRow());
			return tres;
		}
	}

	public void multiScanMNext(int ptno) {
		try {
			mulscanr[ptno] = mulrs[ptno].next();
			return;
		} catch (IOException e) {
			System.out.println("scan next problem");

		}
	}

	public String multiScanM(String cf, String[] qual, String[] res, int ptno) {

		if (mulscanr[ptno] == null) {
			res[0] = "NO";
			return "NO";
		} else {
			for (int i = 0; i < qual.length; ++i) {
				String tres = new String(mulscanr[ptno].getValue(cf.getBytes(),
						qual[i].getBytes()));
				res[i] = tres;
			}
			return "Yes";
		}
	}

}
