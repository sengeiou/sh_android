package com.shootr.android.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;

public class FollowButton extends FrameLayout {

    @InjectView(R.id.follow) View followButton;
    @InjectView(R.id.following) View followingButton;
    @InjectView(R.id.edit) View editButton;

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

        inflater.inflate(R.layout.follow_button_layout, this);

        ButterKnife.inject(this);

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
