package org.half.service;

import org.half.model.Account;
import org.half.model.User;
import org.half.model.enums.AccountType;
import org.half.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.SQLException;

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
}
