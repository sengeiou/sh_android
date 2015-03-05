package com.shootr.android.ui.adapters;

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
import com.shootr.android.ui.model.DraftModel;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.widgets.ClickableTextView;
import com.shootr.android.ui.widgets.DraftItemView;
import com.shootr.android.util.PicassoWrapper;
import java.util.ArrayList;
import java.util.List;

public class DraftAdapter extends RecyclerView.Adapter<DraftAdapter.DraftViewHolder> {

    public static final int NONE_EXPANDED_POSITION = -1;
    public static final DraftViewHolder NONE_EXPANDED_ITEM = null;
    private final PicassoWrapper picasso;
    private final DraftActionListener draftActionListener;

    private List<DraftModel> drafts = new ArrayList<>();
    private DraftViewHolder currentExpandedItem;
    private int currentExpandedItemPosition = -1;

    public DraftAdapter(PicassoWrapper picasso, DraftActionListener draftActionListener) {
        this.picasso = picasso;
        this.draftActionListener = draftActionListener;
    }

    public void setDrafts(List<DraftModel> drafts) {
        this.drafts = drafts;
        notifyDataSetChanged();
    }

    @Override public DraftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_shot_draft, parent, false);
        return new DraftViewHolder(itemView);
    }

    @Override public int getItemCount() {
        return drafts.size();
    }

    @Override public void onBindViewHolder(DraftViewHolder holder, int position) {
        DraftModel draftModel = drafts.get(position);
        ShotModel shotModel = draftModel.getShotModel();

        holder.name.setText(shotModel.getUsername());
        holder.text.setText(shotModel.getComment());
        picasso.loadProfilePhoto(shotModel.getPhoto()).into(holder.avatar);
        bindShotImageIfPresent(holder, draftModel);
        if (isExpandedLocked(position)) {
            currentExpandedItemPosition = position;
            holder.draftItemView.setClickable(false);
        }
        if (currentExpandedItemPosition == position) {
            currentExpandedItem = holder;
            holder.draftItemView.expand(false);
        } else {
            holder.draftItemView.collapse(false);
        }
    }

    private boolean isExpandedLocked(int position) {
        return getItemCount() <= 1;
    }

    private void bindShotImageIfPresent(DraftViewHolder holder, DraftModel draftModel) {
        ShotModel shotModel = draftModel.getShotModel();
        if (shotModel.getImage() != null) {
            picasso.loadTimelineImage(shotModel.getImage()).into(holder.image);
            holder.image.setVisibility(View.VISIBLE);
        } else if (draftModel.getImageFile() != null) {
            picasso.load(draftModel.getImageFile()).into(holder.image);
            holder.image.setVisibility(View.VISIBLE);
        } else {
            holder.image.setVisibility(View.GONE);
        }
    }

    private void toggleItem(DraftViewHolder holder) {
        if (currentExpandedItemPosition == holder.getPosition()) {
            collapseItem(holder);
        } else {
            expandItem(holder);
        }
    }

    private void collapseItem(DraftViewHolder holder) {
        holder.draftItemView.collapse(true);
        boolean isItemAtExpandedPosition = currentExpandedItemPosition == holder.getPosition();
        if (isItemAtExpandedPosition) {
            currentExpandedItemPosition = NONE_EXPANDED_POSITION;
        }
        boolean isCurrentExpandedItem = currentExpandedItem == holder;
        if (isCurrentExpandedItem) {
            currentExpandedItem = NONE_EXPANDED_ITEM;
        }
    }

    private void expandItem(DraftViewHolder holder) {
        collapseCurrentExpandedItem();
        holder.draftItemView.expand(true);
        currentExpandedItem = holder;
        currentExpandedItemPosition = holder.getPosition();
    }

    private void collapseCurrentExpandedItem() {
        if (currentExpandedItem != null) {
            boolean isExpandedItemVisible = currentExpandedItemPosition == currentExpandedItem.getPosition();
            if (isExpandedItemVisible) {
                currentExpandedItem.draftItemView.collapse(true);
            }
        }
        currentExpandedItemPosition = NONE_EXPANDED_POSITION;
    }

    class DraftViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.shot_avatar) ImageView avatar;
        @InjectView(R.id.shot_user_name) TextView name;
        @InjectView(R.id.shot_text) ClickableTextView text;
        @InjectView(R.id.shot_image) ImageView image;
        @InjectView(R.id.shot_draft_buttons) View draftButtons;
        @InjectView(R.id.shot_separator) View separator;
        DraftItemView draftItemView;

        public DraftViewHolder(View itemView) {
            super(itemView);
            draftItemView = (DraftItemView) itemView;
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (!isExpandedLocked(getPosition())) {
                        toggleItem(DraftViewHolder.this);
                    }
                }
            });
        }

        @OnClick(R.id.shot_draft_shoot)
        public void shoot() {
            draftActionListener.onShootDraft(getDraftForThisPosition());
        }

        @OnClick(R.id.shot_draft_edit)
        public void edit() {
            draftActionListener.onEditDraft(getDraftForThisPosition());
        }

        @OnClick(R.id.shot_draft_delete)
        public void delete() {
            draftActionListener.onDeleteDraft(getDraftForThisPosition());
        }

        private DraftModel getDraftForThisPosition() {
            return drafts.get(this.getPosition());
        }
    }

    public interface DraftActionListener {

        void onShootDraft(DraftModel draftModel);

        void onEditDraft(DraftModel draftModel);

        void onDeleteDraft(DraftModel draftModel);
    }
}
