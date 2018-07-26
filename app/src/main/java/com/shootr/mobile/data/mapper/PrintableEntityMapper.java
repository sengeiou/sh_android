package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.api.entity.PromotedReceiptEntity;
import com.shootr.mobile.data.entity.ExternalVideoEntity;
import com.shootr.mobile.data.entity.PollEntity;
import com.shootr.mobile.data.entity.PrintableItemEntity;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.data.entity.TopicEntity;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.domain.model.PrintableItem;
import com.shootr.mobile.domain.model.PrintableType;
import com.shootr.mobile.domain.model.UnknownPrintableItem;
import javax.inject.Inject;

public class PrintableEntityMapper {

  private final ShotEntityMapper shotEntityMapper;
  private final TopicEntityMapper topicEntityMapper;
  private final PollEntityMapper pollEntityMapper;
  private final ExternalVideoEntityMapper externalVideoEntityMapper;
  private final PromotedReceiptEntityMapper promotedReceiptEntityMapper;
  private final UserEntityMapper userEntityMapper;

  @Inject public PrintableEntityMapper(ShotEntityMapper shotEntityMapper,
      TopicEntityMapper topicEntityMapper, PollEntityMapper pollEntityMapper,
      ExternalVideoEntityMapper externalVideoEntityMapper,
      PromotedReceiptEntityMapper promotedReceiptEntityMapper, UserEntityMapper userEntityMapper) {
    this.shotEntityMapper = shotEntityMapper;
    this.topicEntityMapper = topicEntityMapper;
    this.pollEntityMapper = pollEntityMapper;
    this.externalVideoEntityMapper = externalVideoEntityMapper;
    this.promotedReceiptEntityMapper = promotedReceiptEntityMapper;
    this.userEntityMapper = userEntityMapper;
  }

  public PrintableItem map(PrintableItemEntity printableItemEntity) {

    if (printableItemEntity != null && printableItemEntity.getResultType()
        .equals(PrintableType.SHOT)) {
      return shotEntityMapper.transform((ShotEntity) printableItemEntity);
    } else if (printableItemEntity != null && printableItemEntity.getResultType()
        .equals(PrintableType.TOPIC)) {
      return topicEntityMapper.map((TopicEntity) printableItemEntity);
    } else if (printableItemEntity != null && printableItemEntity.getResultType()
        .equals(PrintableType.POLL)) {
      return pollEntityMapper.transform((PollEntity) printableItemEntity);
    } else if (printableItemEntity != null && printableItemEntity.getResultType()
        .equals(PrintableType.EXTERNAL_VIDEO)) {
      return externalVideoEntityMapper.transform(
          (ExternalVideoEntity) printableItemEntity);
    } else if (printableItemEntity != null && printableItemEntity.getResultType()
        .equals(PrintableType.PROMOTED_RECEIPT)) {
      return promotedReceiptEntityMapper.transform(
          (PromotedReceiptEntity) printableItemEntity);
    } else if (printableItemEntity != null && printableItemEntity.getResultType()
        .equals(PrintableType.USER)) {
      return userEntityMapper.transform(
          (UserEntity) printableItemEntity);
    }
    return new UnknownPrintableItem();
  }

}
