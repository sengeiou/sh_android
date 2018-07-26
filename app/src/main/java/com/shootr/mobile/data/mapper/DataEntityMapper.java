package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.DataEntity;
import com.shootr.mobile.data.entity.ExternalVideoEntity;
import com.shootr.mobile.data.entity.ItemsEntity;
import com.shootr.mobile.data.entity.PollEntity;
import com.shootr.mobile.data.entity.PrintableItemEntity;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.data.entity.TopicEntity;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.domain.model.DataItem;
import com.shootr.mobile.domain.model.PrintableItem;
import com.shootr.mobile.domain.model.PrintableType;
import com.shootr.mobile.domain.model.TimelineItem;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class DataEntityMapper {

  private final ShotEntityMapper shotEntityMapper;
  private final TopicEntityMapper topicEntityMapper;
  private final PollEntityMapper pollEntityMapper;
  private final ExternalVideoEntityMapper externalVideoEntityMapper;
  private final UserEntityMapper userEntityMapper;

  @Inject
  public DataEntityMapper(ShotEntityMapper shotEntityMapper, TopicEntityMapper topicEntityMapper,
      PollEntityMapper pollEntityMapper, ExternalVideoEntityMapper externalVideoEntityMapper,
      UserEntityMapper userEntityMapper) {
    this.shotEntityMapper = shotEntityMapper;
    this.topicEntityMapper = topicEntityMapper;
    this.pollEntityMapper = pollEntityMapper;
    this.externalVideoEntityMapper = externalVideoEntityMapper;
    this.userEntityMapper = userEntityMapper;
  }

  public DataItem map(DataEntity entity) {

    DataItem dataItem = new DataItem();
    if (entity.getData() != null) {
      dataItem.setData(getPrintableItemEntities(entity.getData()));
    }

    return dataItem;
  }

  public TimelineItem map(ItemsEntity itemsEntity) {

    TimelineItem timelineItem = new TimelineItem();
    timelineItem.setMaxTimestamp(itemsEntity.getPagination().getMaxTimestamp());
    timelineItem.setSinceTimstamp(itemsEntity.getPagination().getSinceTimestamp());
    timelineItem.setData(getPrintableItemEntities(itemsEntity.getData()));

    return timelineItem;
  }

  private ArrayList<PrintableItem> getPrintableItemEntities(List<PrintableItemEntity> items) {
    ArrayList<PrintableItem> printableItems = new ArrayList<>();

    for (PrintableItemEntity printableItem : items) {
      if (printableItem.getResultType().equals(PrintableType.SHOT)) {
        printableItems.add(shotEntityMapper.transform((ShotEntity) printableItem));
      } else if (printableItem.getResultType().equals(PrintableType.TOPIC)) {
        printableItems.add(topicEntityMapper.map((TopicEntity) printableItem));
      } else if (printableItem.getResultType().equals(PrintableType.POLL)) {
        printableItems.add(pollEntityMapper.transform((PollEntity) printableItem));
      } else if (printableItem.getResultType().equals(PrintableType.EXTERNAL_VIDEO)) {
        printableItems.add(
            externalVideoEntityMapper.transform((ExternalVideoEntity) printableItem));
      } else if (printableItem.getResultType().equals(PrintableType.USER)) {
        printableItems.add(
            userEntityMapper.transform((UserEntity) printableItem));
      }
    }
    return printableItems;
  }
}
