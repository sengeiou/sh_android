package com.shootr.mobile.util;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import com.shootr.mobile.ui.adapters.listeners.OnPollQuestionClickListener;
import com.shootr.mobile.ui.widgets.PollQuestionSpan;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PollVotedSpannableBuilder {

  private static final String REGEX = "\\(question\\)";

  public CharSequence formatWithPollQuestionSpans(final String idPoll, String question,
      String comment, final OnPollQuestionClickListener clickListener) {
    String streamPlaceholder = REGEX;
    SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(comment);

    PollQuestionSpan pollQuestionSpan = new PollQuestionSpan(idPoll) {
      @Override public void onPollQuestionClick(String pollQuestion) {
        clickListener.onPollQuestionClick(idPoll);
      }
    };

    replacePlaceHolderWithQuestionSpan(question, streamPlaceholder, spannableBuilder,
        pollQuestionSpan);

    return spannableBuilder;
  }

  private void replacePlaceHolderWithQuestionSpan(String question, String streamPlaceholder,
      SpannableStringBuilder spannableBuilder, PollQuestionSpan pollQuestionSpan) {
    Pattern termsPattern = Pattern.compile(streamPlaceholder);
    Matcher termsMatcher = termsPattern.matcher(spannableBuilder.toString());
    if (termsMatcher.find()) {
      int termsStart = termsMatcher.start();
      int termsEnd = termsMatcher.end();
      spannableBuilder.replace(termsStart, termsEnd, question);

      spannableBuilder.setSpan(pollQuestionSpan, termsStart, termsStart + question.length(),
          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
  }
}
