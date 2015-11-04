package com.shootr.mobile.domain.service.user;

public interface ChangePasswordGateway {

    void changePassword(String currentPassword, String newPassword) throws
      com.shootr.mobile.domain.exception.InvalidPasswordException;
}
