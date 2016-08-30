package com.shootr.mobile.util;

import javax.inject.Inject;

public class DefaultTabUtils implements StringHashUtils {

  private static final int INITIAL_HASH = 7;
  private static final int MULTIPLIER = 31;
  private static final int MOD = 2;
  public static final int DEFAULT_POSITION = 0;
  public static final int POSITION = 1;


  @Inject public DefaultTabUtils() {
  }

  public int getDefaultTabPosition(String text) {
    int hashCode = getHash(text);

    return getPosition(hashCode);
  }

  private int getPosition(int hashCode) {
    if (Math.abs(hashCode) % MOD == 0) {
      return DEFAULT_POSITION;
    } else {
      return POSITION;
    }
  }

  @Override public int getHash(String text) {
    int hashCode = INITIAL_HASH;
    if (text != null) {
      for (int i = 0; i < text.length(); i++) {
        hashCode = hashCode * MULTIPLIER + text.charAt(i);
      }
    }
    return hashCode;
  }
}
