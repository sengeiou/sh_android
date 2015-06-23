package com.shootr.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.android.R;
import com.shootr.android.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.ShotTextSpannableBuilder;
import com.shootr.android.util.UsernameClickListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivityTimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_ACTIVITY = 0;
    public static final int TYPE_FOOTER = 1;
    private final PicassoWrapper picasso;
    private final OnAvatarClickListener avatarClickListener;
    private final UsernameClickListener usernameClickListener;
    private final AndroidTimeUtils timeUtils;
    private final ShotTextSpannableBuilder shotTextSpannableBuilder;

    private List<ActivityModel> activities = Collections.emptyList();
    private boolean showFooter = false;

    public ActivityTimelineAdapter(PicassoWrapper picasso, AndroidTimeUtils timeUtils, OnAvatarClickListener avatarClickListener,
      UsernameClickListener usernameClickListener) {
        this.picasso = picasso;
        this.avatarClickListener = avatarClickListener;
        this.usernameClickListener = usernameClickListener;
        this.timeUtils = timeUtils;
        this.shotTextSpannableBuilder = new ShotTextSpannableBuilder();
    }

    @Override
    public int getItemViewType(int position) {
        return isFooter(position) ? TYPE_FOOTER : TYPE_ACTIVITY;
    }

    @Override
    public int getItemCount() {
        return showFooter ? activities.size() + 1 : activities.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ACTIVITY:
                return onCreateActivityViewHolder(parent, viewType);
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
          avatarClickListener,
          usernameClickListener);
    }

    private RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        View footer = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_loading, parent, false);
        return new RecyclerView.ViewHolder(footer) {};
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
        ArrayList<ActivityModel> newActivityList = new ArrayList<>(activityModels);
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
