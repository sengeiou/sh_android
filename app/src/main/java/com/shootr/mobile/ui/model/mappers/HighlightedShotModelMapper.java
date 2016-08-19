package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.shot.HighlightedShot;
import com.shootr.mobile.infraestructure.Mapper;
import com.shootr.mobile.ui.model.HighlightedShotModel;
import javax.inject.Inject;

public class HighlightedShotModelMapper extends Mapper<HighlightedShot, HighlightedShotModel> {

  private final ShotModelMapper shotModelMapper;

  @Inject public HighlightedShotModelMapper(ShotModelMapper shotModelMapper) {
    this.shotModelMapper = shotModelMapper;
  }

  @Override public HighlightedShotModel map(HighlightedShot value) {
    if (value == null) {
      return null;
    }

    HighlightedShotModel model = new HighlightedShotModel();
    model.setShotModel(shotModelMapper.transform(value.getShot()));
    model.setIdHighlightedShot(value.getIdHighlightedShot());
    model.setVisible(value.isVisible());
    return model;
  }

  @Override public HighlightedShot reverseMap(HighlightedShotModel value) {
    return null;
  }
}
