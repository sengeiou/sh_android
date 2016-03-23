package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.mobile.R;

public class FollowButton extends FrameLayout {

    @Bind(R.id.follow) View followButton;
    @Bind(R.id.following) View followingButton;
    @Bind(R.id.edit) View editButton;
    @Bind(R.id.add_contributor) View addContributor;
    @Bind(R.id.added_contributor) View addedContributor;

    private boolean isFollowing;
    private boolean isEditProfile;
    private boolean isAddContributor;

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
        this.isAddContributor = false;
        if (isFollowing) {
            followButton.setVisibility(GONE);
            followingButton.setVisibility(VISIBLE);
        } else {
            followButton.setVisibility(VISIBLE);
            followingButton.setVisibility(GONE);
        }
        editButton.setVisibility(GONE);
        hideContributorButtons();
    }

    public void setAddContributor(boolean isAdded) {
        this.isFollowing = false;
        this.isEditProfile = false;
        this.isAddContributor = true;
        if (isAdded) {
            addContributor.setVisibility(GONE);
            addedContributor.setVisibility(VISIBLE);
        } else {
            addContributor.setVisibility(VISIBLE);
            addedContributor.setVisibility(GONE);
        }
        editButton.setVisibility(GONE);
        hideFollowButtons();
    }

    public void setEditProfile() {
        this.isEditProfile = true;
        this.isAddContributor = false;
        editButton.setVisibility(VISIBLE);
        hideFollowButtons();
        hideContributorButtons();
    }

    private void hideFollowButtons() {
        followButton.setVisibility(GONE);
        followingButton.setVisibility(GONE);
    }

    private void hideContributorButtons() {
        addContributor.setVisibility(GONE);
        addedContributor.setVisibility(GONE);
    }

    public boolean isEditProfile() {
        return isEditProfile;
    }

    public boolean isAddContributor() {
        return isAddContributor;
    }

    public boolean isFollowing() {
        return isFollowing;
    }
}
