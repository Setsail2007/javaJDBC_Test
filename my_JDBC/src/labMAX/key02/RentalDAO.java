package labMAX.key02;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class RentalDAO {

    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS rental (" +
                "id INT AUTO_INCREMENT PRIMARY KEY COMMENT '租赁记录ID', " +
                "power_id INT NOT NULL COMMENT '充电宝ID', " +
                "user_name VARCHAR(50) NOT NULL COMMENT '用户名', " +
                "start_time DATETIME NOT NULL COMMENT '借出时间', " +
                "end_time DATETIME COMMENT '归还时间', " +
                "cost DECIMAL(10,2) COMMENT '费用', " +
                "FOREIGN KEY (power_id) REFERENCES my_power(id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租赁记录表'";

        try {
            PreparedStatement pst = DBHepler.openPreparedStatement(sql);
            pst.executeUpdate();
            System.out.println("rental 表创建成功或已存在");
        } catch (SQLException e) {
            System.err.println("创建 rental 表失败：" + e.getMessage());
        } finally {
            DBHepler.Close();
        }
    }

    public static int insert(Rental rental) {
        String sql = "INSERT INTO rental (power_id, user_name, start_time) VALUES (?, ?, ?)";
        try {
            PreparedStatement pst = DBHepler.openPreparedStatement(sql);
            pst.setInt(1, rental.getPowerId());
            pst.setString(2, rental.getUserName());
            pst.setTimestamp(3, rental.getStartTime());
            return pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBHepler.Close();
        }
    }

    public static int updateReturn(int rentalId, Timestamp endTime, double cost) {
        String sql = "UPDATE rental SET end_time = ?, cost = ? WHERE id = ?";
        try {
            PreparedStatement pst = DBHepler.openPreparedStatement(sql);
            pst.setTimestamp(1, endTime);
            pst.setDouble(2, cost);
            pst.setInt(3, rentalId);
            return pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBHepler.Close();
        }
    }

    public static List<Rental> findByUserName(String userName) {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT r.id, r.power_id, p.power_bank, r.start_time, r.end_time, r.cost " +
                "FROM rental r LEFT JOIN my_power p ON r.power_id = p.id " +
                "WHERE r.user_name = ? ORDER BY r.start_time DESC";
        try {
            PreparedStatement pst = DBHepler.openPreparedStatement(sql);
            pst.setString(1, userName);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Rental rental = new Rental();
                rental.setId(rs.getInt("id"));
                rental.setPowerId(rs.getInt("power_id"));
                rental.setStartTime(rs.getTimestamp("start_time"));
                rental.setEndTime(rs.getTimestamp("end_time"));
                rental.setCost(rs.getDouble("cost"));
                rentals.add(rental);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBHepler.Close();
        }
        return rentals;
    }

    public static Rental findById(int id) {
        String sql = "SELECT * FROM rental WHERE id = ?";
        try {
            PreparedStatement pst = DBHepler.openPreparedStatement(sql);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Rental rental = new Rental();
                rental.setId(rs.getInt("id"));
                rental.setPowerId(rs.getInt("power_id"));
                rental.setUserName(rs.getString("user_name"));
                rental.setStartTime(rs.getTimestamp("start_time"));
                rental.setEndTime(rs.getTimestamp("end_time"));
                rental.setCost(rs.getDouble("cost"));
                return rental;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBHepler.Close();
        }
        return null;
    }
}

