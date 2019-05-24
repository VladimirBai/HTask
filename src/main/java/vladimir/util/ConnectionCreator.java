package vladimir.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionCreator {

    private static Connection connection;

    public static Connection getConnection() {
        try {
            connection =  DriverManager.getConnection("jdbc:hsqldb:file:taskdb/db;shutdown=true");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

}
