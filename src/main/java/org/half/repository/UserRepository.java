package org.half.repository;

import org.half.model.User;
import org.half.security.PasswordService;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private static final Map<String, User> userDatabase = new HashMap<>();

    private UserRepository() {}

    public static void initializeDatabase() {
        for (int i = 0; i < 5; i++) {
            User newUser = new User();
            newUser.setUsername("test" + i);
            newUser.setPassword(PasswordService.hashPassword("testPassword" + i));
            newUser.setBalance(500.00 * i);

            addUser(newUser);
        }
    }

    public static void addUser(User user) {
        userDatabase.put(user.username, user);
    }

    public static void updateUser(User user) {
        userDatabase.replace(user.username, user);
    }

    public static User getUser(String username) {
        return userDatabase.get(username);
    }

    public static boolean checkUser(String username) {return userDatabase.containsKey(username);}
}
