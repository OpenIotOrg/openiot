package org.openiot.gsn.storage.db;

import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.DataTypes;
import org.openiot.gsn.storage.StorageManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class SQLServerStorageManager extends StorageManager {

    private static final transient Logger logger = Logger.getLogger(SQLServerStorageManager.class);

    public SQLServerStorageManager() {
        super();
        this.isSqlServer = true;
    }

    @Override
    public String getJDBCPrefix() {
        return "jdbc:jtds:sqlserver:";
    }

    @Override
    public String convertGSNTypeToLocalType(DataField gsnType) {
        String convertedType = null;
        switch (gsnType.getDataTypeID()) {
            case DataTypes.CHAR:
            case DataTypes.VARCHAR:
                // Because the parameter for the varchar is not
                // optional.
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
        //if (isSqlServer()) return "DROP TABLE #NAME";
        //another: return "DROP INDEX #NAME";
        return "DROP INDEX #NAME ON #TABLE";
    }

    @Override
    public String getStatementDropView() {
        // if (isSqlServer()) return "DROP VIEW #NAME";
        return "DROP VIEW #NAME";
    }

    @Override
    public int getTableNotExistsErrNo() {
        return 208; //java.sql.SQLException: Invalid object name
    }

    @Override
    public String addLimit(String query, int limit, int offset) {
        // FIXME, INCORRECT !
        return query + " LIMIT " + limit + " OFFSET " + offset;
    }

    @Override
    public String getStatementDifferenceTimeInMillis() {
        return "select convert(bigint,datediff(second,'1/1/1970',current_timestamp))*1000 ";
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
                .append(".timed < (select min(timed) from (select top ")
                .append(storageSize)
                .append(" * from ")
                .append(virtualSensorName)
                .append(" order by ")
                .append(virtualSensorName)
                .append(".timed DESC ) as x ) ");
    }

    @Override
    public StringBuilder getStatementRemoveUselessDataCountBased(String virtualSensorName, long storageSize) {
        return new StringBuilder()
                .append("delete from ")
                .append(virtualSensorName)
                .append(" where ")
                .append(virtualSensorName)
                .append(".timed < (select min(timed) from (select top ")
                .append(storageSize)
                .append(" * from ")
                .append(virtualSensorName)
                .append(" order by ")
                .append(virtualSensorName)
                .append(".timed DESC ) as x ) ");

    }


}
