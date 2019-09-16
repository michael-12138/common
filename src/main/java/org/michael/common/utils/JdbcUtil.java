package org.michael.common.utils;

import org.michael.common.JdbcConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created on 2019-09-16 11:30
 * Author : Michael.
 */
public class JdbcUtil {

    public static void executeSQLWithRetry(JdbcConnectionPool pool, String sql, int retry) throws Exception {
        Exception ex = null;
        while (retry > 0) {
            retry--;
            try {
                executeSQL(pool, sql);
                return;
            } catch (Exception e) {
                ex = e;
            }
        }
        throw ex;
    }

    public static void executeSQL(JdbcConnectionPool pool, String sql) throws Exception {
        boolean err = false;
        Connection conn = null;
        try {
            conn = pool.acquire();
            executeSQL(conn, sql);
        } catch (Exception e) {
            err = true;
            throw e;
        } finally {
            pool.release(conn, err);
        }
    }

    public static void executeSQL(Connection conn, String sql) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            IOUtil.closeQuietely(pstmt);
        }
    }
}
