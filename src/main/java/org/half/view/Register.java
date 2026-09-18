package org.half.view;
import org.half.utility.ANSI;
import org.half.utility.BankScanner;
import org.half.repository.UserRepository;
import org.half.model.User;
import org.half.security.PasswordService;
import org.half.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Register {
    private static final Logger log = LoggerFactory.getLogger(Register.class);

    private static final UserRepository userRepository = new UserRepository();
    private static final UserService userService = new UserService(userRepository);

    /*
        - First Name
            - Must be 20 characters or fewer
        - Last Name
            - Must be 20 characters or fewer
        - Email
            - Confirm email
            - Must follow valid format
            - Unique
        - Phone Number
            - Must follow valid format
        - Username
            - Unique
            - Must be between 5 and 50 characters
        - Password
            - 8 characters minimum
            - User must Confirm Password
    */
    public static void register() {
        // Creating new user account title
        System.out.println(ANSI.RESET + "\n" + ANSI.rgb(255, 255, 100) +
                "Let's create your profile..." +
                ANSI.RESET);

        // Name
        System.out.print(ANSI.rgb(100, 255, 255) + "First name: " + ANSI.RESET);
        String firstName = BankScanner.getString();
        while (firstName.isBlank() || firstName.length() > 20) {
            if (firstName.isBlank()) {
                System.out.println(ANSI.userWarning("First name cannot be empty."));
                log.warn("First name entered is blank");
            } else {
                System.out.println(ANSI.userWarning("First name must be 20 characters or fewer."));
                log.warn("First name entered is too long: {}", firstName);
            }
            System.out.print(ANSI.rgb(100, 255, 255) + "First name: " + ANSI.RESET);
            firstName = BankScanner.getString();
        }
        log.info("User entered first name: {}", firstName);

        System.out.print(ANSI.rgb(100, 255, 255) + "Last name: " + ANSI.RESET);
        String lastName = BankScanner.getString();
        while (lastName.isBlank() || lastName.length() > 20) {
            if (lastName.isBlank()) {
                System.out.println(ANSI.userWarning("Last name cannot be empty."));
                log.warn("Last name entered is blank");
            } else {
                System.out.println(ANSI.userWarning("Last name must be 20 characters or fewer."));
                log.warn("Last name entered is too long: {}", lastName);
            }
            System.out.print(ANSI.rgb(100, 255, 255) + "Last name: " + ANSI.RESET);
            lastName = BankScanner.getString();
        }
        log.info("User entered last name: {}", lastName);

        // Email
        System.out.print(ANSI.rgb(100, 255, 255) + "Email: " + ANSI.RESET);
        String email = BankScanner.getString();
        while (!isValidEmail(email) || userRepository.getUserByEmail(email) != null) {
            if (!isValidEmail(email)) {
                System.out.println(ANSI.userWarning("Please enter a valid email address."));
                log.warn("Invalid email entered: {}", email);
            } else {
                System.out.println(ANSI.userWarning("An account with that email already exists."));
                log.warn("Email already in use: {}", email);
            }
            System.out.print(ANSI.rgb(100, 255, 255) + "Email: " + ANSI.RESET);
            email = BankScanner.getString();
        }
        log.info("User entered email: {}", email);

        // Phone Number
        String phoneNumberTry;
        do {
            System.out.print(ANSI.rgb(100, 255, 255) + "Phone Number: " + ANSI.RESET);
            phoneNumberTry = BankScanner.getString();
        } while (!isValidPhoneNumber(phoneNumberTry));
        log.info("User entered phone number: {}", phoneNumberTry);
        String phoneNumber = phoneNumberTry;

        // Username
        System.out.print(ANSI.rgb(100, 255, 255) + "Username: " + ANSI.RESET);
        String username = BankScanner.getString();
        while (username.length() < 5 || username.length() > 50 || userRepository.getUser(username) != null) {
            if (username.length() < 5 || username.length() > 50) {
                System.out.println(ANSI.userWarning("Username must be between 5 and 50 characters."));
                log.warn("Username entered is invalid: {}", username);
            } else {
                System.out.println(ANSI.userWarning("That username is already taken."));
                log.warn("Username already taken: {}", username);
            }
            System.out.print(ANSI.rgb(100, 255, 255) + "Username: " + ANSI.RESET);
            username = BankScanner.getString();
        }
        log.info("User entered username: {}", username);

        // Password
        String password;
        do {
            System.out.print(ANSI.rgb(100, 255, 255) + "Password (min. 8 character): " + ANSI.RESET);
            password = BankScanner.getString();
            if(password.length() < 8) {
                System.out.println(ANSI.userWarning("Password must be at least 8 characters long."));
                log.warn("Password entered is too short");
            }
        } while (password.length() < 8);

        String passwordConfirmation;
        do {
            System.out.print(ANSI.rgb(100, 255, 255) + "Confirm password: " + ANSI.RESET);
            passwordConfirmation = BankScanner.getString();
            if(!passwordConfirmation.equals(password)) {
                System.out.println(ANSI.userWarning("Passwords do not match. Please try again."));
                log.warn("Password confirmation does not match the password");
            }
        } while (!passwordConfirmation.equals(password));
        log.info("User successfully set password");
        
        // Add user to repository, confirmation, and redirect to SignIn
        User user = userService.createUser(firstName, lastName, email, phoneNumber, username, PasswordService.hashPassword(password));
        if (user == null) {
            System.out.println("Registration failed. Please try again.");
            log.error("User registration failed for username: {}", username);
            return;
        }

        AccountCreation.createAccount(user);

        System.out.println(ANSI.success("Registration successful. Welcome, ") +
            ANSI.rgb(100, 255, 255) + firstName + ANSI.rgb(100, 255, 100) + "!");
        log.info("User registration successful for user: {}", username);
    }

    /* --- Helper Methods --- */
    private static boolean isValidEmail(String email) {
        return email.matches(".*@.*\\..*");
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        if (!phoneNumber.matches("[0-9+()\\- ]+")) {
            System.out.println(ANSI.userWarning("Please enter a VALID phone number (digits, +, (), -, spaces only)."));
            log.warn("Invalid phone number entered: {}", phoneNumber);
            return false;
        }
        return true;
    }
}
