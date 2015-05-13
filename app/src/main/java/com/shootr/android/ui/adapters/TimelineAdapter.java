package com.shootr.android.ui.adapters;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shootr.android.R;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.widgets.ClickableTextView;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.StartedFollowingShotFormatter;
import com.shootr.android.util.UsernameClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TimelineAdapter extends BindableAdapter<ShotModel> {

    List<ShotModel> shots;
    private PicassoWrapper picasso;
    private final View.OnClickListener avatarClickListener;
    private final View.OnClickListener imageClickListener;
    private UsernameClickListener clickListener;
    private AndroidTimeUtils timeUtils;
    private int tagColor;

    private StartedFollowingShotFormatter startedFollowingShotFormatter;

    public TimelineAdapter(Context context, PicassoWrapper picasso, View.OnClickListener avatarClickListener,
                           View.OnClickListener imageClickListener, UsernameClickListener clickListener, AndroidTimeUtils timeUtils) {
        super(context);
        this.picasso = picasso;
        this.avatarClickListener = avatarClickListener;
        this.imageClickListener = imageClickListener;
        this.clickListener = clickListener;
        this.timeUtils = timeUtils;
        this.shots = new ArrayList<>(0);
        tagColor = context.getResources().getColor(R.color.tag_color);
        startedFollowingShotFormatter = new StartedFollowingShotFormatter();
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
    public void bindView(ShotModel item, int position, View view) {
        switch (getItemViewType(position)) {
            case 0: // Shot

                ViewHolder vh = (ViewHolder) view.getTag();
                vh.position = position;

                String usernameTitle = item.getUsername();
                if (item.isReply()) {
                    vh.name.setText(getReplyName(item));
                } else {
                    vh.name.setText(usernameTitle);
                }

                String comment = item.getComment();
                final SpannableString spannableStringBuilder = startedFollowingShotFormatter
                            .renderStartedFollowingShotSpan(comment, clickListener);
                if(spannableStringBuilder != null){
                    vh.text.setVisibility(View.VISIBLE);
                    vh.text.setText(spannableStringBuilder);
                    vh.text.addLinks();
                    vh.text.setClickable(true);
                    vh.text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            clickListener.onClickPassingUsername(startedFollowingShotFormatter.getUsername());
                        }
                    });
                }else if (comment != null) {
                    vh.text.setVisibility(View.VISIBLE);
                    vh.text.setText(comment);
                    vh.text.addLinks();
                } else {
                    vh.text.setVisibility(View.GONE);
                }

                if (shouldShowTag() && item.getEventTag() != null) {
                    addShotTag(vh, item);
                }

                long timestamp = item.getCsysBirth().getTime();
                vh.timestamp.setText(timeUtils.getElapsedTime(getContext(), timestamp));

                String photo = item.getPhoto();
                picasso.loadProfilePhoto(photo).into(vh.avatar);
                vh.avatar.setTag(vh);
                vh.image.setTag(vh);

                String imageUrl = item.getImage();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    vh.image.setVisibility(View.VISIBLE);
                    picasso.loadTimelineImage(imageUrl).into(vh.image);
                } else {
                    vh.image.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    private String getReplyName(ShotModel item) {
        return getContext().getString(R.string.reply_name_pattern, item.getUsername(), item.getReplyUsername());
    }

    protected boolean shouldShowTag() {
        return false;
    }

    private void addShotTag(ViewHolder vh, ShotModel shotModel) {
        String tag = shotModel.getEventTag();
        CharSequence currentText = vh.text.getText();

        SpannableStringBuilder spanBuilder = new SpannableStringBuilder(currentText);
        ForegroundColorSpan span = new ForegroundColorSpan(tagColor);

        SpannableString tagSpan = new SpannableString(tag);
        tagSpan.setSpan(span, 0, tagSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spanBuilder.append(" ");
        spanBuilder.append(tagSpan);
        vh.text.setText(spanBuilder);
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
        ArrayList<ShotModel> newShotList = new ArrayList<>(shotModels);
        newShotList.addAll(this.shots);
        this.shots = newShotList;
        notifyDataSetChanged();
    }

    public ShotModel getLastShot() {
        return shots.get(shots.size() - 1);
    }

    public static class ViewHolder {
        @InjectView(R.id.shot_avatar) public ImageView avatar;
        @InjectView(R.id.shot_user_name) public TextView name;
        @InjectView(R.id.shot_timestamp) public TextView timestamp;
        @InjectView(R.id.shot_text) public ClickableTextView text;
        @InjectView(R.id.shot_image) public ImageView image;
        public int position;

        public ViewHolder(View view, View.OnClickListener avatarClickListener, View.OnClickListener imageClickListener) {
            ButterKnife.inject(this, view);
            avatar.setOnClickListener(avatarClickListener);
            image.setOnClickListener(imageClickListener);
        }
    }
}
