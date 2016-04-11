package com.shootr.mobile.ui.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.widgets.CheckableImageView;
import com.shootr.mobile.ui.widgets.ClickableTextView;
import com.shootr.mobile.ui.widgets.NiceButtonView;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.ShotTextSpannableBuilder;
import com.shootr.mobile.util.TimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import timber.log.Timber;

public class ShotDetailWithRepliesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_PARENT_SHOT = 0;
    private static final int TYPE_MAIN_SHOT = 1;
    private static final int TYPE_REPLIES_HEADER = 2;
    private static final int TYPE_REPLY = 3;

    private final ImageLoader imageLoader;
    private final AvatarClickListener avatarClickListener;
    private final ShotClickListener parentShotClickListener;
    private final ShotClickListener replyShotClickListener;
    private final StreamClickListener streamClickListener;
    private final ImageClickListener imageClickListener;
    private OnVideoClickListener videoClickListener;
    private final OnUsernameClickListener onUsernameClickListener;
    private final TimeFormatter timeFormatter;
    private final Resources resources;
    private final AndroidTimeUtils timeUtils;
    private final OnParentShownListener onParentShownListener;
    private final OnNiceShotListener onNiceShotListener;
    private final ShotClickListener onClickListenerPinToProfile;

    private ShotModel mainShot;
    private List<ShotModel> replies;
    private float itemElevation;
    private ShotModel parentShot;
    private boolean isShowingParent = false;
    private ShotTextSpannableBuilder shotTextSpannableBuilder;

    private ShotDetailMainViewHolder mainHolder;

    public ShotDetailWithRepliesAdapter(ImageLoader imageLoader, AvatarClickListener avatarClickListener,
      ShotClickListener parentShotClickListener, ShotClickListener replyShotClickListener,
      StreamClickListener streamClickListener, ImageClickListener imageClickListener,
      OnVideoClickListener videoClickListener, OnUsernameClickListener onUsernameClickListener,
      ShotClickListener onClickListenerPinToProfile, OnParentShownListener onParentShownListener,
      OnNiceShotListener onNiceShotListener, TimeFormatter timeFormatter, Resources resources,
      AndroidTimeUtils timeUtils) {
        this.imageLoader = imageLoader;
        this.avatarClickListener = avatarClickListener;
        this.parentShotClickListener = parentShotClickListener;
        this.replyShotClickListener = replyShotClickListener;
        this.streamClickListener = streamClickListener;
        this.imageClickListener = imageClickListener;
        this.videoClickListener = videoClickListener;
        this.onUsernameClickListener = onUsernameClickListener;
        this.onClickListenerPinToProfile = onClickListenerPinToProfile;
        this.onParentShownListener = onParentShownListener;
        this.timeFormatter = timeFormatter;
        this.resources = resources;
        this.timeUtils = timeUtils;
        this.onNiceShotListener = onNiceShotListener;
        this.replies = new ArrayList<>();
        this.itemElevation = resources.getDimension(R.dimen.card_elevation);
        this.shotTextSpannableBuilder = new ShotTextSpannableBuilder();
    }

    public void renderMainShot(ShotModel mainShot) {
        this.mainShot = mainShot;
        notifyItemChanged(getPositionMainShot());
    }

    public void hidePinToProfileButton() {
        if (mainHolder != null) {
            mainHolder.hidePintToProfileContainer();
        }
    }

    public void showPinToProfileContainer() {
        if (mainHolder != null) {
            mainHolder.showPintToProfileContainer();
        }
    }

    private boolean hasParent() {
        return isShowingParent && mainShot != null && mainShot.isReply();
    }

    public void renderParentShot(ShotModel parentShot) {
        this.parentShot = parentShot;
        notifyItemChanged(getPositionParentShot());
    }

    public void renderReplies(List<ShotModel> replies) {
        this.replies = replies;
        notifyDataSetChanged();
    }

    @Override public int getItemViewType(int position) {
        if (position == getPositionParentShot()) {
            return TYPE_PARENT_SHOT;
        } else if (position == getPositionMainShot()) {
            return TYPE_MAIN_SHOT;
        } else if (position == getPositionRepliesHeader()) {
            return TYPE_REPLIES_HEADER;
        } else {
            return TYPE_REPLY;
        }
    }

    private int getPositionParentShot() {
        return hasParent() ? 0 : -1;
    }

    private int getPositionMainShot() {
        return hasParent() ? 1 : 0;
    }

    private int getPositionRepliesHeader() {
        return hasParent() ? 2 : 1;
    }

    private int adapterPositionToReplyPosition(int adapterPosition) {
        int firstReplyPosition = getPositionRepliesHeader() + 1;
        return adapterPosition - firstReplyPosition;
    }

    private boolean isShowingParent() {
        return isShowingParent;
    }

    private void showParent() {
        isShowingParent = true;
        notifyItemInserted(0);
        onParentShownListener.onShown();
    }

    private void hideParent() {
        isShowingParent = false;
        notifyItemRemoved(0);
    }

    @Override public int getItemCount() {
        int itemCount = 1;
        if (hasParent()) {
            itemCount++;
        }
        if (!replies.isEmpty()) {
            itemCount++;
            itemCount = itemCount + replies.size();
        }
        return itemCount;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView;
        switch (viewType) {
            case TYPE_PARENT_SHOT:
                itemView = layoutInflater.inflate(R.layout.include_shot_detail_parent, parent, false);
                return new ShotDetailParentViewHolder(itemView);
            case TYPE_MAIN_SHOT:
                itemView = layoutInflater.inflate(R.layout.include_shot_detail, parent, false);
                ViewCompat.setElevation(itemView, itemElevation);
                return new ShotDetailMainViewHolder(itemView);
            case TYPE_REPLIES_HEADER:
                itemView = layoutInflater.inflate(R.layout.item_list_replies_header, parent, false);
                return new ShotDetailRepliesHeaderHolder(itemView);
            case TYPE_REPLY:
                itemView = layoutInflater.inflate(R.layout.item_list_shot_reply, parent, false);
                ViewCompat.setElevation(itemView, itemElevation);
                return new ShotDetailReplyHolder(itemView);
            default:
                throw new IllegalArgumentException(String.format("ItemViewType %d has no ViewHolder associated",
                  viewType));
        }
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_PARENT_SHOT:
                bindParentShotViewHolder((ShotDetailParentViewHolder) holder);
                break;
            case TYPE_MAIN_SHOT:
                bindMainShotViewHolder((ShotDetailMainViewHolder) holder);
                break;
            case TYPE_REPLIES_HEADER:
                bindRepliesHeaderHolder((ShotDetailRepliesHeaderHolder) holder);
                break;
            case TYPE_REPLY:
                bindReplyViewHolder((ShotDetailReplyHolder) holder, position);
                break;
            default:
                break;
        }
    }

    private void bindParentShotViewHolder(ShotDetailParentViewHolder holder) {
        if (parentShot != null) {
            holder.bindView(parentShot);
        } else if (mainShot.isReply()) {
            holder.showLoading();
        }
    }

    private void bindRepliesHeaderHolder(ShotDetailRepliesHeaderHolder holder) {
        String repliesCountText = holder.itemView.getResources()
          .getQuantityString(R.plurals.replies_header_count_pattern, replies.size(), replies.size());
        ((TextView) holder.itemView).setText(repliesCountText);
    }

    private void bindMainShotViewHolder(ShotDetailMainViewHolder holder) {
        if (mainShot != null) {
            this.mainHolder = holder;
            holder.bindView(mainShot);
        } else {
            Timber.w("Trying to render null main shot");
        }
    }

    private void bindReplyViewHolder(ShotDetailReplyHolder holder, int adapterPosition) {
        int replyPosition = adapterPositionToReplyPosition(adapterPosition);
        ShotModel shotModel = replies.get(replyPosition);
        holder.bindView(shotModel);
    }

    public interface OnParentShownListener {

        void onShown();
    }

    public void disableStreamTitle() {
        if (mainHolder != null) {
            mainHolder.disableStreamTitle();
        }
    }

    public void enableStreamTitle() {
        if (mainHolder != null) {
            mainHolder.enableStreamTitle();
        }
    }

    //region View holders
    public class ShotDetailMainViewHolder extends RecyclerView.ViewHolder {

        private Context context;
        @Bind(R.id.shot_detail_avatar) ImageView avatar;
        @Bind(R.id.shot_detail_user_name) TextView username;
        @Bind(R.id.shot_detail_timestamp) TextView timestamp;
        @Bind(R.id.shot_detail_text) ClickableTextView shotText;
        @Bind(R.id.shot_detail_image) ImageView shotImage;
        @Bind(R.id.shot_detail_stream_title) TextView streamTitle;
        @Bind(R.id.shot_detail_parent_toggle) ImageView parentToggleButton;
        @Bind(R.id.shot_video_frame) View videoFrame;
        @Bind(R.id.shot_video_title) TextView videoTitle;
        @Bind(R.id.shot_video_duration) TextView videoDuration;
        @Bind(R.id.shot_nice_button) NiceButtonView niceButton;
        @Bind(R.id.shot_nice_count) TextView niceCount;
        @Bind(R.id.shot_detail_pin_to_profile_container) LinearLayout pinToProfileContainer;

        public ShotDetailMainViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
        }

        public void bindView(final ShotModel shotModel) {
            username.setText(getUsernameTitle(shotModel));
            timestamp.setText(getTimestampForDate(shotModel.getBirth()));
            setupComment(shotModel);
            showStreamTitle(shotModel);
            setupAvatar(shotModel);
            setupImage(shotModel);
            setupVideo(shotModel);
            setupReply(shotModel);
            setupNiceButton(shotModel);
            setupPinToProfileContainer(shotModel);
        }

        public void setupReply(ShotModel shotModel) {
            if (shotModel.isReply()) {
                setReply();
            } else {
                parentToggleButton.setVisibility(View.GONE);
            }
        }

        public void setupVideo(ShotModel shotModel) {
            if (shotModel.hasVideo()) {
                setVideo(shotModel);
            } else {
                this.videoFrame.setVisibility(View.GONE);
                this.videoFrame.setOnClickListener(null);
            }
        }

        public void setupImage(ShotModel shotModel) {
            String imageUrl = shotModel.getImage();
            if (imageUrl != null) {
                setImage(shotModel, imageUrl);
            } else {
                shotImage.setVisibility(View.GONE);
            }
        }

        public void setupComment(ShotModel shotModel) {
            String comment = shotModel.getComment();
            if (comment != null) {
                setComment(comment);
            } else {
                shotText.setVisibility(View.GONE);
            }
        }

        public void setupAvatar(final ShotModel shotModel) {
            imageLoader.loadProfilePhoto(shotModel.getPhoto(), avatar);
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    avatarClickListener.onClick(shotModel.getIdUser());
                }
            });
        }

        public void setupNiceButton(final ShotModel shotModel) {
            Integer nicesCount = shotModel.getNiceCount();
            if (nicesCount > 0) {
                setNiceCount(nicesCount);
            } else {
                this.niceCount.setVisibility(View.GONE);
            }
            setNiceButton(shotModel);
        }

        public void setNiceButton(final ShotModel shotModel) {
            niceButton.setChecked(shotModel.isMarkedAsNice());
            niceButton.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (shotModel.isMarkedAsNice()) {
                        onNiceShotListener.unmarkNice(shotModel.getIdShot());
                    } else {
                        onNiceShotListener.markNice(shotModel.getIdShot());
                    }
                }
            });
        }

        public void setNiceCount(Integer niceCount) {
            this.niceCount.setVisibility(View.VISIBLE);
            this.niceCount.setText(context.getResources()
              .getQuantityString(R.plurals.nice_count_pattern, niceCount, niceCount));
        }

        public void setComment(String comment) {
            CharSequence spannedComment =
              shotTextSpannableBuilder.formatWithUsernameSpans(comment, onUsernameClickListener);
            shotText.setText(spannedComment);
            shotText.addLinks();
        }

        public void setImage(final ShotModel shotModel, String imageUrl) {
            shotImage.setVisibility(View.VISIBLE);
            imageLoader.loadTimelineImage(imageUrl, shotImage);
            shotImage.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    imageClickListener.onClick(shotModel);
                }
            });
        }

        public void setVideo(final ShotModel shotModel) {
            this.videoFrame.setVisibility(View.VISIBLE);
            this.shotImage.setVisibility(View.VISIBLE);
            this.videoTitle.setText(shotModel.getVideoTitle());
            this.videoDuration.setText(shotModel.getVideoDuration());
            this.videoFrame.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    videoClickListener.onVideoClick(shotModel.getVideoUrl());
                }
            });
        }

        public void setReply() {
            parentToggleButton.setVisibility(View.VISIBLE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (isShowingParent()) {
                        hideParent();
                        parentToggleButton.setImageResource(R.drawable.ic_arrow_down_24_gray50);
                    } else {
                        showParent();
                        parentToggleButton.setImageResource(R.drawable.ic_arrow_up_24_gray50);
                    }
                }
            });
        }

        private String getUsernameTitle(ShotModel shotModel) {
            if (shotModel.isReply()) {
                return resources.getString(R.string.reply_name_pattern,
                  shotModel.getUsername(),
                  shotModel.getReplyUsername());
            } else {
                return shotModel.getUsername();
            }
        }

        private String getTimestampForDate(Date date) {
            return timeFormatter.getDateAndTimeDetailed(date.getTime());
        }

        private void showStreamTitle(final ShotModel shotModel) {
            String title = shotModel.getStreamTitle();
            if (title != null) {
                streamTitle.setText(shotModel.getStreamTitle());
                streamTitle.setVisibility(View.VISIBLE);
            } else {
                streamTitle.setVisibility(View.GONE);
            }
            streamTitle.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    streamClickListener.onClick(shotModel);
                }
            });
        }

        public void disableStreamTitle() {
            streamTitle.setTextColor(context.getResources().getColor(R.color.gray_material));
            streamTitle.setEnabled(false);
        }

        public void enableStreamTitle() {
            streamTitle.setTextColor(context.getResources().getColor(R.color.links));
            streamTitle.setEnabled(true);
        }

        private void setupPinToProfileContainer(final ShotModel shotModel) {
            pinToProfileContainer.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    onClickListenerPinToProfile.onClick(shotModel);
                }
            });
        }

        public void hidePintToProfileContainer() {
            pinToProfileContainer.setVisibility(View.GONE);
        }

        public void showPintToProfileContainer() {
            pinToProfileContainer.setVisibility(View.VISIBLE);
        }
    }

    public class ShotDetailParentViewHolder extends RecyclerView.ViewHolder {

        private Context context;

        @Bind(R.id.shot_detail_parent_progress) View progress;
        @Bind(R.id.shot_detail_parent_shot) View shot;
        @Bind(R.id.shot_detail_parent) FrameLayout container;
        @Bind(R.id.shot_avatar) public ImageView avatar;
        @Bind(R.id.shot_user_name) public TextView name;
        @Bind(R.id.shot_timestamp) public TextView timestamp;
        @Bind(R.id.shot_text) public ClickableTextView text;
        @Bind(R.id.shot_image) public ImageView image;
        @Bind(R.id.shot_video_frame) View videoFrame;
        @Bind(R.id.shot_video_title) TextView videoTitle;
        @Bind(R.id.shot_video_duration) TextView videoDuration;
        @Bind(R.id.shot_nice_button) CheckableImageView niceButton;

        public ShotDetailParentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
        }

        public void showLoading() {
            progress.setVisibility(View.VISIBLE);
        }

        public void bindView(final ShotModel shotModel) {
            shot.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);

            this.name.setText(getUsernameTitle(shotModel));

            String comment = shotModel.getComment();
            if (comment != null) {
                CharSequence spannedComment =
                  shotTextSpannableBuilder.formatWithUsernameSpans(comment, onUsernameClickListener);
                this.text.setText(spannedComment);
                this.text.addLinks();
            } else {
                this.text.setVisibility(View.GONE);
            }

            long creationDate = shotModel.getBirth().getTime();
            this.timestamp.setText(timeUtils.getElapsedTime(itemView.getContext(), creationDate));

            String photo = shotModel.getPhoto();
            imageLoader.loadProfilePhoto(photo, this.avatar);
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    avatarClickListener.onClick(shotModel.getIdUser());
                }
            });

            String imageUrl = shotModel.getImage();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                this.image.setVisibility(View.VISIBLE);
                imageLoader.loadTimelineImage(imageUrl, this.image);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        imageClickListener.onClick(shotModel);
                    }
                });
            } else {
                this.image.setVisibility(View.GONE);
            }

            if (shotModel.hasVideo()) {
                this.videoFrame.setVisibility(View.VISIBLE);
                this.videoTitle.setText(shotModel.getVideoTitle());
                this.videoDuration.setText(shotModel.getVideoDuration());
                this.videoFrame.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        videoClickListener.onVideoClick(shotModel.getVideoUrl());
                    }
                });
            } else {
                this.videoFrame.setVisibility(View.GONE);
                this.videoFrame.setOnClickListener(null);
            }

            niceButton.setChecked(shotModel.isMarkedAsNice());
            niceButton.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (shotModel.isMarkedAsNice()) {
                        onNiceShotListener.unmarkNice(shotModel.getIdShot());
                    } else {
                        onNiceShotListener.markNice(shotModel.getIdShot());
                    }
                }
            });

            container.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    parentShotClickListener.onClick(shotModel);
                }
            });
        }

        private String getUsernameTitle(ShotModel shotModel) {
            if (shotModel.isReply()) {
                return resources.getString(R.string.reply_name_pattern,
                  shotModel.getUsername(),
                  shotModel.getReplyUsername());
            } else {
                return shotModel.getUsername();
            }
        }
    }

    public class ShotDetailRepliesHeaderHolder extends RecyclerView.ViewHolder {

        public ShotDetailRepliesHeaderHolder(View itemView) {
            super(itemView);
        }
    }

    public class ShotDetailReplyHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.shot_reply_container) RelativeLayout container;
        @Bind(R.id.shot_avatar) public ImageView avatar;
        @Bind(R.id.shot_user_name) public TextView name;
        @Bind(R.id.shot_timestamp) public TextView timestamp;
        @Bind(R.id.shot_text) public ClickableTextView text;
        @Bind(R.id.shot_image) public ImageView image;
        @Bind(R.id.shot_video_frame) View videoFrame;
        @Bind(R.id.shot_video_title) TextView videoTitle;
        @Bind(R.id.shot_video_duration) TextView videoDuration;
        @Bind(R.id.shot_nice_button) NiceButtonView niceButton;

        public ShotDetailReplyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindView(final ShotModel reply) {
            this.name.setText(reply.getUsername());

            String comment = reply.getComment();
            if (comment != null) {
                this.text.setVisibility(View.VISIBLE);
                CharSequence spannedComment =
                  shotTextSpannableBuilder.formatWithUsernameSpans(comment, onUsernameClickListener);
                this.text.setText(spannedComment);
                this.text.addLinks();
            } else {
                this.text.setVisibility(View.GONE);
            }

            long creationDate = reply.getBirth().getTime();
            this.timestamp.setText(timeUtils.getElapsedTime(itemView.getContext(), creationDate));

            String photo = reply.getPhoto();
            imageLoader.loadProfilePhoto(photo, this.avatar);
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    avatarClickListener.onClick(reply.getIdUser());
                }
            });

            String imageUrl = reply.getImage();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                this.image.setVisibility(View.VISIBLE);
                imageLoader.loadTimelineImage(imageUrl, this.image);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        imageClickListener.onClick(reply);
                    }
                });
            } else {
                this.image.setVisibility(View.GONE);
            }

            if (reply.hasVideo()) {
                this.videoFrame.setVisibility(View.VISIBLE);
                this.videoTitle.setText(reply.getVideoTitle());
                this.videoDuration.setText(reply.getVideoDuration());
                this.videoFrame.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        videoClickListener.onVideoClick(reply.getVideoUrl());
                    }
                });
            } else {
                this.videoFrame.setVisibility(View.GONE);
                this.videoFrame.setOnClickListener(null);
            }

            niceButton.setChecked(reply.isMarkedAsNice());
            niceButton.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (reply.isMarkedAsNice()) {
                        onNiceShotListener.unmarkNice(reply.getIdShot());
                    } else {
                        onNiceShotListener.markNice(reply.getIdShot());
                    }
                }
            });

            container.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    replyShotClickListener.onClick(reply);
                }
            });
        }
    }

    public interface AvatarClickListener {

        void onClick(String userId);
    }

    public interface ImageClickListener {

        void onClick(ShotModel shot);
    }

    public interface ShotClickListener {

        void onClick(ShotModel shot);
    }

    public interface StreamClickListener {

        void onClick(ShotModel shotModel);
    }
    //endregion
}
