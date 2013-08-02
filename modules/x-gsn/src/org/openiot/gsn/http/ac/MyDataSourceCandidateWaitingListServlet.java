package org.openiot.gsn.http.ac;

import org.openiot.gsn.Main;
import org.openiot.gsn.http.WebConstants;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 21, 2010
 * Time: 8:54:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyDataSourceCandidateWaitingListServlet extends HttpServlet
{
    private static transient Logger logger                             = Logger.getLogger( MyDataSourceCandidateWaitingListServlet.class );
    /****************************************** Servlet Methods*******************************************/
    /******************************************************************************************************/

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
                    Vector v=ctdb.getDataSourceCandidates();
                    if(v.size()==0)
                    {
                        out.println("<p><B>There is no entry in the waiting list !</p></B>");
                     }
                     for(int i=0;i<v.size();i++)
                    {
                        //printForm(out,(DataSource)(v.get(i)));
                        printNewEntry(out,(DataSource)(v.get(i)));
                    }
                }
                catch(Exception e)
                {
                    out.println("<p><B>Can not print the form !</p></B>");
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
               this.printLayoutFooter(out);
           }

       }

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
		out.println("<TITLE>Virtual Sensor Registration Waiting List</TITLE>");
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
        out.println("<h1>Virtual Sensor Registration Waiting List</h1>");
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
    private void printNewEntry(PrintWriter out,DataSource datasource) throws ServletException
    {
        out.println("<h2>New Entry In Waiting List</h2>");
        out.println("<BR>");

        //out.println("<h3>Virtual Sensor Information</h3>");
        out.println("<li class=registerli >Virtual Sensor Information </li><br>");
        this.printDataSourceInformation(out,datasource);

        this.printForm(out,datasource);
        out.println("<br>");


   }
    private void printForm(PrintWriter out,DataSource datasource) throws ServletException
	{
        out.println("<FORM METHOD=POST>");  // posts to itself
        this.printFormInputs(out,datasource);
        //out.println("<h3>Admission Decision </h3>");
        out.println("<BR>");
        out.println("<li class=registerli >Admission Decision </li><br>");
        this.printAdmissionPart(out);
        out.println("<BR>");
        out.println("<BR>");
        this.printFormButtons(out);
		out.println("</FORM>");
		
    }
    private void printFormInputs(PrintWriter out,DataSource datasource)
    {
        String datasourcename=datasource.getDataSourceName();
	    out.println("<INPUT TYPE=HIDDEN NAME=datasourcename size=30 VALUE="+datasourcename +">");
    }
    private void printDataSourceInformation(PrintWriter out,DataSource datasource)
    {
        out.println("<table>");
        out.println("<tr><th>virtual sensor name</th>");
	    out.println("<th>file name</th>");
        out.println("<th>file type</th>");
	    out.println("<th>stored in</th>");
        out.println("<tr><td>"+datasource.getDataSourceName()+"</td>");
        out.println("<td>"+datasource.getFileName()+"</td>");
        out.println("<td>"+datasource.getFileType()+"</td>");
        out.println("<td>"+datasource.getPath() +"</td>");
        out.println("</table>");
        out.println("<br>");
        out.println("<li class=registerli >Virtual Sensor Owner Information</li><br>");
        //out.println("<h3>Virtual Sensor Owner Information</h3>");
        out.println("<table>");
        out.println("<tr><th>owner first name</th>");
        out.println("<th>owner last name</th>");
        out.println("<th>owner E-mail</th></tr>");
        out.println("<tr><td>"+datasource.getOwner().getFirstName() +"</td>");
        out.println("<td>"+datasource.getOwner().getLastName() +"</td>");
        out.println("<td>"+datasource.getOwner().getEmail() +"</td></tr>");


        out.println("</table>");

    }


    private void printAdmissionPart(PrintWriter out)
    {
        out.println("<table>");
	    out.println("<tr><th>Do you allow this virtual sensor registration?</th></tr>");
		//out.println("<tr><td><INPUT  TYPE=RADIO NAME=register VALUE= Yes><FONT COLOR=#000000> Yes ");
        out.println("<tr><td><select name=register id=selectbox>");
        out.println("<option value= >Select</option>");
        out.println(" <option value=Yes>Yes</option>");
        out.println(" <option value=No >No</option>");
        out.println(" </select></td></tr>");
        out.println("</table>");
        out.println("<BR>");
        out.println("<table >");
        out.println("<tr><th>If No, explain the reason here</th></tr> ");
        out.println("<tr><td><TEXTAREA NAME=comments COLS=40 ROWS=6></TEXTAREA></td></tr>");
        out.println("</table>");

    }
    private void printFormButtons(PrintWriter out)
    {
        //out.println("<table class=transparenttable>");
        out.println("<INPUT TYPE=SUBMIT class=sumitbuttonstyle VALUE=\"Submit \">");
        out.println("<INPUT TYPE=RESET class=sumitbuttonstyle VALUE=\"Reset\">");
        //out.println("</table>");

    }


    /****************************************** AC Related Methods*****************************************************/
    /******************************************************************************************************************/
    void handleForm(HttpServletRequest req, HttpServletResponse res)
    {
        HttpSession session = req.getSession();
		PrintWriter out = (PrintWriter) session.getAttribute("out");
		ParameterSet pm = new ParameterSet(req);
        String comments="";
        ConnectToDB ctdb = null;

        if(pm.valueForName("register")==null || pm.valueForName("datasourcename")==null ||pm.valueForName("datasourcename").equals(""))
        {
           return ;
        }
        else
        {
            if(pm.valueForName("register").equals("Yes")|| pm.valueForName("register").equals("No"))
            {
                try
                {
                    ctdb = new ConnectToDB();
                    User owner = ctdb.getUserFromDataSource(pm.valueForName("datasourcename"));  // get the owner of this data source
                    String message = null;
                    if(pm.valueForName("register").equals("Yes"))
                    {
                        String name = pm.valueForName("datasourcename")+".xml";
                        File file = new File("virtual-sensors/receivedVSFiles/" + name);
                        // move the VS into another directory
                        String newFilePath = "virtual-sensors/" + name;
                        File newFile = new File(newFilePath);

                        try {
                            FileUtils.copyFile(file, newFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ctdb.updateOneColumnUnderOneCondition(new Column("ISCANDIDATE","no"),new Column("DATASOURCENAME",pm.valueForName("datasourcename")),"ACDATASOURCE");
                        //send e-mail to the DS owner
                        message = " your Virtual Sensor '"+pm.valueForName("datasourcename")+"' has been activated.";
                    }
                    else if(pm.valueForName("register").equals("No"))
                    {
                        ctdb.deleteDataSourceCandidate(pm.valueForName("datasourcename"));
                        comments=pm.valueForName("comments");
                        //send e-mail to the DS owner
                        message = " unfortunately your Virtual Sensor '"+pm.valueForName("datasourcename")+"' will not be activated.\n"+
                        "The reason for that is the following: "+comments;
                    }
                    Emailer email = new Emailer();
                    // send an email to the Owner of the Resource
                    String msgHead = "Dear "+owner.getFirstName() +", "+"\n"+"\n";
                    String msgTail = "Best Regards,"+"\n"+"GSN Team";
                    String msgBody = "We would like to inform you that"+message+"\n\n";
                    email.sendEmail( "GSN ACCESS ", "GSN USER",owner.getEmail(),"Virtual Sensor Activation", msgHead, msgBody, msgTail);

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
            else
            {
                return;
            }
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
            res.sendRedirect("https://"+req.getServerName()+":"+ Main.getContainerConfig().getSSLPort()+"/gsn/MyDataSourceCandidateWaitingListServlet");

        }
    }
    private void redirectToLogin(HttpServletRequest req, HttpServletResponse res)throws IOException
    {
        req.getSession().setAttribute("login.target", HttpUtils.getRequestURL(req).toString());
        res.sendRedirect("/gsn/MyLoginHandlerServlet");
    }



}
