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


import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 24, 2010
 * Time: 3:17:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyUpdateUserWaitingForGroupServlet  extends HttpServlet
{
    /****************************************** Servlet Methods*******************************************/
    /****************************************************************************************************/
    public void doPost(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
	{
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        // Get the session
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        ConnectToDB ctdb = null;
        if (user == null)
       {
           this.redirectToLogin(req,res);
       }
        else
       {
           this.checkSessionScheme(req,res);
           ParameterSet pm = new ParameterSet(req);
           if(pm.valueForName("groupname")==null)
           {
               res.sendRedirect("/");
               return;
           }
           else if(pm.valueForName("groupname").equals(""))
           {
               res.sendRedirect("/");
               return;
           }
           else
           {
               try
               {
                   ctdb = new ConnectToDB();
                   if(ctdb.valueExistsForThisColumnUnderTwoConditions(new Column("ISUSERWAITING","yes"),new Column("GROUPNAME",pm.valueForName("groupname")),new Column("USERNAME",user.getUserName()),"ACUSER_ACGROUP")==false)
                   {
                       Group group=new Group(pm.valueForName("groupname"));
                       if(pm.valueForName("addgroup")!=null)
                       {
                           if(pm.valueForName("addgroup").equals("Yes"))
                           {
                               group.setGroupType("5");
                               user.setIsWaiting("yes");
                               ctdb.registerGroupForUser(user,group);

                           }
                         }
                         if(pm.valueForName("deletegroup")!=null)
                         {
                             if(pm.valueForName("deletegroup").equals("Yes"))
                             {
                                 group.setGroupType("0");
                                 user.setIsWaiting("yes");
                                 ctdb.updateGroupForUser(user,group);
                             }
                         }
                     }
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
            res.sendRedirect("https://"+req.getServerName()+":"+ Main.getContainerConfig().getSSLPort()+"/gsn/MyUpdateUserWaitingForGroupServlet");

        }
    }
    private void redirectToLogin(HttpServletRequest req, HttpServletResponse res)throws IOException
    {
        req.getSession().setAttribute("login.target", HttpUtils.getRequestURL(req).toString());
        res.sendRedirect("/gsn/MyLoginHandlerServlet");
    }

}
