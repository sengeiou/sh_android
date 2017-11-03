package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;

public class FollowButton extends FrameLayout {

    @BindView(R.id.follow) View followButton;
    @BindView(R.id.following) View followingButton;
    @BindView(R.id.edit) View editButton;

    private boolean isFollowing;
    private boolean isEditProfile;

    public FollowButton(Context context) {
        super(context);
        init(context, null);
    }

    public FollowButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FollowButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        setClickable(true);
        LayoutInflater inflater = LayoutInflater.from(getContext());

        if (attributeSet != null) {
            TypedArray attributes =
                context.obtainStyledAttributes(attributeSet, R.styleable.FollowButton);
            try {
                boolean isDetail = attributes.getBoolean(R.styleable.FollowButton_detail_button, false);
                if (isDetail) {
                    inflater.inflate(R.layout.generic_state_detail_button_layout, this);
                } else {
                    inflater.inflate(R.layout.generic_state_button_layout, this);
                }
            } finally {
                attributes.recycle();
            }
        } else {
            inflater.inflate(R.layout.generic_state_button_layout, this);
        }

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
