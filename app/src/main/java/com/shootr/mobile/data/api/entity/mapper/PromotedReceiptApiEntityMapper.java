package com.shootr.mobile.data.api.entity.mapper;

import com.shootr.mobile.data.api.entity.PromotedReceiptApiEntity;
import com.shootr.mobile.data.api.entity.PromotedReceiptEntity;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PromotedReceiptApiEntityMapper {

  @Inject PromotedReceiptApiEntityMapper() {
  }

  public PromotedReceiptEntity transform(PromotedReceiptApiEntity promotedReceiptApiEntity) {
    PromotedReceiptEntity promotedReceiptEntity = new PromotedReceiptEntity();
    promotedReceiptEntity.setData(promotedReceiptApiEntity.getData());
    promotedReceiptEntity.setProductId(promotedReceiptApiEntity.getProductId());
    promotedReceiptEntity.setDeleted(promotedReceiptApiEntity.getDeleted());

    return promotedReceiptEntity;
  }

  public List<PromotedReceiptEntity> transform(List<PromotedReceiptApiEntity> promotedReceiptApiEntities) {
    ArrayList<PromotedReceiptEntity> promotedReceiptEntities = new ArrayList<>();
    for (PromotedReceiptApiEntity promotedReceiptApiEntity : promotedReceiptApiEntities) {
      promotedReceiptEntities.add(transform(promotedReceiptApiEntity));
    }

    return promotedReceiptEntities;
  }
}
