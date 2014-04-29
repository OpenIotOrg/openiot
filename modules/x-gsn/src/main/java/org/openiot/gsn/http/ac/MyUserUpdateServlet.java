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
import org.apache.log4j.Logger;


import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 22, 2010
 * Time: 1:03:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyUserUpdateServlet  extends HttpServlet
{


    private static transient Logger logger                             = Logger.getLogger( MyUserUpdateServlet.class );
    /****************************************** Servlet Methods*******************************************/
    /****************************************************************************************************/

     public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        // Get the session
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null)
       {
          this.redirectToLogin(req, res);
       }
        else
        {
            this.checkSessionScheme(req,res);
            this.printHeader(out);
            this.printForm(out,user);

        }

    }
     public void doPost(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
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
        out.println("<TITLE>User Update</TITLE>");
        out.println(" <link rel=\"stylesheet\" media=\"screen\" type=\"text/css\" href=\"/style/acstyle.css\"/>");
        //printStyle(out);
        out.println("</HEAD>");
        out.println("<body onload=\"loadScroll()\" onunload=\"saveScroll()\" >");
        out.println("<div id=\"container\">");
        out.println("<div class=box>");
	}
     ///gsn/MyLogoutHandlerServlet
    private void printForm(PrintWriter out,User user)
	{

        ConnectToDB ctdb = null;
        try
        {
            ctdb = new ConnectToDB();

            this.printLayoutMastHead(out,user);

            this.printLayoutContent(out,user,ctdb);
            this.printLayoutSideBar(out,user,ctdb);
            this.printLayoutFooter(out);
        }
        catch(Exception e)
        {

            logger.error("ERROR IN printForm");
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

    private void printLayoutMastHead(PrintWriter out, User user)
    {
        out.println("<div id=\"masthead\">");

        out.println("<div class=\"image_float\"><img src=\"/style/gsn-mark.png\" alt=\"GSN logo\" /></div><br>");
        out.println("<h1>Update Access Rights Form </h1>");
        out.println("<div class=\"spacer\"></div>");

        out.println("</div>");
        out.println("<div id=\"mastheadborder\">");
        this.printLinks(out);
        this.printUserName(out, user);
        out.println("<br><br>");
        out.println("</div>");
    }
    private void printLayoutContent(PrintWriter out, User user, ConnectToDB ctdb)throws SQLException
    {
        //out.println("<div id=\"content\">");
        out.println("<div id=\"twocolumnscontent\">");
        this.printGroupSection(out, user, ctdb);
        out.println("</div>");

    }

    private void printLayoutSideBar(PrintWriter out, User user, ConnectToDB ctdb)throws SQLException
    {
        out.println("<div id=\"sidebar\">");
        this.printDataSourceSection(out, user, ctdb);
        out.println("</div>");
    }

    private void printLayoutFooter(PrintWriter out)
    {
        out.println("<div id=\"twocolumnsfooter\">");
        out.println("<p align=center><FONT COLOR=#000000>Powered by <a class=nonedecolink href=\"http://globalsn.sourceforge.net/\">GSN</a>,  Distributed Information Systems Lab, EPFL 2010</p>");
        out.println("</div>");//footer
        out.println("</div>");//box
        out.println("</div>");//container
        out.println("<BR>");
        //out.println("<HR>");
        out.println("</BODY>");
        out.println("</html>");

    }

    private void printDataSourceSection(PrintWriter out, User user, ConnectToDB ctdb) throws SQLException
    {
        out.println("<h2>");
        out.println("You have access to these virtual sensors : ");
        out.println("</h2>");
        out.println("<p>");
        this.printUserDataSourceList(out,user,ctdb);
        out.println("</p>");
        out.println("<h2>");
        out.println("Other virtual sensors in the system :");
        out.println("</h2>");
        out.println("<p>");
        this.printRemainingDataSourcesList(out,user,ctdb);
        out.println("</p>");
    }
    private void printGroupSection(PrintWriter out, User user, ConnectToDB ctdb) throws SQLException
    {
        out.println("<h2>");
        out.println("You have access to these groups :");
        out.println("</h2>");
        out.println("<p>");
        this.printUserGroupList(out,user,ctdb);
        out.println("</p>");
        out.println("<h2>");
        out.println("Other groups in the system :");
        out.println("</h2>");
        out.println("<p>");
        this.printRemainingGroupsList(out,user,ctdb);
        out.println("</p>");
    }


    private void printLinks(PrintWriter out)
    {
        //out.println("<a class=linkclass href=\"/\">GSN home</a>");
        //out.println("<a class=linkclass href=/gsn/MyAccessRightsManagementServlet>access rights management</a>");
        out.println("<a class=linkclass href=\"/gsn/MyUserAccountManagementServlet\">User account</a>");
        //out.println("<a class=linkclass href=\"/gsn/MyLoginHandlerServlet\">login</a>");
        out.println("<a class=linkclass href=\"/gsn/MyLogoutHandlerServlet\">logout</a>");


    }
    private void printUserName(PrintWriter out, User user)
    {
        //String username=user.getUserName();
        out.println("<p id=\"login\">logged in as : "+user.getUserName()+"</p>");


    }

    private void printUserGroupList(PrintWriter out,User user,ConnectToDB ctdb)throws SQLException
    {
        Group group=null;
        String groupName=null;
        String userName=user.getUserName();
        if(user.getGroupList().size()==0)
        {
            out.println("<table class=transparenttable>");
            out.println("<tr><td><FONT COLOR=#000000>No group is available.</td></tr>");
            out.println("</table>");
        }
        else
        {
            out.println("<table>");
            out.println("<tr>");
            out.println("<th>group name</th>");
            out.println("<th>group structure</th>");
            out.println("<th>updates</th>");
            out.println("</tr>");
            for(int i=0; i<user.getGroupList().size();i++)
            {
                out.println("<tr>");
                group=(Group)(user.getGroupList().get(i));
                groupName=group.getGroupName();
                out.println("<td>"+ groupName +"</td>");
                this.printGroupStructureLink(out, groupName);
                if(ctdb.valueExistsForThisColumnUnderTwoConditions(new Column("ISUSERWAITING","yes"),new Column("GROUPNAME",groupName),new Column("USERNAME",userName),"ACUSER_ACGROUP")==false)
                {
                    out.println("<FORM ACTION=/gsn/MyUpdateUserWaitingForGroupServlet METHOD=POST>");
                    out.println("<INPUT  TYPE=HIDDEN NAME=groupname VALUE="+groupName+">");
                    out.println("<INPUT  TYPE=HIDDEN NAME=deletegroup VALUE=Yes>");
                    out.println("<td style=text-align:center><INPUT TYPE=SUBMIT class= buttonstyle  VALUE=\"delete\"></td>");
                    out.println("</FORM>");
                }
                else
                {
                    out.println("<td>"+"<FONT COLOR=#0000FF>in updates waiting list!</td>");
                }
                out.println("</tr>");
            }
            out.println("</table>");
        }


    }
    private void printGroupStructureLink(PrintWriter out, String groupname)
    {
        String groupurl="/gsn/MyGroupHtmlResultSetServlet?groupname="+groupname;
        /*out.println("<td><a href="+groupurl+" onClick=\"poptastic(this.href); return false;\">display</a></td>");*/
        out.println("<ul class=displaylinkul >");
        out.println("<td style=text-align:center><LI class=displaylinkli><a href="+groupurl+" onClick=\"poptastic(this.href); return false;\">&nbsp&nbsp&nbsp view &nbsp&nbsp&nbsp</a></LI>");
        out.println("</td>");
        out.println("</ul>");

    }



    private void printRemainingGroupsList(PrintWriter out,User user,ConnectToDB ctdb)throws SQLException
    {
        Vector remainingGroupList =ctdb.getGroupListsDifference(ctdb.getGroupList(),user.getGroupList());
        Group group=null;
        String groupName=null;
        String userName=user.getUserName();
        if(remainingGroupList.size()==0)
        {
            out.println("<table class =transparenttable>");
            out.println("<tr><td><FONT COLOR=#000000>No group is available.</td></tr>");
            out.println("</table>");
        }
        else
        {
            out.println("<table>");
            out.println("<tr>");
            out.println("<th>group name</th>");
            out.println("<th>group structure</th>");
            out.println("<th>updates</th>");
            out.println("</tr>");

            for(int i=0; i<remainingGroupList.size();i++)
            {
                group=(Group)(remainingGroupList.get(i));
                groupName=group.getGroupName();
                out.println("<tr>");
                out.println("<td>"+ groupName +"</td>");
                this.printGroupStructureLink(out, groupName);
                if(ctdb.valueExistsForThisColumnUnderTwoConditions(new Column("ISUSERWAITING","yes"),new Column("GROUPNAME",groupName),new Column("USERNAME",userName),"ACUSER_ACGROUP")==false)
                {
                    out.println("<FORM ACTION=/gsn/MyUpdateUserWaitingForGroupServlet METHOD=POST>");
                    out.println("<INPUT  TYPE=HIDDEN NAME=groupname VALUE="+groupName+">");
                    out.println("<INPUT  TYPE=HIDDEN NAME=addgroup VALUE=Yes>");
                    out.println("<td style=text-align:center><INPUT TYPE=SUBMIT class= buttonstyle VALUE=\"add\"></td>");
                    out.println("</FORM>");
                }
                else
                {
                    out.println("<td>"+"<FONT COLOR=#0000FF>in updates waiting list!</td>");
                }
                out.println("</tr>");
            }
            out.println("</table>");
        }
    }
    private void printUserDataSourceList(PrintWriter out,User user,ConnectToDB ctdb)throws SQLException
    {
        DataSource dataSource=null;
        String dataSourceName=null;
        String dataSourceType=null;

        if(user.getDataSourceList().size()==0)
        {
            out.println("<table class =transparenttable>");
            out.println("<tr><td><FONT COLOR=#000000>No virtaul sensor is available.</td></tr>");
            out.println("</table>");
        }
        else
        {
            out.println("<table>");
            out.println("<tr><th> virtual sensor name </th>");
            out.println("<th> access right</th><th> time limitation</th></tr>");
            for(int j=0;j<user.getDataSourceList().size();j++)
            {
                dataSource=(DataSource)user.getDataSourceList().get(j);
                dataSourceName=dataSource.getDataSourceName();
                dataSourceType=dataSource.getDataSourceType();
                if(dataSourceType.equals("4"))
                {
                    out.println("<tr><td>" + dataSourceName + " </td>");
                    out.println("<td>own</td><td> unlimited </td></tr>");
                }
                else
                {
                    if(ctdb.valueExistsForThisColumnUnderTwoConditions(new Column("ISUSERWAITING","yes"),new Column("USERNAME",user.getUserName()),new Column("DATASOURCENAME",dataSourceName), "ACUSER_ACDATASOURCE"))
                    {
                          out.println("<tr><td>" + dataSourceName + " </td>");
                    }
                    else
                    {
                        out.println("<FORM ACTION=/gsn/MyUpdateUserWaitingForDataSourceServlet METHOD=POST>");
                        out.println("<tr><td>" + dataSourceName + " </td>");
                        if(dataSourceType.charAt(0)=='1')
                        {
                            out.println("<td><INPUT CHECKED TYPE=RADIO NAME="+dataSourceName+" VALUE= 1>read");
                            out.println("<INPUT  TYPE=RADIO NAME="+dataSourceName+" VALUE= 2>write ");
                            out.println("<INPUT  TYPE=RADIO NAME="+dataSourceName+" VALUE=3>read/write");
                            out.println("<INPUT  TYPE=RADIO NAME="+dataSourceName+" VALUE=0>delete ");
                        }
                        if(dataSourceType.charAt(0)=='2')
                        {
                            out.println("<td><INPUT TYPE=RADIO NAME="+dataSourceName+" VALUE= 1> read ");
                            out.println("<INPUT CHECKED TYPE=RADIO NAME="+dataSourceName+" VALUE= 2> write ");
                            out.println("<INPUT  TYPE=RADIO NAME="+dataSourceName+" VALUE=3> read/write ");
                            out.println("<INPUT  TYPE=RADIO NAME="+dataSourceName+" VALUE=0> delete ");
                        }
                        if(dataSourceType.charAt(0)=='3')
                        {
                            out.println("<td><INPUT TYPE=RADIO NAME="+dataSourceName+" VALUE= 1> read ");
                            out.println("<INPUT  TYPE=RADIO NAME="+dataSourceName+" VALUE= 2> write ");
                            out.println("<INPUT CHECKED TYPE=RADIO NAME="+dataSourceName+" VALUE=3> read/write ");
                            out.println("<INPUT  TYPE=RADIO NAME="+dataSourceName+" VALUE=0> delete ");
                        }
                    }
                    if(ctdb.valueExistsForThisColumnUnderTwoConditions(new Column("ISUSERWAITING","yes"),new Column("USERNAME",user.getUserName()),new Column("DATASOURCENAME",dataSourceName), "ACUSER_ACDATASOURCE"))
                    {
                        out.println("<td>"+"<FONT COLOR=#0000FF>in updates waiting list!</td></tr>");

                    }
                    else
                    {

                        out.println("&nbsp&nbsp&nbsp<INPUT TYPE=SUBMIT class= buttonstyle VALUE=\"update\"></td>");
                        out.println("</FORM>");
                    }
                    // get the time limitation for this datasource
                    String time = ctdb.getValueForOneColumnUnderTwoConditions(new Column("DEADLINE"), new Column("USERNAME", user.getUserName()), new Column("DATASOURCENAME", dataSourceName), "ACACCESS_DURATION");
                    if (time == null) time = "unlimited";
                    out.println("<td>" +time+"</tr></td>");
                }

            }
        out.println("</table>");
        }
    }
    private void printRemainingDataSourcesList(PrintWriter out,User user,ConnectToDB ctdb)throws SQLException
    {
        DataSource dataSource=null;
        String dataSourceName=null;
        Vector remainingDataSourcesList=ctdb.getDataSourceListsDifference(this.dataSourceVectorForDataSourceNames(ctdb.getValuesVectorForOneColumnUnderOneCondition(new Column("DATASOURCENAME"),new Column("ISCANDIDATE","no"),"ACDATASOURCE")),user.getDataSourceList());
        if(remainingDataSourcesList.size()==0)
        {
            out.println("<table class=transparenttable>");
            out.println("<tr><td><FONT COLOR=#000000>No virtaul sensor is available.</td></tr>");
            out.println("</table>");
        }
        else
        {
            out.println("<table>");
            out.println("<tr><th> virtual sensor name </th>");
            out.println("<th> access right</th></tr>");
            for(int i=0; i<remainingDataSourcesList.size();i++)
            {
                dataSource=(DataSource)(remainingDataSourcesList.get(i));
                dataSourceName=dataSource.getDataSourceName() ;
                if(ctdb.valueExistsForThisColumnUnderTwoConditions(new Column("ISUSERWAITING","yes"),new Column("USERNAME",user.getUserName()),new Column("DATASOURCENAME",dataSource.getDataSourceName()), "ACUSER_ACDATASOURCE"))
                {
                    out.println("<tr><td>" + dataSourceName + " </td>");
                }
                else
                {
                    out.println("<FORM ACTION=/gsn/MyUpdateUserWaitingForDataSourceServlet METHOD=POST>");
                    out.println("<tr><td>" + dataSourceName + " </td>");
                    out.println("<td><INPUT TYPE=RADIO NAME="+dataSourceName+" VALUE= 1> read ");
                    out.println("<INPUT TYPE=RADIO NAME="+dataSourceName+" VALUE= 2> write ");
                    out.println("<INPUT TYPE=RADIO NAME="+dataSourceName+" VALUE=3> read/write ");
                }
                if(ctdb.valueExistsForThisColumnUnderTwoConditions(new Column("ISUSERWAITING","yes"),new Column("USERNAME",user.getUserName()),new Column("DATASOURCENAME",dataSource.getDataSourceName()), "ACUSER_ACDATASOURCE"))
                {
                    out.println("<td>"+"<FONT COLOR=#0000FF>in updates waiting list!</td></tr>");
                }
                else
                {
                    out.println("&nbsp&nbsp&nbsp<INPUT TYPE=SUBMIT TYPE=SUBMIT class= buttonstyle VALUE=\"add\"></td></tr>");
                    out.println("</FORM>");
                }
            }
            out.println("</table>");
        }

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
            res.sendRedirect("https://"+req.getServerName()+":"+ Main.getContainerConfig().getSSLPort()+"/gsn/MyUserUpdateServlet");

        }
    }
    private void redirectToLogin(HttpServletRequest req, HttpServletResponse res)throws IOException
    {
        req.getSession().setAttribute("login.target", HttpUtils.getRequestURL(req).toString());
        res.sendRedirect("/gsn/MyLoginHandlerServlet");
    }
     private Vector dataSourceVectorForDataSourceNames(Vector dataSourceNames)
    {
        Vector dataSourceVector = new Vector();
       for(int i=0;i<dataSourceNames.size();i++)
       {
            dataSourceVector.add(new DataSource((String)dataSourceNames.get(i)));
       }
        return dataSourceVector;
    }




}


