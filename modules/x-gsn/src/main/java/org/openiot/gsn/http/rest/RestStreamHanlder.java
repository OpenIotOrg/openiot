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

package org.openiot.gsn.http.rest;

import org.openiot.gsn.DataDistributer;
import org.openiot.gsn.Main;
import org.openiot.gsn.Mappings;
import org.openiot.gsn.beans.VSensorConfig;
import org.openiot.gsn.http.ac.DataSource;
import org.openiot.gsn.http.ac.GeneralServicesAPI;
import org.openiot.gsn.http.ac.User;
import org.openiot.gsn.storage.SQLUtils;
import org.openiot.gsn.storage.SQLValidator;
import org.openiot.gsn.utils.Helpers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.StringTokenizer;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

public class RestStreamHanlder extends HttpServlet {

	public static final int SUCCESS_200 = 200;

	private static final int _300 = 300;

	private static final String STREAMING = "/streaming/";

	private static transient Logger       logger     = Logger.getLogger ( RestStreamHanlder.class );

	public void doGet ( HttpServletRequest request , HttpServletResponse response ) throws ServletException{


		Object is2ndPass = request.getAttribute("2ndPass");
		Continuation continuation = ContinuationSupport.getContinuation(request);

        if(continuation.isExpired()){
            logger.debug("Continuation has expired.");
            return;
        }

        String[] cred = null;
        if (Main.getContainerConfig().isAcEnabled()) {
            cred = parseAuthorizationHeader(request);
            if (cred == null) {
                try {
                    response.setHeader("WWW-Authenticate", "Basic realm=\"GSN Access Control\"");// set the supported challenge
                    response.sendError(HttpStatus.SC_UNAUTHORIZED, "Unauthorized access.");
                }
                catch (IOException e) {
                    logger.debug(e.getMessage(), e);
                }
                return;
            }
        }

		if(is2ndPass == null) {
            continuation.setAttribute("2ndPass", Boolean.TRUE);
            continuation.setAttribute("status", new LinkedBlockingQueue<Boolean>(1));
            continuation.setTimeout(-1); // Disable the timeout on the continuation.
            continuation.suspend();
            final DefaultDistributionRequest streamingReq;
            try {
                URLParser parser = new URLParser(request);
                //
                if ( Main.getContainerConfig().isAcEnabled()){
                    String vsName = parser.getVSensorConfig().getName();
                    if (DataSource.isVSManaged(vsName)) {
                        User user = GeneralServicesAPI.getInstance().doLogin(cred[0], cred[1]);
                        if ((user == null || (! user.isAdmin() && ! user.hasReadAccessRight(vsName)))) {
                            response.setHeader("WWW-Authenticate", "Basic realm=\"GSN Access Control\"");// set the supported challenge
                            response.sendError(HttpStatus.SC_UNAUTHORIZED, "Unauthorized access.");
                            return;
                        }
                    }
                }
                //
                RestDelivery deliverySystem = new RestDelivery(continuation);
                streamingReq = DefaultDistributionRequest.create(deliverySystem, parser.getVSensorConfig(), parser.getQuery(), parser.getStartTime());
                DataDistributer.getInstance(deliverySystem.getClass()).addListener(streamingReq);
			}catch (Exception e) {
				logger.warn(e.getMessage());
                continuation.complete();
            }
		}else {
            boolean status = false;
            try{
                status = !continuation.getServletResponse().getWriter().checkError();
            } catch (Exception e) {
                logger.debug(e.getMessage(), e);
            }
            continuation.suspend();
            try {
                ((LinkedBlockingQueue<Boolean>)continuation.getAttribute("status")).put(status);
            } catch (InterruptedException e) {
                logger.debug(e.getMessage(), e);
            }
		}
	}
	/**
	 * This happens at the server
	 */
	public void doPost ( HttpServletRequest request , HttpServletResponse response ) throws ServletException  {
        try {
			URLParser parser = new URLParser(request);
            String vsName = parser.getVSensorConfig().getName();

            //
            if (Main.getContainerConfig().isAcEnabled()) {
                String[] cred = parseAuthorizationHeader(request);
                if (cred == null) {
                    try {
                        response.setHeader("WWW-Authenticate", "Basic realm=\"GSN Access Control\"");// set the supported challenge
                        response.sendError(HttpStatus.SC_UNAUTHORIZED, "Unauthorized access.");
                    }
                    catch (IOException e) {
                        logger.debug(e.getMessage(), e);
                    }
                    return;
                }
                if (DataSource.isVSManaged(vsName)){
                    User user = GeneralServicesAPI.getInstance().doLogin(cred[0], cred[1]);
                    if ((user == null || (! user.isAdmin() && ! user.hasReadAccessRight(vsName)))) {
                        response.setHeader("WWW-Authenticate", "Basic realm=\"GSN Access Control\"");// set the supported challenge
                        response.sendError(HttpStatus.SC_UNAUTHORIZED, "Unauthorized access.");
                        return;
                    }
                }
            }
            //
            Double notificationId = Double.parseDouble(request.getParameter(PushDelivery.NOTIFICATION_ID_KEY));
			String localContactPoint = request.getParameter(PushDelivery.LOCAL_CONTACT_POINT);
			if (localContactPoint == null) {
				logger.warn("Push streaming request received without "+PushDelivery.LOCAL_CONTACT_POINT+" parameter !");
				return;
			}
			//checking to see if there is an already registered notification id, in that case, we ignore (re)registeration.
			DeliverySystem delivery;
			if (parser.pushType.equals("wp"))
				delivery = new WPPushDelivery(localContactPoint,notificationId,response.getWriter(),parser.nClass,parser.nMessage);
			else
				delivery = new PushDelivery(localContactPoint,notificationId,response.getWriter());

			boolean isExist = DataDistributer.getInstance(delivery.getClass()).contains(delivery);
			if (isExist) {
				logger.debug("Keep alive request received for the notification-id:"+notificationId);
				response.setStatus(SUCCESS_200);
				delivery.close();
				return;
			}

			DefaultDistributionRequest distributionReq = DefaultDistributionRequest.create(delivery, parser.getVSensorConfig(), parser.getQuery(), parser.getStartTime());
			logger.debug("Rest request received: "+distributionReq.toString());
			DataDistributer.getInstance(delivery.getClass()).addListener(distributionReq);
			logger.debug("Streaming request received and registered:"+distributionReq.toString());
		}catch (Exception e) {
			logger.warn(e.getMessage(),e);
			return ;
		}
	}
	/**
	 * This happens at the client
	 */
	public void doPut( HttpServletRequest request , HttpServletResponse response ) throws ServletException  {
		double notificationId = Double.parseDouble(request.getParameter(PushDelivery.NOTIFICATION_ID_KEY));
		PushRemoteWrapper notification = NotificationRegistry.getInstance().getNotification(notificationId);
		try {
			if (notification!=null) {
				boolean status = notification.manualDataInsertion(request.getParameter(PushDelivery.DATA));
                if (status)
                    response.setStatus(SUCCESS_200);
                else
                    response.setStatus(_300);
			}else {
				logger.warn("Received a Http put request for an INVALID notificationId: " + notificationId);
				response.sendError(_300);
			}
		} catch (IOException e) {
			logger.warn("Failed in writing the status code into the connection.\n"+e.getMessage(),e);
		}
	}

