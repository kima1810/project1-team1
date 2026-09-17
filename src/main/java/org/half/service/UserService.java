package org.half.service;

import org.half.exceptions.UserAlreadyExists;
import org.half.model.User;
import org.half.repository.UserRepository;

public class UserService {
    public static User createUser(String firstName, String lastName, String email, String phoneNumber, String username, String password) {
        if (firstName == null || firstName.isBlank() || firstName.length() > 20
            || lastName == null || lastName.isBlank() || lastName.length() > 20
            || email == null || !email.matches(".*@.*\\..*") || UserRepository.getUserByEmail(email) != null
            || phoneNumber == null || !phoneNumber.matches("[0-9+()\\- ]+")
            || username == null || username.length() < 5 || username.length() > 50
            || password == null || password.length() > 255) {
            return null;
        }
        try{
            User user = new User(firstName, lastName, email, phoneNumber, username, password);
            UserRepository.addUser(user);
            return user;
        } catch (UserAlreadyExists e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
