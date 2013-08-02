package org.openiot.gsn.storage.hibernate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.storage.DataEnumeratorIF;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.Serializable;
import java.util.*;

public class TestHibernateStorage {

    private ArrayList<DataField> dataField = null;

    private static DBConnectionInfo dbInfo = null;

    @BeforeClass
    public static void initClass() {
        //dbInfo = new HibernateUtil.DBConnectionInfo("org.h2.Driver", "jdbc:h2:mem:test", "sa", "");
        dbInfo = new DBConnectionInfo("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/gsn", "root", "");
    }

    @Before
    public void setup() {
        // Contains all the type of fields defined in the class: DataTypes
        dataField = new ArrayList<DataField>();
        dataField.add(new DataField("f_varchar", "varchar(256)"));
        dataField.add(new DataField("f_char", "char(256)"));
        dataField.add(new DataField("f_integer", "integer"));
        dataField.add(new DataField("f_bigint", "bigint"));
        dataField.add(new DataField("f_binary", "binary"));
        dataField.add(new DataField("f_double", "double"));
        dataField.add(new DataField("f_time", "time"));
        dataField.add(new DataField("f_tinyint", "tinyint"));
        dataField.add(new DataField("f_smallint", "smallint"));
    }

    @Test
    public void testSuccessfulTableCreation() {
        String hm;
        // 1. Successful Creation with unique timed set
        HibernateStorage storage1 = HibernateStorage.newInstance(dbInfo, "testSuccessfulTableCreation1", dataField.toArray(new DataField[]{}), true);
        assertNotNull(storage1);
        // 2. Successful creation with unique timed unset
        HibernateStorage storage2 = HibernateStorage.newInstance(dbInfo, "testSuccessfulTableCreation2", dataField.toArray(new DataField[]{}), false);
        assertNotNull(storage2);
    }

    @Test
    public void testFailedTableCreation() {

        ArrayList<DataField> dfs;

        // 1. non valid field name
        dfs = new ArrayList<DataField>(dataField);
        dfs.add(new DataField("f_integer f", "integer")); // We add a non valid field name
        HibernateStorage storage1 = HibernateStorage.newInstance(dbInfo, "testFailedTableCreation1", dfs.toArray(new DataField[]{}), true);
        assertNull(storage1);

        // 2. non valid field name
        dfs = new ArrayList<DataField>(dataField);
        dfs.add(new DataField("f_integer-f", "integer")); // We add a non valid field name
        HibernateStorage storage2 = HibernateStorage.newInstance(dbInfo, "testFailedTableCreation2", dfs.toArray(new DataField[]{}), true);
        assertNull(storage1);

        // 3. non valid identifier name
        dfs = new ArrayList<DataField>(dataField);
        HibernateStorage storage3 = HibernateStorage.newInstance(dbInfo, "testFailedTableCre ation3", dfs.toArray(new DataField[]{}), true); // Invalid identifier name
        assertNull(storage1);

        // 4. duplicated identifier name
        dfs = new ArrayList<DataField>(dataField);
        //Duplicate the first field
        dfs.add(new DataField(dfs.get(0).getName(), dfs.get(0).getDataTypeID()));
        HibernateStorage storage4 = HibernateStorage.newInstance(dbInfo, "testFailedTableCreation4", dfs.toArray(new DataField[]{}), true); // Invalid identifier name
        assertNull(storage1);
    }

    @Test
    public void testMinMaxStorageOfData() {
        DataField[] structure = dataField.toArray(new DataField[]{});
        //
        HibernateStorage storage = HibernateStorage.newInstance(dbInfo, "testMinMaxStorageOfData", dataField.toArray(new DataField[]{}), false);
        assertNotNull(storage);
        // Build the StreamElement containing the min values for each fields.
        StreamElement seMin = generateStreamElement(structure, Byte.MIN_VALUE);
        seMin.setTimeStamp(100);
        // Build the StreamElement containinf the max values for each fields.
        StreamElement seMax = generateStreamElement(structure, Byte.MAX_VALUE);
        seMax.setTimeStamp(200);
        // Store them
        Serializable pkMin = storage.saveStreamElement(seMin);
        assertNotNull(pkMin);
        Serializable pkMax = storage.saveStreamElement(seMax);
        assertNotNull(pkMax);
        // Retrieve them
        StreamElement seMinOut = storage.getStreamElement(pkMin);
        assertNotNull(seMinOut);
        StreamElement seMaxOut = storage.getStreamElement(pkMax);
        assertNotNull(seMaxOut);
        //
        assertEquals(seMin.getTimeStamp(), seMinOut.getTimeStamp());
        assertEquals(seMax.getTimeStamp(), seMaxOut.getTimeStamp());
        for (DataField df : structure) {
            if (df.getDataTypeID() != DataTypes.BINARY) {
                assertEquals(seMin.getData(df.getName()), seMinOut.getData(df.getName()));
                assertEquals(seMax.getData(df.getName()), seMaxOut.getData(df.getName()));
            }
        }
    }

