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
import org.apache.log4j.Logger;


import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 25, 2010
 * Time: 10:48:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class MyOwnerWaitingListServlet extends HttpServlet
{
    /****************************************** Servlet Methods*******************************************/
    /****************************************************************************************************/
    private static transient Logger logger = Logger.getLogger( MyOwnerWaitingListServlet.class );

    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
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
            //ConnectToDB ctdb = new ConnectToDB();
            this.printHeader(out);
            this.printLayoutMastHead(out, user );
            this.printLayoutContent(out);
            try
            {
                ctdb = new ConnectToDB();
                Vector datasourceNames=ctdb.getDataSourceNamesListForThisOwner(user);
                if(datasourceNames.size()==0)
                {
                    out.println("<p>There is no virtual sensor entry in the owner waiting list  ! </p>");
                }
                else
                {
                    int sizeOfPastusers=0;
                    for(int i=0;i<datasourceNames.size();i++)
                    {
                        Vector users = ctdb.completeUsersList( ctdb.getUsersWaitingForThisOwnerDecision((String)(datasourceNames.get(i))));

                        if(users.size()!=0)
                        {
                            sizeOfPastusers=users.size();
                        }
                        for(int j=0;j<users.size();j++)
                        {
                            this.printNewEntry(out,(User)users.get(j));
                        }
                        if(users.size()==0 && (i+1)==datasourceNames.size() && sizeOfPastusers==0)
                        {
                            out.println("<p> There is no user entry in the owner waiting list ! </p>");
                        }
                    }
                }
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
        this.printLayoutFooter(out);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        handleForm(req, res);
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
		out.println("<TITLE>Owner Waiting List</TITLE>");
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
        out.println("<h1>Owner Waiting List </h1>");
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


     private void printNewEntry(PrintWriter out,User user) throws ServletException
     {
         out.println("<h2>New Entry In Waiting List</h2>");
         out.println("<div class=\"image_float\">");
         this.printUserInformation(out,user);
         out.println("</div>");
         this.printForms(out,user);
         out.println("<div class=\"spacer\"></div>");

    }

    private void printLinks(PrintWriter out)
    {
        //out.println("<a class=linkclass href=\"/gsn/MyLoginHandlerServlet\">login</a>");
        //out.println("<a class=linkclass href=/gsn/MyAccessRightsManagementServlet>access rights management</a>");
        out.println("<a class=linkclass href=\"/gsn/MyUserAccountManagementServlet\">User account</a>");
        out.println("<a class=linkclass href=\"/gsn/MyLogoutHandlerServlet\">logout</a>");
        //out.println("<a class=linkclass href=\"/\">GSN home</a>");
    }
    private void printUserName(PrintWriter out, User user)
    {
        //String username=user.getUserName();
        out.println("<p id=\"login\">logged in as : "+user.getUserName()+"</p>");


    }


    private void printForms(PrintWriter out,User user) throws ServletException
    {
        out.println("<B>&nbsp Do you agree that this candidate register for this virtual sensor? </B><br><br>");
        out.println("<table class=\"transparenttable\">");
        printForm(out, user);
        //printNoForm(out, user);
        out.println("</table>");

     }


    private void printForm(PrintWriter out, User user)
    {
        String username=user.getUserName();
        String datasourcename= user.getDataSource().getDataSourceName();
        out.println("<FORM METHOD=POST>");
        out.println("Time limitation (Month/Day/Year): <INPUT TYPE=\"date\" name=\"deadline\">");
        out.println("<INPUT TYPE=\"checkbox\" name=\"unlimited\" >Unlimited Access");
        out.println("<p>For Mozilla, please specify: Year/Month/Day</p>");
        out.println("<INPUT TYPE=HIDDEN NAME=username VALUE="+username+">");
        out.println("<INPUT TYPE=HIDDEN NAME=datasourcename VALUE="+datasourcename+">");
        //out.println("<INPUT TYPE=HIDDEN NAME=register VALUE= Yes>");
        out.println("<tr><td><INPUT TYPE=SUBMIT class= buttonstyle NAME=register VALUE=Yes></td>");
        out.println("<td><INPUT TYPE=SUBMIT class= buttonstyle NAME=register VALUE=No></td></tr>");
        out.println("</FORM>");

    }
    private void printNoForm(PrintWriter out, User user)
    {
        String username=user.getUserName();
        String datasourcename= user.getDataSource().getDataSourceName();
        out.println("<FORM METHOD=POST>");
        out.println("<INPUT TYPE=HIDDEN NAME=username VALUE="+username+">");
        out.println("<INPUT TYPE=HIDDEN NAME=datasourcename VALUE="+datasourcename+">");
        out.println("<INPUT TYPE=HIDDEN NAME=register VALUE= No>");
        out.println("<td><INPUT TYPE=SUBMIT class= buttonstyle VALUE=\"No\"></td></tr>");
        out.println("</FORM>");
    }
    private void printUserInformation(PrintWriter out,User user)
    {
        out.println("<table>");
        out.println("<tr>");
        out.println("<th>first name</th>");
        out.println("<td>"+ user.getFirstName() +"</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<th>last name</th>");
        out.println("<td>"+ user.getLastName() +"</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<th>E-mail</th>");
        out.println("<td>"+ user.getEmail() +"</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<th>virtual sensor name</th>");
        out.println("<td>"+ user.getDataSource().getDataSourceName()  +"</td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<th>access right</th>");
        out.println("<td>"+ setLabel(user.getDataSource())  +"</td>");
        out.println("</tr>");
        out.println("</table>");
        

    }


    private String setLabel(DataSource ds)
    {
        String label=null;
        if(ds.getDataSourceType().charAt(1)=='1')
        {
                label="read";
        }
        else if(ds.getDataSourceType().charAt(1)=='2')
        {
            label="write";
        }
        else if(ds.getDataSourceType().charAt(1)=='3')
        {
            label="read/write";
        }
        return label;

    }
    /****************************************** AC Related Methods*******************************************/
    /***********************************************************************************************************/

    void handleForm(HttpServletRequest req, HttpServletResponse res)
    {

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user"); ////////
        PrintWriter out = (PrintWriter) session.getAttribute("out");

        ParameterSet pm = new ParameterSet(req);

        ConnectToDB ctdb =null;
        try
        {
            String decision = null;   ///////////////
            ctdb= new ConnectToDB();
            if(pm.valueForName("register")==null)
            {
                return;
            }

            else if(pm.valueForName("register").equals("Yes"))
            {
                //pm.valueForName("unlimited")
                           // if the user has not specified a date and not unlimited access is set
                if (( pm.valueForName("unlimited") == null) && pm.valueForName("deadline").length() < 2) {     // The user should have defined a limitation date
                    this.manageUserAlert(out, "Please, specify the Time limitations!");    // print an error message

                } else {
                    if ( pm.valueForName("unlimited") == null) {   // if the user has provided a time limitation
                        ctdb.insertThreeColumnsValuesStrings(pm.valueForName("username"), pm.valueForName("datasourcename"), pm.valueForName("deadline"), "ACACCESS_DURATION");
                    }
                    ctdb.updateOwnerDecision("has accepted the registration",pm.valueForName("username"), pm.valueForName("datasourcename") );
                    decision = "has been accepted by its owner.\n";  ////////////////
                }
            }
            else if(pm.valueForName("register").equals("No"))
            {
                ctdb.updateOwnerDecision("has refused the registration",pm.valueForName("username"), pm.valueForName("datasourcename") );
                decision = "has been refused by its owner.\n"; ///////////////
            }
            if (decision != null) {
                Emailer email = new Emailer();
                User userFromBD = ctdb.getUserForUserName("Admin"); // get the details for the Admin account
                String msgHead = "Dear "+userFromBD.getFirstName() +", "+"\n"+"\n";
                String msgTail = "Best Regards,"+"\n"+"GSN Team";
                String msgBody = "A request for a Virtual Sensor " + decision
                        +"The Virtual Sensor is: " + pm.valueForName("datasourcename")+
                        "\nThe details of its owner are as follows: \n" +
                        "First name: " + user.getFirstName() + "\n"+
                        "Last name: " + user.getLastName() + "\n"+
                        "Email address: " + user.getEmail() + "\n\n"+
                        "You can manage this change by choosing the following options in GSN:\n"+
                        "Access Rights Management -> Admin Only -> Users Updates Waiting List\n"+
                        "or via the URL: "+req.getServerName()+":"+req.getServerPort()+"/gsn/MyUserUpdateWaitingListServlet\n\n";
                // first change Emailer class params to use sendEmail
                email.sendEmail( "GSN ACCESS ", "GSN USER",userFromBD.getEmail(),"Update for a Virtual Sensor access", msgHead, msgBody, msgTail);
            }

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


    private void manageUserAlert(PrintWriter out, String alertMessage)
    {
        this.createAlertBox(out, alertMessage);
        this.callAlertBox(out);
    }

    private void createAlertBox(PrintWriter out, String alertMessage)
    {

        out.println("<div id=\"AlertBox\" class=\"alert\">");
        out.println("<p>");
        out.println(alertMessage );
        out.println("</p>");
        out.println("<form style=\"text-align:right\">");
        out.println("<input");
        out.println("type=\"button\"");
        out.println("class= alertbuttonstyle");
        out.println("value=\"OK\"");
        out.println("style=\"width:75px;\"");
        out.println("onclick=\"document.getElementById('AlertBox').style.display='none'\">");
        out.println("</form>");
        out.println("</div>");
    }
    private void callAlertBox(PrintWriter out)
    {
        out.println("<SCRIPT LANGUAGE=\"JavaScript\" TYPE=\"TEXT/JAVASCRIPT\">");
        out.println("function DisplayAlert(id,left,top) {");
        out.println("document.getElementById(id).style.left=left+'px';");
        out.println("document.getElementById(id).style.top=top+'px';");
        out.println("document.getElementById(id).style.display='block';");
        out.println("}");
        out.println("DisplayAlert('AlertBox',500,200);");
        out.println("</SCRIPT>");
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
            res.sendRedirect("https://"+req.getServerName()+":"+ Main.getContainerConfig().getSSLPort()+"/gsn/MyOwnerWaitingListServlet");

        }
    }
    private void redirectToLogin(HttpServletRequest req, HttpServletResponse res)throws IOException
    {
        req.getSession().setAttribute("login.target", HttpUtils.getRequestURL(req).toString());
        res.sendRedirect("/gsn/MyLoginHandlerServlet");
    }




}

