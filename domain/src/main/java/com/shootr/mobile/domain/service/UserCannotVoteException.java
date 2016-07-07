package com.shootr.mobile.domain.service;

import com.shootr.mobile.domain.exception.ShootrException;

public class UserCannotVoteException extends ShootrException {

  public UserCannotVoteException(Throwable cause) {
    super(cause);
  }
}
