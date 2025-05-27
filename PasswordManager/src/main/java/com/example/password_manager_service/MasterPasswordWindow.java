package com.example.password_manager_service;

import com.example.password_manager_service.config.KeyGeneratorUtil;
import com.example.password_manager_service.config.MasterPasswordValidator;
import com.example.password_manager_service.config.SaltStorage;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class MasterPasswordWindow extends JFrame {

    public MasterPasswordWindow() {
        setTitle("Set Master Password");
        setSize(400, 200);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPasswordField masterPassField = new JPasswordField(20);
        JButton confirmButton = new JButton("Confirm");

        add(new JLabel("Enter Master Password:"));
        add(masterPassField);
        add(confirmButton);

        confirmButton.addActionListener(e -> {
            char[] masterPassword = masterPassField.getPassword();
            if (masterPassword.length == 0) {
                JOptionPane.showMessageDialog(this, "Master password cannot be empty.");
                return;
            }

            try {
                String hash = BCrypt.hashpw(new String(masterPassword), BCrypt.gensalt());
                MasterPasswordValidator.setMasterPasswordHash(hash);

                byte[] salt = KeyGeneratorUtil.generateSalt();
                SaltStorage.saveSalt(salt);

                IvParameterSpec iv = KeyGeneratorUtil.generateIV();
                SaltStorage.saveIV(iv.getIV());

                SecretKeySpec key = KeyGeneratorUtil.deriveKey(masterPassword, salt);
                Arrays.fill(masterPassword, '0');

                // Открытие окна регистрации
                dispose();
                new RegistrationWindow().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error setting up master password: " + ex.getMessage());
            }
        });

        setVisible(true);
    }
}
