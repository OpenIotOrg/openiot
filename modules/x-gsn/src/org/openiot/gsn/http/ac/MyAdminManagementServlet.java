package org.openiot.gsn.http.ac;

import org.openiot.gsn.Main;
import org.openiot.gsn.http.WebConstants;


import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 27, 2010
 * Time: 2:24:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyAdminManagementServlet extends HttpServlet
{
    /****************************************** Servlet Methods*******************************************/
    /******************************************************************************************************/

    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        // Get the session
        HttpSession session = req.getSession();

        User user = (User) session.getAttribute("user");
        if (user == null)
       {
           this.redirectToLogin(req,res);
       }
       else
       {
           if(user.isAdmin()== false)
           {
               res.sendError( WebConstants.ACCESS_DENIED , "Access denied." );
           }
           else
           {
               this.checkSessionScheme(req,res);
               this.printHeader(out);
               this.printLayoutMastHead(out, user );
               this.printLayoutContent(out);
               this.printAdminManagementLinks(out);
               this.printLayoutFooter(out);
           }

       }
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
       this.doGet(req,res);
    }


    
    /****************************************** HTML Printing Methods*******************************************/
    /***********************************************************************************************************/


    private void printHeader(PrintWriter out)
	{
        out.println("<HTML>");
        out.println("<HEAD>");
		out.println("<TITLE>Admin Only</TITLE>");
        out.println(" <link rel=\"stylesheet\" media=\"screen\" type=\"text/css\" href=\"/style/acstyle.css\"/>");
        //printStyle(out);
        out.println("</HEAD>");
        out.println("<body>");
        out.println("<div id=\"container\">");
        out.println("<div class=box>");

	}
    private void printLayoutMastHead(PrintWriter out, User user)
    {
        out.println("<div id=\"masthead\">");

        out.println("<div class=\"image_float\"><img src=\"/style/gsn-mark.png\" alt=\"GSN logo\" /></div><br>");
        out.println("<h1>Admin Only </h1>");
        out.println("<div class=\"spacer\"></div>");

        out.println("</div>");
        out.println("<div id=\"mastheadborder\">");
        this.printLinks(out);
        this.printUserName(out, user);
        out.println("<br><br>");
        out.println("</div>");
    }
    private void printLayoutContent(PrintWriter out)
    {
        out.println("<div id=\"content\">");
    }
    private void printLayoutFooter(PrintWriter out)
    {
        out.println("</div>");
        out.println("<div id=\"footer\">");
        out.println(" <p align=\"center\"><FONT COLOR=\"#000000\"/>Powered by <a class=\"nonedecolink\" href=\"http://globalsn.sourceforge.net/\">GSN</a>,  Distributed Information Systems Lab, EPFL 2010</p>");
        out.println("</div>");//footer
        out.println("</div>");//box
        out.println("</div>");//container
        out.println("</body>");
        out.println("</html>");
    }

    private void printLinks(PrintWriter out)
    {
        //out.println("<a class=linkclass href=\"/gsn/MyLoginHandlerServlet\">login</a>");
        out.println("<a class=linkclass href=\"/\">GSN home</a>");
        out.println("<a class=linkclass href=/gsn/MyAccessRightsManagementServlet>access rights management</a>");
        out.println("<a class=linkclass href=\"/gsn/MyLogoutHandlerServlet\">logout</a>");

    }
    private void printUserName(PrintWriter out, User user)
    {
        //String username=user.getUserName();
        out.println("<p id=\"login\">logged in as : "+user.getUserName()+"</p>");
    }

    public void printAdminManagementLinks(PrintWriter out)
    {
        out.println("<p>Welcome ! you have the following options:</p>");
        out.println("<ul class=linklistul >");
        out.println("<LI class=linklistli><a href=\"/gsn/MyDisplayACTablesContentServlet\">Check access right tables content</a></LI>");
        out.println("<LI class=linklistli><a href=\"/gsn/MyCreateGroupServlet\">Create new groups</a></LI>");
        out.println("<LI class=linklistli><a href=\"/gsn/MyGroupManagementServlet\">Delete or change group structure</a></LI>");
        out.println("<LI class=linklistli><a href=/gsn/MyUserUpdateWaitingListServlet>Check and validate user updates </a></LI>");
        out.println("<LI class=linklistli><a href=\"/gsn/MyUserCandidateWaitingListServlet\">Check and validate new sign up requests </a></LI>");
        out.println("<LI class=linklistli><a href=\"/gsn/MyDataSourceCandidateWaitingListServlet\">Check and validate new virtual sensors registration requests </a></LI>");
        out.println("</ul>");
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
            res.sendRedirect("https://"+req.getServerName()+":"+ Main.getContainerConfig().getSSLPort()+"/gsn/MyAdminManagementServlet");

        }
    }
    private void redirectToLogin(HttpServletRequest req, HttpServletResponse res)throws IOException
    {
        req.getSession().setAttribute("login.target", HttpUtils.getRequestURL(req).toString());
        res.sendRedirect("/gsn/MyLoginHandlerServlet");
    }



}
