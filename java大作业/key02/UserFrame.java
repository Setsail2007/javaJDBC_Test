package labMAX.key02;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class UserFrame extends JFrame {
    private JTable pbTable, rentalTable;
    private DefaultTableModel pbTableModel, rentalTableModel;
    private JButton btnBorrow, btnReturn, btnRefresh;
    private String userName;

    public UserFrame(String userName) {
        this.userName = userName;
        this.setTitle("用户界面 - 欢迎：" + userName);
        this.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        // ========== 充电宝查询面板 ==========
        JPanel queryPanel = new JPanel(new BorderLayout());
        String[] pbColumns = {"ID", "型号", "剩余电量(%)", "可否借出", "电池容量(mAh)"};
        pbTableModel = new DefaultTableModel(pbColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pbTable = new JTable(pbTableModel);
        JScrollPane pbScrollPane = new JScrollPane(pbTable);
        queryPanel.add(pbScrollPane, BorderLayout.CENTER);

        JLabel tipLabel = new JLabel("收费标准：1.5元/小时，不足1小时按1小时计算", SwingConstants.CENTER);
        tipLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        queryPanel.add(tipLabel, BorderLayout.NORTH);

        tabbedPane.addTab("充电宝列表", queryPanel);

        // ========== 我的租赁面板 ==========
        JPanel rentalPanel = new JPanel(new BorderLayout());
        String[] rentalColumns = {"租赁ID", "充电宝ID", "型号", "借出时间", "归还时间", "费用(元)"};
        rentalTableModel = new DefaultTableModel(rentalColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        rentalTable = new JTable(rentalTableModel);
        JScrollPane rentalScrollPane = new JScrollPane(rentalTable);
        rentalPanel.add(rentalScrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("我的租赁", rentalPanel);

        this.add(tabbedPane, BorderLayout.CENTER);

        // ========== 按钮面板 ==========
        JPanel btnPanel = new JPanel();
        btnBorrow = new JButton("借出充电宝");
        btnReturn = new JButton("归还充电宝");
        btnRefresh = new JButton("刷新");

        btnPanel.add(btnBorrow);
        btnPanel.add(btnReturn);
        btnPanel.add(btnRefresh);
        this.add(btnPanel, BorderLayout.SOUTH);

        btnBorrow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                borrowPowerBank();
            }
        });

        btnReturn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnPowerBank();
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPowerBanks();
                loadMyRentals();
            }
        });

        loadPowerBanks();
        loadMyRentals();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 450);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void loadPowerBanks() {
        pbTableModel.setRowCount(0);
        String sql = "SELECT id, power_bank, remaining_power, `if`, battery FROM my_power";
        try {
            ResultSet rs = DBHepler.query(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String model = rs.getString("power_bank");
                int power = rs.getInt("remaining_power");
                int ifValue = rs.getInt("if");
                int battery = rs.getInt("battery");
                String canBorrow = ifValue == 1 ? "✅ 可借" : "❌ 不可借";
                pbTableModel.addRow(new Object[]{id, model, power, canBorrow, battery});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "加载充电宝数据失败：" + ex.getMessage());
        }
    }

    private void loadMyRentals() {
        rentalTableModel.setRowCount(0);
        List<Rental> rentals = RentalDAO.findByUserName(userName);
        for (Rental rental : rentals) {
            String model = getModelByPowerId(rental.getPowerId());
            String startStr = rental.getStartTime() != null ? rental.getStartTime().toString().substring(0, 19) : "--";
            String endStr = rental.getEndTime() != null ? rental.getEndTime().toString().substring(0, 19) : "使用中...";
            String costStr = rental.getEndTime() != null ? String.format("%.2f", rental.getCost()) : "--";

            rentalTableModel.addRow(new Object[]{
                    rental.getId(),
                    rental.getPowerId(),
                    model,
                    startStr,
                    endStr,
                    costStr
            });
        }
    }

    private String getModelByPowerId(int powerId) {
        String sql = "SELECT power_bank FROM my_power WHERE id = ?";
        try {
            PreparedStatement pst = DBHepler.openPreparedStatement(sql);
            pst.setInt(1, powerId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString("power_bank");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "未知";
    }

    private void borrowPowerBank() {
        int selectedRow = pbTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先在充电宝列表中选择一个要借出的充电宝！");
            return;
        }

        int pbId = (int) pbTableModel.getValueAt(selectedRow, 0);
        String canBorrow = (String) pbTableModel.getValueAt(selectedRow, 3);

        if (canBorrow.contains("不可借")) {
            JOptionPane.showMessageDialog(this, "该充电宝当前不可借出（电量不足或已被借出）！");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要借出该充电宝吗？\n收费标准：1.5元/小时，不足1小时按1小时计费。",
                "确认借出", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // 1. 将充电宝 if 设为 0
            String updateSql = "UPDATE my_power SET `if` = 0 WHERE id = ? AND `if` = 1";
            PreparedStatement updatePst = DBHepler.openPreparedStatement(updateSql);
            updatePst.setInt(1, pbId);
            int rows = updatePst.executeUpdate();

            if (rows == 0) {
                JOptionPane.showMessageDialog(this, "借出失败，该充电宝可能已被他人借出！");
                loadPowerBanks();
                return;
            }

            // 2. 创建租赁记录
            Rental rental = new Rental();
            rental.setPowerId(pbId);
            rental.setUserName(userName);
            rental.setStartTime(Timestamp.valueOf(LocalDateTime.now()));

            int result = RentalDAO.insert(rental);
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "借出成功！");
                loadPowerBanks();
                loadMyRentals();
            } else {
                JOptionPane.showMessageDialog(this, "借出失败！");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "借出失败：" + ex.getMessage());
        }
    }

    private void returnPowerBank() {
        int selectedRow = rentalTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先在'我的租赁'中选择一条未归还的租赁记录！");
            return;
        }

        String endTimeStr = (String) rentalTableModel.getValueAt(selectedRow, 4);
        if (!"使用中...".equals(endTimeStr)) {
            JOptionPane.showMessageDialog(this, "该充电宝已经归还过了！");
            return;
        }

        int rentalId = (int) rentalTableModel.getValueAt(selectedRow, 0);
        int pbId = (int) rentalTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要归还该充电宝吗？系统将自动计算费用。",
                "确认归还", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // 1. 获取租赁开始时间
            Rental rental = RentalDAO.findById(rentalId);
            if (rental == null) {
                JOptionPane.showMessageDialog(this, "租赁记录不存在！");
                return;
            }

            LocalDateTime start = rental.getStartTime().toLocalDateTime();
            LocalDateTime end = LocalDateTime.now();

            // 2. 计算费用：1.5元/小时，不足1小时按1小时
            long minutes = Duration.between(start, end).toMinutes();
            long hours = (minutes + 59) / 60; // 向上取整
            if (hours == 0) hours = 1;
            double cost = hours * 1.5;

            // 3. 更新租赁记录
            int result = RentalDAO.updateReturn(rentalId, Timestamp.valueOf(end), cost);
            if (result <= 0) {
                JOptionPane.showMessageDialog(this, "更新租赁记录失败！");
                return;
            }

            // 4. 恢复充电宝状态（根据 remaining_power 决定 if）
            String queryPbSql = "SELECT remaining_power FROM my_power WHERE id = ?";
            PreparedStatement queryPbPst = DBHepler.openPreparedStatement(queryPbSql);
            queryPbPst.setInt(1, pbId);
            ResultSet pbRs = queryPbPst.executeQuery();
            if (pbRs.next()) {
                int remainingPower = pbRs.getInt("remaining_power");
                int ifValue = remainingPower >= 50 ? 1 : 0;
                String updatePbSql = "UPDATE my_power SET `if` = ? WHERE id = ?";
                PreparedStatement updatePbPst = DBHepler.openPreparedStatement(updatePbSql);
                updatePbPst.setInt(1, ifValue);
                updatePbPst.setInt(2, pbId);
                updatePbPst.executeUpdate();
            }

            JOptionPane.showMessageDialog(this,
                    String.format("归还成功！\n使用时长：%d 小时\n费用：%.2f 元", hours, cost));
            loadPowerBanks();
            loadMyRentals();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "归还失败：" + ex.getMessage());
        }
    }
}
