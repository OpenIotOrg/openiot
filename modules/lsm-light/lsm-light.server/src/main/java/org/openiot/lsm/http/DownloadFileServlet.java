package org.openiot.lsm.http;

/**
 * Copyright (c) 2011-2014, OpenIoT
 *
 * This library is free software; you can redistribute it and/or
 * modify it either under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation
 * (the "LGPL"). If you do not alter this
 * notice, a recipient may use your version of this file under the LGPL.
 *
 * You should have received a copy of the LGPL along with this library
 * in the file COPYING-LGPL-2.1; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTY
 * OF ANY KIND, either express or implied. See the LGPL  for
 * the specific language governing rights and limitations.
 *
 * Contact: OpenIoT mailto: info@openiot.eu
 */

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DowloadFileServlet
 */
//@WebServlet(
//		urlPatterns = { "/download" }, 
//		initParams = { 
//				@WebInitParam(name = "filename", value = "")
//		})
public class DownloadFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static final String SAVE_DIR = "/root/apache-tomcat-7.0.32/SensorMiddlewareData/";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadFileServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//get the 'file' parameter
		
		String project = (String) request.getParameter("project");
	    if (project == null || project.equals(""))
	      throw new ServletException(
	          "Invalid or non-existent project parameter.");
	    
	    String fileName = (String) request.getParameter("filename");
	    if (fileName == null || fileName.equals(""))
	      throw new ServletException(
	          "Invalid or non-existent file parameter.");
	    response.setContentType("text/xml");
	    response.setHeader("Pragma", "no-cache"); // HTTP 1.0
	    response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
	    PrintWriter out = response.getWriter();
	    try {

	      File pdf = new File(SAVE_DIR + "/" + project.toLowerCase(Locale.ENGLISH)+"/"+fileName);
	      FileInputStream input = new FileInputStream(pdf);
//	      InputStream input = getServletContext().getResourceAsStream("/"+fileName);

	      String str = convertStreamToString(input);
//	      System.out.println(str);
	      out.println(str);
	      out.close();	     
	    } catch (IOException ioe) {
	      throw new ServletException(ioe.getMessage());
	    } 	    
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	public String convertStreamToString(InputStream is)
            throws IOException {
        //
        // To convert the InputStream to String we use the
        // Reader.read(char[] buffer) method. We iterate until the
        // Reader return -1 which means there's no more data to
        // read. We use the StringWriter class to produce the string.
        //
        if (is != null) {
            Writer writer = new StringWriter();
 
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {        
            return "";
        }
    }
}
