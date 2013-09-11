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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: May 5, 2010
 * Time: 4:02:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyRegistrationOkServlet  extends HttpServlet
{
    /****************************************** Servlet Methods*******************************************/
    /****************************************************************************************************/
    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
	{
        res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		printRegistrationOk(out);


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
       //out.println("<br>");
       out.println("<h1>Sign Up Form</h1>");
       out.println("<div class=\"spacer\"></div>");
       out.println("</div>");
       out.println("<div id=\"mastheadborder\">");
       this.printLinks(out);

       out.println("<br>");
       out.println("</div>");
   }
    
   private void printLayoutContent(PrintWriter out)
   {
       out.println("<div id=\"content\">");
   }

   private void printLinks(PrintWriter out)
   {
       out.println("<a class=linkclass href=\"/\">GSN home</a>");
   }

   private void printLayoutFooter(PrintWriter out)
   {
       out.println("</div>");//content
       out.println("<div id=\"footer\">");
       out.println(" <p align=\"center\"><FONT COLOR=\"#000000\"/>Powered by <a class=\"nonedecolink\" href=\"http://globalsn.sourceforge.net/\">GSN</a>,  Distributed Information Systems Lab, EPFL 2010</p>");
       out.println("</div>");//footer
       //out.println("</div>");//box
       out.println("</div>");//container
       out.println("</body>");
       out.println("</html>");
       out.println("<BR>");
       //out.println("<HR>");
   }
    private void printRegistrationOk(PrintWriter out)
    {
        printHeader(out);
        printLayoutMastHead(out);
        printLayoutContent(out);
        out.println("<p>GSN access have received your registration request!<BR>\n" +
                "You will receive a confirmation E-mail containing your account information in 24 hours. </p>");
        printLayoutFooter(out);
    }
}
