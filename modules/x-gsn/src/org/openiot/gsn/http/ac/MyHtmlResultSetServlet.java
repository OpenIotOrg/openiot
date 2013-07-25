package org.openiot.gsn.http.ac;

import org.openiot.gsn.Main;
import org.openiot.gsn.http.WebConstants;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: May 5, 2010
 * Time: 10:33:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyHtmlResultSetServlet extends HttpServlet
{
    private static transient Logger logger                             = Logger.getLogger( MyHtmlResultSetServlet.class );
    /****************************************** Servlet Methods*******************************************/
    /******************************************************************************************************/

    public void doPost(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
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
               ParameterSet pm = new ParameterSet(req);
               if(pm.valueForName("tablename")==null)
               {
                   res.sendRedirect("/");
               }
               else
               {
                   if(pm.valueForName("tablename").equals(""))
                   {
                       res.sendRedirect("/");
                   }
                   else
                   {
                       try
                       {
                           this.setSessionPrintWriter(req,out);
                           ctdb = new ConnectToDB();
                           this.printHeader(out,pm.valueForName("tablename"));
                           this.printLayoutMastHead(out, user,pm.valueForName("tablename"));
                           this.printLayoutContent(out);
                           ResultSet resultset=null;
                           if(pm.valueForName("tablename").equals("ACUSER"))
                           {
                               resultset=ctdb.selectFiveColumns(new Column("USERNAME"),new Column("FIRSTNAME"),new Column("LASTNAME"),new Column("EMAIL"),new Column("ISCANDIDATE"),"ACUSER");
                           }
                           else
                           {
                                resultset=ctdb.selectAllColumns(pm.valueForName("tablename"));
                           }

                           out.println("<br>");
                           if(resultset==null)
                           {
                              out.println("<p> can not print the form ! </p>"); 
                           }
                           else
                           {
                            out.println(this.resultSetToString(resultset));
                           }
                           out.println("<br>");
                       }
                       catch(Exception e)
                       {
                           out.println("<p><b>Can not display table content!</b></p>");
                           logger.error("ERROR IN DOPOST");
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
       }
    }
    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
           this.doPost(req,res);
    }

    /****************************************** HTML Printing Methods*******************************************/
    /***********************************************************************************************************/

    private void printHeader(PrintWriter out, String tableName)
	{
        out.println("<HTML>");
        out.println("<HEAD>");
		out.println("<TITLE>"+ tableName +" Table Content</TITLE>");
        out.println(" <link rel=\"stylesheet\" media=\"screen\" type=\"text/css\" href=\"/style/acstyle.css\"/>");
        //printStyle(out);
        out.println("</HEAD>");
        out.println("<body>");
        out.println("<div id=\"container\">");
        out.println("<div class=box>");

	}

    private void printLayoutMastHead(PrintWriter out, User user, String tableName)
    {
        out.println("<div id=\"masthead\">");

        out.println("<div class=\"image_float\"><img src=\"/style/gsn-mark.png\" alt=\"GSN logo\" /></div>");
        out.println("<h1>"+ tableName +" Table Content</h1>");
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

    private void printLinks(PrintWriter out)
    {
        //out.println("<a class=linkclass href=\"/gsn/MyLoginHandlerServlet\">login</a>");
        //out.println("<a class=linkclass href=\"/gsn/MyAdminManagementServlet\">admin</a>");
        out.println("<a class=linkclass href=\"/gsn/MyDisplayACTablesContentServlet\"> display AC tables content</a>");
        out.println("<a class=linkclass href=\"/gsn/MyLogoutHandlerServlet\">logout</a>");

        //out.println("<a class=linkclass href=\"/\">GSN home</a>");

    }
    private void printUserName(PrintWriter out, User user)
    {
        //String username=user.getUserName();
        out.println("<p id=\"login\">logged in as : "+user.getUserName()+"</p>");
    }

    private void printLayoutFooter(PrintWriter out)
    {
        out.println("</div>");//content
        out.println("<div id=\"footer\">");
        out.println(" <p align=\"center\"><FONT COLOR=\"#000000\"/>Powered by <a class=\"nonedecolink\" href=\"http://globalsn.sourceforge.net/\">GSN</a>,  Distributed Information Systems Lab, EPFL 2010</p>");
        out.println("</div>");//footer
        out.println("</div>");//box
        out.println("</div>");//container
        out.println("</body>");
        out.println("</html>");
        out.println("<BR>");
        //out.println("<HR>");
    }


    /****************************************** AC related Methods*****************************************************/
    /******************************************************************************************************************/

    private String resultSetToString(ResultSet resultset)// can be called at most once
    {
        StringBuffer out = new StringBuffer();
        // Start a table to display the result set
        out.append("<TABLE>\n");
        try
        {
            ResultSetMetaData rsmd = resultset.getMetaData();
            int numcols = rsmd.getColumnCount();

            // Title the table with the result set's column labels
            out.append("<TR>");
            for (int i = 1; i <= numcols; i++)
            {
                out.append("<TH>" + rsmd.getColumnLabel(i));
            }
            out.append("</TR>\n");

            while(resultset.next())
            {
                out.append("<TR>"); // start a new row
                for (int i = 1; i <= numcols; i++)
                {
                    out.append("<TD>"); // start a new data element
                    Object obj = resultset.getObject(i);
                    if (obj != null)
                        out.append(obj.toString());
                    else
                        out.append("&nbsp;");
                }
                out.append("</TR>\n");
            }

            // End the table
            out.append("</TABLE>\n");
        }
        catch (SQLException e)
        {
            out.append("</TABLE><H1>ERROR:</H1> " + e.getMessage() + "\n");
        }
        return out.toString();
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
            res.sendRedirect("https://"+req.getServerName()+":"+ Main.getContainerConfig().getSSLPort()+"/gsn/MyHtmlResultSetServlet");

        }
    }
    private void redirectToLogin(HttpServletRequest req, HttpServletResponse res)throws IOException
    {
        req.getSession().setAttribute("login.target", HttpUtils.getRequestURL(req).toString());
        res.sendRedirect("/gsn/MyLoginHandlerServlet");
    }

}


