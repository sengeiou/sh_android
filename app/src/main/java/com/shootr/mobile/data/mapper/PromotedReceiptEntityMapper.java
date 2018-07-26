package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.api.entity.PromotedReceiptEntity;
import com.shootr.mobile.domain.model.PromotedReceipt;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PromotedReceiptEntityMapper {

  @Inject public PromotedReceiptEntityMapper() {
  }

  public PromotedReceipt transform(PromotedReceiptEntity entity) {
    PromotedReceipt promotedReceipt = new PromotedReceipt();
    promotedReceipt.setData(entity.getData());
    promotedReceipt.setProductId(entity.getProductId());
    promotedReceipt.setDeleted(entity.getDeleted());

    return promotedReceipt;
  }

  public List<PromotedReceipt> transform(List<PromotedReceiptEntity> promotedReceiptApiEntities) {
    ArrayList<PromotedReceipt> promotedReceiptEntities = new ArrayList<>();
    for (PromotedReceiptEntity promotedReceiptApiEntity : promotedReceiptApiEntities) {
      promotedReceiptEntities.add(transform(promotedReceiptApiEntity));
    }

    return promotedReceiptEntities;
  }
}
