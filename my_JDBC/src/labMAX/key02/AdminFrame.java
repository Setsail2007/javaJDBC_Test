package labMAX.key02;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh;

    public AdminFrame(String adminName) {
        this.setTitle("管理员界面 - 欢迎：" + adminName);
        this.setLayout(new BorderLayout());

        String[] columns = {"ID", "型号", "剩余电量(%)", "可否借出", "电池容量(mAh)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnAdd = new JButton("添加充电宝");
        btnEdit = new JButton("修改充电宝");
        btnDelete = new JButton("删除充电宝");
        btnRefresh = new JButton("刷新列表");

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);
        this.add(btnPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddDialog();
            }
        });

        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(AdminFrame.this, "请先选择一条记录！");
                    return;
                }
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                String model = (String) tableModel.getValueAt(selectedRow, 1);
                int power = (int) tableModel.getValueAt(selectedRow, 2);
                int battery = (int) tableModel.getValueAt(selectedRow, 4);
                showEditDialog(id, model, power, battery);
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(AdminFrame.this, "请先选择一条记录！");
                    return;
                }
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(AdminFrame.this,
                        "确定要删除ID为 " + id + " 的充电宝吗？", "确认删除", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    deletePowerBank(id);
                }
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPowerBanks();
            }
        });

        loadPowerBanks();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700, 400);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void loadPowerBanks() {
        tableModel.setRowCount(0);
        String sql = "SELECT id, power_bank, remaining_power, `if`, battery FROM my_power";
        try {
            ResultSet rs = DBHepler.query(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String model = rs.getString("power_bank");
                int power = rs.getInt("remaining_power");
                int ifValue = rs.getInt("if");
                int battery = rs.getInt("battery");
                String canBorrow = ifValue == 1 ? "可借出" : "不可借出";
                tableModel.addRow(new Object[]{id, model, power, canBorrow, battery});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "加载数据失败：" + ex.getMessage());
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog(this, "添加充电宝", true);
        dialog.setLayout(new GridLayout(5, 2, 5, 5));

        dialog.add(new JLabel("型号："));
        JTextField txtModel = new JTextField();
        dialog.add(txtModel);

        dialog.add(new JLabel("剩余电量(%)："));
        JTextField txtPower = new JTextField();
        dialog.add(txtPower);

        dialog.add(new JLabel("电池容量(mAh)："));
        JTextField txtBattery = new JTextField();
        dialog.add(txtBattery);

        dialog.add(new JLabel("可否借出(自动计算)："));
        JLabel lblIf = new JLabel("--");
        dialog.add(lblIf);

        txtPower.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateIfLabel(txtPower, lblIf);
            }
        });

        JButton btnSave = new JButton("保存");
        JButton btnCancel = new JButton("取消");
        dialog.add(btnSave);
        dialog.add(btnCancel);

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String model = txtModel.getText().trim();
                String powerStr = txtPower.getText().trim();
                String batteryStr = txtBattery.getText().trim();
                if (model.isEmpty() || powerStr.isEmpty() || batteryStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请填写完整信息！");
                    return;
                }
                try {
                    int power = Integer.parseInt(powerStr);
                    int battery = Integer.parseInt(batteryStr);
                    int ifValue = power >= 50 ? 1 : 0;
                    String sql = "INSERT INTO my_power (power_bank, remaining_power, `if`, battery) VALUES (?, ?, ?, ?)";
                    PreparedStatement pst = DBHepler.openPreparedStatement(sql);
                    pst.setString(1, model);
                    pst.setInt(2, power);
                    pst.setInt(3, ifValue);
                    pst.setInt(4, battery);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "添加成功！");
                    dialog.dispose();
                    loadPowerBanks();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "电量和电池容量请输入数字！");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "添加失败：" + ex.getMessage());
                }
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.setSize(300, 220);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditDialog(int id, String oldModel, int oldPower, int oldBattery) {
        JDialog dialog = new JDialog(this, "修改充电宝", true);
        dialog.setLayout(new GridLayout(6, 2, 5, 5));

        dialog.add(new JLabel("ID："));
        dialog.add(new JLabel(String.valueOf(id)));

        dialog.add(new JLabel("型号："));
        JTextField txtModel = new JTextField(oldModel);
        dialog.add(txtModel);

        dialog.add(new JLabel("剩余电量(%)："));
        JTextField txtPower = new JTextField(String.valueOf(oldPower));
        dialog.add(txtPower);

        dialog.add(new JLabel("电池容量(mAh)："));
        JTextField txtBattery = new JTextField(String.valueOf(oldBattery));
        dialog.add(txtBattery);

        dialog.add(new JLabel("可否借出(自动计算)："));
        JLabel lblIf = new JLabel(oldPower >= 50 ? "可借出" : "不可借出");
        dialog.add(lblIf);

        txtPower.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateIfLabel(txtPower, lblIf);
            }
        });

        JButton btnSave = new JButton("保存");
        JButton btnCancel = new JButton("取消");
        dialog.add(btnSave);
        dialog.add(btnCancel);

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String model = txtModel.getText().trim();
                String powerStr = txtPower.getText().trim();
                String batteryStr = txtBattery.getText().trim();
                if (model.isEmpty() || powerStr.isEmpty() || batteryStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "请填写完整信息！");
                    return;
                }
                try {
                    int power = Integer.parseInt(powerStr);
                    int battery = Integer.parseInt(batteryStr);
                    int ifValue = power >= 50 ? 1 : 0;
                    String sql = "UPDATE my_power SET power_bank = ?, remaining_power = ?, `if` = ?, battery = ? WHERE id = ?";
                    PreparedStatement pst = DBHepler.openPreparedStatement(sql);
                    pst.setString(1, model);
                    pst.setInt(2, power);
                    pst.setInt(3, ifValue);
                    pst.setInt(4, battery);
                    pst.setInt(5, id);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "修改成功！");
                    dialog.dispose();
                    loadPowerBanks();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "电量和电池容量请输入数字！");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "修改失败：" + ex.getMessage());
                }
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.setSize(300, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void updateIfLabel(JTextField txtPower, JLabel lblIf) {
        try {
            int power = Integer.parseInt(txtPower.getText().trim());
            if (power >= 50) {
                lblIf.setText("可借出");
            } else {
                lblIf.setText("不可借出");
            }
        } catch (NumberFormatException ex) {
            lblIf.setText("--");
        }
    }

    private void deletePowerBank(int id) {
        String sql = "DELETE FROM my_power WHERE id = ?";
        try {
            PreparedStatement pst = DBHepler.openPreparedStatement(sql);
            pst.setInt(1, id);
            int result = pst.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "删除成功！");
                loadPowerBanks();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "删除失败：" + ex.getMessage());
        }
    }
}
