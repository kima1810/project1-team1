package org.half.view;

import org.half.model.Account;
import org.half.model.RouteModel;
import org.half.model.User;
import org.half.repository.AccountRepository;
import org.half.repository.TransactionModelRepository;
import org.half.service.AccountService;
import org.half.service.TransactionHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.williamcallahan.tui4j.compat.bubbletea.Command;
import com.williamcallahan.tui4j.compat.bubbletea.Message;
import com.williamcallahan.tui4j.compat.bubbletea.Model;
import com.williamcallahan.tui4j.compat.bubbletea.UpdateResult;
import com.williamcallahan.tui4j.compat.lipgloss.Style;
import com.williamcallahan.tui4j.compat.lipgloss.color.Color;
import com.williamcallahan.tui4j.compat.bubbletea.KeyPressMessage;
import com.williamcallahan.tui4j.compat.lipgloss.Borders;

import java.util.List;

public class AccountSelection implements Model {
    private static final Logger log = LoggerFactory.getLogger(AccountSelection.class);

    private static final AccountRepository accountRepository = new AccountRepository();
    private static final TransactionModelRepository transactionModelRepository = new TransactionModelRepository();

    private static final TransactionHistoryService transactionHistoryService = new TransactionHistoryService(transactionModelRepository);

    private static final AccountService accountService = new AccountService(accountRepository, transactionHistoryService);


    // --- State Management ---
    private enum AppState { SELECTING, ENTERING_PIN, ERROR }

    private AppState currentState = AppState.SELECTING;
    private final User user;
    private final List<Account> accounts;

    private int cursor = 0;
    private String pinBuffer = "";
    private Account selectedAccountForPin = null;

    // --- TUI Styling ---
    private final static Style MENU_PANEL = Style.newStyle()
            .border(Borders.roundedBorder()).borderTopForeground(Color.color("63"))
            .padding(1, 4).margin(1, 2);

    private final static Style CONTENT_PANEL = Style.newStyle()
            .border(Borders.normalBorder()).borderTopForeground(Color.color("46"))
            .padding(2, 6).margin(1, 2);

    private final static Style ACTIVE_ITEM = Style.newStyle()
            .foreground(Color.color("0")).background(Color.color("46"))
            .bold(true).padding(0, 1);

    private final static Style TITLE = Style.newStyle().foreground(Color.color("227")).bold(true);
    private final static Style USERNAME = Style.newStyle().foreground(Color.color("51")).italic(true);
    private final static Style TEXT_CURSOR = Style.newStyle().foreground(Color.color("46")).blink(true);
    private final static Style ERROR_TEXT = Style.newStyle().foreground(Color.color("203")).bold(true);

    public AccountSelection(User user, List<Account> accounts) {
        this.user = user;
        this.accounts = accounts;
    }

    @Override
    public Command init() { return null; }

    @Override
    public UpdateResult<? extends Model> update(Message msg) {
        if (msg instanceof KeyPressMessage keyPressMessage) {
            String key = keyPressMessage.key();
            return switch (currentState) {
                case SELECTING -> handleSelectionInput(key);
                case ENTERING_PIN -> handlePinInput(key);
                case ERROR -> handleSimpleReturn(key);
            };
        }
        return UpdateResult.from(this);
    }

    private UpdateResult<? extends Model> handleSelectionInput(String key) {
        int maxCursor = accounts.size() + 1; // Accounts + New Account + Logout

        switch (key) {
            case "k", "K", "up" -> cursor = (cursor - 1 < 0) ? maxCursor : cursor - 1;
            case "j", "J", "down" -> cursor = (cursor + 1 > maxCursor) ? 0 : cursor + 1;
            case "enter" -> {
                if (cursor < accounts.size()) {
                    selectedAccountForPin = accounts.get(cursor);
                    currentState = AppState.ENTERING_PIN;
                    pinBuffer = "";
                } else if (cursor == accounts.size()) {
                    // Send RouteModel to MasterControl to swap to AccountCreation
                    return UpdateResult.from(this, () -> new RouteModel(RouteModel.Route.ACCOUNT_CREATION, user));
                } else {
                    // Send RouteModel to MasterControl to logout to WelcomeMenu
                    return UpdateResult.from(this, () -> new RouteModel(RouteModel.Route.WELCOME, null));
                }
            }
            case "q", "Q" -> {
                return UpdateResult.from(this, () -> new RouteModel(RouteModel.Route.WELCOME, null));
            }
        }
        return UpdateResult.from(this);
    }

