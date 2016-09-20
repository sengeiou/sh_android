package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.exception.InvalidForgotPasswordException;
import com.shootr.mobile.domain.model.user.ForgotPasswordResult;
import java.io.IOException;

public interface ResetPasswordGateway {

    ForgotPasswordResult performPasswordReset(String usernameOrEmail)
      throws IOException, InvalidForgotPasswordException;
}
