package lab13.key02;

import java.sql.*;

public class DBHepler implements DBConfig{

    static Connection conn=null;
    static Statement statement=null;
    static ResultSet rs=null;

    //静态的getConnection()方法，该方法返回Connection类型的对象
    public static Connection getConnection(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn= DriverManager.getConnection(DBConfig.url,  DBConfig.user,DBConfig.password);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        return  conn;
    }
    //静态openStatement()方法，该方法返回值为Statement类型的对象
    public static Statement openStatement(){
        conn=getConnection();
        try {
            statement=conn.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return statement;
    }
    //静态update(String sql)方法，该方法返回值为int型，用于完成数据的增（insert）、删（delete）、改（update）
    public static int update(String sql){
        statement=openStatement();
        int i= 0;
        try {
            i = statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return i;
    }
    //静态query(String sql)方法，该方法返回值为ResultSet类型的对象，用于完成数据的查询（select）
    public static ResultSet query(String sql){
        statement=openStatement();
        try {
            rs = statement.executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rs;
    }
    //静态Close()方法，无返回值，用于释放各种资源。
    public static void Close(){
        try {
            if(rs!=null ) {
                rs.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        try {
            if(statement!=null ) {
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        try {
            if(conn!=null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
