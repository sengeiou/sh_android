package com.shootr.android.domain.service.user;

import com.shootr.android.domain.ForgotPasswordResult;
import java.io.IOException;

public interface ResetPasswordGateway {

    ForgotPasswordResult performPasswordReset(String usernameOrEmail) throws IOException;

}
