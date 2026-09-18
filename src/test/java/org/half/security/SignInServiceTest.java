package org.half.security;

import org.half.model.User;
import org.half.repository.AccountRepository;
import org.half.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SignInServiceTest {

    @Test
    void verifyUser_enteredEmptyUsername_shouldThrowException(){

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> SignInService.verifyUser("", "password")
        );

        assertEquals("Can Not Enter Empty Strings.",
                exception.getMessage()
        );
    }

    @Test
    void verifyUser_enteredEmptyPassword_shouldThrowException(){

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> SignInService.verifyUser("UserAccount", "")
        );

        assertEquals("Can Not Enter Empty Strings.",
                exception.getMessage()
        );
    }

    @Test
    void verifyUser_userDoesNotExist_returnsNull(){
        try(MockedStatic<UserRepository> userRepositoryMocked = mockStatic(UserRepository.class)){

            userRepositoryMocked
                    .when(() -> UserRepository.getPasswordHash("1234567"))
                    .thenReturn(null);

            User activeUser = SignInService.verifyUser("1234567", "passwordtest");

            assertNull(activeUser);

            userRepositoryMocked.verify(
                    () -> UserRepository.getUser("1234567"), never()
            );
        }
    }

    @Test
    void verifyUser_userExistAndPasswordIsIncorrect_returnsNull(){

        try(MockedStatic<UserRepository> userRepositoryMocked = mockStatic(UserRepository.class);
            MockedStatic<PasswordService> passwordServiceMocked = mockStatic(PasswordService.class)){

            userRepositoryMocked
                    .when(() -> UserRepository.getPasswordHash("UserAccount"))
                    .thenReturn("hashedPassword");

            passwordServiceMocked
                    .when(() -> PasswordService.verifyPassword(
                            "wrongPassword",
                            "hashedPassword"))
                    .thenReturn(false);

            User activeUser = SignInService.verifyUser("UserAccount", "wrongPassword");

            assertNull(activeUser);

            userRepositoryMocked.verify(
                    () -> UserRepository.getUser("UserAccount"), never()
            );
        }
    }

    @Test
    void verifyUser_userExistsAndPasswordIsCorrect_returnsUser(){

        User expectedUser = mock(User.class);

        try(MockedStatic<UserRepository> userRepositoryMocked = mockStatic(UserRepository.class);
            MockedStatic<PasswordService> passwordServiceMocked = mockStatic(PasswordService.class)){

            userRepositoryMocked
                    .when(() -> UserRepository.getPasswordHash("UserAccount"))
                    .thenReturn("hashedPassword");

            passwordServiceMocked
                    .when(() -> PasswordService.verifyPassword(
                            "correctPassword",
                            "hashedPassword"))
                    .thenReturn(true);

            userRepositoryMocked
                    .when(() ->UserRepository.getUser("UserAccount"))
                    .thenReturn(expectedUser);

            User activeUser = SignInService.verifyUser("UserAccount", "correctPassword");

            assertNotNull(activeUser);
            assertSame(expectedUser, activeUser);

            userRepositoryMocked.verify(
                    () ->UserRepository.getPasswordHash("UserAccount")
            );

            passwordServiceMocked.verify(
                    () -> PasswordService.verifyPassword(
                            "correctPassword",
                            "hashedPassword"
                    )
            );

            userRepositoryMocked.verify(
                    () -> UserRepository.getUser("UserAccount")
            );
        }
    }
}
