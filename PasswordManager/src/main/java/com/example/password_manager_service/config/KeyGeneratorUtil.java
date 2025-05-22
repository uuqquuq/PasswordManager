package com.example.password_manager_service.config;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

public class KeyGeneratorUtil {
    public static final int ITERATIONS = 65536;
    public static final int KEY_SIZE = 256;
    public static final int SALT_SIZE = 16;

    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_SIZE];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static SecretKeySpec deriveKey(char[] password, byte[] salt) throws Exception {
        KeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_SIZE);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static IvParameterSpec generateIV() {
        byte[] iv = new byte[16]; // AES блок 16 байт
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
}