import java.sql.*;

public class JDBCTest4 {
    // 6)若是执行查询语句，还要从ResultSet读取数据；
// 7)关闭ResultSet、Statement、Connection等。
     public static void main(String[] args){
         Connection conn=null;
         Statement statement=null;
         ResultSet rs=null;
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
             String sql="select * from student where age<=21 limit 5";
             //执行SQl语句
             rs= statement.executeQuery(sql);
             //6)若是执行查询语句，还要从ResultSet读取数据；
             System.out.println("序号"+"\t\t学号"+"\t\t姓名"+"\t\t\t性别"+"\t\t年龄"+"\t\t\t专业"+"\t\t\t宿舍");
             while (rs.next()){
                 System.out.print(rs.getInt("sid")+"\t\t");
                 System.out.print(rs.getString("sno")+"\t\t");
                 System.out.print(rs.getString("sname")+"\t\t");
                 System.out.print(rs.getString("gender")+"\t\t");
                 System.out.print(rs.getInt("age")+"\t\t");
                 System.out.print(rs.getString("major")+"\t\t");
                 System.out.print(rs.getString("dormitory")+"\t\t");
                 System.out.println();
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
