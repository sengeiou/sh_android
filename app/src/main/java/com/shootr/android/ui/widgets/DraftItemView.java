package com.shootr.android.ui.widgets;

import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;

public class DraftItemView extends ForegroundRelativeLayout {

    @InjectView(R.id.shot_avatar) ImageView avatar;
    @InjectView(R.id.shot_user_name) TextView name;
    @InjectView(R.id.shot_text) ClickableTextView text;
    @InjectView(R.id.shot_image) ImageView image;
    @InjectView(R.id.shot_draft_buttons) View draftButtons;
    @InjectView(R.id.shot_separator) View separator;

    private boolean isExpanded;

    public DraftItemView(Context context) {
        super(context);
    }

    public DraftItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DraftItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override protected void onFinishInflate() {
        init();
    }

    public void init() {
        ButterKnife.inject(this, this);
        setupTransitions();
    }

    public void collapse() {
        if(!isExpanded) {
            return;
        }
        ViewCompat.setTranslationZ(this, 0);
        draftButtons.setVisibility(View.GONE);
        setBackgroundColor(0x00ffffff);
        separator.setVisibility(View.VISIBLE);
        isExpanded = false;
    }

    public void expand() {
        if(isExpanded) {
            return;
        }
        setBackgroundColor(getResources().getColor(R.color.white));
        ViewCompat.setTranslationZ(this, getResources().getDimension(R.dimen.card_elevation));
        draftButtons.setVisibility(View.VISIBLE);
        separator.setVisibility(View.INVISIBLE);
        isExpanded = true;
    }


    private void setupTransitions() {
        LayoutTransition transition = new LayoutTransition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            transition.disableTransitionType(LayoutTransition.DISAPPEARING);
        } else {
            transition.setAnimator(LayoutTransition.DISAPPEARING, null);
        }
        transition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        transition.setDuration(LayoutTransition.CHANGE_DISAPPEARING, 250);
        transition.setStartDelay(LayoutTransition.APPEARING, 50);
        transition.setDuration(LayoutTransition.APPEARING, 200);
        transition.setDuration(LayoutTransition.CHANGE_APPEARING, 100);
        this.setLayoutTransition(transition);
    }
}
