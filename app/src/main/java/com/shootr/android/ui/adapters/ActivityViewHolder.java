package com.shootr.android.ui.adapters;

import android.content.Context;
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
import com.shootr.android.util.UsernameClickListener;

public class ActivityViewHolder extends RecyclerView.ViewHolder {

    private final PicassoWrapper picasso;
    private final AndroidTimeUtils androidTimeUtils;
    private final ShotTextSpannableBuilder shotTextSpannableBuilder;
    private final OnAvatarClickListener onAvatarClickListener;
    private final UsernameClickListener usernameClickListener;

    @Bind(R.id.activity_avatar) public ImageView avatar;
    @Bind(R.id.ativity_user_name) public TextView name;
    @Bind(R.id.activity_timestamp) public TextView elapsedTime;
    @Bind(R.id.activity_text) public ClickableTextView text;

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
