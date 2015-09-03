package com.shootr.android.ui.adapters;

import android.content.Context;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.util.ImageLoader;

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
