package com.shootr.mobile.ui.adapters.holders;

import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;

public class SwitchViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.action_name) TextView name;
    @BindView(R.id.action_mute_switch) SwitchCompat muteSwitch;
    @BindView(R.id.action_mute_switch_container) FrameLayout muteContainer;

    public SwitchViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        setupMuteContainer();
    }

    private void setupMuteContainer() {
        muteContainer.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                muteSwitch.setChecked(!muteSwitch.isChecked());
            }
        });
    }

    public void setName(@StringRes int nameRes) {
        name.setText(nameRes);
    }

    public void setName(String actionName) {
        name.setText(actionName);
    }

    public void setMuteSwitch(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        muteSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    public void setMuteStatus(Boolean isChecked) {
        muteSwitch.setChecked(isChecked);
    }
}
