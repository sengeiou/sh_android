package com.shootr.mobile.util;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import com.shootr.mobile.ui.adapters.listeners.OnPollQuestionClickListener;
import com.shootr.mobile.ui.widgets.PollQuestionSpan;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PollQuestionSpannableBuilder {

  public CharSequence formatWithPollQuestionSpans(final String idPoll, final String streamTitle,
      String question, CharSequence comment, final OnPollQuestionClickListener clickListener) {

    SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(comment);

    PollQuestionSpan pollQuestionSpan = new PollQuestionSpan(idPoll) {
      @Override public void onPollQuestionClick(String pollQuestion) {
        clickListener.onPollQuestionClick(idPoll, streamTitle);
      }
    };
    spannableBuilder.setSpan(pollQuestionSpan, comment.length() - question.length(),
        comment.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    return spannableBuilder;
  }

  public CharSequence formatPollQuestionSpans(final String idPoll, final String streamTitle,
      String question, CharSequence comment, final OnPollQuestionClickListener clickListener) {
    SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(comment);
    question = question.trim();
    Pattern pattern = Pattern.compile(Pattern.quote(question));
    Matcher matcher = pattern.matcher(spannableBuilder.toString());

    if (matcher.find()) {
      PollQuestionSpan pollQuestionSpan = new PollQuestionSpan(idPoll) {
        @Override public void onPollQuestionClick(String pollQuestion) {
          clickListener.onPollQuestionClick(idPoll, streamTitle);
        }
      };
      spannableBuilder.setSpan(pollQuestionSpan, matcher.start(), spannableBuilder.length(),
          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    return spannableBuilder;
  }
}
