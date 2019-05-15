package org.donglight.watchdog.server.persistence;

import org.springframework.beans.factory.annotation.Value;

import java.sql.*;
import java.util.LinkedList;


/**
 * jdbc 工具类
 *
 * @author donglight
 * @date 2019/5/13
 * @since 1.0.0
 */
public class JdbcUtil {

    private static final LinkedList<Connection> dataSource = new LinkedList<>();

    private static final ThreadLocal<Connection> tl = new ThreadLocal<>();


    @Value("${spring.datasource.driver-class-name:com.mysql.jdbc.Driver}")
    private String driverClass;

    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/watchdog?characterEncoding=utf-8&useSSL=false}")
    private String url;

    @Value("${spring.datasource.username:root}")
    private String user;

    @Value("${spring.datasource.password:123456}")
    private String password;

    public Connection getConnection() {
        Connection connection = tl.get();
        if (connection == null) {
            if (dataSource.size() == 0) {
                for (int i = 0; i < 5; i++) {
                    try {
                        dataSource.add(DriverManager.getConnection(url, user, password));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            connection = dataSource.removeFirst();
            tl.set(connection);
        }
        return connection;
    }

    public void closeResource(Connection conn, PreparedStatement st, ResultSet rs) {
        try {
            closeConnection(conn);
            closeStatement(st);
            closeResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int executeUpdate(String sql, Object... params) throws SQLException {
        Connection connection = getConnection();
        int i = setParams(connection, sql, params).executeUpdate();
        closeConnection(connection);
        return i;
    }

    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        Connection connection = getConnection();
        ResultSet resultSet = setParams(connection, sql, params).executeQuery();
        closeConnection(connection);
        return resultSet;
    }

    private PreparedStatement setParams(Connection connection, String sql, Object... params) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
        return preparedStatement;
    }

    private void closeResultSet(ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
    }

    private void closeStatement(PreparedStatement st) throws SQLException {
        if (st != null) {
            st.close();
        }
    }

    private static void closeConnection(Connection conn) throws SQLException {
        dataSource.add(conn);
        tl.remove();
    }

}
