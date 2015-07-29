package com.shootr.android.ui.adapters.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.android.ui.adapters.UserListAdapter;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.widgets.SuggestedPeopleListView;
import com.shootr.android.util.PicassoWrapper;

public class FriendsAdapter extends UserListAdapter {

    private static final int DEFAULT_TYPE = 0;
    private static final int SUGGESTED_PEOPLE_TYPE = 1;

    private final UserListAdapter suggestedPeopleAdapter;

    private SuggestedPeopleListView suggestedPeopleListView;

    public FriendsAdapter(Context context, PicassoWrapper picasso, UserListAdapter suggestedPeopleAdapter) {
        super(context, picasso);
        this.suggestedPeopleAdapter = suggestedPeopleAdapter;
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
        if (isSuggestedPeopleView(position)) {
            return SUGGESTED_PEOPLE_TYPE;
        } else {
            return DEFAULT_TYPE;
        }
    }

    private boolean isSuggestedPeopleView(int position) {
        return position == super.getCount();
    }

    @Override public int getViewTypeCount() {
        return 2;
    }

    @Override public int getCount() {
        return super.getCount()+1;
    }

    @Override public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        if (!isSuggestedPeopleView(position)) {
            return super.newView(inflater, position, container);
        } else {
            if (suggestedPeopleListView == null) {
                suggestedPeopleListView = new SuggestedPeopleListView(container.getContext());
                suggestedPeopleListView.setAdapter(suggestedPeopleAdapter);
            }
            return suggestedPeopleListView;
        }
    }

    @Override public void bindView(UserModel item, int position, View view) {
        if (!isSuggestedPeopleView(position)) {
            super.bindView(item, position, view);
        }
    }

    @Override public UserModel getItem(int position) {
        return isSuggestedPeopleView(position) ? null : super.getItem(position);
    }
}
