package com.shootr.mobile.ui.adapters.holders;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ahamed.multiviewadapter.BaseViewHolder;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.PromotedLandingAdapter;
import com.shootr.mobile.ui.adapters.binder.PromotedGroupBinder;
import com.shootr.mobile.ui.adapters.listeners.PromotedItemsClickListener;
import com.shootr.mobile.ui.model.PromotedGroupModel;
import com.shootr.mobile.ui.model.PromotedLandingItemModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.NumberFormatUtil;

public class PromotedGroupViewHolder extends BaseViewHolder<PromotedGroupModel> {

  @BindView(R.id.promoted_recycler) RecyclerView recyclerView;
  @BindView(R.id.subtitle) TextView promotedTitle;

  private final PromotedItemsClickListener promotedItemsClickListener;
  private final ImageLoader imageLoader;
  private final NumberFormatUtil numberFormatUtil;
  private final PromotedGroupBinder.ChangesListener changesListener;

  private LinearLayoutManager linearLayoutManager;
  private PromotedLandingAdapter adapter;
  private RecyclerView.RecycledViewPool recycledViewPool;

  public PromotedGroupViewHolder(View itemView,
      PromotedItemsClickListener promotedItemsClickListener, ImageLoader imageLoader,
      NumberFormatUtil numberFormatUtil, PromotedGroupBinder.ChangesListener changesListener) {
    super(itemView);
    this.changesListener = changesListener;
    ButterKnife.bind(this, itemView);
    this.promotedItemsClickListener = promotedItemsClickListener;
    this.imageLoader = imageLoader;
    this.numberFormatUtil = numberFormatUtil;
    SnapHelper snapHelper = new PagerSnapHelper();
    snapHelper.attachToRecyclerView(recyclerView);
  }

  public void render(PromotedGroupModel promotedGroupModel, int position, int offset) {
    linearLayoutManager =
        new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
    recyclerView.setLayoutManager(linearLayoutManager);

    adapter = new PromotedLandingAdapter(promotedItemsClickListener, imageLoader, numberFormatUtil);
    recyclerView.setAdapter(adapter);

    adapter.setList(promotedGroupModel);
    linearLayoutManager.scrollToPositionWithOffset(position, offset);

    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
        if (position != -1) {
          PromotedLandingItemModel aux = adapter.getItem(position);
          promotedTitle.setText(aux.getSubtitle());
        }
      }
    });
  }

  public void saveLastPosition() {
    try {
      int firstVisiblePosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
      View v = linearLayoutManager.findViewByPosition(firstVisiblePosition);
      int offset = v == null ? 0 : v.getLeft();
      changesListener.savePosition(firstVisiblePosition, offset);
    } catch (IndexOutOfBoundsException exception) {
      /* no-op */
    }
  }
}
