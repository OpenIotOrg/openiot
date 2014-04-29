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

package org.openiot.lsm.security.oauth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.openiot.commons.util.PropertyManagement;
import org.openiot.lsm.functionalont.model.beans.OSDSpecBean;
import org.openiot.lsm.security.oauth.mgmt.Permission;
import org.openiot.lsm.security.oauth.mgmt.Role;
import org.openiot.lsm.security.oauth.mgmt.User;


/**
 * 
 * @author Hoan Nguyen Mau Quoc
 * 
 */
public class LSMSDUMHttpManager {
	String LSMSDumURL;
	private String lsmFunctionalGraphURL;
	final static String OSDSpecBean = "OSDSpecBean";

	public LSMSDUMHttpManager(String funcGraphURL, String serverAddress) {
		init(funcGraphURL, serverAddress);
	}

	public LSMSDUMHttpManager() {
		PropertyManagement props = new PropertyManagement();
		String server = props.getLSMClientConnectionServerHost();
		String funcGraphURL = props.getSdumLsmFunctionalGraph();
		init(funcGraphURL, server);
	}

	private void init(String funcGraphURL, String serverAddress) {
		this.lsmFunctionalGraphURL = funcGraphURL;
		LSMSDumURL = serverAddress + "sdum";
	}

	public String getlsmFunctionalGraphURL() {
		return lsmFunctionalGraphURL;
	}

	public void setlsmFunctionalGraphURL(String lsmFunctionalGraphURL) {
		lsmFunctionalGraphURL = lsmFunctionalGraphURL;
	}


	public void updateOSDSPecBean(OSDSpecBean specBean) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		try {
			URL url = new URL(LSMSDumURL);
			String name = OSDSpecBean;
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("operator", "update");
			conn.setRequestProperty("FunctionalGraphURL", lsmFunctionalGraphURL);
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			dos = new ObjectOutputStream(conn.getOutputStream());
			dos.writeObject(specBean);
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// reads server's response
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String response = reader.readLine();
				System.out.println("Server's response: " + response);
			} else {
				System.out.println("Server returned non-OK code: " + responseCode);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("cannot send data to server");
		}
	}
	
	public void deleteOSDSPecBean(String osdspecId) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		try {
			URL url = new URL(LSMSDumURL);
			String name = OSDSpecBean;
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("FunctionalGraphURL", lsmFunctionalGraphURL);
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("operator", "delete");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "text/html");

			dos = new ObjectOutputStream(conn.getOutputStream());
			dos.writeObject(osdspecId);
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				System.out.println("Server's response: " + responseCode);
			} else {
				System.out.println("Server returned non-OK code: " + responseCode);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("cannot send data to server");
		}
	}

	public void addOSDSPecBean(OSDSpecBean specBean) {
		// TODO Auto-generated method stub
		HttpURLConnection conn = null;
		ObjectOutputStream dos = null;
		int responseCode = 0;
		try {
			URL url = new URL(LSMSDumURL);
			String name = OSDSpecBean;
			// Open a HTTP connection to the URL

			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("objectType", name);
			conn.setRequestProperty("operator", "insert");
			conn.setRequestProperty("FunctionalGraphURL", lsmFunctionalGraphURL);
			conn.setRequestProperty("project", "openiot");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			dos = new ObjectOutputStream(conn.getOutputStream());
			dos.writeObject(specBean);
			dos.flush();
			dos.close();

			// always check HTTP response code from server
			responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// reads server's response
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String response = reader.readLine();
				System.out.println("Server's response: " + response);
			} else {
				System.out.println("Server returned non-OK code: " + responseCode);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("cannot send data to server");
		}
	}
	
	public static void main(String[] args) {
		LSMSDUMHttpManager sdum = new LSMSDUMHttpManager();
		
	}
}
