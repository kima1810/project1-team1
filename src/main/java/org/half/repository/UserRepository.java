package org.half.repository;

import org.half.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private static final Map<String, User> userDatabase = new HashMap<>();

    private UserRepository() {}

    public static void addUser(User user) {
        userDatabase.put(user.username, user);
    }

    public static void updateUser(User user) {
        userDatabase.replace(user.username, user);
    }

    public static User getUser(String username) {
        return userDatabase.get(username);
    }
}
