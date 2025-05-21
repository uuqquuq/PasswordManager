package com.example.password_manager_service;
import com.example.password_manager_service.config.EncryptionUtil;
import com.example.password_manager_service.model.PasswordEntry;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame{

	private List<PasswordEntry> passwordEntries;
	private JTable passwordTable;
	private DefaultTableModel tableModel;
	private JTextField titleField, usernameField, passwordField, websiteField, notesField;

	public Main() {
		passwordEntries = new ArrayList<>();
		initializeUI();
	}

	private void initializeUI() {
		setTitle("Password Manager");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(null);

		// Create main panel with BorderLayout
		JPanel mainPanel = new JPanel(new BorderLayout());

		// Create table
		String[] columnNames = {"Title", "Username", "Website"};
		tableModel = new DefaultTableModel(columnNames, 0);
		passwordTable = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(passwordTable);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		// Create input panel
		JPanel inputPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Add input fields
		titleField = new JTextField(20);
		usernameField = new JTextField(20);
		passwordField = new JTextField(20);
		websiteField = new JTextField(20);
		notesField = new JTextField(20);

		gbc.gridx = 0; gbc.gridy = 0;
		inputPanel.add(new JLabel("Title:"), gbc);
		gbc.gridx = 1;
		inputPanel.add(titleField, gbc);

		gbc.gridx = 0; gbc.gridy = 1;
		inputPanel.add(new JLabel("Username:"), gbc);
		gbc.gridx = 1;
		inputPanel.add(usernameField, gbc);

		gbc.gridx = 0; gbc.gridy = 2;
		inputPanel.add(new JLabel("Password:"), gbc);
		gbc.gridx = 1;
		inputPanel.add(passwordField, gbc);

		gbc.gridx = 0; gbc.gridy = 3;
		inputPanel.add(new JLabel("Website:"), gbc);
		gbc.gridx = 1;
		inputPanel.add(websiteField, gbc);

		gbc.gridx = 0; gbc.gridy = 4;
		inputPanel.add(new JLabel("Notes:"), gbc);
		gbc.gridx = 1;
		inputPanel.add(notesField, gbc);

		// Add buttons
		JPanel buttonPanel = new JPanel();
		JButton addButton = new JButton("Add");
		JButton editButton = new JButton("Edit");
		JButton deleteButton = new JButton("Delete");
		JButton viewButton = new JButton("View Details");

		buttonPanel.add(addButton);
		buttonPanel.add(editButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(viewButton);

		gbc.gridx = 0; gbc.gridy = 5;
		gbc.gridwidth = 2;
		inputPanel.add(buttonPanel, gbc);

		mainPanel.add(inputPanel, BorderLayout.SOUTH);

		// Add action listeners
		addButton.addActionListener(e -> addPasswordEntry());
		editButton.addActionListener(e -> editPasswordEntry());
		deleteButton.addActionListener(e -> deletePasswordEntry());
		viewButton.addActionListener(e -> viewPasswordEntry());

		add(mainPanel);
	}

	private void addPasswordEntry() {
		try {
			String title = titleField.getText();
			String username = usernameField.getText();
			String password = EncryptionUtil.encrypt(passwordField.getText());
			String website = websiteField.getText();
			String notes = notesField.getText();

			PasswordEntry entry = new PasswordEntry(title, username, password, website, notes);
			passwordEntries.add(entry);
			updateTable();
			clearFields();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Error adding password: " + ex.getMessage());
		}
	}

	private void editPasswordEntry() {
		int selectedRow = passwordTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Please select an entry to edit");
			return;
		}

		try {
			PasswordEntry entry = passwordEntries.get(selectedRow);
			entry.setTitle(titleField.getText());
			entry.setUsername(usernameField.getText());
			entry.setPassword(EncryptionUtil.encrypt(passwordField.getText()));
			entry.setWebsite(websiteField.getText());
			entry.setNotes(notesField.getText());

			updateTable();
			clearFields();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Error editing password: " + ex.getMessage());
		}
	}

	private void deletePasswordEntry() {
		int selectedRow = passwordTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Please select an entry to delete");
			return;
		}

		passwordEntries.remove(selectedRow);
		updateTable();
		clearFields();
	}

	private void viewPasswordEntry() {
		int selectedRow = passwordTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Please select an entry to view");
			return;
		}

		try {
			PasswordEntry entry = passwordEntries.get(selectedRow);
			String decryptedPassword = EncryptionUtil.decrypt(entry.getPassword());

			StringBuilder details = new StringBuilder();
			details.append("Title: ").append(entry.getTitle()).append("\n");
			details.append("Username: ").append(entry.getUsername()).append("\n");
			details.append("Password: ").append(decryptedPassword).append("\n");
			details.append("Website: ").append(entry.getWebsite()).append("\n");
			details.append("Notes: ").append(entry.getNotes());

			JOptionPane.showMessageDialog(this, details.toString(), "Password Details", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Error viewing password: " + ex.getMessage());
		}
	}

	private void updateTable() {
		tableModel.setRowCount(0);
		for (PasswordEntry entry : passwordEntries) {
			tableModel.addRow(new Object[]{
					entry.getTitle(),
					entry.getUsername(),
					entry.getWebsite()
			});
		}
	}

	private void clearFields() {
		titleField.setText("");
		usernameField.setText("");
		passwordField.setText("");
		websiteField.setText("");
		notesField.setText("");
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			new Main().setVisible(true);
		});
	}

}
