package com.shootr.mobile.ui.adapters.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.BindableAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnMentionClickListener;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.util.ImageLoader;
import java.util.ArrayList;
import java.util.List;

public class MentionsAdapter extends BindableAdapter<UserModel> {

    private final OnMentionClickListener onMentionClickListener;

    private List<UserModel> users;
    private ImageLoader imageLoader;

    public MentionsAdapter(Context context, OnMentionClickListener onMentionClickListener, ImageLoader imageLoader) {
        super(context);
        this.onMentionClickListener = onMentionClickListener;
        this.imageLoader = imageLoader;
        this.users = new ArrayList<>(0);
    }

    public void setItems(List<UserModel> users) {
        this.users = users;
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
        rowView.setTag(new ViewHolder(rowView, onMentionClickListener));
        return rowView;
    }

    @Override public void bindView(final UserModel item, final int position, View view) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.setUser(item);

        viewHolder.title.setText(item.getName());

        if (verifiedUser(item)) {
            viewHolder.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_verified_user_list, 0);
            viewHolder.title.setCompoundDrawablePadding(6);
        } else {
            viewHolder.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        viewHolder.subtitle.setText(getSubtitle(item));
        viewHolder.subtitle.setVisibility(View.VISIBLE);

        String photo = item.getPhoto();
        imageLoader.loadProfilePhoto(photo, viewHolder.avatar);
    }

    private boolean verifiedUser(UserModel userModel) {
        if (userModel.isVerifiedUser() != null) {
            return userModel.isVerifiedUser();
        }
        return false;
    }

    protected String getSubtitle(UserModel item) {
        return String.format("@%s", item.getUsername());
    }

    public static class ViewHolder {

        @Bind(R.id.user_avatar) ImageView avatar;
        @Bind(R.id.user_name) TextView title;
        @Bind(R.id.user_username) TextView subtitle;
        private UserModel user;

        public ViewHolder(View view, final OnMentionClickListener onMentionClickListener) {
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    onMentionClickListener.mention(user);
                }
            });
        }

        public void setUser(UserModel user) {
            this.user = user;
        }
    }
}