package com.shootr.android.ui.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.ShootrApplication;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.util.PicassoWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class WatchersView extends LinearLayout{

    @Inject PicassoWrapper picasso;

    private LayoutInflater layoutInflater;
    private WatcherViewHolder currentUserViewHolder;
    private Map<UserModel, WatcherViewHolder> watchersHoldersMap;
    private OnProfileClickListener profileClickListener;

    //region Construction
    public WatchersView(Context context) {
        super(context);
        init(context);
    }

    public WatchersView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WatchersView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) public WatchersView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        if (!isInEditMode()) {
            ShootrApplication.get(context).inject(this);
        }
        setOrientation(VERTICAL);
        layoutInflater = LayoutInflater.from(context);
        watchersHoldersMap = new ArrayMap<>();

        if (isInEditMode()) {
            addMockData();
        }
    }
    //endregion

    //region Public Api
    public void setCurrentUserWatching(UserModel currentUserWatching) {
        if (currentUserViewHolder == null) {
            currentUserViewHolder = createViewHolder();
            this.addView(currentUserViewHolder.itemView, 0);
        }
        bindCurrentUserData(currentUserViewHolder, currentUserWatching);
    }

    public void clearWatchers() {
        //TODO improve efficiency. Shouln't be deleting and creating everything each time.
        watchersHoldersMap.clear();
        currentUserViewHolder = null;
        this.removeAllViews();
    }

    public void setWatchers(List<UserModel> watchers) {
        for (UserModel watcher : watchers) {
            putWatcher(watcher);
        }
    }

    public void putWatcher(UserModel userWatching) {
        WatcherViewHolder viewHolder;
        if (watchersHoldersMap.containsKey(userWatching)) {
            viewHolder = watchersHoldersMap.get(userWatching);
        } else {
            viewHolder = createViewHolder();
            watchersHoldersMap.put(userWatching, viewHolder);
            this.addView(viewHolder.itemView);
        }
        bindWatcherData(viewHolder, userWatching);
    }

    public void setOnProfileClickListener(OnProfileClickListener profileClickListener) {
        this.profileClickListener = profileClickListener;
    }

    //endregion

    //region Private methods
    private void bindCurrentUserData(WatcherViewHolder viewHolder, UserModel currentUserWatching) {
        bindWatcherData(viewHolder, currentUserWatching);
    }

    private void bindWatcherData(WatcherViewHolder viewHolder, UserModel userWatching) {
        viewHolder.userId = userWatching.getIdUser();
        viewHolder.name.setText(userWatching.getUsername());
        viewHolder.watchingText.setText(userWatching.getJoinStreamDate());
        if (picasso != null) {
            picasso.loadProfilePhoto(userWatching.getPhoto()).into(viewHolder.avatar);
        }
    }

    private WatcherViewHolder createViewHolder() {
        View itemView = layoutInflater.inflate(R.layout.item_list_stream_watcher, this, false);
        return new WatcherViewHolder(itemView);
    }

    private void addMockData() {
        List<UserModel> userWatchingMocks = new ArrayList<>();
        UserModel watch1 = new UserModel();
        watch1.setIdUser("id");
        watch1.setUsername("Username");

        userWatchingMocks.add(watch1);
        userWatchingMocks.add(watch1);
        userWatchingMocks.add(watch1);
        userWatchingMocks.add(watch1);

        setWatchers(userWatchingMocks);
        setCurrentUserWatching(watch1);
    }
    //endregion

    class WatcherViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        @Bind(R.id.watcher_user_avatar) ImageView avatar;
        @Bind(R.id.watcher_user_name) TextView name;
        @Bind(R.id.watcher_user_watching) TextView watchingText;
        String userId;

        public WatcherViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override public void onClick(View v) {
            if (profileClickListener != null) {
                profileClickListener.onProfile(userId);
            }
        }
    }

    public static interface OnProfileClickListener {

        void onProfile(String idUser);

    }
}
