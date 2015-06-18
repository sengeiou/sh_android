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
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;
import com.shootr.android.ui.model.ActivityModel;
import com.shootr.android.ui.widgets.ClickableTextView;
import com.shootr.android.util.AndroidTimeUtils;
import com.shootr.android.util.PicassoWrapper;
import com.shootr.android.util.ShotTextSpannableBuilder;
import com.shootr.android.util.UsernameClickListener;
import java.util.ArrayList;
import java.util.List;

public class ActivityTimelineAdapter extends BindableAdapter<ActivityModel> {

    List<ActivityModel> activities;
    private PicassoWrapper picasso;
    private final View.OnClickListener avatarClickListener;
    private UsernameClickListener clickListener;
    private AndroidTimeUtils timeUtils;
    private int tagColor;
    private ShotTextSpannableBuilder shotTextSpannableBuilder;

    public ActivityTimelineAdapter(Context context, PicassoWrapper picasso, View.OnClickListener avatarClickListener,
      UsernameClickListener clickListener, AndroidTimeUtils timeUtils) {
        super(context);
        this.picasso = picasso;
        this.avatarClickListener = avatarClickListener;
        this.clickListener = clickListener;
        this.timeUtils = timeUtils;
        this.activities = new ArrayList<>(0);
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
        return activities.size();
    }

    @Override
    public ActivityModel getItem(int position) {
        return activities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        View view = null;
        switch (getItemViewType(position)) {
            case 0:
                view = inflater.inflate(R.layout.item_list_activity, container, false);
                view.setTag(new ViewHolder(view, avatarClickListener));
                break;
            default:
                break;
        }
        return view;
    }

    @Override
    public void bindView(final ActivityModel item, int position, View view) {
        switch (getItemViewType(position)) {
            case 0:
                ViewHolder vh = (ViewHolder) view.getTag();
                vh.position = position;

                String usernameTitle = item.getUsername();
                vh.name.setText(usernameTitle);

                String comment = item.getComment();
                String tag = null;
                if (shouldShowTag() && item.getEventTag() != null) {
                    tag = item.getEventTag();
                }

                SpannableStringBuilder commentWithTag = buildCommentTextWithTag(comment, tag);
                if (commentWithTag != null) {
                    addActivityComment(vh, commentWithTag);
                    vh.text.setVisibility(View.VISIBLE);
                } else {
                    vh.text.setVisibility(View.GONE);
                }

                long timestamp = item.getPublishDate().getTime();
                vh.timestamp.setText(timeUtils.getElapsedTime(getContext(), timestamp));

                String photo = item.getUserPhoto();
                picasso.loadProfilePhoto(photo).into(vh.avatar);
                vh.avatar.setTag(vh);
                break;
            default:
                break;
        }
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

    private void addActivityComment(ViewHolder vh, CharSequence comment) {
        CharSequence spannedComment = shotTextSpannableBuilder.formatWithUsernameSpans(comment, clickListener);
        vh.text.setText(spannedComment);
        vh.text.addLinks();
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

    public ActivityModel getLastActivity() {
        return activities.get(activities.size() - 1);
    }

    public static class ViewHolder {
        @InjectView(R.id.activity_avatar) public ImageView avatar;
        @InjectView(R.id.ativity_user_name) public TextView name;
        @InjectView(R.id.activity_timestamp) public TextView timestamp;
        @InjectView(R.id.activity_text) public ClickableTextView text;
        public int position;

        public ViewHolder(View view, View.OnClickListener avatarClickListener) {
            ButterKnife.inject(this, view);

            avatar.setOnClickListener(avatarClickListener);
        }

    }

}
