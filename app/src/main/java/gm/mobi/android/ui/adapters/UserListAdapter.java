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
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.ui.model.UserVO;
import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends BindableAdapter<UserVO> {

    private List<UserVO> users;
    private Picasso picasso;

    private FollowUnfollowAdapterCallback callback;

    public UserListAdapter(Context context, Picasso picasso) {
        super(context);
        this.picasso = picasso;
        this.users = new ArrayList<>(0);
    }

    public void setItems(List<UserVO> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public void addItems(List<UserVO> users) {
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    public boolean isFollowButtonVisible() {
        return true;
    }

    @Override public int getCount() {
        return users.size();
    }

    @Override public UserVO getItem(int position) {
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

    @Override public void bindView(UserVO item, final int position, View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.name.setText(item.getUserName());
        viewHolder.username.setText(item.getFavoriteTeamName());
        String photo = item.getPhoto();
        if (photo != null && !photo.isEmpty()) {
            picasso.load(photo).into(viewHolder.avatar);
        } else {
            picasso.load(R.drawable.ic_contact_picture_default).into(viewHolder.avatar);
        }
        if(isFollowButtonVisible()){
            if(item.getRelationship() == Follow.RELATIONSHIP_FOLLOWING){
                viewHolder.followButton.setVisibility(View.GONE);
                viewHolder.followingButton.setVisibility(View.VISIBLE);
            }else if(item.getRelationship() == Follow.RELATIONSHIP_OWN){
                viewHolder.followButton.setVisibility(View.GONE);
                viewHolder.followingButton.setVisibility(View.GONE);
            }else{
                viewHolder.followButton.setVisibility(View.VISIBLE);
                viewHolder.followingButton.setVisibility(View.GONE);
            }
            viewHolder.followingButton.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if(callback!=null){
                        callback.unFollow(position);
                    }
                }
            });

            viewHolder.followButton.setOnClickListener(new View.OnClickListener(){
                @Override public void onClick(View v) {
                    if(callback!=null){
                        callback.follow(position);
                    }
                }
            });
        }else{
            viewHolder.followButton.setVisibility(View.GONE);
            viewHolder.followingButton.setVisibility(View.GONE);
         }

    }

    public void setCallback(FollowUnfollowAdapterCallback callback){
        this.callback = callback;
    }

    public List<UserVO> getItems() {
        return users;
    }

    public static class ViewHolder {
        @InjectView(R.id.user_avatar) ImageView avatar;
        @InjectView(R.id.user_name) TextView name;
        @InjectView(R.id.user_username) TextView username;
        @InjectView(R.id.follow_button) View followButton;
        @InjectView(R.id.following_button) View followingButton;
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
