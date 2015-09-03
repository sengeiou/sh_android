package com.shootr.android.ui.adapters;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.android.R;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.ui.adapters.listeners.OnFollowUnfollowListener;
import com.shootr.android.ui.adapters.listeners.OnUserClickListener;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.widgets.FollowButton;
import com.shootr.android.util.ImageLoader;
import java.util.Collections;
import java.util.List;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;
import static com.shootr.android.domain.utils.Preconditions.checkPositionIndex;

public class StreamDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_AUTHOR = 1;
    private static final int TYPE_MEDIA = 2;
    private static final int TYPE_PARTICIPANTS_TITLE = 3;
    private static final int TYPE_PARTICIPANT = 4;
    private static final int TYPE_DESCRIPTION = 5;

    private static final int EXTRA_ITEMS_ABOVE_PARTICIPANTS = 4;

    private final View.OnClickListener onAuthorClickListener;
    private final View.OnClickListener onMediaClickListener;
    private final OnUserClickListener onUserClickListener;
    private final OnFollowUnfollowListener onFollowUnfollowListener;
    private final ImageLoader imageLoader;
    private ActionViewHolder authorViewHolder;
    private ActionViewHolder mediaViewHolder;

    private List<UserModel> participants = Collections.emptyList();
    private TextViewHolder descriptionViewHolder;

    private String authorName;
    private String description;
    private int mediaCount;

    public StreamDetailAdapter(ImageLoader imageLoader,
      View.OnClickListener onAuthorClickListener,
      View.OnClickListener onMediaClickListener,
      OnUserClickListener onUserClickListener, OnFollowUnfollowListener onFollowUnfollowListener) {
        this.onAuthorClickListener = onAuthorClickListener;
        this.onMediaClickListener = onMediaClickListener;
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

    public void setMediaCount(int mediaCount) {
        this.mediaCount = mediaCount;
        if (mediaViewHolder != null) {
            mediaViewHolder.setNumber(mediaCount);
        }
    }

    @Override
    public int getItemViewType(int position) {
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
                    v = inflater.inflate(R.layout.item_menu_text, parent, false);
                    descriptionViewHolder = new TextViewHolder(v);
                }
                return descriptionViewHolder;
            case TYPE_AUTHOR:
                if (authorViewHolder == null) {
                    v = inflater.inflate(R.layout.item_menu_action, parent, false);
                    v.setOnClickListener(onAuthorClickListener);
                    authorViewHolder = new ActionViewHolder(v);
                }
                return authorViewHolder;
            case TYPE_MEDIA:
                if (mediaViewHolder == null) {
                    v = inflater.inflate(R.layout.item_menu_action, parent, false);
                    v.setOnClickListener(onMediaClickListener);
                    mediaViewHolder = new ActionViewHolder(v);
                }
                return mediaViewHolder;
            case TYPE_PARTICIPANTS_TITLE:
                v = inflater.inflate(R.layout.item_card_title_separator, parent, false);
                return new SeparatorViewHolder(v);
            case TYPE_PARTICIPANT:
                v = inflater.inflate(R.layout.item_list_stream_watcher, parent, false);
                return new WatcherViewHolder(v, onUserClickListener, imageLoader, onFollowUnfollowListener);
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
                authorViewHolder.setIcon(R.drawable.ic_stream_author_24_gray50);
                authorViewHolder.setName(authorName);
                break;
            case TYPE_MEDIA:
                mediaViewHolder.setIcon(R.drawable.ic_action_stream_gallery_gray_24);
                mediaViewHolder.setName(R.string.stream_detail_media);
                mediaViewHolder.setNumber(mediaCount);
                break;
            case TYPE_PARTICIPANTS_TITLE:
                /* no-op */
                break;
            case TYPE_PARTICIPANT:
                UserModel user = participants.get(participantPosition(position));
                ((WatcherViewHolder) viewHolder).bind(user);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return EXTRA_ITEMS_ABOVE_PARTICIPANTS + participants.size();
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

        @Bind(R.id.text) TextView text;

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

        @Bind(R.id.action_icon) ImageView icon;
        @Bind(R.id.action_name) TextView name;
        @Bind(R.id.action_number) TextView optionalNumber;

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

        public SeparatorViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class WatcherViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final OnUserClickListener onUserClickListener;
        private final ImageLoader imageLoader;
        private final OnFollowUnfollowListener onFollowUnfollowListener;

        @Bind(R.id.watcher_user_avatar) ImageView avatar;
        @Bind(R.id.watcher_user_name) TextView name;
        @Bind(R.id.watcher_user_watching) TextView watchingText;
        @Bind(R.id.user_follow_button) FollowButton followButton;

        private String userId;

        public WatcherViewHolder(View itemView,
          OnUserClickListener onUserClickListener,
          ImageLoader imageLoader,
          OnFollowUnfollowListener onFollowUnfollowListener) {
            super(itemView);
            this.onUserClickListener = onUserClickListener;
            this.imageLoader = imageLoader;
            this.onFollowUnfollowListener = onFollowUnfollowListener;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(final UserModel userModel) {
            userId = userModel.getIdUser();
            name.setText(userModel.getUsername());
            watchingText.setText(userModel.getJoinStreamDate());
            imageLoader.loadProfilePhoto(userModel.getPhoto(), avatar);

                if(userModel.getRelationship() == FollowEntity.RELATIONSHIP_FOLLOWING){
                    followButton.setFollowing(true);
                    followButton.setVisibility(View.VISIBLE);
                }else if(userModel.getRelationship() == FollowEntity.RELATIONSHIP_OWN){
                    followButton.setVisibility(View.GONE);
                }else{
                    followButton.setFollowing(false);
                    followButton.setVisibility(View.VISIBLE);
                }
                followButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (followButton.isFollowing()) {
                            onFollowUnfollowListener.onUnfollow(userModel);
                        } else {
                            onFollowUnfollowListener.onFollow(userModel);
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
}
