package org.openiot.lsm.http;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UploadFileServlet
 */
@WebServlet("/UploadFileServlet")
public class UploadFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadFileServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

//	static final String SAVE_DIR = "/usr/local/tomcat7/webapps/SensorMiddlewareData/OpenIoTOntology/";
	static final String SAVE_DIR = "/root/apache-tomcat-7.0.32/SensorMiddlewareData/";
    static final int BUFFER_SIZE = 4096;
     
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
         
        // Gets file name for HTTP header
        String fileName = request.getHeader("fileName");
        String pro = request.getHeader("project").toLowerCase(); 
        
//      File saveFile = new File(SAVE_DIR + fileName);
        File saveFile = new File(SAVE_DIR + pro + "/" + fileName);
        
        // prints out all header values
        System.out.println("===== Begin headers =====");
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String headerName = names.nextElement();
            System.out.println(headerName + " = " + request.getHeader(headerName));         
        }
        System.out.println("===== End headers =====\n");
         
        // opens input stream of the request for reading data
        InputStream inputStream = request.getInputStream();
         
        // opens an output stream for writing file
        FileOutputStream outputStream = new FileOutputStream(saveFile);
         
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
        System.out.println("Receiving data...");
         
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
         
        System.out.println("Data received.");
        outputStream.close();
        inputStream.close();
         
        System.out.println("File written to: " + saveFile.getAbsolutePath());
         
        // sends response to client
        response.getWriter().print("UPLOAD DONE");
        response.getWriter().print("You can download from this link: http://lsm.deri.ie/");
    }

}
