package org.openiot.gsn.storage.hibernate;

import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.storage.DataEnumeratorIF;
import org.openiot.gsn.utils.GSNRuntimeException;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

import java.io.Serializable;
import java.util.*;

public class HibernateStorage implements VirtualSensorStorage {

    private static final transient Logger logger = Logger.getLogger(HibernateStorage.class);

    private SessionFactory sf;

    private String identifier;

    private DataField[] structure;

    private static final int PAGE_SIZE = 1000;

    public static HibernateStorage newInstance(DBConnectionInfo dbInfo, String identifier, DataField[] structure, boolean unique) {
        try {
            return new HibernateStorage(dbInfo, identifier, structure, unique);
        }
        catch (RuntimeException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    private HibernateStorage(DBConnectionInfo dbInfo, String identifier, DataField[] structure, boolean unique) throws RuntimeException {
        String em = generateEntityMapping(identifier, structure, unique);
        this.sf = HibernateUtil.getSessionFactory(dbInfo.getDriverClass(), dbInfo.getUrl(), dbInfo.getUserName(), dbInfo.getPassword(), em);
        if (this.sf == null)
            throw new RuntimeException("Unable to instanciate the Storage for:" + identifier);
        this.identifier = identifier.toLowerCase();
        this.structure = structure;
    }

    public boolean init() {
        return true;
    }


    public Serializable saveStreamElement(StreamElement se) throws GSNRuntimeException {
        // Create the dynamic map 
        try {
            return storeElement(se2dm(se));
        }
        catch (org.hibernate.exception.ConstraintViolationException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Error occurred on inserting data to the database, an stream element dropped due to: ")
                    .append(e.getMessage())
                    .append(". (Stream element: ")
                    .append(se.toString())
                    .append(")");
            logger.warn(sb.toString());
            throw new GSNRuntimeException(e.getMessage());
        }
        catch (RuntimeException e) {
            throw new GSNRuntimeException(e.getMessage());
        }
    }

    public StreamElement getStreamElement(Serializable pk) throws GSNRuntimeException {
        Transaction tx = null;
        try {
            Session session = sf.getCurrentSession();
            tx = session.beginTransaction();
            Map<String, Serializable> dm = (Map<String, Serializable>) session.get(identifier, pk);
            tx.commit();
            return dm2se(dm);
        } catch (RuntimeException e) {
            try {
                if (tx != null)
                    tx.rollback();
            } catch (RuntimeException ex) {
                logger.error("Couldn't roll back transaction.");
            }
            throw e;
        }
    }

    public long countStreamElement() throws GSNRuntimeException {
        Transaction tx = null;
        try {
            Session session = sf.getCurrentSession();
            tx = session.beginTransaction();
            Criteria criteria = session.createCriteria(identifier);
            criteria.setProjection(Projections.rowCount());
            List count = criteria.list();
            tx.commit();
            return (Long) count.get(0);

        } catch (RuntimeException e) {
            try {
                if (tx != null)
                    tx.rollback();
            } catch (RuntimeException ex) {
                logger.error("Couldn't roll back transaction.");
            }
            throw e;
        }
    }

    public DataEnumeratorIF getStreamElements(int pageSize, Order order, Criterion[] crits, int maxResults) throws GSNRuntimeException {
        return new PaginatedDataEnumerator(pageSize, order, crits, maxResults);
    }

    public DataEnumeratorIF getStreamElements(int pageSize, Order order, Criterion[] crits) throws GSNRuntimeException {
        return getStreamElements(pageSize, order, crits, -1);
    }

    //

    private Serializable storeElement(Map dm) {
        Transaction tx = null;
        try {
            Session session = sf.getCurrentSession();
            tx = session.beginTransaction();
            Serializable pk = session.save(identifier, dm);
            tx.commit();
            return pk;
        } catch (RuntimeException e) {
            try {
                if (tx != null)
                    tx.rollback();
            } catch (RuntimeException ex) {
                logger.error("Couldn't roll back transaction.");
            }
            throw e;
        }
    }

    protected void finalize() throws Throwable {
        try {
            if (sf != null)
                HibernateUtil.closeSessionFactory(sf);
        } finally {
            super.finalize();
        }
    }


    //

    /**
     * @param gsnType
     * @return
     */
    public static String convertGSNTypeToLocalType(DataField gsnType) {
        switch (gsnType.getDataTypeID()) {
            case DataTypes.VARCHAR:
            case DataTypes.CHAR:
                return "string";
            case DataTypes.BIGINT:
            case DataTypes.TIME:
                return "long";
            case DataTypes.INTEGER:
                return "integer";
            case DataTypes.SMALLINT:
                return "short";
            case DataTypes.TINYINT:
                return "byte";
            case DataTypes.DOUBLE:
                return "double";
            case DataTypes.BINARY:
                return "binary";
        }
        return null;
    }

    private StreamElement dm2se(Map<String, Serializable> dm) {
        ArrayList<Serializable> data = new ArrayList<Serializable>();
        long timed = (Long) dm.get("timed");
        for (DataField df : structure) {
            if (!"timed".equalsIgnoreCase(df.getName()))
                data.add(dm.get(df.getName()));
        }
        return new StreamElement(structure, data.toArray(new Serializable[]{data.size()}), timed);
    }

    private Map<String, Serializable> se2dm(StreamElement se) {
        Map<String, Serializable> dm = new HashMap<String, Serializable>();
        dm.put("timed", se.getTimeStamp());
        for (String fieldName : se.getFieldNames()) {
            if (!"timed".equalsIgnoreCase(fieldName))
                dm.put(fieldName, se.getData(fieldName));
        }
        return dm;
    }

    /**
     * Create the Hibernate mapping configuration file for the specified virtual sensor, according to the structure.
     * The <code>pk</code> and <code>timed</code> are added by default to the mapping. Moreover, an index on
     * the <code>timed</code> field is created. Finally, an optional <code>UNIQUE</code> clause is added to the
     * <code>timed</code> column, iff the parameter <code>unique</code> is set to <code>true</code>.
     *
     * @param identifier
     * @param structure
     * @param unique
     * @return return a StringBuilder containing the hibernate mapping configuration
     */
    private static String generateEntityMapping(String identifier, DataField[] structure, boolean unique) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE hibernate-mapping PUBLIC\n");
        sb.append("\"-//Hibernate/Hibernate Mapping DTD 3.0//EN\"\n");
        sb.append("\"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\">\n");
        sb.append("<hibernate-mapping>\n");
        sb.append("<class entity-name=\"")
                .append(identifier.toLowerCase())
                .append("\" table=\"")
                .append(identifier.toLowerCase())
                .append("\">\n");
        //sb.append("<cache usage=\"read-only\"/>");
        // PK field
        sb.append("<id type=\"long\" column=\"PK\" name=\"pk\" >\n");
        sb.append("<generator class=\"native\"/>\n");
        sb.append("</id>\n");
        // TIMED field and it index
        sb.append("<property name=\"timed\"  column=\"TIMED\"  type=\"long\"");
        sb.append(" index=\"")
                .append(identifier.toUpperCase())
                .append("_TIMED_INDEX\"");
        sb.append(" not-null=\"true\"");
        if (unique) {
            sb.append(" unique=\"true\"");
        }
        sb.append(" />\n");
        // OTHER DATA FIELDS
        for (DataField df : structure) {
            if (!"timed".equalsIgnoreCase(df.getName())) {
                sb.append("<property name=\"")
                        .append(df.getName())
                        .append("\" column=\"")
                        .append(df.getName().toUpperCase())
                        .append("\" type=\"")
                        .append(convertGSNTypeToLocalType(df))
                        .append("\"/>\n");
            }
        }
        sb.append("</class>\n");
        sb.append("</hibernate-mapping>\n");
        return sb.toString();
    }

    //

    private class PaginatedDataEnumerator implements DataEnumeratorIF {

        /** The global max number of result returned */
        private int maxResults;

        private int currentPage;

        private int pageSize;

        private Order order;

        private Criterion[] crits;

        private Iterator<Map<String, Serializable>> pci;

        private boolean closed;

        private PaginatedDataEnumerator(int pageSize, Order order, Criterion[] crits, int maxResults) {
            this.maxResults = maxResults;
            this.pageSize = pageSize;
            this.order = order;
            this.crits = crits;
            currentPage = 0;
            pci = null;             //page content iterator
            if (maxResults == 0)
                close();
            hasMoreElements();
        }

        /**
         * This method checks if there is one or more {@link org.openiot.gsn.beans.StreamElement} available in the DataEnumerator.
         * If the current page is empty, it tries to load the next page.
         * @return
         */
        public boolean hasMoreElements() {

            // Check if the DataEnumerator is closed
            if (closed)
                return false;

            // Check if there is still data in the current pageContent
            if (pci != null && pci.hasNext())
                return true;

            // Compute the next number of elements to fetch
            int offset = currentPage * pageSize;
            int mr = pageSize;
            if (maxResults > 0) {
                int remaining = maxResults - offset;
                mr = remaining > 0 ? remaining >= pageSize ? pageSize : remaining % pageSize : 0;
            }

            // Try to load the next page
            pci = null;
            Transaction tx = null;
            try {
                Session session = sf.getCurrentSession();
                tx = session.beginTransaction();
                //
                Criteria criteria = session.createCriteria(identifier);
                for (Criterion criterion : crits) {
                    criteria.add(criterion);
                }
                criteria.addOrder(order);
                criteria.setCacheable(true);
                criteria.setReadOnly(true);
                criteria.setFirstResult(offset);
                criteria.setMaxResults(mr);
                //
                pci = criteria.list().iterator();
                tx.commit();
                currentPage++;

            } catch (RuntimeException e) {
                try {
                    if (tx != null)
                        tx.rollback();
                } catch (RuntimeException ex) {
                    logger.error("Couldn't roll back transaction.");
                }
                throw e;
            }
            if(pci != null &&  pci.hasNext()) {
                return true;
            }
            else {
                close();
                return false;
            }
        }

        public StreamElement nextElement() throws RuntimeException {
            if ( ! hasMoreElements())
                throw new IndexOutOfBoundsException("The DataEnumerator has no more StreamElement or is closed."); 
            else
                return dm2se(pci.next());
        }

        public void close() {
            if (! closed)
                 closed = true;
        }
    }
}
