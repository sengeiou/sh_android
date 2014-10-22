package gm.mobi.android.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import gm.mobi.android.R;
import gm.mobi.android.db.objects.FollowEntity;
import gm.mobi.android.ui.model.UserModel;
import gm.mobi.android.ui.widgets.FollowButton;
import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends BindableAdapter<UserModel> {

    private List<UserModel> users;
    private Picasso picasso;

    private FollowUnfollowAdapterCallback callback;

    public UserListAdapter(Context context, Picasso picasso) {
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
        return getItem(position).getIdUser();
    }

    @Override public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        View rowView = inflater.inflate(R.layout.item_list_user, container, false);
        rowView.setTag(new ViewHolder(rowView));
        return rowView;
    }

    @Override public void bindView(final UserModel item, final int position, View view) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.name.setText(item.getUserName());
        viewHolder.username.setText(item.getFavoriteTeamName());
        String photo = item.getPhoto();
        if (photo != null && !photo.isEmpty()) {
            picasso.load(photo).into(viewHolder.avatar);
        } else {
            picasso.load(R.drawable.ic_contact_picture_default).into(viewHolder.avatar);
        }

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
                                changeButtonState(viewHolder,FollowEntity.RELATIONSHIP_NONE);
                                callback.unFollow(position);
                            }
                       }else{
                           if(callback!=null){
                               changeButtonState(viewHolder,FollowEntity.RELATIONSHIP_FOLLOWING);
                               callback.follow(position);
                           }
                       }
                }
            });

        }else{
            viewHolder.followButton.setVisibility(View.GONE);
         }
    }

    public void setCallback(FollowUnfollowAdapterCallback callback){
        this.callback = callback;
    }

    public List<UserModel> getItems() {
        return users;
    }

    public static class ViewHolder {
        @InjectView(R.id.user_avatar) ImageView avatar;
        @InjectView(R.id.user_name) TextView name;
        @InjectView(R.id.user_username) TextView username;
        @InjectView(R.id.user_follow_button) FollowButton followButton;
        public int position;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }


    public interface FollowUnfollowAdapterCallback{
        public void follow(int position);
        public void unFollow(int position);
    }

    public void changeButtonState(ViewHolder viewHolder ,int stateFollow){
        if(stateFollow == FollowEntity.RELATIONSHIP_FOLLOWING) {
            viewHolder.followButton.setFollowing(true);
        }else{
            viewHolder.followButton.setFollowing(false);

        }
    }


}
