package org.openiot.lsm.server;
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
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Properties;


import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.openiot.lsm.beans.Observation;
import org.openiot.lsm.beans.RDFTuple;
import org.openiot.lsm.beans.Sensor;
import org.openiot.lsm.beans.User;
import org.openiot.lsm.schema.LSMSchema;
import org.openiot.lsm.utils.ObsConstant;




public class LSMTripleStore implements LSMServer {
    static final int BUFFER_SIZE = 4096;
	final String RDFServletURL = ObsConstant.ServerHost + "rdfservlet";
	final String ObjectServletURL = ObsConstant.ServerHost + "objservlet";
    final String UPLOAD_URL = ObsConstant.ServerHost + "upload";
	private User user;
	
		
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public boolean sensorAdd(String triple,String graphURL) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;  
        DataOutputStream dos = null;  
        String api = "1";
        String urlString = RDFServletURL+"?api="+api+"&username="+user.getUsername()+"&pass="+user.getPass()
        		+"&responsetype=xml";
        try{  
            URL url = new URL(urlString);  

      // Open a HTTP connection to the URL  

         conn = (HttpURLConnection) url.openConnection();  
         conn.setDoInput(true);  
         conn.setDoOutput(true);  
         conn.setUseCaches(false);  

         // Use a post method.  
         conn.setRequestMethod("POST");  

         conn.setRequestProperty("Connection", "Keep-Alive");  
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
         conn.setRequestProperty("graphURL", graphURL);
         
         dos = new DataOutputStream( conn.getOutputStream() );  
//         System.out.println(triple);
         dos.writeBytes(triple);  
         dos.flush();  
         dos.close();  
         
      // always check HTTP response code from server
	     int responseCode = conn.getResponseCode();
	     if (responseCode == HttpURLConnection.HTTP_OK) {
	            // reads server's response
	    	 InputStream is =conn.getInputStream();
	         int ch;
	         StringBuffer sb = new StringBuffer();        
	         while ((ch = is.read()) != -1) {
	             sb.append((char)ch);
	         }
	         System.out.println(sb);
	         Document document = DocumentHelper.parseText(sb.toString());
	  		 Element root = document.getRootElement();
	  		 List<Element> elements = root.elements();
	  		 for(Element elm:elements){
				 if(elm.getName().equals("login")){
					 String isLogin = elm.getStringValue();
					 if(isLogin.equals("false"))
						 return false;
				 }else if(elm.getName().equals("feed")){
					 String isFeed = elm.getStringValue();
					 if(isFeed.equals("false"))
						 return false;
				 }
			 }          
	         is.close();
	     } else {
	         System.out.println("Server returned non-OK code: " + responseCode);
	     }
        }catch (Exception ex) {  
        	ex.printStackTrace();
            System.out.println("cannot send data to server");     
        }  
        return true;
    }

	@Override
	public boolean sensorDataUpdate(String triples,String graphURL){
		HttpURLConnection conn = null;  
        DataOutputStream dos = null;  
        String api = "2";
        String urlString = RDFServletURL+"?api="+api+"&username="+user.getUsername()+"&pass="+user.getPass()
        		+"&responsetype=xml";
        try{  
            URL url = new URL(urlString);  

      // Open a HTTP connection to the URL  

         conn = (HttpURLConnection) url.openConnection();  
         conn.setDoInput(true);  
         conn.setDoOutput(true);  
         conn.setUseCaches(false);  

         // Use a post method.  
         conn.setRequestMethod("POST");  

         conn.setRequestProperty("Connection", "Keep-Alive");  
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
         conn.setRequestProperty("graphURL", graphURL);
         
         dos = new DataOutputStream( conn.getOutputStream() );  
//         System.out.println(triples);
         dos.writeBytes(triples);  
         dos.flush();  
         dos.close();  
         
         InputStream is =conn.getInputStream();
         int ch;
         StringBuffer sb = new StringBuffer();        
         while ((ch = is.read()) != -1) {
             sb.append((char)ch);
         }
         Document document = DocumentHelper.parseText(sb.toString());
  		 Element root = document.getRootElement();
  		 List<Element> elements = root.elements();
  		 for(Element elm:elements){
			 if(elm.getName().equals("login")){
				 String isLogin = elm.getStringValue();
				 if(isLogin.equals("false"))
					 return false;
			 }else if(elm.getName().equals("feed")){
				 String isFeed = elm.getStringValue();
				 if(isFeed.equals("false"))
					 return false;
			 }
		 }
          
         is.close();
        }catch (Exception ex) {  
        	ex.printStackTrace();
            System.out.println("cannot send the data to LSM Server");     
        }
		return true;
	}

	@Override
	public boolean sensorDelete(String sensorURL,String graphURL) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;  
        DataOutputStream dos = null;  
        String api = "3";
        String urlString = RDFServletURL+"?api="+api+"&username="+user.getUsername()+"&pass="+user.getPass()
        		+"&responsetype=xml";
        try{  
            URL url = new URL(urlString);  

      // Open a HTTP connection to the URL  

         conn = (HttpURLConnection) url.openConnection();  
         conn.setDoInput(true);  
         conn.setDoOutput(true);  
         conn.setUseCaches(false);  

         // Use a post method.  
         conn.setRequestMethod("POST");  

         conn.setRequestProperty("Connection", "Keep-Alive");  
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
         conn.setRequestProperty("graphURL", graphURL);
         
         dos = new DataOutputStream( conn.getOutputStream() );  
         dos.writeBytes(sensorURL);  
         dos.flush();  
         dos.close();  
         
         InputStream is =conn.getInputStream();
         int ch;
         StringBuffer sb = new StringBuffer();        
         while ((ch = is.read()) != -1) {
             sb.append((char)ch);
         }
         
         Document document = DocumentHelper.parseText(sb.toString());
  		 Element root = document.getRootElement();
  		 List<Element> elements = root.elements();
  		 for(Element elm:elements){
			 if(elm.getName().equals("login")){
				 String isLogin = elm.getStringValue();
				 if(isLogin.equals("false"))
					 return false;
			 }else if(elm.getName().equals("feed")){
				 String isFeed = elm.getStringValue();
				 if(isFeed.equals("false"))
					 return false;
			 }
		 }
          
         is.close();
        }catch (Exception ex) {  
        	ex.printStackTrace();
            System.out.println("cannot send the data to LSM Server");     
        }
		return true;
	}
	
	@Override
	public boolean deleteAllReadings(String sensorURL,String graphURL) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;  
        DataOutputStream dos = null;  
        String api = "4";
        String urlString = RDFServletURL+"?api="+api+"&username="+user.getUsername()+"&pass="+user.getPass()
        		+"&responsetype=xml";
        try{  
            URL url = new URL(urlString);  

      // Open a HTTP connection to the URL  

         conn = (HttpURLConnection) url.openConnection();  
         conn.setDoInput(true);  
         conn.setDoOutput(true);  
         conn.setUseCaches(false);  

         // Use a post method.  
         conn.setRequestMethod("POST");  

         conn.setRequestProperty("Connection", "Keep-Alive");  
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
         conn.setRequestProperty("graphURL", graphURL);
         
         dos = new DataOutputStream( conn.getOutputStream() );  
//         System.out.println(triples);
         dos.writeBytes(sensorURL);  
         dos.flush();  
         dos.close();  
         
         InputStream is =conn.getInputStream();
         int ch;
         StringBuffer sb = new StringBuffer();        
         while ((ch = is.read()) != -1) {
             sb.append((char)ch);
         }
         System.out.println(sb.toString());
         Document document = DocumentHelper.parseText(sb.toString());
  		 Element root = document.getRootElement();
  		 List<Element> elements = root.elements();
  		 for(Element elm:elements){
 			 if(elm.getName().equals("login")){
 				 String isLogin = elm.getStringValue();
 				 if(isLogin.equals("false"))
 					 return false;
 			 }else if(elm.getName().equals("feed")){
 				 String isFeed = elm.getStringValue();
 				 if(isFeed.equals("false"))
 					 return false;
 			 }
 		 }
          
         is.close();
        }catch (Exception ex) {  
        	ex.printStackTrace();
            System.out.println("cannot send the data to LSM Server");     
        }
		return true;
	}

	@Override
	public boolean deleteAllReadings(String sensorURL, String graphURL,String dateOperator,
			Date fromTime, Date toTime) {
		// TODO Auto-generated method stub
				HttpURLConnection conn = null;  
		        DataOutputStream dos = null;  
		        String api = "delete";
		        String urlString = RDFServletURL+"?api="+api+"&username="+user.getUsername()+"&pass="+user.getPass()
		        		+"&responsetype=xml";
		        try{  
		            URL url = new URL(urlString);  

		      // Open a HTTP connection to the URL  

		         conn = (HttpURLConnection) url.openConnection();  
		         conn.setDoInput(true);  
		         conn.setDoOutput(true);  
		         conn.setUseCaches(false);  

		         // Use a post method.  
		         conn.setRequestMethod("POST");  

		         conn.setRequestProperty("Connection", "Keep-Alive");  
		         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
		         conn.setRequestProperty("graphURL", graphURL);
		         conn.setRequestProperty("dateOperator", dateOperator);
		         conn.setRequestProperty("fromTime", fromTime.toString());
		         conn.setRequestProperty("toTime", toTime.toString());
		         
		         dos = new DataOutputStream( conn.getOutputStream() );  
//		         System.out.println(triples);
		         dos.writeBytes(sensorURL);  
		         dos.flush();  
		         dos.close();  
		         
		         InputStream is =conn.getInputStream();
		         int ch;
		         StringBuffer sb = new StringBuffer();        
		         while ((ch = is.read()) != -1) {
		             sb.append((char)ch);
		         }
		         Document document = DocumentHelper.parseText(sb.toString());
		  		 Element root = document.getRootElement();
		  		 List<Element> elements = root.elements();
		  		 for(Element elm:elements){
		  			 if(elm.getName().equals("login")){
		  				 String isLogin = elm.getStringValue();
		  				 if(isLogin.equals("false"))
		  					 return false;
		  			 }else if(elm.getName().equals("feed")){
		  				 String isFeed = elm.getStringValue();
		  				 if(isFeed.equals("false"))
		  					 return false;
		  			 }
		  		 }
		         is.close();
		        }catch (Exception ex) {  
		        	ex.printStackTrace();
		            System.out.println("cannot send the data to LSM Server");     
		        }
				return true;
	}

	@Override
	public Sensor getSensorById(String sensorURL,String graphURL) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;  
        DataOutputStream dos = null;  
        String api = "5";
        String urlString = RDFServletURL+"?api="+api+"&username="+user.getUsername()+"&pass="+user.getPass()
        		+"&responsetype=object";
        Sensor sensor = null;
        try{  
            URL url = new URL(urlString);  

      // Open a HTTP connection to the URL  

         conn = (HttpURLConnection) url.openConnection();  
         conn.setDoInput(true);  
         conn.setDoOutput(true);  
         conn.setUseCaches(false);  

         // Use a post method.  
         conn.setRequestMethod("POST");  

         conn.setRequestProperty("Connection", "Keep-Alive");  
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
         conn.setRequestProperty("graphURL", graphURL);
         
         dos = new DataOutputStream( conn.getOutputStream() );  
         dos.writeBytes(sensorURL);  
         dos.flush();  
         dos.close();  
         
         ObjectInputStream outputFromServlet =new ObjectInputStream(conn.getInputStream());
         sensor = (Sensor) outputFromServlet .readObject();
         System.out.println(sensor.getId());
         
        }catch (Exception ex) {  
        	ex.printStackTrace();
            System.out.println("Sorry!!!Your sensor does not exist. Please check your sensorURL i");     
        }
		return sensor;
	}

	@Override
	public Sensor getSensorBySource(String sensorsource,String graphURL) {
		// TODO Auto-generated method stub		
		HttpURLConnection conn = null;  
        DataOutputStream dos = null;  
        String api = "6";
        String urlString = RDFServletURL+"?api="+api+"&username="+user.getUsername()+"&pass="+user.getPass()
        		+"&responsetype=object";
        Sensor sensor = null;
        try{  
            URL url = new URL(urlString);  
      // Open a HTTP connection to the URL  

         conn = (HttpURLConnection) url.openConnection();  
         conn.setDoInput(true);  
         conn.setDoOutput(true);  
         conn.setUseCaches(false);  

         // Use a post method.  
         conn.setRequestMethod("POST");  

         conn.setRequestProperty("Connection", "Keep-Alive");  
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
         conn.setRequestProperty("graphURL", graphURL);
         
         dos = new DataOutputStream( conn.getOutputStream() );  
         dos.writeBytes(sensorsource);  
         dos.flush();  
         dos.close();  
         
         ObjectInputStream outputFromServlet =new ObjectInputStream(conn.getInputStream());
         sensor = (Sensor) outputFromServlet .readObject();
         System.out.println(sensor.getId());         
        }catch (Exception ex) {  
        	ex.printStackTrace();
            System.out.println("Sorry!!!Your sensor does not exist. Please check your sensorsource!");     
            return null;
        }
		return sensor;
	}

	/**
	 * add new Sensor
	 *
	 */
	@Override
	public String sensorAdd(Sensor sensor) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;  
        ObjectOutputStream dos = null;  
        int responseCode = 0;
        String api = "21";
        String urlString = ObjectServletURL+"?api="+api+"&username="+user.getUsername()
        		+"&pass="+user.getPass() +"&responsetype=xml";
        try{  
            URL url = new URL(urlString);  

      // Open a HTTP connection to the URL  

         conn = (HttpURLConnection) url.openConnection();  
         conn.setDoInput(true);  
         conn.setDoOutput(true);  
         conn.setUseCaches(false);  

         // Use a post method.  
         conn.setRequestMethod("POST");  

         conn.setRequestProperty("Connection", "Keep-Alive");  
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
        
         dos = new ObjectOutputStream( conn.getOutputStream() );  
         System.out.println(sensor.getId());
         dos.writeObject(sensor);  
         dos.flush();  
         dos.close();  
         
         responseCode = conn.getResponseCode();
	     if (responseCode == HttpURLConnection.HTTP_OK) {
	            // reads server's response
	    	 InputStream is =conn.getInputStream();
	         int ch;
	         StringBuffer sb = new StringBuffer();        
	         while ((ch = is.read()) != -1) {
	             sb.append((char)ch);
	         }
	         is.close();
	         System.out.println(sb);
	         return sb.toString();	  		
	  	}	  		
        }catch (Exception ex) {  
        	ex.printStackTrace();
            System.out.println("cannot send data to server");     
        }
        return responseCode+"";
	}

	@Override
	public boolean sensorDataUpdate(Observation observation){
		// TODO Auto-generated method stub
			HttpURLConnection conn = null;  
	        ObjectOutputStream dos = null;  
	        String api = "22";
	        String urlString = ObjectServletURL+"?api="+api+"&username="+user.getUsername()+"&pass="+user.getPass()
	        		+"&responsetype=xml";
	        try{  
	            URL url = new URL(urlString);  

	      // Open a HTTP connection to the URL  

	         conn = (HttpURLConnection) url.openConnection();  
	         conn.setDoInput(true);  
	         conn.setDoOutput(true);  
	         conn.setUseCaches(false);  

	         // Use a post method.  
	         conn.setRequestMethod("POST");  

	         conn.setRequestProperty("Connection", "Keep-Alive");  
	         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
	        
	         dos = new ObjectOutputStream( conn.getOutputStream() );  
//	         System.out.println(observation.getId());
	         dos.writeObject(observation);  
	         dos.flush();  
	         dos.close();  
	         
	         InputStream is =conn.getInputStream();
	         int ch;
	         StringBuffer sb = new StringBuffer();        
	         while ((ch = is.read()) != -1) {
	             sb.append((char)ch);
	         }
	         System.out.println(sb.toString());
	         Document document = DocumentHelper.parseText(sb.toString());
	  		 Element root = document.getRootElement();
	  		 List<Element> elements = root.elements();
	  		 for(Element elm:elements){
	 			 if(elm.getName().equals("login")){
	 				 String isLogin = elm.getStringValue();
	 				 if(isLogin.equals("false"))
	 					 return false;
	 			 }else if(elm.getName().equals("feed")){
	 				 String isFeed = elm.getStringValue();
	 				 if(isFeed.equals("false"))
	 					 return false;
	 			 }
	 		 }
	         is.close();
	        }catch (Exception ex) {  
	        	ex.printStackTrace();
	            System.out.println("cannot send the data to LSM Server");     
	        }
			return true;
	}
	
	
	@SuppressWarnings("null")
	@Override
	public boolean pushRDF(String graphURL,String triples) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;  
        ObjectOutputStream dos = null;  
        String api = "23";
        String urlString = ObjectServletURL+"?api="+api+"&username="+user.getUsername()+"&pass="+user.getPass()
        		+"&responsetype=xml";
        try{  
            URL url = new URL(urlString);  

      // Open a HTTP connection to the URL  

         conn = (HttpURLConnection) url.openConnection();  
         conn.setDoInput(true);  
         conn.setDoOutput(true);  
         conn.setUseCaches(false);  

         // Use a post method.  
         conn.setRequestMethod("POST");  

         conn.setRequestProperty("Connection", "Keep-Alive");  
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
         
         RDFTuple tuple = new RDFTuple(graphURL,triples);
         dos = new ObjectOutputStream( conn.getOutputStream() );
         dos.writeObject(tuple);  
         dos.flush();  
         dos.close();  
         
         InputStream is =conn.getInputStream();
         int ch;
         StringBuffer sb = new StringBuffer();        
         while ((ch = is.read()) != -1) {
             sb.append((char)ch);
         }
//         System.out.println(sb.toString());
         Document document = DocumentHelper.parseText(sb.toString());
  		 Element root = document.getRootElement();
  		 List<Element> elements = root.elements();
  		 for(Element elm:elements){
 			 if(elm.getName().equals("login")){
 				 String isLogin = elm.getStringValue();
 				 if(isLogin.equals("false"))
 					 return false;
 			 }else if(elm.getName().equals("feed")){
 				 String isFeed = elm.getStringValue();
 				 if(isFeed.equals("false"))
 					 return false;
 			 }
 		 }
         is.close();
        }catch (Exception ex) {  
        	ex.printStackTrace();
            System.out.println("cannot send the data to LSM Server");   
            return false;
        }
		return true;
	}

	@Override
	public boolean deleteTriples(String graphURL, String triples) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;  
        ObjectOutputStream dos = null;  
        String api = "24";
        String urlString = ObjectServletURL+"?api="+api+"&username="+user.getUsername()+"&pass="+user.getPass()
        		+"&responsetype=xml";
        try{  
            URL url = new URL(urlString);  

      // Open a HTTP connection to the URL  

         conn = (HttpURLConnection) url.openConnection();  
         conn.setDoInput(true);  
         conn.setDoOutput(true);  
         conn.setUseCaches(false);  

         // Use a post method.  
         conn.setRequestMethod("POST");  

         conn.setRequestProperty("Connection", "Keep-Alive");  
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
         
         RDFTuple tuple = new RDFTuple(graphURL,triples);
         dos = new ObjectOutputStream( conn.getOutputStream() );
         dos.writeObject(tuple);  
         dos.flush();  
         dos.close();  
         
         InputStream is =conn.getInputStream();
         int ch;
         StringBuffer sb = new StringBuffer();        
         while ((ch = is.read()) != -1) {
             sb.append((char)ch);
         }
//         System.out.println(sb.toString());
         Document document = DocumentHelper.parseText(sb.toString());
  		 Element root = document.getRootElement();
  		 List<Element> elements = root.elements();
  		 for(Element elm:elements){
 			 if(elm.getName().equals("login")){
 				 String isLogin = elm.getStringValue();
 				 if(isLogin.equals("false"))
 					 return false;
 			 }else if(elm.getName().equals("feed")){
 				 String isFeed = elm.getStringValue();
 				 if(isFeed.equals("false"))
 					 return false;
 			 }
 		 }
         is.close();
        }catch (Exception ex) {  
        	ex.printStackTrace();
            System.out.println("cannot send the data to LSM Server");   
            return false;
        }
		return true;
	}

	@Override
	public boolean deleteTriples(String graphURL) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;  
        ObjectOutputStream dos = null;  
        String api = "24";
        String urlString = ObjectServletURL+"?api="+api+"&username="+user.getUsername()+"&pass="+user.getPass()
        		+"&responsetype=xml";
        try{  
            URL url = new URL(urlString);  

      // Open a HTTP connection to the URL  

         conn = (HttpURLConnection) url.openConnection();  
         conn.setDoInput(true);  
         conn.setDoOutput(true);  
         conn.setUseCaches(false);  

         // Use a post method.  
         conn.setRequestMethod("POST");  

         conn.setRequestProperty("Connection", "Keep-Alive");  
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
         
         RDFTuple tuple = new RDFTuple(graphURL,"all");
         dos = new ObjectOutputStream( conn.getOutputStream() );
         dos.writeObject(tuple);  
         dos.flush();  
         dos.close();  
         
         InputStream is =conn.getInputStream();
         int ch;
         StringBuffer sb = new StringBuffer();        
         while ((ch = is.read()) != -1) {
             sb.append((char)ch);
         }
//         System.out.println(sb.toString());
         Document document = DocumentHelper.parseText(sb.toString());
  		 Element root = document.getRootElement();
  		 List<Element> elements = root.elements();
  		 for(Element elm:elements){
 			 if(elm.getName().equals("login")){
 				 String isLogin = elm.getStringValue();
 				 if(isLogin.equals("false"))
 					 return false;
 			 }else if(elm.getName().equals("feed")){
 				 String isFeed = elm.getStringValue();
 				 if(isFeed.equals("false"))
 					 return false;
 			 }
 		 }
         is.close();
        }catch (Exception ex) {  
        	ex.printStackTrace();
            System.out.println("cannot send the data to LSM Server");   
            return false;
        }
		return true;
	}

	@Override
	public void uploadSchema(LSMSchema schema,String name) {
		// TODO Auto-generated method stub
		try{
			URL url = new URL(UPLOAD_URL);
	        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
	        httpConn.setUseCaches(false);
	        httpConn.setDoOutput(true);
	        httpConn.setRequestMethod("POST");
	        // sets file name as a HTTP header
	        httpConn.setRequestProperty("fileName", name);
	        httpConn.setRequestProperty("project", "openiot");
	        // opens output stream of the HTTP connection for writing data
	        OutputStream outputStream = httpConn.getOutputStream();
	 
	        System.out.println("Start writing data...");
	        schema.getBase().write(outputStream,"RDF/XML");
	        System.out.println("Data was written.");
	        outputStream.close();

	        // always check HTTP response code from server
	        int responseCode = httpConn.getResponseCode();
	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            // reads server's response
	            BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    httpConn.getInputStream()));
	            String response = reader.readLine();
	            System.out.println("Server's response: " + response);
	        } else {
	            System.out.println("Server returned non-OK code: " + responseCode);
	        }
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	

}
