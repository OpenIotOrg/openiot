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
 * @author Jerome Rousselot
 * @author Mehdi Riahi
 * @author rhietala
 * @author gsn_devs
 * @author Ali Salehi
 * @author Timotee Maret
 * @author Mehdi Riahi
 * @author Sofiane Sarni
 * @author Milos Stojanovic
*/

package org.openiot.gsn.storage;

import org.openiot.gsn.Main;
import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.beans.StreamElement;
import org.openiot.gsn.http.datarequest.AbstractQuery;
import org.openiot.gsn.utils.GSNRuntimeException;
import org.openiot.gsn.utils.ValidityTools;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;

import org.apache.commons.dbcp.*;
import org.apache.log4j.Logger;

public abstract class StorageManager {

    private static final transient Logger logger = Logger.getLogger(StorageManager.class);

    private String databaseDriver;

    private BasicDataSource pool;

    public void init(String databaseDriver, String username, String password, String databaseURL, int maxDBConnections) {
        this.databaseDriver = databaseDriver;
        pool = DataSources.getDataSource(new DBConnectionInfo(databaseDriver,databaseURL,username,password));
        pool.setMaxActive(maxDBConnections);
        pool.setMaxIdle(maxDBConnections);

        pool.setRemoveAbandoned(true);    // removing unused connections, used to clean after poorly written code
        pool.setRemoveAbandonedTimeout(300);    // 5 minutes
        //
        Connection con = null;
        try {
            initDatabaseAccess(con = getConnection());
            logger.info(new StringBuilder().append("StorageManager DB connection initialized successfuly. driver:").append(databaseDriver).append(" url:").append(databaseURL));
        } catch (Exception e) {
            logger.error(new StringBuilder().append("Connecting to the database with the following properties failed :").append("\n\t UserName :").append(username).append("\n\t Password : ").append(password).append("\n\t Driver class : ").append(databaseDriver).append("\n\t Database URL : ").append(databaseURL).toString());
            logger.error(new StringBuilder().append(e.getMessage()).append(", Please refer to the logs for more detailed information.").toString());
            logger.error("Make sure in the gsn.xml file, the <storage ...> element is correct.");
            e.printStackTrace();
            if (logger.isInfoEnabled())
                logger.info(e.getMessage(), e);
        } finally {
            close(con);
        }
    }

    public void initDatabaseAccess(Connection con) throws Exception {}

    public abstract byte convertLocalTypeToGSN(int jdbcType, int precision);

    public abstract String getStatementDropIndex();

    public abstract String getStatementDropView();

    public abstract int getTableNotExistsErrNo();

    public abstract String addLimit(String query, int limit, int offset);

    public abstract StringBuilder getStatementUselessDataRemoval(String virtualSensorName, long storageSize);

    public byte convertLocalTypeToGSN(int jdbcType) {
        return convertLocalTypeToGSN(jdbcType, 0);
    }




