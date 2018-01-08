package com.shootr.mobile.domain.exception;

public class MassiveRegisterErrorException extends ShootrException {

  public MassiveRegisterErrorException(String message) {
    super(message);
  }

  public MassiveRegisterErrorException(Throwable cause) {
    super(cause);
  }
}
