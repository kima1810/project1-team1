package org.half.utility;

import org.half.view.MainMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PromptUserSelection {
    private static final Logger log = LoggerFactory.getLogger(PromptUserSelection.class);

    public static int promptUserSelection () {
        int optionSelected;
        while (true) {
            try {
                String userInput = BankScanner.getString().trim();
                optionSelected = Integer.parseInt(userInput);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Not a number. Please try again...");
                log.warn("Not a number. Please try again...");
                log.error("This should not be a error.");
            }

        }

        return optionSelected;
    }
}
