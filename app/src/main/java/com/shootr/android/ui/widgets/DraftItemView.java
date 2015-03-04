package com.shootr.android.ui.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
    private float expandedElevation;
    private int fadeInStartDelay;
    private int fadeInDuration;
    private int fadeOutDuration;
    private int expandCollapseDuration;

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
        expandedElevation = getResources().getDimension(R.dimen.card_elevation);
        fadeInStartDelay = getResources().getInteger(R.integer.draft_buttons_fade_in_start);
        fadeInDuration = getResources().getInteger(R.integer.draft_buttons_fade_in_duration);
        fadeOutDuration = getResources().getInteger(R.integer.draft_buttons_fade_out_duration);
        expandCollapseDuration = getResources().getInteger(R.integer.draft_expand_collapse_duration);
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

        handleExpandCollapseAnimation();
    }

    public void expand() {
        if(isExpanded) {
            return;
        }
        setBackgroundColor(getResources().getColor(R.color.white));
        ViewCompat.setTranslationZ(this, expandedElevation);
        draftButtons.setVisibility(View.VISIBLE);
        separator.setVisibility(View.INVISIBLE);
        isExpanded = true;

        handleExpandCollapseAnimation();
    }

    private void handleExpandCollapseAnimation() {
        final ViewGroup view = this;
        final int startingHeight = view.getHeight();
        final ViewTreeObserver observer = this.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override public boolean onPreDraw() {
                if (observer.isAlive()) {
                    observer.removeOnPreDrawListener(this);
                }

                final int endingHeight = view.getHeight();
                final int distance = Math.abs(endingHeight - startingHeight);
                final int baseHeight = Math.min(endingHeight, startingHeight);
                final boolean isExpand = endingHeight > startingHeight;

                view.getLayoutParams().height = startingHeight;
                if (!isExpand) {
                    draftButtons.setVisibility(VISIBLE);
                }

                // Buttons animation
                if (isExpand) {
                    draftButtons.setAlpha(0f);
                    draftButtons.animate().alpha(1f).setStartDelay(fadeInStartDelay).setDuration(fadeInDuration).start();
                } else {
                    draftButtons.setAlpha(1f);
                    draftButtons.animate().alpha(0f).setDuration(fadeOutDuration).start();
                }
                view.requestLayout();

                //Expansion and shadow animation
                final ValueAnimator animator = isExpand ? ValueAnimator.ofFloat(0f, 1f) : ValueAnimator.ofFloat(1f, 0f);
                //TODO scroll to visible
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override public void onAnimationUpdate(ValueAnimator animation) {
                        Float value = (Float) animation.getAnimatedValue();
                        view.getLayoutParams().height = (int) (value * distance + baseHeight);
                        float elevation = expandedElevation * value;
                        ViewCompat.setTranslationZ(view, elevation);
                        view.requestLayout();
                    }
                });

                // Set final values after animation
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override public void onAnimationEnd(Animator animation) {
                        view.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        if (isExpand) {
                            //draftButtons.setAlpha(1f);
                        } else {
                            draftButtons.setVisibility(GONE);
                        }
                    }
                });

                animator.setDuration(expandCollapseDuration);
                animator.start();

                return false;
            }
        });
    }


}
