package com.shootr.android.util;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import com.shootr.android.ui.adapters.listeners.UsernameClickListener;
import com.shootr.android.ui.widgets.UsernameSpan;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShotTextSpannableBuilder {

    private static final String USERNAME_REGEX = "@[_A-Za-z0-9]+";

    private final Pattern pattern;

    public ShotTextSpannableBuilder() {
        this.pattern = Pattern.compile(USERNAME_REGEX);
    }

    public CharSequence formatWithUsernameSpans(final CharSequence comment, final UsernameClickListener clickListener) {
        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(comment);
        Matcher matcher = pattern.matcher(comment);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String username = spannableBuilder.subSequence(start + 1, end).toString();

            UsernameSpan usernameClickSpan = new UsernameSpan(username) {
                @Override public void onUsernameClick(String username) {
                    clickListener.onClick(username);
                }
            };
            spannableBuilder.setSpan(usernameClickSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableBuilder;
    }
}
