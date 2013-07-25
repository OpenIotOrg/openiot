package org.openiot.gsn.http.rest;

import org.openiot.gsn.beans.DataField;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class RemoteWrapperHttpClient {


	public static void main(String[] args) throws ClientProtocolException, IOException, ClassNotFoundException {
		
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		final String url = "select * from localsystemtime where timed > 10/10";

		HttpGet httpget = new HttpGet("http://localhost:22001/streaming/"+URLEncoder.encode(url, "UTF-8")+"/12345"); 
		HttpResponse response = httpclient.execute(httpget);

		ObjectInputStream out = (StreamElement4Rest.getXstream()).createObjectInputStream( (response.getEntity().getContent()));
		DataField[] structure = (DataField[]) out.readObject();
		StreamElement4Rest se = null;
		try {
			while((se = (StreamElement4Rest)out.readObject())!=null) {
				System.out.println(se.toStreamElement());
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

}

