/**
*    Copyright (c) 2011-2014, OpenIoT
*   
*    This file is part of OpenIoT.
*
*    OpenIoT is free software: you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation, version 3 of the License.
*
*    OpenIoT is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
*
*     Contact: OpenIoT mailto: info@openiot.eu
*/

package org.openiot.gsn.http;

import org.openiot.gsn.Mappings;
import org.openiot.gsn.VirtualSensorInitializationFailedException;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.vsensor.AbstractVirtualSensor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

/**
 * @todo validation & security part
 */

public class FieldUpload extends HttpServlet {
	static final long serialVersionUID = 13;
	private static final transient Logger logger = Logger.getLogger( StreamElement.class );
	   
	public void  doGet ( HttpServletRequest req , HttpServletResponse res ) throws ServletException , IOException {
		doPost(req, res);
	}
	
	public void doPost ( HttpServletRequest req , HttpServletResponse res ) throws ServletException , IOException {
		String msg;
		Integer code;
		PrintWriter out = res.getWriter();
		ArrayList<String> paramNames = new ArrayList<String>();
		ArrayList<String> paramValues = new ArrayList<String>();
		
		//Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		if (!isMultipart) {
			out.write("not multipart!");
			code = 666;
			msg = "Error post data is not multipart!";
			logger.error(msg);
		} else {
			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Set overall request size constraint
			upload.setSizeMax(5*1024*1024);
			
			
			List items;
			try {
				// Parse the request
				items = upload.parseRequest(req);
			
				//building xml data out of the input
				String cmd = "";
				String vsname = "";
				Base64 b64 = new Base64();
				StringBuilder sb = new StringBuilder("<input>\n" );
				Iterator iter = items.iterator();
				while (iter.hasNext()) {
					FileItem item = (FileItem) iter.next();
				    if (item.getFieldName().equals("vsname")){
				    	//define which cmd block is sent
				    	sb.append("<vsname>"+item.getString()+"</vsname>\n");
				    	vsname = item.getString();
				    } else if (item.getFieldName().equals("cmd")){
				    	//define which cmd block is sent
				    	cmd = item.getString();
				    	sb.append("<command>"+item.getString()+"</command>\n");
				    	sb.append("<fields>\n");
				    } else if (item.getFieldName().split(";")[0].equals(cmd)) {
				    	//only for the defined cmd    	
				    	sb.append("<field>\n");
			    	    sb.append("<name>"+item.getFieldName().split(";")[1]+"</name>\n");
			    	    paramNames.add(item.getFieldName().split(";")[1]);
			    	    if (item.isFormField()) {
					    	sb.append("<value>"+item.getString()+"</value>\n");
					    	paramValues.add(item.getString());
			    	    } else {
			    	    	sb.append("<value>"+new String(b64.encode(item.get()))+"</value>\n");
					    	paramValues.add(new String(b64.encode(item.get())));
			    	    }
			    	    sb.append("</field>\n");
				    }
				}
				sb.append("</fields>\n");
				sb.append("</input>\n" );
			
				//do something with xml aka statement.toString()
			
			    AbstractVirtualSensor vs = null;
			    try {
			    	vs = Mappings.getVSensorInstanceByVSName( vsname ).borrowVS( );
			    	vs.dataFromWeb( cmd , paramNames.toArray(new String[]{}) , paramValues.toArray(new Serializable[]{}) );
			    } catch ( VirtualSensorInitializationFailedException e ) {
			      logger.warn("Sending data back to the source virtual sensor failed !: "+e.getMessage( ),e);
			    } finally {
			    	Mappings.getVSensorInstanceByVSName(vsname).returnVS(vs);
			    }
				
				code = 200;
				msg = "The upload to the virtual sensor went successfully! ("+vsname+")";
			} catch (ServletFileUpload.SizeLimitExceededException e) {
				code = 600;
				msg = "Upload size exceeds maximum limit!";
				logger.error(msg, e);
	        } catch(Exception e){
				code = 500;
				msg = "Internal Error: "+e;
				logger.error(msg, e);
			}
			
		}
		//callback to the javascript
		out.write("<script>window.parent.GSN.msgcallback('"+msg+"',"+code+");</script>");
	}
}