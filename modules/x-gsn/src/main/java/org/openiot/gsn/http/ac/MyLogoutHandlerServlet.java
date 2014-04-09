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
*/

package org.openiot.gsn.http.ac;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 15, 2010
 * Time: 7:40:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyLogoutHandlerServlet extends HttpServlet
{
    
     /****************************************** Servlet Methods*******************************************/
    /******************************************************************************************************/

    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
        HttpSession session = req.getSession(false);
        if(session != null)
        {
            session.invalidate();
        }
        res.setHeader("Cache-Control", "no-cache");
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Expires", "0");
        res.sendRedirect( "/");
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
        this.doGet(req,res);
    }


}
