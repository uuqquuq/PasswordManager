# Password Manager

A simple and secure password manager application built with Java Swing.

## Features

- Store and manage passwords securely
- Encrypt sensitive data
- User-friendly graphical interface
- Add, edit, delete, and view password entries
- Store additional information like website URLs and notes

## Requirements

- Java 11 or higher
- Maven

## Building the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the following command to build the application:
   ```bash
   mvn clean package
   ```
4. The executable JAR file will be created in the `target` directory

## Running the Application

Run the following command from the project directory:
```bash
java -jar target/password-manager-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Usage

1. **Adding a Password Entry**
   - Fill in the title, username, password, website, and notes fields
   - Click the "Add" button

2. **Editing a Password Entry**
   - Select an entry from the table
   - Modify the fields as needed
   - Click the "Edit" button

3. **Deleting a Password Entry**
   - Select an entry from the table
   - Click the "Delete" button

4. **Viewing Password Details**
   - Select an entry from the table
   - Click the "View Details" button

## Security Notes

- All passwords are encrypted using AES encryption
- The master key is currently hardcoded in the application (in a production environment, this should be stored securely)
- Passwords are never stored in plain text

## License

This project is licensed under the MIT License. 