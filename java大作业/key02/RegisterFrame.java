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

public class RegisterFrame extends JFrame {
    private JTextField txt_username;
    private JPasswordField txt_pwd;
    private JButton btn_reg, btn_back;

    public RegisterFrame() {
        setTitle("用户注册");
        setLayout(new GridLayout(3, 1, 5, 5));
        setSize(300, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 用户名
        JPanel p1 = new JPanel();
        p1.add(new JLabel("注册用户名："));
        txt_username = new JTextField(12);
        p1.add(txt_username);

        // 密码
        JPanel p2 = new JPanel();
        p2.add(new JLabel("设置密码(仅数字)："));
        txt_pwd = new JPasswordField(12);
        txt_pwd.setEchoChar('*');
        // 密码只能输入数字
        txt_pwd.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char ch = e.getKeyChar();
                if (!Character.isDigit(ch)) {
                    txt_pwd.setText("");
                    txt_pwd.requestFocus();
                }
            }
        });
        p2.add(txt_pwd);

        // 按钮
        JPanel p3 = new JPanel();
        btn_reg = new JButton("立即注册");
        btn_back = new JButton("返回登录");

        // 注册事件
        btn_reg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txt_username.getText().trim();
                String pwd = new String(txt_pwd.getPassword()).trim();

                // 非空校验
                if (username.isEmpty() || pwd.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "用户名和密码不能为空！");
                    return;
                }

                try {
                    // 先查询用户名是否已存在
                    String checkSql = "SELECT * FROM `user` WHERE user = ?";
                    PreparedStatement checkPst = DBHepler.openPreparedStatement(checkSql);
                    checkPst.setString(1, username);
                    ResultSet rs = checkPst.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, "该用户名已被注册，请更换！");
                        return;
                    }

                    // 插入新用户
                    String insertSql = "INSERT INTO `user`(user, password) VALUES (?, ?)";
                    PreparedStatement insertPst = DBHepler.openPreparedStatement(insertSql);
                    insertPst.setString(1, username);
                    insertPst.setString(2, pwd);
                    int row = insertPst.executeUpdate();

                    if (row > 0) {
                        JOptionPane.showMessageDialog(null, "注册成功！请去登录");
                        // 注册成功返回登录页
                        new LoginFrame().setVisible(true);
                        RegisterFrame.this.dispose();
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "注册失败：" + ex.getMessage());
                }
            }
        });

        // 返回登录
        btn_back.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });

        p3.add(btn_reg);
        p3.add(btn_back);

        add(p1);
        add(p2);
        add(p3);
        setVisible(true);
    }
}