package com.example.password_manager_service;

import com.example.password_manager_service.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class RegistrationWindow extends JFrame {

    public RegistrationWindow() {
        setTitle("Register New User");
        setSize(400, 200);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTextField userField = new JTextField(20);
        JPasswordField passField = new JPasswordField(20);
        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back to Login");

        add(new JLabel("Username:"));
        add(userField);
        add(new JLabel("Master Password:"));
        add(passField);
        add(registerButton);
        add(backButton);

        registerButton.addActionListener(e -> {
            String username = userField.getText();
            char[] password = passField.getPassword();

            if (password.length < 8) {
                JOptionPane.showMessageDialog(this, "Password must be at least 8 characters.");
                return;
            }

            try {
                UserService userService = new UserService();
                userService.registerUser(username, password);
                JOptionPane.showMessageDialog(this, "Registration successful!");
                dispose(); // закрыть окно регистрации
                new LoginWindow().setVisible(true); // вернуться к логину
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage());
            } finally {
                Arrays.fill(password, '0');
            }
        });

        backButton.addActionListener(e -> {
            dispose();
            new LoginWindow().setVisible(true);
        });

        setVisible(true);
    }
}
