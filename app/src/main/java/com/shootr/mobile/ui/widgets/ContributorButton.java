package com.shootr.mobile.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;

public class ContributorButton extends FrameLayout {

    @BindView(R.id.add_contributor) View addContributor;
    @BindView(R.id.added_contributor) View addedContributor;

    public ContributorButton(Context context) {
        super(context);
        init();
    }

    public ContributorButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ContributorButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setClickable(true);
        LayoutInflater inflater = LayoutInflater.from(getContext());

        inflater.inflate(R.layout.generic_state_button_layout, this);

        ButterKnife.bind(this);
    }

    public void setAddContributor(boolean isAdded) {

        if (isAdded) {
            addContributor.setVisibility(GONE);
            addedContributor.setVisibility(VISIBLE);
        } else {
            addContributor.setVisibility(VISIBLE);
            addedContributor.setVisibility(GONE);
        }
    }
}
