package org.half.service;

import org.half.model.Account;
import org.half.model.enums.AccountType;
import org.half.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.OptionalDouble;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountServiceTransferTest {
    private static final long SOURCE_ACCOUNT_NUMBER = 111_111_111_111L;
    private static final long DESTINATION_ACCOUNT_NUMBER = 222_222_222_222L;

    private AccountRepository accountRepository;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        accountService = new AccountService(
                accountRepository,
                mock(TransactionHistoryService.class)
        );
    }

    @Test
    void transfer_shouldUseBalanceReturnedByDatabase() {
        Account sourceAccount = sourceAccountWithBalance(100.00);
        when(accountRepository.transferFunds(
                SOURCE_ACCOUNT_NUMBER,
                DESTINATION_ACCOUNT_NUMBER,
                25.00
        )).thenReturn(OptionalDouble.of(75.00));

        boolean result = accountService.transfer(
                sourceAccount,
                DESTINATION_ACCOUNT_NUMBER,
                25.00
        );

        assertTrue(result);
        assertEquals(75.00, sourceAccount.getBalance(), 0.001);
    }

    @Test
    void transfer_shouldLetDatabaseDecide_whenCachedBalanceIsStale() {
        Account sourceAccount = sourceAccountWithBalance(10.00);
        when(accountRepository.transferFunds(
                SOURCE_ACCOUNT_NUMBER,
                DESTINATION_ACCOUNT_NUMBER,
                25.00
        )).thenReturn(OptionalDouble.of(75.00));

        boolean result = accountService.transfer(
                sourceAccount,
                DESTINATION_ACCOUNT_NUMBER,
                25.00
        );

        assertTrue(result);
        assertEquals(75.00, sourceAccount.getBalance(), 0.001);
        verify(accountRepository).transferFunds(
                SOURCE_ACCOUNT_NUMBER,
                DESTINATION_ACCOUNT_NUMBER,
                25.00
        );
    }

    @Test
    void transfer_shouldLeaveCachedBalanceUnchanged_whenDatabaseRejectsIt() {
        Account sourceAccount = sourceAccountWithBalance(100.00);
        when(accountRepository.transferFunds(
                SOURCE_ACCOUNT_NUMBER,
                DESTINATION_ACCOUNT_NUMBER,
                125.00
        )).thenReturn(OptionalDouble.empty());

        boolean result = accountService.transfer(
                sourceAccount,
                DESTINATION_ACCOUNT_NUMBER,
                125.00
        );

        assertFalse(result);
        assertEquals(100.00, sourceAccount.getBalance(), 0.001);
    }

    @Test
    void transfer_shouldRejectInvalidAmount_beforeCallingDatabase() {
        Account sourceAccount = sourceAccountWithBalance(100.00);

        assertFalse(accountService.transfer(
                sourceAccount,
                DESTINATION_ACCOUNT_NUMBER,
                0.00
        ));
        verify(accountRepository, never()).transferFunds(
                SOURCE_ACCOUNT_NUMBER,
                DESTINATION_ACCOUNT_NUMBER,
                0.00
        );
    }

    private Account sourceAccountWithBalance(double balance) {
        return new Account(
                null,
                SOURCE_ACCOUNT_NUMBER,
                "pin-hash",
                AccountType.CHECKING,
                balance
        );
    }
}
