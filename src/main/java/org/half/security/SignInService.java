package org.half.security;

import org.half.model.User;
import org.half.repository.UserRepository;

public class SignInService {
    public static User verifyUser(String userName, String userPassword){
        String dataBasePassword = UserRepository.getPasswordHash(userName);

        if (dataBasePassword != null) {
            if (PasswordService.verifyPassword(userPassword, dataBasePassword)) {
                return UserRepository.getUser(userName);
            }
        }
        return null;
    }
}
