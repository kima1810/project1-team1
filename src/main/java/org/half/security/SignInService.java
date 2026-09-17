package org.half.security;

import org.half.model.User;
import org.half.repository.UserRepository;
import org.half.view.MainMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignInService {
    private static final Logger log = LoggerFactory.getLogger(SignInService.class);

    public static User verifyUser(String userName, String userPassword){
        if(userName.isEmpty() || userPassword.isEmpty()) {
            throw new IllegalArgumentException("Can Not Enter Empty Strings.");
        }
        String dataBasePassword = UserRepository.getPasswordHash(userName);

        if (dataBasePassword != null) {
            if (PasswordService.verifyPassword(userPassword, dataBasePassword)) {
                return UserRepository.getUser(userName);
            }
        }
        return null;
    }
}
