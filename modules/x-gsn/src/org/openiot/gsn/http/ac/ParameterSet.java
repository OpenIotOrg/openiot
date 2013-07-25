package org.openiot.gsn.http.ac;
/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Feb 15, 2010
 * Time: 12:38:44 PM
 * To change this template use File | Settings | File Templates.
 */

import com.oreilly.servlet.MultipartRequest;
import org.apache.log4j.Logger;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* this is a  class for using and processing the parameters of a http form */

public class ParameterSet
{

	private Hashtable paramset;
    private MultipartRequest multipartreq;
    private static final int  maxPostSize=5 * 1024 * 1024;
     private static transient Logger logger                             = Logger.getLogger( ParameterSet.class );

    /****************************************** Constructors*******************************************/
    /*************************************************************************************************/

    /* constructor for processing parameters of a normal form */
	public ParameterSet(HttpServletRequest req)
	{
		paramset=new Hashtable();
		Enumeration myenum = req.getParameterNames();
		while (myenum.hasMoreElements())
		{
			String name = (String) myenum.nextElement();
            String values[] = req.getParameterValues(name);
             if (values != null)
             {
                if(values.length<=1)
                {
                  paramset.put( name,req.getParameter(name));

                }
             }
             else
             {
                 paramset.put( name,req.getParameter(name));


             }
		}
	}

    /* constructor for processing parameters of a file uploading form */
    public ParameterSet(HttpServletRequest req, String saveDirectory)
    {
        try
        {
            this.multipartreq = new MultipartRequest(req,saveDirectory, maxPostSize);
            paramset=new Hashtable();
            Enumeration myenum =multipartreq.getParameterNames();
            while (myenum.hasMoreElements())
		    {
			    String name = (String) myenum.nextElement();
			    paramset.put( name,multipartreq.getParameter(name));
			    //printHashtable(name);
            }
        }
        catch(Exception e)
        {

            logger.error("ERROR IN ParameterSet");
			logger.error(e.getMessage(),e);

        }


    }

    /****************************************** Methods *******************************************/
    /*************************************************************************************************/

	public void printHashtable(String name)
	{
		System.out.println("Param name = "+ name +" value =  "+ this.paramset.get(name));

	}

	boolean hasEmptyParameter()
	{
		return paramset.contains("");
	}

    /* given the name of the parameter, returns its value */
	String valueForName(String name)
	{
		return (String) paramset.get(name);

	}
    /* if a parameter has more than one value, given the name of the parameter, returns a Vector of its values */
    Vector getValuesForParam(String paramname,HttpServletRequest req)
    {
        String values[] = req.getParameterValues(paramname);
        Vector vector = new Vector();
        if (values != null)
        {
             for (int i = 0; i < values.length; i++)
            {
                vector.add( values[i]);
            }
        }
        return vector;

    }
    /* make a DataSource(virtual sensor name, access right) object using parameters of a file uploading form */
    public DataSource fileUploader(String vsname,String saveDirectory)
    {
        DataSource ds=null;
        String name=null;
        String filename = null;
        String filetype = null;
        File file = null;
        try
        {
            Enumeration filesEnum = this.multipartreq.getFileNames();
            while (filesEnum.hasMoreElements())
            {
                name = (String)filesEnum.nextElement();
                filename = this.multipartreq.getFilesystemName(name);
                filetype = this.multipartreq.getContentType(name);
                file = this.multipartreq.getFile(name);

                if(filename!=null && filetype!=null )
                {     //rename the name of the Virtual Sensor to the unique name
                    //logger.info("filename " + filename);
                    //logger.info("name " + name);
                    //logger.info("abs path " + file.getAbsolutePath());
                    changeSensorName(file.getAbsolutePath(), vsname);
                    String newFilePath = file.getAbsolutePath().replace(file.getName(), "") + vsname+".xml";
                    File newFile = new File(newFilePath);

                    try {
                        FileUtils.moveFile(file, newFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ds = new DataSource(vsname,"4",vsname+".xml",filetype,saveDirectory);
                }
                if (file != null)
                {
                    logger.info("length: " + file.length());
                }
            }
        }
        catch(Exception e)
        {
            logger.error("ERROR IN fileUploader Method");
			logger.error(e.getMessage(),e);
          

        }
        return ds;
    }

    private static void changeSensorName(String filepath, String name) {
        File f=new File(filepath);

        Pattern pat = Pattern.compile("name[\\s]*=[\\s]*\"[\\s]*[\\S]+[\\s]*\"");
        Matcher match;
        FileInputStream fs = null;
        InputStreamReader in = null;
        BufferedReader br = null;

        StringBuffer sb = new StringBuffer();
        String textinLine;

        try {
            fs = new FileInputStream(f);
            in = new InputStreamReader(fs);
            br = new BufferedReader(in);

            while(true)
            {
                textinLine=br.readLine();
                if(textinLine==null)
                    break;
                if ( textinLine.contains("<virtual-sensor") ) {
                    match = pat.matcher(textinLine);
                    match.find();
                    textinLine=match.replaceFirst("name=\""+name+"\"");
                }
                sb.append(textinLine+"\n");
            }
            fs.close();
            in.close();
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            FileWriter fstream = new FileWriter(f);              // write file again
            BufferedWriter outobj = new BufferedWriter(fstream);
            outobj.write(sb.toString());
            outobj.close();

        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
    }

}
