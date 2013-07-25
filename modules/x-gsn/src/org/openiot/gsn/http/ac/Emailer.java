package org.openiot.gsn.http.ac;

import org.openiot.gsn.utils.services.EmailService;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 27, 2010
 * Time: 7:03:49 PM
 * To change this template use File | Settings | File Templates.
 */

/* This class is used to send E-mails from a AC servlet, the Email sent encrypted,
  we are using EPFL SMTP server, so the parameters are fixed for that server */

public class Emailer
{
    private static transient Logger logger                             = Logger.getLogger( Emailer.class );

    public void sendEmail( String senderAlias, String receiverName,String receiverEmail,String subject, String msgHead, String msgBody, String msgTail)
    {

        ArrayList<String> to = new ArrayList<String>();
        to.add(receiverEmail);
        EmailService.sendEmail(to, subject,  msgHead + msgBody + msgTail);

    }



}
