package org.openiot.gsn.storage;

import org.openiot.gsn.storage.hibernate.DBConnectionInfo;
import org.openiot.gsn.utils.jndi.GSNContext;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

import javax.naming.NamingException;

public class DataSources {

    private static final transient Logger logger = Logger.getLogger( DataSources.class );

    public static BasicDataSource getDataSource(DBConnectionInfo dci) {
        BasicDataSource ds = null;
        try {
            ds = (BasicDataSource)GSNContext.getMainContext().lookup(Integer.toString(dci.hashCode()));
            if (ds == null) {
                ds = new BasicDataSource();
                ds.setDriverClassName(dci.getDriverClass());
                ds.setUsername(dci.getUserName());
                ds.setPassword(dci.getPassword());
                ds.setUrl(dci.getUrl());
		//ds.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                //ds.setAccessToUnderlyingConnectionAllowed(true); 
                GSNContext.getMainContext().bind(Integer.toString(dci.hashCode()), ds);
                logger.warn("Created a DataSource to: " + ds.getUrl());
            }
        } catch (NamingException e) {
            logger.error(e.getMessage(), e);
        }
        return ds;
    }
}
