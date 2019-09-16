package org.michael.common;

import java.sql.Connection;

/**
 * Created on 2019-09-16 11:21
 * Author : Michael.
 */
public class JdbcConnectionPool extends ObjectPool<Connection> {

    public JdbcConnectionPool(int coreSize, String url, String username, String password) {
        super(coreSize, initFacotry(url, username, password));
    }

    private static JdbcFactory initFacotry(String url, String username, String password) {
        JdbcConfig jdbcConfig = new JdbcConfig(url, username, password);
        JdbcFactory factory = new JdbcFactory(jdbcConfig);
        return factory;
    }
}
