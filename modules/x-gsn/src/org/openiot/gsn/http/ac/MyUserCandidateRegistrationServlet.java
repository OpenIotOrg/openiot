package org.openiot.gsn.http.ac;

import org.openiot.gsn.Main;
import org.apache.log4j.Logger;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 18, 2010
 * Time: 7:31:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyUserCandidateRegistrationServlet extends HttpServlet
{
    private static transient Logger logger                             = Logger.getLogger( MyUserCandidateRegistrationServlet.class );
    /****************************************** Servlet Methods*******************************************/
    /****************************************************************************************************/
    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
	{
        res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		HttpSession session = req.getSession();
        checkSessionScheme(req, res);
        setSessionPrintWriter(req,out);
		printHeader(out);
        printLayoutMastHead(out);
        printLayoutContent(out);
		printForm(out);
		printLayoutFooter(out);


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
     private void printForm(PrintWriter out) throws ServletException
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
            this.printPersonalInputs(out);
            out.println("<br>");
            out.println("<br>");
            //out.println("</div>");
            out.println("<h2> Account Information</h2>");
            //out.println("<font class=myhead> Account Information</font>");
            //out.println("<br>");
            this.printAccountInputs(out);
            out.println("<br>");
            out.println("<BR>");
            out.println("<h2> Choose your group(s)</h2>");
            //out.println("<br>");
            //out.println("<font class=myhead> Choose your group(s)</font>");
            this.printGroupList(out,groupList);

            out.println("<BR>");
            out.println("<BR>");
            this.printFormButtons(out);
            out.println("</FORM>");

        }

    }
    private void printPersonalInputs(PrintWriter out)
    {
        out.println("<table>");
        out.println("<tr><th>first name</th><td><INPUT class=inputclass TYPE=TEXT NAME=firstname size=30></td></tr>");
        out.println("<tr><th>last name</th><td><INPUT class=inputclass TYPE=TEXT NAME=lastname size=30></td></tr>");
        out.println("<tr><th>E-mail</th><td><INPUT class=inputclass TYPE=TEXT NAME=email  size=30 ></td></tr>");
        
        out.println("</table>");

    }
    private void printAccountInputs(PrintWriter out)
    {
        out.println("<table>");
        out.println("<tr><th>username</th><td><INPUT class=inputclass TYPE=TEXT NAME=username size=30></td></tr>");
        out.println("<tr><th>password</th><td><INPUT class=inputclass TYPE=PASSWORD NAME=password size=30></td></tr>");
        out.println("</table>");

    }

    private void printFormButtons(PrintWriter out)
    {
        //out.println("<table class=transparenttable>");
        out.println("<INPUT TYPE=SUBMIT class=bigsumitbuttonstyle VALUE=\"Submit \">");
        //out.println("<td><INPUT TYPE=RESET class=changegroupbuttonstyle VALUE=\"Reset\"></td></tr>");
        out.println("</table>");
    }
    private void printGroupList(PrintWriter out,Vector groupList)
    {
        Group gr=null;
        String grname=null;
        Vector grds=null;

        if(groupList.size()==0)
        {
            out.println("<table class=invisibletable>");
            out.println("<tr><td>No group is available.</td></tr>");
            out.println("</table>");
        }
        else
        {
            out.println("<table border= 1 >");
            out.println("<tr>");
            out.println("<th>group name</th>");
            out.println("<th>group structure</th>");
            out.println("<th>choose this group</th>");
            out.println("</tr>");

            for(int i=0; i<groupList.size();i++)
            {
                out.println("<tr>");
                gr=(Group)(groupList.get(i));
                grname=gr.getGroupName();
                grds=gr.getDataSourceList();
                this.printGroupName(out, grname);
                this.printGroupStructureLink(out, grname);
                //this.printDataSourceList(out,gr);
                this.printCheckBox(out, grname);
                out.println("</tr>");
            }
            out.println("</table>");
        }
    } 
    private void printGroupName(PrintWriter out, String groupname)
    {
        out.println("<td>"+groupname +"</td>");
    }
    private void printCheckBox(PrintWriter out, String groupname)
    {
        out.println("<td style=text-align:center><INPUT TYPE=CHECKBOX class=checkboxclass NAME= groupname VALUE= "+groupname+"></td>");
    }
    private void printGroupStructureLink(PrintWriter out, String groupname)
    {
        String groupurl="/gsn/MyGroupHtmlResultSetServlet?groupname="+groupname;                                                           
        out.println("<ul class=displaylinkul >");

        out.println("<td style=text-align:center><LI class=displaylinkli><a href="+groupurl+" onClick=\"poptastic(this.href); return false;\">&nbsp&nbsp&nbsp view &nbsp&nbsp&nbsp</a></LI>");
        out.println("</td>");
        out.println("</ul>");
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
        //Vector groupnames= pm.getValuesForParam("groupname",req);
        //groupVectorForGroupNames(pm.getValuesForParam("groupname",req));
        User user=allowUserToRegister(pm, out,groupVectorForGroupNames(pm.getValuesForParam("groupname",req)));
        if(user!= null)
        {   ConnectToDB ctdb = null;  // used to retrieve admin data
            try
			{     //printRegistrationOk(out);
                // send a confirmation email to the user
                Emailer email = new Emailer();
                String msgHead = "Dear "+user.getFirstName() +", "+"\n"+"\n";
                String msgTail = "Best Regards,"+"\n"+"GSN Team";
                String msgBody = "Welcome to GSN, we would like to confirm your request for a new Account."+"\n"
                       +"You will receive another email confirming the activation of your account.\n\n";
                // first change Emailer class params to use sendEmail
                email.sendEmail( "GSN ACCESS ", "GSN USER",user.getEmail(),"Your registration to GSN ", msgHead, msgBody, msgTail);

				// send email to Administrator
                ctdb = new ConnectToDB();
                User userFromBD = ctdb.getUserForUserName("Admin"); // get the details for the Admin account
                msgHead = "Dear "+userFromBD.getFirstName() +", "+"\n"+"\n";
                msgTail = "Best Regards,"+"\n"+"GSN Team";
                msgBody = "A new Account has been created and awaits your confirmation."+"\n"
                        +"The account details are the following:\n"+
                          "First name: " + user.getFirstName() + "\n"+
                          "Last name: " + user.getLastName() + "\n"+
                          "Email address: " + user.getEmail() + "\n\n"+
                        "You can manage this change by choosing the following options in GSN:\n"+
                        "Access Rights Management -> Admin Only -> User Registration Waiting List\n"+
                        "or via the URL: "+req.getServerName()+":"+req.getServerPort()+"/gsn/MyUserCandidateWaitingListServlet\n\n";
                // first change Emailer class params to use sendEmail
                email.sendEmail( "GSN ACCESS ", "GSN USER",userFromBD.getEmail(),"Request for new GSN Account", msgHead, msgBody, msgTail);
                //logger.warn("The email address of the Admin is: " + userFromBD.getEmail());
                // redirect them to the page
				res.sendRedirect("/gsn/MyRegistrationOkServlet");
			}
			catch (Exception ignored)
			{
				out.println("problem with redirecting to the target !");
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
        else
        {
            /*out.println("Failed to sign up"+"<BR>");
			out.println("You may want to try again <BR>");*/
        }
    }

    User allowUserToRegister(ParameterSet pm,PrintWriter out,Vector groupList)
    {
		User waitinguser=null;
		ConnectToDB ctdb =null;
        EmailAddress emailadd=null;
        try
		{
			if(pm.hasEmptyParameter())
			{
				//out.println("At least one of the input parameters is empty "+"<br>");
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
	                if(ctdb.valueExistsForThisColumn(new Column("USERNAME",pm.valueForName("username")), "ACUSER")==false)
                    {
                        User temp=new User(pm.valueForName("username"),Protector.encrypt(pm.valueForName("password")),pm.valueForName("firstname"),pm.valueForName("lastname"),pm.valueForName("email"),groupList,"yes");
                        if(ctdb.registerUserCandidate(temp)== true)
                        {
                            waitinguser=temp;
                        }
                        else
                        {
                            //out.println("Registration failed !");
                            this.managaeUserAlert(out, "Registration failed !" );
                        }
                    }
                    else
                    {
                        //out.println("This username exists already in DB, choose another one!");
                        this.managaeUserAlert(out, "This username exists already in DB, choose another one!" );
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

		return waitinguser;
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
        out.println("<p>");
        out.println("Failed to sign up, ");
        out.println("you may want to try again !");
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
        out.println("<!--");
        out.println("DisplayAlert('AlertBox',500,200);");
        out.println("//-->");
        out.println("</SCRIPT>");
    }




}
