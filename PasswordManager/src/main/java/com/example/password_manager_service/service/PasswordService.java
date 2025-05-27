package com.example.password_manager_service.service;

import com.example.password_manager_service.config.DatabaseUtil;
import com.example.password_manager_service.model.PasswordEntry;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PasswordService {

    public List<PasswordEntry> getAllPasswordEntries(int userId) throws Exception {
        List<PasswordEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM password_entries WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                PasswordEntry entry = new PasswordEntry();
                entry.setId(rs.getInt("id"));
                entry.setTitle(rs.getString("title"));
                entry.setUsername(rs.getString("username"));
                entry.setPassword(rs.getString("password")); // зашифрован в БД
                entry.setWebsite(rs.getString("website"));
                entry.setNotes(rs.getString("notes"));

                entries.add(entry);
            }
        }
        return entries;
    }

    public void savePasswordEntry(PasswordEntry entry, int userId) throws Exception {
        String sql = "INSERT INTO password_entries(title, username, password, website, notes, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entry.getTitle());
            pstmt.setString(2, entry.getUsername());
            pstmt.setString(3, entry.getPassword());
            pstmt.setString(4, entry.getWebsite());
            pstmt.setString(5, entry.getNotes());
            pstmt.setInt(6, userId);

            pstmt.executeUpdate();
        }
    }

    public void updatePasswordEntry(int id, PasswordEntry entry, int userId) throws Exception {
        String sql = "UPDATE password_entries SET title=?, username=?, password=?, website=?, notes=? WHERE id=? AND user_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entry.getTitle());
            pstmt.setString(2, entry.getUsername());
            pstmt.setString(3, entry.getPassword());
            pstmt.setString(4, entry.getWebsite());
            pstmt.setString(5, entry.getNotes());
            pstmt.setInt(6, id);
            pstmt.setInt(7, userId);

            pstmt.executeUpdate();
        }
    }

    public void deletePasswordEntry(int id, int userId) throws Exception {
        String sql = "DELETE FROM password_entries WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        }
    }
}