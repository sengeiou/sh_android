package com.shootr.mobile.util;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.widgets.UsernameSpan;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShotTextSpannableBuilder implements TextSpannableBuilder {

  private static final String USERNAME_REGEX = "@[-_A-Za-z0-9]{3,25}";

  private final Pattern pattern;

  public ShotTextSpannableBuilder() {
    this.pattern = Pattern.compile(USERNAME_REGEX);
  }

  @Override public CharSequence formatWithUsernameSpans(final CharSequence comment,
      final OnUsernameClickListener clickListener) {
    SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(comment);
    Matcher matcher = pattern.matcher(comment);
    while (matcher.find()) {
      String username = spannableBuilder.subSequence(matcher.start() + 1, matcher.end()).toString();
      UsernameSpan usernameClickSpan = new UsernameSpan(username) {
        @Override public void onUsernameClick(String username) {
          clickListener.onUsernameClick(username);
        }
      };
      spannableBuilder.setSpan(usernameClickSpan, matcher.start(), matcher.end(),
          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    return spannableBuilder;
  }
}
