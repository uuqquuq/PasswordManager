package com.example.password_manager_service.model;

import java.io.Serializable;

public class PasswordEntry implements Serializable {
    private int id;
    private String title;
    private String username;
    private String password;
    private String website;
    private String notes;
    private int userId;

    public PasswordEntry() {
    }

    public PasswordEntry(String title, String username, String password, String website, String notes, int userId) {
        this.title = title;
        this.username = username;
        this.password = password;
        this.website = website;
        this.notes = notes;
        this.userId = userId;
    }

    public PasswordEntry(String title, String username, String password, String website, String notes) {
        this.title = title;
        this.username = username;
        this.password = password;
        this.website = website;
        this.notes = notes;
    }

    public PasswordEntry(int id, String title, String username, String password, String website, String notes, int userId) {
        this.id = id;
        this.title = title;
        this.username = username;
        this.password = password;
        this.website = website;
        this.notes = notes;
        this.userId = userId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return title;
    }
} 