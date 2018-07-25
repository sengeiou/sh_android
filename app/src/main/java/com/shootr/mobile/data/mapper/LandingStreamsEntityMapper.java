package com.shootr.mobile.data.mapper;

import android.support.annotation.NonNull;
import com.shootr.mobile.data.entity.LandingStreamsEntity;
import com.shootr.mobile.data.entity.PromotedLandingItemEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.domain.model.HotStreams;
import com.shootr.mobile.domain.model.PrintableType;
import com.shootr.mobile.domain.model.PromotedLandingItem;
import com.shootr.mobile.domain.model.PromotedItems;
import com.shootr.mobile.domain.model.UserStreams;
import com.shootr.mobile.domain.model.stream.LandingStreams;
import java.util.ArrayList;
import javax.inject.Inject;

public class LandingStreamsEntityMapper {

  private final StreamEntityMapper streamEntityMapper;
  private final ImageMediaEntityMapper imageMediaEntityMapper;

  @Inject public LandingStreamsEntityMapper(StreamEntityMapper streamEntityMapper,
      ImageMediaEntityMapper imageMediaEntityMapper) {
    this.streamEntityMapper = streamEntityMapper;
    this.imageMediaEntityMapper = imageMediaEntityMapper;
  }

  public LandingStreams transform(LandingStreamsEntity landingStreamsEntity) {

    LandingStreams landingStreams = new LandingStreams();

    HotStreams hotStreams = new HotStreams();
    UserStreams userStreams = new UserStreams();
    PromotedItems promotedItems = new PromotedItems();

    ArrayList<PromotedLandingItem> promotedItemsList = setupPromotedItems(landingStreamsEntity);

    promotedItems.setPromotedItems(promotedItemsList);

    hotStreams.setStreams(
        new ArrayList<>(streamEntityMapper.transform(landingStreamsEntity.getHot().getData())));
    userStreams.setStreams(new ArrayList<>(
        streamEntityMapper.transform(landingStreamsEntity.getUserStreams().getData())));

    landingStreams.setUserStreams(userStreams);
    landingStreams.setHotStreams(hotStreams);
    landingStreams.setPromoted(promotedItems);

    return landingStreams;
  }

  @NonNull
  private ArrayList<PromotedLandingItem> setupPromotedItems(LandingStreamsEntity landingStreamsEntity) {
    ArrayList<PromotedLandingItem> promotedItemsList = new ArrayList<>();

    if (landingStreamsEntity.getPromoted() != null
        && landingStreamsEntity.getPromoted().getPromoted() != null) {
      for (PromotedLandingItemEntity promotedEntity : landingStreamsEntity.getPromoted().getPromoted()) {
        if (promotedEntity.getData().getResultType().equals(PrintableType.STREAM)) {
          PromotedLandingItem promotedItem = new PromotedLandingItem();
          promotedItem.setData(
              streamEntityMapper.transform((StreamEntity) promotedEntity.getData()));
          promotedItem.setImage(imageMediaEntityMapper.transform(promotedEntity.getImage()));
          promotedItem.setSubtitle(promotedEntity.getSubtitle());

          promotedItemsList.add(promotedItem);
        }
      }
    }
    return promotedItemsList;
  }
}
