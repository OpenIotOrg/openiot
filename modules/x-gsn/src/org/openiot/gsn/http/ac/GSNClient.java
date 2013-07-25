package org.openiot.gsn.http.ac;



import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.Header;
import org.apache.log4j.Logger;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.net.URI;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 27, 2010
 * Time: 1:39:48 PM
 * To change this template use File | Settings | File Templates.
 */

/* this class defines a Http Client which can do login, send queries and receive responses */
public class GSNClient
{
    private String host;
    private int gsnhttpport; // GSN serevr htttp port
    private int gsnhttpsport;// GSN server https port
    private DefaultHttpClient httpclient;
    private KeyStore trustStore; // KeyStore for materials used for SSL/TLS protocl
    final static String trustStorePassWord="changeit"; // better to be stored in GSN.xml

    private static transient Logger logger= Logger.getLogger( GSNClient.class );

    public GSNClient( String host, int gsnhttpport, int gsnhttpsport )
    {
        this.host=host;
        this.gsnhttpport=gsnhttpport;
        this.gsnhttpsport=gsnhttpsport;
        httpclient = new DefaultHttpClient();
        FileInputStream instream=null;
        try
        {
            this.trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
            instream = new FileInputStream(new File("conf/clienttestkeystore"));
            this.trustStore.load(instream, "changeit".toCharArray());
            SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
            socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            Scheme sch = new Scheme("https", socketFactory, gsnhttpsport);
            Scheme plainsch = new Scheme("http", PlainSocketFactory.getSocketFactory(),gsnhttpport);
            httpclient.getConnectionManager().getSchemeRegistry().register(sch);
            httpclient.getConnectionManager().getSchemeRegistry().register(plainsch);

        }
        catch(KeyStoreException e)
        {

            logger.error("ERROR IN GSNCLIENT : Exception while creating trustStore :");
			logger.error(e.getMessage(),e);
        }
        catch(FileNotFoundException e)
        {
            logger.error("ERROR IN GSNCLIENT : FileInputStream exception :");
			logger.error(e.getMessage(),e);
        }
        catch(Exception e)
        {
            logger.error("ERROR IN GSNCLIENT : Exception while loading truststore :");
			logger.error(e.getMessage(),e);
        }
        finally
        {   try
            {
                if(instream !=null)
                {
                    instream.close();
                }
            }
            catch(Exception e)
            {}
        }
    }

    public boolean doLogin(String username, String password)throws IOException, KeyStoreException
    {
        ArrayList formparams = new ArrayList();
        formparams.add(new BasicNameValuePair("username", username));
        formparams.add(new BasicNameValuePair("password", password));
        UrlEncodedFormEntity entityform = new UrlEncodedFormEntity(formparams, "UTF-8");
        boolean loginOK=false;
        try
        {
            URI uri = URIUtils.createURI("https", this.host, this.gsnhttpsport, "/gsn/MyLoginHandlerServlet",null, null);
            HttpPost httppost = new HttpPost(uri);
            httppost.addHeader("client","apache");


            logger.info("executing request" + httppost.getRequestLine());
            httppost.setEntity(entityform);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            Header header = response.getFirstHeader("logedin");
            if(header!=null)
            {
                if(header.getValue().equals("yes"))
                {
                    loginOK = true;
                }
                else if(header.getValue().equals("no"))
                {
                    loginOK = false;
                }
            }

           logger.info("---------------------------------------- "+response.getStatusLine() );

            if (entity != null)
            {
                logger.info("Response content length: " + entity.getContentLength());
                logger.info(EntityUtils.toString(entity));



            }
            if (entity != null)
            {
                entity.consumeContent();
            }

        }
        catch(Exception e)
        {
             logger.error("ERROR IN DOLOGIN:  Exception while creating uri");
            logger.error(e.getMessage(),e);
        }
        return loginOK;
    }

    public String sendQuery(String scheme,String path, String query, String fragment)
    {
        String message="";
        try
        {
            int queryport=0;
            if(scheme.equals("http"))
            {
                queryport=this.gsnhttpport;
            }
            else if(scheme.equals("https"))
            {
                 queryport=this.gsnhttpsport;
            }
            URI uri = URIUtils.createURI(scheme, this.host, queryport, path,query, fragment);
            //HttpGet httpget = new HttpGet("http://localhost:22001/gsn?REQUEST=116&name=remotepushtest");
            HttpGet httpget = new HttpGet(uri);

            logger.info("executing request" + httpget.getRequestLine());

            HttpResponse response = httpclient.execute(httpget);
            HttpEntity firstentity = response.getEntity();

            logger.info("----------------------------------------");
            logger.info(response.getStatusLine());
            if (firstentity != null)
            {
                logger.info("Response content length: " + firstentity.getContentLength());
                message=EntityUtils.toString(firstentity);
            }
            if (firstentity != null)
            {
                firstentity.consumeContent();
            }

        }
        catch(Exception e)
        {
            message="Exception in sendQuery : "+ e.getMessage() +" The cause : "+e.getCause();

        }
        return message;
    }

    public void shutdownConnection()
    {
        this.httpclient.getConnectionManager().shutdown();
    }
}
