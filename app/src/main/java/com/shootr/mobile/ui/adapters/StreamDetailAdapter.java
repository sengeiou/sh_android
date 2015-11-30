package com.shootr.mobile.ui.adapters;

import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.ui.adapters.listeners.OnFollowUnfollowListener;
import com.shootr.mobile.ui.adapters.listeners.OnUserClickListener;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.widgets.FollowButton;
import com.shootr.mobile.util.ImageLoader;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;
import static com.shootr.mobile.domain.utils.Preconditions.checkPositionIndex;

public class StreamDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_AUTHOR = 1;
    private static final int TYPE_MEDIA = 2;
    private static final int TYPE_PARTICIPANTS_TITLE = 3;
    private static final int TYPE_PARTICIPANT = 4;
    private static final int TYPE_DESCRIPTION = 5;
    private static final int TYPE_ALL_PARTICIPANTS = 6;

    private static final int EXTRA_ITEMS_ABOVE_PARTICIPANTS = 4;

    private final View.OnClickListener onAuthorClickListener;
    private final View.OnClickListener onMediaClickListener;
    private final View.OnClickListener onAllParticipantsClickListener;
    private final OnUserClickListener onUserClickListener;
    private final OnFollowUnfollowListener onFollowUnfollowListener;
    private final ImageLoader imageLoader;
    private ActionViewHolder authorViewHolder;
    private ActionViewHolder mediaViewHolder;
    private AllParticipantsViewHolder allParticipantsViewHolder;
    private SeparatorViewHolder separatorViewHolder;

    private List<UserModel> participants = Collections.emptyList();
    private TextViewHolder descriptionViewHolder;

    private String authorName;
    private String description;

    private final Set<String> keepFollowButtonIds = new HashSet<>();
    private boolean isAllParticipantsVisible = false;

    public StreamDetailAdapter(ImageLoader imageLoader, View.OnClickListener onAuthorClickListener,
      View.OnClickListener onMediaClickListener, View.OnClickListener onAllParticipantsClickListener,
      OnUserClickListener onUserClickListener, OnFollowUnfollowListener onFollowUnfollowListener) {
        this.onAuthorClickListener = onAuthorClickListener;
        this.onMediaClickListener = onMediaClickListener;
        this.onAllParticipantsClickListener = onAllParticipantsClickListener;
        this.onUserClickListener = onUserClickListener;
        this.imageLoader = imageLoader;
        this.onFollowUnfollowListener = onFollowUnfollowListener;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
        if (authorViewHolder != null) {
            authorViewHolder.name.setText(authorName);
        }
    }

    public void setDescription(String description) {
        this.description = description;
        if (descriptionViewHolder != null) {
            descriptionViewHolder.setText(description);
        }
    }

    public void showAllParticipants() {
        isAllParticipantsVisible = true;
        if (allParticipantsViewHolder != null) {
            allParticipantsViewHolder.setVisible(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == getItemCount() -1) {
            return TYPE_ALL_PARTICIPANTS;
        }
        switch (position) {
            case 0:
                return TYPE_DESCRIPTION;
            case 1:
                return TYPE_AUTHOR;
            case 2:
                return TYPE_MEDIA;
            case 3:
                return TYPE_PARTICIPANTS_TITLE;
            default:
                return TYPE_PARTICIPANT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_DESCRIPTION:
                if (descriptionViewHolder == null) {
                    v = inflater.inflate(com.shootr.mobile.R.layout.item_menu_text, parent, false);
                    descriptionViewHolder = new TextViewHolder(v);
                }
                return descriptionViewHolder;
            case TYPE_AUTHOR:
                if (authorViewHolder == null) {
                    v = inflater.inflate(com.shootr.mobile.R.layout.item_menu_action, parent, false);
                    v.setOnClickListener(onAuthorClickListener);
                    authorViewHolder = new ActionViewHolder(v);
                }
                return authorViewHolder;
            case TYPE_MEDIA:
                if (mediaViewHolder == null) {
                    v = inflater.inflate(com.shootr.mobile.R.layout.item_menu_action, parent, false);
                    v.setOnClickListener(onMediaClickListener);
                    mediaViewHolder = new ActionViewHolder(v);
                }
                return mediaViewHolder;
            case TYPE_PARTICIPANTS_TITLE:
                v = inflater.inflate(R.layout.include_stream_detail_separator, parent, false);
                separatorViewHolder = new SeparatorViewHolder(v, android.R.id.text1);
                separatorViewHolder.render("PAJO MIERDA");
                return separatorViewHolder;
            case TYPE_PARTICIPANT:
                v = inflater.inflate(com.shootr.mobile.R.layout.item_list_stream_watcher, parent, false);
                return new WatcherViewHolder(v, onUserClickListener, imageLoader, onFollowUnfollowListener, keepFollowButtonIds);
            case TYPE_ALL_PARTICIPANTS:
                if (allParticipantsViewHolder == null) {
                    v = inflater.inflate(com.shootr.mobile.R.layout.include_all_participants_button, parent, false);
                    allParticipantsViewHolder = new AllParticipantsViewHolder(v);
                    v.setOnClickListener(onAllParticipantsClickListener);
                }
                return allParticipantsViewHolder;
            default:
                throw new IllegalStateException("No holder declared for view type " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_DESCRIPTION:
                descriptionViewHolder.setText(description);
                break;
            case TYPE_AUTHOR:
                authorViewHolder.setIcon(com.shootr.mobile.R.drawable.ic_stream_author_24_gray50);
                authorViewHolder.setName(authorName);
                break;
            case TYPE_MEDIA:
                mediaViewHolder.setIcon(com.shootr.mobile.R.drawable.ic_action_stream_gallery_gray_24);
                mediaViewHolder.setName(com.shootr.mobile.R.string.stream_detail_media);
                break;
            case TYPE_PARTICIPANT:
                UserModel user = participants.get(participantPosition(position));
                ((WatcherViewHolder) viewHolder).bind(user);
                break;
            case TYPE_ALL_PARTICIPANTS:
                allParticipantsViewHolder.setVisible(isAllParticipantsVisible);
        }
    }

    @Override
    public int getItemCount() {
        return EXTRA_ITEMS_ABOVE_PARTICIPANTS + participants.size() + 1;
    }

    private int participantPosition(int adapterPosition) {
        int userItemPosition = adapterPosition - EXTRA_ITEMS_ABOVE_PARTICIPANTS;
        checkPositionIndex(userItemPosition, participants.size());
        return userItemPosition;
    }

    public void setParticipants(List<UserModel> watchers) {
        this.participants = watchers;
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder {

        @Bind(com.shootr.mobile.R.id.text) TextView text;

        public TextViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setText(String text) {
            this.text.setText(text);
            this.text.setVisibility(text != null ? View.VISIBLE : View.GONE);
        }
    }

    public static class ActionViewHolder extends RecyclerView.ViewHolder {

        @Bind(com.shootr.mobile.R.id.action_icon) ImageView icon;
        @Bind(com.shootr.mobile.R.id.action_name) TextView name;
        @Bind(com.shootr.mobile.R.id.action_number) TextView optionalNumber;

        public ActionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setIcon(@DrawableRes int iconRes) {
            icon.setImageResource(iconRes);
        }

        public void setName(@StringRes int nameRes) {
            name.setText(nameRes);
        }

        public void setName(String actionName) {
            name.setText(actionName);
        }

        public void setNumber(int number) {
            if (number < 1) {
                optionalNumber.setVisibility(View.GONE);
            } else {
                optionalNumber.setVisibility(View.VISIBLE);
                optionalNumber.setText(String.valueOf(number));
            }
        }
    }

    public static class SeparatorViewHolder extends RecyclerView.ViewHolder {

        protected TextView titleText;

        public SeparatorViewHolder(View itemView, @IdRes int titleID) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(titleID);
        }

        public void render(String title){
            titleText.setText(title);
        }

    }

    public static class WatcherViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final OnUserClickListener onUserClickListener;
        private final ImageLoader imageLoader;
        private final OnFollowUnfollowListener onFollowUnfollowListener;
        private final Set<String> keepFollowButtonIds;

        @Bind(com.shootr.mobile.R.id.watcher_user_avatar) ImageView avatar;
        @Bind(com.shootr.mobile.R.id.watcher_user_name) TextView name;
        @Bind(com.shootr.mobile.R.id.watcher_user_watching) TextView watchingText;
        @Bind(com.shootr.mobile.R.id.user_follow_button) FollowButton followButton;

        private String userId;

        public WatcherViewHolder(View itemView,
          OnUserClickListener onUserClickListener,
          ImageLoader imageLoader,
          OnFollowUnfollowListener onFollowUnfollowListener, Set<String> keepFollowButtonIds) {
            super(itemView);
            this.onUserClickListener = onUserClickListener;
            this.imageLoader = imageLoader;
            this.onFollowUnfollowListener = onFollowUnfollowListener;
            this.keepFollowButtonIds = keepFollowButtonIds;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(final UserModel userModel) {
            userId = userModel.getIdUser();
            name.setText(userModel.getUsername());
            watchingText.setText(userModel.getJoinStreamDate());
            imageLoader.loadProfilePhoto(userModel.getPhoto(), avatar);

            switch (userModel.getRelationship()) {
                case FollowEntity.RELATIONSHIP_FOLLOWING:
                    boolean shouldShowButton = keepFollowButtonIds.contains(userModel.getIdUser());
                    followButton.setVisibility(shouldShowButton ? View.VISIBLE : View.GONE);
                    followButton.setFollowing(true);
                    break;
                case FollowEntity.RELATIONSHIP_OWN:
                    followButton.setVisibility(View.GONE);
                    break;
                default:
                    followButton.setVisibility(View.VISIBLE);
                    followButton.setFollowing(false);
            }
            followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (followButton.isFollowing()) {
                        onFollowUnfollowListener.onUnfollow(userModel);
                        followButton.setFollowing(false);
                    } else {
                        onFollowUnfollowListener.onFollow(userModel);
                        followButton.setFollowing(true);
                        keepFollowButtonIds.add(userModel.getIdUser());
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            checkNotNull(userId);
            if (onUserClickListener != null) {
                onUserClickListener.onUserClick(userId);
            }
        }
    }

    public static class AllParticipantsViewHolder extends RecyclerView.ViewHolder {

        @Bind(com.shootr.mobile.R.id.all_participants_button) TextView button;

        public AllParticipantsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setVisible(boolean visible) {
            button.setVisibility(visible? View.VISIBLE : View.GONE);
        }

    }
}