    @Test
    public void testCorrectInsertWithExtraField() {
        DataField[] structure = dataField.toArray(new DataField[]{});
        //
        HibernateStorage storage = HibernateStorage.newInstance(dbInfo, "testCorrectInsertWithExtraField", dataField.toArray(new DataField[]{}), false);
        assertNotNull(storage);
        // Build a StreamElement with an extra field compared to the structure.
        dataField.add(new DataField("extraField", DataTypes.INTEGER));
        StreamElement se = generateStreamElement(dataField.toArray(new DataField[]{}), Byte.MIN_VALUE);
        se.setTimeStamp(100);
        //
        Serializable pk = storage.saveStreamElement(se);
        assertNotNull(pk);
        StreamElement seOut = storage.getStreamElement(pk);
        // Check that no field is null in the fetched StreamElement
        for (DataField df : structure) {
            assertNotNull(seOut.getData(df.getName()));
        }
        // Check that the values are equals
        for (int i = 0; i < seOut.getData().length; i++) {
            if (seOut.getFieldTypes()[i] != DataTypes.BINARY)
                assertEquals(se.getData(seOut.getFieldNames()[i]), seOut.getData()[i]);
        }
    }

    @Test
    public void testCorrectInsertWithLessField() {
        DataField[] structure = dataField.toArray(new DataField[]{});
        //
        HibernateStorage storage = HibernateStorage.newInstance(dbInfo, "testCorrectInsertWithLessField", dataField.toArray(new DataField[]{}), false);
        assertNotNull(storage);
        // Build a StreamElement which miss a field compared to the structure.
        DataField removed = dataField.remove(0);
        StreamElement se = generateStreamElement(dataField.toArray(new DataField[]{}), Byte.MIN_VALUE);
        se.setTimeStamp(100);
        //
        Serializable pk = storage.saveStreamElement(se);
        assertNotNull(pk);
        StreamElement seOut = storage.getStreamElement(pk);
        assertNotNull(seOut);
        //
        for (DataField df : structure) {
            if (!removed.getName().equalsIgnoreCase(df.getName()))
                assertNotNull(seOut.getData(df.getName()));
            else
                assertNull(seOut.getData(df.getName()));
        }
        // Check that the values are equals
        for (int i = 0; i < seOut.getData().length; i++) {
            if (seOut.getFieldTypes()[i] != DataTypes.BINARY)
                assertEquals(se.getData(seOut.getFieldNames()[i]), seOut.getData()[i]);
        }
    }

    @Test
    public void testCorrectInsertionWithUnorderedFields() {
        DataField[] structure = dataField.toArray(new DataField[]{});
        //
        HibernateStorage storage = HibernateStorage.newInstance(dbInfo, "testCorrectInsertionWithUnorderedFields", dataField.toArray(new DataField[]{}), false);
        assertNotNull(storage);
        // Build a StreamElement which reorder fields
        DataField[] fields = dataField.toArray(new DataField[]{});
        DataField tmp = fields[fields.length - 1];
        fields[fields.length - 1] = fields[0];
        fields[0] = tmp;
        StreamElement se = generateStreamElement(fields, Byte.MIN_VALUE);
        se.setTimeStamp(100);
        //
        Serializable pk = storage.saveStreamElement(se);
        assertNotNull(pk);
        StreamElement seOut = storage.getStreamElement(pk);
        assertNotNull(seOut);
        // Check that the values are equals
        for (int i = 0; i < seOut.getData().length; i++) {
            if (seOut.getFieldTypes()[i] != DataTypes.BINARY)
                assertEquals(se.getData(seOut.getFieldNames()[i]), seOut.getData()[i]);
        }
    }

