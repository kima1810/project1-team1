package org.half.view;

import org.half.model.RouteModel;
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

public class WelcomeMenu implements Model {
    private static final Logger log = LoggerFactory.getLogger(WelcomeMenu.class);

    private final String[] CHOICES = {
            "Sign In",
            "Create a New User Account",
            "Exit Bank"
    };

    private int cursor = 0;

    @Override
    public Command init() { return null; }

    @Override
    public UpdateResult<? extends Model> update(Message msg) {
        if (msg instanceof KeyPressMessage keyPressMessage) {
            return switch (keyPressMessage.key()) {
                case "k", "K", "up" -> {
                    cursor = (cursor - 1 < 0) ? CHOICES.length - 1 : cursor - 1;
                    yield UpdateResult.from(this);
                }
                case "j", "J", "down" -> {
                    cursor = (cursor + 1 >= CHOICES.length) ? 0 : cursor + 1;
                    yield UpdateResult.from(this);
                }
                case "enter" -> {
                    if (cursor == 0) {
                        yield UpdateResult.from(this, () -> new RouteModel(RouteModel.Route.SIGN_IN, null));
                    } else if (cursor == 1) {
                        yield UpdateResult.from(this, () -> new RouteModel(RouteModel.Route.REGISTER, null));
                    } else {
                        yield UpdateResult.from(this, () -> new RouteModel(RouteModel.Route.EXIT, null));
                    }
                }
                case "q", "Q" -> {
                    yield UpdateResult.from(this, () -> new RouteModel(RouteModel.Route.EXIT, null));
                }
                default -> UpdateResult.from(this);
            };
        }
        return UpdateResult.from(this);
    }

    @Override
    public String view() {
        StringBuilder buffer = new StringBuilder();

        // ASCII Logo
        buffer.append(Theme.LOGO.render(
                " /$$$$$$$$ /$$  /$$$$$$   /$$                     /$$ /$$$$$$$   /$$$$$$        /$$$$$$$                      /$$      \n" +
                        "| $$_____/|__/ /$$__  $$ | $$                    /$$/| $$____/  /$$$_  $$      | $$__  $$                    | $$      \n" +
                        "| $$       /$$| $$  \\__//$$$$$$   /$$   /$$     /$$/ | $$      | $$$$\\ $$      | $$  \\ $$  /$$$$$$  /$$$$$$$ | $$   /$$\n" +
                        "| $$$$$   | $$| $$$$   |_  $$_/  | $$  | $$    /$$/  | $$$$$$$ | $$ $$ $$      | $$$$$$$  |____  $$| $$__  $$| $$  /$$/\n" +
                        "| $$__/   | $$| $$_/     | $$    | $$  | $$   /$$/   |_____  $$| $$\\ $$$$      | $$__  $$  /$$$$$$$| $$  \\ $$| $$$$$$/ \n" +
                        "| $$      | $$| $$       | $$ /$$| $$  | $$  /$$/     /$$  \\ $$| $$ \\ $$$      | $$  \\ $$ /$$__  $$| $$  | $$| $$_  $$ \n" +
                        "| $$      | $$| $$       |  $$$$/|  $$$$$$$ /$$/     |  $$$$$$/|  $$$$$$/      | $$$$$$$/|  $$$$$$$| $$  | $$| $$ \\  $$\n" +
                        "|__/      |__/|__/        \\___/   \\____  $$|__/       \\______/  \\______/       |_______/  \\_______/|__/  |__/|__/  \\__/\n" +
                        "                                  /$$  | $$                                                                            \n" +
                        "                                 |  $$$$$$/                                                                            \n" +
                        "                                  \\______/                                                                             "
        ));

        buffer.append("\n");
        buffer.append(Theme.TITLE.render("Welcome to Bank 50"));
        buffer.append("\n");

        for (int i = 0; i < CHOICES.length; i++) {
            if (cursor == i) {
                buffer.append(Theme.ACTIVE_ITEM_SELECT.render("▶ " + CHOICES[i])).append("\n");
            } else {
                if (i == 2) {
                    buffer.append(Style.newStyle().foreground(Color.color("203")).render("  " + CHOICES[i])).append("\n");
                } else {
                    buffer.append("  ").append(CHOICES[i]).append("\n");
                }
            }
        }
        return Theme.MAIN_PANEL.render(buffer.toString());
    }
}
