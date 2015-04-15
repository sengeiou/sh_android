package com.shootr.android.ui.adapters;

import android.content.res.Resources;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.widgets.ClickableTextView;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.TimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import timber.log.Timber;

public class ShotDetailWithRepliesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_MAIN_SHOT = 0;
    private static final int TYPE_REPLIES_HEADER = 1;
    private static final int TYPE_REPLY = 2;
    private static final int POSITION_MAIN_SHOT = 0;
    private static final int POSITION_REPLIES_HEADER = 1;

    private final PicassoWrapper picasso;
    private final AvatarClickListener avatarClickListener;
    private final ImageClickListener imageClickListener;
    private final TimeFormatter timeFormatter;
    private final Resources resources;
    private final AndroidTimeUtils timeUtils;

    private ShotModel mainShot;
    private List<ShotModel> replies;
    private float itemElevation;

    public ShotDetailWithRepliesAdapter(PicassoWrapper picasso, AvatarClickListener avatarClickListener,
      ImageClickListener imageClickListener, TimeFormatter timeFormatter, Resources resources,
      AndroidTimeUtils timeUtils) {
        this.picasso = picasso;
        this.avatarClickListener = avatarClickListener;
        this.imageClickListener = imageClickListener;
        this.timeFormatter = timeFormatter;
        this.resources = resources;
        this.timeUtils = timeUtils;
        this.replies = new ArrayList<>();
        this.itemElevation = resources.getDimension(R.dimen.card_elevation);
    }

    public void renderMainShot(ShotModel mainShot) {
        this.mainShot = mainShot;
        notifyItemChanged(POSITION_MAIN_SHOT);
    }

    public void renderReplies(List<ShotModel> shotModels) {
        boolean wasEmpty = this.replies.isEmpty();
        int firstItemInserted = getItemCount();
        int insertedItemCount = shotModels.size();
        this.replies = shotModels;
        if (wasEmpty) {
            Timber.d("inserted %d %d", firstItemInserted, insertedItemCount);
            notifyItemRangeInserted(firstItemInserted, insertedItemCount);
        } else {
            Timber.d("changed %d %d", firstItemInserted, insertedItemCount);
            notifyItemRangeChanged(firstItemInserted, insertedItemCount);
        }
    }

    @Override public int getItemViewType(int position) {
        switch (position) {
            case POSITION_MAIN_SHOT:
                return TYPE_MAIN_SHOT;
            case POSITION_REPLIES_HEADER:
                return TYPE_REPLIES_HEADER;
            default:
                return TYPE_REPLY;
        }
    }

    @Override public int getItemCount() {
        int itemCount = 1;
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
            case TYPE_MAIN_SHOT:
                bindMainShotViewHolder((ShotDetailMainViewHolder) holder);
                break;
            case TYPE_REPLIES_HEADER:
                bindRepliesHeaderHolder((ShotDetailRepliesHeaderHolder) holder);
                break;
            case TYPE_REPLY:
                bindReplyViewHolder((ShotDetailReplyHolder) holder, position);
                break;
        }
    }

    private void bindRepliesHeaderHolder(ShotDetailRepliesHeaderHolder holder) {
        String repliesCountText =
          holder.itemView.getResources().getString(R.string.replies_header_count_pattern, replies.size());
        ((TextView) holder.itemView).setText(repliesCountText);
    }

    private void bindMainShotViewHolder(ShotDetailMainViewHolder holder) {
        if (mainShot != null) {
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

    private int adapterPositionToReplyPosition(int adapterPosition) {
        int firstReplyPosition = POSITION_REPLIES_HEADER + 1;
        return adapterPosition - firstReplyPosition;
    }

    //region View holders
    public class ShotDetailMainViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.shot_avatar) ImageView avatar;
        @InjectView(R.id.shot_user_name) TextView username;
        @InjectView(R.id.shot_timestamp) TextView timestamp;
        @InjectView(R.id.shot_text) ClickableTextView shotText;
        @InjectView(R.id.shot_image) ImageView shotImage;
        @InjectView(R.id.shot_event_title) TextView eventTitle;

        public ShotDetailMainViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        public void bindView(final ShotModel shotModel) {
            username.setText(getUsernameTitle(shotModel));
            timestamp.setText(getTimestampForDate(shotModel.getCsysBirth()));
            String comment = shotModel.getComment();
            if (comment != null) {
                shotText.setText(comment);
                shotText.addLinks();
            } else {
                shotText.setVisibility(View.GONE);
            }
            showEventTitle(shotModel);

            picasso.loadProfilePhoto(shotModel.getPhoto()).into(avatar);
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    avatarClickListener.onClick(shotModel.getIdUser());
                }
            });

            String imageUrl = shotModel.getImage();
            if (imageUrl != null) {
                picasso.loadTimelineImage(imageUrl).into(shotImage);
                shotImage.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        imageClickListener.onClick(shotModel);
                    }
                });
            } else {
                shotImage.setVisibility(View.GONE);
            }
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

        private void showEventTitle(ShotModel shotModel) {
            String title = shotModel.getEventTitle();
            if (title != null) {
                eventTitle.setText(shotModel.getEventTitle());
                eventTitle.setVisibility(View.VISIBLE);
            } else {
                eventTitle.setVisibility(View.GONE);
            }
        }
    }

    public class ShotDetailRepliesHeaderHolder extends RecyclerView.ViewHolder {

        public ShotDetailRepliesHeaderHolder(View itemView) {
            super(itemView);
        }
    }

    public class ShotDetailReplyHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.shot_avatar) public ImageView avatar;
        @InjectView(R.id.shot_user_name) public TextView name;
        @InjectView(R.id.shot_timestamp) public TextView timestamp;
        @InjectView(R.id.shot_text) public ClickableTextView text;
        @InjectView(R.id.shot_image) public ImageView image;

        public ShotDetailReplyHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        public void bindView(final ShotModel reply) {
            this.name.setText(reply.getUsername());

            String comment = reply.getComment();
            if (comment != null) {
                this.text.setVisibility(View.VISIBLE);
                this.text.setText(comment);
                this.text.addLinks();
            } else {
                this.text.setVisibility(View.GONE);
            }

            long creationDate = reply.getCsysBirth().getTime();
            this.timestamp.setText(timeUtils.getElapsedTime(itemView.getContext(), creationDate));

            String photo = reply.getPhoto();
            picasso.loadProfilePhoto(photo).into(this.avatar);
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    avatarClickListener.onClick(reply.getIdUser());
                }
            });

            String imageUrl = reply.getImage();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                this.image.setVisibility(View.VISIBLE);
                picasso.loadTimelineImage(imageUrl).into(this.image);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        imageClickListener.onClick(reply);
                    }
                });
            } else {
                this.image.setVisibility(View.GONE);
            }
        }
    }

    public interface AvatarClickListener {

        void onClick(Long userId);
    }

    public interface ImageClickListener {

        void onClick(ShotModel shot);
    }
    //endregion
}
