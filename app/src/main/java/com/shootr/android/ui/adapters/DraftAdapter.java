package com.shootr.android.ui.adapters;

import android.animation.LayoutTransition;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.shootr.android.R;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.widgets.ClickableTextView;
import com.shootr.android.util.PicassoWrapper;
import java.util.ArrayList;
import java.util.List;

public class DraftAdapter extends RecyclerView.Adapter<DraftAdapter.DraftViewHolder> {

    private final PicassoWrapper picasso;

    private List<ShotModel> drafts = new ArrayList<>();
    private DraftViewHolder currentExpandedItem;
    private float expandedElevation;
    private int expandedBackgroundColor;
    private int collapsedBackgroundColor;

    public DraftAdapter(Resources resources, PicassoWrapper picasso) {
        this.picasso = picasso;
        expandedBackgroundColor = resources.getColor(R.color.white);
        collapsedBackgroundColor = 0x00ffffff;
        expandedElevation = resources.getDimension(R.dimen.card_elevation);

    }

    public void setDrafts(List<ShotModel> drafts) {
        this.drafts = drafts;
    }

    @Override public DraftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_shot_draft, parent, false);
        return new DraftViewHolder(itemView);
    }

    @Override public int getItemCount() {
        return drafts.size();
    }

    @Override public void onBindViewHolder(DraftViewHolder holder, int position) {
        ShotModel shotModel = drafts.get(position);
        holder.name.setText(shotModel.getUsername());
        holder.text.setText(shotModel.getComment());
        picasso.loadProfilePhoto(shotModel.getPhoto()).into(holder.avatar);
        bindShotImageIfPresent(holder, shotModel);
    }

    private void bindShotImageIfPresent(DraftViewHolder holder, ShotModel shotModel) {
        if (shotModel.getImage() != null) {
            picasso.loadTimelineImage(shotModel.getImage()).into(holder.image);
            holder.image.setVisibility(View.VISIBLE);
        } else {
            holder.image.setVisibility(View.GONE);
        }
    }

    private void toggleItem(DraftViewHolder holder) {
        if (currentExpandedItem == holder) {
            collapseItem(holder);
        } else {
            expandItem(holder);
        }
        //notifyItemRangeChanged(0, drafts.size());
    }

    private void expandItem(DraftViewHolder holder) {
        collapseCurrentExpandedItem();
        View itemView = holder.itemView;
        itemView.setBackgroundColor(expandedBackgroundColor);
        ViewCompat.setTranslationZ(itemView, expandedElevation);
        holder.draftButtons.setVisibility(View.VISIBLE);
        holder.separator.setVisibility(View.INVISIBLE);
        currentExpandedItem = holder;
    }

    private void collapseCurrentExpandedItem() {
        if (currentExpandedItem != null) {
            collapseItem(currentExpandedItem);
        }
    }

    private void collapseItem(DraftViewHolder holder) {
        View itemView = holder.itemView;
        ViewCompat.setTranslationZ(itemView, 0);
        holder.draftButtons.setVisibility(View.GONE);
        itemView.setBackgroundColor(collapsedBackgroundColor);
        holder.separator.setVisibility(View.VISIBLE);
        currentExpandedItem = null;
    }

    class DraftViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.shot_avatar) ImageView avatar;
        @InjectView(R.id.shot_user_name) TextView name;
        @InjectView(R.id.shot_text) ClickableTextView text;
        @InjectView(R.id.shot_image) ImageView image;
        @InjectView(R.id.shot_draft_buttons) View draftButtons;
        @InjectView(R.id.shot_separator) View separator;

        public DraftViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    toggleItem(DraftViewHolder.this);
                }
            });
            setupTransitions((ViewGroup) itemView);
        }

        @OnClick(R.id.shot_draft_shoot)
        public void shoot() {
            /* no-op */
        }

        @OnClick(R.id.shot_draft_edit)
        public void edit() {
            /* no-op */
        }

        @OnClick(R.id.shot_draft_remove)
        public void remove() {
            /* no-op */
        }

        private void setupTransitions(ViewGroup itemView) {
            LayoutTransition transition = new LayoutTransition();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                transition.disableTransitionType(LayoutTransition.DISAPPEARING);
            } else {
                transition.setAnimator(LayoutTransition.DISAPPEARING, null);
            }
            transition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
            transition.setDuration(LayoutTransition.CHANGE_DISAPPEARING, 250);
            transition.setStartDelay(LayoutTransition.APPEARING, 50);
            transition.setDuration(LayoutTransition.APPEARING, 200);
            transition.setDuration(LayoutTransition.CHANGE_APPEARING, 100);
            ((ViewGroup) itemView).setLayoutTransition(transition);
        }
    }
}
