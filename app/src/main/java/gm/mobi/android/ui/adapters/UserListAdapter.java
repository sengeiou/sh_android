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
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.User;
import java.util.List;

public class UserListAdapter extends BindableAdapter<User> {

    private List<User> users;
    private Picasso picasso;

    public UserListAdapter(Context context, Picasso picasso, List<User> users) {
        super(context);
        this.picasso = picasso;
        this.users = users;
    }

    public void setItems(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
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
        viewHolder.name.setText(item.getName());
        viewHolder.username.setText(item.getUserName());
        picasso.load(item.getPhoto()).into(viewHolder.avatar);
    }

    public static class ViewHolder {
        @InjectView(R.id.user_avatar) ImageView avatar;
        @InjectView(R.id.user_name) TextView name;
        @InjectView(R.id.user_username) TextView username;
        public int position;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
