package com.shootr.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.android.R;
import com.shootr.android.domain.ActivityType;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.adapters.listeners.OnImageClickListener;
import com.shootr.android.ui.adapters.listeners.OnShotClick;
import com.shootr.android.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.android.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.android.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.ShotTextSpannableBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivityTimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_FOOTER = -1;
    public static final int TYPE_GENERIC_ACTIVITY = 0;
    public static final int TYPE_CHECKIN = 1;
    public static final int TYPE_LISTED = 2;
    public static final int TYPE_STARTED_SHOOTING = 3;
    public static final int TYPE_NICE_SHOT = 4;
    public static final int TYPE_RECOMMEND_STREAM = 5;

    private final PicassoWrapper picasso;
    private final AndroidTimeUtils timeUtils;
    private final OnAvatarClickListener avatarClickListener;
    private final OnUsernameClickListener onUsernameClickListener;
    private final OnStreamTitleClickListener streamTitleClickListener;
    private final OnImageClickListener onImageClickListener;
    private final OnVideoClickListener onVideoClickListener;
    private final OnShotClick onShotClick;

    private final ShotTextSpannableBuilder shotTextSpannableBuilder;
    private List<ActivityModel> activities = Collections.emptyList();
    private boolean showFooter = false;

    public ActivityTimelineAdapter(PicassoWrapper picasso,
      AndroidTimeUtils timeUtils,
      OnAvatarClickListener avatarClickListener,
      OnUsernameClickListener onUsernameClickListener,
      OnStreamTitleClickListener streamTitleClickListener,
      OnImageClickListener onImageClickListener,
      OnVideoClickListener onVideoClickListener, OnShotClick onShotClick) {
        this.picasso = picasso;
        this.avatarClickListener = avatarClickListener;
        this.onUsernameClickListener = onUsernameClickListener;
        this.timeUtils = timeUtils;
        this.streamTitleClickListener = streamTitleClickListener;
        this.onImageClickListener = onImageClickListener;
        this.onVideoClickListener = onVideoClickListener;
        this.onShotClick = onShotClick;
        this.shotTextSpannableBuilder = new ShotTextSpannableBuilder();
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooter(position)) {
            return TYPE_FOOTER;
        } else {
            String activityType = activities.get(position).getType();
            switch (activityType) {
                case ActivityType.CHECKIN:
                    return TYPE_CHECKIN;
                case ActivityType.LISTED_STREAM:
                    return TYPE_LISTED;
                case ActivityType.STARTED_SHOOTING:
                    return TYPE_STARTED_SHOOTING;
                case ActivityType.NICE_SHOT:
                    return TYPE_NICE_SHOT;
                case ActivityType.RECOMMEND_STREAM:
                    return TYPE_RECOMMEND_STREAM;
                default:
                    return TYPE_GENERIC_ACTIVITY;
            }
        }
    }

    @Override
    public int getItemCount() {
        return showFooter ? activities.size() + 1 : activities.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_GENERIC_ACTIVITY:
                return onCreateActivityViewHolder(parent, viewType);
            case TYPE_CHECKIN:
                return onCreateCheckinViewHolder(parent, viewType);
            case TYPE_RECOMMEND_STREAM:
                //TODO REAL HOLDER
                return onCreateActivityViewHolder(parent, viewType);
            case TYPE_LISTED:
                return onCreateListedViewHolder(parent, viewType);
            case TYPE_STARTED_SHOOTING:
                return onCreateStartedShootingViewHolder(parent, viewType);
            case TYPE_NICE_SHOT:
                return onCreateNiceShotViewHolder(parent, viewType);
            case TYPE_FOOTER:
                return onCreateFooterViewHolder(parent, viewType);
        }
        throw new IllegalStateException("View type %d not handled");
    }

    private ActivityViewHolder onCreateActivityViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_activity, parent, false);
        return new ActivityViewHolder(view,
          picasso,
          timeUtils,
          shotTextSpannableBuilder,
          avatarClickListener, onUsernameClickListener);
    }

    private CheckinViewHolder onCreateCheckinViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_activity, parent, false);
        return new CheckinViewHolder(view,
          picasso,
          timeUtils,
          shotTextSpannableBuilder,
          avatarClickListener, onUsernameClickListener, streamTitleClickListener);
    }

    private ListedViewHolder onCreateListedViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_activity, parent, false);
        return new ListedViewHolder(view,
          picasso,
          timeUtils,
          shotTextSpannableBuilder,
          avatarClickListener, onUsernameClickListener, streamTitleClickListener);
    }

    private StartedShootingViewHolder onCreateStartedShootingViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_activity, parent, false);
        return new StartedShootingViewHolder(view,
          picasso,
          timeUtils,
          shotTextSpannableBuilder,
          avatarClickListener, onUsernameClickListener, streamTitleClickListener);
    }

    private NiceShotViewHolder onCreateNiceShotViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_activity_nice_shot, parent, false);
        return new NiceShotViewHolder(view,
          picasso,
          timeUtils,
          shotTextSpannableBuilder,
          avatarClickListener, onUsernameClickListener,
          onImageClickListener,
          onVideoClickListener,
          onShotClick);
    }

    private RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        View footer = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_loading, parent, false);
        return new RecyclerView.ViewHolder(footer) {
            /* no-op */
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!isFooter(position)) {
            ((ActivityViewHolder) holder).render(activities.get(position));
        }
    }

    private boolean isFooter(int position) {
        return position >= activities.size();
    }

    public void addActivitiesBelow(List<ActivityModel> newActivities) {
        this.activities.addAll(newActivities);
        notifyDataSetChanged();
    }

    public void setActivities(List<ActivityModel> activities) {
        this.activities = activities;
        notifyDataSetChanged();
    }

    public void addActivitiesAbove(List<ActivityModel> activityModels) {
        List<ActivityModel> newActivityList = new ArrayList<>(activityModels);
        newActivityList.addAll(this.activities);
        this.activities = newActivityList;
        notifyDataSetChanged();
    }

    public void setFooterVisible(boolean visible) {
        showFooter = visible;
        notifyDataSetChanged();
    }

    public ActivityModel getLastActivity() {
        return activities.get(activities.size() - 1);
    }
}