    /**
     *
     * @param request
     * @return [username,password] or null if unable to retrieve these pieces of information.
     */
    private String[] parseAuthorizationHeader(HttpServletRequest request) {
        // Get username/password from the Authorization header
        String authHeader = request.getHeader("Authorization"); // form: BASIC d2VibWFzdGVyOnRyeTJndWVTUw
        if (authHeader != null) {
            String[] ahs = authHeader.split(" ");
            if (ahs.length == 2) {
                String b64UsernamPassword = ahs[1]; // we get: d2VibWFzdGVyOnRyeTJndWVTUw
                String userPass = new String(DatatypeConverter.parseBase64Binary(b64UsernamPassword)); // form: username:passsword
                String[] ups;
                if ((ups = userPass.split(":")).length == 2) {
                    return new String[]{
                            ups[0], // username
                            ups[1]  // password
                    };
                }
            }
        }
        return null;
    }

	class URLParser{
		private String query,tableName,pushType,nMessage;
		private int nClass;
		private long startTime;
		private VSensorConfig config;
		public URLParser(HttpServletRequest request) throws UnsupportedEncodingException, Exception {
			String requestURI = request.getRequestURI().substring(request.getRequestURI().toLowerCase().indexOf(STREAMING)+STREAMING.length());
			StringTokenizer tokens = new StringTokenizer(requestURI,"/");
			startTime = System.currentTimeMillis();
			String first = tokens.nextToken();
			if (first.startsWith("wp")){ //registering from a Windows Phone
				pushType = "wp";
				nClass = Integer.parseInt(first.substring(2,3));
				nMessage = URLDecoder.decode(first.substring(3),"UTF-8");
				query = tokens.nextToken();
			}else{
				query = first;
			}
			query = URLDecoder.decode(query,"UTF-8");
			if (tokens.hasMoreTokens()) 
				startTime= Helpers.convertTimeFromIsoToLong(URLDecoder.decode(tokens.nextToken(),"UTF-8"));
			tableName = SQLValidator.getInstance().validateQuery(query);
			if (tableName==null)
				throw new RuntimeException("Bad Table name in the query:"+query);
			/** IMPORTANT: We change the table names to lower-case as some databases (e.g., MySQL on linux) are case sensitive and in 
			 * general we use lower case for table names in GSN. **/
			tableName=tableName.trim();
			query = SQLUtils.newRewrite(query, tableName, tableName.toLowerCase()).toString();
			tableName=tableName.toLowerCase();
			config = Mappings.getConfig(tableName);
		}
		public VSensorConfig getVSensorConfig() {
			return config;
		}
		public String getQuery() {
			return query;
		}
		public long getStartTime() {
			return startTime;
		}

	}

}


