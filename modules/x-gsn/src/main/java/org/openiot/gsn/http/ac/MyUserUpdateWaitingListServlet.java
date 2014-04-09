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
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 26, 2010
 * Time: 2:34:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyUserUpdateWaitingListServlet  extends HttpServlet
{
      private static transient Logger logger                             = Logger.getLogger( MyUserUpdateWaitingListServlet.class );
    /****************************************** Servlet Methods*******************************************/
    /****************************************************************************************************/


    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
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
               this.printHeader(out);
               this.printLayoutMastHead(out, user );
               this.printLayoutContent(out);
               try
               {
                   ctdb = new ConnectToDB();
                   Vector waitingUsers = ctdb.getWaitingUsers();
                   if(waitingUsers.size()==0)
                   {
                       out.println("<p><B> There is no entry in the waiting user list !</B> </p>");
                   }
                   for(int i=0;i<waitingUsers.size();i++)
                   {
                        //printForm(out,(User)(waitingUsers.get(i)));

                       this.printNewEntry(out,(User)(waitingUsers.get(i)));

                        
                   }
               }
               catch(Exception e)
               {
                   out.println("<p><B> Can not print the form</B> </p>");

                   logger.error("ERROR IN DOGET");
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
               //out.println("</BODY>");
               this.printLayoutFooter(out);
           }


       }
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
        doGet(req,res);
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
		out.println("<TITLE>Users Updates Waiting List</TITLE>");
        out.println(" <link rel=\"stylesheet\" media=\"screen\" type=\"text/css\" href=\"/style/acstyle.css\"/>");
        //printStyle(out);
        out.println("</HEAD>");
        out.println("<body onload=\"loadScroll()\" onunload=\"saveScroll()\" >");
        //call for alert pop up 
        //out.println("<input type=\"button\" onclick=\"popup()\" value=\"popup\">");
        out.println("<div id=\"container\">");
        out.println("<div class=box>");

	}
    private void printLayoutMastHead(PrintWriter out, User user)
    {
        out.println("<div id=\"masthead\">");

        out.println("<div class=\"image_float\"><img src=\"/style/gsn-mark.png\" alt=\"GSN logo\" /></div><br>");
        out.println("<h1>Users Updates Waiting List</h1>");
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
    
    private void printNewEntry(PrintWriter out,User user) throws ServletException
    {
        out.println("<h2>New Entry In Waiting List</h2>");
        //out.println("<BR>");

        //out.println("<h3>User Information</h3>");
        out.println("<br>");
        out.println("<li class=registerli >User Information </li><br>");

        this.printUserInformation(out,user);

        //out.println("<h3>Selected Groups </h3>");
        out.println("<br>");
        out.println("<li class=registerli >Selected Groups </li>");

        this.printUserGroupList(out,user);

        //out.println("<h3>Selected Virtual Sensors</h3>");
        out.println("<br>");

        out.println("<li class=registerli>Selected Virtual Sensors</li><br>");

        this.printUserDataSourceList(out,user);

        out.println("<BR>");


   }
    //new version without group ds combunation
    private void printUserGroupList(PrintWriter out,User user)
    {
        Group group=null;
        String label=null;
        String groupName=null;
        String groupType=null;
        String userName = user.getUserName();
        if(user.getGroupList().size()==0)
        {

            out.println("<p>No group is selected.</p>");
        }
        else
        {
            out.println("<table >");
            out.println("<tr><th> group name </th>");
            out.println("<th> group structure</th>");
            out.println("<th> user choice</th>");
            out.println("<th> admin decision</th>");
            out.println("<th> admin decision</th></tr>");
            for(int i=0; i<user.getGroupList().size();i++)
            {

                group=(Group)(user.getGroupList().get(i));
                groupName=group.getGroupName();
                groupType=group.getGroupType();

                if(groupType.equals("5"))
                {
                    label=" user wants to add this group ";
                }
                else if(groupType.equals("0"))
                {
                    label=" user wants to delete this group ";
                }
                out.println("<tr><td>"+groupName +"</td>");
                this.printGroupStructureLink(out, group.getGroupName());
                out.println("<td>"+label  +" </td>");


                out.println("<FORM ACTION=/gsn/MyUpdateUserGroupServlet METHOD=POST>");
                out.println("<INPUT  TYPE=HIDDEN NAME=groupname VALUE="+groupName+">");
                out.println("<INPUT  TYPE=HIDDEN NAME=grouptype VALUE="+groupType+">");
                out.println("<INPUT TYPE=HIDDEN NAME=username VALUE= "+userName+">");
                out.println("<INPUT TYPE=HIDDEN NAME=update VALUE= yes>");
                out.println("<td><INPUT TYPE=SUBMIT class=creategroupbuttonstyle VALUE=\"agree to update\"></td>");
                out.println("</FORM>");

                out.println("<FORM ACTION=/gsn/MyUpdateUserGroupServlet METHOD=POST>");
                out.println("<INPUT  TYPE=HIDDEN NAME=groupname VALUE="+groupName+">");
                out.println("<INPUT  TYPE=HIDDEN NAME=grouptype VALUE="+groupType+">");
                out.println("<INPUT TYPE=HIDDEN NAME=username VALUE= "+userName+">");
                out.println("<INPUT TYPE=HIDDEN NAME=update VALUE= no>");
                out.println("<td><INPUT TYPE=SUBMIT class=creategroupbuttonstyle VALUE=\"refuse to update\"></td></tr>");
                out.println("</FORM>");
            }
            out.println("</table>");

        }

    }
    private void printGroupStructureLink(PrintWriter out, String groupname)
    {
        String groupurl="/gsn/MyGroupHtmlResultSetServlet?groupname="+groupname;
        out.println("<ul class=displaylinkul >");
        out.println("<td style=text-align:center><LI class=displaylinkli><a href="+groupurl+" onClick=\"poptastic(this.href); return false;\">&nbsp&nbsp&nbsp view &nbsp&nbsp&nbsp</a></LI></td>");
        out.println("</ul>");

    }

     private void printGroupListModulo(PrintWriter out,User user,int index)
    {
        Group group=null;
        String label=null;
        String groupName=null;
        String groupType=null;
        String userName = user.getUserName();
        group=(Group)(user.getGroupList().get(index));
        groupName=group.getGroupName();
        groupType=group.getGroupType();
        if(groupType.equals("5"))
        {
            label=" user wants to add this group ";
        }
        else if(groupType.equals("0"))
        {
             label=" user wants to delete this group ";
        }

        out.println("<table class=\"transparenttable\">");
        out.println("<tr><td><B>groupname: </B>"+groupName +"</td></tr>");
        out.println("<tr><td><B>user choice: </B>"+label  +" </td></tr>");
        out.println("</table>");
        out.println("<BR>");
        this.printGroupDataSourceList(out,group);
        out.println("<BR>");

        out.println("<FORM ACTION=/gsn/MyUpdateUserGroupServlet METHOD=POST>");
        out.println("<INPUT  TYPE=HIDDEN NAME=groupname VALUE="+groupName+">");
        out.println("<INPUT  TYPE=HIDDEN NAME=grouptype VALUE="+groupType+">");
        out.println("<INPUT TYPE=HIDDEN NAME=username VALUE= "+userName+">");
        out.println("<INPUT TYPE=HIDDEN NAME=update VALUE= yes>");
        out.println("<table class=\"transparenttable\">");
        out.println("<td><INPUT TYPE=SUBMIT class=creategroupbuttonstyle VALUE=\"agree to update\"></td>");
        out.println("</FORM>");

        out.println("<FORM ACTION=/gsn/MyUpdateUserGroupServlet METHOD=POST>");
        out.println("<INPUT  TYPE=HIDDEN NAME=groupname VALUE="+groupName+">");
        out.println("<INPUT  TYPE=HIDDEN NAME=grouptype VALUE="+groupType+">");
        out.println("<INPUT TYPE=HIDDEN NAME=username VALUE= "+userName+">");
        out.println("<INPUT TYPE=HIDDEN NAME=update VALUE= no>");
        out.println("<td><INPUT TYPE=SUBMIT class=creategroupbuttonstyle VALUE=\"refuse to update\"></td></tr>");
        out.println("</FORM>");

        out.println("</table>");

    }


    private void printGroupDataSourceList(PrintWriter out,Group group)
    {
        DataSource ds=null;
        String dsname=null;
        String dstype=null;
        String label=null;
        out.println("<table >");
        out.println(" <caption>"+group.getGroupName()+" combination</caption>");
        out.println("<tr><th> virtual sensor name </th>");
        out.println("<th> access right</th></tr>");

        for(int j=0;j<group.getDataSourceList().size();j++)
        {
            ds=(DataSource)group.getDataSourceList().get(j);
            dsname=ds.getDataSourceName();
            dstype=ds.getDataSourceType();

            if(dstype.charAt(0)=='1')
            {
                label="read";
            }
            else if(dstype.charAt(0)=='2')
            {
                label="write";
            }
            else if(dstype.charAt(0)=='3')
            {
                label="read/write";
            }
            out.println("<tr><td>" +dsname+"</td>");
            out.println("<td>" +label  +"</td></tr>");
        }
        out.println("</table>");

    }

    private void printUserInformation(PrintWriter out,User user)
    {
        out.println("<table>");
        out.println("<tr><th>username</th>");
        out.println("<th>user first name</th>");
        out.println("<th>user last name</th>");
        out.println("<th>user E-mail</th></tr>");
        out.println("<tr><td>"+user.getUserName() +"</td>");
        out.println("<td>"+user.getFirstName()  +" </td>");
        out.println("<td>"+user.getLastName() +"</td>");
        out.println("<td>"+user.getEmail() +"</td></tr>");
        out.println("</table>");

    }
    private void printUserDataSourceList(PrintWriter out,User user)
    {
        String userName=user.getUserName();
        DataSource ds=null;
        String dsname=null;
        String dstype=null;
        String ownerDecision=null;
        String label=null;

         if(user.getDataSourceList().size()==0)
        {
            out.println("<p>No virtaul sensor is selected.</p>");
            out.println("<BR>");
        }

        else
         {

            out.println("<table>");
            out.println("<tr><th> virtual sensor name </th>");
            out.println("<th> access right</th>");
            out.println("<th> owner decision</th>");
            out.println("<th> admin decision</th>");
            out.println("<th> admin decision</th></tr>");
            for(int i=0; i<user.getDataSourceList().size();i++)
            {

                ds=(DataSource)(user.getDataSourceList().get(i));

                dsname=ds.getDataSourceName();
                dstype=ds.getDataSourceType();
                ownerDecision=ds.getOwnerDecision();
               
                if(dstype.charAt(1)=='1')
                {
                    label="read";
                }
                else if(dstype.charAt(1)=='2')
                {
                    label="write";
                }
                else if(dstype.charAt(1)=='3')
                {
                    label="read/write";
                }
                else if(dstype.charAt(1)=='0')
                {
                    label="delete";
                }
                if(ownerDecision.equals("notreceived"))
                {
                     ownerDecision = "not received";
                }
                out.println("<tr><td>" +dsname+"</td>");
                out.println("<td>" +label  +"</td>");
                out.println("<td>" +ownerDecision  +"</td>");

                out.println("<FORM ACTION=/gsn/MyUpdateUserDataSourceServlet METHOD=POST>");
                out.println("<INPUT TYPE=HIDDEN NAME=username VALUE= "+userName+">");
                out.println("<INPUT TYPE=HIDDEN NAME= datasourcename VALUE= "+dsname+"> ");
                out.println("<INPUT TYPE=HIDDEN NAME= datasourcetype VALUE= "+dstype+"> ");
                out.println("<INPUT TYPE=HIDDEN NAME=update VALUE= yes>");
                out.println("<td><INPUT TYPE=SUBMIT class=creategroupbuttonstyle VALUE=\"agree to update \"></td>");
                out.println("</FORM>");

                out.println("<FORM ACTION=/gsn/MyUpdateUserDataSourceServlet METHOD=POST>");
                out.println("<INPUT TYPE=HIDDEN NAME=username VALUE= "+userName+">");
                out.println("<INPUT TYPE=HIDDEN NAME= datasourcename VALUE= "+dsname+"> ");
                out.println("<INPUT TYPE=HIDDEN NAME= datasourcetype VALUE= "+dstype+"> ");
                out.println("<INPUT TYPE=HIDDEN NAME=update VALUE= no>");
                out.println("<td><INPUT TYPE=SUBMIT class=creategroupbuttonstyle VALUE=\"refuse to update\"></td></tr>");
                out.println("</FORM>");
            }
         }
        out.println("</table>");

    }
    private void printFooter(PrintWriter out) throws ServletException
    {

        out.println("<p>\n" +
                "<table width=\"100%\"><tr>\n" +
                "<td align=right><A HREF=\"/gsn/MyLogoutHandlerServlet\">logout</a>"+
                "  <A HREF=/gsn/MyAdminManagementServlet>back to admin account management </a></td>"+
                "</tr></table>");
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
            res.sendRedirect("https://"+req.getServerName()+":"+ Main.getContainerConfig().getSSLPort()+"/gsn/MyUserUpdateWaitingListServlet");

        }
    }
    private void redirectToLogin(HttpServletRequest req, HttpServletResponse res)throws IOException
    {
        req.getSession().setAttribute("login.target", HttpUtils.getRequestURL(req).toString());
        res.sendRedirect("/gsn/MyLoginHandlerServlet");
    }





}
