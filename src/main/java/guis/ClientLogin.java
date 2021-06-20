package guis;

import myClasses.ListUser;
import myClasses.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientLogin extends JFrame {
    private JPanel mainPanel;
    private JTabbedPane loginPane;
    private JPanel loginPanel;
    private JTextField usernameTextfield;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JPanel registerPanel;
    private JLabel usernameRegLabel;
    private JLabel passwordRegLabel;
    private JTextField usernameReg;
    private JPasswordField passwordReg;
    private JPasswordField repasswordReg;
    private JButton registerButton;
    private ListUser listUser = new ListUser();

    public ClientLogin() {
        this.setTitle("Login");
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500,300);


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameTextfield.getText();
                char[] temp = passwordField.getPassword();
                String password = new String(temp);

                if (username.length() <= 0 || password.length() <= 0) {
                    JOptionPane.showMessageDialog(getFrame(), "Enter username and password");
                    return;
                }

                for (User user : listUser.list) {
                    if (username.equals(user.getUsername()) &&
                    password.equals(user.getPassword())) {
                        JOptionPane.showMessageDialog(getFrame(), "Login successfully");

                        ServerSelector serverSelector = new ServerSelector(username);
                        serverSelector.setVisible(true);

                        getFrame().dispose();
                        return;
                    }
                }
                JOptionPane.showMessageDialog(getFrame(), "Wrong username or password");
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameReg.getText();
                char[] temp = passwordReg.getPassword();
                String password = new String(temp);
                char[] anotherTemp = repasswordReg.getPassword();
                String repassword = new String(anotherTemp);

                if (username.length() <= 0 || password.length() <= 0 || repassword.length() <= 0) {
                    JOptionPane.showMessageDialog(getFrame(), "Enter username and password");
                    return;
                }

                for (User user : listUser.list) {
                    if (username.equals(user.getUsername())) {
                        JOptionPane.showMessageDialog(getFrame(), "Username is not available!");
                        return;
                    }
                }
                if (!password.equals(repassword)) {
                    JOptionPane.showMessageDialog(getFrame(), "Passwords are not matched!");
                    return;
                }
                User user = new User(username, password);
                listUser.list.add(user);
                listUser.saveList();
                JOptionPane.showMessageDialog(getFrame(), "Register successfully! Please login from the login tab");
                usernameReg.setText("");
                passwordReg.setText("");
                repasswordReg.setText("");
            }
        });
    }

    public JFrame getFrame() {
        return this;
    }
}
