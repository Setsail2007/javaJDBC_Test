package JDBCTest5;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

    public interface DBConfig {
        //链接数据库的信息
        String DRIVER = "com.mysql.jdbc.Driver";
        String URL = "jdbc:mysql://localhost:3306/db_mysise?useSSL=false&serverTimezone=UTC";
        String USER = "root";
        String PASSWORD = "root";

    }


