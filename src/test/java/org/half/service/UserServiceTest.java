package org.half.service;

import org.half.exceptions.UserAlreadyExists;
import org.half.model.User;
import org.half.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    /* --- Successful Test Cases --- */

    // User is created successfully
    @Test
    void createUser_success_allValid() {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String hashedPassword = "hashedPassword123";

        try (
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class)
        ) {
            userRepositoryMock.when(() -> UserRepository.getUserByEmail(email)).thenReturn(null);

            User createdUser = UserService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

            assertNotNull(createdUser);
            assertEquals(firstName, createdUser.getFirstName());
            assertEquals(lastName, createdUser.getLastName());
            assertEquals(email, createdUser.getEmail());
            assertEquals(phoneNumber, createdUser.getPhoneNumber());
            assertEquals(username, createdUser.getUsername());
            assertEquals(hashedPassword, createdUser.getPassword());

            userRepositoryMock.verify(() -> UserRepository.addUser(any(User.class)), times(1));
        }
    }

    /* --- Failed Test Cases --- */

    // First name is blank or null
    // Guard short-circuits before reaching UserRepository, so no mocking is needed
    @Test
    void createUser_failure_firstNameBlank() {
        String firstName = "";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String hashedPassword = "hashedPassword123";

        User createdUser = UserService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

        assertNull(createdUser);
    }

    @Test
    void createUser_failure_firstNameNull() {
        String firstName = null;
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String hashedPassword = "hashedPassword123";

        User createdUser = UserService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

        assertNull(createdUser);
    }

    // First name is longer than 20 characters
    @Test
    void createUser_failure_firstNameTooLong() {
        String firstName = "ThisFirstNameIsWayTooLongToBeValid";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String hashedPassword = "hashedPassword123";

        User createdUser = UserService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

        assertNull(createdUser);
    }

    // Last name is blank or null
    @Test
    void createUser_failure_lastNameBlank() {
        String firstName = "Alex";
        String lastName = "";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String hashedPassword = "hashedPassword123";

        User createdUser = UserService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

        assertNull(createdUser);
    }

    @Test
    void createUser_failure_lastNameNull() {
        String firstName = "Alex";
        String lastName = null;
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String hashedPassword = "hashedPassword123";

        User createdUser = UserService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

        assertNull(createdUser);
    }

    // Last name is longer than 20 characters
    @Test
    void createUser_failure_lastNameTooLong() {
        String firstName = "Alex";
        String lastName = "ThisLastNameIsWayTooLongToBeValid";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String hashedPassword = "hashedPassword123";

        User createdUser = UserService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

        assertNull(createdUser);
    }

    // Email is null
    @Test
    void createUser_failure_emailNull() {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = null;
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String hashedPassword = "hashedPassword123";

        User createdUser = UserService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

        assertNull(createdUser);
    }

    // Email is invalid
    @Test
    void createUser_failure_emailInvalid() {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kimexample.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String hashedPassword = "hashedPassword123";

        User createdUser = UserService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

        assertNull(createdUser);
    }

    // Email already in use
    // This is the only guard clause that touches UserRepository directly, so it must be mocked
    @Test
    void createUser_failure_emailAlreadyInUse() {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String hashedPassword = "hashedPassword123";

        User existingUser = mock(User.class);

        try (
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class)
        ) {
            userRepositoryMock.when(() -> UserRepository.getUserByEmail(email)).thenReturn(existingUser);

            User createdUser = UserService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

            assertNull(createdUser);
        }
    }

    // Phone number is null
    // Email is valid here, so the guard reaches UserRepository.getUserByEmail before failing on the phone number
    @Test
    void createUser_failure_phoneNumberNull() {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = null;
        String username = "alexkim";
        String hashedPassword = "hashedPassword123";

        try (
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class)
        ) {
            userRepositoryMock.when(() -> UserRepository.getUserByEmail(email)).thenReturn(null);

            User createdUser = UserService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

            assertNull(createdUser);
        }
    }

    // Phone number contains characters outside the allowed set
    @Test
    void createUser_failure_phoneNumberInvalid() {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "invalid-phone!";
        String username = "alexkim";
        String hashedPassword = "hashedPassword123";

        try (
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class)
        ) {
            userRepositoryMock.when(() -> UserRepository.getUserByEmail(email)).thenReturn(null);

            User createdUser = UserService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

            assertNull(createdUser);
        }
    }

    // Username is null
    @Test
    void createUser_failure_usernameNull() {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = null;
        String hashedPassword = "hashedPassword123";

        try (
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class)
        ) {
            userRepositoryMock.when(() -> UserRepository.getUserByEmail(email)).thenReturn(null);

            User createdUser = UserService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

            assertNull(createdUser);
        }
    }

    // Username is too short
    @Test
    void createUser_failure_usernameTooShort() {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "al";
        String hashedPassword = "hashedPassword123";

        try (
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class)
        ) {
            userRepositoryMock.when(() -> UserRepository.getUserByEmail(email)).thenReturn(null);

            User createdUser = UserService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

            assertNull(createdUser);
        }
    }

    // Username is too long
    @Test
    void createUser_failure_usernameTooLong() {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "a".repeat(51);
        String hashedPassword = "hashedPassword123";

        try (
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class)
        ) {
            userRepositoryMock.when(() -> UserRepository.getUserByEmail(email)).thenReturn(null);

            User createdUser = UserService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

            assertNull(createdUser);
        }
    }

    // Username is already taken
    @Test
    void createUser_failure_usernameAlreadyExist() {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String hashedPassword = "hashedPassword123";

        try (
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class)
        ) {
            userRepositoryMock.when(() -> UserRepository.getUserByEmail(email)).thenReturn(null);
            userRepositoryMock.when(() -> UserRepository.addUser(any(User.class)))
                    .thenThrow(new UserAlreadyExists("Username '" + username + "' is already taken."));

            User createdUser = UserService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

            assertNull(createdUser);
        }
    }

    // Password is null
    @Test
    void createUser_failure_passwordNull() {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String hashedPassword = null;

        try (
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class)
        ) {
            userRepositoryMock.when(() -> UserRepository.getUserByEmail(email)).thenReturn(null);

            User createdUser = UserService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

            assertNull(createdUser);
        }
    }

    // Password is longer than 255 characters
    @Test
    void createUser_failure_passwordTooLong() {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String hashedPassword = "a".repeat(256);

        try (
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class)
        ) {
            userRepositoryMock.when(() -> UserRepository.getUserByEmail(email)).thenReturn(null);

            User createdUser = UserService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

            assertNull(createdUser);
        }
    }

    // Repository failure
    @Test
    void createUser_failure_repositoryLevelFailure() {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String hashedPassword = "hashedPassword123";

        try (
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class)
        ) {
            userRepositoryMock.when(() -> UserRepository.getUserByEmail(email)).thenReturn(null);
            userRepositoryMock.when(() -> UserRepository.addUser(any(User.class)))
                    .thenThrow(new UserAlreadyExists("Unable to create user."));

            User createdUser = UserService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

            assertNull(createdUser);
        }
    }
}
