package com.shootr.mobile.ui.adapters;

import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.ui.widgets.StreamTitleSpan;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

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

        checkNotNull(streamTitle, "Oh no! Stream from Api is corrupted! idStream=%s", activity.getIdStream());

        replacePlaceholderWithStreamTitleSpan(spannableCheckinPattern,
          streamPlaceholder,
          streamTitle,
          new StreamTitleSpan(activity.getIdStream(), activity.getStreamShortTitle(), activity.getIdStreamAuthor()) {
              @Override
              public void onStreamClick(String streamId, String streamShortTitle, String idAuthor) {
                  onStreamTitleClickListener.onStreamTitleClick(streamId, streamShortTitle, idAuthor);
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
