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
 * Date: Apr 21, 2010
 * Time: 1:04:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyChangeGroupCombinationServlet  extends HttpServlet
{
    private static transient Logger logger                             = Logger.getLogger( MyChangeGroupCombinationServlet.class );
    /****************************************** Servlet Methods*******************************************/
    /******************************************************************************************************/

    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        // Get the session
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        ConnectToDB ctdb = null;
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
               printHeader(out);
               printLayoutMastHead( out, user);
               this.printLayoutContent(out);
               ParameterSet pm = new ParameterSet(req);
               if(pm.valueForName("groupname")==null)
               {
                   out.println("<p> can not print the form ! </p>");
               }
               else
               {
                   if(pm.valueForName("groupname").equals(""))
                   {
                       res.sendRedirect("/");

                   }
                   try
                   {
                        ctdb = new ConnectToDB();
                        printForm(out,new Group(pm.valueForName("groupname"),ctdb.getDataSourceListForGroup(pm.valueForName("groupname"))));

                   }
                   catch(Exception e)
                   {
                       out.println("<p> can not print the form ! </p>");

                       logger.error("ERROR IN DOGET : ");
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
           this.printLayoutFooter(out);
        }
        }

    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doGet(req,res);
        handleForm(req,res);
    }

    /****************************************** HTML Printing Methods*******************************************/
    /***********************************************************************************************************/

    private void printHeader(PrintWriter out)
	{
        out.println("<HTML>");
        out.println("<HEAD>");
		out.println("<TITLE>Change Group Structure Form</TITLE>");
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
        out.println("<h1>Change Group Structure Form</h1>");
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

    private void printForm(PrintWriter out,Group group)
    {
        String groupName= group.getGroupName();
        Vector remainingDataSourcelist=this.getDSNamesDifference(group.getDataSourceList());

        if(remainingDataSourcelist==null)
        {
            out.println("<p><LI>Can not print the form !</p>");
            return;
        }
        else
        {

            out.println("<FORM METHOD=POST>");  // posts to itself
            //out.println("<div class=\"image_float\">");
            this.printGroupDataSourceList(out,group);
            //out.println("</div>");
            out.println("<br>");
            out.println("<h2>Other virtual sensors</h2>");
            out.println("<BR>");
            this.printOtherDataSourceList(out,remainingDataSourcelist);
            //out.println("<div class=\"spacer\"></div>");
            //out.println("<BR>");
            out.println("<BR>");
            out.println("<BR>");
            this.printFormButtons(out);
            out.println("</FORM>");

        }
    }

    private void printLinks(PrintWriter out)
    {

        out.println("<a class=linkclass href=/gsn/MyGroupManagementServlet>group management</a>");
        out.println("<a class=linkclass href=\"/gsn/MyLogoutHandlerServlet\">logout</a>");
    }
    private void printUserName(PrintWriter out, User user)
    {
        //String username=user.getUserName();
        out.println("<p id=\"login\">logged in as : "+user.getUserName()+"</p>");


    }

    private void printGroupDataSourceList(PrintWriter out, Group group)
    {
        DataSource ds=null;
        String dsname=null;
        String dstype=null;
        String groupName= group.getGroupName();
        out.println("<h2>Group Information</h2><br>");
        out.println("<table>");
        out.println("<tr><th>group name</th><td>"+groupName+"</td></tr>");
        out.println("</table><br>");
        out.println("<h2>Curent virtual sensors in this group</h2><br>");

        if(group.getDataSourceList().size()==0)
        {
            out.println("<table class=transparenttable>");
            out.println("<tr><td>No virtaul sensor is available.</td></tr>");
            out.println("</table>");
            
        }
        else
        {   out.println("<table>");
            out.println("<tr><th> virtual sensor name </th>");
            out.println("<th> access right</th></tr>");
            for(int j=0;j<group.getDataSourceList().size();j++)
            {
                ds=(DataSource)group.getDataSourceList().get(j);
                dsname=ds.getDataSourceName();
                dstype=ds.getDataSourceType();
                out.println("<tr><td>" + dsname + "</td>");
                if(dstype.charAt(0)=='1')
                {
                    out.println("<td><INPUT CHECKED TYPE=RADIO NAME="+dsname+" VALUE= 1> read");
                    out.println("<INPUT  TYPE=RADIO NAME="+dsname+" VALUE= 2> write");
                    out.println("<INPUT  TYPE=RADIO NAME="+dsname+" VALUE=3> read/write");
                }
                else if(dstype.charAt(0)=='2')
                {
                    out.println("<td><INPUT TYPE=RADIO NAME="+dsname+" VALUE= 1> read ");
                    out.println("<INPUT CHECKED TYPE=RADIO NAME="+dsname+" VALUE= 2> write ");
                    out.println("<INPUT  TYPE=RADIO NAME="+dsname+" VALUE=3> read/write");
                }
                else if(dstype.charAt(0)=='3')
                {
                    out.println("<td><INPUT TYPE=RADIO NAME="+dsname+" VALUE= 1> read ");
                    out.println("<INPUT  TYPE=RADIO NAME="+dsname+" VALUE= 2> write");
                    out.println("<INPUT CHECKED TYPE=RADIO NAME="+dsname+" VALUE=3> read/write");
                }
                out.println("<INPUT  TYPE=RADIO NAME="+dsname+" VALUE=0> delete </td></tr>");
            }
            out.println("</table>");
        }




    }
    private void printOtherDataSourceList(PrintWriter out, Vector dataSourceList)
    {
        DataSource ds=null;
        String dataSourceName=null;
        
        //out.println("<table class=tablewithgreatleftmargin>");



        if(dataSourceList.size()==0)
        {
            out.println("<table class=transparenttable>");
            out.println("<tr><td>No virtaul sensor is available.</td></tr>");
            out.println("</table>");
        }
        else
        {
            out.println("<table>");
            out.println("<tr><th> virtual sensor name </th>");
            out.println("<th> access right</th></tr>");
            for(int i=0; i<dataSourceList.size();i++)
            {
                ds=(DataSource)(dataSourceList.get(i));
                dataSourceName=ds.getDataSourceName();
                out.println("<tr><td>" + dataSourceName + "</td>");
                out.println("<td><FONT COLOR=#333><INPUT TYPE=RADIO NAME="+dataSourceName+" VALUE= 1> read ");
                out.println("<FONT COLOR=#333><INPUT TYPE=RADIO NAME="+dataSourceName+" VALUE= 2> write ");
                out.println("<FONT COLOR=#333><INPUT TYPE=RADIO NAME="+dataSourceName+" VALUE=3> read/write </td></tr>");
            }
            out.println("</table>");

        }
    }

    private void printFormButtons(PrintWriter out)
    {
        //out.println("<table class=transparenttable>");
        out.println("<INPUT TYPE=SUBMIT class=sumitbuttonstyle VALUE=\"Submit\">");
        out.println("<INPUT TYPE=RESET class=sumitbuttonstyle VALUE=\"Reset\">");
        //out.println("</table>");
    }

    /****************************************** DB related Methods*******************************************/
    /********************************************************************************************************************/

     private Vector getDSNamesDifference(Vector newVector)
    {
        Vector dsNamesDifference =null;
		ConnectToDB ctdb = null;
		try
		{   ctdb = new ConnectToDB();
			dsNamesDifference = ctdb.getDataSourceListsDifference(this.dataSourceVectorForDataSourceNames(ctdb.getValuesVectorForOneColumnUnderOneCondition(new Column("DATASOURCENAME"),new Column("ISCANDIDATE","no"),"ACDATASOURCE")),newVector);
		}
        catch(Exception e)
        {

            logger.error("ERROR IN getDSNamesDifference : ");
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
        return dsNamesDifference;
    }
    /****************************************** AC related Methods*******************************************/
    /********************************************************************************************************************/


     private void handleForm(HttpServletRequest req,HttpServletResponse res) throws IOException
	{
        HttpSession session = req.getSession();
        PrintWriter out = (PrintWriter) session.getAttribute("out");
        ParameterSet pm = new ParameterSet(req);
        ConnectToDB ctdb = null;
        try
        {
            ctdb = new ConnectToDB();
            Group changedGroup= new Group(pm.valueForName("groupname"),ctdb.getChangedDataSourceListForParameterSet(pm));
            ctdb.changeGroupCombination(changedGroup);
            res.sendRedirect("/gsn/MyGroupManagementServlet");
        }
        catch(Exception e)
        {
            out.println("Change group combination was not successful. <BR>");
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
            res.sendRedirect("https://"+req.getServerName()+":"+ Main.getContainerConfig().getSSLPort()+"/gsn/MyChangeGroupCombinationServlet");

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



