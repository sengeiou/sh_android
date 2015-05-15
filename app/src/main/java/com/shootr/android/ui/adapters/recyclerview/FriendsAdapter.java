package com.shootr.android.ui.adapters.recyclerview;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shootr.android.R;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.ui.adapters.BindableAdapter;
import com.shootr.android.ui.adapters.UserListAdapter;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.widgets.FollowButton;
import com.shootr.android.util.PicassoWrapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FriendsAdapter extends UserListAdapter {

    private static final String EMPTY_EVENT_SUBTITLE = "";

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
        String eventTitle = item.getEventWatchingTitle();
        if (eventTitle != null) {
            return eventTitle;
        } else {
            return EMPTY_EVENT_SUBTITLE;
        }
    }
}
