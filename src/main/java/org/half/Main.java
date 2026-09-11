package org.half;

import org.half.repository.UserRepository;
import org.half.utility.BankScanner;
import org.half.view.SignIn;

public class Main {
    public static void main(String[] args) {
        SignIn.signIn();
        BankScanner.closeScanner();
    }
}