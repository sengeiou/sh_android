package com.shootr.mobile.domain.exception;

public class InvalidLoginMethodForShootrException extends ShootrException {

  public InvalidLoginMethodForShootrException(String message) {
    super(message);
  }

  public InvalidLoginMethodForShootrException(Throwable cause) {
    super(cause);
  }
}
