package org.michael.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Created on 2019-09-16 11:20
 * Author : Michael.
 */
public class JdbcConfig {

    private final String url;
    private final String username;
    private final String password;
    private final String driverName = "com.mysql.jdbc.Driver";

    public JdbcConfig(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDriverName() {
        return driverName;
    }

    public Connection createConnection() throws Exception {

        synchronized (this) {
            try {
                Class.forName(driverName);
            } catch (ClassNotFoundException e) {
                throw e;
            }
        }
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw e;
        }
    }

    public void executeBatchUpdate(Connection conn, List<String> sqls) throws SQLException {
        if (sqls.size() == 0) {
            return;
        }
        Statement stmt = conn.createStatement();
        Boolean autoCommit = null;
        if (stmt == null)
            stmt = conn.createStatement();

        try {
            autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            for (String sql : sqls) {
                if (sql != null) {
                    stmt.addBatch(sql);
                }
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (autoCommit != null) {
                try {
                    conn.setAutoCommit(autoCommit);
                } catch (SQLException e) {
                    throw e;
                }
            }
        }
    }
}
