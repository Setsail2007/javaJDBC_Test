package JDBCTest5;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcTest2 {
    public static void main(String[] args) {
        DBHelper dbHelper = new DBHelper();
        int result = dbHelper.undate("Insert into student (sno,sname,gender,age,major,dormitory) values" +
                " ('2025121231','邓俊航','男',19,'数据科学与大数据技术','A2 616') ," +
                "('2025121232','李四','女',19,'数据科学与大数据技术','科创楼8888')");

        if (result > 0) {
            System.out.println("数据库插入记录成功！");
        } else {
            System.out.println("数据库插入记录失败！");
        }

        ResultSet rs = DBHelper.query("select sname,major from student where sname like '邓%'");
        try {
            System.out.println("===== 姓邓的同学 =====");
            while (rs.next()) {
                String sname = rs.getString("sname");
                String major = rs.getString("major");
                System.out.println("姓名: " + sname + "，专业: " + major);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbHelper.close(dbHelper.conn, dbHelper.statement, rs);
        }
    }

}

