package org.half;

import org.half.utility.BankScanner;
import org.half.view.MainMenu;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to Bank 50!");

        MainMenu.mainMenu();
        BankScanner.closeScanner();
    }
}