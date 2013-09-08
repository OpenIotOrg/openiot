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
import org.openiot.gsn.storage.SQLUtils;
import org.openiot.gsn.storage.StorageManager;
import org.apache.log4j.Logger;

import java.sql.*;

public class OracleStorageManager extends StorageManager {

    private static final transient Logger logger = Logger.getLogger(OracleStorageManager.class);

    public OracleStorageManager() {
        super();
        this.isOracle = true;
    }

    @Override
    public String getJDBCPrefix() {
        return "jdbc:oracle:thin:";
    }

    @Override
    public String convertGSNTypeToLocalType(DataField gsnType) {
        String convertedType = null;
        switch (gsnType.getDataTypeID()) {
            case DataTypes.BIGINT:
            case DataTypes.SMALLINT:
            case DataTypes.INTEGER:
            case DataTypes.TINYINT:
                convertedType = "number(38,0)";
                break;
            case DataTypes.DOUBLE:
                convertedType = "number(38,16)";
                break;
            case DataTypes.CHAR:
            case DataTypes.VARCHAR:
                // Because the parameter for the varchar is not
                // optional.
                convertedType = gsnType.getType();
                convertedType = convertedType.toLowerCase().replace("varchar", "varchar2");
                break;
            case DataTypes.BINARY:
                convertedType = "LONG RAW";
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
            case Types.NUMERIC:
                if (precision == 0)
                    return DataTypes.BIGINT;
                else
                    return DataTypes.DOUBLE;
            case Types.VARCHAR:
                return DataTypes.VARCHAR;
            case Types.CHAR:
                return DataTypes.CHAR;
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
        return "DROP INDEX #NAME ON #TABLE";
    }

    @Override
    public String getStatementDropView() {
        return "DROP VIEW #NAME";
    }

    @Override
    public int getTableNotExistsErrNo() {
        return 208; //java.sql.SQLException: Invalid object name
    }

    @Override
    public String addLimit(String query, int limit, int offset) {
        String toAppend = "";
        if (offset == 0)
            toAppend = " ROWNUM <= " + limit;
        else
            toAppend = " ROWNUM BETWEEN " + offset + " AND " + (limit + offset) + " ";

        int indexOfWhere = SQLUtils.getWhereIndex(query);
        int indexOfGroupBy = SQLUtils.getGroupByIndex(query);
        int indexOfOrder = SQLUtils.getOrderByIndex(query);

        StringBuilder toReturn = new StringBuilder(query);
        if (indexOfGroupBy < 0 && indexOfWhere < 0 && indexOfOrder < 0)
            return query + " WHERE " + toAppend;
        if (indexOfWhere < 0 && indexOfOrder > 0)
            return toReturn.insert(indexOfOrder, " WHERE " + toAppend).toString();
        if (indexOfWhere < 0 && indexOfGroupBy > 0)
            return toReturn.insert(indexOfGroupBy, " WHERE " + toAppend).toString();
        if (indexOfWhere > 0) {
            StringBuilder tmp = toReturn.insert(indexOfWhere + " WHERE ".length(), toAppend + " AND (");
            int endIndex = tmp.length();
            if (indexOfGroupBy > 0)
                endIndex = SQLUtils.getGroupByIndex(tmp);
            else if (indexOfOrder > 0)
                endIndex = SQLUtils.getOrderByIndex(tmp);
            tmp.insert(endIndex, ")");
            return tmp.toString();
        }
        return query + " LIMIT " + limit + " OFFSET " + offset;
    }

    @Override
    public String getStatementDifferenceTimeInMillis() {
        return "";
    }

    @Override
    public StringBuilder getStatementDropTable(CharSequence tableName, Connection conn) throws SQLException {
        StringBuilder sb = new StringBuilder("Drop table ");
        sb.append(tableName);
        return sb;
    }

    @Override
    public StringBuilder getStatementCreateTable(String tableName, DataField[] structure) {
        StringBuilder result = new StringBuilder("CREATE TABLE ").append(tableName);
        result.append(" (PK number(38) PRIMARY KEY, timed number(38) NOT NULL, ");
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
                .append(" where timed <= ( SELECT * FROM ( SELECT timed FROM ")
                .append(virtualSensorName)
                .append(" group by timed ORDER BY timed DESC) where rownum = ")
                .append(storageSize + 1)
                .append(" )");
    }

    //

    @Override
    public void executeDropTable(CharSequence tableName, Connection connection) {
        PreparedStatement prepareStatement = null;
        try {
            super.executeDropTable(tableName, connection);
            executeCommand("drop sequence " + tableNamePostFixAppender(tableName, "_SEQ"), connection);
            executeCommand("drop trigger " + tableNamePostFixAppender(tableName, "_TRIG"), connection);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        }
    }

    @Override
    public void executeCommand(String sql, Connection connection) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException error) {
            if ((sql.toLowerCase().contains("drop trigger") && error.getMessage().contains("does not exist")) ||
                    (sql.toLowerCase().contains("create sequence") && error.getMessage().contains("name is already used")))
                // ignore it for oracle
                ;
            else
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

    @Override
    public void executeCreateTable(CharSequence tableName, DataField[] structure, boolean unique, Connection connection) throws SQLException {
        StringBuilder sql = getStatementCreateTable(tableName, structure, connection);
        if (logger.isDebugEnabled())
            logger.debug(new StringBuilder().append("The create table statement is : ").append(sql).toString());

        PreparedStatement prepareStatement = connection.prepareStatement(sql.toString());
        prepareStatement.execute();
        prepareStatement.close();

        // need to make a sequence and trigger.
        String oracleSeq = "create sequence " + tableNamePostFixAppender(tableName, "_SEQ");
        String oracleTrigger = "create or replace trigger " + tableNamePostFixAppender(tableName, "_TRIG") + " before insert on " + tableName + " for each row begin select " + tableNamePostFixAppender(tableName, "_SEQ") + ".nextval into :NEW.pk from dual; end;";
        logger.debug(oracleSeq);
        logger.debug(oracleTrigger);
        executeCommand(oracleSeq, connection);
        executeCommand(oracleTrigger, connection);

        sql = getStatementCreateIndexOnTimed(tableName, unique);
        if (logger.isDebugEnabled())
            logger.debug(new StringBuilder().append(
                    "The create index statement is : ").append(sql).toString());
        prepareStatement = connection.prepareStatement(sql.toString());
        prepareStatement.execute();

    }

    @Override
    public StringBuilder getStatementRemoveUselessDataCountBased(String virtualSensorName, long storageSize) {
        return new StringBuilder()
                .append("delete from ")
                .append(virtualSensorName)
                .append(" where timed <= ( SELECT * FROM ( SELECT timed FROM ")
                .append(virtualSensorName)
                .append(" group by timed ORDER BY timed DESC) where rownum = ")
                .append(storageSize + 1)
                .append(" )");
    }

    // private

    /**
	 * This method is used ONLY for ORACLE DB.
	 * ADDS the postfix at the end of the tableName. If the table name ends with " then
	 * updates it properly.
	 * @param table_name
	 * @return
	 */
    @Override
	public String tableNamePostFixAppender(CharSequence table_name,String postFix) {
		String tableName = table_name.toString();
		if (tableName.endsWith("\""))
			return (tableName.substring(0, tableName.length()-2))+postFix+"\"";
		else
			return tableName+postFix;
	}

    @Override
    public StringBuilder tableNameGeneratorInString (CharSequence tableName) {
		if (tableName.charAt(0)=='_')
			return new StringBuilder( "\"").append(tableName).append("\"");
		return new StringBuilder(tableName);
	}
}