    @Test
    public void testCorrectInsertionWithNullValues() {
        DataField[] structure = dataField.toArray(new DataField[]{});
        //
        HibernateStorage storage = HibernateStorage.newInstance(dbInfo, "testCorrectInsertionWithNullValues", dataField.toArray(new DataField[]{}), false);
        assertNotNull(storage);
        // Build a StreamElement whith null values
        DataField[] fields = dataField.toArray(new DataField[]{});
        StreamElement se = generateStreamElement(fields, Byte.MIN_VALUE);
        se.setTimeStamp(100);
        for (int i = 0; i < se.getData().length; i++) {
            se.setData(i, null);
        }
        //
        Serializable pk = storage.saveStreamElement(se);
        assertNotNull(pk);
        StreamElement seOut = storage.getStreamElement(pk);
        assertNotNull(seOut);
        // Check that the values are equals
        for (int i = 0; i < seOut.getData().length; i++) {
            if (seOut.getFieldTypes()[i] != DataTypes.BINARY)
                assertEquals(se.getData(seOut.getFieldNames()[i]), seOut.getData()[i]);
        }
    }

    @Test(expected = org.openiot.gsn.utils.GSNRuntimeException.class)
    public void testWrongInsertDuToUniqueTimed() {
        DataField[] structure = dataField.toArray(new DataField[]{});
        //
        HibernateStorage storage = HibernateStorage.newInstance(dbInfo, "testWrongInsertDuToUniqueTimed", dataField.toArray(new DataField[]{}), true);
        assertNotNull(storage);
        // Build two StreamElement with the same timed field
        StreamElement se1 = generateStreamElement(structure, Byte.MIN_VALUE);
        se1.setTimeStamp(100);
        StreamElement se2 = generateStreamElement(structure, Byte.MAX_VALUE);
        se2.setTimeStamp(100);
        // Store the first stream element, should work
        Serializable pk = storage.saveStreamElement(se1);
        assertNotNull(pk);
        // Store the second stream element, should throw an exception because of the duplicated timed field.
        pk = storage.saveStreamElement(se2);
    }

    @Test(expected = org.openiot.gsn.utils.GSNRuntimeException.class)
    public void testWrongInsertDueToFormatTypeMismatch() {
        DataField[] structure = dataField.toArray(new DataField[]{});
        //
        HibernateStorage storage = HibernateStorage.newInstance(dbInfo, "testWrongInsertDueToFormatTypeMismatch", dataField.toArray(new DataField[]{}), false);
        assertNotNull(storage);
        // Build a StreamElement which change a field type compared to the structure.
        String oldName = dataField.get(0).getName();
        byte oldType = dataField.get(0).getDataTypeID();
        byte wrongType = DataTypes.INTEGER;
        //
        assertNotSame(wrongType, dataField.get(0).getDataTypeID()); // Check that we are seting a different type.
        dataField.set(0, new DataField(oldName, wrongType));
        assertEquals(structure.length, dataField.size()); // Check that the number of element match
        StreamElement se = generateStreamElement(dataField.toArray(new DataField[]{}), Byte.MIN_VALUE);
        se.setTimeStamp(100);
        // Should generate an exception because the StreamElement structure does not match the storage structure.
        Serializable pk = storage.saveStreamElement(se);
    }

