package org.half.view;

import org.half.model.RouteModel;
import org.half.model.User;
import org.half.security.SignInService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.half.style.Theme;

import com.williamcallahan.tui4j.compat.bubbletea.Command;
import com.williamcallahan.tui4j.compat.bubbletea.Message;
import com.williamcallahan.tui4j.compat.bubbletea.Model;
import com.williamcallahan.tui4j.compat.bubbletea.UpdateResult;
import com.williamcallahan.tui4j.compat.lipgloss.Style;
import com.williamcallahan.tui4j.compat.lipgloss.color.Color;
import com.williamcallahan.tui4j.compat.bubbletea.KeyPressMessage;

public class SignIn implements Model {
    private static final Logger log = LoggerFactory.getLogger(SignIn.class);

    private final long startTime = System.currentTimeMillis();

    private final String[] inputs = {"", ""};
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
            } else if (key.equals("tab") || key.equals("down") || key.equals("up") || key.equals("shift+tab")) {
                activeIndex = (activeIndex == 0) ? 1 : 0;
                errorMessage = "";
            } else if (key.equals("enter")) {
                if (activeIndex == 1) {
                    return attemptLogin();
                } else {
                    activeIndex = 1;
                }
            } else if (key.equals("backspace") || key.equals("ctrl+h") || key.equals("delete") || key.equals("\b")) {
                if (!inputs[activeIndex].isEmpty()) {
                    inputs[activeIndex] = inputs[activeIndex].substring(0, inputs[activeIndex].length() - 1);
                }
            } else if (key.length() == 1) {
                inputs[activeIndex] += key;
            }
        }
        return UpdateResult.from(this);
    }

    private UpdateResult<? extends Model> attemptLogin() {
        if (inputs[0].isEmpty() || inputs[1].isEmpty()) {
            errorMessage = "Username and password are required.";
            return UpdateResult.from(this);
        }

        User activeUser = SignInService.verifyUser(inputs[0], inputs[1]);
        if (activeUser != null) {
            log.info("User logged in: userId={}", activeUser.getUsername());
            // Success! Send RouteModel to MasterControl to swap to AccountSelection
            return UpdateResult.from(this, () -> new RouteModel(RouteModel.Route.ACCOUNT_SELECTION, activeUser));
        } else {
            errorMessage = "Invalid credentials. Try again.";
            inputs[1] = ""; // Clear password field on failure
            return UpdateResult.from(this);
        }
    }

    @Override
    public String view() {
        StringBuilder content = new StringBuilder();
        content.append(Theme.TITLE.render("Account Sign In")).append("\n\n");

        // Username Field
        if (activeIndex == 0) {
            content.append(Theme.ACTIVE_MENU_ITEM.render("▶ Username: ")).append(Style.newStyle().foreground(Color.color("227")).render(inputs[0])).append(Theme.TEXT_CURSOR.render("█")).append("\n");
        } else {
            content.append("  Username: ").append(inputs[0]).append("\n");
        }

        // Password Field
        String maskedPassword = "*".repeat(inputs[1].length());
        if (activeIndex == 1) {
            content.append(Theme.ACTIVE_MENU_ITEM.render("▶ Password: ")).append(Style.newStyle().foreground(Color.color("227")).render(maskedPassword)).append(Theme.TEXT_CURSOR.render("█")).append("\n");
        } else {
            content.append("  Password: ").append(maskedPassword).append("\n");
        }

        content.append("\n");
        if (!errorMessage.isEmpty()) {
            content.append(Theme.ERROR_TEXT.render("⚠ " + errorMessage)).append("\n\n");
        } else {
            content.append(Style.newStyle().foreground(Color.color("240")).render("[Tab] switch fields • [Enter] login")).append("\n\n");
        }

        return Theme.MAIN_PANEL.render(content.toString());
    }
}
