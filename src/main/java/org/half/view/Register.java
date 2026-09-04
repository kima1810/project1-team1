package org.half.view;

import org.half.utility.BankScanner;
import org.half.repository.UserRepository;
import org.half.model.User;

public class Register {
    /*
        - First Name
        - Last Name
        - Email
            - Confirm email
        - Phone Number
        - Username
        - Password
            - 8 characters minimum
            - User must Confirm Password
    */
    public static void register() {
        // Name
        System.out.print("First name: ");
        String firstName = BankScanner.getString();

        System.out.print("Last name: ");
        String lastName = BankScanner.getString();

        // Email
        System.out.print("Email: ");
        String email = BankScanner.getString();

        String emailConfirmation = "";
        do {
            System.out.print("Confirm email");
            emailConfirmation = BankScanner.getString();
            if(!emailConfirmation.equals(email)) {
                System.out.println("Emails do not match. Please try again.");
            }
        } while (!emailConfirmation.equals(email));

        // Phone Number
        String phoneNumberTry;
        do {
            System.out.print("Phone Number: ");
            phoneNumberTry = BankScanner.getString();
        } while (isValidPhoneNumber(phoneNumberTry));
        int phoneNumber = Integer.parseInt(phoneNumberTry);

        // Username
        System.out.print("Username: ");
        String username = BankScanner.getString();

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
        UserRepository.addUser(new User(firstName, lastName, email, phoneNumber, username, password, 0.0));
        System.out.println("Registration successful. Welcome to Bank 50 " + firstName + "!");
        SignIn.signIn();
    }

    // Helper Methods
    private static boolean isValidPhoneNumber(String phoneNumber) {
        try {
            Integer.parseInt(phoneNumber);
            if(phoneNumber.length() < 10) {
                System.out.println("Please enter a VALID phone number.");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Invalid phone number. Please enter DIGITS ONLY.");
            return false;
        }
    }
}
