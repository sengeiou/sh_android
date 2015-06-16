package com.shootr.android.ui.adapters.recyclerview;

import android.content.Context;
import com.shootr.android.ui.adapters.UserListAdapter;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.util.PicassoWrapper;

public class FriendsAdapter extends UserListAdapter {

    public FriendsAdapter(Context context, PicassoWrapper picasso) {
        super(context, picasso);
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
}
