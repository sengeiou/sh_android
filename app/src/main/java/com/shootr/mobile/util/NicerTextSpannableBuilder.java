package com.shootr.mobile.util;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.widgets.NicerSpan;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NicerTextSpannableBuilder implements TextSpannableBuilder {

    private static final String USERNAME_REGEX = "[.-_A-Za-z0-9]+,";

    private final Pattern pattern;

    public NicerTextSpannableBuilder() {
        this.pattern = Pattern.compile(USERNAME_REGEX);
    }

    @Override
    public CharSequence formatWithUsernameSpans(CharSequence comment, final OnUsernameClickListener clickListener) {
        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(comment);
        Matcher matcher = pattern.matcher(comment);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String username = spannableBuilder.subSequence(start, end - 1).toString();

            NicerSpan nicerSpan = new NicerSpan(username) {
                @Override public void onUsernameClick(String username) {
                    clickListener.onUsernameClick(username);
                }
            };
            spannableBuilder.setSpan(nicerSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableBuilder.subSequence(0, comment.length() - 2);
    }
}
