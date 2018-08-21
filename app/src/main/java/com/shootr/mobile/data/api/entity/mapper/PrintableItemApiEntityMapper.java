package com.shootr.mobile.data.api.entity.mapper;

import com.shootr.mobile.data.api.entity.ExternalVideoApiEntity;
import com.shootr.mobile.data.api.entity.PrintableItemApiEntity;
import com.shootr.mobile.data.api.entity.PromotedReceiptApiEntity;
import com.shootr.mobile.data.api.entity.ShotApiEntity;
import com.shootr.mobile.data.api.entity.TopicApiEntity;
import com.shootr.mobile.data.entity.PrintableItemEntity;
import com.shootr.mobile.domain.model.PrintableType;
import javax.inject.Inject;

public class PrintableItemApiEntityMapper {

  private final ShotApiEntityMapper shotApiEntityMapper;
  private final TopicApiEntityMapper topicApiEntityMapper;
  private final ExternalVideoApiEntityMapper externalVideoApiEntityMapper;
  private final PromotedReceiptApiEntityMapper promotedReceiptApiEntityMapper;

  @Inject public PrintableItemApiEntityMapper(ShotApiEntityMapper shotApiEntityMapper,
      TopicApiEntityMapper topicApiEntityMapper,
      ExternalVideoApiEntityMapper externalVideoApiEntityMapper,
      PromotedReceiptApiEntityMapper promotedReceiptApiEntityMapper) {
    this.shotApiEntityMapper = shotApiEntityMapper;
    this.topicApiEntityMapper = topicApiEntityMapper;
    this.externalVideoApiEntityMapper = externalVideoApiEntityMapper;
    this.promotedReceiptApiEntityMapper = promotedReceiptApiEntityMapper;
  }

  public PrintableItemEntity map(PrintableItemApiEntity printableItemApiEntity) {

    if (printableItemApiEntity != null && printableItemApiEntity.getResultType()
        .equals(PrintableType.SHOT)) {
      return shotApiEntityMapper.transform((ShotApiEntity) printableItemApiEntity);
    } else if (printableItemApiEntity != null && printableItemApiEntity.getResultType()
        .equals(PrintableType.TOPIC)) {
      return topicApiEntityMapper.map((TopicApiEntity) printableItemApiEntity);
    } else if (printableItemApiEntity != null && printableItemApiEntity.getResultType()
        .equals(PrintableType.POLL)) {
      return (PrintableItemEntity) printableItemApiEntity;
    } else if (printableItemApiEntity != null && printableItemApiEntity.getResultType()
        .equals(PrintableType.EXTERNAL_VIDEO)) {
      return externalVideoApiEntityMapper.transform(
          (ExternalVideoApiEntity) printableItemApiEntity);
    } else if (printableItemApiEntity != null && printableItemApiEntity.getResultType()
        .equals(PrintableType.PROMOTED_RECEIPT)) {
      return promotedReceiptApiEntityMapper.transform(
          (PromotedReceiptApiEntity) printableItemApiEntity);
    } else if (printableItemApiEntity != null && printableItemApiEntity.getResultType()
        .equals(PrintableType.USER)) {
      return (PrintableItemEntity) printableItemApiEntity;
    } else if (printableItemApiEntity != null && printableItemApiEntity.getResultType()
        .equals(PrintableType.STREAM)) {
      return (PrintableItemEntity) printableItemApiEntity;
    }
    return new PrintableItemEntity() {
      @Override public String getResultType() {
        return PrintableType.UNKNOWN;
      }
    };
  }
}
