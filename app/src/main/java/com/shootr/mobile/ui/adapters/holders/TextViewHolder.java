package com.shootr.mobile.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.mobile.R;

public class TextViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.text) TextView text;

    public TextViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setText(String text) {
        this.text.setText(text);
        this.text.setVisibility(text != null ? View.VISIBLE : View.GONE);
    }
}
