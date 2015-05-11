package com.shootr.android.ui.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.shootr.android.R;

public class CheckinBar extends FrameLayout {

    @InjectView(R.id.checkin_text) TextView checkinText;

    private boolean isExpanded;
    private int checkinOriginalHeight;

    public CheckinBar(Context context) {
        super(context);
        init();
    }

    public CheckinBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CheckinBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //TODO this FrameLayout is wrapping the checkin_bar's layout. It's inneficient, it shoud contain a merge so this is the actual container
        LayoutInflater.from(getContext()).inflate(R.layout.checkin_bar, this, true);
        checkinOriginalHeight = getResources().getDimensionPixelSize(R.dimen.checkin_bar_height);
        setVisibility(GONE);
        ButterKnife.inject(this);
    }

    public void setText(String text) {
        checkinText.setText(text);
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void collapse() {
        if (!isExpanded) {
            return;
        }
        startExpandCollapseAnimation(false);
        isExpanded = false;
    }

    public void expand() {
        if (isExpanded) {
            return;
        }
        startExpandCollapseAnimation(true);
        isExpanded = true;
    }

    private void startExpandCollapseAnimation(final boolean expand) {
        setVisibility(VISIBLE);
        final ValueAnimator valueAnimator = getAnimatorForAction(expand);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                getLayoutParams().height = (int) (value * checkinOriginalHeight);
                requestLayout();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                if (!expand) {
                    setVisibility(GONE);
                }
            }
        });
        valueAnimator.start();
    }

    private ValueAnimator getAnimatorForAction(boolean expand) {
        return expand ? ValueAnimator.ofFloat(0f, 1f) : ValueAnimator.ofFloat(1f, 0f);
    }
}
