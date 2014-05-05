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
*/

package org.openiot.gsn.http.ac;

import org.openiot.gsn.Main;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: May 15, 2010
 * Time: 5:57:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyAccessRightsManagementServlet extends HttpServlet
{

     private static transient Logger logger= Logger.getLogger( MyAccessRightsManagementServlet.class );

    /****************************************** Servlet Methods*******************************************/
    /****************************************************************************************************/

    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        // Get the session
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        this.checkSessionScheme(req,res);
        this.printHeader(out);
        this.printLayoutMastHead(out,user);
        this.printLayoutContent(out);
        this.printUserAccountLinks(out);
        this.printLayoutFooter(out);
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
		out.println("<TITLE>Access Rights Management</TITLE>");
        out.println(" <link rel=\"stylesheet\" media=\"screen\" type=\"text/css\" href=\"/style/acstyle.css\"/>");
        out.println("</HEAD>");
        out.println("<body>");
        out.println("<div id=\"container\">");
        out.println("<div class=box>");

	}
   private void printLayoutMastHead(PrintWriter out, User user)
    {
        out.println("<div id=\"masthead\">");

        out.println("<div class=\"image_float\"><img src=\"/style/gsn-mark.png\" alt=\"GSN logo\" /></div><br>");

        out.println("<h1>Access Rights Management</h1>");
        out.println("<div class=\"spacer\"></div>");

        out.println("</div>");
        out.println("<div id=\"mastheadborder\">");
        this.printLinks(out,user);
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

    private void printLinks(PrintWriter out, User user)
    {
        //out.println("<a class=linkclass href=\"/gsn/MyLoginHandlerServlet\">login</a>");
        out.println("<a class=linkclass href=\"/\">GSN home</a>");
        if(user!=null)
        {
            out.println("<a class=linkclass href=\"/gsn/MyLogoutHandlerServlet\">logout</a>");
        }

    }
    private void printUserName(PrintWriter out, User user)
    {
        //String username=user.getUserName();
        if(user !=null)
        {
            out.println("<p id=\"login\">logged in as : "+user.getUserName()+"</p>");
        }
    }
    public void printUserAccountLinks(PrintWriter out)
    {
        out.println("<p>Welcome to your access rights management ! you have the following options:</p>");
        out.println("<ul class=linklistul >");
        out.println("<LI class=linklistli><a href=/gsn/MyUserCandidateRegistrationServlet>New User?  Sign Up</a></LI>");
        out.println("<LI class=linklistli><a href=\"/gsn/MyUserAccountManagementServlet\">User Account Management</a></LI>");
        out.println("<LI class=linklistli><a href=\"/gsn/MyAdminManagementServlet\">Admin Only</a></LI>");
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
            res.sendRedirect("https://"+req.getServerName()+":"+ Main.getContainerConfig().getSSLPort()+"/gsn/MyAccessRightsManagementServlet");

        }
    }






}
