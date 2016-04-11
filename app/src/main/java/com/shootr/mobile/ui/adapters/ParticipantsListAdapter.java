package com.shootr.mobile.ui.adapters;

import android.content.Context;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.util.ImageLoader;

public class ParticipantsListAdapter extends UserListAdapter {

    public ParticipantsListAdapter(Context context, ImageLoader imageLoader) {
        super(context, imageLoader);
    }

    protected boolean showSubtitle(UserModel item) {
        return true;
    }

    protected String getSubtitle(UserModel item) {
        return item.getJoinStreamDate();
    }
}
