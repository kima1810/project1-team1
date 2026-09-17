package org.half;

import org.half.utility.BankScanner;
import org.half.utility.PromptUserSelection;
import org.half.view.SignIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Bank application started");
        SignIn.signIn();
        BankScanner.closeScanner();
        log.info("Bank application shutting down");
    }
}