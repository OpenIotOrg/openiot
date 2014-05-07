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
 * @author Julien Eberle
*/

package org.openiot.gsn.http.rest;

import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.StreamElement;

import java.io.IOException;
import java.io.Writer;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

public class WPPushDelivery implements DeliverySystem {
	
	//the minimum amount of seconds between two notifications
	//use that to limit the notification rate, especially for testing
	private int DELAY = 60*15;

	//1: tile notification, 2: toast notification, 3:raw notification
	private int notificationClass = 3;
	
	//a message to be used as text or referring to the underlying query 
	private String notificationMessage = "";

	private XStream xstream = StreamElement4Rest.getXstream();

	private boolean isClosed = false;

	private static transient Logger       logger     = Logger.getLogger ( WPPushDelivery.class );

	private HttpPost httpPost;

	private DefaultHttpClient httpclient = new DefaultHttpClient();

	private Writer writer;
	
	private String lastmessage = "";
	
	private long lastTimeSent = 0;

	private double notificationId;

	/**
	 * Builds a new Windows Phone Push Delivery class
	 * @param deliveryContactPoint: the unique url for Windows phone push service
	 * @param notificaitonId: the id for identifying the phone on GSN
	 * @param writer: where to write back the output structure
	 * @param notificationClass: 1: tile notification, 2: toast notification, 3:raw notification
	 * @param notificationMessage: a message to be used as text or referring to the underlying query
	 */
	public WPPushDelivery(String deliveryContactPoint,double notificaitonId, Writer writer,int notificationClass,String notificationMessage) {
		httpPost = new HttpPost(deliveryContactPoint);
		this.notificationClass = notificationClass;
		this.writer = writer;
		this.notificationId = notificaitonId;
		this.notificationMessage = notificationMessage;
	}

    /**
    * Write the structure of the data as xml in the writer given by the constructor.
    * Can be useful in case of raw notification only.
    * @param fields the field structure to write.
    * @throws IOException in case the writer fails.
    */
	public void writeStructure(DataField[] fields) throws IOException {
		String xml = xstream.toXML(fields);
		if (writer ==null)
			throw new RuntimeException("The writer structure is null.");
		writer.write(xml);
		writer=  null;
	}

	/**
	 * Generate the content of the notification according to the notification class 
	 * and sends it to the phone. The content of the streamElement can be used in 
	 * different ways and this method should be adapted to the specific application.
	 * @param se the streamElement
	 */
	public boolean writeStreamElement(StreamElement se) {
		String xml = xstream.toXML(new StreamElement4Rest(se)); //raw notification
		
		if (notificationClass == 2) {     //toast notification
			httpPost.setHeader("X-WindowsPhone-Target", "toast");
			xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
	                "<wp:Notification xmlns:wp=\"WPNotification\">" +
	                   "<wp:Toast>" +
	                        "<wp:Text1>GSN Push Service</wp:Text1>" +
	                        "<wp:Text2>"+ notificationMessage +"</wp:Text2>" +
	                        "<wp:Param></wp:Param>" +
	                   "</wp:Toast> " +
	                "</wp:Notification>";
		}
		if (notificationClass == 1) {   //tile notification
			httpPost.setHeader("X-WindowsPhone-Target", "token"); 
			xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
	                  "<wp:Notification xmlns:wp=\"WPNotification\">" +
	                      "<wp:Tile ID=\"tile_id\">" +
	                        "<wp:BackgroundImage>/Images/front.png</wp:BackgroundImage>" +
	                        "<wp:Count></wp:Count>" +
	                        "<wp:Title>GSN Push Service</wp:Title>" +
	                        "<wp:BackBackgroundImage>/Images/back.png</wp:BackBackgroundImage>" +
	                        "<wp:BackTitle>" + notificationMessage+ "</wp:BackTitle>" +
	                        "<wp:BackContent></wp:BackContent>" +
	                     "</wp:Tile> " +
	                  "</wp:Notification>";
		}
		
		if (xml.equalsIgnoreCase(lastmessage)){ // don't send twice the same message
			return true;
		}
		if (se.getTimeStamp() < lastTimeSent + DELAY){// limit the rate for sending messages
			return true;
		}
		lastmessage = xml;
		lastTimeSent = se.getTimeStamp();
		boolean success = sendData(xml);
		isClosed = !success;
		return success;
	}

	/**
	 * No need to keep the connection alive
	 */
    public boolean writeKeepAliveStreamElement() {
        return true;
    }

    /**
     * closing all connections
     */
    public void close() {
		httpclient.getConnectionManager().shutdown();
		isClosed = true;
	}

	public boolean isClosed() {
		return isClosed  ;
	}

	/**
	 * Send the given xml string to the contact point.
	 * Take action according to the reply from Microsoft's servers (http://msdn.microsoft.com/en-us/library/ff941100%28VS.92%29.aspx).
	 * 
	 * @param xml
	 * @return
	 */
	private boolean sendData(String xml) {
		System.out.println(xml);
		try {
			httpPost.setHeader("X-NotificationClass",""+notificationClass);
			httpPost.setEntity(new StringEntity(xml, HTTP.UTF_8));
			HttpResponse response = httpclient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			String nStatus = "NA";
			String dStatus = "NA";
			String sStatus = "NA";
			response.getEntity().getContent().close(); // releasing the connection to the http client's pool
			if (response.containsHeader("X-NotificationStatus"))
				nStatus = response.getFirstHeader("X-NotificationStatus").getValue();
			if (response.containsHeader("X-DeviceConnectionStatus"))
				dStatus = response.getFirstHeader("X-DeviceConnectionStatus").getValue();
			if (response.containsHeader("X-SubscriptionStatus"))
				sStatus = response.getFirstHeader("X-SubscriptionStatus").getValue();
			logger.warn("Status for client "+notificationId+":(" +statusCode+")" + nStatus + ", " + dStatus + "," +sStatus);
			if (statusCode != RestStreamHanlder.SUCCESS_200) {
				return false;
			}
			if (nStatus.equalsIgnoreCase("QueueFull")){
				lastTimeSent = System.currentTimeMillis()/1000 + 60*60*2;
			}
			if (nStatus.equalsIgnoreCase("Suppressed")){
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.warn(e.getMessage(),e);
			return false;
		}

	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WPPushDelivery that = (WPPushDelivery) o;
        if (Double.compare(that.notificationId, notificationId) != 0) return false;
        if (httpPost != null ? !httpPost.getURI().equals(that.httpPost.getURI()) : that.httpPost != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = httpPost != null ? httpPost.getURI().hashCode() : 0;
        temp = notificationId != +0.0d ? Double.doubleToLongBits(notificationId) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
