package com.shootr.mobile.util;

import android.graphics.Color;
import android.graphics.Typeface;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import javax.inject.Inject;

public class InitialsLoaderTool implements InitialsLoader {

  @Inject public InitialsLoaderTool() {
  }

  @Override public String getLetters(String words) {
    String[] split = words.split(" ");
    String initials;
    if (split.length > 1) {
      String firstWord = split[0];
      String lastWord = split[split.length - 1];
      initials =
          String.valueOf(String.valueOf(firstWord.charAt(0)) + String.valueOf(lastWord.charAt(0)))
              .toUpperCase();
    } else {
      String firstWord = split[0];
      initials = String.valueOf(firstWord.charAt(0)).toUpperCase();
    }
    return initials;
  }

  @Override public Integer getColorForLetters(String letters) {
    ColorGenerator generator = ColorGenerator.MATERIAL;
    return generator.getColor(letters);
  }

  @Override public TextDrawable getTextDrawable(String initials, int backgroundColor) {
    return TextDrawable.builder()
        .beginConfig()
        .width(56)
        .height(56)
        .textColor(Color.WHITE)
        .useFont(Typeface.DEFAULT)
        .fontSize(24)
        .endConfig()
        .buildRound(initials, backgroundColor);
  }

  @Override
  public TextDrawable getCustomTextDrawable(String initials, int backgroundColor, int width,
      int height, int fontSize) {
    return TextDrawable.builder()
        .beginConfig()
        .width(width)
        .height(height)
        .textColor(Color.WHITE)
        .useFont(Typeface.DEFAULT)
        .fontSize(fontSize)
        .endConfig()
        .buildRound(initials, backgroundColor);
  }
}
