package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.PromotedTierEntity;
import com.shootr.mobile.domain.model.Benefits;
import com.shootr.mobile.domain.model.PromotedTier;
import com.shootr.mobile.domain.model.shot.Background;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PromotedTierEntityMapper {

  @Inject public PromotedTierEntityMapper() {
  }

  public PromotedTier transform(PromotedTierEntity promoted) {
    PromotedTier entity = new PromotedTier();

    entity.setProductId(promoted.getProductId());
    entity.setPrice(promoted.getPrice());
    entity.setCurrency(promoted.getCurrency());

    Background background = new Background();
    background.setAngle(promoted.getBackground().getAngle());
    background.setColors(promoted.getBackground().getColors());
    background.setType(promoted.getBackground().getType());
    entity.setBackground(background);

    Benefits benefits = new Benefits();
    benefits.setDuration(promoted.getBenefits().getDuration());
    benefits.setLenght(promoted.getBenefits().getLenght());
    benefits.setImportant(promoted.getBenefits().isImportant());
    entity.setBenefits(benefits);

    return entity;
  }

  public List<PromotedTier> transform(List<PromotedTierEntity> promoteds) {
    ArrayList<PromotedTier> list = new ArrayList<>();
    for (PromotedTierEntity promoted : promoteds) {
      list.add(transform(promoted));
    }
    return list;
  }
}
