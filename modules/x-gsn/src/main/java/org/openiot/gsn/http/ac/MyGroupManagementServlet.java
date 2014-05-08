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

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 20, 2010
 * Time: 8:01:31 PM
 * To change this template use File | Settings | File Templates.
 */
import org.openiot.gsn.Main;
import org.openiot.gsn.http.WebConstants;
import org.apache.log4j.Logger;


import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

public class MyGroupManagementServlet extends HttpServlet
{

    private static transient Logger logger                             = Logger.getLogger( MyGroupManagementServlet.class );
    /****************************************** Servlet Methods*******************************************/
    /******************************************************************************************************/
    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
        Vector groupList=null;
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
               this.setSessionPrintWriter(req,out);
               
            try
            {
                ctdb = new ConnectToDB();
                this.printHeader(out);
                this.printLayoutMastHead(out,user);
                this.printLayoutContent(out);
                groupList=ctdb.getGroupList();
                for(int i=0;i<groupList.size();i++)
                {
                    
                    printGroupInformation(out,(Group)(groupList.get(i)));

                }
                out.println("<div class=\"spacer\"></div>");
                if(groupList.size()==0)
                {
                    out.println("<p><B> There is no entry in the Group List ! </B></p>");
                }
            }
            catch(Exception e)
            {

                logger.error("ERROR IN doGet");
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
        this.printLayoutFooter(out);
    }


    /****************************************** HTML Printing Methods*******************************************/
    /***********************************************************************************************************/

    private void printHeader(PrintWriter out)
	{
        out.println("<HTML>");
        out.println("<HEAD>");
        //For Java Script!!
        //this.printEmbeddedJS(out);
        out.println("<script type=\"text/javascript\" src=\"/js/acjavascript.js\"></script>");
		out.println("<TITLE>Group Management</TITLE>");
        out.println(" <link rel=\"stylesheet\" media=\"screen\" type=\"text/css\" href=\"/style/acstyle.css\"/>");
        //printStyle(out);
        out.println("</HEAD>");
        //out.println("<body>");
        out.println("<body onload=\"loadScroll()\" onunload=\"saveScroll()\" >");
        out.println("<div id=\"container\">");
        out.println("<div class=box>");

	}
    private void printLayoutMastHead(PrintWriter out, User user)
    {
        out.println("<div id=\"masthead\">");

        out.println("<div class=\"image_float\"><img src=\"/style/gsn-mark.png\" alt=\"GSN logo\" /></div><br>");
        out.println("<h1>Group Management</h1>");
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
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
    private void printLinks(PrintWriter out)
    {

        out.println("<a class=linkclass href=\"/gsn/MyAdminManagementServlet\">admin only</a>");
        out.println("<a class=linkclass href=\"/gsn/MyLogoutHandlerServlet\">logout</a>");
    }
    private void printUserName(PrintWriter out, User user)
    {
        //String username=user.getUserName();
        out.println("<p id=\"login\">logged in as : "+user.getUserName()+"</p>");
    }


    private void printGroupInformation(PrintWriter out,Group group) throws ServletException
	{
        out.println("<h2>New Group Entry </h2>");
        out.println("<table>");
        out.println("<tr><th> group name </th>");
        out.println("<th> group structure</th>");
        out.println("<th> admin decision</th>");
        out.println("<th> admin decision</th></tr>");
        out.println("<tr>");
        this.printInputs(out,group);
        this.printGroupStructureLink(out, group.getGroupName());
        this.printForms(out,group.getGroupName());
        out.println("</tr>");
        out.println("</table>");
        out.println("<br>");
    }
    private void printGroupStructureLink(PrintWriter out, String groupname)
    {
        String groupurl="/gsn/MyGroupHtmlResultSetServlet?groupname="+groupname;
        out.println("<ul class=displaylinkul >");
        out.println("<td style=text-align:center><LI class=displaylinkli><a href="+groupurl+" onClick=\"poptastic(this.href); return false;\">&nbsp&nbsp&nbsp view &nbsp&nbsp&nbsp</a></LI>");
        out.println("</td>");
        out.println("</ul>");
       

    }

    private void printInputs(PrintWriter out,Group group)
    {
        out.println("<td>"+group.getGroupName()+"</td>");
    }
    private void printForms(PrintWriter out,String groupname)
    {
        this.printDeleteForm(out,groupname);
        this.printChangeForm(out,groupname);
    }
    private void printDeleteForm(PrintWriter out,String groupname)
    {
        out.println("<FORM ACTION=/gsn/MyDeleteGroupServlet METHOD=POST>");
        out.println("<INPUT  TYPE=HIDDEN NAME=groupname VALUE="+groupname+">");
        out.println("<td><INPUT TYPE=SUBMIT  class=creategroupbuttonstyle VALUE=\"Delete Group\"></td>");
        out.println("</FORM>");
    }
    private void printChangeForm(PrintWriter out,String groupname)
    {
        out.println("<FORM ACTION=/gsn/MyChangeGroupCombinationServlet METHOD=GET>");
        out.println("<INPUT  TYPE=HIDDEN NAME=groupname VALUE="+groupname+">");
        out.println("<td><INPUT TYPE=SUBMIT  class=creategroupbuttonstyle VALUE=\"Change Group \"></td>");
        out.println("</FORM>");

    }



    
    /****************************************** Client Session related Methods*******************************************/
    /********************************************************************************************************************/

    private void setSessionPrintWriter(HttpServletRequest req,PrintWriter out)
    {
        req.getSession().setAttribute("out",out);
    }
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
            res.sendRedirect("https://"+req.getServerName()+":"+ Main.getContainerConfig().getSSLPort()+"/gsn/MyGroupManagementServlet");

        }
    }
    private void redirectToLogin(HttpServletRequest req, HttpServletResponse res)throws IOException
    {
        req.getSession().setAttribute("login.target", HttpUtils.getRequestURL(req).toString());
        res.sendRedirect("/gsn/MyLoginHandlerServlet");
    }

  



}
