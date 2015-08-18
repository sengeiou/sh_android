package com.shootr.android.ui.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.ui.widgets.ClickableTextView;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.ShotTextSpannableBuilder;
import com.shootr.android.ui.adapters.listeners.UsernameClickListener;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class ActivityViewHolder extends RecyclerView.ViewHolder {

    private final PicassoWrapper picasso;
    private final AndroidTimeUtils androidTimeUtils;
    private final ShotTextSpannableBuilder shotTextSpannableBuilder;
    private final OnAvatarClickListener onAvatarClickListener;
    private final UsernameClickListener usernameClickListener;

    @Nullable @Bind(R.id.activity_avatar) ImageView avatar;
    @Nullable @Bind(R.id.ativity_user_name) TextView name;
    @Nullable @Bind(R.id.activity_timestamp) TextView elapsedTime;
    @Bind(R.id.activity_text) ClickableTextView text;

    public ActivityViewHolder(View view,
      PicassoWrapper picasso,
      AndroidTimeUtils androidTimeUtils,
      ShotTextSpannableBuilder shotTextSpannableBuilder,
      OnAvatarClickListener onAvatarClickListener,
      UsernameClickListener usernameClickListener) {
        super(view);
        this.androidTimeUtils = androidTimeUtils;
        this.picasso = picasso;
        this.onAvatarClickListener = onAvatarClickListener;
        this.shotTextSpannableBuilder = shotTextSpannableBuilder;
        this.usernameClickListener = usernameClickListener;
        ButterKnife.bind(this, view);
    }

    public void render(final ActivityModel activity) {
        checkNotNull(name);
        checkNotNull(avatar);
        checkNotNull(elapsedTime);

        name.setText(activity.getUsername());
        text.setText(formatActivityComment(activity));
        elapsedTime.setText(androidTimeUtils.getElapsedTime(getContext(), activity.getPublishDate().getTime()));
        picasso.loadProfilePhoto(activity.getUserPhoto()).into(avatar);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAvatarClickListener.onClick(activity.getIdUser(), avatar);
            }
        });
    }

    protected CharSequence formatActivityComment(final ActivityModel activity) {
        return shotTextSpannableBuilder.formatWithUsernameSpans(activity.getComment(), usernameClickListener);
    }


    private Context getContext() {
        return itemView.getContext();
    }
}
