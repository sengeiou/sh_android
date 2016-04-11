package com.shootr.mobile.ui.adapters;

import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.view.View;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotClick;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class StreamActivityViewHolder extends GenericActivityViewHolder {

    private final OnShotClick onActivityClickListener;

    public StreamActivityViewHolder(View view, ImageLoader imageLoader, AndroidTimeUtils androidTimeUtils,
      OnAvatarClickListener onAvatarClickListener, OnShotClick onShotClick) {
        super(view, imageLoader, androidTimeUtils, onAvatarClickListener);
        this.onActivityClickListener = onShotClick;
    }

    @Override public void render(ActivityModel activity) {
        super.render(activity);
        text.setText(formatActivityComment(activity));
        if (activity.getShot() != null) {
            enableShotClick(activity);
        } else {
            itemView.setOnClickListener(null);
        }
    }

    private void enableShotClick(final ActivityModel activity) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onActivityClickListener.onShotClick(activity.getShot());
            }
        });
    }

    protected CharSequence formatActivityComment(final ActivityModel activity) {
        String commentPattern = getCommentPattern();
        String streamPlaceholder = "\\(stream\\)";
        String streamTitle = activity.getStreamTitle();
        SpannableStringBuilder spannableCheckinPattern = new SpannableStringBuilder(commentPattern);

        replacePlaceholderWithStreamTitle(spannableCheckinPattern, streamPlaceholder, streamTitle);

        return spannableCheckinPattern;
    }

    @NonNull protected abstract String getCommentPattern();

    private void replacePlaceholderWithStreamTitle(SpannableStringBuilder spannableBuilder, String placeholder,
      String replaceText) {
        Pattern termsPattern = Pattern.compile(placeholder);
        Matcher termsMatcher = termsPattern.matcher(spannableBuilder.toString());
        if (termsMatcher.find()) {
            int termsStart = termsMatcher.start();
            int termsEnd = termsMatcher.end();
            spannableBuilder.replace(termsStart, termsEnd, replaceText);
        }
    }
}
