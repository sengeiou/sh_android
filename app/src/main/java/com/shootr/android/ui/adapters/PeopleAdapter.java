package com.shootr.android.ui.adapters;

import android.content.Context;
import com.shootr.android.R;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.util.PicassoWrapper;
import com.squareup.picasso.Picasso;

public class PeopleAdapter extends UserListAdapter {

    private String noEventText;

    public PeopleAdapter(Context context, PicassoWrapper picasso) {
        super(context, picasso);
        noEventText = context.getString(R.string.event_subtitle_nothing);
    }

    @Override public boolean isFollowButtonVisible() {
        return false;
    }

    @Override protected boolean showSubtitle(UserModel item) {
        return true;
    }

    @Override protected String getSubtitle(UserModel item) {
        String eventTitle = item.getEventWatchingTitle();
        if (eventTitle != null) {
            return eventTitle;
        } else {
            return noEventText;
        }
    }
}
