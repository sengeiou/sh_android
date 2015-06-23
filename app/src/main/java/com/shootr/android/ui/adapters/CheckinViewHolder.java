package com.shootr.android.ui.adapters;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnEventTitleClickListener;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.ui.widgets.EventTitleSpan;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.ShotTextSpannableBuilder;
import com.shootr.android.util.UsernameClickListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckinViewHolder extends ActivityViewHolder {

    private final OnEventTitleClickListener onEventTitleClickListener;

    public CheckinViewHolder(View view,
      PicassoWrapper picasso,
      AndroidTimeUtils androidTimeUtils,
      ShotTextSpannableBuilder shotTextSpannableBuilder,
      OnAvatarClickListener onAvatarClickListener,
      UsernameClickListener usernameClickListener,
      OnEventTitleClickListener onEventTitleClickListener) {
        super(view, picasso, androidTimeUtils, shotTextSpannableBuilder, onAvatarClickListener, usernameClickListener);
        this.onEventTitleClickListener = onEventTitleClickListener;
    }

    public void render(final ActivityModel activity) {
        super.render(activity);
        text.setMovementMethod(new LinkMovementMethod());
        text.addLinks();
    }

    @Override
    protected CharSequence formatActivityComment(final ActivityModel activity) {
        String checkinPattern = getContext().getString(R.string.checkin_activity_text_pattern);
        String checkinEventPlaceholder = "\\(event\\)";
        String checkinEventTitle = activity.getEventTitle();
        SpannableStringBuilder spannableCheckinPattern = new SpannableStringBuilder(checkinPattern);

        replacePlaceholderWithEventTitleSpan(spannableCheckinPattern,
          checkinEventPlaceholder,
          checkinEventTitle,
          new EventTitleSpan(activity.getIdEvent()) {
              @Override
              public void onEventClick(String eventId) {
                  onEventTitleClickListener.onClick(eventId);
              }
          });

        return spannableCheckinPattern;
    }

    private void replacePlaceholderWithEventTitleSpan(SpannableStringBuilder spannableBuilder,
      String placeholder,
      String replaceText,
      EventTitleSpan eventTitleSpan) {
        Pattern termsPattern = Pattern.compile(placeholder);
        Matcher termsMatcher = termsPattern.matcher(spannableBuilder.toString());
        if (termsMatcher.find()) {
            int termsStart = termsMatcher.start();
            int termsEnd = termsMatcher.end();
            spannableBuilder.replace(termsStart, termsEnd, replaceText);

            spannableBuilder.setSpan(eventTitleSpan,
              termsStart,
              termsStart + replaceText.length(),
              Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private Context getContext() {
        return itemView.getContext();
    }
}
