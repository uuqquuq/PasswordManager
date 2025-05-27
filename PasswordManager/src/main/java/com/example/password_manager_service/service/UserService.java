package com.example.password_manager_service.service;

import com.example.password_manager_service.config.DatabaseUtil;
import com.example.password_manager_service.config.KeyGeneratorUtil;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.Arrays;

public class UserService {

    public void registerUser(String username, char[] password) throws Exception {
        if (userExists(username)) {
            throw new RuntimeException("Username already exists");
        }

        byte[] salt = KeyGeneratorUtil.generateSalt();
        String hash = BCrypt.hashpw(new String(password), BCrypt.gensalt());

        String sql = "INSERT INTO users(username, password, salt) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hash);
            pstmt.setBytes(3, salt);

            pstmt.executeUpdate();
        }
    }

    public boolean authenticate(String username, char[] password) throws Exception {
        String sql = "SELECT password, salt FROM users WHERE username = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                return BCrypt.checkpw(new String(password), storedHash);
            }
            return false;
        } finally {
            Arrays.fill(password, '0');
        }
    }

    public int getUserId(String username) throws Exception {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new Exception("User not found");
            }
        }
    }

    public byte[] loadSaltForUser(String username) throws Exception {
        String sql = "SELECT salt FROM users WHERE username = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBytes("salt");
            } else {
                throw new Exception("User not found");
            }
        }
    }

    public boolean userExists(String username) throws Exception {
        String sql = "SELECT EXISTS(SELECT 1 FROM users WHERE username = ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean(1);
            }
        }
        return false;
    }

    public String getUsername(int userId) throws Exception {
        String sql = "SELECT username FROM users WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        }
        return null;
    }
}