    /**
     * Returns false if the table doesnt exist. Uses the current default
     * connection.
     *
     * @param tableName
     * @return False if the table doesn't exist in the current connection.
     * @throws SQLException
     */
    public boolean tableExists(CharSequence tableName) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            return tableExists(tableName, new DataField[]{}, connection);
        } finally {
            close(connection);
        }
    }

    /**
     * Checks to see if the given tablename exists using the given connection.
     *
     * @param tableName
     * @param connection
     * @return
     * @throws SQLException
     */
    public boolean tableExists(CharSequence tableName, Connection connection) throws SQLException {
        return tableExists(tableName, new DataField[]{}, connection);
    }


    public abstract StringBuilder getStatementRemoveUselessDataCountBased(String virtualSensorName, long storageSize) ;

    public StringBuilder getStatementRemoveUselessDataTimeBased(String virtualSensorName, long storageSize) {
        StringBuilder query = null;
        long timedToRemove = -1;
        Connection conn = null;
        try {
            ResultSet rs = Main.getStorage(virtualSensorName).executeQueryWithResultSet(new StringBuilder("SELECT MAX(timed) FROM ").append(virtualSensorName), conn = Main.getStorage(virtualSensorName).getConnection());
            if (rs.next())
                timedToRemove = rs.getLong(1);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            Main.getStorage(virtualSensorName).close(conn);
        }
        query = new StringBuilder().append("delete from ").append(virtualSensorName).append(" where ").append(virtualSensorName).append(".timed < ").append(timedToRemove);
        query.append(" - ").append(storageSize);
        return query;
    }

    public DataField[] tableToStructure(CharSequence tableName, Connection connection) throws SQLException {
        StringBuilder sb = new StringBuilder("select * from ").append(tableName).append(" where 1=0 ");
        ResultSet rs = null;
        DataField[] toReturn = null;
        try {
            rs = executeQueryWithResultSet(sb, connection);
            ResultSetMetaData structure = rs.getMetaData();
            ArrayList<DataField> toReturnArr = new ArrayList<DataField>();
            for (int i = 1; i <= structure.getColumnCount(); i++) {
                String colName = structure.getColumnLabel(i);
                if (colName.equalsIgnoreCase("pk")) continue;
                int colType = structure.getColumnType(i);
                byte colTypeInGSN = convertLocalTypeToGSN(colType);
                toReturnArr.add(new DataField(colName, colTypeInGSN));
            }
            toReturn = toReturnArr.toArray(new DataField[]{});
        } finally {
            if (rs != null)
                close(rs);
        }
        return toReturn;
    }

    /*
    * Alternative method to 'tableToStructure'
    * Useful for correctly creating structure for fields with variable length (like char, varchar, binary, blob)  
    * */
    public DataField[] tableToStructureByString(String tableName, Connection connection) throws SQLException {
        StringBuilder sb = new StringBuilder("select * from ").append(tableName).append(" where 1=0 ");
        ResultSet rs = null;
        DataField[] toReturn = null;
        try {
            rs = executeQueryWithResultSet(sb, connection);
            ResultSetMetaData structure = rs.getMetaData();
            ArrayList<DataField> toReturnArr = new ArrayList<DataField>();
            for (int i = 1; i <= structure.getColumnCount(); i++) {
                String colName = structure.getColumnLabel(i);
                if (colName.equalsIgnoreCase("pk")) continue;
                if (colName.equalsIgnoreCase("timed")) continue;
                int colType = structure.getColumnType(i);
                String colTypeName = structure.getColumnTypeName(i);
                int precision = structure.getPrecision(i);
                byte colTypeInGSN = convertLocalTypeToGSN(colType);
                if ((colTypeInGSN == DataTypes.VARCHAR) || (colTypeInGSN == DataTypes.CHAR))
                    toReturnArr.add(new DataField(colName, colTypeName, precision, colName));
                else
                    toReturnArr.add(new DataField(colName, colTypeInGSN));
            }
            toReturn = toReturnArr.toArray(new DataField[]{});
        } finally {
            if (rs != null)
                close(rs);
        }
        return toReturn;
    }


    /**
     * Returns false if the table doesnt exist. If the table exists but the
     * structure is not compatible with the specified fields the method throws
     * GSNRuntimeException. Note that this method doesn't close the connection
     *
     * @param tableName
     * @param connection (this method will not close it and the caller is responsible
     *                   for closing the connection)
     * @return
     * @throws SQLException
     * @Throws GSNRuntimeException
     */

    public boolean tableExists(CharSequence tableName, DataField[] fields, Connection connection) throws SQLException,
            GSNRuntimeException {
        if (!ValidityTools.isValidJavaVariable(tableName))
            throw new GSNRuntimeException("Table name is not valid");
        StringBuilder sb = new StringBuilder("select * from ").append(tableNameGeneratorInString(tableName)).append(" where 1=0 ");
        ResultSet rs = null;
        try {
            rs = executeQueryWithResultSet(sb, connection);
            ResultSetMetaData structure = rs.getMetaData();
            if (fields != null && fields.length > 0)
                nextField:for (DataField field : fields) {
                    for (int i = 1; i <= structure.getColumnCount(); i++) {
                        String colName = structure.getColumnLabel(i);
                        int colType = structure.getColumnType(i);
                        int colTypeScale = structure.getScale(i);
                        if (field.getName().equalsIgnoreCase(colName))
                            if (field.getDataTypeID() == convertLocalTypeToGSN(colType, colTypeScale))
                                continue nextField;
                            else
                                throw new GSNRuntimeException("The column : "
                                        + colName + " in the >" + tableName
                                        + "< table is not compatible with type : "
                                        + field.getType()
                                        + ". The actual type for this table (currently in the database): " + colType);
                    }
                    throw new GSNRuntimeException("The table " + tableName
                            + " in the database, doesn't have the >" + field.getName()
                            + "< column.");
                }
        } catch (SQLException e) {
            if (e.getErrorCode() == getTableNotExistsErrNo() || e.getMessage().contains("does not exist"))
                return false;
            else {
                logger.error(e.getErrorCode());
                throw e;
            }
        } finally {
            close(rs);
        }
        return true;
    }

    public boolean tableExists(CharSequence tableName, DataField[] fields)
            throws SQLException {
        Connection conn = null;
        boolean to_return = true;
        try {
            conn = getConnection();
            to_return = tableExists(tableName, fields, conn);
        } finally {
            close(conn);
        }
        return to_return;
    }

    /**
     * Returns true if the specified query has any result in it's result set.
     * The created result set will be closed automatically.
     *
     * @param sqlQuery
     * @return
     */
    public boolean isThereAnyResult(StringBuilder sqlQuery) {
        boolean toreturn = false;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement prepareStatement = connection.prepareStatement(
                    sqlQuery.toString());
            ResultSet resultSet = prepareStatement.executeQuery();
            toreturn = resultSet.next();
        } catch (SQLException error) {
            logger.error(error.getMessage(), error);
        } finally {
            close(connection);
        }
        return toreturn;
    }

    /**
     * Executes the query of the database. Returns the specified colIndex of the
     * first row. Useful for image recovery of the web interface.
     *
     * @param query The query to be executed.
     * @return A resultset with only one row and one column. The user of the
     *         method should first call next on the result set to make sure that
     *         the row is there and then retrieve the value for the row.
     * @throws SQLException
     */

    public ResultSet getBinaryFieldByQuery(StringBuilder query,
                                           String colName, long pk, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(query.toString());
        ps.setLong(1, pk);
        return ps.executeQuery();
    }

    public void closeStatement(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void close(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void close(PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void close(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            logger.debug(e.getMessage(), e);
        }
    }

    /**
     * @throws SQLException
     */
    public void shutdown() throws SQLException {
        logger.warn("Closing the connection pool [done].");
    }

    /**
     * ************************************************************************
     * Various Statement Executors.
     * ************************************************************************
     */

    public void executeRenameTable(String oldName, String newName)
            throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            executeRenameTable(oldName, newName, conn);
        } finally {
            close(conn);
        }

    }

    public void executeRenameTable(String oldName, String newName, Connection connection) throws SQLException {
        PreparedStatement prepareStatement = null;
        try {
            prepareStatement = connection.prepareStatement(getStatementRenameTable(oldName, newName));
            prepareStatement.execute();
        } finally {
            close(prepareStatement);
        }

    }

    public void executeDropTable(CharSequence tableName) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            executeDropTable(tableName, conn);
        } finally {
            close(conn);
        }
    }

    public void executeDropTable(CharSequence tableName, Connection connection) {
        PreparedStatement prepareStatement = null;
        try {
            String stmt = getStatementDropTable(tableName, connection).toString();
            if (logger.isDebugEnabled())
                logger.debug("Dropping table structure: " + tableName + " With query: " + stmt);
            prepareStatement = connection.prepareStatement(stmt);
            prepareStatement.execute();
        } catch (SQLException e) {
            logger.info(e.getMessage(), e);
        }
    }

    public void executeDropView(StringBuilder tableName) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            executeDropView(tableName, conn);
        } finally {
            close(conn);
        }
    }

    public void executeDropView(StringBuilder tableName, Connection connection) throws SQLException {
        if (logger.isDebugEnabled())
            logger.debug("Dropping table structure: " + tableName);
        PreparedStatement prepareStatement = connection.prepareStatement(getStatementDropView(tableName, connection).toString());
        prepareStatement.execute();
        close(prepareStatement);
    }

    public void executeCreateTable(CharSequence tableName, DataField[] structure, boolean unique) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            executeCreateTable(tableName, structure, unique, conn);
        } finally {
            close(conn);
        }
    }

    /**
     * Create a table with a index on the timed field.
     *
     * @param tableName
     * @param structure
     * @param unique     , setting this true cause the system to create a unique index on time.
     * @param connection
     * @throws SQLException
     */
    public void executeCreateTable(CharSequence tableName, DataField[] structure, boolean unique, Connection connection) throws SQLException {
        StringBuilder sql = getStatementCreateTable(tableName, structure, connection);
        if (logger.isDebugEnabled())
            logger.debug(new StringBuilder().append("The create table statement is : ").append(sql).toString());

        PreparedStatement prepareStatement = connection.prepareStatement(sql.toString());
        prepareStatement.execute();
        prepareStatement.close();
        sql = getStatementCreateIndexOnTimed(tableName, unique);
        if (logger.isDebugEnabled())
            logger.debug(new StringBuilder().append(
                    "The create index statement is : ").append(sql).toString());
        prepareStatement = connection.prepareStatement(sql.toString());
        prepareStatement.execute();

    }

    public ResultSet executeQueryWithResultSet(StringBuilder query,Connection connection) throws SQLException {
        return connection.prepareStatement(query.toString()).executeQuery();
    }

    public ResultSet executeQueryWithResultSet(AbstractQuery abstractQuery, Connection c) throws SQLException {
        if (abstractQuery.getLimitCriterion() == null) {
            return executeQueryWithResultSet(abstractQuery.getStandardQuery(), c);
        }
        String query = addLimit(abstractQuery.getStandardQuery().toString(), abstractQuery.getLimitCriterion().getSize(), abstractQuery.getLimitCriterion().getOffset());
        return executeQueryWithResultSet(new StringBuilder(query), c);
    }

    public DataEnumerator executeQuery(StringBuilder query, boolean binaryFieldsLinked, Connection connection) throws SQLException {
        if (logger.isDebugEnabled())
            logger.debug("Executing query: " + query + "( Binary Field Linked:" + binaryFieldsLinked + ")");
        return new DataEnumerator(this, connection.prepareStatement(query.toString()), binaryFieldsLinked);
    }

    /**
     * Attention: Caller should close the connection.
     *
     * @param abstractQuery
     * @param binaryFieldsLinked
     * @param connection
     * @return
     * @throws SQLException
     */
    public DataEnumerator executeQuery(AbstractQuery abstractQuery, boolean binaryFieldsLinked, Connection connection) throws SQLException {
        if (abstractQuery.getLimitCriterion() == null) {
            return executeQuery(abstractQuery.getStandardQuery(), binaryFieldsLinked, connection);
        }
        String query = addLimit(abstractQuery.getStandardQuery().toString(), abstractQuery.getLimitCriterion().getSize(), abstractQuery.getLimitCriterion().getOffset());
        if (logger.isDebugEnabled())
            logger.debug("Executing query: " + query + "(" + binaryFieldsLinked + ")");
        return new DataEnumerator(this, connection.prepareStatement(query.toString()), binaryFieldsLinked);
    }

    public DataEnumerator streamedExecuteQuery(AbstractQuery abstractQuery, boolean binaryFieldsLinked, Connection connection) throws SQLException {
        if (abstractQuery.getLimitCriterion() == null) {
            return streamedExecuteQuery(abstractQuery.getStandardQuery().toString(), binaryFieldsLinked, connection);
        }
        String query = addLimit(abstractQuery.getStandardQuery().toString(), abstractQuery.getLimitCriterion().getSize(), abstractQuery.getLimitCriterion().getOffset());
        if (logger.isDebugEnabled())
            logger.debug("Executing query: " + query + "(" + binaryFieldsLinked + ")");
        return streamedExecuteQuery(query, binaryFieldsLinked, connection);
    }

    public DataEnumerator executeQuery(StringBuilder query, boolean binaryFieldsLinked) throws SQLException {
        return executeQuery(query, binaryFieldsLinked, getConnection());
    }

    public DataEnumerator streamedExecuteQuery(String query, boolean binaryFieldsLinked, Connection conn) throws SQLException {
        return new DataEnumerator(this, conn.prepareStatement(query), binaryFieldsLinked);
    }


    public DataEnumerator streamedExecuteQuery(String query, boolean binaryFieldsLinked) throws SQLException {
        return streamedExecuteQuery(query, binaryFieldsLinked, getConnection());
    }


    public void executeCreateView(CharSequence viewName, CharSequence selectQuery) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            executeCreateView(viewName, selectQuery, connection);
        } finally {
            close(connection);
        }
    }

    public void executeCreateView(CharSequence viewName, CharSequence selectQuery, Connection connection) throws SQLException {
        StringBuilder statement = getStatementCreateView(viewName, selectQuery);
        if (logger.isDebugEnabled())
            logger.debug("Creating a view:" + statement);
        final PreparedStatement prepareStatement = connection.prepareStatement(statement.toString());
        prepareStatement.execute();
        close(prepareStatement);
    }

    /**
     * This method executes the provided statement over the connection. If there
     * is an error retruns -1 otherwise it returns the output of the
     * executeUpdate method on the PreparedStatement class which reflects the
     * number of changed rows in the underlying table.
     *
     * @param sql
     * @param connection
     * @return Number of effected rows or -1 if there is an error.
     */
    public void executeCommand(String sql, Connection connection) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException error) {
            logger.error(error.getMessage() + " FOR: " + sql, error);
        } finally {
            try {
                if (stmt != null && !stmt.isClosed())
                    stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int executeUpdate(String updateStatement, Connection connection) {
        int toReturn = -1;
        //PreparedStatement prepareStatement = null;
        try {
            //prepareStatement = connection.prepareStatement(updateStatement);
            //toReturn = prepareStatement.executeUpdate();
            toReturn = connection.createStatement().executeUpdate(updateStatement);
        } catch (SQLException error) {
            logger.error(error.getMessage(), error);
        }
        return toReturn;
    }

    public int executeUpdate(StringBuilder updateStatement, Connection connection) {
        int to_return = -1;
        to_return = executeUpdate(updateStatement.toString(), connection);
        return to_return;
    }

    public int executeUpdate(StringBuilder updateStatement) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            return executeUpdate(updateStatement, connection);
        } finally {
            close(connection);
        }
    }

    public void executeInsert(CharSequence tableName, DataField[] fields, StreamElement se) throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            executeInsert(tableName, fields, se, connection);
        } finally {
            close(connection);
        }
    }

    public void executeInsert(CharSequence tableName, DataField[] fields, StreamElement streamElement, Connection connection) throws SQLException {
        PreparedStatement ps = null;
        String query = getStatementInsert(tableName, fields).toString();
        try {
            ps = connection.prepareStatement(query);
            int counter = 1;
            for (DataField dataField : fields) {
                if (dataField.getName().equalsIgnoreCase("timed"))
                    continue;
                Serializable value = streamElement.getData(dataField.getName());

                switch (dataField.getDataTypeID()) {
                    case DataTypes.VARCHAR:
                        if (value == null)
                            ps.setNull(counter, Types.VARCHAR);
                        else
                            ps.setString(counter, value.toString());
                        break;
                    case DataTypes.CHAR:
                        if (value == null)
                            ps.setNull(counter, Types.CHAR);
                        else
                            ps.setString(counter, value.toString());
                        break;
                    case DataTypes.INTEGER:
                        if (value == null)
                            ps.setNull(counter, Types.INTEGER);
                        else
                            ps.setInt(counter, ((Number) value).intValue());
                        break;
                    case DataTypes.SMALLINT:
                        if (value == null)
                            ps.setNull(counter, Types.SMALLINT);
                        else
                            ps.setShort(counter, ((Number) value).shortValue());
                        break;
                    case DataTypes.TINYINT:
                        if (value == null)
                            ps.setNull(counter, Types.TINYINT);
                        else
                            ps.setByte(counter, ((Number) value).byteValue());
                        break;
                    case DataTypes.DOUBLE:
                        if (value == null)
                            ps.setNull(counter, Types.DOUBLE);
                        else
                            ps.setDouble(counter, ((Number) value).doubleValue());
                        break;
                    case DataTypes.BIGINT:
                        if (value == null)
                            ps.setNull(counter, Types.BIGINT);
                        else
                            ps.setLong(counter, ((Number) value).longValue());
                        break;
                    case DataTypes.BINARY:
                        if (value == null)
                            ps.setNull(counter, Types.BINARY);
                        else
                            ps.setBytes(counter, (byte[]) value);
                        break;
                    default:
                        logger.error("The type conversion is not supported for : "
                                + dataField.getName() + "("
                                + dataField.getDataTypeID() + ") : ");
                }
                counter++;
            }
            ps.setLong(counter, streamElement.getTimeStamp());
            ps.execute();
        } catch (GSNRuntimeException e) {
            //if (e.getType() == GSNRuntimeException.UNEXPECTED_VIRTUAL_SENSOR_REMOVAL) {
            //    if (logger.isDebugEnabled())
            //        logger.debug("An stream element dropped due to unexpected virtual sensor removal. (Stream element: " + streamElement.toString() + ")+ Query: " + query, e);
            //} else
                logger.warn("Inserting a stream element failed : "
                        + streamElement.toString(), e);
        } catch (SQLException e) {
            if (e.getMessage().toLowerCase().contains("duplicate entry"))
                logger.info("Error occurred on inserting data to the database, an stream element dropped due to: " + e.getMessage() + ". (Stream element: " + streamElement.toString() + ")+ Query: " + query);
            else
                logger.warn("Error occurred on inserting data to the database, an stream element dropped due to: " + e.getMessage() + ". (Stream element: " + streamElement.toString() + ")+ Query: " + query);
            throw e;
        }
        finally {
            close(ps);
        }
    }

    /***************************************************************************
     * Statement Generators
     **************************************************************************/
    /**
     * Creates a sql statement which can be used for inserting the specified
     * stream element in to the specified table.
     *
     * @param tableName The table which the generated sql will pointing to.
     * @param fields    The stream element for which the sql statement is generated.
     * @return A sql statement which can be used for inserting the provided
     *         stream element into the specified table.
     */
    public StringBuilder getStatementInsert(CharSequence tableName, DataField fields[]) {
        StringBuilder toReturn = new StringBuilder("insert into ").append(tableName).append(" ( ");
        int numberOfQuestionMarks = 1; //Timed is always there.
        for (DataField dataField : fields) {
            if (dataField.getName().equalsIgnoreCase("timed"))
                continue;
            numberOfQuestionMarks++;
            toReturn.append(dataField.getName()).append(" ,");
        }
        toReturn.append(" timed ").append(" ) values (");
        for (int i = 1; i <= numberOfQuestionMarks; i++)
            toReturn.append("?,");
        toReturn.deleteCharAt(toReturn.length() - 1);
        toReturn.append(")");
        return toReturn;
    }

    public String getStatementRenameTable(String oldName, String newName) {
        return new StringBuilder("alter table ").append(oldName).append(" rename to ").append(newName).toString();
    }

    public abstract StringBuilder getStatementDropTable(CharSequence tableName, Connection conn) throws SQLException ;

    /**
     * First detects the appropriate DB Engine to use. Get's the drop index
     * statement syntax (which is DB dependent) and executes it.
     *
     * @param indexName
     * @param connection
     * @return
     * @throws SQLException
     */
    public StringBuilder getStatementDropIndex(CharSequence indexName, CharSequence tableName,Connection connection) throws SQLException {
       return new StringBuilder(getStatementDropIndex().replace("#NAME",indexName).replace("#TABLE", tableName));
    }

    public StringBuilder getStatementDropView(CharSequence viewName,Connection connection) throws SQLException {
        return new StringBuilder(getStatementDropView().replace("#NAME",viewName));
    }

    public StringBuilder getStatementCreateIndexOnTimed(
            CharSequence tableName, boolean unique) throws SQLException {
        StringBuilder toReturn = new StringBuilder("CREATE ");
        if (unique)
            toReturn.append(" UNIQUE ");
        toReturn.append(" INDEX ").append(tableNamePostFixAppender(tableName, "_INDEX")).append(" ON ").append(tableName).append(" (timed DESC)");
        return toReturn;
    }

    public StringBuilder getStatementCreateTable(CharSequence tableName, DataField[] structure, Connection connection) throws SQLException {
        return getStatementCreateTable(tableName.toString(), structure);
    }

    public abstract StringBuilder getStatementCreateTable(String tableName, DataField[] structure);

    public StringBuilder getStatementCreateView(CharSequence viewName,CharSequence selectQuery) {
        return new StringBuilder("create view ").append(viewName).append(" AS ( ").append(selectQuery).append(" ) ");
    }

    private String driver = null;


    /**
     * The prefix is in lower case
     *
     * @return
     */
    public abstract String getJDBCPrefix();

    public String getJDBCDriverClass() {
        return databaseDriver;
    }

    /*
    * Converts from internal GSN data types to a supported DB data type.
    * @param field The DataField to be converted @return convertedType The
    * datatype name used by the target database.
    */
    public abstract String convertGSNTypeToLocalType(DataField gsnType);


    /**
     * Obtains the default database connection.
     * The conneciton comes from the data source which is configured through gsn.xml file.
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        if (logger.isDebugEnabled())
            logger.debug(new StringBuilder("Asking a con. to DB: ")
                    .append(pool.getUrl())
                    .append(" => busy: ")
                    .append(pool.getNumActive())
                    .append(", max-size: ")
                    .append(pool.getMaxActive())
                    .append(", idle: ")
                    .append(pool.getNumIdle()));
        return pool.getConnection();
    }


    /**
     * Retruns an approximation of the difference between the current time of the DB and that of the local system
     *
     * @return
     */
    public long getTimeDifferenceInMillis() {
        String query = getStatementDifferenceTimeInMillis();
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement prepareStatement = connection.prepareStatement(query);
            long time1 = System.currentTimeMillis();
            ResultSet resultSet;
            resultSet = prepareStatement.executeQuery();
            resultSet.next();
            long time2 = System.currentTimeMillis();
            return resultSet.getLong(1) - time2 + (time2 - time1) / 2;
        } catch (SQLException error) {
            logger.error(error.getMessage(), error);
        } finally {
            close(connection);
        }
        return 0;
    }

    public abstract String getStatementDifferenceTimeInMillis();

    public ArrayList<String> getInternalTables() throws SQLException {
        return new ArrayList<String>();
    }

    //

    public String randomTableNameGenerator ( int length ) {
		byte oneCharacter;
		StringBuffer result = new StringBuffer ( length );
		for ( int i = 0 ; i < length ; i++ ) {
			oneCharacter = ( byte ) ( ( Math.random ( ) * ( 'z' - 'a' + 1 ) ) + 'a' );
			result.append ( ( char ) oneCharacter );
		}
		return result.toString ( );
	}

	public int tableNameGenerator ( ) {
		return randomTableNameGenerator ( 15 ).hashCode ( );
	}

    public StringBuilder tableNameGeneratorInString (CharSequence tableName) {
		return new StringBuilder(tableName);
	}

	public StringBuilder tableNameGeneratorInString (int code) {
		StringBuilder sb = new StringBuilder ("_");
		if (code<0)
			sb.append ( "_" );
		sb.append ( Math.abs (code) );
		return tableNameGeneratorInString(sb);
	}

    public String tableNamePostFixAppender(CharSequence table_name,String postFix) {
        String tableName = table_name.toString();
		if (tableName.endsWith("\""))
			return (tableName.substring(0, tableName.length()-2))+postFix+"\"";
		else
			return tableName+postFix;
    }

    // deprecated
    protected boolean isH2 ;
    protected boolean isMysql ;
    protected boolean isOracle ;
    protected boolean isSqlServer ;
    protected boolean isPostgres ;
    public boolean isOracle(){ return isOracle; }
    public boolean isH2(){ return isH2; }
    public boolean isSqlServer(){ return isSqlServer; }
    public boolean isMysqlDB(){ return isMysql; }
    public boolean isPostgres(){ return isPostgres; }



}
