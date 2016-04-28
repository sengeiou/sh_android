package com.shootr.mobile.ui.adapters.holders;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.mobile.R;

public class ActionViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.action_icon) ImageView icon;
    @Bind(R.id.action_name) TextView name;
    @Bind(R.id.action_number) TextView optionalNumber;
    @Bind(R.id.menu_container) FrameLayout container;

    public ActionViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setIcon(@DrawableRes int iconRes) {
        icon.setImageResource(iconRes);
    }

    public void setName(@StringRes int nameRes) {
        name.setText(nameRes);
    }

    public void setName(String actionName) {
        name.setText(actionName);
    }

    public void setNumber(int number) {
        if (number < 1) {
            optionalNumber.setVisibility(View.GONE);
        } else {
            optionalNumber.setVisibility(View.VISIBLE);
            optionalNumber.setText(String.valueOf(number));
        }
    }

    public void disable() {
        Context context = name.getContext();
        container.setEnabled(false);
        name.setTextColor(context.getResources().getColor(R.color.gray_60));
    }
}