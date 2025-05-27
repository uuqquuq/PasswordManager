package com.example.password_manager_service.config;

import com.example.password_manager_service.config.DatabaseUtil;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class MasterPasswordValidator {

    // Проверяет, установлен ли мастер-пароль
    public static boolean isMasterPasswordSet() throws Exception {
        String sql = "SELECT EXISTS(SELECT 1 FROM master_password LIMIT 1)";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getBoolean(1); // true, если запись есть
            }
            return false;
        }
    }

    // Сохраняет хэш мастер-пароля
    public static void setMasterPasswordHash(String hash) throws Exception {
        String sql = "INSERT INTO master_password(password_hash) VALUES (?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hash);
            pstmt.executeUpdate();
        }
    }

    // Проверяет введённый пароль
    public static boolean verifyMasterPassword(String candidate) throws Exception {
        String sql = "SELECT password_hash FROM master_password LIMIT 1";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                return BCrypt.checkpw(candidate, storedHash);
            }
            return false;
        }
    }

    public static boolean isSaltSet() throws Exception {
        String sql = "SELECT EXISTS(SELECT 1 FROM app_config WHERE config_key = 'master_password_salt')";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getBoolean(1);
            }
        }
        return false;
    }
}