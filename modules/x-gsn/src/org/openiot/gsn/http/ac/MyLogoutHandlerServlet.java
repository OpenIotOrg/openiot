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
