package com.shootr.mobile.domain.exception;

public class InvalidLoginMethodForFacebookException extends ShootrException {

  public InvalidLoginMethodForFacebookException(String message) {
    super(message);
  }

  public InvalidLoginMethodForFacebookException(Throwable cause) {
    super(cause);
  }
}
