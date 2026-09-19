package org.half.security;

import org.half.model.User;
import org.half.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignInService {
    private static final Logger log = LoggerFactory.getLogger(SignInService.class);

    private static final UserRepository userRepository = new UserRepository();

    public static User verifyUser(String userName, String userPassword){
        if(userName.isEmpty() || userPassword.isEmpty()) {
            throw new IllegalArgumentException("Can Not Enter Empty Strings.");
        }
        String dataBasePassword = userRepository.getPasswordHash(userName);

        if (dataBasePassword != null) {
            if (PasswordService.verifyPassword(userPassword, dataBasePassword)) {
                return userRepository.getUser(userName);
            }
        }
        return null;
    }
}
