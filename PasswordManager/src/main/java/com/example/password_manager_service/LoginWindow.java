package com.example.password_manager_service;

import com.example.password_manager_service.config.KeyGeneratorUtil;
import com.example.password_manager_service.config.SaltStorage;
import com.example.password_manager_service.service.UserService;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class LoginWindow extends JFrame {

    public LoginWindow() {
        setTitle("Login");
        setSize(400, 200);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTextField userField = new JTextField(20);
        JPasswordField passField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register New User");

        add(new JLabel("Username:"));
        add(userField);
        add(new JLabel("Master Password:"));
        add(passField);
        add(loginButton);
        add(registerButton);

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            char[] password = passField.getPassword();
            try {
                UserService userService = new UserService();
                if (userService.authenticate(username, password)) {
                    byte[] salt = userService.loadSaltForUser(username);
                    SecretKeySpec key = KeyGeneratorUtil.deriveKey(password, salt);
                    Arrays.fill(password, '0'); // Clear sensitive data

                    IvParameterSpec iv;
                    try {
                        iv = new IvParameterSpec(SaltStorage.loadIV());
                    } catch (Exception ex) {
                        iv = KeyGeneratorUtil.generateIV();
                        SaltStorage.saveIV(iv.getIV());
                    }

                    int userId = userService.getUserId(username);
                    dispose(); // Close login window
                    IvParameterSpec finalIv = iv;
                    SwingUtilities.invokeLater(() -> new Main(key, finalIv, userId).setVisible(true));
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error during login: " + ex.getMessage());
            }
        });

        registerButton.addActionListener(e -> {
            dispose(); // закрыть текущее окно
            new RegistrationWindow().setVisible(true); // открыть окно регистрации
        });

        setVisible(true);
    }
}
