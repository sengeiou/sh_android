package com.shootr.android.ui.presenter;

import com.shootr.android.domain.QueuedShot;
import com.shootr.android.domain.interactor.shot.GetDraftsInteractor;
import com.shootr.android.domain.interactor.shot.SendDraftInteractor;
import com.shootr.android.ui.model.DraftModel;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.DraftModelMapper;
import com.shootr.android.ui.views.DraftsView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class DraftsPresenter implements Presenter {

    private final GetDraftsInteractor getDraftsInteractor;
    private final SendDraftInteractor sendDraftInteractor;
    private final DraftModelMapper draftModelMapper;

    private DraftsView draftsView;

    @Inject public DraftsPresenter(GetDraftsInteractor getDraftsInteractor, SendDraftInteractor sendDraftInteractor,
      DraftModelMapper draftModelMapper) {
        this.getDraftsInteractor = getDraftsInteractor;
        this.sendDraftInteractor = sendDraftInteractor;
        this.draftModelMapper = draftModelMapper;
    }

    public void initialize(DraftsView draftsView) {
        this.draftsView = draftsView;
        this.loadDrafts();
    }

    private void loadDrafts() {
        getDraftsInteractor.loadDrafts(new GetDraftsInteractor.Callback() {
            @Override public void onLoaded(List<QueuedShot> drafts) {
                onDraftListLoaded(draftModelMapper.transform(drafts));
            }
        });
    }

    private void onDraftListLoaded(List<DraftModel> drafts) {
        if (drafts.isEmpty()) {
            draftsView.showEmpty();
        } else {
            draftsView.hideEmpty();
        }
        renderViewDraftList(drafts);
        showShootAllButtonIfMoreThanOneDraft(drafts);
    }

    private void renderViewDraftList(List<DraftModel> drafts) {
        draftsView.showDrafts(drafts);
    }

    private void showShootAllButtonIfMoreThanOneDraft(List<DraftModel> drafts) {
        if (drafts.size() > 1) {
            draftsView.showShootAllButton();
        } else {
            draftsView.hideShootAllButton();
        }
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }

    public void sendDraft(DraftModel draftModel) {
        sendDraftInteractor.sendDraft(draftModel.getIdQueue());
    }
}
