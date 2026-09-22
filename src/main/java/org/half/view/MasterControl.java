package org.half.view;

import org.half.model.Account;
import org.half.model.RouteModel;
import org.half.model.User;
import org.half.repository.AccountRepository;
import org.half.repository.TransactionModelRepository;
import org.half.service.AccountService;
import java.util.List;

import com.williamcallahan.tui4j.compat.bubbletea.Command;
import com.williamcallahan.tui4j.compat.bubbletea.Message;
import com.williamcallahan.tui4j.compat.bubbletea.Model;
import com.williamcallahan.tui4j.compat.bubbletea.UpdateResult;
import com.williamcallahan.tui4j.compat.bubbletea.QuitMessage;
import org.half.service.TransactionHistoryService;

public class MasterControl implements Model {
    private Model currentView;

    private static final AccountRepository accountRepository = new AccountRepository();
    private static final TransactionModelRepository transactionModelRepository = new TransactionModelRepository();

    private static final TransactionHistoryService transactionHistoryService = new TransactionHistoryService(transactionModelRepository);

    private static final AccountService accountService = new AccountService(accountRepository, transactionHistoryService);

    public MasterControl() {
        this.currentView = new WelcomeMenu();
    }

    @Override
    public Command init() {
        return currentView.init();
    }

    @Override
    public UpdateResult<? extends Model> update(Message msg) {
        if (msg instanceof RouteModel routeMsg) {
            return handleRouting(routeMsg);
        }

        UpdateResult<? extends Model> childResult = currentView.update(msg);
        this.currentView = childResult.model();
        return UpdateResult.from(this, childResult.command());
    }

    private UpdateResult<? extends Model> handleRouting(RouteModel msg) {
        switch (msg.destination()) {
            case WELCOME -> this.currentView = new WelcomeMenu();
            case SIGN_IN -> this.currentView = new SignIn();
            case REGISTER -> this.currentView = new Register();
            case ACCOUNT_SELECTION -> {
                User user = (User) msg.payload();
                List<Account> accounts = accountService.getAccounts(user);
                if (accounts == null || accounts.isEmpty()) {
                    this.currentView = new AccountCreation(user);
                } else {
                    this.currentView = new AccountSelection(user, accounts);
                }
            }
            case ACCOUNT_CREATION -> {
                this.currentView = new AccountCreation((User) msg.payload());
            }
            case MAIN_MENU -> {
                // Safely unpacks the array sent by verifyPin()
                Object[] payload = (Object[]) msg.payload();
                this.currentView = new MainMenu((User) payload[0], (Account) payload[1]);
            }
            case EXIT -> {
                return UpdateResult.from(this, QuitMessage::new);
            }
        }
        return UpdateResult.from(this, currentView.init());
    }

    @Override
    public String view() {
        return currentView.view();
    }
}