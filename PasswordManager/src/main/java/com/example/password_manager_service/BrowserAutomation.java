package com.example.password_manager_service;

import com.example.password_manager_service.model.PasswordEntry;
import com.example.password_manager_service.service.PasswordService;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.logging.Logger;

public class BrowserAutomation {

    private static final Logger LOGGER = Logger.getLogger(BrowserAutomation.class.getName());

    public static void main(String[] args) {
        WebDriver driver = null;
        try {
            // Настройка WebDriver
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();

            // Переход на сайт DemoQA
            processSite(driver, "https://demoqa.com/login ");

        } catch (Exception ex) {
            LOGGER.severe("Error during automation: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private static void processSite(WebDriver driver, String url) {
        try {
            driver.get(url);

            // Явное ожидание загрузки страницы входа
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userName")));

            // Поиск полей для ввода логина и пароля
            WebElement usernameField = findElementBySelectors(driver,
                    By.id("userName"),
                    By.name("userName"));

            WebElement passwordField = findElementBySelectors(driver,
                    By.id("password"),
                    By.name("password"));

            if (usernameField == null || passwordField == null) {
                LOGGER.warning("Login fields not found on site: " + url);
                return;
            }

            // Ввод тестовых учетных данных (замените на реальные или динамические данные)
            String testUsername = "testuser"; // Замените на корректные учетные данные
            String testPassword = "TestPassword@123"; // Замените на корректные учетные данные

            usernameField.sendKeys(testUsername);
            passwordField.sendKeys(testPassword);

            // Нажатие кнопки входа
            WebElement loginButton = findElementBySelectors(driver,
                    By.id("login"),
                    By.cssSelector("button[type='submit']"));
            if (loginButton == null) {
                LOGGER.warning("Login button not found on site: " + url);
                return;
            }
            loginButton.click();

            // Ожидание успешного входа
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("userName-value")));

            // Логирование успешного входа
            LOGGER.info("Successfully logged into DemoQA");

            // Получение данных из профиля пользователя
            String username = driver.findElement(By.id("userName-value")).getText();
            String siteTitle = driver.getTitle();

            // Логирование
            LOGGER.info("Username: " + username);
            LOGGER.info("Site Title: " + siteTitle);

            // Сохранение данных
            saveToDatabase(username, testPassword, siteTitle, url, 5); // ID текущего пользователя (должен быть динамическим)

        } catch (Exception ex) {
            LOGGER.severe("Error processing site: " + url + " - " + ex.getMessage());
        }
    }

    private static WebElement findElementBySelectors(WebDriver driver, By... selectors) {
        for (By selector : selectors) {
            try {
                WebElement element = driver.findElement(selector);
                if (element.isDisplayed()) {
                    return element;
                }
            } catch (NoSuchElementException ignored) {
                // Пропускаем, если элемент не найден
            }
        }
        return null;
    }

    public static void saveToDatabase(String username, String password, String siteTitle, String url, int currentUserId) {
        try {
            // Создание экземпляра PasswordService для сохранения данных
            PasswordService passwordService = new PasswordService();

            // Создание объекта PasswordEntry
            PasswordEntry entry = new PasswordEntry(siteTitle, username, password, url, "");

            // Сохранение данных в базу данных
            passwordService.savePasswordEntry(entry, currentUserId);

            LOGGER.info("Data saved to database successfully!");
        } catch (Exception ex) {
            LOGGER.severe("Error saving data to database: " + ex.getMessage());
        }
    }
}