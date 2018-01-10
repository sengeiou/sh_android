package com.shootr.mobile.data.service;

import android.support.annotation.NonNull;
import com.shootr.mobile.data.api.entity.CreateAccountApiEntity;
import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.exception.ErrorInfo;
import com.shootr.mobile.data.api.service.AuthApiService;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.data.mapper.UserEntityMapper;
import com.shootr.mobile.domain.exception.EmailAlreadyExistsException;
import com.shootr.mobile.domain.exception.MassiveRegisterErrorException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrError;
import com.shootr.mobile.domain.exception.UsernameAlreadyExistsException;
import com.shootr.mobile.domain.model.user.LoginResult;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.service.user.CreateAccountGateway;
import com.shootr.mobile.domain.utils.DeviceFactory;
import java.io.IOException;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class ServiceCreateAccountGateway implements CreateAccountGateway {

  private final AuthApiService authApiService;
  private final UserEntityMapper userEntityMapper;
  private final DeviceFactory deviceFactory;

  @Inject public ServiceCreateAccountGateway(AuthApiService authApiService,
      UserEntityMapper userEntityMapper, DeviceFactory deviceFactory) {
    this.authApiService = authApiService;
    this.userEntityMapper = userEntityMapper;
    this.deviceFactory = deviceFactory;
  }

  @Override public LoginResult performCreateAccount(String username, String email, String password,
      String locale) throws EmailAlreadyExistsException, UsernameAlreadyExistsException,
      MassiveRegisterErrorException {
    try {
      return sendCreateAccount(username, email, password, locale);
    } catch (ApiException apiException) {
      if (ErrorInfo.EmailAlreadyExistsException == apiException.getErrorInfo()) {
        throw new EmailAlreadyExistsException(apiException);
      } else if (String.valueOf(apiException.getErrorInfo().code())
          .equals(ShootrError.ERROR_CODE_MASSIVE_REGISTER)) {
        throw new MassiveRegisterErrorException(apiException);
      } else if (ErrorInfo.UserNameAlreadyExistsException == apiException.getErrorInfo()) {
        throw new UsernameAlreadyExistsException(apiException);
      } else {
        throw new ServerCommunicationException(apiException);
      }
    } catch (IOException networkError) {
      throw new ServerCommunicationException(networkError);
    }
  }

  @NonNull protected LoginResult sendCreateAccount(String username, String email, String password,
      String locale) throws ApiException, IOException {
    CreateAccountApiEntity createAccountApiEntity =
        new CreateAccountApiEntity(username, email, password, locale, deviceFactory.getAndroidId(),
            deviceFactory.getAdvertisingId());
    UserEntity newLoggedInUser = authApiService.createAccount(createAccountApiEntity);
    checkNotNull(newLoggedInUser);
    User loggedInUser = userEntityMapper.transform(newLoggedInUser);
    String sessionToken = newLoggedInUser.getSessionToken();
    return new LoginResult(loggedInUser, sessionToken);
  }
}
