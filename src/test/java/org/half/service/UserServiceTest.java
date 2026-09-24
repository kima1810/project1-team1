package org.half.service;

import org.half.exceptions.EmptyStringException;
import org.half.exceptions.UserAlreadyExists;
import org.half.model.User;
import org.half.repository.UserRepository;
import org.half.security.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp(){
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }
    /* --- Successful Test Cases --- */

    // User is created successfully
    @Test
    void createUser_success_allValid() throws UserAlreadyExists {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String hashedPassword = "hashedPassword123";

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getUserByEmail(email)).thenReturn(null);
        UserService userService = new UserService(userRepository);

        User createdUser = userService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

        assertNotNull(createdUser);
        assertEquals(firstName, createdUser.getFirstName());
        assertEquals(lastName, createdUser.getLastName());
        assertEquals(email, createdUser.getEmail());
        assertEquals(phoneNumber, createdUser.getPhoneNumber());
        assertEquals(username, createdUser.getUsername());
        assertEquals(hashedPassword, createdUser.getPassword());

        verify(userRepository, times(1)).addUser(any(User.class));
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

        UserService userService = new UserService(mock(UserRepository.class));
        User createdUser = userService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

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

        UserService userService = new UserService(mock(UserRepository.class));
        User createdUser = userService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

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

        UserService userService = new UserService(mock(UserRepository.class));
        User createdUser = userService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

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

        UserService userService = new UserService(mock(UserRepository.class));
        User createdUser = userService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

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

        UserService userService = new UserService(mock(UserRepository.class));
        User createdUser = userService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

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

        UserService userService = new UserService(mock(UserRepository.class));
        User createdUser = userService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

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

        UserService userService = new UserService(mock(UserRepository.class));
        User createdUser = userService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

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

        UserService userService = new UserService(mock(UserRepository.class));
        User createdUser = userService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

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

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getUserByEmail(email)).thenReturn(existingUser);
        UserService userService = new UserService(userRepository);

        User createdUser = userService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

        assertNull(createdUser);
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

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getUserByEmail(email)).thenReturn(null);
        UserService userService = new UserService(userRepository);

        User createdUser = userService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

        assertNull(createdUser);
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

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getUserByEmail(email)).thenReturn(null);
        UserService userService = new UserService(userRepository);

        User createdUser = userService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

        assertNull(createdUser);
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

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getUserByEmail(email)).thenReturn(null);
        UserService userService = new UserService(userRepository);

        User createdUser = userService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

        assertNull(createdUser);
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

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getUserByEmail(email)).thenReturn(null);
        UserService userService = new UserService(userRepository);

        User createdUser = userService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

        assertNull(createdUser);
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

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getUserByEmail(email)).thenReturn(null);
        UserService userService = new UserService(userRepository);

        User createdUser = userService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

        assertNull(createdUser);
    }

    // Username is already taken
    @Test
    void createUser_failure_usernameAlreadyExist() throws UserAlreadyExists {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String hashedPassword = "hashedPassword123";

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getUserByEmail(email)).thenReturn(null);
        doThrow(new UserAlreadyExists("Username '" + username + "' is already taken."))
                .when(userRepository).addUser(any(User.class));
        UserService userService = new UserService(userRepository);

        User createdUser = userService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

        assertNull(createdUser);
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

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getUserByEmail(email)).thenReturn(null);
        UserService userService = new UserService(userRepository);

        User createdUser = userService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

        assertNull(createdUser);
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

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getUserByEmail(email)).thenReturn(null);
        UserService userService = new UserService(userRepository);

        User createdUser = userService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

        assertNull(createdUser);
    }

    // Repository failure
    @Test
    void createUser_failure_repositoryLevelFailure() throws UserAlreadyExists {
        String firstName = "Alex";
        String lastName = "Kim";
        String email = "alex.kim@example.com";
        String phoneNumber = "555-123-4567";
        String username = "alexkim";
        String hashedPassword = "hashedPassword123";

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getUserByEmail(email)).thenReturn(null);
        doThrow(new UserAlreadyExists("Unable to create user."))
                .when(userRepository).addUser(any(User.class));
        UserService userService = new UserService(userRepository);

        User createdUser = userService.createUser(firstName, lastName, email, phoneNumber, username, hashedPassword);

        assertNull(createdUser);
    }

    @Test
    void verifyUser_enteredEmptyUsername_shouldThrowException(){
        EmptyStringException exception = assertThrows(
                EmptyStringException.class,
                () -> userService.verifyUser("", "password")
        );

        assertEquals("Empty String(s) passed to verifyUser.",
                exception.getMessage()
        );

        verify(userRepository, never()).getPasswordHash("");
    }

    @Test
    void verifyUser_enteredEmptyPassword_shouldThrowException(){
        EmptyStringException exception = assertThrows(
                EmptyStringException.class,
                () -> userService.verifyUser("UserAccount", "")
        );

        assertEquals("Empty String(s) passed to verifyUser.",
                exception.getMessage()
        );

        verify(userRepository, never()).getPasswordHash("UserAccount");
    }

    @Test
    void verifyUser_userDoesNotExist_returnsNull(){
        when(userRepository.getPasswordHash("1234567"))
                .thenReturn(null);

        User activeUser = userService.verifyUser("1234567", "passwordtest");

        assertNull(activeUser);
        verify(userRepository, times(1)).getPasswordHash("1234567");
        verify(userRepository, never()).getUser(anyString());
    }

    @Test
    void verifyUser_userExistAndPasswordIsIncorrect_returnsNull(){
        MockedStatic<PasswordService> passwordServiceMocked = mockStatic(PasswordService.class);
        when(userRepository.getPasswordHash("UserAccount"))
            .thenReturn("hashedPassword");

        passwordServiceMocked
                .when(() -> PasswordService.verifyPassword(
                        "wrongPassword",
                        "hashedPassword"))
                .thenReturn(false);

        User activeUser = userService.verifyUser("UserAccount", "wrongPassword");

        assertNull(activeUser);
        verify(userRepository, times(1)).getPasswordHash("UserAccount");
        verify(userRepository, never()).getUser("UserAccount");

        passwordServiceMocked.close();
    }

    @Test
    void verifyUser_userExistsAndPasswordIsCorrect_returnsUser(){
        MockedStatic<PasswordService> passwordServiceMocked = mockStatic(PasswordService.class);
        User expectedUser = mock(User.class);

        when(userRepository.getPasswordHash("UserAccount"))
                .thenReturn("hashedPassword");

        passwordServiceMocked
                .when(() -> PasswordService.verifyPassword(
                        "correctPassword",
                        "hashedPassword"))
                .thenReturn(true);

        when(userRepository.getUser("UserAccount"))
                .thenReturn(expectedUser);

        User activeUser = userService.verifyUser("UserAccount", "correctPassword");

        assertNotNull(activeUser);
        assertEquals(expectedUser, activeUser);
        verify(userRepository, times(1)).getPasswordHash("UserAccount");
        verify(userRepository, times(1)).getUser("UserAccount");

        passwordServiceMocked.verify(
                () -> PasswordService.verifyPassword(
                        "correctPassword",
                        "hashedPassword"
                )
        );

        passwordServiceMocked.close();
    }
}
