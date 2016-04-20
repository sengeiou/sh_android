package com.shootr.mobile.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.widgets.ContributorButton;
import com.shootr.mobile.util.ImageLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContributorsListAdapter extends BindableAdapter<UserModel> {

    private List<UserModel> users;
    private ImageLoader imageLoader;
    private Boolean isHolder;
    private AddRemoveContributorAdapterCallback callback;
    private Boolean isAdding;

    public ContributorsListAdapter(Context context, ImageLoader imageLoader, Boolean isHolder, Boolean isAdding) {
        super(context);
        this.imageLoader = imageLoader;
        this.isHolder = isHolder;
        this.isAdding = isAdding;
        this.users = new ArrayList<>(0);
    }

    public void setCallback(AddRemoveContributorAdapterCallback callback) {
        this.callback = callback;
    }

    public void setItems(List<UserModel> users) {
        this.users = users;
    }

    public void removeUserFromList(UserModel user){
        users.remove(user);
        notifyDataSetChanged();
    }

    public void removeItems() {
        this.users = Collections.emptyList();
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
        View rowView = inflater.inflate(R.layout.item_list_contributor, container, false);
        rowView.setTag(new ViewHolder(rowView));
        return rowView;
    }

    @Override public void bindView(UserModel item, final int position, View view) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.title.setText(item.getName());

        if (verifiedUser(item)) {
            viewHolder.title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_verified_user_list, 0);
            viewHolder.title.setCompoundDrawablePadding(6);
        } else {
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

        if (isHolder) {
            if(isAdding){
                viewHolder.contributorButton.setVisibility(View.VISIBLE);
                viewHolder.contributorButton.setAddContributor(false);
            } else{
                viewHolder.contributorButton.setVisibility(View.VISIBLE);
                viewHolder.contributorButton.setAddContributor(true);
            }
            setupContributorButtonListener(position, viewHolder);
        } else {
            viewHolder.contributorButton.setVisibility(View.GONE);
        }
    }

    private void setupContributorButtonListener(final int position, final ViewHolder viewHolder) {
        viewHolder.contributorButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if(!isAdding){
                    if (callback != null) {
                        callback.remove(position);
                    }
                }else{
                    if (callback != null) {
                        callback.add(position);
                    }
                }
            }
        });
    }

    private boolean verifiedUser(UserModel userModel) {
        if (userModel.isVerifiedUser() != null) {
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

    public List<UserModel> getItems() {
        return users;
    }

    public static class ViewHolder {

        @Bind(com.shootr.mobile.R.id.user_avatar) ImageView avatar;
        @Bind(R.id.user_name) TextView title;
        @Bind(R.id.user_username) TextView subtitle;
        @Bind(R.id.contributor_button) ContributorButton contributorButton;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface AddRemoveContributorAdapterCallback {

        void add(int position);

        void remove(int position);
    }
}
