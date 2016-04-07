package com.shootr.mobile.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.ActivityType;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotClick;
import com.shootr.mobile.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.ShotTextSpannableBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivityTimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_FOOTER = -1;
    public static final int TYPE_GENERIC_ACTIVITY = 0;
    public static final int TYPE_CHECKIN = 1;
    public static final int TYPE_OPENED = 2;
    public static final int TYPE_STARTED_SHOOTING = 3;
    public static final int TYPE_NICE_SHOT = 4;
    public static final int TYPE_SHARE_STREAM = 5;
    public static final int TYPE_SHARE_SHOT = 6;
    public static final int TYPE_STREAM_FAVORITED = 7;
    public static final int TYPE_MENTION = 8;
    public static final int TYPE_FOLLOW = 9;
    public static final int TYPE_PINNED_SHOT = 10;
    public static final int TYPE_REPLY_SHOT = 11;

    private final ImageLoader imageLoader;
    private final AndroidTimeUtils timeUtils;
    private final OnAvatarClickListener avatarClickListener;
    private final OnUsernameClickListener onUsernameClickListener;
    private final OnStreamTitleClickListener streamTitleClickListener;
    private final OnShotClick onShotClick;

    private final ShotTextSpannableBuilder shotTextSpannableBuilder;
    private List<ActivityModel> activities = Collections.emptyList();
    private String currentUserId;
    private boolean showFooter = false;

    public ActivityTimelineAdapter(ImageLoader imageLoader, AndroidTimeUtils timeUtils,
      OnAvatarClickListener avatarClickListener, OnUsernameClickListener onUsernameClickListener,
      OnStreamTitleClickListener streamTitleClickListener, OnShotClick onShotClick) {
        this.imageLoader = imageLoader;
        this.avatarClickListener = avatarClickListener;
        this.onUsernameClickListener = onUsernameClickListener;
        this.timeUtils = timeUtils;
        this.streamTitleClickListener = streamTitleClickListener;
        this.onShotClick = onShotClick;
        this.shotTextSpannableBuilder = new ShotTextSpannableBuilder();
    }

    @Override public int getItemViewType(int position) {
        if (isFooter(position)) {
            return TYPE_FOOTER;
        } else {
            String activityType = activities.get(position).getType();
            switch (activityType) {
                case ActivityType.CHECKIN:
                    return TYPE_CHECKIN;
                case ActivityType.OPENED_STREAM:
                    return TYPE_OPENED;
                case ActivityType.STARTED_SHOOTING:
                    return TYPE_STARTED_SHOOTING;
                case ActivityType.NICE_SHOT:
                    return TYPE_NICE_SHOT;
                case ActivityType.SHARE_STREAM:
                    return TYPE_SHARE_STREAM;
                case ActivityType.SHARE_SHOT:
                    return TYPE_SHARE_SHOT;
                case ActivityType.STREAM_FAVORITED:
                    return TYPE_STREAM_FAVORITED;
                case ActivityType.MENTION:
                    return TYPE_MENTION;
                case ActivityType.START_FOLLOW:
                    return TYPE_FOLLOW;
                case ActivityType.PINNED_SHOT:
                    return TYPE_PINNED_SHOT;
                case ActivityType.REPLY_SHOT:
                    return TYPE_REPLY_SHOT;
                default:
                    return TYPE_GENERIC_ACTIVITY;
            }
        }
    }

    @Override public int getItemCount() {
        return showFooter ? activities.size() + 1 : activities.size();
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_GENERIC_ACTIVITY:
                return onCreateActivityViewHolder(parent);
            case TYPE_CHECKIN:
                return onCreateActivityViewHolder(parent);
            case TYPE_SHARE_STREAM:
                return onCreateSharedStreamViewHolder(parent);
            case TYPE_SHARE_SHOT:
                return onCreateShareShotViewHolder(parent);
            case TYPE_PINNED_SHOT:
                return onCreatePinnedShotViewHolder(parent);
            case TYPE_MENTION:
                return onCreateMentionViewHolder(parent);
            case TYPE_OPENED:
                return onCreateOpenedViewHolder(parent);
            case TYPE_STARTED_SHOOTING:
                return onCreateStartedShootingViewHolder(parent);
            case TYPE_NICE_SHOT:
                return onCreateNiceShotViewHolder(parent);
            case TYPE_STREAM_FAVORITED:
                return onCreateStreamFavoritedViewHolder(parent);
            case TYPE_FOLLOW:
                return onCreateFollowViewHolder(parent);
            case TYPE_FOOTER:
                return onCreateFooterViewHolder(parent);
            case TYPE_REPLY_SHOT:
                return onCreateReplyViewHolder(parent);
        }
        throw new IllegalStateException("View type %d not handled");
    }

    private View createActivityView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_activity, parent, false);
    }

    private GenericActivityViewHolder onCreateActivityViewHolder(ViewGroup parent) {
        View view = createActivityView(parent);
        return new GenericActivityViewHolder(view, imageLoader, timeUtils, avatarClickListener);
    }

    private OpenedViewHolder onCreateOpenedViewHolder(ViewGroup parent) {
        return new OpenedViewHolder(createActivityView(parent),
          imageLoader,
          timeUtils,
          shotTextSpannableBuilder,
          avatarClickListener,
          onUsernameClickListener,
          streamTitleClickListener);
    }

    private StreamFavoritedViewHolder onCreateStreamFavoritedViewHolder(ViewGroup parent) {
        return new StreamFavoritedViewHolder(createActivityView(parent),
          imageLoader,
          timeUtils,
          avatarClickListener,
          streamTitleClickListener);
    }

    private SharedStreamViewHolder onCreateSharedStreamViewHolder(ViewGroup parent) {
        return new SharedStreamViewHolder(createActivityView(parent),
          imageLoader,
          timeUtils,
          shotTextSpannableBuilder,
          avatarClickListener,
          onUsernameClickListener,
          streamTitleClickListener);
    }

    private StartedShootingViewHolder onCreateStartedShootingViewHolder(ViewGroup parent) {
        return new StartedShootingViewHolder(createActivityView(parent),
          imageLoader,
          timeUtils,
          shotTextSpannableBuilder,
          avatarClickListener,
          onUsernameClickListener,
          streamTitleClickListener);
    }

    private NiceShotViewHolder onCreateNiceShotViewHolder(ViewGroup parent) {
        return new NiceShotViewHolder(createActivityView(parent),
          imageLoader,
          timeUtils,
          avatarClickListener,
          onShotClick);
    }

    private ShareShotViewHolder onCreateShareShotViewHolder(ViewGroup parent) {
        return new ShareShotViewHolder(createActivityView(parent),
          imageLoader,
          timeUtils,
          avatarClickListener,
          onShotClick);
    }

    private PinnedShotViewHolder onCreatePinnedShotViewHolder(ViewGroup parent) {
        return new PinnedShotViewHolder(createActivityView(parent),
          imageLoader,
          timeUtils,
          avatarClickListener,
          onShotClick);
    }

    private MentionViewHolder onCreateMentionViewHolder(ViewGroup parent) {
        return new MentionViewHolder(createActivityView(parent),
          imageLoader,
          timeUtils,
          avatarClickListener,
          onShotClick);
    }

    private ReplyViewHolder onCreateReplyViewHolder(ViewGroup parent) {
        return new ReplyViewHolder(createActivityView(parent),
          imageLoader,
          timeUtils,
          avatarClickListener,
          onShotClick);
    }

    private FollowActivityViewHolder onCreateFollowViewHolder(ViewGroup parent) {
        View view = createActivityView(parent);
        FollowActivityViewHolder viewHolder = new FollowActivityViewHolder(view,
          imageLoader,
          timeUtils,
          shotTextSpannableBuilder,
          avatarClickListener,
          onUsernameClickListener);
        viewHolder.setCurrentUserId(currentUserId);
        return viewHolder;
    }

    private RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent) {
        View footer = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_loading, parent, false);
        return new RecyclerView.ViewHolder(footer) {
            /* empty implementation */
        };
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!isFooter(position)) {
            ((GenericActivityViewHolder) holder).render(activities.get(position));
        }
    }

    private boolean isFooter(int position) {
        return position >= activities.size();
    }

    public void addActivitiesBelow(List<ActivityModel> newActivities) {
        this.activities.addAll(newActivities);
        notifyDataSetChanged();
    }

    public void setActivities(List<ActivityModel> activities, String currentUserId) {
        this.activities = activities;
        this.currentUserId = currentUserId;
        notifyDataSetChanged();
    }

    public void addActivitiesAbove(List<ActivityModel> activityModels) {
        List<ActivityModel> newActivityList = new ArrayList<>(activityModels);
        newActivityList.addAll(this.activities);
        this.activities = newActivityList;
        notifyDataSetChanged();
    }

    public void setFooterVisible(Boolean visible) {
        showFooter = visible;
        notifyDataSetChanged();
    }

    public ActivityModel getLastActivity() {
        return activities.get(activities.size() - 1);
    }
}
