package org.half.view;

import org.half.model.User;
import org.half.repository.UserRepository;
import org.half.security.PasswordService;
import org.half.service.UserService;
import org.half.utility.BankScanner;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class RegisterTest {

    /* --- Successful Test Cases --- */

    // All correct inputs on first try
    @Test
    void register_success_allValidFirstTry() {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String password = "password123";
        String passwordConfirmation = "password123";
        String hashedPassword = "hashedPassword123";

        User createdUser = mock(User.class);

        try (
                MockedStatic<BankScanner> scannerMock = Mockito.mockStatic(BankScanner.class);
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class);
                MockedStatic<PasswordService> passwordServiceMock = Mockito.mockStatic(PasswordService.class);
                MockedStatic<UserService> userServiceMock = Mockito.mockStatic(UserService.class);
                MockedStatic<AccountCreation> accountCreationMock = Mockito.mockStatic(AccountCreation.class)
        ) {
            scannerMock.when(BankScanner::getString).thenReturn(
                    firstName,
                    lastName,
                    email,
                    phoneNumber,
                    username,
                    password,
                    passwordConfirmation
            );

            userRepositoryMock.when(() -> UserRepository.getUser(username)).thenReturn(null);
            passwordServiceMock.when(() -> PasswordService.hashPassword(password)).thenReturn(hashedPassword);
            userServiceMock.when(() -> UserService.createUser(
                    firstName, lastName, email, phoneNumber, username, hashedPassword
            )).thenReturn(createdUser);

            Register.register();

            userServiceMock.verify(() -> UserService.createUser(
                    eq(firstName), eq(lastName), eq(email), eq(phoneNumber), eq(username), eq(hashedPassword)
            ), times(1));

            accountCreationMock.verify(() -> AccountCreation.createAccount(createdUser), times(1));
        }
    }

    // First name is over 20 characters
    @Test
    void register_success_FirstNameTooLong() {
        String invalidFirstName = "ThisFirstNameIsWayTooLongToBeValid";
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String password = "password123";
        String passwordConfirmation = "password123";
        String hashedPassword = "hashedPassword123";

        User createdUser = mock(User.class);

        try (
                MockedStatic<BankScanner> scannerMock = Mockito.mockStatic(BankScanner.class);
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class);
                MockedStatic<PasswordService> passwordServiceMock = Mockito.mockStatic(PasswordService.class);
                MockedStatic<UserService> userServiceMock = Mockito.mockStatic(UserService.class);
                MockedStatic<AccountCreation> accountCreationMock = Mockito.mockStatic(AccountCreation.class)
        ) {
            scannerMock.when(BankScanner::getString).thenReturn(
                    invalidFirstName,
                    firstName,
                    lastName,
                    email,
                    phoneNumber,
                    username,
                    password,
                    passwordConfirmation
            );

            userRepositoryMock.when(() -> UserRepository.getUser(username)).thenReturn(null);
            passwordServiceMock.when(() -> PasswordService.hashPassword(password)).thenReturn(hashedPassword);
            userServiceMock.when(() -> UserService.createUser(
                    firstName, lastName, email, phoneNumber, username, hashedPassword
            )).thenReturn(createdUser);

            Register.register();

            userServiceMock.verify(() -> UserService.createUser(
                    eq(firstName), eq(lastName), eq(email), eq(phoneNumber), eq(username), eq(hashedPassword)
            ), times(1));

            accountCreationMock.verify(() -> AccountCreation.createAccount(createdUser), times(1));
        }
    }

    // First name is blank
    @Test
    void register_success_FirstNameBlank() {
        String blankFirstName = "   ";
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String password = "password123";
        String passwordConfirmation = "password123";
        String hashedPassword = "hashedPassword123";

        User createdUser = mock(User.class);

        try (
                MockedStatic<BankScanner> scannerMock = Mockito.mockStatic(BankScanner.class);
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class);
                MockedStatic<PasswordService> passwordServiceMock = Mockito.mockStatic(PasswordService.class);
                MockedStatic<UserService> userServiceMock = Mockito.mockStatic(UserService.class);
                MockedStatic<AccountCreation> accountCreationMock = Mockito.mockStatic(AccountCreation.class)
        ) {
            scannerMock.when(BankScanner::getString).thenReturn(
                    blankFirstName,
                    firstName,
                    lastName,
                    email,
                    phoneNumber,
                    username,
                    password,
                    passwordConfirmation
            );

            userRepositoryMock.when(() -> UserRepository.getUser(username)).thenReturn(null);
            passwordServiceMock.when(() -> PasswordService.hashPassword(password)).thenReturn(hashedPassword);
            userServiceMock.when(() -> UserService.createUser(
                    firstName, lastName, email, phoneNumber, username, hashedPassword
            )).thenReturn(createdUser);

            Register.register();

            userServiceMock.verify(() -> UserService.createUser(
                    eq(firstName), eq(lastName), eq(email), eq(phoneNumber), eq(username), eq(hashedPassword)
            ), times(1));

            accountCreationMock.verify(() -> AccountCreation.createAccount(createdUser), times(1));
        }
    }

    // Last name is over 20 characters
    @Test
    void register_success_LastNameTooLong() {
        String firstName = "Alex";
        String invalidLastName = "ThisLastNameIsWayTooLongToBeValid";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String password = "password123";
        String passwordConfirmation = "password123";
        String hashedPassword = "hashedPassword123";

        User createdUser = mock(User.class);

        try (
                MockedStatic<BankScanner> scannerMock = Mockito.mockStatic(BankScanner.class);
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class);
                MockedStatic<PasswordService> passwordServiceMock = Mockito.mockStatic(PasswordService.class);
                MockedStatic<UserService> userServiceMock = Mockito.mockStatic(UserService.class);
                MockedStatic<AccountCreation> accountCreationMock = Mockito.mockStatic(AccountCreation.class)
        ) {
            scannerMock.when(BankScanner::getString).thenReturn(
                    firstName,
                    invalidLastName,
                    lastName,
                    email,
                    phoneNumber,
                    username,
                    password,
                    passwordConfirmation
            );

            userRepositoryMock.when(() -> UserRepository.getUser(username)).thenReturn(null);
            passwordServiceMock.when(() -> PasswordService.hashPassword(password)).thenReturn(hashedPassword);
            userServiceMock.when(() -> UserService.createUser(
                    firstName, lastName, email, phoneNumber, username, hashedPassword
            )).thenReturn(createdUser);

            Register.register();

            userServiceMock.verify(() -> UserService.createUser(
                    eq(firstName), eq(lastName), eq(email), eq(phoneNumber), eq(username), eq(hashedPassword)
            ), times(1));

            accountCreationMock.verify(() -> AccountCreation.createAccount(createdUser), times(1));
        }
    }

    // Last name is blank
    @Test
    void register_success_LastNameBlank() {
        String firstName = "Alex";
        String blankLastName = "   ";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String password = "password123";
        String passwordConfirmation = "password123";
        String hashedPassword = "hashedPassword123";

        User createdUser = mock(User.class);

        try (
                MockedStatic<BankScanner> scannerMock = Mockito.mockStatic(BankScanner.class);
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class);
                MockedStatic<PasswordService> passwordServiceMock = Mockito.mockStatic(PasswordService.class);
                MockedStatic<UserService> userServiceMock = Mockito.mockStatic(UserService.class);
                MockedStatic<AccountCreation> accountCreationMock = Mockito.mockStatic(AccountCreation.class)
        ) {
            scannerMock.when(BankScanner::getString).thenReturn(
                    firstName,
                    blankLastName,
                    lastName,
                    email,
                    phoneNumber,
                    username,
                    password,
                    passwordConfirmation
            );

            userRepositoryMock.when(() -> UserRepository.getUser(username)).thenReturn(null);
            passwordServiceMock.when(() -> PasswordService.hashPassword(password)).thenReturn(hashedPassword);
            userServiceMock.when(() -> UserService.createUser(
                    firstName, lastName, email, phoneNumber, username, hashedPassword
            )).thenReturn(createdUser);

            Register.register();

            userServiceMock.verify(() -> UserService.createUser(
                    eq(firstName), eq(lastName), eq(email), eq(phoneNumber), eq(username), eq(hashedPassword)
            ), times(1));

            accountCreationMock.verify(() -> AccountCreation.createAccount(createdUser), times(1));
        }
    }

    // Email is an invalid format
    @Test
    void register_success_InvalidEmail() {
        String firstName = "Alex";
        String lastName = "Kim";
        String invalidEmail = "not-an-email";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String password = "password123";
        String passwordConfirmation = "password123";
        String hashedPassword = "hashedPassword123";

        User createdUser = mock(User.class);

        try (
                MockedStatic<BankScanner> scannerMock = Mockito.mockStatic(BankScanner.class);
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class);
                MockedStatic<PasswordService> passwordServiceMock = Mockito.mockStatic(PasswordService.class);
                MockedStatic<UserService> userServiceMock = Mockito.mockStatic(UserService.class);
                MockedStatic<AccountCreation> accountCreationMock = Mockito.mockStatic(AccountCreation.class)
        ) {
            scannerMock.when(BankScanner::getString).thenReturn(
                    firstName,
                    lastName,
                    invalidEmail,
                    email,
                    phoneNumber,
                    username,
                    password,
                    passwordConfirmation
            );

            userRepositoryMock.when(() -> UserRepository.getUser(username)).thenReturn(null);
            passwordServiceMock.when(() -> PasswordService.hashPassword(password)).thenReturn(hashedPassword);
            userServiceMock.when(() -> UserService.createUser(
                    firstName, lastName, email, phoneNumber, username, hashedPassword
            )).thenReturn(createdUser);

            Register.register();

            userServiceMock.verify(() -> UserService.createUser(
                    eq(firstName), eq(lastName), eq(email), eq(phoneNumber), eq(username), eq(hashedPassword)
            ), times(1));

            accountCreationMock.verify(() -> AccountCreation.createAccount(createdUser), times(1));
        }
    }

    // Email is already taken
    @Test
    void register_success_EmailAlreadyTaken() {
        String firstName = "Alex";
        String lastName = "Kim";
        String takenEmail = "taken@example.com";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String password = "password123";
        String passwordConfirmation = "password123";
        String hashedPassword = "hashedPassword123";

        User existingUser = mock(User.class);
        User createdUser = mock(User.class);

        try (
                MockedStatic<BankScanner> scannerMock = Mockito.mockStatic(BankScanner.class);
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class);
                MockedStatic<PasswordService> passwordServiceMock = Mockito.mockStatic(PasswordService.class);
                MockedStatic<UserService> userServiceMock = Mockito.mockStatic(UserService.class);
                MockedStatic<AccountCreation> accountCreationMock = Mockito.mockStatic(AccountCreation.class)
        ) {
            scannerMock.when(BankScanner::getString).thenReturn(
                    firstName,
                    lastName,
                    takenEmail,
                    email,
                    phoneNumber,
                    username,
                    password,
                    passwordConfirmation
            );

            userRepositoryMock.when(() -> UserRepository.getUserByEmail(takenEmail)).thenReturn(existingUser);
            userRepositoryMock.when(() -> UserRepository.getUserByEmail(email)).thenReturn(null);
            userRepositoryMock.when(() -> UserRepository.getUser(username)).thenReturn(null);
            passwordServiceMock.when(() -> PasswordService.hashPassword(password)).thenReturn(hashedPassword);
            userServiceMock.when(() -> UserService.createUser(
                    firstName, lastName, email, phoneNumber, username, hashedPassword
            )).thenReturn(createdUser);

            Register.register();

            userServiceMock.verify(() -> UserService.createUser(
                    eq(firstName), eq(lastName), eq(email), eq(phoneNumber), eq(username), eq(hashedPassword)
            ), times(1));

            accountCreationMock.verify(() -> AccountCreation.createAccount(createdUser), times(1));
        }
    }

    // Phone number contains invalid characters
    @Test
    void register_success_InvalidPhoneNumber() {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String invalidPhoneNumber = "invalid-phone!";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String password = "password123";
        String passwordConfirmation = "password123";
        String hashedPassword = "hashedPassword123";

        User createdUser = mock(User.class);

        try (
                MockedStatic<BankScanner> scannerMock = Mockito.mockStatic(BankScanner.class);
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class);
                MockedStatic<PasswordService> passwordServiceMock = Mockito.mockStatic(PasswordService.class);
                MockedStatic<UserService> userServiceMock = Mockito.mockStatic(UserService.class);
                MockedStatic<AccountCreation> accountCreationMock = Mockito.mockStatic(AccountCreation.class)
        ) {
            scannerMock.when(BankScanner::getString).thenReturn(
                    firstName,
                    lastName,
                    email,
                    invalidPhoneNumber,
                    phoneNumber,
                    username,
                    password,
                    passwordConfirmation
            );

            userRepositoryMock.when(() -> UserRepository.getUser(username)).thenReturn(null);
            passwordServiceMock.when(() -> PasswordService.hashPassword(password)).thenReturn(hashedPassword);
            userServiceMock.when(() -> UserService.createUser(
                    firstName, lastName, email, phoneNumber, username, hashedPassword
            )).thenReturn(createdUser);

            Register.register();

            userServiceMock.verify(() -> UserService.createUser(
                    eq(firstName), eq(lastName), eq(email), eq(phoneNumber), eq(username), eq(hashedPassword)
            ), times(1));

            accountCreationMock.verify(() -> AccountCreation.createAccount(createdUser), times(1));
        }
    }

    // Username is under 5 characters
    @Test
    void register_success_UsernameTooShort() {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String invalidUsername = "ab";
        String username = "alexkim";
        String password = "password123";
        String passwordConfirmation = "password123";
        String hashedPassword = "hashedPassword123";

        User createdUser = mock(User.class);

        try (
                MockedStatic<BankScanner> scannerMock = Mockito.mockStatic(BankScanner.class);
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class);
                MockedStatic<PasswordService> passwordServiceMock = Mockito.mockStatic(PasswordService.class);
                MockedStatic<UserService> userServiceMock = Mockito.mockStatic(UserService.class);
                MockedStatic<AccountCreation> accountCreationMock = Mockito.mockStatic(AccountCreation.class)
        ) {
            scannerMock.when(BankScanner::getString).thenReturn(
                    firstName,
                    lastName,
                    email,
                    phoneNumber,
                    invalidUsername,
                    username,
                    password,
                    passwordConfirmation
            );

            userRepositoryMock.when(() -> UserRepository.getUser(username)).thenReturn(null);
            passwordServiceMock.when(() -> PasswordService.hashPassword(password)).thenReturn(hashedPassword);
            userServiceMock.when(() -> UserService.createUser(
                    firstName, lastName, email, phoneNumber, username, hashedPassword
            )).thenReturn(createdUser);

            Register.register();

            userServiceMock.verify(() -> UserService.createUser(
                    eq(firstName), eq(lastName), eq(email), eq(phoneNumber), eq(username), eq(hashedPassword)
            ), times(1));

            accountCreationMock.verify(() -> AccountCreation.createAccount(createdUser), times(1));
        }
    }

    // Username is over 50 characters
    @Test
    void register_success_UsernameTooLong() {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String invalidUsername = "a".repeat(51);
        String username = "alexkim";
        String password = "password123";
        String passwordConfirmation = "password123";
        String hashedPassword = "hashedPassword123";

        User createdUser = mock(User.class);

        try (
                MockedStatic<BankScanner> scannerMock = Mockito.mockStatic(BankScanner.class);
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class);
                MockedStatic<PasswordService> passwordServiceMock = Mockito.mockStatic(PasswordService.class);
                MockedStatic<UserService> userServiceMock = Mockito.mockStatic(UserService.class);
                MockedStatic<AccountCreation> accountCreationMock = Mockito.mockStatic(AccountCreation.class)
        ) {
            scannerMock.when(BankScanner::getString).thenReturn(
                    firstName,
                    lastName,
                    email,
                    phoneNumber,
                    invalidUsername,
                    username,
                    password,
                    passwordConfirmation
            );

            userRepositoryMock.when(() -> UserRepository.getUser(username)).thenReturn(null);
            passwordServiceMock.when(() -> PasswordService.hashPassword(password)).thenReturn(hashedPassword);
            userServiceMock.when(() -> UserService.createUser(
                    firstName, lastName, email, phoneNumber, username, hashedPassword
            )).thenReturn(createdUser);

            Register.register();

            userServiceMock.verify(() -> UserService.createUser(
                    eq(firstName), eq(lastName), eq(email), eq(phoneNumber), eq(username), eq(hashedPassword)
            ), times(1));

            accountCreationMock.verify(() -> AccountCreation.createAccount(createdUser), times(1));
        }
    }

    // Username is already taken
    @Test
    void register_success_UsernameAlreadyTaken() {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String takenUsername = "takenuser";
        String username = "alexkim";
        String password = "password123";
        String passwordConfirmation = "password123";
        String hashedPassword = "hashedPassword123";

        User existingUser = mock(User.class);
        User createdUser = mock(User.class);

        try (
                MockedStatic<BankScanner> scannerMock = Mockito.mockStatic(BankScanner.class);
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class);
                MockedStatic<PasswordService> passwordServiceMock = Mockito.mockStatic(PasswordService.class);
                MockedStatic<UserService> userServiceMock = Mockito.mockStatic(UserService.class);
                MockedStatic<AccountCreation> accountCreationMock = Mockito.mockStatic(AccountCreation.class)
        ) {
            scannerMock.when(BankScanner::getString).thenReturn(
                    firstName,
                    lastName,
                    email,
                    phoneNumber,
                    takenUsername,
                    username,
                    password,
                    passwordConfirmation
            );

            userRepositoryMock.when(() -> UserRepository.getUser(takenUsername)).thenReturn(existingUser);
            userRepositoryMock.when(() -> UserRepository.getUser(username)).thenReturn(null);
            passwordServiceMock.when(() -> PasswordService.hashPassword(password)).thenReturn(hashedPassword);
            userServiceMock.when(() -> UserService.createUser(
                    firstName, lastName, email, phoneNumber, username, hashedPassword
            )).thenReturn(createdUser);

            Register.register();

            userServiceMock.verify(() -> UserService.createUser(
                    eq(firstName), eq(lastName), eq(email), eq(phoneNumber), eq(username), eq(hashedPassword)
            ), times(1));

            accountCreationMock.verify(() -> AccountCreation.createAccount(createdUser), times(1));
        }
    }

    // Password is under 8 characters
    @Test
    void register_success_PasswordTooShort() {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String invalidPassword = "short1";
        String password = "password123";
        String passwordConfirmation = "password123";
        String hashedPassword = "hashedPassword123";

        User createdUser = mock(User.class);

        try (
                MockedStatic<BankScanner> scannerMock = Mockito.mockStatic(BankScanner.class);
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class);
                MockedStatic<PasswordService> passwordServiceMock = Mockito.mockStatic(PasswordService.class);
                MockedStatic<UserService> userServiceMock = Mockito.mockStatic(UserService.class);
                MockedStatic<AccountCreation> accountCreationMock = Mockito.mockStatic(AccountCreation.class)
        ) {
            scannerMock.when(BankScanner::getString).thenReturn(
                    firstName,
                    lastName,
                    email,
                    phoneNumber,
                    username,
                    invalidPassword,
                    password,
                    passwordConfirmation
            );

            userRepositoryMock.when(() -> UserRepository.getUser(username)).thenReturn(null);
            passwordServiceMock.when(() -> PasswordService.hashPassword(password)).thenReturn(hashedPassword);
            userServiceMock.when(() -> UserService.createUser(
                    firstName, lastName, email, phoneNumber, username, hashedPassword
            )).thenReturn(createdUser);

            Register.register();

            userServiceMock.verify(() -> UserService.createUser(
                    eq(firstName), eq(lastName), eq(email), eq(phoneNumber), eq(username), eq(hashedPassword)
            ), times(1));

            accountCreationMock.verify(() -> AccountCreation.createAccount(createdUser), times(1));
        }
    }

    // Password confirmation does not match
    @Test
    void register_success_PasswordConfirmationMismatch() {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String password = "password123";
        String mismatchedConfirmation = "wrongPassword";
        String passwordConfirmation = "password123";
        String hashedPassword = "hashedPassword123";

        User createdUser = mock(User.class);

        try (
                MockedStatic<BankScanner> scannerMock = Mockito.mockStatic(BankScanner.class);
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class);
                MockedStatic<PasswordService> passwordServiceMock = Mockito.mockStatic(PasswordService.class);
                MockedStatic<UserService> userServiceMock = Mockito.mockStatic(UserService.class);
                MockedStatic<AccountCreation> accountCreationMock = Mockito.mockStatic(AccountCreation.class)
        ) {
            scannerMock.when(BankScanner::getString).thenReturn(
                    firstName,
                    lastName,
                    email,
                    phoneNumber,
                    username,
                    password,
                    mismatchedConfirmation,
                    passwordConfirmation
            );

            userRepositoryMock.when(() -> UserRepository.getUser(username)).thenReturn(null);
            passwordServiceMock.when(() -> PasswordService.hashPassword(password)).thenReturn(hashedPassword);
            userServiceMock.when(() -> UserService.createUser(
                    firstName, lastName, email, phoneNumber, username, hashedPassword
            )).thenReturn(createdUser);

            Register.register();

            userServiceMock.verify(() -> UserService.createUser(
                    eq(firstName), eq(lastName), eq(email), eq(phoneNumber), eq(username), eq(hashedPassword)
            ), times(1));

            accountCreationMock.verify(() -> AccountCreation.createAccount(createdUser), times(1));
        }
    }

    /* --- Failed Test Cases --- */

    // UserService fails to create the user
    @Test
    void register_fail_userServiceReturnsNull() {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String password = "password123";
        String passwordConfirmation = "password123";
        String hashedPassword = "hashedPassword123";

        try (
                MockedStatic<BankScanner> scannerMock = Mockito.mockStatic(BankScanner.class);
                MockedStatic<UserRepository> userRepositoryMock = Mockito.mockStatic(UserRepository.class);
                MockedStatic<PasswordService> passwordServiceMock = Mockito.mockStatic(PasswordService.class);
                MockedStatic<UserService> userServiceMock = Mockito.mockStatic(UserService.class);
                MockedStatic<AccountCreation> accountCreationMock = Mockito.mockStatic(AccountCreation.class)
        ) {
            scannerMock.when(BankScanner::getString).thenReturn(
                    firstName,
                    lastName,
                    email,
                    phoneNumber,
                    username,
                    password,
                    passwordConfirmation
            );

            userRepositoryMock.when(() -> UserRepository.getUser(username)).thenReturn(null);
            passwordServiceMock.when(() -> PasswordService.hashPassword(password)).thenReturn(hashedPassword);
            userServiceMock.when(() -> UserService.createUser(
                    firstName, lastName, email, phoneNumber, username, hashedPassword
            )).thenReturn(null);

            Register.register();

            userServiceMock.verify(() -> UserService.createUser(
                    eq(firstName), eq(lastName), eq(email), eq(phoneNumber), eq(username), eq(hashedPassword)
            ), times(1));

            accountCreationMock.verify(() -> AccountCreation.createAccount(any(User.class)), never());
        }
    }
}
