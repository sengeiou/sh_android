package com.shootr.mobile.util;

import com.amulyakhare.textdrawable.TextDrawable;

public interface InitialsLoader {

  String getLetters(String title);

  Integer getColorForLetters(String letters);

  TextDrawable getTextDrawable(String initials, int backgroundColor);

  TextDrawable getCustomTextDrawable(String initials, int backgroundColor, int width, int height,
      int fontSize);
}
