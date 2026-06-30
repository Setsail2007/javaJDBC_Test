package labMAX.key02;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JLabel lb1, lb2;
    private JTextField txt_userid;
    private JPasswordField txt_password;
    private JButton btn_submint, btn_reset, btn_register; // 新增注册按钮
    private JCheckBox cb_admin;

    public LoginFrame() {
        this.setTitle("用户登录窗口");
        this.setLayout(new GridLayout(4, 1));

        // 用户名面板
        JPanel panel1 = new JPanel();
        lb1 = new JLabel("用户名：");
        txt_userid = new JTextField(12);
        panel1.add(lb1);
        panel1.add(txt_userid);

        // 密码面板
        JPanel panel2 = new JPanel();
        lb2 = new JLabel("密 码：");
        txt_password = new JPasswordField(12);
        txt_password.setEchoChar('*');
        txt_password.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char key = e.getKeyChar();
                if (!Character.isDigit(key)) {
                    txt_password.setText("");
                    txt_password.requestFocusInWindow();
                }
            }
        });
        panel2.add(lb2);
        panel2.add(txt_password);

        // 管理员复选框
        JPanel panelCheck = new JPanel();
        cb_admin = new JCheckBox("是否是管理员");
        panelCheck.add(cb_admin);

        // 按钮面板：提交、重置、注册
        JPanel panel3 = new JPanel();
        btn_submint = new JButton("提交");
        btn_reset = new JButton("重置");
        btn_register = new JButton("用户注册"); // 注册按钮

        // 登录提交事件
        btn_submint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txt_userid.getText().trim();
                String pwd = new String(txt_password.getPassword()).trim();
                int i = cb_admin.isSelected() ? 1 : 0;
                String sql;
                if (i == 0) {
                    sql = "select * from `user` where user = ? and password = ? ";
                } else {
                    sql = "select * from `manager` where manageruser = ? and password = ? ";
                }
                try {
                    PreparedStatement prest = DBHepler.openPreparedStatement(sql);
                    prest.setString(1, username);
                    prest.setString(2, pwd);
                    ResultSet rs = prest.executeQuery();
                    if (rs.next()) {
                        if (i == 0) {
                            new UserFrame(username);
                        } else {
                            new AdminFrame(username);
                        }
                        LoginFrame.this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this, "用户名密码不正确！");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "数据库异常：" + ex.getMessage());
                }
            }
        });

        // 重置按钮
        btn_reset.addActionListener(e -> {
            txt_userid.setText("");
            txt_password.setText("");
        });

        // 注册按钮：打开注册窗口，关闭当前登录窗口
        btn_register.addActionListener(e -> {
            new RegisterFrame();
            this.dispose();
        });

        panel3.add(btn_submint);
        panel3.add(btn_reset);
        panel3.add(btn_register);

        this.add(panel1);
        this.add(panel2);
        this.add(panelCheck);
        this.add(panel3);

        pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(320, 260);
        setLocationRelativeTo(null); // 窗口居中
    }

    public static void main(String args[]) {
        RentalDAO.createTable();
        new LoginFrame().setVisible(true);
    }
}