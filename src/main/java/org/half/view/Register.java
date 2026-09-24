package org.half.view;

import org.half.model.RouteModel;
import org.half.model.User;
import org.half.repository.UserRepository;
import org.half.security.PasswordService;
import org.half.service.UserService;
import org.half.style.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.williamcallahan.tui4j.compat.bubbletea.Command;
import com.williamcallahan.tui4j.compat.bubbletea.Message;
import com.williamcallahan.tui4j.compat.bubbletea.Model;
import com.williamcallahan.tui4j.compat.bubbletea.UpdateResult;
import com.williamcallahan.tui4j.compat.bubbletea.KeyPressMessage;

public class Register implements Model {
    private static final Logger log = LoggerFactory.getLogger(Register.class);

    private static final UserRepository userRepository = new UserRepository();
    private static final UserService userService = new UserService(userRepository);

    private final long startTime = System.currentTimeMillis();

    private final String[] fieldNames = {
            "First Name", "Last Name", "Email", "Phone Number", "Username", "Password", "Confirm Password"
    };

    // Tracks the user's typing for each specific field
    private final String[] inputs = {"", "", "", "", "", "", ""};

    private int activeIndex = 0;
    private String errorMessage = "";

    @Override
    public Command init() { return null; }

    @Override
    public UpdateResult<? extends Model> update(Message msg) {
        if (System.currentTimeMillis() - startTime < 150) {
            return UpdateResult.from(this);
        }

        if (msg instanceof KeyPressMessage keyPressMessage) {
            String key = keyPressMessage.key();

            if (key.equals("esc")) {
                // Send RouteModel to MasterControl to swap back to WelcomeMenu
                return UpdateResult.from(this, () -> new RouteModel(RouteModel.Route.WELCOME, null));
            } else if (key.equals("tab") || key.equals("down")) {
                activeIndex = (activeIndex + 1) % fieldNames.length;
                errorMessage = "";
            } else if (key.equals("shift+tab") || key.equals("up")) {
                activeIndex = (activeIndex - 1 < 0) ? fieldNames.length - 1 : activeIndex - 1;
                errorMessage = "";
            } else if (key.equals("enter")) {
                if (activeIndex == fieldNames.length - 1) {
                    return validateAndSubmit();
                } else {
                    activeIndex++; // Enter moves to the next field until the last one
                }
            } else if (key.equals("backspace") || key.equals("ctrl+h") || key.equals("delete") || key.equals("\b")) {
                if (!inputs[activeIndex].isEmpty()) {
                    inputs[activeIndex] = inputs[activeIndex].substring(0, inputs[activeIndex].length() - 1);
                }
            } else if (key.length() == 1) {
                inputs[activeIndex] += key; // Append typed character
            }
        }
        return UpdateResult.from(this);
    }

    private UpdateResult<? extends Model> validateAndSubmit() {
        // 1. First Name Validation
        if (inputs[0].isBlank() || inputs[0].length() > 20) {
            errorMessage = "First name must be 1-20 characters.";
            if(inputs[0].isBlank()) {
                log.warn("No first name was entered.");
            }
            if(inputs[0].length() > 20) {
                log.warn("First name is too long");
            }
            activeIndex = 0; return UpdateResult.from(this);
        }
        log.info("First name validated successfully");
        
        // 2. Last Name Validation
        if (inputs[1].isBlank() || inputs[1].length() > 20) {
            errorMessage = "Last name must be 1-20 characters.";
            if(inputs[1].isBlank()) {
                log.warn("No last name was entered.");
            }
            if(inputs[1].length() > 20) {
                log.warn("Last name is too long: {}", inputs[1]);
            }
            activeIndex = 1; return UpdateResult.from(this);
        }
        log.info("Last name validated successfully");

        // 3. Email Validation
        if (!inputs[2].matches(".*@.*\\..*") || userRepository.getUserByEmail(inputs[2]) != null) {
            errorMessage = "Invalid email or already in use.";
            if(!inputs[2].matches(".*@.*\\..*")) {
                log.warn("Invalid email format: {}", inputs[2]);
            }
            if(userRepository.getUserByEmail(inputs[2]) != null) {
                log.warn("Email already in use: {}", inputs[2]);
            }
            activeIndex = 2; return UpdateResult.from(this);
        }
        log.info("Email validated successfully");

        // 4. Phone Validation
        if (!inputs[3].matches("[0-9+()\\- ]+")) {
            errorMessage = "Invalid phone number format.";
            log.warn("Invalid phone number format: {}", inputs[3]);
            activeIndex = 3; return UpdateResult.from(this);
        }
        log.info("Phone number validated successfully");

        // 5. Username Validation
        if (inputs[4].length() < 5 || inputs[4].length() > 50 || userRepository.getUser(inputs[4]) != null) {
            errorMessage = "Username must be 5-50 chars and unique.";
            if(inputs[4].length() < 5) {
                log.warn("Username too short: {}", inputs[4]);
            }
            if(inputs[4].length() > 50) {
                log.warn("Username too long: {}", inputs[4]);
            }
            if(userRepository.getUser(inputs[4]) != null) {
                log.warn("Username already in use: {}", inputs[4]);
            }
            activeIndex = 4; return UpdateResult.from(this);
        }
        log.info("Username validated successfully");

        // 6. Password Validation
        if (inputs[5].length() < 8) {
            errorMessage = "Password must be at least 8 characters.";
            log.warn("Password too short");
            activeIndex = 5; return UpdateResult.from(this);
        }
        log.info("Password validated successfully");
        if (!inputs[6].equals(inputs[5])) {
            errorMessage = "Passwords do not match.";
            log.warn("Passwords do not match");
            activeIndex = 6; return UpdateResult.from(this);
        }
        log.info("Password confirmation validated successfully");

        // All checks passed
        User registeredUser = userService.createUser(
                inputs[0], inputs[1], inputs[2], inputs[3], inputs[4], PasswordService.hashPassword(inputs[5])
        );

        log.info("User registration successful for user: {}", registeredUser.getUsername());

        // Send RouteModel to MasterControl to swap to AccountCreation and pass the user
        return UpdateResult.from(this, () -> new RouteModel(RouteModel.Route.WELCOME, registeredUser));
    }

    @Override
    public String view() {
        StringBuilder content = new StringBuilder();
        content.append(Theme.TITLE.render("Create a new Profile")).append("\n\n");

        for (int i = 0; i < fieldNames.length; i++) {
            String displayString = inputs[i];

            // Mask password fields
            if (i == 5 || i == 6) {
                displayString = "*".repeat(inputs[i].length());
            }

            if (i == activeIndex) {
                content.append(Theme.ACTIVE_ITEM_INPUT.render("▶ " + String.format("%-18s", fieldNames[i] + ":")))
                        .append(" ")
                        .append(Theme.TITLE.render(displayString))
                        .append(Theme.TEXT_CURSOR.render("█")).append("\n");
            } else {
                content.append("  ").append(String.format("%-18s", fieldNames[i] + ":"))
                        .append(" ").append(displayString).append("\n");
            }
        }

        content.append("\n");
        if (!errorMessage.isEmpty()) {
            content.append(Theme.ERROR_TEXT.render("⚠ " + errorMessage)).append("\n\n");
        } else {
            content.append(Theme.FOOTER_TEXT.render("[Tab] switch fields • [Enter] submit • [Esc] cancel")).append("\n\n");
        }

        return Theme.MAIN_PANEL.render(content.toString());
    }
}
