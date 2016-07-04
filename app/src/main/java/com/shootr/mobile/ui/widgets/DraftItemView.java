package com.shootr.mobile.ui.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.shootr.mobile.R;

public class DraftItemView extends FrameLayout {

    @Bind(R.id.shot_avatar) ImageView avatar;
    @Bind(R.id.shot_user_name) TextView name;
    @Bind(R.id.shot_text) ClickableTextView text;
    @Bind(R.id.shot_image) ImageView image;
    @Bind(R.id.shot_draft_buttons) View draftButtons;

    private boolean isExpanded;
    private float expandedElevation;
    private int fadeInStartDelay;
    private int fadeInDuration;
    private int fadeOutDuration;
    private int expandCollapseDuration;
    private int collapsedBackgroundColor;
    private int expandedBackgroundColor;

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
        super.onFinishInflate();
        init();
    }

    public void init() {
        ButterKnife.bind(this, this);
        expandedBackgroundColor = getResources().getColor(R.color.white);
        collapsedBackgroundColor = getResources().getColor(R.color.transparent);

        expandedElevation = getResources().getDimension(R.dimen.card_elevation);
        fadeInStartDelay = getResources().getInteger(R.integer.draft_buttons_fade_in_start);
        fadeInDuration = getResources().getInteger(R.integer.draft_buttons_fade_in_duration);
        fadeOutDuration = getResources().getInteger(R.integer.draft_buttons_fade_out_duration);
        expandCollapseDuration = getResources().getInteger(R.integer.draft_expand_collapse_duration);
    }

    public void collapse(boolean animate) {
        if (!isExpanded) {
            return;
        }
        setViewValuesForExpandedState(false, animate);
        isExpanded = false;
    }

    public void expand(boolean animate) {
        if (isExpanded) {
            return;
        }
        setViewValuesForExpandedState(true, animate);
        isExpanded = true;
    }

    private void setViewValuesForExpandedState(boolean expand, boolean animate) {
        setBackgroundColor(expand ? expandedBackgroundColor : collapsedBackgroundColor);
        ViewCompat.setTranslationZ(this, expand ? expandedElevation : 0);
        draftButtons.setVisibility(expand ? VISIBLE : View.GONE);
        draftButtons.setAlpha(expand ? 1f : 0f);
        if (animate) {
            handleExpandCollapseAnimation();
        }
    }

    private void handleExpandCollapseAnimation() {
        final ViewGroup view = this;
        final int startingHeight = view.getHeight();
        final ViewTreeObserver observer = this.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            private static final boolean DONT_DRAW_THIS_FRAME = false;

            @Override public boolean onPreDraw() {
                stopListeningNextFrames();

                final int endingHeight = view.getHeight();
                final int baseHeight = Math.min(endingHeight, startingHeight);
                final int expandDistance = Math.abs(endingHeight - startingHeight);
                final boolean isExpand = endingHeight > startingHeight;

                setViewToInitialState(isExpand);
                setupButtonsFadeEffect(isExpand);
                setupExpandCollapseAnimation(baseHeight, expandDistance, isExpand);
                return DONT_DRAW_THIS_FRAME;
            }

            private void setViewToInitialState(boolean isExpand) {
                view.getLayoutParams().height = startingHeight;
                if (!isExpand) {
                    draftButtons.setVisibility(VISIBLE);
                }
            }

            private void setupButtonsFadeEffect(boolean isExpand) {
                if (isExpand) {
                    draftButtons.setAlpha(0f);
                    draftButtons.animate()
                      .alpha(1f)
                      .setStartDelay(fadeInStartDelay)
                      .setDuration(fadeInDuration)
                      .start();
                } else {
                    draftButtons.setAlpha(1f);
                    draftButtons.animate().alpha(0f).setDuration(fadeOutDuration).start();
                }
                view.requestLayout();
            }

            private void setupExpandCollapseAnimation(final int baseHeight, final int expandDistance,
              final boolean isExpand) {
                final ValueAnimator animator = getAnimatorForAction(isExpand);
                //TODO scroll to make visible
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override public void onAnimationUpdate(ValueAnimator animation) {
                        Float value = (Float) animation.getAnimatedValue();
                        view.getLayoutParams().height = (int) (value * expandDistance + baseHeight);
                        float elevation = expandedElevation * value;
                        ViewCompat.setTranslationZ(view, elevation);
                        view.requestLayout();
                    }
                });

                setupFinalValuesAfterAnimation(isExpand, animator);

                animator.setDuration(expandCollapseDuration);
                animator.start();
            }

            private void setupFinalValuesAfterAnimation(final boolean isExpand, ValueAnimator animator) {
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override public void onAnimationEnd(Animator animation) {
                        view.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        if (!isExpand) {
                            draftButtons.setVisibility(GONE);
                        }
                    }
                });
            }

            private ValueAnimator getAnimatorForAction(boolean expand) {
                return expand ? ValueAnimator.ofFloat(0f, 1f) : ValueAnimator.ofFloat(1f, 0f);
            }

            private void stopListeningNextFrames() {
                if (observer.isAlive()) {
                    observer.removeOnPreDrawListener(this);
                }
            }
        });
    }
}
