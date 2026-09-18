package org.half.service;

import org.half.exceptions.UserAlreadyExists;
import org.half.model.User;
import org.half.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String firstName, String lastName, String email, String phoneNumber, String username, String password) {
        if (firstName == null || firstName.isBlank() || firstName.length() > 20
            || lastName == null || lastName.isBlank() || lastName.length() > 20
            || email == null || !email.matches(".*@.*\\..*") || userRepository.getUserByEmail(email) != null
            || phoneNumber == null || !phoneNumber.matches("[0-9+()\\- ]+")
            || username == null || username.length() < 5 || username.length() > 50
            || password == null || password.length() > 255) {
            log.error("Invalid user input: firstName={}, lastName={}, email={}, phoneNumber={}, username={}", firstName, lastName, email, phoneNumber, username);
            return null;
        }
        try{
            User user = new User(firstName, lastName, email, phoneNumber, username, password);
            userRepository.addUser(user);
            log.info("User created successfully: {}", user);
            return user;
        } catch (UserAlreadyExists e) {
            System.out.println(e.getMessage());
            log.error("User already exists: {}", e.getMessage());
            return null;
        }
    }
}
