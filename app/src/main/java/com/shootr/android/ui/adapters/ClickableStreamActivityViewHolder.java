package com.shootr.android.ui.adapters;

import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.ui.widgets.StreamTitleSpan;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.ImageLoader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ClickableStreamActivityViewHolder extends GenericActivityViewHolder {

    private final OnStreamTitleClickListener onStreamTitleClickListener;

    public ClickableStreamActivityViewHolder(View view,
      ImageLoader imageLoader,
      AndroidTimeUtils androidTimeUtils, OnAvatarClickListener onAvatarClickListener, OnStreamTitleClickListener onStreamTitleClickListener) {
        super(view, imageLoader, androidTimeUtils, onAvatarClickListener);
        this.onStreamTitleClickListener = onStreamTitleClickListener;
    }

    @Override
    protected void renderText(ActivityModel activity) {
        text.setText(formatActivityComment(activity));
        text.setMovementMethod(new LinkMovementMethod());
        text.addLinks();
    }

    protected CharSequence formatActivityComment(final ActivityModel activity) {
        String commentPattern = getCommentPattern();
        String streamPlaceholder = "\\(stream\\)";
        String streamTitle = activity.getStreamTitle();
        SpannableStringBuilder spannableCheckinPattern = new SpannableStringBuilder(commentPattern);

        replacePlaceholderWithStreamTitleSpan(spannableCheckinPattern,
          streamPlaceholder,
          streamTitle,
          new StreamTitleSpan(activity.getIdStream(), activity.getStreamTitle()) {
              @Override
              public void onStreamClick(String streamId, String streamTitle) {
                  onStreamTitleClickListener.onStreamTitleClick(streamId, streamTitle);
              }
          });

        return spannableCheckinPattern;
    }

    @NonNull
    protected abstract String getCommentPattern();

    private void replacePlaceholderWithStreamTitleSpan(SpannableStringBuilder spannableBuilder,
      String placeholder,
      String replaceText,
      StreamTitleSpan streamTitleSpan) {
        Pattern termsPattern = Pattern.compile(placeholder);
        Matcher termsMatcher = termsPattern.matcher(spannableBuilder.toString());
        if (termsMatcher.find()) {
            int termsStart = termsMatcher.start();
            int termsEnd = termsMatcher.end();
            spannableBuilder.replace(termsStart, termsEnd, replaceText);

            spannableBuilder.setSpan(streamTitleSpan,
              termsStart,
              termsStart + replaceText.length(),
              Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
