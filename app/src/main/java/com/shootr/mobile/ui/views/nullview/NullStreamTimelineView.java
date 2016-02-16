package com.shootr.mobile.ui.views.nullview;

import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.views.StreamTimelineView;
import java.util.List;

public class NullStreamTimelineView implements StreamTimelineView {

    @Override public void setShots(List<ShotModel> shots) {
        /* no-op */
    }

    @Override public void hideShots() {
        /* no-op */
    }

    @Override public void showShots() {
        /* no-op */
    }

    @Override public void addNewShots(List<ShotModel> newShots) {
        /* no-op */
    }

    @Override public void addOldShots(List<ShotModel> oldShots) {
        /* no-op */
    }

    @Override public void showLoadingOldShots() {
        /* no-op */
    }

    @Override public void hideLoadingOldShots() {
        /* no-op */
    }

    @Override public void showCheckingForShots() {
        /* no-op */
    }

    @Override public void hideCheckingForShots() {
        /* no-op */
    }

    @Override public void showShotShared() {
        /* no-op */
    }

    @Override public void hideHoldingShots() {
        /* no-op */
    }

    @Override public void showAllStreamShots() {
        /* no-op */
    }

    @Override public void showHoldingShots() {
        /* no-op */
    }

    @Override public void hideAllStreamShots() {
        /* no-op */
    }

    @Override public void setTitle(String shortTitle) {
        /* no-op */
    }

    @Override public Integer getFirstVisiblePosition() {
        return null;
    }

    @Override public void setPosition(int newPosition) {
        /* no-op */
    }

    @Override public void showTimelineIndicator(Integer numberNewShots) {
        /* no-op */
    }

    @Override public void hideTimelineIndicator() {
        /* no-op */
    }

    @Override public void showEmpty() {
        /* no-op */
    }

    @Override public void hideEmpty() {
        /* no-op */
    }

    @Override public void showLoading() {
        /* no-op */
    }

    @Override public void hideLoading() {
        /* no-op */
    }

    @Override public void showError(String message) {
        /* no-op */
    }
}
