package com.example.password_manager_service;

import com.example.password_manager_service.config.EncryptionUtil;
import com.example.password_manager_service.config.KeyGeneratorUtil;
import com.example.password_manager_service.config.MasterPasswordValidator;
import com.example.password_manager_service.config.SaltStorage;
import com.example.password_manager_service.model.PasswordEntry;
import com.example.password_manager_service.service.PasswordService;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main extends JFrame {

	private List<PasswordEntry> passwordEntries;
	private JTable passwordTable;
	private DefaultTableModel tableModel;

	// Текстовые поля
	private JTextField titleField, usernameField, websiteField, notesField;
	private JPasswordField passwordField;

	private PasswordService passwordService;
	private SecretKeySpec encryptionKey;
	private IvParameterSpec encryptionIV;

	public Main(SecretKeySpec encryptionKey, IvParameterSpec encryptionIV) {
		this.encryptionKey = encryptionKey;
		this.encryptionIV = encryptionIV;
		this.passwordService = new PasswordService();
		this.passwordEntries = new ArrayList<>();
		loadData(); // Загрузка данных из БД
		initializeUI();
	}

	private void loadData() {
		try {
			passwordEntries.clear();
			List<PasswordEntry> entries = passwordService.getAllPasswordEntries();

			for (PasswordEntry entry : entries) {
				try {
					String decryptedPass = EncryptionUtil.decrypt(entry.getPassword(), encryptionKey, encryptionIV);
					entry.setPassword(decryptedPass);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this,
							"Failed to decrypt a password. Possible wrong master password.",
							"Decryption Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}

			passwordEntries.addAll(entries);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this,
					"Error loading data from database: " + ex.getMessage(),
					"Database Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void initializeUI() {
		setTitle("Password Manager");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(null);

		JPanel mainPanel = new JPanel(new BorderLayout());

		// Создание таблицы
		String[] columnNames = {"Title", "Username", "Website"};
		tableModel = new DefaultTableModel(columnNames, 0);
		passwordTable = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(passwordTable);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		// Поля ввода
		JPanel inputPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		titleField = new JTextField(20);
		usernameField = new JTextField(20);
		passwordField = new JPasswordField(20);
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

		// Кнопки
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

		// Слушатели событий
		addButton.addActionListener(this::addPasswordEntry);
		editButton.addActionListener(this::editPasswordEntry);
		deleteButton.addActionListener(this::deletePasswordEntry);
		viewButton.addActionListener(this::viewPasswordEntry);

		add(mainPanel);
	}

	private void addPasswordEntry(ActionEvent e) {
		try {
			String title = titleField.getText();
			String username = usernameField.getText();
			String password = new String(passwordField.getPassword());
			String website = websiteField.getText();
			String notes = notesField.getText();

			String encryptedPassword = EncryptionUtil.encrypt(password, encryptionKey, encryptionIV);

			PasswordEntry entry = new PasswordEntry(title, username, encryptedPassword, website, notes);
			passwordService.savePasswordEntry(entry); // Сохраняем в БД зашифрованный пароль

			entry.setPassword(password); // Для отображения — расшифровываем
			passwordEntries.add(entry);
			updateTable();
			clearFields();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Error adding password: " + ex.getMessage());
		}
	}

	private void editPasswordEntry(ActionEvent e) {
		int selectedRow = passwordTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Please select an entry to edit");
			return;
		}

		try {
			PasswordEntry entry = passwordEntries.get(selectedRow);
			entry.setTitle(titleField.getText());
			entry.setUsername(usernameField.getText());
			entry.setWebsite(websiteField.getText());
			entry.setNotes(notesField.getText());

			String newPassword = new String(passwordField.getPassword());
			entry.setPassword(EncryptionUtil.encrypt(newPassword, encryptionKey, encryptionIV));

			passwordService.updatePasswordEntry(entry.getId(), entry);

			entry.setPassword(newPassword); // Расшифрован для отображения
			updateTable();
			clearFields();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Error editing password: " + ex.getMessage());
		}
	}

	private void deletePasswordEntry(ActionEvent e) {
		int selectedRow = passwordTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Please select an entry to delete");
			return;
		}

		try {
			PasswordEntry entry = passwordEntries.get(selectedRow);
			passwordService.deletePasswordEntry(entry.getId());
			passwordEntries.remove(selectedRow);
			updateTable();
			clearFields();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Error deleting password: " + ex.getMessage());
		}
	}

	private void viewPasswordEntry(ActionEvent e) {
		int selectedRow = passwordTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Please select an entry to view");
			return;
		}

		try {
			PasswordEntry entry = passwordEntries.get(selectedRow);
			String decryptedPassword = entry.getPassword(); // Уже дешифрован при загрузке

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
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(() -> showMasterPasswordDialog());
	}

	private static void showMasterPasswordDialog() {
		JPasswordField passwordField = new JPasswordField();
		int option = JOptionPane.showConfirmDialog(null, passwordField, "Enter Master Password", JOptionPane.OK_CANCEL_OPTION);

		if (option == JOptionPane.OK_OPTION) {
			char[] masterPassword = passwordField.getPassword();
			if (masterPassword.length == 0) {
				JOptionPane.showMessageDialog(null, "Master password cannot be empty.");
				return;
			}

			try {
				boolean isPasswordSet = MasterPasswordValidator.isMasterPasswordSet();

				if (!isPasswordSet) {
					// Первый запуск — установка мастер-пароля
					String passwordText = new String(masterPassword);
					String hash = BCrypt.hashpw(passwordText, BCrypt.gensalt());
					MasterPasswordValidator.setMasterPasswordHash(hash);
				} else {
					// Не первый раз — проверяем
					String enteredPassword = new String(masterPassword);
					if (!MasterPasswordValidator.verifyMasterPassword(enteredPassword)) {
						JOptionPane.showMessageDialog(null, "Incorrect master password.");
						Arrays.fill(masterPassword, '0');
						return;
					}
				}

				// Генерация ключа и IV
				byte[] salt = SaltStorage.loadSalt();
				IvParameterSpec iv = new IvParameterSpec(SaltStorage.loadIV());

				SecretKeySpec key = KeyGeneratorUtil.deriveKey(masterPassword, salt);

				// Очистка чувствительных данных
				Arrays.fill(masterPassword, '0');

				// Запуск основного окна
				SwingUtilities.invokeLater(() -> new Main(key, iv).setVisible(true));
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "Error during authentication: " + ex.getMessage());
				ex.printStackTrace();
			}
		} else {
			System.exit(0);
		}
	}
}