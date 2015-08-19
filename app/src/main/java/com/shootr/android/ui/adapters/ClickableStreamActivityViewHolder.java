package com.shootr.android.ui.adapters;

import android.content.Context;
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
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.ShotTextSpannableBuilder;
import com.shootr.android.ui.adapters.listeners.OnUsernameClickListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ClickableStreamActivityViewHolder extends ActivityViewHolder {

    private final OnStreamTitleClickListener onStreamTitleClickListener;

    public ClickableStreamActivityViewHolder(View view, PicassoWrapper picasso, AndroidTimeUtils androidTimeUtils,
      ShotTextSpannableBuilder shotTextSpannableBuilder, OnAvatarClickListener onAvatarClickListener,
      OnUsernameClickListener onUsernameClickListener, OnStreamTitleClickListener onStreamTitleClickListener) {
        super(view, picasso, androidTimeUtils, shotTextSpannableBuilder, onAvatarClickListener, onUsernameClickListener);
        this.onStreamTitleClickListener = onStreamTitleClickListener;
    }

    public void render(final ActivityModel activity) {
        super.render(activity);
        text.setMovementMethod(new LinkMovementMethod());
        text.addLinks();
    }

    @Override
    protected CharSequence formatActivityComment(final ActivityModel activity) {
        if (activity.getIdStream() == null) {
            return super.formatActivityComment(activity);
        }
        String commentPattern = getPatternText();
        String streamPlaceholder = "\\(stream\\)";
        String streamTitle = activity.getStreamTitle();
        SpannableStringBuilder spannableCheckinPattern = new SpannableStringBuilder(commentPattern);

        replacePlaceholderWithStreamTitleSpan(spannableCheckinPattern,
          streamPlaceholder,
          streamTitle,
          new StreamTitleSpan(activity.getIdStream(), activity.getStreamTitle()) {
              @Override public void onStreamClick(String streamId, String streamTitle) {
                  onStreamTitleClickListener.onStreamTitleClick(streamId, streamTitle);
              }
          });

        return spannableCheckinPattern;
    }

    @NonNull
    protected abstract String getPatternText();

    private void replacePlaceholderWithStreamTitleSpan(SpannableStringBuilder spannableBuilder, String placeholder,
      String replaceText, StreamTitleSpan streamTitleSpan) {
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

    protected Context getContext() {
        return itemView.getContext();
    }
}
