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

package org.openiot.gsn.http.ac;

import org.openiot.gsn.Main;


import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 24, 2010
 * Time: 4:41:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyUpdateUserWaitingForDataSourceServlet extends HttpServlet
{
    private static transient Logger logger                             = Logger.getLogger( MyUpdateUserWaitingForDataSourceServlet.class );
    /****************************************** Servlet Methods*******************************************/
    /****************************************************************************************************/
    public void doPost(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        // Get the session
        HttpSession session = req.getSession();
        ConnectToDB ctdb = null;
        DataSource newDataSource=null;
        DataSource oldDataSource=null;
        //String newType=null;
        User user = (User) session.getAttribute("user");
        if (user == null)
       {
          this.redirectToLogin(req,res);
       }
       else
       {
           this.checkSessionScheme(req,res);
           ParameterSet pm = new ParameterSet(req);

           try
           {
               ctdb=new ConnectToDB();
               if(ctdb.getDataSourceListForParameterSet(pm)==null)
               {
                   res.sendRedirect("/");
                   return;
               }
               if(ctdb.getDataSourceListForParameterSet(pm).size()==0)
               {
                   res.sendRedirect("/gsn/MyUserUpdateServlet");
                   return;
               }
               newDataSource=(DataSource)ctdb.getDataSourceListForParameterSet(pm).get(0);
               if(newDataSource==null)
               {
                   res.sendRedirect("/");
                   return;
               }

               oldDataSource=ctdb.getDataSourceForUser(user,newDataSource.getDataSourceName());


               if(ctdb.valueExistsForThisColumnUnderTwoConditions(new Column("ISUSERWAITING","yes"),new Column("USERNAME",user.getUserName()),new Column("DATASOURCENAME",newDataSource.getDataSourceName()), "ACUSER_ACDATASOURCE")==false)
               {

                   if(oldDataSource==null)
                   {
                       
                       user.setIsWaiting("yes");
                       newDataSource.setDataSourceType("5"+ newDataSource.getDataSourceType());
                       newDataSource.setOwnerDecision("notreceived");
                       ctdb.registerDataSourceForUser(user,newDataSource);
                   }
                   else
                   {
                       if(oldDataSource.getDataSourceType().equals(newDataSource.getDataSourceType())==false)
                       {
                           user.setIsWaiting("yes");
                           oldDataSource.setDataSourceType(oldDataSource.getDataSourceType().charAt(0)+ newDataSource.getDataSourceType());
                           oldDataSource.setOwnerDecision("notreceived");
                           ctdb.updateDataSourceForUser(user,oldDataSource);
                           
                       }
                   }
               }
               DataSource dataSource = newDataSource;              // if the action is related with a new data source
               if (oldDataSource != null) {
                   dataSource = oldDataSource; // otherwise, it is about the old data source
               }
                 logger.warn(dataSource.getDataSourceType());
               ///////////////// Send notification to Admin
               String access = "";
               if (dataSource.getDataSourceType().charAt(1) == '1') {          // define what type of access does the user want
                   access = "read";
               } else if (dataSource.getDataSourceType().charAt(1) == '2') {
                   access = "write";
               } else if (dataSource.getDataSourceType().charAt(1) == '3') {
                   access = "read/write";
               }

               User userFromBD = ctdb.getUserForUserName("Admin"); // get the details for the Admin account
               User owner = ctdb.getUserFromDataSource(dataSource.getDataSourceName());    // get the details of the Owner
               Emailer email = new Emailer();

               // send an email to the Owner of the Resource
               String msgHead = "Dear "+owner.getFirstName() +", "+"\n"+"\n";
               String msgTail = "Best Regards,"+"\n"+"GSN Team";
               String msgBody = "A new request has been made to access a Virtual Sensor that belongs to you."+"\n\n"+
                       "The details of the Virtual Sensor are the following:\n\n"+
                       "Virtual Sensor name: " + dataSource.getDataSourceName() +
                       "\nVirtual Sensor requested access type: " + access +
                       "\n\nThe User making the request has the following details:\n\n"+
                       "First name: " + user.getFirstName() + "\n"+
                       "Last name: " + user.getLastName() + "\n"+
                       "GSN username: " + user.getUserName() + "\n"+
                       "Email address: " + user.getEmail() + "\n\n"+
                       "You can manage this request by choosing the following options in GSN:\n"+
                       "Access Rights Management -> User Account Management -> Owner Waiting List\n"+
                       "or via the URL: "+req.getServerName()+":"+req.getServerPort()+"/gsn/MyOwnerWaitingListServlet\n\n";

               email.sendEmail( "GSN ACCESS ", "GSN USER",userFromBD.getEmail(),"Request for access to a Virtual Sensor", msgHead, msgBody, msgTail);

               // send an email to the administrator
               msgHead = "Dear "+userFromBD.getFirstName() +", "+"\n"+"\n";
               msgTail = "Best Regards,"+"\n"+"GSN Team";
               msgBody = "A new request has been made to access a Virtual Sensor."+"\n\n"
                       +"The User making the request has the following details:\n\n"+
                       "First name: " + user.getFirstName() + "\n"+
                       "Last name: " + user.getLastName() + "\n"+
                       "GSN username: " + user.getUserName() + "\n"+
                       "Email address: " + user.getEmail() + "\n\n"+
                       "The details of the Virtual Sensor are the following:\n\n"+
                       "Virtual Sensor name: " + dataSource.getDataSourceName() +
                       "\nVirtual Sensor requested access type: " + access +
                       "\n\nThe Owner of the Virtual Sensor is the following:\n"+
                       "First name: " + owner.getFirstName() + "\n"+
                       "Last name: " + owner.getLastName() + "\n"+
                       "GSN username: " + owner.getUserName() + "\n"+
                       "Email address: " + owner.getEmail() + "\n\n"+
                       "You can manage this request by choosing the following options in GSN:\n"+
                       "Access Rights Management -> Admin Only -> Users Updates Waiting List\n"+
                       "or via the URL: "+req.getServerName()+":"+req.getServerPort()+"/gsn/MyUserUpdateWaitingListServlet\n\n";
               email.sendEmail( "GSN ACCESS ", "GSN USER",userFromBD.getEmail(),"Request for access to a Virtual Sensor", msgHead, msgBody, msgTail);

               res.sendRedirect("/gsn/MyUserUpdateServlet");
           }
           catch(Exception e)
           {
               out.println("Exception caught : "+e.getMessage());
           }
           finally
           {
               if(ctdb!=null)
               {
                   ctdb.closeStatement();
                   ctdb.closeConnection();
               }
           }
       }
    }
    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
           this.doPost(req,res);
    }

    /****************************************** Client Session related Methods*******************************************/
    /********************************************************************************************************************/


    private void checkSessionScheme(HttpServletRequest req, HttpServletResponse res)throws IOException
    {

         if(req.getScheme().equals("https")== true)
        {
            if((req.getSession().getAttribute("scheme")==null))
            {
                req.getSession().setAttribute("scheme","https");
            }
        }
         else if(req.getScheme().equals("http")== true )
        {
             if((req.getSession().getAttribute("scheme")==null))
            {
                req.getSession().setAttribute("scheme","http");
            }
            res.sendRedirect("https://"+req.getServerName()+":"+ Main.getContainerConfig().getSSLPort()+"/gsn/MyUpdateUserWaitingForDataSourceServlet");

        }
    }
    private void redirectToLogin(HttpServletRequest req, HttpServletResponse res)throws IOException
    {
        req.getSession().setAttribute("login.target", HttpUtils.getRequestURL(req).toString());
        res.sendRedirect("/gsn/MyLoginHandlerServlet");
    }

}
