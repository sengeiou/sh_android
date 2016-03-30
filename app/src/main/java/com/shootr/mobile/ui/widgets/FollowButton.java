package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.shootr.mobile.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FollowButton extends FrameLayout {

    @Bind(R.id.follow) View followButton;
    @Bind(R.id.following) View followingButton;
    @Bind(R.id.edit) View editButton;

    private boolean isFollowing;
    private boolean isEditProfile;

    public FollowButton(Context context) {
        super(context);
        init();
    }

    public FollowButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FollowButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setClickable(true);
        LayoutInflater inflater = LayoutInflater.from(getContext());

        inflater.inflate(R.layout.generic_state_button_layout, this);

        ButterKnife.bind(this);

        if (isInEditMode()) {
            setFollowing(false);
        }
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    public void setFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
        this.isEditProfile = false;
        if (isFollowing) {
            followButton.setVisibility(GONE);
            followingButton.setVisibility(VISIBLE);
        } else {
            followButton.setVisibility(VISIBLE);
            followingButton.setVisibility(GONE);
        }
        editButton.setVisibility(GONE);
    }

    public void setEditProfile() {
        this.isEditProfile = true;
        editButton.setVisibility(VISIBLE);
        hideFollowButtons();
    }

    private void hideFollowButtons() {
        followButton.setVisibility(GONE);
        followingButton.setVisibility(GONE);
    }

    public boolean isEditProfile() {
        return isEditProfile;
    }

    public boolean isFollowing() {
        return isFollowing;
    }
}
