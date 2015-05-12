package com.shootr.android.util;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StartedFollowingShotFormatter {

    public StartedFollowingShotFormatter() {

    }

    public SpannableStringBuilder renderStartedFollowingShotSpan(String comment, final Context context) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(comment);
        final View.OnClickListener startedFollowingShotListener = new View.OnClickListener() {
            @Override public void onClick(View v) {
                Toast.makeText(context, "username", Toast.LENGTH_SHORT).show();
            }
        };
        return replacePatternWithClickableText(spannableStringBuilder, startedFollowingShotListener);
    }

    public boolean commentIsAStartedFollowingShot(String comment) {
        Pattern termsPattern = Pattern.compile("Started following");
        Matcher termsMatcher = termsPattern.matcher(comment);
        if (termsMatcher.find()) {
            return true;
        }
        return false;
    }

    private SpannableStringBuilder replacePatternWithClickableText(SpannableStringBuilder spannableBuilder,
                                                                   final View.OnClickListener onClick) {
        SpannableStringBuilder spanWithClick = spannableBuilder;
        Pattern termsPattern = Pattern.compile("@[_A-Za-z0-9]+");
        Matcher termsMatcher = termsPattern.matcher(spanWithClick.toString());
        if (termsMatcher.find()) {
            int termsStart = termsMatcher.start();
            int termsEnd = termsMatcher.end();

            CharacterStyle termsSpan = new ClickableSpan() {
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }

                @Override public void onClick(View widget) {
                    onClick.onClick(widget);
                }
            };
            spanWithClick.setSpan(termsSpan,
                    termsStart,
                    termsEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spanWithClick;
    }
}
