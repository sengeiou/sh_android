package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.PromotedTier;
import com.shootr.mobile.ui.model.BackgroundModel;
import com.shootr.mobile.ui.model.BenefitsModel;
import com.shootr.mobile.ui.model.PromotedTierModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PromotedTierModelMapper {

  @Inject public PromotedTierModelMapper() {
  }

  public PromotedTierModel transform(PromotedTier promoted) {
    PromotedTierModel entity = new PromotedTierModel();

    entity.setProductId(promoted.getProductId());
    entity.setPrice(promoted.getPrice());
    entity.setCurrency(promoted.getCurrency());

    BackgroundModel background = new BackgroundModel();
    background.setAngle(promoted.getBackground().getAngle());
    background.setColors(promoted.getBackground().getColors());
    background.setType(promoted.getBackground().getType());
    entity.setBackground(background);

    BenefitsModel benefits = new BenefitsModel();
    benefits.setDuration(promoted.getBenefits().getDuration());
    benefits.setLenght(promoted.getBenefits().getLenght());
    benefits.setImportant(promoted.getBenefits().isImportant());
    entity.setBenefits(benefits);

    return entity;
  }

  public List<PromotedTierModel> transform(List<PromotedTier> promoteds) {
    ArrayList<PromotedTierModel> list = new ArrayList<>();
    for (PromotedTier promoted : promoteds) {
      list.add(transform(promoted));
    }
    return list;
  }
}
