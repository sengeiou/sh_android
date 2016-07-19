package com.shootr.mobile.util;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import com.shootr.mobile.ui.adapters.listeners.OnPollQuestionClickListener;
import com.shootr.mobile.ui.widgets.PollQuestionSpan;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PollQuestionSpannableBuilder {

  public CharSequence formatWithPollQuestionSpans(final String idPoll, String question,
      CharSequence comment, final OnPollQuestionClickListener clickListener) {

    SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(comment);

    String pollQuestion = spannableBuilder.subSequence(0, question.length()).toString();

    PollQuestionSpan pollQuestionSpan = new PollQuestionSpan(idPoll, pollQuestion) {
      @Override public void onPollQuestionClick(String pollQuestion) {
        clickListener.onPollQuestionClick(idPoll);
      }
    };
    spannableBuilder.setSpan(pollQuestionSpan, 0, question.length(),
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    return spannableBuilder;
  }

  public CharSequence formatSharedPollQuestionSpans(final String idPoll, String question,
      CharSequence comment, final OnPollQuestionClickListener clickListener) {
    SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(comment);

    Pattern pattern = Pattern.compile(question);
    Matcher matcher = pattern.matcher(spannableBuilder.toString());

    if (matcher.find()) {
      String pollQuestion =
          spannableBuilder.subSequence(matcher.start(), matcher.end()).toString();
      PollQuestionSpan pollQuestionSpan = new PollQuestionSpan(idPoll, pollQuestion) {
        @Override public void onPollQuestionClick(String pollQuestion) {
          clickListener.onPollQuestionClick(idPoll);
        }
      };
      spannableBuilder.setSpan(pollQuestionSpan, matcher.start(), matcher.end() + 1,
          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    return spannableBuilder;
  }
}
