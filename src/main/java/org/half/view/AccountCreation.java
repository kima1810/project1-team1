package org.half.view;

import org.half.exceptions.IllegalPinLength;
import org.half.model.RouteModel;
import org.half.model.User;
import org.half.model.enums.AccountType;
import org.half.repository.AccountRepository;
import org.half.repository.TransactionModelRepository;
import org.half.service.AccountService;
import org.half.service.TransactionHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.half.style.Theme;

import com.williamcallahan.tui4j.compat.bubbletea.Command;
import com.williamcallahan.tui4j.compat.bubbletea.Message;
import com.williamcallahan.tui4j.compat.bubbletea.Model;
import com.williamcallahan.tui4j.compat.bubbletea.UpdateResult;
import com.williamcallahan.tui4j.compat.bubbletea.KeyPressMessage;

public class AccountCreation implements Model {
    private static final Logger log = LoggerFactory.getLogger(AccountCreation.class);

    private static final AccountRepository accountRepository = new AccountRepository();
    private static final TransactionModelRepository transactionModelRepository = new TransactionModelRepository();

    private static final TransactionHistoryService transactionHistoryService = new TransactionHistoryService(transactionModelRepository);

    private static final AccountService accountService = new AccountService(accountRepository, transactionHistoryService);

    private enum Step {
        CHOOSE_TYPE,
        ENTER_PIN,
        CONFIRM_PIN,
        SUCCESS,
        ERROR
    }

    private final User user;
    private Step currentStep = Step.CHOOSE_TYPE;

    private final String[] CHOICES = {"Checking Account", "Savings Account", "Cancel"};
    private int cursor = 0;

    private AccountType selectedType = null;
    private String pinBuffer = "";
    private String confirmPinBuffer = "";
    private long createdAccountNumber = -1;
    private String errorMessage = "";

    // Constructor accepting the logged-in User
    public AccountCreation(User user) {
        this.user = user;
    }

    @Override
    public Command init() {
        return null;
    }

    @Override
    public UpdateResult<? extends Model> update(Message msg) {
        if (msg instanceof KeyPressMessage keyPressMessage) {
            String key = keyPressMessage.key();

            return switch (currentStep) {
                case CHOOSE_TYPE -> handleChooseTypeInput(key);
                case ENTER_PIN -> handleEnterPinInput(key);
                case CONFIRM_PIN -> handleConfirmPinInput(key);
                case SUCCESS -> handleSuccessInput(key);
                case ERROR -> handleErrorInput(key);
            };
        }
        return UpdateResult.from(this);
    }

    private UpdateResult<? extends Model> handleChooseTypeInput(String key) {
        switch (key) {
            case "k", "K", "up" -> cursor = (cursor - 1 < 0) ? CHOICES.length - 1 : cursor - 1;
            case "j", "J", "down" -> cursor = (cursor + 1 >= CHOICES.length) ? 0 : cursor + 1;
            case "enter" -> {
                if (cursor == 0) {
                    selectedType = AccountType.CHECKING;
                    log.info("User selected account type: {}", selectedType);
                    currentStep = Step.ENTER_PIN;
                } else if (cursor == 1) {
                    selectedType = AccountType.SAVINGS;
                    log.info("User selected account type: {}", selectedType);
                    currentStep = Step.ENTER_PIN;
                } else {
                    return cancelAndReturn();
                }
            }
            case "q", "Q", "esc" -> {
                return cancelAndReturn();
            }
        }
        return UpdateResult.from(this);
    }

    private UpdateResult<? extends Model> handleEnterPinInput(String key) {
        if (key.equals("esc")) {
            currentStep = Step.CHOOSE_TYPE;
            pinBuffer = "";
        } else if (isBackspace(key)) {
            if (!pinBuffer.isEmpty()) pinBuffer = pinBuffer.substring(0, pinBuffer.length() - 1);
        } else if (key.equals("enter") && pinBuffer.length() == 4) {
            currentStep = Step.CONFIRM_PIN;
        } else if (key.length() == 1 && Character.isDigit(key.charAt(0)) && pinBuffer.length() < 4) {
            pinBuffer += key;
        }
        return UpdateResult.from(this);
    }

    private UpdateResult<? extends Model> handleConfirmPinInput(String key) {
        if (key.equals("esc")) {
            currentStep = Step.ENTER_PIN;
            confirmPinBuffer = "";
        } else if (isBackspace(key)) {
            if (!confirmPinBuffer.isEmpty()) confirmPinBuffer = confirmPinBuffer.substring(0, confirmPinBuffer.length() - 1);
        } else if (key.equals("enter") && confirmPinBuffer.length() == 4) {
            if (!confirmPinBuffer.equals(pinBuffer)) {
                log.warn("PINs entered do not match.");
                errorMessage = "PINs do not match. Please try again.";
                pinBuffer = "";
                confirmPinBuffer = "";
                currentStep = Step.ERROR;
            } else {
                log.info("PIN accepted.");
                createAccountRecord();
            }
        } else if (key.length() == 1 && Character.isDigit(key.charAt(0)) && confirmPinBuffer.length() < 4) {
            confirmPinBuffer += key;
        }
        return UpdateResult.from(this);
    }