    @Test
    public void testConcurrentInsertion() {
        final int nbThread = 10;
        final long nbStreamElement = 100;
        //
        final DataField[] structure = dataField.toArray(new DataField[]{});
        final HibernateStorage storage = HibernateStorage.newInstance(dbInfo, "testConcurrentInsertion", dataField.toArray(new DataField[]{}), false);
        assertNotNull(storage);
        //
        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < nbThread; i++) {
            final int j = i;
            threads.add(new Thread() {
                int tid = j;

                public void run() {
                    System.out.println("Thread " + tid + " has started.");
                    for (int k = 0; k < nbStreamElement; k++) {
                        // Build a StreamElement to store
                        StreamElement se1 = generateStreamElement(structure, Byte.MIN_VALUE);
                        se1.setTimeStamp(System.currentTimeMillis());
                        Serializable pk = storage.saveStreamElement(se1);
                        assertNotNull(pk);
                    }
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        // Wait that all the thread have completed
        for (Thread thread : threads) {
            try {
                thread.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Check that the number of elements euqlas: nbThread x nbStreamElement.
        assertEquals((nbThread * nbStreamElement), storage.countStreamElement());
    }

    @Test
    public void testPaginatedQueryWithoutCriterionWithLimit() {
        int numberOfElements = 283;
        String identifier = "testPaginatedQueryWithoutCriterionWithLimit";
        generateDataTest(identifier, numberOfElements);
        //
        DataField[] structure = dataField.toArray(new DataField[]{});
        HibernateStorage storage = HibernateStorage.newInstance(dbInfo, identifier, dataField.toArray(new DataField[]{}), false);
        assertNotNull(storage);
        //
        int[] pageSizes = new int[]{1, 11, numberOfElements / 2, numberOfElements / 3, numberOfElements / 3 - 1, numberOfElements / 3 + 1, numberOfElements - 1, numberOfElements + 1, numberOfElements * 2};
        //
        for (int pageSize : pageSizes) {
            System.out.println("testPaginatedQueryWithoutCriterionWithLimit ASC with pageSize: " + pageSize);
            // Set the limit to a fixed number: 1
            checkQueryResult(storage.getStreamElements(pageSize, Order.asc("timed"), new Criterion[]{}, 1), 1, 1);
            // Set the limit to a fixed number: 11
            checkQueryResult(storage.getStreamElements(pageSize, Order.asc("timed"), new Criterion[]{}, 11), 1, 11);
            // Set the limit to numberOfElements/10+1
            checkQueryResult(storage.getStreamElements(pageSize, Order.asc("timed"), new Criterion[]{}, numberOfElements / 10 + 1), 1, numberOfElements / 10 + 1);
            // Set the limit to numberOfElements/10-1
            checkQueryResult(storage.getStreamElements(pageSize, Order.asc("timed"), new Criterion[]{}, numberOfElements / 10 - 1), 1, numberOfElements / 10 - 1);
            // Set the limit to 0
            checkQueryResult(storage.getStreamElements(pageSize, Order.asc("timed"), new Criterion[]{}, 0), -1, -1);
        }
                //
        for (int pageSize : pageSizes) {
            System.out.println("testPaginatedQueryWithoutCriterionWithLimit DESC with pageSize: " + pageSize);
            // Set the limit to a fixed number: 1
            checkQueryResult(storage.getStreamElements(pageSize, Order.desc("timed"), new Criterion[]{}, 1), numberOfElements, numberOfElements);
            // Set the limit to a fixed number: 11
            checkQueryResult(storage.getStreamElements(pageSize, Order.desc("timed"), new Criterion[]{}, 11), numberOfElements, numberOfElements - 10);
            // Set the limit to numberOfElements/10+1
            checkQueryResult(storage.getStreamElements(pageSize, Order.desc("timed"), new Criterion[]{}, numberOfElements / 10 + 1), numberOfElements, numberOfElements - (numberOfElements / 10 + 1) + 1);
            // Set the limit to numberOfElements/10-1
            checkQueryResult(storage.getStreamElements(pageSize, Order.desc("timed"), new Criterion[]{}, numberOfElements / 10 - 1), numberOfElements, numberOfElements - (numberOfElements / 10 - 1) + 1);
            // Set the limit to 0
            checkQueryResult(storage.getStreamElements(pageSize, Order.desc("timed"), new Criterion[]{}, 0), -1, -1);
        }
    }

    @Test
    public void testPaginatedQueryWitCriterionWithLimit() {
        int numberOfElements = 817;
        String identifier = "testPaginatedQueryWitCriterionWithLimit";
        generateDataTest(identifier, numberOfElements);
        //
        DataField[] structure = dataField.toArray(new DataField[]{});
        HibernateStorage storage = HibernateStorage.newInstance(dbInfo, identifier, dataField.toArray(new DataField[]{}), false);
        assertNotNull(storage);
        //
        int[] pageSizes = new int[]{1, 11, numberOfElements / 2, numberOfElements / 3, numberOfElements / 3 - 1, numberOfElements / 3 + 1, numberOfElements - 1, numberOfElements + 1, numberOfElements * 2};
        //
        for (int pageSize : pageSizes) {
            System.out.println("testPaginatedQueryWitCriterionWithLimit with pageSize: " + pageSize);
            // Set the limit to a fixed number: 1 and timed > numberOfElements/2
            checkQueryResult(storage.getStreamElements(pageSize, Order.asc("timed"), new Criterion[]{Restrictions.gt("timed", (long)numberOfElements/2)}, 1), numberOfElements/2+1, numberOfElements/2+1);
            // Set the limit to a fixed number: 11 and timed > numberOfElements/2
            checkQueryResult(storage.getStreamElements(pageSize, Order.asc("timed"), new Criterion[]{Restrictions.gt("timed", (long)numberOfElements/2)}, 11), numberOfElements/2+1, numberOfElements/2+11);
            // Set the limit to 0 and timed > numberOfElements/2
            checkQueryResult(storage.getStreamElements(pageSize, Order.asc("timed"), new Criterion[]{Restrictions.gt("timed", (long)numberOfElements/2)}, 0), -1, -1);
        }
    }

    private void checkQueryResult(DataEnumeratorIF de, int firstTimed, int lastTimed) {
        System.out.println("Checking Query Result with expected firstTimed: " + firstTimed + ", expected lastTimed: " + lastTimed);
        assertNotNull(de);
        int nb = 0;
        if (lastTimed < 0 || firstTimed < 0) {
            while (de.hasMoreElements()) {
                StreamElement se = de.nextElement();
                assertNotNull(se);
                nb++;
            }
            assertEquals(0, nb);
        } else {
            if (firstTimed <= lastTimed) { // ASC
                while (de.hasMoreElements()) {
                    StreamElement se = de.nextElement();
                    assertNotNull(se);
                    assertEquals(firstTimed + nb, (int) se.getTimeStamp());
                    nb++;
                }
                assertEquals((lastTimed - firstTimed + 1), nb);
            } else { // DESC
                while (de.hasMoreElements()) {
                    StreamElement se = de.nextElement();
                    assertNotNull(se);
                    assertEquals(firstTimed - nb, (int) se.getTimeStamp());
                    nb++;
                }
                assertEquals((firstTimed - lastTimed + 1), nb);
            }
        }
    }

    //

    /**
     * @param fields
     * @param mode   mode < 0    : return a StreamElement with the minimal value for each field
     *               mode >= 0   : return a StreamElement with the maximal value for each field
     * @return
     */
    private StreamElement generateStreamElement(DataField[] fields, byte mode) {
        ArrayList<Serializable> data = new ArrayList<Serializable>();
        for (DataField df : fields) {
            switch (df.getDataTypeID()) {
                case DataTypes.VARCHAR:
                case DataTypes.CHAR:
                    data.add("A message.");
                    break;
                case DataTypes.INTEGER:
                    data.add(mode < 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE);
                    break;
                case DataTypes.BIGINT:
                    data.add(mode < 0 ? Long.MIN_VALUE : Long.MAX_VALUE);
                    break;
                case DataTypes.BINARY:
                    data.add(new byte[]{0x01, 0x02, 0x03});
                    break;
                case DataTypes.DOUBLE:
                    data.add(mode < 0 ? -1000000000000.0 : 1000000000000.0); // We don't use the Double.MIN & Double.MAX as the DBMS round the values and thus change them to -inf or +inf
                    break;
                case DataTypes.TIME:
                    data.add(System.currentTimeMillis());
                    break;
                case DataTypes.TINYINT:
                    data.add(mode < 0 ? Byte.MIN_VALUE : Byte.MAX_VALUE);
                    break;
                case DataTypes.SMALLINT:
                    data.add(mode < 0 ? Short.MIN_VALUE : Short.MAX_VALUE);
                    break;
            }
        }
        return new StreamElement(fields, data.toArray(new Serializable[]{}));
    }

    private void generateDataTest(String identifier, int nb) {
        DataField[] structure = dataField.toArray(new DataField[]{});
        HibernateStorage storage = HibernateStorage.newInstance(dbInfo, identifier, dataField.toArray(new DataField[]{}), false);
        assertNotNull(storage);
        //
        for (int k = 1; k <= nb; k++) {
            // Build a StreamElement to store
            StreamElement se1 = generateStreamElement(structure, Byte.MIN_VALUE);
            se1.setTimeStamp(k);
            Serializable pk = storage.saveStreamElement(se1);
            assertNotNull(pk);
        }
        //Check the number of elements
        assertEquals((long) nb, storage.countStreamElement());
    }
}
