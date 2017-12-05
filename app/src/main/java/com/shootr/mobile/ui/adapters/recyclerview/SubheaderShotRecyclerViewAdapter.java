package com.shootr.mobile.ui.adapters.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.shootr.mobile.domain.model.shot.ShotType;
import com.shootr.mobile.ui.model.ShotModel;
import java.util.Collections;
import java.util.List;

public abstract class SubheaderShotRecyclerViewAdapter<VH extends RecyclerView.ViewHolder, H, T>
    extends RecyclerView.Adapter<VH> {

  protected static final int TYPE_HEADER_SHOT = -1;
  protected static final int TYPE_HEADER_CHECK_IN = -3;
  protected static final int TYPE_SUBHEADER_SHOT = -5;
  protected static final int TYPE_SUBHEADER_CHECK_IN = -7;
  protected static final int TYPE_ITEM_SHOT = -8;
  protected static final int TYPE_ITEM_CHECK_IN = -10;
  protected static final int TYPE_ITEM_CONTENT_AD = -11;

  private H header;
  private List<T> items = Collections.emptyList();

  /**
   * Invokes onCreateHeaderViewHolder or onCreateItemViewHolder methods based on the view type
   * param.
   */
  @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
    VH viewHolder;
    if (isHeaderType(viewType)) {
      viewHolder = onCreateHeaderViewHolder(parent, viewType);
    } else if (isSubheaderType(viewType)) {
      viewHolder = onCreateSubheaderViewHolder(parent, viewType);
    } else {
      viewHolder = onCreateItemViewHolder(parent, viewType);
    }
    return viewHolder;
  }

  /**
   * Invokes onBindHeaderViewHolder or onBindItemViewHolder methods based on the position param.
   */
  @Override public void onBindViewHolder(VH holder, int position) {
    if (isHeaderPosition(position)) {
      onBindHeaderViewHolder(holder, position);
    } else if (isSubheaderPosition(position)) {
      onBindSubheaderViewHolder(holder, position);
    } else {
      onBindItemViewHolder(holder, position);
    }
  }

  /**
   * Returns the type associated to an item given a position passed as arguments. If the position
   * is related to a header item returns the constant TYPE_HEADER, if not, returns TYPE_ITEM.
   * If your application has to support different types override this method and provide your
   * implementation. Remember that TYPE_HEADER and TYPE_ITEM are internal constants can be
   * used to identify an item given a position, try to use different values in your application.
   */
  @Override public int getItemViewType(int position) {
    int typeHeader = TYPE_ITEM_SHOT;
    ShotModel shotModel =
        items != null && !items.isEmpty() ? (ShotModel) items.get(position) : null;
    if (isHeaderPosition(position)) {
      typeHeader = getHeaderType(shotModel);
    } else if (isSubheaderPosition(position)) {
      typeHeader = getSubheaderType(shotModel);
    } else {
      typeHeader = getItemType(shotModel);
    }
    return typeHeader;
  }

  private int getItemType(ShotModel shotModel) {
    int typeHeader = TYPE_ITEM_SHOT;
    if (shotModel != null) {
      if (shotModel.getType().equals(ShotType.CTACHECKIN) || shotModel.getType()
          .equals(ShotType.CTAGENERICLINK)) {
        typeHeader = TYPE_ITEM_CHECK_IN;
      } else {
        typeHeader = TYPE_ITEM_SHOT;
      }
    }
    return typeHeader;
  }

  private int getSubheaderType(ShotModel shotModel) {
    int typeHeader = TYPE_SUBHEADER_SHOT;
    if (shotModel != null) {
      if (shotModel.getType().equals(ShotType.CTACHECKIN) || shotModel.getType()
          .equals(ShotType.CTAGENERICLINK)) {
        typeHeader = TYPE_SUBHEADER_CHECK_IN;
      } else {
        typeHeader = TYPE_SUBHEADER_SHOT;
      }
    }
    return typeHeader;
  }

  private int getHeaderType(ShotModel shotModel) {
    int typeHeader = TYPE_HEADER_SHOT;
    if (shotModel != null) {
      if (shotModel.getType().equals(ShotType.CTACHECKIN) || shotModel.getType()
          .equals(ShotType.CTAGENERICLINK)) {
        typeHeader = TYPE_HEADER_CHECK_IN;
      } else {
        typeHeader = TYPE_HEADER_SHOT;
      }
    }
    return typeHeader;
  }

  /**
   * Returns the items list size if there is no a header configured or the size plus one if there
   * is a header already configured.
   */
  @Override public int getItemCount() {
    return hasHeader() ? items.size() + 2 : items.size();
  }

  public H getHeader() {
    return header;
  }

  public Object getItem(int position) {
    int itemPosition = position;
    if (hasHeader() && hasItems()) {
      itemPosition = position;
      --itemPosition;
      --itemPosition;
    }
    return items.get(itemPosition);
  }

  public void setHeader(H header) {
    this.header = header;
  }

  public void setItems(List<T> items) {
    validateItems(items);
    this.items = items;
  }

  public List<T> getItems() {
    return this.items;
  }

  protected abstract VH onCreateHeaderViewHolder(ViewGroup parent, int viewType);

  protected abstract VH onCreateSubheaderViewHolder(ViewGroup parent, int viewType);

  protected abstract VH onCreateItemViewHolder(ViewGroup parent, int viewType);

  protected abstract void onBindHeaderViewHolder(VH holder, int position);

  protected abstract void onBindSubheaderViewHolder(VH holder, int position);

  protected abstract void onBindItemViewHolder(VH holder, int position);

  /**
   * Returns true if the position type parameter passed as argument is equals to 0 and the adapter
   * has a not null header already configured.
   */
  public boolean isHeaderPosition(int position) {
    return hasHeader() && position == 0;
  }

  public boolean isSubheaderPosition(int position) {
    return hasHeader() && position == 1;
  }

  /**
   * Returns true if the view type parameter passed as argument is equals to TYPE_HEADER.
   */
  protected boolean isHeaderType(int viewType) {
    return viewType == TYPE_HEADER_CHECK_IN || viewType == TYPE_HEADER_SHOT;
  }

  protected boolean isSubheaderType(int viewType) {
    return viewType == TYPE_SUBHEADER_CHECK_IN || viewType == TYPE_SUBHEADER_SHOT;
  }

  /**
   * Returns true if the header configured is not null.
   */
  protected boolean hasHeader() {
    return getHeader() != null;
  }

  private boolean hasItems() {
    return !items.isEmpty();
  }

  private void validateItems(List<T> items) {
    if (items == null) {
      throw new IllegalArgumentException("You can't use a null List<Item> instance.");
    }
  }

  protected int getFirstItemPosition() {
    return hasHeader() ? 2 : 0;
  }
}
