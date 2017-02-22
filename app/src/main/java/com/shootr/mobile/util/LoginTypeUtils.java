package com.shootr.mobile.util;

import java.util.Random;
import javax.inject.Inject;

public class LoginTypeUtils implements RandomUtils {

  private static final int HIGH_VALUE = 2;

  @Inject public LoginTypeUtils() {
  }

  @Override public int getRandom() {
    Random r = new Random();
    return r.nextInt(HIGH_VALUE);
  }

  public boolean shouldShowLongLogin() {
    return getRandom() == 0;
  }
}
