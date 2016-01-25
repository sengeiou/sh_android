package com.shootr.mobile.ui.adapters;

import android.view.View;
import android.widget.LinearLayout;
import butterknife.Bind;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnHideClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.ShotTextSpannableBuilder;

public class ShotProfileViewHolder extends ShotViewHolder {

    @Bind(R.id.shot_hide_button) LinearLayout hideButton;

    OnHideClickListener onHideClickListener;

    public ShotProfileViewHolder(View view, OnAvatarClickListener avatarClickListener,
      OnVideoClickListener videoClickListener, OnHideClickListener onHideClickListener,
      OnUsernameClickListener onUsernameClickListener, AndroidTimeUtils timeUtils, ImageLoader imageLoader,
      ShotTextSpannableBuilder shotTextSpannableBuilder) {
        super(view,
          avatarClickListener,
          videoClickListener,
          null,
          onUsernameClickListener,
          timeUtils,
          imageLoader,
          shotTextSpannableBuilder);
        this.onHideClickListener=onHideClickListener;
    }

    @Override protected void render(ShotModel shot, boolean shouldShowShortTitle) {
        super.render(shot, shouldShowShortTitle);
        bindHideButton(shot);

    }

    private void bindHideButton(final ShotModel shot){
        hideButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onHideClickListener.onHideClick(shot.getIdShot());
            }
        });
    }
}
