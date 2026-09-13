package org.half.view;

import org.half.utility.BankScanner;
import org.half.repository.UserRepository;
import org.half.model.User;
import org.half.security.PasswordService;

public class Register {
    /*
        - First Name
            - Must be 20 characters or fewer
        - Last Name
            - Must be 20 characters or fewer
        - Email
            - Confirm email
            - Must follow valid format
        - Phone Number
            - Must follow valid format
        - Username
        - Password
            - 8 characters minimum
            - User must Confirm Password
    */
    public static void register() {
        // Name
        System.out.print("First name: ");
        String firstName = BankScanner.getString();
        while (firstName.length() > 20) {
            System.out.println("First name must be 20 characters or fewer.");
            System.out.print("First name: ");
            firstName = BankScanner.getString();
        }

        System.out.print("Last name: ");
        String lastName = BankScanner.getString();
        while (lastName.length() > 20) {
            System.out.println("Last name must be 20 characters or fewer.");
            System.out.print("Last name: ");
            lastName = BankScanner.getString();
        }

        // Email
        System.out.print("Email: ");
        String email = BankScanner.getString();
        while (!isValidEmail(email)) {
            System.out.println("Please enter a valid email address.");
            System.out.print("Email: ");
            email = BankScanner.getString();
        }

        // Phone Number
        String phoneNumberTry;
        do {
            System.out.print("Phone Number: ");
            phoneNumberTry = BankScanner.getString();
        } while (!isValidPhoneNumber(phoneNumberTry));
        String phoneNumber = phoneNumberTry;

        // Username
        System.out.print("Username: ");
        String username = BankScanner.getString();
        while (username.length() < 5 || username.length() > 50 
        /*|| UserRepository.getUser(username) != null*/
        ) {
            if (username.length() < 5 || username.length() > 50) {
                System.out.println("Username must be between 5 and 50 characters.");
            } else {
                System.out.println("That username is already taken.");
            }
            System.out.print("Username: ");
            username = BankScanner.getString();
        }

        // Password
        String password;
        do {
            System.out.print("Password (min. 8 character): ");
            password = BankScanner.getString();
            if(password.length() < 8) {
                System.out.println("Password must be at least 8 characters long.");
            }
        } while (password.length() < 8);

        String passwordConfirmation;
        do {
            System.out.print("Confirm password: ");
            passwordConfirmation = BankScanner.getString();
            if(!passwordConfirmation.equals(password)) {
                System.out.println("Passwords do not match. Please try again.");
            }
        } while (!passwordConfirmation.equals(password));
        
        // Add user to repository, confirmation, and redirect to SignIn
        User user = UserService.createUser(firstName, lastName, email, phoneNumber, username, PasswordService.hashPassword(password));
        UserRepository.addUser(user);

        AccountCreation.createAccount(user);

        System.out.println("Registration successful. Welcome to Fifty/50 Bank, " + firstName + "!");
    }

    /* --- Helper Methods --- */
    private static boolean isValidEmail(String email) {
        return email.matches(".*@.*\\..*");
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        if (!phoneNumber.matches("[0-9+()\\- ]+")) {
            System.out.println("Please enter a VALID phone number (digits, +, (), -, spaces only).");
            return false;
        }
        return true;
    }
}
