package JDBCTest5;

import java.sql.*;

public class DBHelper implements DBConfig{
    Connection conn=getConnection();
    Statement statement=openStatement(conn);

    //获取数据库连接
    static Connection getConnection(){
        Connection conn=null;
        try {
            Class.forName(DRIVER);
            conn=DriverManager.getConnection(URL,USER,PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
    //获取Statement对象
     static Statement openStatement(Connection conn){
        Statement statement=null;
        try {
            statement=conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statement;
    }
    //sql的增，删，改
    static int undate(String sql){
        Connection conn=getConnection();
        Statement statement=openStatement(conn);
        int result=0;
        try {
            result=statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    //sql的查
    static ResultSet query(String sql){
        Connection conn=getConnection();
        Statement statement=openStatement(conn);
        ResultSet resultSet=null;
        try {
            resultSet=statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    //关闭数据库连接
    static void close(Connection conn, Statement statement, ResultSet resultSet){
        if(resultSet!=null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(statement!=null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(conn!=null){
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}


}
