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
 * @author Behnaz Bostanipour
 * @author Timotee Maret
 * @author Julien Eberle
*/

package org.openiot.gsn.http.ac;

import org.openiot.gsn.Main;
import org.openiot.gsn.http.WebConstants;
import org.apache.log4j.Logger;


import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 26, 2010
 * Time: 7:37:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyUpdateUserDataSourceServlet  extends HttpServlet
{
    private static transient Logger logger                             = Logger.getLogger( MyUpdateUserDataSourceServlet.class );
    /****************************************** Servlet Methods*******************************************/
    /******************************************************************************************************/
    public void doPost(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        // Get the session
        HttpSession session = req.getSession();
        ConnectToDB ctdb = null;
        User user = (User) session.getAttribute("user");
        if (user == null)
       {
           this.redirectToLogin(req,res);
       }
       else
       {
           this.checkSessionScheme(req,res);
           if(!user.getUserName().equals("Admin"))
           {
               res.sendError( WebConstants.ACCESS_DENIED , "Access denied." );
           }
           else
           {
               ParameterSet pm = new ParameterSet(req);
               if(pm.valueForName("datasourcename")==null|| pm.valueForName("datasourcetype")==null|| pm.valueForName("update")==null || pm.valueForName("username")==null )
               {
                   res.sendRedirect("/");
                   return;
               }
               if(pm.valueForName("datasourcename").equals("")|| pm.valueForName("datasourcetype").equals("")|| pm.valueForName("update").equals("")|| pm.valueForName("username").equals("") )
               {
                   res.sendRedirect("/");
                   return;
               }
               try
               {
                   ctdb = new ConnectToDB();

                   User waitingUser = ctdb.getUserForUserName(pm.valueForName("username"));
                   //String updatedType=null;
                   String userMessage = null;
                   String label = null;

                   if(pm.valueForName("datasourcetype").charAt(1)=='1')
                   {
                       label="read";
                   }
                   else if(pm.valueForName("datasourcetype").charAt(1)=='2')
                   {
                       label="write";
                   }
                   else if(pm.valueForName("datasourcetype").charAt(1)=='3')
                   {
                       label="read/write";
                   }

                   if(pm.valueForName("update").equals("yes"))
                   {
                       if(pm.valueForName("datasourcetype").charAt(1)=='0')
                       {
                          ctdb.deleteDataSourceForUser(new DataSource(pm.valueForName("datasourcename")), waitingUser);
                          userMessage="Your access to the Virtual Sensor '"+ pm.valueForName("datasourcename") +"' has been removed.";
                       }
                       else
                       {
                           //updatedType=pm.valueForName("datasourcetype").substring(1,2);
                           waitingUser.setIsWaiting("no");
                           ctdb.updateDataSourceForUser(waitingUser,new DataSource(pm.valueForName("datasourcename"),pm.valueForName("datasourcetype").substring(1,2)));
                           ctdb.updateOwnerDecision("notreceived",pm.valueForName("username"), pm.valueForName("datasourcename") );
                           userMessage = "Congratulations, you have '"+ label +"' access to the Virtual Sensor: "+ pm.valueForName("datasourcename");
                       }
                   }
                   else if(pm.valueForName("update").equals("no"))
                   {
                       if(pm.valueForName("datasourcetype").charAt(0)=='5')
                       {
                           userMessage="Unfortunately, your request to have '"+ label +"' access rights to the Virtual Sensor '"+ pm.valueForName("datasourcename") +"' has been rejected.";
                           Column column1 = new Column("USERNAME", waitingUser.getUserName());
                           Column column2 = new Column("DATASOURCENAME", pm.valueForName("datasourcename"));
                           ctdb.deleteUnderTwoConditions(column1, column2,"ACACCESS_DURATION"); // remove this from the Duration Table
                           ctdb.deleteDataSourceForUser(new DataSource(pm.valueForName("datasourcename")), waitingUser);
                       }
                       else
                       {
                           userMessage="Unfortunately, your request for changing access rights to the Virtual Sensor '"+ pm.valueForName("datasourcename") +"' has been rejected.";
                           Column column1 = new Column("USERNAME", waitingUser.getUserName());
                           Column column2 = new Column("DATASOURCENAME", pm.valueForName("datasourcename"));
                           ctdb.deleteUnderTwoConditions(column1, column2,"ACACCESS_DURATION"); // remove this from the Duration Table
                           waitingUser.setIsWaiting("no");
                           ctdb.updateDataSourceForUser(waitingUser,new DataSource(pm.valueForName("datasourcename"),pm.valueForName("datasourcetype").substring(0,1)));
                           ctdb.updateOwnerDecision("notreceived",pm.valueForName("username"), pm.valueForName("datasourcename") );
                       }
                   }

                   Emailer email = new Emailer();
                   String msgHead = "Dear "+waitingUser.getFirstName()+" "+waitingUser.getLastName()+", "+"\n"+"\n";
                   String msgTail = "Best Regards,"+"\n"+"GSN Team";
                   String msgBody = userMessage+"\n"
                           +"You can view your available sensors by going to:\n\n"+
                           "User Account Management -> Update Access Rights Form\n"+
                           "or via the URL: "+req.getServerName()+":"+req.getServerPort()+"/gsn/MyUserUpdateServlet\n\n";

                   // first change Emailer class params to use sendEmail
                   email.sendEmail( "GSN ACCESS ", "GSN USER",waitingUser.getEmail(),"Access to a Virtual Sensor", msgHead, msgBody, msgTail);


                    res.sendRedirect("/gsn/MyUserUpdateWaitingListServlet");
                }
                catch(Exception e)
                {
                    logger.error("ERROR IN doPost");
			        logger.error(e.getMessage(),e);

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
            res.sendRedirect("https://"+req.getServerName()+":"+ Main.getContainerConfig().getSSLPort()+"/gsn/MyUpdateUserDataSourceServlet");

        }
    }
    private void redirectToLogin(HttpServletRequest req, HttpServletResponse res)throws IOException
    {
        req.getSession().setAttribute("login.target", HttpUtils.getRequestURL(req).toString());
        res.sendRedirect("/gsn/MyLoginHandlerServlet");
    }


}
