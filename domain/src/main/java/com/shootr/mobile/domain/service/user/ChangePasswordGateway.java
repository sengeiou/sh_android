package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.exception.InvalidPasswordException;

public interface ChangePasswordGateway {

    void changePassword(String currentPassword, String newPassword, String language) throws InvalidPasswordException;
}
