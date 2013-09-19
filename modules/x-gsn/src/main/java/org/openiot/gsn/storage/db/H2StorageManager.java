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

package org.openiot.gsn.storage.db;

import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.storage.StorageManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class H2StorageManager extends StorageManager {

    private static final transient Logger logger = Logger.getLogger(H2StorageManager.class);

    public H2StorageManager() {
        super();
        this.isH2 = true;
    }

    @Override
    public String getJDBCPrefix() {
        return "jdbc:h2:";
    }

    @Override
    public String convertGSNTypeToLocalType(DataField gsnType) {
        String convertedType = null;
                switch (gsnType.getDataTypeID()) {
                    case DataTypes.CHAR:
                    case DataTypes.VARCHAR:
                        // Because the parameter for the varchar is not
                        // optional.
                    	if (gsnType.getType().trim().equalsIgnoreCase("string"))
                            convertedType = "TEXT";
                        else
                            convertedType = gsnType.getType();
                        break;
                    default:
                        convertedType = DataTypes.TYPE_NAMES[gsnType.getDataTypeID()];
                        break;
                }
                return convertedType;
    }

    @Override
    public byte convertLocalTypeToGSN(int jdbcType, int precision) {
        switch (jdbcType) {
                    case Types.BIGINT:
                        return DataTypes.BIGINT;
                    case Types.INTEGER:
                        return DataTypes.INTEGER;
                    case Types.SMALLINT:
                        return DataTypes.SMALLINT;
                    case Types.TINYINT:
                        return DataTypes.TINYINT;
                    case Types.VARCHAR:
                        return DataTypes.VARCHAR;
                    case Types.CHAR:
                        return DataTypes.CHAR;
                    case Types.DOUBLE:
                    case Types.DECIMAL:    // This is needed for doing aggregates in datadownload servlet.
                        return DataTypes.DOUBLE;
                    case Types.BINARY:
                    case Types.BLOB:
                    case Types.VARBINARY:
                    case Types.LONGVARBINARY:
                        return DataTypes.BINARY;
                    default:
                        logger.error("The type can't be converted to GSN form : " + jdbcType);
                        break;
                }
                return -100;
    }

    @Override
    public String getStatementDropIndex() {
        return "DROP INDEX #NAME";
    }

    @Override
    public String getStatementDropView() {
        return "DROP VIEW #NAME IF EXISTS";
    }

    @Override
    public int getTableNotExistsErrNo() {
        return 42102;
    }

    @Override
    public String addLimit(String query, int limit, int offset) {
        return query + " LIMIT " + limit + " OFFSET " + offset;
    }

    @Override
    public void initDatabaseAccess(Connection con) throws Exception {
        Statement stmt = con.createStatement();
        stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
        stmt.execute("CREATE ALIAS IF NOT EXISTS NOW_MILLIS FOR \"java.lang.System.currentTimeMillis\";");
        super.initDatabaseAccess(con);
    }

    @Override
    public String getStatementDifferenceTimeInMillis() {
        return "call NOW_MILLIS()";    
    }

    @Override
    public StringBuilder getStatementDropTable(CharSequence tableName, Connection conn) throws SQLException {
        StringBuilder sb = new StringBuilder("Drop table if exists ");
        sb.append(tableName);
        return sb;
    }

    @Override
    public StringBuilder getStatementCreateTable(String tableName, DataField[] structure) {
        StringBuilder result = new StringBuilder("CREATE TABLE ").append(tableName);
        result.append(" (PK BIGINT NOT NULL IDENTITY, timed BIGINT NOT NULL, ");
        for (DataField field : structure) {
            if (field.getName().equalsIgnoreCase("pk") || field.getName().equalsIgnoreCase("timed")) continue;
            result.append(field.getName().toUpperCase()).append(' ');
            result.append(convertGSNTypeToLocalType(field));
            result.append(" ,");
        }
        result.delete(result.length() - 2, result.length());
        result.append(")");
        return result;
    }

    @Override
    public StringBuilder getStatementUselessDataRemoval(String virtualSensorName, long storageSize) {
        return new StringBuilder()
                .append("delete from ")
                .append(virtualSensorName)
                .append(" where ")
                .append(virtualSensorName)
                .append(".timed not in ( select ")
                .append(virtualSensorName)
                .append(".timed from ")
                .append(virtualSensorName)
                .append(" order by ")
                .append(virtualSensorName)
                .append(".timed DESC  LIMIT  ")
                .append(storageSize)
                .append(" offset 0 )");
    }

    @Override
    public StringBuilder getStatementRemoveUselessDataCountBased(String virtualSensorName, long storageSize) {
        return new StringBuilder()
                .append("delete from ")
                .append(virtualSensorName)
                .append(" where ")
                .append(virtualSensorName)
                .append(".timed not in ( select ")
                .append(virtualSensorName)
                .append(".timed from ")
                .append(virtualSensorName)
                .append(" order by ")
                .append(virtualSensorName)
                .append(".timed DESC  LIMIT  ")
                .append(storageSize).append(" offset 0 )");

    }

    //

    /*
     * This method only works with HSQLDB database. If one doesn't close the
     * HSQLDB properly with high probability, the DB goes into instable or in
     * worst case becomes corrupted.
     */
    @Override
    public void shutdown() throws SQLException {
        getConnection().createStatement().execute("SHUTDOWN");
        logger.warn("Closing the database server (for HSqlDB) [done].");
        logger.warn("Closing the connection pool [done].");
        super.shutdown();
    }
}
