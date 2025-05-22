package com.example.password_manager_service.config;

import java.sql.*;

public class SaltStorage {

    private static final String SALT_KEY = "master_password_salt";
    private static final String IV_KEY = "initialization_vector";

    public static byte[] loadSalt() throws Exception {
        String sql = "SELECT config_value FROM app_config WHERE config_key = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, SALT_KEY);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBytes("config_value");
            } else {
                byte[] newSalt = KeyGeneratorUtil.generateSalt();
                saveSalt(newSalt);
                return newSalt;
            }
        }
    }

    public static void saveSalt(byte[] salt) throws Exception {
        String sql = "INSERT INTO app_config(config_key, config_value) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, SALT_KEY);
            pstmt.setBytes(2, salt);

            pstmt.executeUpdate();
        }
    }

    public static byte[] loadIV() throws Exception {
        String sql = "SELECT config_value FROM app_config WHERE config_key = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, IV_KEY);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBytes("config_value");
            } else {
                byte[] newIV = KeyGeneratorUtil.generateIV().getIV(); // метод ниже
                saveIV(newIV);
                return newIV;
            }
        }
    }

    public static void saveIV(byte[] iv) throws Exception {
        String sql = "INSERT INTO app_config(config_key, config_value) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, IV_KEY);
            pstmt.setBytes(2, iv);

            pstmt.executeUpdate();
        }
    }
}