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
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.shootr.android.R;
import com.shootr.android.ShootrApplication;
import com.shootr.android.ui.model.UserWatchingModel;
import com.shootr.android.util.PicassoWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class WatchersView extends LinearLayout{

    @Inject PicassoWrapper picasso;

    private LayoutInflater layoutInflater;
    private OnEditListener onEditListener;
    private WatcherViewHolder currentUserViewHolder;
    private Map<UserWatchingModel, WatcherViewHolder> watchersHoldersMap;
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
    public void setCurrentUserWatching(UserWatchingModel currentUserWatching) {
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

    public void setWatchers(List<UserWatchingModel> watchers) {
        for (UserWatchingModel watcher : watchers) {
            putWatcher(watcher);
        }
    }

    public void putWatcher(UserWatchingModel userWatching) {
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
    //endregion

    private void bindCurrentUserData(WatcherViewHolder viewHolder, UserWatchingModel currentUserWatching) {
        bindWatcherData(viewHolder, currentUserWatching);

        boolean currenUserItemEnabled = !currentUserWatching.isWatching();
        setCurrentUserEnabledStatus(viewHolder, currenUserItemEnabled);

        viewHolder.editButton.setVisibility(VISIBLE);
        viewHolder.editButton.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                if (onEditListener != null) {
                    onEditListener.onEdit();
                }
            }
        });
    }

    private void setCurrentUserEnabledStatus(WatcherViewHolder viewHolder, boolean currenUserItemEnabled) {
        if (currenUserItemEnabled) {
            viewHolder.name.setAlpha(0.5f);
            viewHolder.watchingText.setAlpha(0.5f);
            viewHolder.avatar.setAlpha(0.5f);
        } else {
            viewHolder.name.setAlpha(1f);
            viewHolder.watchingText.setAlpha(1f);
            viewHolder.avatar.setAlpha(1f);
        }
    }

    private void bindWatcherData(WatcherViewHolder viewHolder, UserWatchingModel userWatching) {
        viewHolder.userId = userWatching.getIdUser();
        viewHolder.name.setText(userWatching.getUserName());
        viewHolder.watchingText.setText(userWatching.getPlace());
        if (picasso != null) {
            picasso.loadProfilePhoto(userWatching.getPhoto()).into(viewHolder.avatar);
        }
    }

    private WatcherViewHolder createViewHolder() {
        View itemView = layoutInflater.inflate(R.layout.item_list_event_watcher, this, false);
        return new WatcherViewHolder(itemView);
    }

    private void addMockData() {
        List<UserWatchingModel> userWatchingMocks = new ArrayList<>();
        UserWatchingModel watch1 = new UserWatchingModel();
        watch1.setIdUser(1L);
        watch1.setUserName("Username");
        watch1.setPlace("Watching");

        userWatchingMocks.add(watch1);
        userWatchingMocks.add(watch1);
        userWatchingMocks.add(watch1);
        userWatchingMocks.add(watch1);

        setWatchers(userWatchingMocks);
        setCurrentUserWatching(watch1);
    }

    public void setOnProfileClickListener(OnProfileClickListener profileClickListener) {
        this.profileClickListener = profileClickListener;
    }

    public void setOnEditListener(OnEditListener onEditListener) {
        this.onEditListener = onEditListener;
    }

    class WatcherViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        @InjectView(R.id.watcher_user_avatar) ImageView avatar;
        @InjectView(R.id.watcher_user_name) TextView name;
        @InjectView(R.id.watcher_user_watching) TextView watchingText;
        @InjectView(R.id.watcher_user_edit) View editButton;
        Long userId;

        public WatcherViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override public void onClick(View v) {
            if (profileClickListener != null) {
                profileClickListener.onProfile(userId);
            }
        }
    }

    public static interface OnProfileClickListener {

        void onProfile(Long idUser);

    }

    public static interface OnEditListener {

        void onEdit();
    }
}