    private void createAccountRecord() {
        try {
            int pin = Integer.parseInt(pinBuffer);
            long accNum = accountService.createAccount(user, pin, selectedType);
            if (accNum != -1) {
                createdAccountNumber = accNum;
                currentStep = Step.SUCCESS;
                log.info("Account successfully created! {username: {}, account: {}", user.getUsername(), selectedType + String.format("****%04d", createdAccountNumber));
            } else {
                errorMessage = "Failed to create account. Please try again...";
                log.error("Failed to create an account for user: {}", user.getUsername());
                currentStep = Step.ERROR;
            }
        } catch (IllegalPinLength illegalPinLength) {
            errorMessage = illegalPinLength.getMessage();
            log.error("Illegal pin length was passed for account creation.");
            currentStep = Step.ERROR;
        } catch (Exception e) {
            log.error("Something when wrong when creating a new account: {}", e.getMessage());
            errorMessage = e.getMessage();
            currentStep = Step.ERROR;
        }
    }

    private UpdateResult<? extends Model> handleSuccessInput(String key) {
        if (key.equals("enter") || key.equals("esc")) {
            return UpdateResult.from(this, () -> new RouteModel(RouteModel.Route.ACCOUNT_SELECTION, user));
        }
        return UpdateResult.from(this);
    }

    private UpdateResult<? extends Model> handleErrorInput(String key) {
        if (key.equals("enter") || key.equals("esc")) {
            currentStep = Step.ENTER_PIN;
        }
        return UpdateResult.from(this);
    }

    private UpdateResult<? extends Model> cancelAndReturn() {
        return UpdateResult.from(this, () -> new RouteModel(RouteModel.Route.ACCOUNT_SELECTION, user));
    }

    private boolean isBackspace(String key) {
        return key.equals("backspace") || key.equals("ctrl+h") || key.equals("delete") || key.equals("\b");
    }

    @Override
    public String view() {
        return switch (currentStep) {
            case CHOOSE_TYPE -> renderChooseType();
            case ENTER_PIN -> renderPinEntry("Create a 4-digit PIN:", pinBuffer);
            case CONFIRM_PIN -> renderPinEntry("Confirm your 4-digit PIN:", confirmPinBuffer);
            case SUCCESS -> renderSuccess();
            case ERROR -> renderError();
        };
    }

    private String renderChooseType() {
        log.info("Creating a new account for user: {}", user.getUsername());

        StringBuilder buffer = new StringBuilder();
        buffer.append(Theme.TITLE.render("Create a New Account")).append("\n\n");
        buffer.append("Choose account type:\n\n");

        for (int i = 0; i < CHOICES.length; i++) {
            if (cursor == i) {
                buffer.append(Theme.ACTIVE_ITEM_INPUT.render("▶ " + CHOICES[i])).append("\n");
            } else {
                if (i == 2) {
                    buffer.append(Theme.ERROR_TEXT.render("  " + CHOICES[i])).append("\n");
                } else {
                    buffer.append("  ").append(CHOICES[i]).append("\n");
                }
            }
        }
        return Theme.MAIN_PANEL.render(buffer.toString());
    }

    private String renderPinEntry(String heading, String buffer) {
        String masked = "*".repeat(buffer.length());
        String content = Theme.TITLE.render("Account Setup (" + selectedType + ")") + "\n\n" +
                heading + "\n" +
                Theme.TITLE.render(masked) + Theme.TEXT_CURSOR.render("█") + "\n\n" +
                Theme.FOOTER_TEXT.render("Type 4 digits • [Enter] continue • [Esc] back");
        return Theme.MAIN_PANEL.render(content);
    }

    private String renderSuccess() {
        String content = Theme.TITLE.render("Account Successfully Created!") + "\n\n" +
                "Type: " + Theme.LOGO.render(selectedType.toString()) + "\n" +
                "Account Number: " + Theme.TITLE.render(String.valueOf(createdAccountNumber)) + "\n\n" +
                Theme.FOOTER_TEXT.render("Press [Enter] to return to Account Selection");
        return Theme.MAIN_PANEL.render(content);
    }

    private String renderError() {
        String content = Theme.TITLE.render("Account Creation Failed") + "\n\n" +
                Theme.ERROR_TEXT.render("⚠ " + errorMessage) + "\n\n" +
                Theme.FOOTER_TEXT.render("Press [Enter] to try again");
        return Theme.MAIN_PANEL.render(content);
    }
}
