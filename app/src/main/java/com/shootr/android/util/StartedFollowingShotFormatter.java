package com.shootr.android.util;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StartedFollowingShotFormatter {

    private String username;
    private UsernameClickListener onClickListener;

    public StartedFollowingShotFormatter() {

    }

    public SpannableString renderStartedFollowingShotSpan(String comment, UsernameClickListener onClickListener) {
        SpannableString spannableString = new SpannableString(comment);
        this.onClickListener = onClickListener;
        return replacePatternWithClickableText(spannableString);
    }

    public boolean commentIsAStartedFollowingShot(String comment) {
        Pattern termsPattern = Pattern.compile("Started following");
        Matcher termsMatcher = termsPattern.matcher(comment);
        if (termsMatcher.find()) {
            return true;
        }
        return false;
    }

    private SpannableString replacePatternWithClickableText(SpannableString spannableBuilder) {
        SpannableString spanWithClick = spannableBuilder;
        Pattern termsPattern = Pattern.compile("@[_A-Za-z0-9]+");
        Matcher termsMatcher = termsPattern.matcher(spanWithClick.toString());
        if (termsMatcher.find()) {
            int termsStart = termsMatcher.start();
            int termsEnd = termsMatcher.end();

            username = extractUsernameFromExpression(spanWithClick, termsStart, termsEnd);

            CharacterStyle termsSpan = new ClickableSpan() {
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }

                @Override public void onClick(View widget) {
                    onClickListener.onClickPassingUsername(username);
                }
            };
            spanWithClick.setSpan(termsSpan,
                    termsStart,
                    termsEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spanWithClick;
    }

    private String extractUsernameFromExpression(SpannableString spanWithClick, int termsStart, int termsEnd) {
        return spanWithClick.toString().substring(termsStart+1,termsEnd);
    }

    public String getUsername(){
        return username;
    }
}
