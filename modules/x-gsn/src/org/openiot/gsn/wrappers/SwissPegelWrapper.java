package org.openiot.gsn.wrappers;


import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.StreamElement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import au.com.bytecode.opencsv.CSVReader;

public class SwissPegelWrapper extends AbstractWrapper {

	// The first line describes the data logger, had to check for each file it reads.
	// The 2nd, 3rd and 4th lines are going to have data structure information for rest of the output
	// Time stamp is always the first column in the output.
	private static final String DateFormat = "HH:mm dd.MM.yyyy";
	private static final String SvnDateFormat = "yyyy-MM-dd'T'HH:mm:ss";

	private static final String QUOTE = "\"";
	private static final String SAMPLING = "sampling";
	private static final String SKIP_LINES = "skip_lines";
	private static final String SEPERATOR = "seperator";

	private int sampling = -1; //in milliseconds.
//	private int SAMPLING_DEFAULT = 10*60*1000; // 10 mins
	private int SAMPLING_DEFAULT = 1*60*1000; // 1 min for testing

	private static final String DIRECTORY = "directory";
	private String directory  =null; 

        private static final String DATADIRECTORY = "data_directory";
        private String datadirectory  =null;

	private static final String SVNURL = "svnurl";
	private String svnurl =null; 

	private static final String SVNLOGIN = "svnlogin";
	private String svnlogin =null; 

	private static final String SVNPASSWD = "svnpasswd";
	private String svnpasswd =null; 

	private boolean file_handling = true;

	private final transient Logger   logger             = Logger.getLogger( SwissPegelWrapper.class );
	private DataField[] structure = {
			new DataField( "pegel" , "double" , "pegel"),
			new DataField( "minterval" , "double" , "measurement interval"),
			new DataField( "quality" , "double" , "measurement quality"),
			new DataField( "measurementType", "double", "measurement type") };
	private int threadCounter=0;
	private SimpleDateFormat dateTimeFormat ;
	private SimpleDateFormat svnDateTimeFormat ;
	private long lastModified= 0;
	private long lastEnteredStreamelement =0;
	private int skip_lines = 3;
	private char seperator = '\t';

	private File statusFile = null;

	public static final String NOT_A_NUMBER = "not_a_number";
	private List<String> not_a_number_constants = new ArrayList<String>() ;

	public boolean initialize() {
		setName( "SwissPegelWrapper-Thread:" + ( ++threadCounter ) );
		dateTimeFormat = new SimpleDateFormat( DateFormat );
		svnDateTimeFormat = new SimpleDateFormat( SvnDateFormat );
		sampling = getActiveAddressBean( ).getPredicateValueAsInt(SAMPLING, SAMPLING_DEFAULT);
		directory = getActiveAddressBean().getPredicateValue(DIRECTORY);
		datadirectory = getActiveAddressBean().getPredicateValue("data_directory");
		svnurl = getActiveAddressBean().getPredicateValue(SVNURL);
		if (datadirectory!=null && datadirectory.length()>0)
		if (svnurl!=null && svnurl.length()>0){
			if (directory==null||directory.length()==0){
				logger.error("The wrapper failed, the "+DIRECTORY+" parameter is missing.");
				return false;
			}
			file_handling = false;
			svnlogin = getActiveAddressBean().getPredicateValue(SVNLOGIN);
			svnpasswd = getActiveAddressBean().getPredicateValue(SVNPASSWD);
		}
		skip_lines = getActiveAddressBean().getPredicateValueAsInt(SKIP_LINES, 4);
		String seperator_text = getActiveAddressBean().getPredicateValue(SEPERATOR);

		String not_a_number_constant_val = getActiveAddressBean().getPredicateValue(NOT_A_NUMBER);

		if (not_a_number_constant_val != null && not_a_number_constant_val.trim().length()>0) {
			StringTokenizer st = new StringTokenizer(not_a_number_constant_val,",");
			while (st.hasMoreTokens()) 
				not_a_number_constants.add(st.nextToken().trim());
		}
		if (seperator_text.equals("tab")) seperator='\t';
		else if (seperator_text.equals("space")) seperator=' ';
		else seperator = seperator_text.charAt(0);

		if (!readStatus()) return false; 

		logger.warn("wrapper correctly initialized");
		return true;
	}

