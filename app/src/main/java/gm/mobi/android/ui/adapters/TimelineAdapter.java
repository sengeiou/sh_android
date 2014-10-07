package gm.mobi.android.ui.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import gm.mobi.android.R;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.util.TimeUtils;

public class TimelineAdapter extends BindableAdapter<Shot> {

    List<Shot> shots;
    private Picasso picasso;
    private final View.OnClickListener avatarClickListener;

    public TimelineAdapter(Context context, List<Shot> shots, Picasso picasso, View.OnClickListener avatarClickListener) {
        super(context);
        this.picasso = picasso;
        this.avatarClickListener = avatarClickListener;
        this.shots = shots;
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
    public Shot getItem(int position) {
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
                view.setTag(new ViewHolder(view, avatarClickListener));
                break;
        }
        return view;
    }

    @Override
    public void bindView(Shot item, int position, View view) {
        switch (getItemViewType(position)) {
            case 0: // Shot

                ViewHolder vh = (ViewHolder) view.getTag();
                vh.position = position;

                vh.name.setText(item.getUser().getName());

                vh.text.setText(item.getComment());

                long timestamp = item.getCsys_birth().getTime();
                vh.timestamp.setText(TimeUtils.getElapsedTime(getContext(), timestamp));

                String photo = item.getUser().getPhoto();
                boolean isValidPhotoUrl = photo != null && !photo.isEmpty();
                if (isValidPhotoUrl) {
                    picasso.load(photo).into(vh.avatar);
                } else {
                    picasso.load(R.drawable.ic_contact_picture_default).into(vh.avatar);
                }
                vh.avatar.setTag(vh);
                break;
        }
    }

    public void addShotsBelow(List<Shot> newShots) {
        this.shots.addAll(newShots);
        notifyDataSetChanged();
    }

    public void setShots(List<Shot> shots) {
        this.shots = shots;
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        @InjectView(R.id.shot_avatar) ImageView avatar;
        @InjectView(R.id.shot_user_name) TextView name;
        @InjectView(R.id.shot_timestamp) TextView timestamp;
        @InjectView(R.id.shot_text) TextView text;
        public int position;

        public ViewHolder(View view, View.OnClickListener avatarClickListener) {
            ButterKnife.inject(this, view);
            avatar.setOnClickListener(avatarClickListener);

        }
    }

}
