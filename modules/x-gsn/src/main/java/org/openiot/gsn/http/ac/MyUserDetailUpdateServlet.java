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
import java.util.Vector;

public class MyUserDetailUpdateServlet extends HttpServlet
{
    private static transient Logger logger                             = Logger.getLogger( MyUserDetailUpdateServlet.class );
    /****************************************** Servlet Methods*******************************************/
    /****************************************************************************************************/
    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
	{
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null)
        {
           this.redirectToLogin(req,res);
        }
        else {
            res.setContentType("text/html");
            PrintWriter out = res.getWriter();
            checkSessionScheme(req, res);
            setSessionPrintWriter(req,out);
		    printHeader(out);
            printLayoutMastHead(out);
            printLayoutContent(out);
		    printForm(out, user);
		    printLayoutFooter(out);
        }

    }
    public void doPost(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
	{
        doGet(req,res);
        handleForm(req, res);
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
		out.println("<TITLE>Sign Up Form</TITLE>");
        out.println(" <link rel=\"stylesheet\" media=\"screen\" type=\"text/css\" href=\"/style/acstyle.css\"/>");
        //printStyle(out);
        out.println("</HEAD>");
        out.println("<body>");

        out.println("<div id=\"container\">");
        out.println("<div class=box>");

	}
    private void printLayoutMastHead(PrintWriter out)
    {
        out.println("<div id=\"masthead\">");

        out.println("<div class=\"image_float\"><img src=\"/style/gsn-mark.png\" alt=\"GSN logo\" /></div><br>");
        out.println("<h1>Sign Up Form </h1>");
        out.println("<div class=\"spacer\"></div>");

        out.println("</div>");
        out.println("<div id=\"mastheadborder\">");
        this.printLinks(out);
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
        out.println("<a class=linkclass href=\"/\">GSN home</a>");
        out.println("<a class=linkclass href=/gsn/MyAccessRightsManagementServlet>access rights management</a>");
    }
     private void printForm(PrintWriter out, User user) throws ServletException
	{
        Vector groupList = this.getGroupList();
        if(groupList==null)
        {
            out.println("<p><b>Can not print the form !</b></p>");
            return;
        }
        else
        {   out.println("<br>");
            out.println("<FORM METHOD=POST>");  // posts to itself

            //out.println("<div class=image_float>");
            out.println("<h2> Personal Information</h2>");
            //out.println("<br>");
            this.printPersonalInputs(out, user);
            out.println("<br>");
            out.println("<br>");
            //out.println("</div>");
            out.println("<h2> Account Information</h2>");
            //out.println("<font class=myhead> Account Information</font>");
            //out.println("<br>");
            this.printAccountInputs(out, user);
            out.println("<br>");
            out.println("<BR>");
            //out.println("<h2> Choose your group(s)</h2>");
            //out.println("<br>");
            //out.println("<font class=myhead> Choose your group(s)</font>");
            //this.printGroupList(out,groupList);

            out.println("<BR>");
            out.println("<BR>");
            this.printFormButtons(out);
            out.println("</FORM>");

        }

    }
    private void printPersonalInputs(PrintWriter out, User user)
    {
        out.println("<table>");
        out.println("<tr><th>first name</th><td><input class=\"inputclass\" type=\"text\" name=\"firstname\" size=\"30\" value=\"" + user.getFirstName() + "\" /></td></tr>");
        out.println("<tr><th>last name</th><td><input class=\"inputclass\" type=\"text\" name=\"lastname\" size=\"30\" value=\"" + user.getLastName() + "\" /></td></tr>");
        out.println("<tr><th>E-mail</th><td><input class=\"inputclass\" type=\"text\" name=\"email\"  size=\"30\" value=\"" + user.getEmail() + "\"/></td></tr>");

        out.println("</table>");

    }
    private void printAccountInputs(PrintWriter out, User user)
    {
        out.println("<table>");
        out.println("<tr><th>username</th><td>" + user.getUserName() + "</td></tr>");
        out.println("<tr><th>password</th><td><input class=\"inputclass\" type=\"password\" name=\"password\" size=\"30\" /></td></tr>");
        out.println("<tr><th>new password</th><td><input class=\"inputclass\" type=\"password\" name=\"newpassword\" size=\"30\" /></td></tr>");
        out.println("</table>");

    }

    private void printFormButtons(PrintWriter out)
    {
        //out.println("<table class=transparenttable>");
        out.println("<INPUT TYPE=SUBMIT class=bigsumitbuttonstyle VALUE=\"Submit \">");
        //out.println("<td><INPUT TYPE=RESET class=changegroupbuttonstyle VALUE=\"Reset\"></td></tr>");
        out.println("</table>");
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
           res.sendRedirect("https://"+req.getServerName()+":"+ Main.getContainerConfig().getSSLPort()+"/gsn/MyUserCandidateRegistrationServlet");

       }
   }
    /****************************************** DB related Methods******************************************************/
    /********************************************************************************************************************/

     private Vector getGroupList()
    {
        Vector groupList =null;
		ConnectToDB ctdb = null;
		try
		{   ctdb = new ConnectToDB();
			groupList = ctdb.getGroupList();
		}
        catch(Exception e)
        {

            logger.error("ERROR IN getGroupList");
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
        return groupList;
    }

    /****************************************** AC related Methods******************************************************/
    /********************************************************************************************************************/

    private void handleForm(HttpServletRequest req,HttpServletResponse res) throws IOException
    {
        HttpSession session = req.getSession();
        PrintWriter out = (PrintWriter) session.getAttribute("out");
        ParameterSet pm = new ParameterSet(req);
        if (session.getAttribute("user") != null)
        {
        User muser=allowUserToRegister(pm, out,new User((User)session.getAttribute("user")));
        if(muser!= null)
        {
            try
			{
				res.sendRedirect("/gsn/MyLogoutHandlerServlet");
			}
			catch (Exception ignored)
			{
				out.println("problem with redirecting to the target !");
			}
        }
        }
    }

    private boolean isNotDefined (ParameterSet pm, String name) {
        return pm.valueForName(name) == null || "".equals(pm.valueForName(name));
    }

    User allowUserToRegister(ParameterSet pm,PrintWriter out,User user)
    {
		//User waitinguser=null;
		ConnectToDB ctdb =null;
        EmailAddress emailadd=null;
        try
		{
			if(isNotDefined(pm,"password") || isNotDefined(pm,"firstname") || isNotDefined(pm,"lastname") || isNotDefined(pm,"email"))
			{
				//out.println("At least one of the input parameters is empty "+"<br>");
                user = null;
                this.managaeUserAlert(out, "At least one of the input parameters is empty " );
			}
			else
			{
                emailadd= new EmailAddress(pm.valueForName("email"));
                if (emailadd.isValid()==false)
	            {
	                //out.println("Invalid email address "+"<br>");
                    this.managaeUserAlert(out, "Invalid email address " );
	                //redirect
	               // return false;
	            }
                else
                {
                    ctdb =new ConnectToDB();
	                if(ctdb.valueExistsForThisColumn(new Column("USERNAME",user.getUserName()), "ACUSER"))
                    {
                        String pwd = Protector.encrypt(pm.valueForName("password"));
                        if(ctdb.isPasswordCorrectForThisUser(user.getUserName(), pwd)) // Check if the current password matchs
                        {
                            String newpwd = isNotDefined(pm, "newpassword") ? pwd : Protector.encrypt(pm.valueForName("newpassword"));
                            user.setPassword(newpwd);
                            user.setFirstName(pm.valueForName("firstname"));
                            user.setLastName(pm.valueForName("lastname"));
                            user.setEmail(pm.valueForName("email"));
                            if(ctdb.updateUserDetails(user))
                            {
                                logger.debug("Successfully updated the user details.");
                            }
                            else
                            {
                                user = null;
                                this.managaeUserAlert(out, "User Detail Update failed !" );
                            }
                        }
                        else
                        {
                            user = null;
                            this.managaeUserAlert(out, "The password does not match the current password." );
                        }
                    }
                    else
                    {
                        user = null;
                        this.managaeUserAlert(out, "This username does not exist and thus can't be updated." );
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

		return user;
	 }

    
    private void managaeUserAlert(PrintWriter out, String alertMessage)
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
        //out.println("<p>");
        //out.println("Failed to sign up, ");
        //out.println("you may want to try again !");
        //out.println("</p>");
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
        out.println("<!--");
        out.println("DisplayAlert('AlertBox',500,200);");
        out.println("//-->");
        out.println("</SCRIPT>");
    }

    private void redirectToLogin(HttpServletRequest req, HttpServletResponse res)throws IOException
    {
        req.getSession().setAttribute("login.target", HttpUtils.getRequestURL(req).toString());
        res.sendRedirect("/gsn/MyLoginHandlerServlet");
    }


}
