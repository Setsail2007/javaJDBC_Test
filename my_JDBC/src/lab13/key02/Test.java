package lab13.key02;

import JDBCTest5.DBHelper;

public class Test {
    public static void main(String[] args){
       DBHepler dbHepler=new DBHepler();
        String sql="Insert into  student (sno,sname,gender,age,major,dormitory) values " +
                "('2025213231','张三','男',19,'数据科学与大数据技术','科创楼101'),"+
                "('2025213232','李四','女',20,'数据科学与大数据技术','科创楼102')";
        dbHepler.update(sql);


    }
}
