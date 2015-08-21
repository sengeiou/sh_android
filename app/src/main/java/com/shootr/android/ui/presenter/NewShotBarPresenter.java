package com.shootr.android.ui.presenter;

import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.QueuedShot;
import com.shootr.android.domain.bus.ShotFailed;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.shot.GetDraftsInteractor;
import com.shootr.android.domain.interactor.stream.GetStreamIsReadOnlyInteractor;
import com.shootr.android.ui.views.NewShotBarView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.io.File;
import java.util.List;
import javax.inject.Inject;

public class NewShotBarPresenter implements Presenter, ShotFailed.Receiver {

    private final GetStreamIsReadOnlyInteractor getStreamIsReadOnlyInteractor;
    private final GetDraftsInteractor getDraftsInteractor;
    private final Bus bus;

    private NewShotBarView newShotBarView;
    private String idStreamForShot;
    private boolean isStreamReadOnly = false;

    @Inject public NewShotBarPresenter(GetStreamIsReadOnlyInteractor getStreamIsReadOnlyInteractor,
      GetDraftsInteractor getDraftsInteractor,
      @Main Bus bus) {
        this.getStreamIsReadOnlyInteractor = getStreamIsReadOnlyInteractor;
        this.getDraftsInteractor = getDraftsInteractor;
        this.bus = bus;
    }

    public void setView(NewShotBarView newShotBarView) {
        this.newShotBarView = newShotBarView;
    }

    public void initialize(NewShotBarView newShotBarView, String idStreamForShot) {
        this.idStreamForShot = idStreamForShot;
        this.setView(newShotBarView);
        this.checkReadOnlyStatus();
        this.updateDraftsButtonVisibility();
    }

    private void checkReadOnlyStatus() {
        getStreamIsReadOnlyInteractor.isStreamReadOnly(idStreamForShot, new Interactor.Callback<Boolean>() {
            @Override
            public void onLoaded(Boolean isReadOnly) {
                isStreamReadOnly = isReadOnly;
            }
        });
    }

    public void newShotFromTextBox() {
        if (!isStreamReadOnly) {
            newShotBarView.openNewShotView();
        } else {
            //TODO show error
        }
    }

    public void newShotFromImage() {
        if (!isStreamReadOnly) {
            newShotBarView.pickImage();
        } else {
            //TODO show error
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

    @Override public void resume() {
        bus.register(this);
        updateDraftsButtonVisibility();
    }

    @Override public void pause() {
        bus.unregister(this);
    }

    @Subscribe
    @Override public void onShotFailed(ShotFailed.Stream stream) {
        updateDraftsButtonVisibility();
    }
}