    private UpdateResult<? extends Model> handlePinInput(String key) {
        if (key.equals("esc")) {
            currentState = AppState.SELECTING;
            pinBuffer = "";
        } else if (key.equals("backspace") || key.equals("ctrl+h") || key.equals("delete") || key.equals("\b")) {
            if (!pinBuffer.isEmpty()) pinBuffer = pinBuffer.substring(0, pinBuffer.length() - 1);
        } else if (key.equals("enter") && !pinBuffer.isEmpty()) {
            return verifyPin();
        } else if (key.length() == 1 && Character.isDigit(key.charAt(0)) && pinBuffer.length() < 4) {
            pinBuffer += key;
        }
        return UpdateResult.from(this);
    }

    private UpdateResult<? extends Model> verifyPin() {
        try {
            int pin = Integer.parseInt(pinBuffer);
            if (accountService.verifyAccount(selectedAccountForPin, pin)) {
                // Success! Send the Object[] array payload to the Master Router
                return UpdateResult.from(this, () -> new RouteModel(RouteModel.Route.MAIN_MENU, new Object[]{user, selectedAccountForPin}));
            } else {
                currentState = AppState.ERROR;
                pinBuffer = "";
            }
        } catch (NumberFormatException e) {
            currentState = AppState.ERROR;
            pinBuffer = "";
        } catch (Exception e) {
            // This catches hidden database/backend crashes so the app doesn't just disappear!
            log.error("Fatal error during PIN verification", e);
            currentState = AppState.ERROR;
            pinBuffer = "";
        }
        return UpdateResult.from(this);
    }

    private UpdateResult<? extends Model> handleSimpleReturn(String key) {
        if (key.equals("enter") || key.equals("esc")) {
            currentState = AppState.ENTERING_PIN;
        }
        return UpdateResult.from(this);
    }

    @Override
    public String view() {
        return switch (currentState) {
            case SELECTING -> renderSelection();
            case ENTERING_PIN -> renderPinEntry();
            case ERROR -> renderError();
        };
    }

    private String renderSelection() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("\nWelcome, ").append(USERNAME.render(user.getUsername())).append("!\n\n");
        buffer.append(TITLE.render("Your Accounts:")).append("\n\n");

        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            String formattedAccount = String.format("%s ****%04d", account.getAccountType(), (account.getAccountNumber() % 10000));
            if (cursor == i) {
                buffer.append(ACTIVE_ITEM.render("▶ " + formattedAccount));
            } else {
                buffer.append("  ").append(formattedAccount);
            }
            buffer.append("\n");
        }

        if (cursor == accounts.size()) buffer.append(ACTIVE_ITEM.render("▶ Open a new account")).append("\n");
        else buffer.append(Style.newStyle().foreground(Color.color("87")).render("  Open a new account")).append("\n");

        if (cursor == accounts.size() + 1) buffer.append(ACTIVE_ITEM.render("▶ Logout")).append("\n");
        else buffer.append(Style.newStyle().foreground(Color.color("203")).render("  Logout")).append("\n");

        return MENU_PANEL.render(buffer.toString());
    }

    private String renderPinEntry() {
        String formattedAccount = String.format("%s ****%04d", selectedAccountForPin.getAccountType(), (selectedAccountForPin.getAccountNumber() % 10000));
        String maskedPin = "*".repeat(pinBuffer.length());

        String content = TITLE.render("Secure Login") + "\n\n" +
                "Enter 4-digit PIN for " + formattedAccount + ":\n" +
                Style.newStyle().foreground(Color.color("227")).render(maskedPin) + TEXT_CURSOR.render("█") + "\n\n" +
                Style.newStyle().foreground(Color.color("240")).render("Press [Enter] to submit • [Esc] to cancel");
        return CONTENT_PANEL.render(content);
    }

    private String renderError() {
        String content = TITLE.render("Authentication Failed") + "\n\n" +
                ERROR_TEXT.render("⚠ Invalid PIN credentials.") + "\n\n" +
                Style.newStyle().foreground(Color.color("240")).render("Press [Enter] to try again");
        return CONTENT_PANEL.render(content);
    }
}