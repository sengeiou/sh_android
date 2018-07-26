package com.shootr.mobile.data.api.entity.mapper;

import android.support.annotation.NonNull;
import com.shootr.mobile.data.api.entity.DataApiEntity;
import com.shootr.mobile.data.api.entity.ExternalVideoApiEntity;
import com.shootr.mobile.data.api.entity.ItemsApiEntity;
import com.shootr.mobile.data.api.entity.PrintableItemApiEntity;
import com.shootr.mobile.data.api.entity.ShotApiEntity;
import com.shootr.mobile.data.api.entity.TopicApiEntity;
import com.shootr.mobile.data.entity.DataEntity;
import com.shootr.mobile.data.entity.ItemsEntity;
import com.shootr.mobile.data.entity.PrintableItemEntity;
import com.shootr.mobile.domain.model.PrintableType;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class DataApiEntityMapper {

  private final ShotApiEntityMapper shotApiEntityMapper;
  private final TopicApiEntityMapper topicApiEntityMapper;
  private final ExternalVideoApiEntityMapper externalVideoApiEntityMapper;

  @Inject public DataApiEntityMapper(ShotApiEntityMapper shotApiEntityMapper,
      TopicApiEntityMapper topicApiEntityMapper,
      ExternalVideoApiEntityMapper externalVideoApiEntityMapper) {
    this.shotApiEntityMapper = shotApiEntityMapper;
    this.topicApiEntityMapper = topicApiEntityMapper;
    this.externalVideoApiEntityMapper = externalVideoApiEntityMapper;
  }

  public DataEntity map(DataApiEntity apiEntity) {

    DataEntity dataEntity = new DataEntity();

    if (apiEntity != null) {
      ArrayList<PrintableItemEntity> printableItemEntities = getPrintableItemEntities(apiEntity.getData());
      dataEntity.setData(printableItemEntities);
    }

    return dataEntity;
  }

  @NonNull private ArrayList<PrintableItemEntity> getPrintableItemEntities(
      List<PrintableItemApiEntity> items) {
    ArrayList<PrintableItemEntity> printableItemEntities = new ArrayList<>();

    for (PrintableItemApiEntity printableItemApiEntity : items) {
      if (printableItemApiEntity != null && printableItemApiEntity.getResultType()
          .equals(PrintableType.SHOT)) {
        printableItemEntities.add(
            shotApiEntityMapper.transform((ShotApiEntity) printableItemApiEntity));
      } else if (printableItemApiEntity != null && printableItemApiEntity.getResultType()
          .equals(PrintableType.TOPIC)) {
        printableItemEntities.add(
            topicApiEntityMapper.map((TopicApiEntity) printableItemApiEntity));
      } else if (printableItemApiEntity != null && printableItemApiEntity.getResultType()
          .equals(PrintableType.POLL)) {
        printableItemEntities.add((PrintableItemEntity) printableItemApiEntity);
      } else if (printableItemApiEntity != null && printableItemApiEntity.getResultType()
          .equals(PrintableType.EXTERNAL_VIDEO)) {
        printableItemEntities.add(externalVideoApiEntityMapper.transform(
            (ExternalVideoApiEntity) printableItemApiEntity));
      } else if (printableItemApiEntity != null && printableItemApiEntity.getResultType()
          .equals(PrintableType.USER)) {
        printableItemEntities.add((PrintableItemEntity) printableItemApiEntity);
      }
    }
    return printableItemEntities;
  }

  public ItemsEntity map(ItemsApiEntity apiEntity) {

    ItemsEntity itemsEntity = new ItemsEntity();

    itemsEntity.setPagination(apiEntity.getPagination());
    itemsEntity.setData(getPrintableItemEntities(apiEntity.getData()));

    return itemsEntity;
  }
}