	private boolean readStatus(){
		String filename;
		if (file_handling) filename = datadirectory;
		else filename  = svnurl;
		filename = filename.replace('/','_');
		filename = filename.replace(':','_');
		filename = filename.replace('\\','_');
		statusFile = new File(directory+File.separator+filename+"_status.txt");
		String contents = null;
		if (statusFile.exists()){
			try {
				BufferedReader input =  new BufferedReader(new FileReader(statusFile));
				try {
					String line = null; //not declared within while loop
					while (( line = input.readLine()) != null){
						contents = line;
					}
				}
				finally {
					input.close();
				}
			}
			catch (IOException ex){
				ex.printStackTrace();
			}
			logger.warn("Content of the last line of the status file: "+contents);
			if (contents!=null){
				String[] list = contents.split(";");
				logger.warn("number of split elements: "+list.length+"  0:"+list[0]+"  1:"+list[1]);
				this.lastEnteredStreamelement = new Long(list[0]);
				this.lastModified = new Long(list[1]);
			} else {
				this.lastEnteredStreamelement = new Long(0);
                                this.lastModified = new Long(0);
			}
		} else {
			try {
				statusFile.createNewFile();
			} catch (IOException e) {
				logger.error("the status file can not be created "+statusFile.getAbsolutePath());
				return false;
			}
		}

		return true;
	}

	private void writeStatus(){
		try{
			FileWriter fstream = new FileWriter(statusFile);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(lastEnteredStreamelement+";"+lastModified);
			out.close();
		}catch (Exception e){//Catch exception if any
			logger.error("Error: " + e.getMessage());
		}
	}

	public  StreamElement rowToSE(String[] data) {
		Date date;
		StreamElement se = null;
		try {
			date = dateTimeFormat.parse(data[1]+" "+data[0]);
			se = new StreamElement(structure,removeTimestampFromRow(data),date.getTime());
		} catch (ParseException e) {
			logger.error("invalide date format! "+data[1]+" "+data[0]);
			logger.error(e.getMessage(),e);
		}finally {
			return se;
		}
	}

	public Double[] removeTimestampFromRow(String [] data) {
		Double[] toReturn = new Double[structure.length];
		next_val:for (int i=0;i<structure.length;i++) {
			String val = null;
			try{
				val = data[i+2].trim();
			}catch(Exception e){
				logger.warn("column out of bound: "+(i+2));
				toReturn[i] = null;
				continue next_val;
			}			
			for (String nan : not_a_number_constants) {
				if (val.equals(nan)) {
					toReturn[i] = null;
					continue next_val;
				}
			}
			try{
				toReturn[i] = Double.parseDouble(val);
			}catch(Exception e){
				logger.warn("data parsing exception " +e.toString());
				toReturn[i] = null;
			}
		}
		return toReturn;
	}

	public void run() {
		CSVReader reader = null;
		while (true) {
			try{
				Thread.sleep(sampling);
				logger.warn("new sampling started "+file_handling);
				if (file_handling){
					TreeMap<Long,File> list = getNewFileDataAvailable();
					for (Long modified: list.keySet()){
						File file = list.get(modified);
						logger.warn("processing the received file list "+file.getAbsolutePath());
						try {
							String[] data = null;
							reader = new CSVReader(new FileReader(file),seperator,'\"',skip_lines);
							logger.warn("parse file "+file.getAbsolutePath());
							while ((data =reader.readNext()) !=null) {
//								if (data.length<(current_structure.length+1)) {
//								logger.info("Possible empty line ignored.");
//								continue;
//								}
								StreamElement streamElement = rowToSE(data);
								if (streamElement.getTimeStamp()>this.lastEnteredStreamelement){
									logger.warn("posting data");
									postStreamElement(streamElement);
									this.lastEnteredStreamelement = streamElement.getTimeStamp();
								}
							}
							this.lastModified = modified.longValue();
							writeStatus();
						} catch (Exception e) {
							logger.error("Error in reading/processing "+file);
							logger.error(e.getMessage(),e);
						} finally {
							if (reader!=null)
								try {
									reader.close();
								} catch (IOException e) {
								}
						}
					}
				} else {
					logger.warn("start svn data processing");
					TreeMap<Long,String> list = getNewSvnDataAvailable();
					logger.warn("the list has been derived; there are elements: "+list.size());
					for (Long modified:list.keySet()){
						String name = list.get(modified);
						logger.warn("processing the received file list "+name);
						try {
							String[] data = null;
							Process p = Runtime.getRuntime().exec("svn cat "+name+" --username '"+svnlogin+"' --password '"+svnpasswd+"' ");
							InputStream in = p.getInputStream();
							BufferedReader d = new BufferedReader(new InputStreamReader(in));
							reader = new CSVReader(d,seperator,'\"',skip_lines);
							logger.warn("parse file "+name);
							while ((data =reader.readNext()) !=null) {
//								if (data.length<(current_structure.length+1)) {
//								logger.info("Possible empty line ignored.");
//								continue;
//								}
								StreamElement streamElement = rowToSE(data);
								if (streamElement.getTimeStamp()>this.lastEnteredStreamelement){
									logger.warn("posting data");
									postStreamElement(streamElement);
									this.lastEnteredStreamelement = streamElement.getTimeStamp();
								}
							}
							this.lastModified = modified.longValue();
							writeStatus();
						} catch (Exception e) {
							logger.error("Error in reading/processing "+name);
							logger.error(e.getMessage(),e);
						} finally {
							if (reader!=null)
								try {
									reader.close();
								} catch (IOException e) {
								}
						}
					}
				}
			} catch (InterruptedException e){
				logger.error(e.getMessage(), e);
			}
		}
	}

