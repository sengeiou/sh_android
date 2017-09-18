package com.shootr.mobile.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.adapters.holders.OnBoardingHeaderViewHolder;
import com.shootr.mobile.ui.adapters.holders.OnBoardingStreamViewHolder;
import com.shootr.mobile.ui.adapters.holders.OnBoardingUserViewHolder;
import com.shootr.mobile.ui.adapters.listeners.OnBoardingFavoriteClickListener;
import com.shootr.mobile.ui.model.OnBoardingModel;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.InitialsLoader;
import java.util.List;

public class OnBoardingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public static final String USER_ONBOARDING = "user";
  public static final String STREAM_ONBOARDING = "stream";


  private static final int TYPE_HEADER = 0;
  private static final int TYPE_STREAM = 1;
  private static final int TYPE_USER = 2;
  private static final int UNKNOWN = -1;

  private List<OnBoardingModel> onBoardingModelList;
  private final OnBoardingFavoriteClickListener onFavoriteClickListener;
  private final ImageLoader imageLoader;
  private final InitialsLoader initialsLoader;
  private final String onBoardingType;

  private int lastAnimatedPosition = -1;
  private int screenHeight;

  public OnBoardingAdapter(OnBoardingFavoriteClickListener onFavoriteClickListener,
      ImageLoader imageLoader, InitialsLoader initialsLoader, String onBOardingType) {
    this.imageLoader = imageLoader;
    this.initialsLoader = initialsLoader;
    this.onFavoriteClickListener = onFavoriteClickListener;
    this.onBoardingType = onBOardingType;
  }

  public void setOnBoardingModelList(List<OnBoardingModel> onBoardingModelList) {
    this.onBoardingModelList = onBoardingModelList;
  }

  @Override public int getItemViewType(int position) {
    if (isHeaderPosition(position)) {
      return TYPE_HEADER;
    } else {
      if (onBoardingModelList.get(position - 1).getStreamModel() != null) {
        return TYPE_STREAM;
      } else if (onBoardingModelList.get(position - 1).getUserModel() != null) {
        return TYPE_USER;
      } else {
        return UNKNOWN;
      }
    }
  }

  private boolean isHeaderPosition(int position) {
    return position == 0;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == TYPE_STREAM) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_list_stream, parent, false);
      return new OnBoardingStreamViewHolder(view, onFavoriteClickListener, imageLoader,
          initialsLoader);
    } else if (viewType == TYPE_USER) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_list_user, parent, false);
      return new OnBoardingUserViewHolder(view, onFavoriteClickListener, imageLoader,
          initialsLoader);
    } else if (viewType == TYPE_HEADER) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.onboarding_header, parent, false);
      return new OnBoardingHeaderViewHolder(view, onBoardingType);
    } else {
      return  null;
    }
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder.getItemViewType() == TYPE_STREAM) {
      ((OnBoardingStreamViewHolder) holder).render(onBoardingModelList.get(position - 1));
    } else if (holder.getItemViewType() == TYPE_USER) {
      //TODO
    } else if (holder.getItemViewType() == TYPE_HEADER) {
      ((OnBoardingHeaderViewHolder) holder).render();
    }
  }

  @Override public int getItemCount() {
    return onBoardingModelList != null ? onBoardingModelList.size() + 1 : 1;
  }

  public void updateFavorite(OnBoardingModel onBoardingModel) {
    int position = onBoardingModelList.indexOf(onBoardingModel);
    onBoardingModelList.get(position).setFavorite(!onBoardingModel.isFavorite());
  }
}
