package com.shootr.mobile.ui.adapters;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.holders.ShotDetailMainViewHolder;
import com.shootr.mobile.ui.adapters.holders.ShotDetailParentViewHolder;
import com.shootr.mobile.ui.adapters.holders.ShotDetailRepliesHeaderHolder;
import com.shootr.mobile.ui.adapters.holders.ShotDetailReplyHolder;
import com.shootr.mobile.ui.adapters.listeners.AvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnParentShownListener;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShareClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NicerTextSpannableBuilder;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShotTextSpannableBuilder;
import com.shootr.mobile.util.TimeFormatter;
import java.util.ArrayList;
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
    private final ShotClickListener streamClickListener;
    private final ShotClickListener imageClickListener;
    private final OnVideoClickListener videoClickListener;
    private final OnUsernameClickListener onUsernameClickListener;
    private final TimeFormatter timeFormatter;
    private final NumberFormatUtil numberFormatUtil;
    private final Resources resources;
    private final AndroidTimeUtils timeUtils;
    private final OnParentShownListener onParentShownListener;
    private final OnNiceShotListener onNiceShotListener;
    private final ShotClickListener onClickListenerPinToProfile;
    private final ShotClickListener nicesClickListener;
    private final OnUrlClickListener onUrlClickListener;
    private final ShareClickListener reshootClickListener;
    private final ShareClickListener shareClickListener;

    private ShotModel mainShot;
    private List<ShotModel> replies;
    private float itemElevation;
    private List<ShotModel> parents;
    private boolean isShowingParent = false;
    private ShotTextSpannableBuilder shotTextSpannableBuilder;
    private NicerTextSpannableBuilder nicerTextSpannableBuilder;
    private ShotDetailMainViewHolder mainHolder;

    public ShotDetailWithRepliesAdapter(ImageLoader imageLoader,
        AvatarClickListener avatarClickListener, ShotClickListener parentShotClickListener,
        ShotClickListener replyShotClickListener, ShotClickListener streamClickListener,
        ShotClickListener imageClickListener, OnVideoClickListener videoClickListener,
        OnUsernameClickListener onUsernameClickListener, NumberFormatUtil numberFormatUtil,
        ShotClickListener onClickListenerPinToProfile, OnParentShownListener onParentShownListener,
        OnNiceShotListener onNiceShotListener, ShotClickListener nicesClickListener,
        OnUrlClickListener onUrlClickListener, TimeFormatter timeFormatter, Resources resources,
        AndroidTimeUtils timeUtils, ShareClickListener reshootClickListener,
        ShareClickListener shareClickListener) {
        this.imageLoader = imageLoader;
        this.avatarClickListener = avatarClickListener;
        this.parentShotClickListener = parentShotClickListener;
        this.replyShotClickListener = replyShotClickListener;
        this.streamClickListener = streamClickListener;
        this.imageClickListener = imageClickListener;
        this.videoClickListener = videoClickListener;
        this.onUsernameClickListener = onUsernameClickListener;
        this.numberFormatUtil = numberFormatUtil;
        this.onClickListenerPinToProfile = onClickListenerPinToProfile;
        this.onParentShownListener = onParentShownListener;
        this.nicesClickListener = nicesClickListener;
        this.onUrlClickListener = onUrlClickListener;
        this.timeFormatter = timeFormatter;
        this.resources = resources;
        this.timeUtils = timeUtils;
        this.onNiceShotListener = onNiceShotListener;
        this.reshootClickListener = reshootClickListener;
        this.shareClickListener = shareClickListener;
        this.replies = new ArrayList<>();
        this.itemElevation = resources.getDimension(R.dimen.card_elevation);
        this.shotTextSpannableBuilder = new ShotTextSpannableBuilder();
        this.nicerTextSpannableBuilder = new NicerTextSpannableBuilder();
    }

    public void hidePinToProfileButton() {
        if (mainShot != null) {
            mainShot.setCanBePinned(false);
            notifyItemChanged(getPositionMainShot());
        }
    }

    public void showPinToProfileContainer() {
        if (mainShot != null) {
            mainShot.setCanBePinned(true);
            notifyItemChanged(getPositionMainShot());
        }
    }

    public void disableStreamTitle() {
        if (mainShot != null) {
            mainShot.setTitleEnabled(false);
            notifyItemChanged(getPositionMainShot());
        }
    }

    public void enableStreamTitle() {
        if (mainShot != null) {
            mainShot.setTitleEnabled(true);
            notifyItemChanged(getPositionMainShot());
        }
    }

    private boolean hasParent() {
        return isShowingParent && mainShot != null && mainShot.isReply();
    }

    private int getPositionMainShot() {
        return hasParent() ? parents.size() : 0;
    }

    private int getPositionRepliesHeader() {
        return hasParent() ? parents.size() + 1 : 1;
    }

    private int adapterPositionToReplyPosition(int adapterPosition) {
        int firstReplyPosition = getPositionRepliesHeader() + 1;
        return adapterPosition - firstReplyPosition;
    }

    //region Lifecycle methods
    @Override public int getItemViewType(int position) {
        if (hasParent() && position < parents.size()) {
            return TYPE_PARENT_SHOT;
        } else if (position == getPositionMainShot()) {
            return TYPE_MAIN_SHOT;
        } else if (position == getPositionRepliesHeader()) {
            return TYPE_REPLIES_HEADER;
        } else {
            return TYPE_REPLY;
        }
    }

    private void showParent() {
        isShowingParent = true;
        recalculateShotsListPosition();
    }

    private void recalculateShotsListPosition() {
        onParentShownListener.onShown(parents.size(), replies.size());
    }

    @Override public int getItemCount() {
        int itemCount = 1;
        if (hasParent()) {
            itemCount += parents.size();
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
                return setupShotDetailParentViewHolder(parent, layoutInflater);
            case TYPE_MAIN_SHOT:
                return setupShotDetailMainViewHolder(parent, layoutInflater);
            case TYPE_REPLIES_HEADER:
                itemView = layoutInflater.inflate(R.layout.item_list_replies_header, parent, false);
                return new ShotDetailRepliesHeaderHolder(itemView);
            case TYPE_REPLY:
                return setupShotDetailReplyHolder(parent, layoutInflater);
            default:
                throw new IllegalArgumentException(String.format("ItemViewType %d has no ViewHolder associated",
                  viewType));
        }
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_PARENT_SHOT:
                bindParentsViewHolder((ShotDetailParentViewHolder) holder, position);
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
    //endregion

    //region Setups
    @NonNull private RecyclerView.ViewHolder setupShotDetailReplyHolder(ViewGroup parent,
      LayoutInflater layoutInflater) {
        View itemView;
        itemView = layoutInflater.inflate(R.layout.item_list_shot_reply, parent, false);
        ViewCompat.setElevation(itemView, itemElevation);
        return new ShotDetailReplyHolder(itemView,
          shotTextSpannableBuilder,
          onUsernameClickListener,
          timeUtils,
          imageLoader,
          avatarClickListener,
          imageClickListener,
          videoClickListener,
          onNiceShotListener,
          replyShotClickListener, numberFormatUtil);
    }

    @NonNull
    private RecyclerView.ViewHolder setupShotDetailMainViewHolder(ViewGroup parent, LayoutInflater layoutInflater) {
        View itemView;
        itemView = layoutInflater.inflate(R.layout.include_shot_detail, parent, false);
        ViewCompat.setElevation(itemView, itemElevation);
        return new ShotDetailMainViewHolder(itemView,
          imageLoader,
          avatarClickListener,
          streamClickListener,
          imageClickListener,
          videoClickListener,
          onUsernameClickListener,
          timeFormatter, numberFormatUtil, resources,
          onNiceShotListener,
          onClickListenerPinToProfile,
          nicesClickListener,
          shotTextSpannableBuilder,
          nicerTextSpannableBuilder, onUrlClickListener, reshootClickListener, shareClickListener);
    }

    @NonNull
    private RecyclerView.ViewHolder setupShotDetailParentViewHolder(ViewGroup parent, LayoutInflater layoutInflater) {
        View itemView;
        itemView = layoutInflater.inflate(R.layout.include_shot_detail_parent, parent, false);
        return new ShotDetailParentViewHolder(itemView,
          shotTextSpannableBuilder,
          onUsernameClickListener,
          timeUtils,
          imageLoader,
          avatarClickListener,
          imageClickListener,
          videoClickListener,
          onNiceShotListener,
          parentShotClickListener, numberFormatUtil, resources);
    }
    //endregion

    //region Renderers
    public void renderMainShot(ShotModel mainShot) {
        this.mainShot = mainShot;
        notifyDataSetChanged();
    }

    public void renderParentShot(List<ShotModel> parentShot) {
        this.parents = parentShot;
        notifyDataSetChanged();
        if (!isShowingParent) {
            showParent();
        }
    }

    public void renderReplies(List<ShotModel> replies) {
        this.replies = replies;
        notifyDataSetChanged();
    }

    private void bindParentsViewHolder(ShotDetailParentViewHolder holder, int adapterPosition) {
        if (parents != null) {
            ShotModel shotModel = parents.get(adapterPosition);
            holder.bindView(shotModel);
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

    public ShotModel getMainShot() {
        return mainShot;
    }

    //endregion
}
