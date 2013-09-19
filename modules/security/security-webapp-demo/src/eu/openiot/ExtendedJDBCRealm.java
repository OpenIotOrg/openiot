package eu.openiot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.util.JdbcUtils;

public class ExtendedJDBCRealm extends JdbcRealm {

	public DataSource getDataSource() {
		return dataSource;
	}

	public Map<String, String> getAllPermissions() throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String, String> permissions = new HashMap<String, String>();
		Connection conn = dataSource.getConnection();
		try {
			ps = conn.prepareStatement("SELECT * from PERMISSIONS");

			rs = ps.executeQuery();

			while (rs.next()) {

				String name = rs.getString(1);
				String description = rs.getString(2);
				permissions.put(name, description);
			}

		} finally {
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(ps);
		}
		return permissions;
	}
}
