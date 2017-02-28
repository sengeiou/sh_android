package com.shootr.mobile.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.model.activity.ActivityType;
import com.shootr.mobile.ui.adapters.holders.FollowActivityViewHolder;
import com.shootr.mobile.ui.adapters.holders.GenericActivityViewHolder;
import com.shootr.mobile.ui.adapters.holders.MentionViewHolder;
import com.shootr.mobile.ui.adapters.holders.NiceShotViewHolder;
import com.shootr.mobile.ui.adapters.holders.OpenedViewHolder;
import com.shootr.mobile.ui.adapters.holders.PinnedShotViewHolder;
import com.shootr.mobile.ui.adapters.holders.PollFinishedViewHolder;
import com.shootr.mobile.ui.adapters.holders.PollPublishedViewHolder;
import com.shootr.mobile.ui.adapters.holders.PollSharedViewHolder;
import com.shootr.mobile.ui.adapters.holders.PollVotedViewHolder;
import com.shootr.mobile.ui.adapters.holders.ReplyViewHolder;
import com.shootr.mobile.ui.adapters.holders.ShareShotViewHolder;
import com.shootr.mobile.ui.adapters.holders.SharedStreamViewHolder;
import com.shootr.mobile.ui.adapters.holders.StartedShootingViewHolder;
import com.shootr.mobile.ui.adapters.holders.StreamCheckInViewHolder;
import com.shootr.mobile.ui.adapters.holders.StreamFavoritedViewHolder;
import com.shootr.mobile.ui.adapters.holders.WakeUpShotActivityViewHolder;
import com.shootr.mobile.ui.adapters.listeners.ActivityFollowUnfollowListener;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnPollQuestionClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnShotClick;
import com.shootr.mobile.ui.adapters.listeners.OnStreamTitleClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.model.ActivityModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.PollQuestionSpannableBuilder;
import com.shootr.mobile.util.PollVotedSpannableBuilder;
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
    public static final int TYPE_POLL_PUBLISHED = 12;
    public static final int TYPE_POLL_VOTED = 13;
    public static final int TYPE_POLL_SHARED = 14;
    public static final int TYPE_POLL_FINISHED = 15;
    public static final int TYPE_WAKE_UP_SHOT = 16;

    private final ImageLoader imageLoader;
    private final AndroidTimeUtils timeUtils;
    private final OnAvatarClickListener avatarClickListener;
    private final OnUsernameClickListener onUsernameClickListener;
    private final OnStreamTitleClickListener streamTitleClickListener;
    private final OnShotClick onShotClick;
    private final PollQuestionSpannableBuilder pollQuestionSpannableBuilder;
    private final PollVotedSpannableBuilder pollVotedSpannableBuilder;
    private final OnPollQuestionClickListener onPollQuestionClickListener;
    private final ActivityFollowUnfollowListener followUnfollowListener;

    private final ShotTextSpannableBuilder shotTextSpannableBuilder;
    private List<ActivityModel> activities = Collections.emptyList();
    private String currentUserId;
    private boolean showFooter = false;

    public ActivityTimelineAdapter(ImageLoader imageLoader, AndroidTimeUtils timeUtils,
        OnAvatarClickListener avatarClickListener, OnUsernameClickListener onUsernameClickListener,
        OnStreamTitleClickListener streamTitleClickListener, OnShotClick onShotClick,
        OnPollQuestionClickListener onPollQuestionClickListener,
        ActivityFollowUnfollowListener followUnfollowListener) {
        this.imageLoader = imageLoader;
        this.avatarClickListener = avatarClickListener;
        this.onUsernameClickListener = onUsernameClickListener;
        this.timeUtils = timeUtils;
        this.streamTitleClickListener = streamTitleClickListener;
        this.onShotClick = onShotClick;
        this.onPollQuestionClickListener = onPollQuestionClickListener;
        this.followUnfollowListener = followUnfollowListener;
        this.pollQuestionSpannableBuilder = new PollQuestionSpannableBuilder();
        this.pollVotedSpannableBuilder = new PollVotedSpannableBuilder();
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
                case ActivityType.POLL_PUBLISHED:
                    return TYPE_POLL_PUBLISHED;
                case ActivityType.VOTED_IN_POLL:
                    return TYPE_POLL_VOTED;
                case ActivityType.SHARE_POLL:
                    return TYPE_POLL_SHARED;
                case ActivityType.FINISHED_POLL:
                    return TYPE_POLL_FINISHED;
                case ActivityType.WAKE_UP_SHOT:
                    return TYPE_WAKE_UP_SHOT;
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
                return onCreateStreamCheckInViewHolder(parent);
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
            case TYPE_POLL_PUBLISHED:
                return onCreatePollPublishedViewHolder(parent);
            case TYPE_POLL_VOTED:
                return onCreatePollVotedViewHolder(parent);
            case TYPE_POLL_SHARED:
                return onCreatePollSharedViewHolder(parent);
            case TYPE_POLL_FINISHED:
                return onCreatePollFinishedViewHolder(parent);
            case TYPE_WAKE_UP_SHOT:
                return onCreateWakeUpViewHolder(parent);
            default:
                throw new IllegalStateException("View type %d not handled");
        }
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
            avatarClickListener,
            streamTitleClickListener);
    }

    private WakeUpShotActivityViewHolder onCreateWakeUpViewHolder(ViewGroup parent) {
        return new WakeUpShotActivityViewHolder(createActivityView(parent),
            imageLoader,
            timeUtils,
            avatarClickListener,
            streamTitleClickListener);
    }

    private StreamFavoritedViewHolder onCreateStreamFavoritedViewHolder(ViewGroup parent) {
        return new StreamFavoritedViewHolder(createActivityView(parent),
          imageLoader,
          timeUtils,
          avatarClickListener,
          streamTitleClickListener);
    }

    private StreamCheckInViewHolder onCreateStreamCheckInViewHolder(ViewGroup parent) {
        return new StreamCheckInViewHolder(createActivityView(parent),
            imageLoader,
            timeUtils,
            avatarClickListener,
            streamTitleClickListener);
    }

    private SharedStreamViewHolder onCreateSharedStreamViewHolder(ViewGroup parent) {
        return new SharedStreamViewHolder(createActivityView(parent),
          imageLoader,
          timeUtils,
            avatarClickListener,
            streamTitleClickListener);
    }

    private StartedShootingViewHolder onCreateStartedShootingViewHolder(ViewGroup parent) {
        return new StartedShootingViewHolder(createActivityView(parent),
          imageLoader,
          timeUtils,
          avatarClickListener,
          onShotClick);
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
          onUsernameClickListener, followUnfollowListener);
        viewHolder.setCurrentUserId(currentUserId);
        return viewHolder;
    }

    private PollPublishedViewHolder onCreatePollPublishedViewHolder(ViewGroup parent) {
        View view = createActivityView(parent);
        return new PollPublishedViewHolder(view, imageLoader, timeUtils, avatarClickListener,
            pollQuestionSpannableBuilder, onPollQuestionClickListener);
    }

    private PollVotedViewHolder onCreatePollVotedViewHolder(ViewGroup parent) {
        View view = createActivityView(parent);
        return new PollVotedViewHolder(view, imageLoader, timeUtils, avatarClickListener,
            pollVotedSpannableBuilder, onPollQuestionClickListener);
    }

    private PollSharedViewHolder onCreatePollSharedViewHolder(ViewGroup parent) {
        View view = createActivityView(parent);
        return new PollSharedViewHolder(view, imageLoader, timeUtils, avatarClickListener,
            pollQuestionSpannableBuilder, onPollQuestionClickListener);
    }

    private RecyclerView.ViewHolder onCreatePollFinishedViewHolder(ViewGroup parent) {
        View view = createActivityView(parent);
        return new PollFinishedViewHolder(view, imageLoader, timeUtils, avatarClickListener,
            pollQuestionSpannableBuilder, onPollQuestionClickListener);
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
