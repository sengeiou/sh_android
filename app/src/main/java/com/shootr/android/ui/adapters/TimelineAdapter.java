package com.shootr.android.ui.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import com.shootr.android.R;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.widgets.ClickableTextView;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.ShotTextSpannableBuilder;
import com.shootr.android.util.UsernameClickListener;
import java.util.ArrayList;
import java.util.List;

public class TimelineAdapter extends BindableAdapter<ShotModel> {

    public static final String NO_COMMENT_BUT_SHOULD_SHOW_TAG = "";
    List<ShotModel> shots;
    private PicassoWrapper picasso;
    private final View.OnClickListener avatarClickListener;
    private final View.OnClickListener imageClickListener;
    private final VideoClickListener videoClickListener;
    private UsernameClickListener clickListener;
    private AndroidTimeUtils timeUtils;
    private int tagColor;
    private ShotTextSpannableBuilder shotTextSpannableBuilder;

    public TimelineAdapter(Context context,
      PicassoWrapper picasso,
      View.OnClickListener avatarClickListener,
      View.OnClickListener imageClickListener,
      VideoClickListener videoClickListener,
      UsernameClickListener clickListener,
      AndroidTimeUtils timeUtils) {
        super(context);
        this.picasso = picasso;
        this.avatarClickListener = avatarClickListener;
        this.imageClickListener = imageClickListener;
        this.videoClickListener = videoClickListener;
        this.clickListener = clickListener;
        this.timeUtils = timeUtils;
        this.shots = new ArrayList<>(0);
        tagColor = context.getResources().getColor(R.color.tag_color);
        this.shotTextSpannableBuilder = new ShotTextSpannableBuilder();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getCount() {
        return shots.size();
    }

    public boolean isLast(int position) {
        return position == getCount() - 1;
    }

    @Override
    public ShotModel getItem(int position) {
        return shots.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        View view = null;
        switch (getItemViewType(position)) {
            case 0: // Shot
                view = inflater.inflate(R.layout.item_list_shot, container, false);
                view.setTag(new ViewHolder(view, avatarClickListener, imageClickListener));
                break;
            default:
                break;
        }
        return view;
    }

    @Override
    public void bindView(final ShotModel item, int position, View view) {
        ViewHolder vh = (ViewHolder) view.getTag();
        vh.position = position;

        bindUsername(item, vh);
        bindComment(item, vh);
        bindElapsedTime(item, vh);
        bindPhoto(item, vh);
        bindImageInfo(item, vh);
        bindVideoInfo(item, vh);
        bindNiceInfo(item, vh);
    }

    protected void bindPhoto(ShotModel item, ViewHolder vh) {
        String photo = item.getPhoto();
        picasso.loadProfilePhoto(photo).into(vh.avatar);
        vh.avatar.setTag(vh);
        vh.image.setTag(vh);
    }

    protected void bindUsername(ShotModel item, ViewHolder vh) {
        String usernameTitle = item.getUsername();
        if (item.isReply()) {
            vh.name.setText(getReplyName(item));
        } else {
            vh.name.setText(usernameTitle);
        }
    }

    protected void bindElapsedTime(ShotModel item, ViewHolder vh) {
        long timestamp = item.getBirth().getTime();
        vh.timestamp.setText(timeUtils.getElapsedTime(getContext(), timestamp));
    }

    protected void bindComment(ShotModel item, ViewHolder vh) {
        String comment = item.getComment();
        String tag = null;
        if (shouldShowTag() && item.getStreamTag() != null) {
            tag = item.getStreamTag();
        }

        SpannableStringBuilder commentWithTag = buildCommentTextWithTag(comment, tag);
        if (commentWithTag != null) {
            addShotComment(vh, commentWithTag);
            vh.text.setVisibility(View.VISIBLE);
        } else {
            vh.text.setVisibility(View.GONE);
        }
    }

    protected void bindImageInfo(ShotModel item, ViewHolder vh) {
        String imageUrl = item.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            vh.image.setVisibility(View.VISIBLE);
            picasso.loadTimelineImage(imageUrl).into(vh.image);
        } else {
            vh.image.setVisibility(View.GONE);
        }
    }

    protected void bindVideoInfo(final ShotModel item, ViewHolder vh) {
        if (item.hasVideo()) {
            vh.videoFrame.setVisibility(View.VISIBLE);
            vh.videoDuration.setText(item.getVideoDuration());
            vh.videoFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoClickListener.onClick(item.getVideoUrl());
                }
            });
        } else {
            vh.videoFrame.setVisibility(View.GONE);
            vh.videoFrame.setOnClickListener(null);
        }
    }

    private void bindNiceInfo(ShotModel item, ViewHolder vh) {
        //TODO add nice(d?) status to the button
    }

    private @Nullable SpannableStringBuilder buildCommentTextWithTag(@Nullable String comment, @Nullable String tag) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (comment == null && tag == null) {
            return null;
        }
        if (comment != null) {
            builder.append(comment);
        }
        if (comment != null && tag != null) {
            builder.append(" ");
        }
        if (tag != null) {
            builder.append(formatTag(tag));
        }
        return builder;
    }


    protected boolean shouldShowTag() {
        return false;
    }

    private SpannableString formatTag(String tag) {
        ForegroundColorSpan span = new ForegroundColorSpan(tagColor);

        SpannableString tagSpan = new SpannableString(tag);
        tagSpan.setSpan(span, 0, tagSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return tagSpan;
    }

    private void addShotComment(ViewHolder vh, CharSequence comment) {
        CharSequence spannedComment = shotTextSpannableBuilder.formatWithUsernameSpans(comment, clickListener);
        vh.text.setText(spannedComment);
        vh.text.addLinks();
    }

    private String getReplyName(ShotModel item) {
        return getContext().getString(R.string.reply_name_pattern, item.getUsername(), item.getReplyUsername());
    }

    public void addShotsBelow(List<ShotModel> newShots) {
        this.shots.addAll(newShots);
        notifyDataSetChanged();
    }

    public void setShots(List<ShotModel> shots) {
        this.shots = shots;
        notifyDataSetChanged();
    }

    public void addShotsAbove(List<ShotModel> shotModels) {
        List<ShotModel> newShotList = new ArrayList<>(shotModels);
        newShotList.addAll(this.shots);
        this.shots = newShotList;
        notifyDataSetChanged();
    }

    public ShotModel getLastShot() {
        return shots.get(shots.size() - 1);
    }

    public static class ViewHolder {
        @Bind(R.id.shot_avatar) public ImageView avatar;
        @Bind(R.id.shot_user_name) public TextView name;
        @Bind(R.id.shot_timestamp) public TextView timestamp;
        @Bind(R.id.shot_text) public ClickableTextView text;
        @Bind(R.id.shot_image) public ImageView image;
        @Bind(R.id.shot_video_frame) public View videoFrame;
        @Bind(R.id.shot_video_duration) public TextView videoDuration;
        @Bind(R.id.shot_nice_button) public Checkable niceButton;
        public int position;

        public ViewHolder(View view, View.OnClickListener avatarClickListener, View.OnClickListener imageClickListener) {
            ButterKnife.bind(this, view);

            avatar.setOnClickListener(avatarClickListener);
            image.setOnClickListener(imageClickListener);
        }

        @OnClick(R.id.shot_nice_button)
        public void onNiceClick() {
            // TODO trigger listener instead
            niceButton.toggle();
        }

        public void setVideoClickListener(View.OnClickListener videoClickListener) {
            videoFrame.setOnClickListener(videoClickListener);
        }
    }

    public interface VideoClickListener {

        void onClick(String url);

    }
}
