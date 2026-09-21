package org.half.view;

import org.half.model.Account;
import org.half.model.RouteModel;
import org.half.model.User;
import org.half.repository.AccountRepository;
import org.half.service.AccountService;
import org.half.service.TransactionHistoryService;
import org.half.style.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import org.half.model.TransactionModel;
import org.half.repository.TransactionModelRepository;

import com.williamcallahan.tui4j.compat.bubbletea.Command;
import com.williamcallahan.tui4j.compat.bubbletea.Message;
import com.williamcallahan.tui4j.compat.bubbletea.Model;
import com.williamcallahan.tui4j.compat.bubbletea.UpdateResult;
import com.williamcallahan.tui4j.compat.lipgloss.Style;
import com.williamcallahan.tui4j.compat.lipgloss.color.Color;
import com.williamcallahan.tui4j.compat.bubbletea.KeyPressMessage;
import com.williamcallahan.tui4j.compat.lipgloss.Borders;

public class MainMenu implements Model {
    private static final Logger log = LoggerFactory.getLogger(MainMenu.class);

    private static final AccountRepository accountRepository = new AccountRepository();
    private static final TransactionModelRepository transactionModelRepository = new TransactionModelRepository();

    private static final TransactionHistoryService transactionHistoryService = new TransactionHistoryService(transactionModelRepository);

    private static final AccountService accountService = new AccountService(accountRepository, transactionHistoryService);

    // --- 1. SPA State Management ---
    private enum AppState {
        MENU, VIEWING_BALANCE, VIEWING_HISTORY, TYPING_INPUT, NOTIFICATION
    }
    private enum ActionType {
        NONE, WITHDRAW, DEPOSIT, TRANSFER_DEST, TRANSFER_AMOUNT
    }

    private AppState currentState = AppState.MENU;
    private ActionType currentAction = ActionType.NONE;

    private final User activeUser;
    private final Account activeAccount;

    // Memory buffers for native TUI text input
    private String inputBuffer = "";
    private String notificationMessage = "";
    private long transferDest = 0;
    private boolean animationToggle = false;

    private final static Style ACTIVE_ITEM = Style.newStyle()
            .foreground(Color.color("0")).background(Color.color("46"))
            .bold(true).padding(0, 1);

    private final static Style TITLE = Style.newStyle().foreground(Color.color("227")).bold(true);
    private final static Style TEXT_CURSOR = Style.newStyle().foreground(Color.color("46")).blink(true);

    private final static String[] CHOICES = {
            "View balance", "Withdraw", "Deposit", "Transfer", "Transaction history", "Switch Account"
    };
    private int cursor = 0;

    // Updated Constructor to accept both the User and the Account
    public MainMenu(User activeUser, Account activeAccount) {
        this.activeUser = activeUser;
        this.activeAccount = activeAccount;
    }

    @Override
    public Command init() { return null; }

    // --- 3. The Router (Update) ---
    @Override
    public UpdateResult<? extends Model> update(Message msg) {
        if (msg instanceof KeyPressMessage keyPressMessage) {
            String key = keyPressMessage.key();
            return switch (currentState) {
                case MENU -> handleMenuInput(key);
                case TYPING_INPUT -> handleTypingInput(key);
                case VIEWING_BALANCE, VIEWING_HISTORY, NOTIFICATION -> handleSimpleReturn(key);
            };
        }
        return UpdateResult.from(this);
    }

    private UpdateResult<? extends Model> handleMenuInput(String key) {
        return switch (key) {
            case "k", "K", "up" -> {
                cursor = (cursor - 1 < 0) ? CHOICES.length - 1 : cursor - 1;
                animationToggle = false;
                yield UpdateResult.from(this);
            }
            case "j", "J", "down" -> {
                cursor = (cursor + 1 >= CHOICES.length) ? 0 : cursor + 1;
                animationToggle = false;
                yield UpdateResult.from(this);
            }
            case "enter" -> routeMenuSelection();
            case "q", "Q" -> UpdateResult.from(this, () -> new RouteModel(RouteModel.Route.ACCOUNT_SELECTION, activeUser));
            default -> UpdateResult.from(this);
        };
    }

    private UpdateResult<? extends Model> routeMenuSelection() {
        switch (cursor) {
            case 0: currentState = AppState.VIEWING_BALANCE; break;
            case 1: currentAction = ActionType.WITHDRAW; prepareInput(); break;
            case 2: currentAction = ActionType.DEPOSIT; prepareInput(); break;
            case 3: currentAction = ActionType.TRANSFER_DEST; prepareInput(); break;
            case 4: currentState = AppState.VIEWING_HISTORY; break;
            case 5: return UpdateResult.from(this, () -> new RouteModel(RouteModel.Route.ACCOUNT_SELECTION, activeUser)); // Routes back to Account Selection!
        }
        return UpdateResult.from(this);
    }

    private void prepareInput() {
        inputBuffer = "";
        currentState = AppState.TYPING_INPUT;
    }

    private UpdateResult<? extends Model> handleTypingInput(String key) {
        if (key.equals("esc")) {
            currentState = AppState.MENU;
        } else if (key.equals("ctrl+h") || key.equals("delete")) {
            if (!inputBuffer.isEmpty()) {
                inputBuffer = inputBuffer.substring(0, inputBuffer.length() - 1);
            }
        } else if (key.equals("enter") && !inputBuffer.isEmpty()) {
            processTransactionInput();
        } else if (key.length() == 1 && (Character.isDigit(key.charAt(0)) || key.equals("."))) {
            inputBuffer += key;
        }
        return UpdateResult.from(this);
    }

