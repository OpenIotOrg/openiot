package org.openiot.csiro.netatmoapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.amber.oauth2.client.OAuthClient;
import org.apache.amber.oauth2.client.URLConnectionClient;
import org.apache.amber.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.amber.oauth2.common.exception.OAuthProblemException;
import org.apache.amber.oauth2.common.exception.OAuthSystemException;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class Netatmo {
		
	private OAuthClient client = new OAuthClient(new URLConnectionClient());
	private OAuthAccessTokenResponse response = null;
	private static Netatmo netatmo = null;
	private String accessToken;
	private long tokenExpiry;
	private boolean tokenValid = false;
	private HttpClient httpclient;
	
	private Timer timer;
	
	
	public static Netatmo getInstance(){
		if (netatmo == null){
			netatmo = new Netatmo();
		}
		return netatmo;			
	}
	
	public Boolean authenticate(User user){
		try {
			response = client.accessToken(user.getAuthRequest());
		} catch (OAuthSystemException e) {
			e.printStackTrace();
			return false;
		} catch (OAuthProblemException e) {
			e.printStackTrace();
			return false;
		}
	    
	    this.accessToken = response.getAccessToken();;
	    this.tokenExpiry = response.getExpiresIn();
	    this.tokenValid = true;
	    
	    //start a timer to reduce the token validity every second.
	    //when token is expired, the tokenValid variable will become false
//	    this.timer = new Timer();
//	    timer.scheduleAtFixedRate(new ExpiryTimer(), 0, 1000);
	    
	    return true;
	    
	}
		
	public String getNetatmotUser() throws IOException, URISyntaxException, HttpException{
		///check if accessToken is value
		if (this.isTokenValid()){
			httpclient = new DefaultHttpClient();
			String strurl = Constants.GETUSER + "?" + Constants.ACCESSTOKEN + "=" + URLEncoder.encode(this.accessToken);
			
//			URL url = new URL(strurl);
			
			
			strurl = strurl.replaceAll(" ", "%20");
			HttpGet request = new HttpGet(strurl);
			HttpResponse response = httpclient.execute(request);
			BufferedReader rd = new BufferedReader
					  (new InputStreamReader(response.getEntity().getContent()));
			
			StringBuilder temp_str = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				temp_str.append(line);
			}
			return temp_str.toString();
			
		}
		else
			return "Inavlid Token. Get New Token";		
	}
	
	
	public String getNetatmotUserDeviceList() throws IOException, URISyntaxException, HttpException{
		///check if accessToken is value
		if (this.isTokenValid()){
			httpclient = new DefaultHttpClient();
			String strurl = Constants.DEVICELIST + "?" + Constants.ACCESSTOKEN + "=" + URLEncoder.encode(this.accessToken);
			
//			URL url = new URL(strurl);
			
			
			strurl = strurl.replaceAll(" ", "%20");
			HttpGet request = new HttpGet(strurl);
			HttpResponse response = httpclient.execute(request);
			BufferedReader rd = new BufferedReader
					  (new InputStreamReader(response.getEntity().getContent()));
			
			StringBuilder temp_str = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				temp_str.append(line);
			}
			return temp_str.toString();
			
		}
		else
			return "Inavlid Token. Get New Token";		
	}
		
	
	public String getMeasure(MeasureParameters parameters) throws IOException, URISyntaxException, HttpException{
		///check if accessToken is value
		if (this.isTokenValid()){
			httpclient = new DefaultHttpClient();
			
			//generate the URL based on input parameters
			
			//basic string
			//e.g. /api/getmeasure?access_token=[YOURTOKEN]
			StringBuilder url = new StringBuilder();
			url.append(Constants.MEASURE);
			url.append(Constants._QMARK);
			url.append(Constants.ACCESSTOKEN);
			url.append(Constants._EQUAL);
			url.append(URLEncoder.encode(this.accessToken));
			
			//format the parameters
			if (parameters.getDevice_id()!=""){
				url.append(Constants._AND + "device_id" + Constants._EQUAL);
				url.append(parameters.getDevice_id());
			}
			
			if (parameters.getModule_id()!=""){
				url.append(Constants._AND + "module_id" + Constants._EQUAL);
				url.append(parameters.getModule_id());
			}	
			
			ArrayList<String> tmp = parameters.getType();
			if (tmp.size() > 0){
				url.append(Constants._AND + "type" + Constants._EQUAL);				
				for (int i=0; i<tmp.size(); i++){
					String str = tmp.get(i);					
					url.append(str);
					if (i!= tmp.size()-1)
						url.append(Constants._COMMA);
					
				}
			}
			
			if (parameters.getScale()!=""){
				url.append(Constants._AND + "scale" + Constants._EQUAL);
				url.append(parameters.getScale());
			}	
			
			if (parameters.getDate_begin()!=""){
				url.append(Constants._AND + "date_begin" + Constants._EQUAL);
				url.append(getTimestamp(parameters.getDate_begin()));
			}	
			
			if (parameters.getDate_end()!=""){
				url.append(Constants._AND + "date_end" + Constants._EQUAL);
				url.append(getTimestamp(parameters.getDate_end()));
			}	

			
			if (parameters.getLimit() > 0){
				url.append(Constants._AND + "limit" + Constants._EQUAL);
				url.append(parameters.getLimit());
			}	
			
			url.append(Constants._AND + "date_end" + Constants._EQUAL + "last");
			
//			URL url = new URL(strurl);
			
			//System.out.println("The URL is ->" + url.toString());
						
			HttpGet request = new HttpGet(url.toString());
			HttpResponse response = httpclient.execute(request);
			BufferedReader rd = new BufferedReader
					  (new InputStreamReader(response.getEntity().getContent()));
			
			StringBuilder temp_str = new StringBuilder();
			String line = "";
			while ((line = rd.readLine()) != null) {
				temp_str.append(line);
			}
			return temp_str.toString();
			
		}
		else
			return "Inavlid Token. Get New Token";		
	}	
	
	 public String getAccessToken() {
		return accessToken;
	}

	public long getTokenExpiry() {
		return tokenExpiry;
	}
	
	public void setTokenExpiry(long tokenExpiry) {
		this.tokenExpiry = tokenExpiry;
	}

	public boolean isTokenValid() {
		return tokenValid;
	}

	public void setTokenValid(boolean tokenValid) {
		this.tokenValid = tokenValid;
	}

	private long getTimestamp(String dateString){		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy hh:mm");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));  
		Date dt = null;
		try {
			dt = dateFormat.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(dt!=null)
			return dt.getTime();
		else
			return 0;
		
	}


	class ExpiryTimer extends TimerTask {
	        public void run() {
	            setTokenExpiry(getTokenExpiry() - 1);
	            if (getTokenExpiry() == 0){
	            	setTokenValid(false);	            	
	            	timer.cancel();	            
	            }
//	            System.out.println("reduced 1");
	            	
	        }
	 }
}
