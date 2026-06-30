import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCTest2 {
    // 6)若是执行查询语句，还要从ResultSet读取数据；
// 7)关闭ResultSet、Statement、Connection等。
     public static void main(String[] args){
         Connection conn=null;
         Statement statement=null;
         // 1)创建数据源；
        // 2)注册、加载特定的驱动程序；
         try {
             Class.forName("com.mysql.jdbc.Driver");
             //3)创建连接-- Connection对象；
             conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/db_mysise?useSSL=false&serverTimezone=UTC",
                     "root","root");
             if(conn != null){
                 System.out.println("连接数据库成功！");
             }else {
                 System.out.println("连接数据库不成功！");
             }
             // 4)利用Connection对象生成Statement对象；
             statement=conn.createStatement();
             // 5)利用Statement对象执行SQL语句，如查询、更新、插入、删除等；
             String sql="delete from student where sname='邓俊航'";
             //执行SQl语句
             int result= statement.executeUpdate(sql);
             if (result>0){
                 System.out.println("数据库删除录成功！");
             }else {
                 System.out.println("数据库删除记录失败！");
             }
         } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
         }
         //7)关闭ResultSet、Statement、Connection等。
         finally {
             if (statement !=null || conn!=null){
                 try {
                     statement.close();
                     conn.close();
                 } catch (SQLException e) {
                     throw new RuntimeException(e);
                 }
             }
         }
//         6)若是执行查询语句，还要从ResultSet读取数据；

     }
}