	private TreeMap<Long,String> getNewSvnDataAvailable(){
		TreeMap<Long,String> nameList = new TreeMap<Long,String>();
		try{
			logger.warn("start getNewSvnDataAvailable()");
			logger.warn("svnlogin:"+svnlogin+"   svnpasswd:"+svnpasswd);
			logger.warn("svnurl:"+svnurl);
			String cmd = "svn info "+svnurl+"/ --username '"+svnlogin+"' --password '"+svnpasswd+"' -R --xml";
			Process p = Runtime.getRuntime().exec(cmd);
			logger.warn("process initialized");
			InputStream in = p.getInputStream();

			DOMParser parser = new DOMParser();
			InputSource source = new InputSource(in);
			parser.parse(source);

			Document doc = parser.getDocument();
			NodeList entries = doc.getElementsByTagName("entry");
			logger.warn("entries: "+entries.getLength());
			for(int i=0;i<entries.getLength();i++){
				Element e = (Element) entries.item(i);
				logger.warn("kind: "+e.getAttribute("kind"));
				if( e.getAttribute("kind").equals("file")){
					String name = e.getAttribute("path");
					Element urlElem = (Element) e.getElementsByTagName("url").item(0);
					NodeList l = urlElem.getChildNodes();
					String url =null;
					String dateStr = null;
					for (int j=0;j<l.getLength();j++){
						Node n = l.item(j);
						if (n.getNodeType()==Node.TEXT_NODE){
							url = n.getNodeValue();
							break;
						}
					}
					Element dateElem = (Element) e.getElementsByTagName("date").item(0);
					l = dateElem.getChildNodes();
					for (int j=0;j<l.getLength();j++){
						Node n = l.item(j);
						if (n.getNodeType()==Node.TEXT_NODE){
							dateStr = n.getNodeValue();
							break;
						}
					}
					if (url!=null) logger.warn("url: "+url);
					if (dateStr!=null) logger.warn("dateStr: "+dateStr);
					Date date = svnDateTimeFormat.parse(dateStr.substring(0, SvnDateFormat.length()-2));
					if (date.getTime() > this.lastModified ){
						nameList.put(new Long(date.getTime()),url);
						logger.warn("add file: path name: "+name+"   url: "+url+"    date: "+dateStr);
					}
				}

			}

		} catch(IOException e){
			logger.error("the svn can not be updated: "+e.getMessage());
		} catch (SAXException e) {
			logger.error("the svn created XML is not valid: "+e.getMessage());
		} catch (DOMException e) {
			logger.error("the xml provided by the svn resulted in a DOM excoption "+e.getMessage());
		} catch (ParseException e) {
			logger.error("the date format provided by the svn resulted in a parsing exception "+e.getMessage());
		}

		return nameList;
	}

	public DataField[] getOutputFormat() {
		return structure;
	}

	public String getWrapperName() {
		return "STS Piezometer Wrapper";
	}

	public void dispose() {
		threadCounter--; 
		writeStatus();
	}


	/**
	 * scan the directory for new files and return the files in an ordered list according
	 * to their modification date for inserting the data; an empty collection is returned 
	 * if there are no new files
	 * @return
	 */
	public TreeMap<Long,File> getNewFileDataAvailable(){
		File dir = new File(datadirectory);
		File[] list = dir.listFiles();
		TreeMap<Long,File> map = new TreeMap<Long,File>();
		long modified = this.lastModified;

		for (int i=0;i<list.length;i++){
			if ((list[i].getTotalSpace()>10) && !(list[i].getName().startsWith("."))){
				long l = list[i].lastModified();
				if (l>this.lastModified) {
					modified = l;
					map.put(new Long(l), list[i]);
				}
			}
		}
		return map;
	}
}

