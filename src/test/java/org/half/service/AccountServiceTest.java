package org.half.service;

import org.half.exceptions.IllegalPinLength;
import org.half.exceptions.InsufficientFundsException;
import org.half.model.Account;
import org.half.model.User;
import org.half.model.enums.AccountType;
import org.half.repository.AccountRepository;
import org.half.security.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    private AccountRepository accountRepository;
    private TransactionHistoryService transactionHistoryService;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        transactionHistoryService = mock(TransactionHistoryService.class);

        accountService = new AccountService(
                accountRepository,
                transactionHistoryService
        );
    }

    @Test
    void createAccount_shouldThrowIllegalPinLength_whenPinHasMoreThanFourDigits() throws SQLException {
        User user = mock(User.class);

        IllegalPinLength exception = assertThrows(
                IllegalPinLength.class,
                () -> accountService.createAccount(
                        user,
                        10000,
                        AccountType.CHECKING
                )
        );

        verifyNoInteractions(accountRepository);
    }

    @Test
    void createAccount_shouldAcceptFourDigitPin() throws SQLException, IllegalPinLength {

        User user = mock(User.class);

        doNothing().when(accountRepository).addAccount(any(Account.class));

        long accountNumber = accountService.createAccount(
                user,
                9999,
                AccountType.CHECKING
        );

        assertTrue(accountNumber >= 100000000000L);
        assertTrue(accountNumber < 1000000000000L);

        verify(accountRepository, times(1))
                .addAccount(any(Account.class));
    }

    @Test
    void createAccount_shouldCreateAccountSuccessfully() throws SQLException, IllegalPinLength {
        User user = mock(User.class);

        doNothing().when(accountRepository).addAccount(any(Account.class));

        long accountNumber = accountService.createAccount(
                user,
                1234,
                AccountType.CHECKING
        );

        assertTrue(accountNumber >= 100000000000L);
        assertTrue(accountNumber < 1000000000000L);

        verify(accountRepository, times(1))
                .addAccount(any(Account.class));
    }

    @Test
    void createAccount_shouldRetry_whenAccountNumberAlreadyExists() throws SQLException, IllegalPinLength {
        User user = mock(User.class);

        SQLException duplicateKeyException = new SQLException("[SQLITE_CONSTRAINT_PRIMARYKEY]");

        doThrow(duplicateKeyException).doNothing().when(accountRepository).addAccount(any(Account.class));

        long accountNumber = accountService.createAccount(
                user,
                1234,
                AccountType.CHECKING
        );

        assertTrue(accountNumber >= 100000000000L);
        assertTrue(accountNumber < 1000000000000L);

        verify(accountRepository, times(2))
                .addAccount(any(Account.class));
    }

    @Test
    void createAccount_shouldReturnMinusOne_afterTenDuplicateKeyFailures() throws SQLException, IllegalPinLength {
        User user = mock(User.class);

        SQLException duplicateKeyException = new SQLException("[SQLITE_CONSTRAINT_PRIMARYKEY]");

        doThrow(duplicateKeyException).when(accountRepository).addAccount(any(Account.class));

        long result = accountService.createAccount(
                user,
                1234,
                AccountType.CHECKING
        );

        assertEquals(-1, result);

        verify(accountRepository, times(10)).addAccount(any(Account.class));
    }

    @Test
    void verifyAccount_shouldThrowIllegalPinLength_whenPinHasMoreThanFourDigits() {
        Account account = mock(Account.class);

        IllegalPinLength exception = assertThrows(
                IllegalPinLength.class,
                () -> accountService.verifyAccount(
                        account,
                        10000
                )
        );

        assertEquals(
                "Invalid pin. Cannot be more than 4 digits.",
                exception.getMessage()
        );

        verifyNoInteractions(account);
    }

    @Test
    void verifyAccount_shouldReturnTrue_whenPinIsCorrect() throws IllegalPinLength {
        Account account = mock(Account.class);

        when(account.getPinHash()).thenReturn(PasswordService.hashPassword("1234"));

        boolean result = accountService.verifyAccount(
                account,
                1234
        );

        assertTrue(result);

        verify(account, times(1)).getPinHash();
    }

    @Test
    void verifyAccount_shouldReturnFalse_whenPinIsIncorrect() throws IllegalPinLength {
        Account account = mock(Account.class);

        when(account.getPinHash()).thenReturn(PasswordService.hashPassword("1234"));

        boolean result = accountService.verifyAccount(
                account,
                9999
        );

        assertFalse(result);

        verify(account, times(1))
                .getPinHash();
    }

    @Test
    void getAccounts_shouldReturnAccounts_whenRepositorySucceeds() throws SQLException {
        User user = mock(User.class);

        List<Account> expectedAccounts = List.of(
                mock(Account.class),
                mock(Account.class)
        );

        when(accountRepository.getAllAccounts(user)).thenReturn(expectedAccounts);

        List<Account> actualAccounts = accountService.getAccounts(user);

        assertSame(expectedAccounts, actualAccounts);

        verify(accountRepository, times(1)).getAllAccounts(user);
    }

    @Test
    void getAccounts_shouldReturnEmptyList_whenRepositoryReturnsEmptyList() throws SQLException {
        User user = mock(User.class);

        List<Account> expectedAccounts = List.of();

        when(accountRepository.getAllAccounts(user)).thenReturn(expectedAccounts);

        List<Account> actualAccounts = accountService.getAccounts(user);

        assertNotNull(actualAccounts);
        assertTrue(actualAccounts.isEmpty());

        verify(accountRepository, times(1))
                .getAllAccounts(user);
    }

    @Test
    void testDepositRequest_UpdatesBalanceAndCallsServices() {
        Account testAccount = new Account(null, 123456789L, "1234", AccountType.CHECKING, 100.0);

        try (MockedStatic<AccountRepository> repoMock = Mockito.mockStatic(AccountRepository.class);
             MockedStatic<TransactionHistoryService> historyMock = Mockito.mockStatic(TransactionHistoryService.class)) {

            AccountService.Deposit_Request(testAccount, 50.0);

            assertEquals(150.0, testAccount.getBalance(), "Balance should be updated to 150.0");

            repoMock.verify(() -> AccountRepository.Update_Balance(testAccount, 150.0));
            historyMock.verify(() -> TransactionHistoryService.attemptAddDepositOrWithdrawal("Deposit", 50.0, 123456789L));
        }
    }

    @Test
    void testDepositRequest_WithZeroAmount_DoesNotChangeBalance() {
        Account testAccount = new Account(null, 123456789L, "1234", AccountType.CHECKING, 200.0);

        try (MockedStatic<AccountRepository> repoMock = Mockito.mockStatic(AccountRepository.class);
             MockedStatic<TransactionHistoryService> historyMock = Mockito.mockStatic(TransactionHistoryService.class)) {

            AccountService.Deposit_Request(testAccount, 0.0);

            assertEquals(200.0, testAccount.getBalance(), "Balance should remain 200.0");
            repoMock.verify(() -> AccountRepository.Update_Balance(testAccount, 200.0));
            historyMock.verify(() -> TransactionHistoryService.attemptAddDepositOrWithdrawal("Deposit", 0.0, 123456789L));
        }
    }

    @Test
    void testDepositRequest_WithNegativeAmount_ThrowsException() {
        Account testAccount = new Account(null, 123456789L, "1234", AccountType.CHECKING, 100.0);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            AccountService.Deposit_Request(testAccount, -50.0);
        });

        assertEquals("Amount cannot be negative.", exception.getMessage());
    }

    @Test
    void testWithdrawRequest_OverdraftAttempt_ThrowsInsufficientFundsException() {
        Account testAccount = new Account(null, 987654321L, "4321", AccountType.SAVINGS, 100.0);

        Exception exception = assertThrows(InsufficientFundsException.class, () -> {
            AccountService.Withdraw_Request(testAccount, 500.0);
        });

        assertEquals("Account balance cannot be less than amount.", exception.getMessage());
    }

    @Test
    void testWithdrawRequest_WithNegativeAmount_ThrowsException() {
        Account testAccount = new Account(null, 987654321L, "4321", AccountType.SAVINGS, 100.0);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            AccountService.Withdraw_Request(testAccount, -20.0);
        });

        assertEquals("Amount cannot be negative.", exception.getMessage());
    }

    @Test
    void testWithdrawRequest_FloatingPointPrecision() {
        // Doubles can cause weird fractional issues (e.g., 100.05 - 100.04 = 0.010000000000005)
        Account testAccount = new Account(null, 987654321L, "4321", AccountType.SAVINGS, 100.05);

        try (MockedStatic<AccountRepository> repoMock = Mockito.mockStatic(AccountRepository.class);
             MockedStatic<TransactionHistoryService> historyMock = Mockito.mockStatic(TransactionHistoryService.class)) {

            AccountService.Withdraw_Request(testAccount, 100.04);

            assertEquals(0.01, testAccount.getBalance(), 0.001, "Balance should be exactly 0.01 despite double precision artifacts");
        }
    }
}
