package com.shootr.mobile.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shootr.mobile.R;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.widgets.FollowButton;
import com.shootr.mobile.util.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UserListAdapter extends BindableAdapter<UserModel> {

    private List<UserModel> users;
    private ImageLoader imageLoader;

    private FollowUnfollowAdapterCallback callback;

    public UserListAdapter(Context context, ImageLoader imageLoader) {
        super(context);
        this.imageLoader = imageLoader;
        this.users = new ArrayList<>(0);
    }

    public void setItems(List<UserModel> users) {
        this.users = users;
    }

    public void addItems(List<UserModel> users) {
        this.users.addAll(users);
    }

    public void removeItems() {
        this.users = Collections.emptyList();
    }

    public boolean isFollowButtonVisible() {
        return true;
    }

    @Override public int getCount() {
        return users.size();
    }

    @Override public UserModel getItem(int position) {
        return users.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        View rowView = inflater.inflate(R.layout.item_list_user, container, false);
        rowView.setTag(new ViewHolder(rowView));
        return rowView;
    }

    @Override public void bindView(final UserModel item, final int position, View view) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.title.setText(item.getName());

        if (verifiedUser(item)) {
            viewHolder.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_verified_user_list, 0);
            viewHolder.title.setCompoundDrawablePadding(6);
        }else{
            viewHolder.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        if (showSubtitle(item)) {
            viewHolder.subtitle.setText(getSubtitle(item));
            viewHolder.subtitle.setVisibility(View.VISIBLE);
        } else {
            viewHolder.subtitle.setVisibility(View.GONE);
        }

        String photo = item.getPhoto();
        imageLoader.loadProfilePhoto(photo, viewHolder.avatar);

        if (isFollowButtonVisible()) {
            if (item.getRelationship() == FollowEntity.RELATIONSHIP_FOLLOWING) {
                viewHolder.followButton.setVisibility(View.VISIBLE);
                viewHolder.followButton.setFollowing(true);
            } else if (item.getRelationship() == FollowEntity.RELATIONSHIP_OWN) {
                viewHolder.followButton.setVisibility(View.GONE);
                viewHolder.followButton.setEditProfile();
            } else {
                viewHolder.followButton.setVisibility(View.VISIBLE);
                viewHolder.followButton.setFollowing(false);
            }
            viewHolder.followButton.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (viewHolder.followButton.isFollowing()) {
                        if (callback != null) {
                            callback.unFollow(position);
                        }
                    } else {
                        if (callback != null) {
                            callback.follow(position);
                        }
                    }
                }
            });
        } else {
            viewHolder.followButton.setVisibility(View.GONE);
        }
    }

    private boolean verifiedUser(UserModel userModel) {
        if(userModel.isVerifiedUser()!=null) {
            return userModel.isVerifiedUser();
        }
        return false;
    }

    protected boolean showSubtitle(UserModel item) {
        return true;
    }

    protected String getSubtitle(UserModel item) {
        return getUsernameForSubtitle(item);
    }

    private String getUsernameForSubtitle(UserModel item) {
        return String.format("@%s", item.getUsername());
    }

    public void setCallback(FollowUnfollowAdapterCallback callback) {
        this.callback = callback;
    }

    public List<UserModel> getItems() {
        return users;
    }

    public static class ViewHolder {

        @Bind(R.id.user_avatar) ImageView avatar;
        @Bind(R.id.user_name) TextView title;
        @Bind(R.id.user_username) TextView subtitle;
        @Bind(R.id.user_follow_button) FollowButton followButton;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface FollowUnfollowAdapterCallback {

        void follow(int position);

        void unFollow(int position);
    }
}
