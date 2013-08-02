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
 * Date: Apr 20, 2010
 * Time: 10:03:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyDeleteGroupServlet extends HttpServlet
{
    private static transient Logger logger                             = Logger.getLogger( MyDeleteGroupServlet.class );

    /****************************************** Servlet Methods*******************************************/
    /******************************************************************************************************/

    public void doPost(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        ConnectToDB ctdb = null;

        // Get the session
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null)
       {
         this.redirectToLogin(req,res);
       }
       else
       {
           this.checkSessionScheme(req,res);
            if(user.isAdmin()== false)
           {
               res.sendError( WebConstants.ACCESS_DENIED , "Access denied." );
           }
           else
           {
               ParameterSet pm = new ParameterSet(req);
               if(pm.valueForName("groupname")==null)
               {
                   res.sendRedirect("/");
               }
               else
               {
                   if(pm.valueForName("groupname").equals(""))
                   {
                       res.sendRedirect("/");
                   }
                   try
                   {
                       ctdb = new ConnectToDB();
                       ctdb.deleteGroup(pm.valueForName("groupname"));
                       //session.setAttribute("group", null);
                       res.sendRedirect("/gsn/MyGroupManagementServlet");
                   }
                   catch(Exception e)
                   {
                       out.println("Can not delete this group!");
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
            res.sendRedirect("https://"+req.getServerName()+":"+ Main.getContainerConfig().getSSLPort()+"/gsn/MyDeleteGroupServlet");

        }
    }
    private void redirectToLogin(HttpServletRequest req, HttpServletResponse res)throws IOException
    {
        req.getSession().setAttribute("login.target", HttpUtils.getRequestURL(req).toString());
        res.sendRedirect("/gsn/MyLoginHandlerServlet");
    }

}