    private void processTransactionInput() {
        try {
            if (currentAction == ActionType.WITHDRAW) {
                double amount = Double.parseDouble(inputBuffer);
                accountService.Withdraw_Request(activeAccount, amount);
                showNotification("Withdrawal successful! New balance: $" + String.format("%.2f", activeAccount.getBalance()));

            } else if (currentAction == ActionType.DEPOSIT) {
                double amount = Double.parseDouble(inputBuffer);
                accountService.Deposit_Request(activeAccount, amount);
                showNotification("Deposit successful! New balance: $" + String.format("%.2f", activeAccount.getBalance()));

            } else if (currentAction == ActionType.TRANSFER_DEST) {
                transferDest = Long.parseLong(inputBuffer);
                currentAction = ActionType.TRANSFER_AMOUNT;
                inputBuffer = "";

            } else if (currentAction == ActionType.TRANSFER_AMOUNT) {
                double amount = Double.parseDouble(inputBuffer);
                boolean success = accountService.transfer(activeAccount, transferDest, amount);
                if (success) {
                    showNotification("Transfer complete! New balance: $" + String.format("%.2f", activeAccount.getBalance()));
                } else {
                    showNotification("Transfer failed. Check destination account and funds.");
                }
            }
        } catch (NumberFormatException e) {
            showNotification("Error: Invalid number format entered.");
        } catch (Exception e) {
            showNotification("Failed: " + e.getMessage());
        }
    }

    private void showNotification(String msg) {
        this.notificationMessage = msg;
        this.currentState = AppState.NOTIFICATION;
    }

    private UpdateResult<? extends Model> handleSimpleReturn(String key) {
        if (key.equals("q") || key.equals("esc") || key.equals("enter")) {
            this.currentState = AppState.MENU;
        }
        return UpdateResult.from(this);
    }

    @Override
    public String view() {
        return switch (currentState) {
            case MENU -> renderMenu();
            case VIEWING_BALANCE -> renderBalance();
            case TYPING_INPUT -> renderTypingBox();
            case NOTIFICATION -> renderNotification();
            case VIEWING_HISTORY -> renderHistory();
        };
    }

    private String renderMenu() {
        StringBuilder content = new StringBuilder();
        content.append(TITLE.render("Main Menu")).append("\n\n");
        for (int i = 0; i < CHOICES.length; i++) {
            if (cursor == i) {
                content.append(ACTIVE_ITEM.render("▶ " + CHOICES[i])).append("\n");
            } else {
                if (i == 5) content.append(Style.newStyle().foreground(Color.color("203")).render("  " + CHOICES[i])).append("\n");
                else content.append("  ").append(CHOICES[i]).append("\n");
            }
        }
        return Theme.MAIN_PANEL.render(content.toString());
    }

    private String renderBalance() {
        String content = TITLE.render("Account Balance") + "\n\n" +
                "Available Funds: " + Style.newStyle().foreground(Color.color("46")).bold(true).render(String.format("$%.2f", activeAccount.getBalance())) + "\n\n" +
                Style.newStyle().foreground(Color.color("240")).render("Press 'enter' to return");
        return Theme.CONTENT_PANEL.render(content);
    }

    private String renderTypingBox() {
        String prompt = switch (currentAction) {
            case WITHDRAW -> "Enter amount to withdraw ($):";
            case DEPOSIT -> "Enter amount to deposit ($):";
            case TRANSFER_DEST -> "Enter destination account number:";
            case TRANSFER_AMOUNT -> "Enter amount to transfer to ****" + (transferDest % 10000) + " ($):";
            default -> "";
        };

        String content = TITLE.render("Transaction Input") + "\n\n" +
                prompt + "\n" +
                Style.newStyle().foreground(Color.color("227")).render(inputBuffer) + TEXT_CURSOR.render("█") + "\n\n" +
                Style.newStyle().foreground(Color.color("240")).render("Press [Enter] to submit • [Esc] to cancel");
        return Theme.CONTENT_PANEL.render(content);
    }

    private String renderNotification() {
        String content = TITLE.render("System Notice") + "\n\n" +
                notificationMessage + "\n\n" +
                Style.newStyle().foreground(Color.color("240")).render("Press 'enter' to continue");
        return Theme.CONTENT_PANEL.render(content);
    }

    private String renderHistory() {
        StringBuilder content = new StringBuilder();
        content.append(TITLE.render("Transaction History")).append("\n\n");

        String header = String.format("%-22s %-12s %-15s %-15s %-15s",
                "Date", "Type", "Amount", "Origin ID", "Dest ID");

        content.append(Style.newStyle().foreground(Color.color("51")).italic(true).render(header)).append("\n");
        content.append("──────────────────────────────────────────────────────────────────────────────────\n");

        List<TransactionModel> history = transactionModelRepository.printOutTransactions(activeAccount.getAccountNumber());

        if (history.isEmpty()) {
            content.append(Style.newStyle().foreground(Color.color("240")).render("No transactions found.\n"));
        } else {
            for (TransactionModel t : history) {
                String origin = (t.getOriginAccountId() == 0) ? "N/A" : String.valueOf(t.getOriginAccountId());
                String dest = (t.getDestinationAccountId() == 0) ? "N/A" : String.valueOf(t.getDestinationAccountId());

                String row = String.format("%-22s %-12s $%-14.2f %-15s %-15s",
                        t.getDateTime(),
                        t.getType(),
                        t.getAmount(),
                        origin,
                        dest);
                content.append(row).append("\n");
            }
        }

        content.append("\n\n");
        content.append(Style.newStyle().foreground(Color.color("240")).render("Press 'enter' to return"));

        return Theme.CONTENT_PANEL.render(content.toString());
    }
}