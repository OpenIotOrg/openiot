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

package org.openiot.gsn.wrappers;

import org.openiot.gsn.beans.AddressBean;
import org.openiot.gsn.beans.DataField;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/*
 * This wrapper allows GSN to extract Rss Feed from a given URL of Rss Feed.
 * Gets one parameter called url
 * Output has three fields (title[varchar[100]], author[varchar[100]],description[varchar[255]],link[varchar[255]]). 
 */
public class RssWrapper extends AbstractWrapper {
  
  
  private int                      SAMPLING_RATE_IN_MSEC       = 60000; //every 60 seconds.
  
  private static int               threadCounter      = 0;
  
  private final transient Logger   logger             = Logger.getLogger( RssWrapper.class );
  
  private String                   urlPath               ;
  
  private AddressBean              addressBean           ;
  
  private int                      rate                  ;
  
  private URL                      url                   ;
  
  private SyndFeedInput            rss_input             ;
 
  private SyndFeed                 feed                  ;
  
  private transient final DataField [] outputStructure = new  DataField [] { new DataField( "title" , "varchar(100)" , "Title of this Feed Entry" ),new DataField("author","varchar(100)","Author of This Feed Entry."),new DataField("description","varchar(255)","Description Field of This Feed Entry."),new DataField("link","varchar(255)","Link of This Feed Entry.")};
  
  public boolean initialize (  ) {
    this.addressBean =getActiveAddressBean( );
    urlPath = this.addressBean.getPredicateValueWithDefault("url", null);
    if (urlPath == null) {
      logger.error("Loading the rss wrapper failed due to missing *url* parameter.");
      return false;
    }
    rate = this.addressBean.getPredicateValueAsInt( "rate" ,SAMPLING_RATE_IN_MSEC);
    setName( "RssReceiver-Thread" + ( ++threadCounter ) );
    if ( logger.isDebugEnabled( ) ) logger.debug( "RssWrapper is now running @" + rate + " Rate." );
    return true;
  }
  
  public void run ( ) {
    while ( isActive( ) ) {
      try {
        Thread.sleep( rate );
        rss_input = new SyndFeedInput();
        feed = rss_input.build(new XmlReader(url));
        for (SyndEntry entry: (List<SyndEntry>) feed.getEntries()) {
          String title = entry.getTitle();
          String link = entry.getLink();
          String description= entry.getDescription().getValue();
          String author = entry.getAuthor();
          long publish_date = entry.getPublishedDate().getTime();
          postStreamElement(publish_date,new Serializable[] {title,author,description,link});   
        }
      }catch (com.sun.syndication.io.FeedException e){
        logger.error( e.getMessage( ) , e );
      }catch ( InterruptedException e ) {
        logger.error( e.getMessage( ) , e );
      }catch (IOException e) {
        logger.error( e.getMessage( ) , e );
      }
    }
  }
  
  public String getWrapperName() {
    return "Rss Wrapper";
  }
  
  public void dispose (  ) {
    threadCounter--;
  }
  
  public  DataField[] getOutputFormat ( ) {
    return outputStructure;
  }
}
