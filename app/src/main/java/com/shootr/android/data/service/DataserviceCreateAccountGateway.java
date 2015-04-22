package com.shootr.android.data.service;

import com.shootr.android.data.entity.UserCreateAccountEntity;
import com.shootr.android.domain.User;
import com.shootr.android.domain.service.user.CreateAccountGateway;
import com.shootr.android.service.ShootrService;
import com.shootr.android.util.SecurityUtils;
import java.io.IOException;
import javax.inject.Inject;

public class DataserviceCreateAccountGateway implements CreateAccountGateway {

    private final ShootrService shootrService;

    @Inject public DataserviceCreateAccountGateway(ShootrService shootrService){
        this.shootrService = shootrService;
    }

    @Override public void performCreateAccount(String username, String email, String hashedPassword) throws IOException {
        UserCreateAccountEntity userCreateAccountEntity = getUserCreateAccountEntity(username, email, hashedPassword);
        shootrService.createAccount(userCreateAccountEntity);
    }

    private UserCreateAccountEntity getUserCreateAccountEntity(String username, String email, String hashedPassword) {
        UserCreateAccountEntity userCreateAccountEntity = new UserCreateAccountEntity();
        userCreateAccountEntity.setUserName(username);
        userCreateAccountEntity.setEmail(email);
        userCreateAccountEntity.setHashedPassword(hashedPassword);
        return userCreateAccountEntity;
    }
}
