package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.QueuedShot;
import com.shootr.mobile.domain.bus.ShotFailed;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.GetDraftsInteractor;
import com.shootr.mobile.domain.interactor.stream.GetStreamIsReadOnlyInteractor;
import com.shootr.mobile.domain.service.NetworkNotAvailableException;
import com.shootr.mobile.ui.views.NewShotBarView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.io.File;
import java.util.List;
import javax.inject.Inject;

public class NewShotBarPresenter implements Presenter, ShotFailed.Receiver {

    private final GetStreamIsReadOnlyInteractor getStreamIsReadOnlyInteractor;
    private final GetDraftsInteractor getDraftsInteractor;
    private final ErrorMessageFactory errorMessageFactory;
    private final Bus bus;

    private NewShotBarView newShotBarView;
    private String idStreamForShot;
    private boolean isStreamReadOnly = false;
    private boolean hasBeenPaused;
    private boolean isInStreamTimeline = false;

    @Inject public NewShotBarPresenter(GetStreamIsReadOnlyInteractor getStreamIsReadOnlyInteractor,
      GetDraftsInteractor getDraftsInteractor, ErrorMessageFactory errorMessageFactory, @Main Bus bus) {
        this.getStreamIsReadOnlyInteractor = getStreamIsReadOnlyInteractor;
        this.getDraftsInteractor = getDraftsInteractor;
        this.errorMessageFactory = errorMessageFactory;
        this.bus = bus;
    }

    public void setView(NewShotBarView newShotBarView) {
        this.newShotBarView = newShotBarView;
    }

    public void initialize(NewShotBarView newShotBarView, String idStreamForShot, boolean isInStreamTimeline) {
        this.idStreamForShot = idStreamForShot;
        this.setView(newShotBarView);
        this.checkReadOnlyStatus();
        this.updateDraftsButtonVisibility();
    }

    private void checkReadOnlyStatus() {
        getStreamIsReadOnlyInteractor.isStreamReadOnly(idStreamForShot, new Interactor.Callback<Boolean>() {
            @Override public void onLoaded(Boolean isReadOnly) {
                isStreamReadOnly = isReadOnly;
                if (isStreamReadOnly) {
                    showReadOnlyError();
                }
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                if(error instanceof NetworkNotAvailableException) {
                    /* no-op */
                } else {
                    showReadOnlyError();
                }
            }
        });
    }

    public void newShotFromTextBox() {
        if (!isStreamReadOnly) {
            newShotBarView.openNewShotView();
        } else {
            this.showReadOnlyError();
        }
    }

    public void newShotFromImage() {
        if (!isStreamReadOnly) {
            newShotBarView.pickImage();
        } else {
            this.showReadOnlyError();
        }
    }

    public void newShotImagePicked(File image) {
        newShotBarView.openNewShotViewWithImage(image);
    }

    private void updateDraftsButtonVisibility() {
        getDraftsInteractor.loadDrafts(new GetDraftsInteractor.Callback() {
            @Override public void onLoaded(List<QueuedShot> drafts) {
                if (!drafts.isEmpty()) {
                    newShotBarView.showDraftsButton();
                } else {
                    newShotBarView.hideDraftsButton();
                }
            }
        });
    }

    private void showReadOnlyError() {
        newShotBarView.showError(errorMessageFactory.getStreamReadOnlyError());
    }

    @Override public void resume() {
        bus.register(this);
        if(hasBeenPaused) {
            updateDraftsButtonVisibility();
        }
    }

    @Override public void pause() {
        bus.unregister(this);
        hasBeenPaused = true;
    }

    @Subscribe
    @Override public void onShotFailed(ShotFailed.Event event) {
        updateDraftsButtonVisibility();
    }
}
