package com.shootr.android.ui.presenter;

import com.shootr.android.domain.QueuedShot;
import com.shootr.android.domain.interactor.shot.GetDraftsInteractor;
import com.shootr.android.ui.views.NewShotBarView;
import java.io.File;
import java.util.List;
import javax.inject.Inject;

public class NewShotBarPresenter implements Presenter {

    private final GetDraftsInteractor getDraftsInteractor;

    private NewShotBarView newShotBarView;

    @Inject public NewShotBarPresenter(GetDraftsInteractor getDraftsInteractor) {
        this.getDraftsInteractor = getDraftsInteractor;
    }

    protected void setView(NewShotBarView newShotBarView) {
        this.newShotBarView = newShotBarView;
    }

    public void initialize(NewShotBarView newShotBarView) {
        this.setView(newShotBarView);
        this.updateDraftsButtonVisibility();
    }

    public void newShotFromTextBox() {
        newShotBarView.openNewShotView();
    }

    public void newShotFromImage() {
        newShotBarView.pickImage();
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

    }

    @Override public void pause() {

    }
}
