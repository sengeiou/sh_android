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
import gm.mobi.android.db.objects.User;
import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends BindableAdapter<User> {

    private List<User> users;
    private Picasso picasso;

    public UserListAdapter(Context context, Picasso picasso) {
        super(context);
        this.picasso = picasso;
        this.users = new ArrayList<>(0);
    }

    public void setItems(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public void addItems(List<User> users) {
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    public boolean isFollowButtonVisible() {
        return true;
    }

    @Override public int getCount() {
        return users.size();
    }

    @Override public User getItem(int position) {
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

    @Override public void bindView(User item, int position, View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.name.setText(item.getUserName());
        viewHolder.username.setText(item.getFavoriteTeamName());
        String photo = item.getPhoto();
        if (photo != null && !photo.isEmpty()) {
            picasso.load(photo).into(viewHolder.avatar);
        } else {
            picasso.load(R.drawable.ic_contact_picture_default).into(viewHolder.avatar);
        }
        if (!isFollowButtonVisible()) {
            viewHolder.followButton.setVisibility(View.GONE);
        }
    }

    public List<User> getItems() {
        return users;
    }

    public static class ViewHolder {
        @InjectView(R.id.user_avatar) ImageView avatar;
        @InjectView(R.id.user_name) TextView name;
        @InjectView(R.id.user_username) TextView username;
        @InjectView(R.id.profile_follow_button) View followButton;

        public int position;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
