package com.shootr.android.ui.adapters;

import android.content.res.Resources;
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
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.TimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import timber.log.Timber;

public class ShotDetailWithRepliesAdapter
  extends RecyclerView.Adapter<ShotDetailWithRepliesAdapter.ShotDetailViewHolder> {

    private static final int TYPE_MAIN_SHOT = 0;
    private static final int TYPE_REPLIES_HEADER = 1;
    private static final int TYPE_REPLY = 2;
    private static final int POSITION_MAIN_SHOT = 0;
    private static final int POSITION_REPLIES_HEADER = 1;

    private final PicassoWrapper picasso;
    private final TimeFormatter timeFormatter;
    private final Resources resources;

    private ShotModel mainShot;
    private List<ShotModel> replies;

    public ShotDetailWithRepliesAdapter(PicassoWrapper picasso, TimeFormatter timeFormatter, Resources resources) {
        this.picasso = picasso;
        this.timeFormatter = timeFormatter;
        this.resources = resources;
        this.replies = new ArrayList<>();
    }

    public void renderMainShot(ShotModel mainShot) {
        this.mainShot = mainShot;
        notifyItemChanged(POSITION_MAIN_SHOT);
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

    @Override
    public ShotDetailWithRepliesAdapter.ShotDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView;
        switch (viewType) {
            case TYPE_MAIN_SHOT:
                itemView = layoutInflater.inflate(R.layout.include_shot_detail, parent, false);
                return new ShotDetailMainViewHolder(itemView);
            default:
                throw new IllegalArgumentException(String.format("ItemViewType %d has no ViewHolder associated",
                  viewType));
        }
    }

    @Override public void onBindViewHolder(ShotDetailWithRepliesAdapter.ShotDetailViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_MAIN_SHOT:
                bindMainShotViewHolder((ShotDetailMainViewHolder) holder);
                break;
        }
    }

    private void bindMainShotViewHolder(ShotDetailMainViewHolder holder) {
        if (mainShot != null) {
            holder.bindView(mainShot);
        } else {
            Timber.w("Trying to render null main shot");
        }
    }

    //region View holders
    public abstract static class ShotDetailViewHolder extends RecyclerView.ViewHolder {

        public ShotDetailViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class ShotDetailMainViewHolder extends ShotDetailViewHolder {

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

        public void bindView(ShotModel shotModel) {
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
            String imageUrl = shotModel.getImage();
            if (imageUrl != null) {
                picasso.loadTimelineImage(imageUrl).into(shotImage);
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
    //endregion
}
