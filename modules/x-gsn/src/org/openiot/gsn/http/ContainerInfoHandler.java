package org.openiot.gsn.http;

import org.openiot.gsn.Main;
import org.openiot.gsn.Mappings;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.beans.VSensorConfig;
import org.openiot.gsn.beans.WebInput;
//import org.openiot.gsn.http.accesscontrol.User;
import org.openiot.gsn.http.ac.DataSource;
import org.openiot.gsn.http.ac.User;
import org.openiot.gsn.storage.DataEnumerator;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.KeyValue;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

public class ContainerInfoHandler implements RequestHandler {
  
  private static transient Logger logger             = Logger.getLogger( ContainerInfoHandler.class );
  
  public void handle ( HttpServletRequest request , HttpServletResponse response ) throws IOException {
	  response.setStatus( HttpServletResponse.SC_OK );
	  String reqName = request.getParameter("name");



        //Added by Behnaz
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        response.setHeader("Cache-Control","no-store");
        response.setDateHeader("Expires", 0);
        response.setHeader("Pragma","no-cache");



        response.getWriter( ).write( buildOutput(reqName,user));
  }
  
  //return only the requested sensor if specified (otherwise use null)
  //Added by Behnaz. New parameter User user to method buildOutput.
  public String buildOutput (String reqName, User user) {
	  SimpleDateFormat sdf = new SimpleDateFormat (Main.getContainerConfig().getTimeFormat());
	  
	  
    StringBuilder sb = new StringBuilder( "<gsn " );
    sb.append( "name=\"" ).append( StringEscapeUtils.escapeXml( Main.getContainerConfig( ).getWebName( ) ) ).append( "\" " );
    sb.append( "author=\"" ).append( StringEscapeUtils.escapeXml( Main.getContainerConfig( ).getWebAuthor( ) ) ).append( "\" " );
    sb.append( "email=\"" ).append( StringEscapeUtils.escapeXml( Main.getContainerConfig( ).getWebEmail( ) ) ).append( "\" " );
    sb.append( "description=\"" ).append( StringEscapeUtils.escapeXml( Main.getContainerConfig( ).getWebDescription( ) ) ).append("\">\n" );


    Iterator < VSensorConfig > vsIterator = Mappings.getAllVSensorConfigs( );

    
    while ( vsIterator.hasNext( ) ) {
      VSensorConfig sensorConfig = vsIterator.next( );
      if(Main.getContainerConfig().isAcEnabled())
      {
          if (user != null)
          {
              if ( (reqName != null && !sensorConfig.getName().equals(reqName) )|| ( user.hasReadAccessRight(sensorConfig.getName())== false && user.isAdmin()==false) ) continue;
          }
          else
              if ( (reqName != null && !sensorConfig.getName().equals(reqName)) || DataSource.isVSManaged(sensorConfig.getName()))
              {
                  continue;
              }
      }
      else
      {
            if ( (reqName != null && !sensorConfig.getName().equals(reqName))) continue;   
      }
      sb.append("<virtual-sensor");
      sb.append(" name=\"").append(sensorConfig.getName()).append("\"" );
      sb.append(" last-modified=\"" ).append(new File(sensorConfig.getFileName()).lastModified()).append("\"");
      if (sensorConfig.getDescription() != null) {
        sb.append(" description=\"").append(StringEscapeUtils.escapeXml(sensorConfig.getDescription())).append("\"");
      }
      sb.append( ">\n" );
      
      ArrayList<StreamElement> ses = getMostRecentValueFor(sensorConfig.getName());
      int counter = 1;
      if (ses!=null ) {
        for (StreamElement se:ses){
          for ( DataField df : sensorConfig.getOutputStructure( ) ) {
            sb.append("\t<field");
            sb.append(" name=\"").append(df.getName().toLowerCase()).append("\"");
            sb.append(" type=\"").append(df.getType()).append("\"");
            if (df.getDescription() != null && df.getDescription().trim().length() != 0)
              sb.append(" description=\"").append(StringEscapeUtils.escapeXml(df.getDescription())).append("\"");
            sb.append(">");
            if (se!= null ) 
              if (df.getType().toLowerCase( ).trim( ).indexOf( "binary" ) > 0 )
                sb.append( se.getData( df.getName( ) ) );
              else
                sb.append( se.getData( StringEscapeUtils.escapeXml( df.getName( ) ) ) );
            sb.append("</field>\n");
          }
          SimpleDateFormat fsdf = sensorConfig.getSDF() != null ? sensorConfig.getSDF() : sdf ; 
          sb.append("\t<field name=\"timed\" type=\"string\" description=\"The timestamp associated with the stream element\">" ).append( se == null ? "" : fsdf.format(new Date(se.getTimeStamp( ))) ).append( "</field>\n" );
          for ( KeyValue df : sensorConfig.getAddressing( )){
            sb.append("\t<field");
            sb.append(" name=\"").append( StringEscapeUtils.escapeXml( df.getKey( ).toString( ).toLowerCase()) ).append( "\"");
            sb.append(" category=\"predicate\">");
            sb.append(StringEscapeUtils.escapeXml( df.getValue( ).toString( ) ) );
            sb.append("</field>\n" );
          }
          if (sensorConfig.getWebinput( )!=null){
            for ( WebInput wi : sensorConfig.getWebinput( ) ) {
              for ( DataField df : wi.getParameters ( ) ) {
                sb.append( "\t<field");
                sb.append(" command=\"").append( wi.getName( ) ).append( "\"" );
                sb.append(" name=\"" ).append( df.getName( ).toLowerCase()).append( "\"" );
                sb.append(" category=\"input\"");
                sb.append(" type=\"").append( df.getType( ) ).append( "\"" );
                if ( df.getDescription( ) != null && df.getDescription( ).trim( ).length( ) != 0 )
                  sb.append( " description=\"" ).append( StringEscapeUtils.escapeXml( df.getDescription( ) ) ).append( "\"" );
                sb.append( "></field>\n" );
              }
            }
          }
          counter++;
        }
      }
      sb.append( "</virtual-sensor>\n" );
    }
    sb.append( "</gsn>\n" );
    return sb.toString();
  }
  
  public boolean isValid ( HttpServletRequest request , HttpServletResponse response ) throws IOException {
    return true;
  }
  /**
   * returns null if there is an error.
   * 
   * @param virtual_sensor_name
   * @return
   */
  public static ArrayList<StreamElement> getMostRecentValueFor(String virtual_sensor_name) {
    StringBuilder query=  new StringBuilder("select * from " ).append(virtual_sensor_name).append( " where timed = (select max(timed) from " ).append(virtual_sensor_name).append(")");
    ArrayList<StreamElement> toReturn=new ArrayList<StreamElement>() ;
    try {
      DataEnumerator result = Main.getStorage(virtual_sensor_name).executeQuery( query , true );
      while ( result.hasMoreElements( ) ) 
        toReturn.add(result.nextElement());
    } catch (SQLException e) {
      logger.error("ERROR IN EXECUTING, query: "+query);
      logger.error(e.getMessage(),e);
      return null;
    }
    return toReturn;
  }
}
