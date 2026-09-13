package org.half.service;

import org.half.model.User;
import org.half.repository.UserRepository;

public class UserService {
    public static User createUser(String firstName, String lastName, String email, String phoneNumber, String username, String password) {
        User user = new User(firstName, lastName, email, phoneNumber, username, password);
        UserRepository.addUser(user);
        return user;
    }
}
