package com.shootr.android.ui.widgets;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;

public class FavoriteIconView extends FrameLayout {

    @InjectView(R.id.favorite_icon_add) ImageView addIcon;

    public FavoriteIconView(Context context) {
        super(context);
        init();
    }

    public FavoriteIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FavoriteIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.favorite_icon, this, true);
        ButterKnife.inject(this);
    }

    public void hideAnimated(Animator.AnimatorListener animationListener) {
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(addIcon, "alpha", 0f);
        alphaAnim.addListener(animationListener);
        alphaAnim.start();
    }
}
