package org.openiot.gsn.wrappers;

import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.StreamElement;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;

public class WanWrapper extends AbstractWrapper {
  // The first line describes the data logger, had to check for each file it reads.
  // The 2nd, 3rd and 4th lines are going to have data structure information for rest of the output
  // Time stamp is always the first column in the output.
  private static final String DateFormat = "yyyy-MM-dd HH:mm:ss";
  
  private static final String QUOTE = "\"";
  private static final String SAMPLING = "sampling";
  private int sampling = -1; //in milliseconds.
  private int SAMPLING_DEFAULT = 10*60*1000; // 10 mins
  
  private static final String FILE = "file";
  private String filename  =null; //in milliseconds.
  
  private final transient Logger   logger             = Logger.getLogger( WanWrapper.class );
  private DataField[] structure;
  private int threadCounter=0;
  private SimpleDateFormat dateTimeFormat ;
  
  public static final String NOT_A_NUMBER = "not_a_number";
  private List<String> not_a_number_constants = new ArrayList<String>() ;
  
  public boolean initialize() {
    setName( "WanWrapper-Thread:" + ( ++threadCounter ) );
    dateTimeFormat = new SimpleDateFormat( DateFormat );
    sampling = getActiveAddressBean( ).getPredicateValueAsInt(SAMPLING, SAMPLING_DEFAULT);
    filename = getActiveAddressBean().getPredicateValue(FILE);
    String not_a_number_constant_val = getActiveAddressBean().getPredicateValue(NOT_A_NUMBER);
    
    if (not_a_number_constant_val != null && not_a_number_constant_val.trim().length()>0) {
      StringTokenizer st = new StringTokenizer(not_a_number_constant_val,",");
      while (st.hasMoreTokens()) 
        not_a_number_constants.add(st.nextToken().trim());
    }
    
    if (filename ==null||filename.length()==0 ) {
      logger.error("The wrapper failed, the "+FILE+" parameter is missing.");
      return false;
    }
    try {
      structure = headerToStructure(getHeader(filename));
    } catch (IOException e) {
      logger.error("The system expects a valid content [at least headers] inside "+filename);
      logger.error(e.getMessage(),e);
      return false;
    }
    return true;
  }
  
  public  String[][] getHeader(String filename) throws IOException {
    CSVReader reader = new CSVReader(new FileReader(filename));
    String[] data =  reader.readNext();
    if (data == null)
      return null;
    String[][] headers = new String[4][data.length];
    headers[0]=data;
    headers[1]= reader.readNext();
    headers[2]= reader.readNext();
    headers[3]= reader.readNext();
    if (headers[0]==null||headers[1]==null ||headers[2]==null||headers[3]==null) {
      logger.debug("Header read incompletely.");
      System.out.println(headers[0]==null);
      System.out.println(headers[1]==null);
      System.out.println(headers[2]==null);
      System.out.println(headers[3]==null);
      return null;
    }
    reader.close();
    return headers;
  }
  
  public static DataField[]  headerToStructure(String[][] header) {
    if (header == null)
      return null;
    DataField[] output = new DataField[header[1].length-1];
    for (int i=1;i<header[1].length;i++) {
      StringBuilder description = new StringBuilder(header[2][i]);
      if (header[3][i].length()>2)
        description.append(" - ").append(header[3][i]);
      output[i-1]= new DataField(convertHeaderName(header[1][i]),"double", description.toString());
    }
    return output;
  }
  
  public static String convertHeaderName(String name) {
    return name.replace("(", "").replace(")", "");
  }
  public  StreamElement rowToSE(DataField[] structure, String[] data) {
    Date date;
    StreamElement se = null;
    try {
      date = dateTimeFormat.parse(data[0]);
      se = new StreamElement(structure,removeTimestampFromRow(structure,data),date.getTime());
    } catch (ParseException e) {
      logger.error("invalide date format! "+data[0]);
      logger.error(e.getMessage(),e);
    }finally {
      return se;
    }
  }
  
  public Double[] removeTimestampFromRow(DataField[] structure, String [] data) {
    Double[] toReturn = new Double[structure.length];
    next_val:for (int i=0;i<structure.length;i++) {
      String val = data[i+1].trim();
      for (String nan : not_a_number_constants) {
        if (val.equals(nan)) {
          toReturn[i] = null;
          continue next_val;
        }
      }
      toReturn[i] = Double.parseDouble(val);
    }
    return toReturn;
  }
  
  public void run() {
    File input =null;
    CSVReader reader = null;
    while (true) {
      try {
        Thread.sleep(sampling);
        input = new File (filename);
        if (!input.exists()|| !input.isFile()) {
          logger.debug("The specified file: "+filename+" doesn't exist, going to sleep.");
          continue;
        }
        if (!isNewDataAvailable())
          continue;
        DataField[] current_structure = headerToStructure(getHeader(filename));
        if (current_structure==null)
          continue;
        String[] data = null;
        reader = new CSVReader(new FileReader(filename),',','\"',4);
        while ((data =reader.readNext()) !=null) {
          if (data.length<(current_structure.length+1)) {
            logger.info("Possible empty line ignored.");
            continue;
          }
          StreamElement streamElement = rowToSE(current_structure, data);
          postStreamElement(streamElement);
        }
      } catch (Exception e) {
        logger.error("Error in reading/processing "+filename);
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
  
  public DataField[] getOutputFormat() {
    return structure;
  }
  
  public String getWrapperName() {
    return "WanWrapper";
  }
  
  public void dispose() {
    threadCounter--;  
  }
  
  private long lastModified= -1;
  /**
   * Returns true if the size of the file is bigger than 10 bytes and the last modified time of the file
   * is after the last_modified time we recorded from the previous call of this method. This method sets
   * the value of last_modified variable hence calling this method twice results false in the second call.
   * @return boolean
   */
  public boolean isNewDataAvailable() {
    File f = new File(filename);
    if (f.getTotalSpace()<10)
      return false;
     long current_lastModified = f.lastModified();
    if ( lastModified<current_lastModified && current_lastModified<(System.currentTimeMillis()+2*60*1000)) {
      lastModified=current_lastModified;
      return true;
    }
    return false;
  }
}
