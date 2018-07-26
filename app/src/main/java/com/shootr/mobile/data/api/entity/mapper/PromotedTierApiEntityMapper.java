package com.shootr.mobile.data.api.entity.mapper;

import com.shootr.mobile.data.api.entity.PromotedTierApiEntity;
import com.shootr.mobile.data.entity.BackgroundEntity;
import com.shootr.mobile.data.entity.BenefitsEntity;
import com.shootr.mobile.data.entity.PromotedTierEntity;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PromotedTierApiEntityMapper {

  @Inject public PromotedTierApiEntityMapper() {
  }

  public PromotedTierEntity transform(PromotedTierApiEntity promoted) {
    PromotedTierEntity entity = new PromotedTierEntity();

    entity.setProductId(promoted.getProductId());
    entity.setPrice(promoted.getPrice());
    entity.setCurrency(promoted.getCurrency());

    BackgroundEntity backgroundEntity = new BackgroundEntity();
    backgroundEntity.setAngle(promoted.getBackground().getAngle());
    backgroundEntity.setColors(promoted.getBackground().getColors());
    backgroundEntity.setType(promoted.getBackground().getType());
    entity.setBackground(backgroundEntity);

    BenefitsEntity benefitsEntity = new BenefitsEntity();
    benefitsEntity.setDuration(promoted.getBenefits().getDuration());
    benefitsEntity.setLenght(promoted.getBenefits().getLenght());
    benefitsEntity.setImportant(promoted.getBenefits().isImportant());
    entity.setBenefits(benefitsEntity);

    return entity;
  }

  public List<PromotedTierEntity> transform(List<PromotedTierApiEntity> promoteds) {
    ArrayList<PromotedTierEntity> list = new ArrayList<>();
    for (PromotedTierApiEntity promoted : promoteds) {
      list.add(transform(promoted));
    }
    return list;
  }
}
