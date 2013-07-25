package org.openiot.gsn.utils.jndi;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.util.Hashtable;



public class GSNContextFactory implements InitialContextFactory {

    private static GSNContext singleton = new GSNContext();

    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
        return singleton;
    }
}
