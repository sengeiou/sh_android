package com.shootr.mobile.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import com.shootr.mobile.ui.component.PhotoPickerController;
import com.shootr.mobile.ui.views.NewShotBarView;
import com.shootr.mobile.util.FeedbackMessage;

public abstract class NewShotBarViewDelegate implements NewShotBarView {

    private final PhotoPickerController photoPickerController;
    private final View draftsButton;
    private final FeedbackMessage feedbackMessage;

    public NewShotBarViewDelegate(PhotoPickerController photoPickerController, View draftsButton,
      FeedbackMessage feedbackMessage) {
        this.photoPickerController = photoPickerController;
        this.draftsButton = draftsButton;
        this.feedbackMessage = feedbackMessage;
        this.setupDraftButtonTransition();
    }

    private void setupDraftButtonTransition() {
        LayoutTransition transition = new LayoutTransition();
        // Disable button appearing and disappearing (button), we will do manually
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            transition.disableTransitionType(LayoutTransition.APPEARING);
            transition.disableTransitionType(LayoutTransition.DISAPPEARING);
        } else {
            transition.setAnimator(LayoutTransition.APPEARING, null);
            transition.setAnimator(LayoutTransition.DISAPPEARING, null);
        }

        // Setup text shrinking (when button appears)
        transition.setDuration(LayoutTransition.CHANGE_APPEARING, 200);
        transition.setInterpolator(LayoutTransition.CHANGE_APPEARING, new AccelerateDecelerateInterpolator());

        // Setup text expanding (when button disappears)
        transition.setInterpolator(LayoutTransition.CHANGE_DISAPPEARING, new AccelerateDecelerateInterpolator());
        transition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);

        ((ViewGroup) draftsButton.getParent()).setLayoutTransition(transition);
    }

    @Override public void showPrivateMessageOptions() {
        photoPickerController.pickPrivateMessageOptions();
    }

    @Override public void pickImage() {
        photoPickerController.pickPhoto();
    }

    @Override public void showHolderOptions() {
        photoPickerController.pickOption();
    }

    @Override public void showDraftsButton() {
        if (draftsButton.getVisibility() == View.VISIBLE) {
            return;
        }
        draftsButton.setVisibility(View.VISIBLE);
        draftsButton.setScaleX(0);
        draftsButton.setScaleY(0);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(draftsButton, "scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(draftsButton, "scaleY", 0f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(500);
        set.setStartDelay(200);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                draftsButton.setScaleX(1f);
                draftsButton.setScaleY(1f);
            }
        });
        set.start();
    }

    @Override public void hideDraftsButton() {
        if (draftsButton.getVisibility() == View.GONE) {
            return;
        }
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(draftsButton, "scaleX", 1f, 0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(draftsButton, "scaleY", 1f, 0f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(500);
        set.setInterpolator(new AccelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                draftsButton.setVisibility(View.GONE);
            }
        });
        set.start();
    }

    @Override public void showError(String errorMessage) {
        feedbackMessage.show(draftsButton, errorMessage);
    }
}