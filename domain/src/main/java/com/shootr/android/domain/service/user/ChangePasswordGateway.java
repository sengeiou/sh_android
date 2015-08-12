package com.shootr.android.domain.service.user;

import com.shootr.android.domain.exception.InvalidPasswordException;

public interface ChangePasswordGateway {

    void changePassword(String currentPassword, String newPassword) throws InvalidPasswordException;
}
