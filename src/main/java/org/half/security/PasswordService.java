package org.half.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

// Utility class for a hashing and verifying password using Argon2id
public class PasswordService {
    // Argon2id instance
    private static final Argon2 argon2id = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    // Private constructor, singleton pattern
    private PasswordService() {}

    // Hash the given String using Argon2id
    public static String hashPassword(String password) {
        // Convert password to chars
        char[] passwordChars = password.toCharArray();

        try {
            // Hash password and return it
            return argon2id.hash(
                    3,
                    131072,
                    2,
                    passwordChars
            );
        } finally {
            // Wipe the password array from memory
            argon2id.wipeArray(passwordChars);
        }
    }

    // Verify the password against the given hash
    public static boolean verifyPassword(String password, String hashedPassword) {
        // Convert the password to chars
        char[] passwordChars = password.toCharArray();

        try {
            // Return true if passwords match, else false
            return argon2id.verify(hashedPassword, passwordChars);
        } finally {
            // Wipe the password array from memory
            argon2id.wipeArray(passwordChars);
        }
    }
}
