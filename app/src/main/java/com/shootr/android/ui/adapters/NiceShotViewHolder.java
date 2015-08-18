package com.shootr.android.ui.adapters;

import android.view.View;
import android.widget.Toast;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.ShotTextSpannableBuilder;
import com.shootr.android.util.UsernameClickListener;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

public class NiceShotViewHolder extends ActivityViewHolder {

    private TimelineAdapter.ViewHolder shotViewHolder;

    public NiceShotViewHolder(final View view,
      PicassoWrapper picasso,
      AndroidTimeUtils androidTimeUtils,
      ShotTextSpannableBuilder shotTextSpannableBuilder,
      OnAvatarClickListener onAvatarClickListener,
      UsernameClickListener usernameClickListener) {
        super(view, picasso, androidTimeUtils, shotTextSpannableBuilder, onAvatarClickListener, usernameClickListener);
        shotViewHolder = new TimelineAdapter.ViewHolder(view, new View.OnClickListener() {
            //TODO
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Avatar", Toast.LENGTH_SHORT).show();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Toast.makeText(v.getContext(), "Image", Toast.LENGTH_SHORT).show();
            }
        }, new TimelineAdapter.VideoClickListener() {
            @Override
            public void onClick(String url) {
                //TODO
                Toast.makeText(view.getContext(), "Video", Toast.LENGTH_SHORT).show();
            }
        }, null, usernameClickListener, androidTimeUtils, picasso, shotTextSpannableBuilder);
        shotViewHolder.niceButton.setVisibility(View.GONE);
    }

    @Override
    public void render(ActivityModel activityModel) {
        text.setText(activityModel.getComment());
        ShotModel shotModel = checkNotNull(activityModel.getShot());
        shotViewHolder.render(shotModel, false);
    }
}
