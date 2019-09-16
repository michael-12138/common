package org.michael.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created on 2019-09-16 11:21
 * Author : Michael.
 */
public class JdbcFactory implements ObjectPool.ObjectFactory<Connection> {

    private final JdbcConfig jdbcConfig;

    private Class<?> dirver;

    public JdbcFactory(JdbcConfig jdbcConfig) {
        this.jdbcConfig = jdbcConfig;
    }

    @Override
    public boolean isValid(Connection o) {
        if (o == null) {
            return false;
        }
        try {
            return o.isValid(2000);
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public Connection createObject() throws Exception {

        synchronized (this) {
            if (dirver == null) {
                try {
                    dirver = Class.forName(jdbcConfig.getDriverName());
                } catch (ClassNotFoundException e) {
                    throw e;
                }
            }
        }
        try {
            return DriverManager.getConnection(jdbcConfig.getUrl(), jdbcConfig.getUsername(), jdbcConfig.getPassword());
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public void releaseObject(Connection e) {
        if (e != null) {
            try {
                e.close();
            } catch (SQLException e1) {
            }
        }
    }
}
