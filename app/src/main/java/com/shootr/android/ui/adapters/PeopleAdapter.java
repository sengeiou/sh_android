package com.shootr.android.ui.adapters;

import android.content.Context;
import com.squareup.picasso.Picasso;

public class PeopleAdapter extends UserListAdapter {

    public PeopleAdapter(Context context, Picasso picasso) {
        super(context, picasso);
    }

    @Override public boolean isFollowButtonVisible() {
        return false;
    }
}
