package com.shootr.mobile.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.mobile.R;

public class LoadingViewHolder extends RecyclerView.ViewHolder {

  @Bind(R.id.progressBar) ProgressBar progressBar;

  public LoadingViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this, itemView);
  }

  public void render() {
    progressBar.setIndeterminate(true);
  }
}
