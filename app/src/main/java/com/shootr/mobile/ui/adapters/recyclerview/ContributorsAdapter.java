package com.shootr.mobile.ui.adapters.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.UserListAdapter;
import com.shootr.mobile.ui.adapters.listeners.OnUserClickListener;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.util.ImageLoader;

public class ContributorsAdapter extends UserListAdapter {

    private static final int DEFAULT_TYPE = 0;
    private static final int INVITE_CONTRIBUTORS_TYPE = 1;

    private OnUserClickListener onUserClickListener;

    public ContributorsAdapter(Context context, ImageLoader imageLoader, OnUserClickListener onUserClickListener) {
        super(context, imageLoader);
        this.onUserClickListener = onUserClickListener;
    }

    @Override public boolean isFollowButtonVisible() {
        return false;
    }

    @Override protected boolean showSubtitle(UserModel item) {
        return true;
    }

    @Override protected String getSubtitle(UserModel item) {
        return usernameInSubtitleFormat(item);
    }

    private String usernameInSubtitleFormat(UserModel item) {
        return "@"+item.getUsername();
    }

    @Override public int getItemViewType(int position) {
        if (position == 0) {
            return INVITE_CONTRIBUTORS_TYPE;
        } else {
            return DEFAULT_TYPE;
        }
    }

    private boolean isSuggestedPeopleView(int position) {
        return position == super.getCount()+1;
    }

    @Override public int getViewTypeCount() {
        return 3;
    }

    @Override public int getCount() {
        return super.getCount()+2;
    }

    @Override public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        switch (getItemViewType(position)) {
            case INVITE_CONTRIBUTORS_TYPE:
                return createInviteContributorsView(inflater, container);
            case DEFAULT_TYPE:
            default:
                return super.newView(inflater, position, container);
        }
    }

    @Override public void bindView(UserModel item, int position, View view) {
        if (getItemViewType(position) == DEFAULT_TYPE) {
            super.bindView(item, position, view);
        }
    }

    private View createInviteContributorsView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.include_add_contributor, container, false);
    }

    @Override public UserModel getItem(int layoutPosition) {
        int userPosition = layoutPosition - 1;
        return layoutPosition == 0 || isSuggestedPeopleView(layoutPosition) ? null : super.getItem(userPosition);
    }

    @Override
    public boolean isEnabled(int position) {
        return !isSuggestedPeopleView(position);
    }
}
