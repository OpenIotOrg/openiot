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

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 19, 2010
 * Time: 6:26:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyDataSourceCandidateRegistrationServlet extends HttpServlet
{

     private static transient Logger logger                             = Logger.getLogger( MyDataSourceCandidateRegistrationServlet.class );

    /****************************************** Servlet Methods*******************************************/
    /****************************************************************************************************/
    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
        res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		HttpSession session = req.getSession();


        User user = (User) session.getAttribute("user");

        if (user == null)
        {
            redirectToLogin(req,res);
        }
        else
        {
            this.checkSessionScheme(req,res);
            this.setSessionPrintWriter(req,out);
            printHeader(out);
            printLayoutMastHead(out, user);
            printLayoutContent(out);
            printForm(out);
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
		out.println("<TITLE>Virtual Sensor Registration Form</TITLE>");
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
        out.println("<h1>Virtual Sensor Registration Form </h1>");
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
        //out.println("<a class=linkclass href=\"/gsn/MyLoginHandlerServlet\">login</a>");
        //out.println("<a class=linkclass href=\"/\">GSN home</a>");
        //out.println("<a class=linkclass href=/gsn/MyAccessRightsManagementServlet>access rights management</a>");
        out.println("<a class=linkclass href=\"/gsn/MyUserAccountManagementServlet\">User account</a>");
        out.println("<a class=linkclass href=\"/gsn/MyLogoutHandlerServlet\">logout</a>");


    }
    private void printUserName(PrintWriter out, User user)
    {
        //String username=user.getUserName();
        out.println("<p id=\"login\">logged in as : "+user.getUserName()+"</p>");
    }


    private void printForm(PrintWriter out)
    {
        out.println("<p>Please enter the virtual sensor name and upload the related file:</p>");
        out.println(" <form ENCTYPE=\"multipart/form-data\" method=\"post\" id=\"enquiryform\">");
        out.println("<div class=formcaontainer>");
        out.println("<fieldset class=loginhandlerfieldset>");
        //out.println("<legend>Upload File</legend>");
        //out.println("<br>");
        //out.println("Please enter the virtual sensor name and upload the related file:<BR><BR>");
        this.printFormInputs(out);
        out.println("</fieldset>");
        out.println("</div>"); //end of form caontainer
        this.printFormButtons(out);
        out.println("</form>");
    }


    private void printFormInputs(PrintWriter out)
    {

        out.println(" <p style=line-height:150%><label for=\"virtual sensor name\">virtual sensor name</label>");
        out.println("<input type=\"text\" name=\"vsname\" id=\"vsname\" tabindex=\"1\" /><br></p>");
        out.println("<p><label for=\"file\">Upload file </label>");
        out.println("<input type=\"FILE\" name=\"file\" id=\"file\" tabindex=\"2\" /><br></p>");


    }
    
    private void printFormButtons(PrintWriter out)
    {
        out.println("<input type=\"submit\" class=fileuploaderbuttonstyle  value=\"Submit \" tabindex=\"3\" />");
    }

    /****************************************** AC related Methods******************************************************/
    /********************************************************************************************************************/
    private void handleForm(HttpServletRequest req,HttpServletResponse res) throws IOException, ServletException
	{

        ConnectToDB ctdb = null;
        HttpSession session = req.getSession();
		PrintWriter out = (PrintWriter) session.getAttribute("out");
        User user = (User) session.getAttribute("user");
		ParameterSet pm = new ParameterSet(req,"virtual-sensors/receivedVSFiles");
        logger.warn(pm);
        if(pm.hasEmptyParameter())
        {
            //out.println("Please enter the virtual sensor name. <BR>");
            //printError(out);
            this.managaeUserAlert(out, "Please enter the virtual sensor name. ",true );
        }
        else
        {
            try
            {
                ctdb = new ConnectToDB();
                if(ctdb.valueExistsForThisColumn(new Column("DATASOURCENAME",pm.valueForName("vsname")),"ACDATASOURCE")!=true)
                {

                    DataSource ds = pm.fileUploader((pm.valueForName("vsname")).toLowerCase(),"virtual-sensors/receivedVSFiles");
                    if( ds !=null)
                    {
                        ds.setIsCandidate("yes");
                        user.setIsWaiting("no");
                        ds.setOwner(user);
                        ctdb.registerDataSourceCandidate(ds);
                        // send an email to the Administrator
                        Emailer email = new Emailer();
                        User userFromBD = ctdb.getUserForUserName("Admin"); // get the details for the Admin account
                        String msgHead = "Dear "+userFromBD.getFirstName() +", "+"\n"+"\n";
                        String msgTail = "Best Regards,"+"\n"+"GSN Team";
                        String msgBody = "A new Virtual Sensor has been uploaded and awaits your activation."+"\n"
                                +"VS's name is: "+pm.valueForName("vsname")+"\n"
                                +"The user who uploaded the VS is the following:\n"+
                                "First name: " + user.getFirstName() + "\n"+
                                "Last name: " + user.getLastName() + "\n"+
                                "User name: " + user.getUserName() + "\n"+
                                "Email address: " + user.getEmail() + "\n\n"+
                                 "You can manage this request by choosing the following options in GSN:\n"+
                                "Access Rights Management -> Admin Only -> Virtual Sensor Registration Waiting List\n"+
                                "or via the URL: "+req.getServerName()+":"+req.getServerPort()+"/gsn/MyDataSourceCandidateWaitingListServlet\n\n";
                        // first change Emailer class params to use sendEmail
                        email.sendEmail( "GSN ACCESS ", "GSN USER",userFromBD.getEmail(),"New Virtual Sensor is Uploaded", msgHead, msgBody, msgTail);
                        this.managaeUserAlert(out, "File upload was successful.",false );
                    }
                     else
                    {
                        //out.println("Can not upload the file. <BR>");
                        //printError(out);
                        this.managaeUserAlert(out, "Can not upload the file. ",true );
                    }
                }
                else
                {
                    
                    //out.println("This datasource name exists already in DB. Choose another name! <BR>");
                    //printError(out);
                    this.managaeUserAlert(out, "This datasource name exists already in DB. Choose another name! ",true );
                }
            }
            catch(Exception e)
            {

                logger.error("ERROR IN handleForm");
			    logger.error(e.getMessage(),e);
                out.println("Can not upload the file. <BR>");
                printError(out);
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
    private void printError (PrintWriter out) throws ServletException
    {
        out.println("Failed to register the datasource. <BR>");
        out.println("You may want to try to upload again the file, or upload another file. <BR>");
    }
    private void managaeUserAlert(PrintWriter out, String alertMessage, boolean hasFailed)
    {
        this.createAlertBox(out, alertMessage, hasFailed);
        this.callAlertBox(out);
    }


    private void createAlertBox(PrintWriter out, String alertMessage, boolean hasFailed)
    {
        out.println("<div id=\"AlertBox\" class=\"alert\">");
        out.println("<p>");
        out.println(alertMessage );
        out.println("</p>");
        if(hasFailed== true)
        {
            out.println("<p>");
            out.println("Failed to register the datasource, ");
            out.println("you may want to try again !");
            out.println("</p>");
        }
        else
        {
            out.println("<p>");
            out.println(" Ready to upload the next file." );
            out.println("</p>");
        }

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
           res.sendRedirect("https://"+req.getServerName()+":"+ Main.getContainerConfig().getSSLPort()+"/gsn/MyDataSourceCandidateRegistrationServlet");

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
        this.printAlertBox(out);
        out.println("// -->");
        out.println("</script>");

    }
    void printAlertBox(PrintWriter out)
    {
        out.println("function DisplayAlert(id,left,top) {");
        out.println("document.getElementById(id).style.left=left+'px';");
        out.println("document.getElementById(id).style.top=top+'px';");
        out.println("document.getElementById(id).style.display='block';");
        out.println("}");
    }

}
