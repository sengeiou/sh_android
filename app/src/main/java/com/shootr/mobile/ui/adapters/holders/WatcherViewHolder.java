package com.shootr.mobile.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnFollowUnfollowListener;
import com.shootr.mobile.ui.adapters.listeners.OnUserClickListener;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.widgets.AvatarView;
import com.shootr.mobile.ui.widgets.FollowButton;
import com.shootr.mobile.util.ImageLoader;
import java.util.Set;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class WatcherViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final OnUserClickListener onUserClickListener;
    private final ImageLoader imageLoader;
    private final OnFollowUnfollowListener onFollowUnfollowListener;
    private final Set<String> keepFollowButtonIds;

    @BindView(R.id.watcher_user_avatar) AvatarView avatar;
    @BindView(R.id.watcher_user_name) TextView name;
    @BindView(R.id.watcher_user_watching) TextView watchingText;
    @BindView(R.id.user_follow_button) FollowButton followButton;

    private String userId;

    public WatcherViewHolder(View itemView, OnUserClickListener onUserClickListener, ImageLoader imageLoader,
      OnFollowUnfollowListener onFollowUnfollowListener, Set<String> keepFollowButtonIds) {
        super(itemView);
        this.onUserClickListener = onUserClickListener;
        this.imageLoader = imageLoader;
        this.onFollowUnfollowListener = onFollowUnfollowListener;
        this.keepFollowButtonIds = keepFollowButtonIds;
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
    }

    public void bind(final UserModel userModel) {
        userId = userModel.getIdUser();
        name.setText(userModel.getUsername());
        setupVerifiedStatus(userModel);
        watchingText.setText(userModel.getJoinStreamDate());
        imageLoader.loadProfilePhoto(userModel.getPhoto(), avatar, userModel.getUsername());
        setupFollowButton(userModel);
    }

    private void setupVerifiedStatus(UserModel userModel) {
        if (verifiedUser(userModel)) {
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_verified_user_list, 0);
            name.setCompoundDrawablePadding(6);
        } else {
            name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    private void setupFollowButton(final UserModel userModel) {
        if (userModel.isMe()) {
            followButton.setVisibility(View.GONE);
            followButton.setEditProfile();
        } else if (userModel.isFollowing()) {
            followButton.setVisibility(View.VISIBLE);
            followButton.setFollowing(true);
        } else {
            followButton.setVisibility(View.VISIBLE);
            followButton.setFollowing(false);
        }
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (followButton.isFollowing()) {
                    onFollowUnfollowListener.onUnfollow(userModel);
                    followButton.setFollowing(false);
                } else {
                    onFollowUnfollowListener.onFollow(userModel);
                    followButton.setFollowing(true);
                    keepFollowButtonIds.add(userModel.getIdUser());
                }
            }
        });
    }

    @Override public void onClick(View v) {
        checkNotNull(userId);
        if (onUserClickListener != null) {
            onUserClickListener.onUserClick(userId);
        }
    }

    private boolean verifiedUser(UserModel userModel) {
        if (userModel.isVerifiedUser() != null) {
            return userModel.isVerifiedUser();
        }
        return false;
    }
}
