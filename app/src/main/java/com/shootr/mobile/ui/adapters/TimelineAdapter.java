package com.shootr.mobile.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.ui.adapters.listeners.OnAvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnHideClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.ShotTextSpannableBuilder;
import java.util.ArrayList;
import java.util.List;

public class TimelineAdapter extends BindableAdapter<ShotModel> {

    private final ImageLoader imageLoader;
    private final OnAvatarClickListener avatarClickListener;
    private final OnVideoClickListener videoClickListener;
    private final OnNiceShotListener onNiceShotListener;
    private final OnUsernameClickListener onUsernameClickListener;
    private final AndroidTimeUtils timeUtils;
    private final ShotTextSpannableBuilder shotTextSpannableBuilder;
    private final OnHideClickListener onHideClickListener;

    private List<ShotModel> shots;

    private boolean isCurrentUser;

    public TimelineAdapter(Context context,
      ImageLoader imageLoader,
      AndroidTimeUtils timeUtils,
      OnAvatarClickListener avatarClickListener,
      OnVideoClickListener videoClickListener,
      OnNiceShotListener onNiceShotListener,
      OnUsernameClickListener onUsernameClickListener,
      OnHideClickListener onHideClickListener,Boolean isCurrentUser) {
        super(context);
        this.imageLoader = imageLoader;
        this.avatarClickListener = avatarClickListener;
        this.videoClickListener = videoClickListener;
        this.onNiceShotListener = onNiceShotListener;
        this.onUsernameClickListener = onUsernameClickListener;
        this.timeUtils = timeUtils;
        this.shots = new ArrayList<>(0);
        shotTextSpannableBuilder = new ShotTextSpannableBuilder();
        this.onHideClickListener = onHideClickListener;
        this.isCurrentUser = isCurrentUser;
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
                view = inflater.inflate(com.shootr.mobile.R.layout.item_list_shot, container, false);
                view.setTag(new ShotViewHolder(view, avatarClickListener, videoClickListener,
                  onNiceShotListener,
                  onHideClickListener,
                  onUsernameClickListener,
                  timeUtils,
                  imageLoader,
                  shotTextSpannableBuilder,isCurrentUser));
                break;
            default:
                break;
        }
        return view;
    }

    @Override
    public void bindView(final ShotModel item, int position, View view) {
        ShotViewHolder vh = (ShotViewHolder) view.getTag();
        vh.position = position;
        vh.render(item, this.shouldShowShortTitle());
    }

    protected boolean shouldShowShortTitle() {
        return false;
    }

    public void addShotsBelow(List<ShotModel> newShots) {
        this.shots.addAll(newShots);
        notifyDataSetChanged();
    }

    public void removeShot(ShotModel shotModel) {
        this.shots.remove(shotModel);
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

    public OnAvatarClickListener getAvatarClickListener() {
        return avatarClickListener;
    }

    public OnVideoClickListener getVideoClickListener() {
        return videoClickListener;
    }

    public OnUsernameClickListener getOnUsernameClickListener() {
        return onUsernameClickListener;
    }

    public AndroidTimeUtils getTimeUtils() {
        return timeUtils;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public ShotTextSpannableBuilder getShotTextSpannableBuilder() {
        return shotTextSpannableBuilder;
    }

    public void setIsCurrentUser(boolean isCurrentUser) {
        this.isCurrentUser = isCurrentUser;
    }
}
