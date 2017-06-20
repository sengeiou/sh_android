package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.exception.ShootrException;

public class UserCannotVoteDueToDeviceException extends ShootrException {

  public UserCannotVoteDueToDeviceException(Throwable cause) {
    super(cause);
  }
}
