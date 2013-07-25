package org.openiot.gsn.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class ChartDownload extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static transient Logger logger = Logger.getLogger(ChartDownload.class);

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	public void doPost ( HttpServletRequest req , HttpServletResponse res ) throws ServletException , IOException {
		
	}
}
