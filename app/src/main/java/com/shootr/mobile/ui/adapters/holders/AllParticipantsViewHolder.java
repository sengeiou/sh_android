package com.shootr.mobile.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.mobile.R;

public class AllParticipantsViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.all_participants_button) TextView button;

    public AllParticipantsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setVisible(boolean visible) {
        button.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
