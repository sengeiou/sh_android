package com.shootr.mobile.util;

import com.amulyakhare.textdrawable.TextDrawable;

public interface InitialsLoader {

  String getLetters(String title);

  Integer getColorForLetters(String letters);

  TextDrawable getTextDrawable(String initials, int backgroundColor);
}
