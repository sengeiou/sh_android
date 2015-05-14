package com.shootr.android.ui.adapters.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shootr.android.R;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.ui.adapters.BindableAdapter;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.widgets.FollowButton;
import com.shootr.android.util.PicassoWrapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FriendsAdapter extends BindableAdapter<UserModel> {

    private List<UserModel> users;
    private PicassoWrapper picasso;

    private FollowUnfollowAdapterCallback callback;

    public FriendsAdapter(Context context, PicassoWrapper picasso) {
        super(context);
        this.picasso = picasso;
        this.users = new ArrayList<>(0);
    }

    public void setItems(List<UserModel> users) {
        this.users = users;
    }

    public void addItems(List<UserModel> users) {
        this.users.addAll(users);
    }

    public void removeItems(){
        this.users = null;
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

        if (showSubtitle(item)) {
            viewHolder.subtitle.setText(geWatchingEventTitleForSubtitle(item));
            viewHolder.subtitle.setVisibility(View.VISIBLE);
        } else {
            viewHolder.subtitle.setVisibility(View.GONE);
        }

        String photo = item.getPhoto();
        picasso.loadProfilePhoto(photo).into(viewHolder.avatar);

        if(isFollowButtonVisible()){
            if(item.getRelationship() == FollowEntity.RELATIONSHIP_FOLLOWING){
                viewHolder.followButton.setFollowing(true);
            }else if(item.getRelationship() == FollowEntity.RELATIONSHIP_OWN){
                viewHolder.followButton.setEditProfile();
            }else{
                viewHolder.followButton.setFollowing(false);
            }
            viewHolder.followButton.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if(viewHolder.followButton.isFollowing()){
                        if(callback!=null){
                            callback.unFollow(position);
                        }
                    }else{
                        if(callback!=null){
                            callback.follow(position);
                        }
                    }
                }
            });

        }else{
            viewHolder.followButton.setVisibility(View.GONE);
        }
    }

    private String geWatchingEventTitleForSubtitle(UserModel item) {
        String eventWatchingTitle = item.getEventWatchingTitle();
        if(eventWatchingTitle != null){
            return eventWatchingTitle;
        }
            return "";
    }

    protected boolean showSubtitle(UserModel item) {
        return true;
    }

    protected String getSubtitle(UserModel item) {
        return "";
    }

    public void setCallback(FollowUnfollowAdapterCallback callback){
        this.callback = callback;
    }

    public List<UserModel> getItems() {
        return users;
    }

    public static class ViewHolder {
        @InjectView(R.id.user_avatar)
        ImageView avatar;
        @InjectView(R.id.user_name)
        TextView title;
        @InjectView(R.id.user_username) TextView subtitle;
        @InjectView(R.id.user_follow_button)
        FollowButton followButton;
        public int position;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }


    public interface FollowUnfollowAdapterCallback{
        public void follow(int position);
        public void unFollow(int position);
    }

}
