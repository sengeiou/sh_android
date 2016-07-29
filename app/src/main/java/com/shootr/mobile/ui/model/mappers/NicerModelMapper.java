package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.shot.Nicer;
import com.shootr.mobile.ui.model.NicerModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class NicerModelMapper {

    @Inject public NicerModelMapper() {
    }

    public NicerModel transform(Nicer nicer) {
        if (nicer == null) {
            return null;
        }
        NicerModel nicerModel = new NicerModel();
        nicerModel.setUserName(nicer.getUserName());
        nicerModel.setIdUser(nicer.getIdUser());
        nicerModel.setIdShot(nicer.getIdShot());
        nicerModel.setIdNice(nicer.getIdNice());

        return nicerModel;
    }

    public List<NicerModel> transform(List<Nicer> nicers) {
        List<NicerModel> nicerModels = new ArrayList<>(nicers.size());
        for (Nicer nicer : nicers) {
            nicerModels.add(transform(nicer));
        }

        return nicerModels;
    }
}
