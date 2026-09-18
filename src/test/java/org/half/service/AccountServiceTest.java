package org.half.service;

import org.half.exceptions.InsufficientFundsException;
import org.half.model.Account;
import org.half.model.User;
import org.half.model.enums.AccountType;
import org.half.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

class AccountServiceTest {

    @Test
    void createAccount_shouldThrowException_whenPinHasMoreThanFourDigits() {
        User user = mock(User.class);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> AccountService.createAccount(
                        user,
                        10_000,
                        AccountType.CHECKING
                )
        );

        assertEquals(
                "Invalid pin. Cannot be more than 4 digits.",
                exception.getMessage()
        );
    }

    @Test
    void createAccount_shouldCreateAccountSuccessfully() {
        User user = mock(User.class);

        try (MockedStatic<AccountRepository> repositoryMock =
                     Mockito.mockStatic(AccountRepository.class)) {

            repositoryMock
                    .when(() -> AccountRepository.addAccount(any(Account.class)))
                    .thenAnswer(invocation -> null);

            long accountNumber = AccountService.createAccount(
                    user,
                    1234,
                    AccountType.CHECKING
            );

            assertTrue(accountNumber >= 100_000_000_000L);
            assertTrue(accountNumber < 1_000_000_000_000L);

            repositoryMock.verify(
                    () -> AccountRepository.addAccount(any(Account.class)),
                    times(1)
            );
        }
    }

    @Test
    void createAccount_shouldAcceptFourDigitPin() {
        User user = mock(User.class);

        try (MockedStatic<AccountRepository> repositoryMock =
                     Mockito.mockStatic(AccountRepository.class)) {

            repositoryMock
                    .when(() -> AccountRepository.addAccount(any(Account.class)))
                    .thenAnswer(invocation -> null);

            assertDoesNotThrow(() ->
                    AccountService.createAccount(
                            user,
                            9999,
                            AccountType.CHECKING
                    )
            );

            repositoryMock.verify(
                    () -> AccountRepository.addAccount(any(Account.class)),
                    times(1)
            );
        }
    }

    @Test
    void createAccount_shouldRetry_whenAccountNumberAlreadyExists() {

        User user = mock(User.class);

        SQLException duplicateKeyException =
                new SQLException("[SQLITE_CONSTRAINT_PRIMARYKEY]");

        try (MockedStatic<AccountRepository> repositoryMock =
                     Mockito.mockStatic(AccountRepository.class)) {

            repositoryMock
                    .when(() -> AccountRepository.addAccount(any(Account.class)))
                    .thenThrow(duplicateKeyException)
                    .thenAnswer(invocation -> null);

            long accountNumber = AccountService.createAccount(
                    user,
                    1234,
                    AccountType.CHECKING
            );

            assertTrue(accountNumber >= 100_000_000_000L);
            assertTrue(accountNumber < 1_000_000_000_000L);

            repositoryMock.verify(
                    () -> AccountRepository.addAccount(any(Account.class)),
                    times(2)
            );
        }
    }

    @Test
    void createAccount_shouldReturnMinusOne_afterTenFailures() {

        User user = mock(User.class);

        SQLException duplicateKeyException =
                new SQLException("[SQLITE_CONSTRAINT_PRIMARYKEY]");

        try (MockedStatic<AccountRepository> repositoryMock =
                     Mockito.mockStatic(AccountRepository.class)) {

            repositoryMock
                    .when(() -> AccountRepository.addAccount(any(Account.class)))
                    .thenThrow(duplicateKeyException);

            long result = AccountService.createAccount(
                    user,
                    1234,
                    AccountType.CHECKING
            );

            assertEquals(-1, result);

            repositoryMock.verify(
                    () -> AccountRepository.addAccount(any(Account.class)),
                    times(10)
            );
        }
    }

    @Test
    void createAccount_shouldRetry_whenNonPrimaryKeySqlExceptionOccurs() {

        User user = mock(User.class);

        SQLException sqlException =
                new SQLException("Database connection failed");

        try (MockedStatic<AccountRepository> repositoryMock =
                     Mockito.mockStatic(AccountRepository.class)) {

            repositoryMock
                    .when(() -> AccountRepository.addAccount(any(Account.class)))
                    .thenThrow(sqlException)
                    .thenAnswer(invocation -> null);

            long accountNumber = AccountService.createAccount(
                    user,
                    1234,
                    AccountType.CHECKING
            );

            assertTrue(accountNumber >= 100_000_000_000L);
            assertTrue(accountNumber < 1_000_000_000_000L);

            repositoryMock.verify(
                    () -> AccountRepository.addAccount(any(Account.class)),
                    times(2)
            );
        }
    }

    @Test
    void getAccounts_shouldReturnAccounts_whenRepositorySucceeds() {
        User user = mock(User.class);
        List<Account> expectedAccounts = List.of(
                mock(Account.class),
                mock(Account.class)
        );

        try (MockedStatic<AccountRepository> repositoryMock =
                     Mockito.mockStatic(AccountRepository.class)) {

            repositoryMock
                    .when(() -> AccountRepository.getAllAccounts(user))
                    .thenReturn(expectedAccounts);

            List<Account> actualAccounts = AccountService.getAccounts(user);

            assertSame(expectedAccounts, actualAccounts);

            repositoryMock.verify(
                    () -> AccountRepository.getAllAccounts(user)
            );
        }
    }

    @Test
    void getAccounts_shouldReturnNull_whenRepositoryThrowsSQLException() {
        User user = mock(User.class);
        SQLException exception = new SQLException("Database error");

        try (MockedStatic<AccountRepository> repositoryMock =
                     Mockito.mockStatic(AccountRepository.class)) {

            repositoryMock
                    .when(() -> AccountRepository.getAllAccounts(user))
                    .thenThrow(exception);

            List<Account> actualAccounts = AccountService.getAccounts(user);

            assertNull(actualAccounts);

            repositoryMock.verify(
                    () -> AccountRepository.getAllAccounts(user)
            );
        }
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
