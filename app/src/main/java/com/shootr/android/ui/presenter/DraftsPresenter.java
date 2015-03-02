package com.shootr.android.ui.presenter;

import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.views.DraftsView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class DraftsPresenter implements Presenter {

    private DraftsView draftsView;

    @Inject public DraftsPresenter() {
    }

    public void initialize(DraftsView draftsView) {
        this.draftsView = draftsView;
        this.loadDrafts();
    }

    private void loadDrafts() {
        loadMockDrafts();
    }

    private void renderViewDraftList(List<ShotModel> drafts) {
        draftsView.showDrafts(drafts);
    }

    private void loadMockDrafts() {
        renderViewDraftList(mockDrafts());
    }

    private List<ShotModel> mockDrafts() {
        List<ShotModel> shots = new ArrayList<>();
        shots.add(mockShot("Windows",
          "El veloz murciélago hindú comía feliz cardillo y kiwi. La cigüeña tocaba el saxofón detrás del palenque de paja"));
        shots.add(mockShot(null, "El viejo Señor Gómez pedía queso, kiwi y habas, pero le ha tocado un saxofón."));
        shots.add(mockShot("Apple", "Jovencillo emponzoñado de whisky: ¡qué figurota exhibe!"));
        shots.add(mockShot(null,
          "Ví aquél BMW Z3 del año 1997, 4x2, y de RIN16\" y fijo costó $20,5K... -¿Te gustó? -¡Sí, 138HP!"));
        return shots;
    }

    private ShotModel mockShot(String tag, String comment) {
        ShotModel shotModel = new ShotModel();
        shotModel.setUsername("rafa");
        shotModel.setComment(comment);
        shotModel.setPhoto("https://pbs.twimg.com/profile_images/2576254530/xrq3ziszvvt90xf54579.png");
        shotModel.setEventTag(tag);
        shotModel.setCsysBirth(new Date());
        return shotModel;
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
