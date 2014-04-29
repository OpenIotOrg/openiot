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
import org.openiot.gsn.http.WebConstants;
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
 * Date: Apr 20, 2010
 * Time: 12:06:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyUserCandidateWaitingListServlet extends HttpServlet
{
    private static transient Logger logger                             = Logger.getLogger( MyUserCandidateWaitingListServlet.class );
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
               Vector candidates = this.getUserCandidateList();
               if(candidates==null)
               {
                   out.println("<p><B>Can not print the form !</p></B>");
                   return;
               }
               else
               {
                   if(candidates.size()==0)
                   {
                       out.println("<p><B>There is no entry in the waiting list !</p></B>");
                   }
                   for(int i=0;i<candidates.size();i++)
                   {
                       //printForm(out,(User)candidates.get(i));
                       printNewEntry(out,(User)candidates.get(i));
                       
                   }
               }
             this.printLayoutFooter(out);
           }
       }
        
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
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
		out.println("<TITLE>User Registration Waiting List</TITLE>");
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
        out.println("<h1>User Registration Waiting List</h1>");
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
        out.println("<h2>New Entry In Waiting List</h2><br>");
        //out.println("<BR>");

        //out.println("<h3>User Information</h3>");
        out.println("<li class=registerli >User Information </li><br>");
        this.printUserInformation(out,user);
        out.println("<br>");

        this.printForm(out,user);
        out.println("<br>");
        

   }
    private void printForm(PrintWriter out,User waitinguser) throws ServletException
	{
        out.println("<FORM METHOD=POST>");  // posts to itself
        this.printFormInputs(out,waitinguser);
        //out.println("<h3>Selected Groups </h3>");
        out.println("<li class=registerli >Selected Groups </li><br>");
        this.printGroupList(out,waitinguser.getGroupList());
        out.println("<br>");
		//out.println("<h3>Admission Decision </h3>");
        out.println("<li class=registerli >Admission Decision </li><br>");
        this.printAdmissionPart(out);
        out.println("<BR>");
        out.println("<BR>");
        this.printFormButtons(out);
		out.println("</FORM>");

	}
    private void printFormButtons(PrintWriter out)
    {
        //out.println("<table class=transparenttable>");
        out.println("<INPUT TYPE=SUBMIT class=sumitbuttonstyle VALUE=\"Submit \">");
        out.println("<INPUT TYPE=RESET class=sumitbuttonstyle VALUE=\"Reset\">");
        //out.println("</table>");
    }
    private void printGroupList(PrintWriter out,Vector groupList)
    {
        String grname=null;
        Group group=null;
        if(groupList.size()==0)
        {
            out.println("<table class=transparenttable>");
            out.println("<tr><td>No group is selected.</td></tr>");
             out.println("</table>");
        }
        else
        {
            out.println("<table>");
            //out.println("<caption >selected groups</caption>");
            out.println("<tr><th> group name </th>");
            out.println("<th>admin decision</th></tr>");

            for(int i=0; i<groupList.size();i++)
		    {
                group=(Group)groupList.get(i);
                grname=group.getGroupName();
            
                out.println("<tr><td>"+grname+"</td>");
                out.println("<td style=text-align:center><INPUT CHECKED TYPE=CHECKBOX NAME= groupname VALUE= "+grname+"></td></tr>");
            }
	        out.println("</table>");
        }
    }

    private void printAdmissionPart(PrintWriter out)
    {
        out.println("<table>");
	    out.println("<tr><th>Do you allow this candidate registration?</th></tr>");
		//out.println("<tr><td><INPUT  TYPE=RADIO NAME=register VALUE= Yes><FONT COLOR=#000000> Yes ");
        out.println("<tr><td><select name=register id=selectbox>");
        out.println("<option value= >Select</option>");
        out.println(" <option value=Yes>Yes</option>");
        out.println(" <option value=No >No</option>");
        out.println(" </select></td></tr>");
        out.println("</table>");
        out.println("<BR>");
        out.println("<table>");
        out.println("<tr><th>If No, explain the reason here: </th></tr> ");
        out.println("<tr><td><TEXTAREA NAME=comments COLS=40 ROWS=6></TEXTAREA> </td></tr>");
        out.println("</table>");

    }

    private void printUserInformation(PrintWriter out,User user)
    {
        out.println("<table>");
        out.println("<tr><th>username</th>");
	    out.println("<th>first name</th>");
	    out.println("<th>last name</th>");
	    out.println("<th>E-mail</th></tr>");
        out.println("<tr><td>"+user.getUserName()+"</td>");
        out.println("<td>"+user.getUserName() +"</td>");
        out.println("<td>"+user.getLastName()+"</td>");
        out.println("<td>"+user.getEmail() +"</td></tr>");

        out.println("</table>");

    }
    private void printFormInputs(PrintWriter out,User user)
    {
        String username=user.getUserName();
	    out.println("<INPUT TYPE=HIDDEN NAME=username size=30 VALUE="+username +">");
    }

    /****************************************** DB related Methods******************************************************/
    /********************************************************************************************************************/

     private Vector getUserCandidateList()
    {
        Vector candidateList =null;
		ConnectToDB ctdb = null;
		try
		{   ctdb = new ConnectToDB();
			candidateList = ctdb.getUserCandidates();
		}
		catch(ClassNotFoundException e)
		{

            logger.error("ERROR IN getUserCandidateList : Could not load database driver  ");
			logger.error(e.getMessage(),e);
		}
		catch(SQLException e)
		{

            logger.error("ERROR IN getUserCandidateList : SQLException caught  ");
            logger.error(e.getMessage(),e);
			while((e = e.getNextException())!= null )
			{
				logger.error(e.getMessage());
			}

		}
        catch(Exception e)
        {
            logger.error("ERROR IN getUserCandidateList : "+e.getMessage());
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
        return candidateList;
    }
    /****************************************** AC related Methods******************************************************/
    /********************************************************************************************************************/

    void handleForm(HttpServletRequest req, HttpServletResponse res)
    {
        User temp=null;
        ConnectToDB ctdb =null;
        HttpSession session = req.getSession();
        PrintWriter out = (PrintWriter) session.getAttribute("out");
        ParameterSet pm = new ParameterSet(req);
        Vector newGroupList=groupVectorForGroupNames(pm.getValuesForParam("groupname",req));
        String comments=null;
        User user = null;
        String msgHead = null;
        String msgBody = null;
        String msgTail = null;
        if(pm.valueForName("register")==null || pm.valueForName("register").equals("")  )
        {
            return;
        }
        else
        {
            try
            {
                ctdb =new ConnectToDB();
                user = ctdb.getUserForUserName(pm.valueForName("username"));
                Emailer email = new Emailer();
                msgHead = "Dear "+user.getFirstName() +", "+"\n"+"\n";
                msgTail = "Best Regards,"+"\n"+"GSN Team";
                if(pm.valueForName("register").equals("Yes"))
                {
                    ctdb.updateOneColumnUnderOneCondition(new Column("ISCANDIDATE","no"),new Column("USERNAME",pm.valueForName("username")),"ACUSER");

                    ctdb.deleteGroupListsDifferenceForUser(ctdb.getGroupListForUser(pm.valueForName("username")),newGroupList,pm.valueForName("username"));

                    msgBody = "Congratulations ! your registration as a GSN user has been accepted !"+"\n"
                              +"your username : "+user.getUserName()+"\n"+"You can now log in.\n";

                    //send E-mail to user..
                }
                else if(pm.valueForName("register").equals("No"))
                {
                    ctdb.deleteUserCandidate(pm.valueForName("username"));
                    comments=pm.valueForName("comments");

                    msgBody = "We are sorry ! your registration as a GSN user has been rejected !"+"\n"
                              +"Here is the reason : "+"\n"+comments+"\n"+"\n";

                    //send e-mail to user with comments
                }
                // first change Emailer class params to use sendEmail
                email. sendEmail( "GSN ACCESS ", "GSN USER",user.getEmail(),"Your registration to GSN ", msgHead, msgBody, msgTail);
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

    private Vector groupVectorForGroupNames(Vector groupNames)
    {
        Vector groupVector = new Vector();
       for(int i=0;i<groupNames.size();i++)
       {
            groupVector.add(new Group((String)groupNames.get(i)));
       }
        return groupVector;
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
            res.sendRedirect("https://"+req.getServerName()+":"+ Main.getContainerConfig().getSSLPort()+"/gsn/MyUserCandidateWaitingListServlet");

        }
    }
    private void redirectToLogin(HttpServletRequest req, HttpServletResponse res)throws IOException
    {
        req.getSession().setAttribute("login.target", HttpUtils.getRequestURL(req).toString());
        res.sendRedirect("/gsn/MyLoginHandlerServlet");
    }
    /****************************************** JS Methods*************************************************************/
    /******************************************************************************************************************/

    void printEmbeddedJS(PrintWriter out)
    {
        out.println("<script type=\"text/javascript\">");
        out.println("<!--");
        //this.printScrollbarPositionKeeperJS(out);
        out.println("// -->");
        out.println("</script>");

    }




}
