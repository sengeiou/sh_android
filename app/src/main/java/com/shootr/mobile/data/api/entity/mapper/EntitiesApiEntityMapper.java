package com.shootr.mobile.data.api.entity.mapper;

import android.support.annotation.NonNull;
import com.shootr.mobile.data.api.entity.BaseMessageEntitiesApiEntity;
import com.shootr.mobile.data.api.entity.BaseMessagePollApiEntity;
import com.shootr.mobile.data.api.entity.CardApiEntity;
import com.shootr.mobile.data.api.entity.ImageMediaApiEntity;
import com.shootr.mobile.data.api.entity.MentionsApiEntity;
import com.shootr.mobile.data.api.entity.StreamIndexApiEntity;
import com.shootr.mobile.data.api.entity.UrlApiEntity;
import com.shootr.mobile.data.entity.BaseMessagePollEntity;
import com.shootr.mobile.data.entity.CardEntity;
import com.shootr.mobile.data.entity.EntitiesEntity;
import com.shootr.mobile.data.entity.ImageMediaEntity;
import com.shootr.mobile.data.entity.ImageSizeEntity;
import com.shootr.mobile.data.entity.MentionsEntity;
import com.shootr.mobile.data.entity.SizeEntity;
import com.shootr.mobile.data.entity.StreamIndexEntity;
import com.shootr.mobile.data.entity.UrlEntity;
import java.util.ArrayList;
import javax.inject.Inject;

public class EntitiesApiEntityMapper {

  @Inject public EntitiesApiEntityMapper() {
  }

  public EntitiesEntity setupEntities(BaseMessageEntitiesApiEntity entitiesApiEntity) {
    EntitiesEntity entitiesEntity = new EntitiesEntity();
    if (entitiesApiEntity != null) {
      setupUrls(entitiesApiEntity, entitiesEntity);
      setupStreams(entitiesApiEntity, entitiesEntity);
      setupPollsEntities(entitiesApiEntity, entitiesEntity);
      setupImages(entitiesApiEntity, entitiesEntity);
      setupMentions(entitiesApiEntity, entitiesEntity);
      setupCards(entitiesApiEntity, entitiesEntity);
    }
    return entitiesEntity;
  }

  private void setupStreams(BaseMessageEntitiesApiEntity entitiesApiEntity, EntitiesEntity entitiesEntity) {
    ArrayList<StreamIndexEntity> streamsEntities = new ArrayList<>();
    if (entitiesApiEntity != null) {
      for (StreamIndexApiEntity streamIndexApiEntity : entitiesApiEntity.getStreams()) {
        StreamIndexEntity streamIndexEntity = new StreamIndexEntity();
        streamIndexEntity.setStreamTitle(streamIndexApiEntity.getStreamTitle());
        streamIndexEntity.setIdStream(streamIndexApiEntity.getIdStream());
        streamIndexEntity.setIndices(streamIndexApiEntity.getIndices());
        streamsEntities.add(streamIndexEntity);
      }
    }
    entitiesEntity.setStreams(streamsEntities);
  }

  private void setupPollsEntities(BaseMessageEntitiesApiEntity entitiesApiEntity, EntitiesEntity entitiesEntity) {
    ArrayList<BaseMessagePollEntity> pollEntities = new ArrayList<>();

    for (BaseMessagePollApiEntity baseMessagePollApiEntity : entitiesApiEntity
        .getPolls()) {
      BaseMessagePollEntity baseMessagePollEntity = new BaseMessagePollEntity();
      baseMessagePollEntity.setIdPoll(baseMessagePollApiEntity.getIdPoll());
      baseMessagePollEntity.setIndices(baseMessagePollApiEntity.getIndices());
      baseMessagePollEntity.setPollQuestion(baseMessagePollApiEntity.getPollQuestion());
      pollEntities.add(baseMessagePollEntity);
    }
    entitiesEntity.setPolls(pollEntities);
  }

  private void setupUrls(BaseMessageEntitiesApiEntity entitiesApiEntity, EntitiesEntity entitiesEntity) {
    ArrayList<UrlEntity> urlEntities = new ArrayList<>();
    if (entitiesApiEntity != null) {
      for (UrlApiEntity urlApiEntity : entitiesApiEntity.getUrls()) {
        UrlEntity urlEntity = transformUrls(urlApiEntity);
        urlEntities.add(urlEntity);
      }
    }
    entitiesEntity.setUrls(urlEntities);
  }

  @NonNull private UrlEntity transformUrls(UrlApiEntity urlApiEntity) {
    UrlEntity urlEntity = new UrlEntity();
    urlEntity.setDisplayUrl(urlApiEntity.getDisplayUrl());
    urlEntity.setUrl(urlApiEntity.getUrl());
    urlEntity.setIndices(urlApiEntity.getIndices());
    return urlEntity;
  }

  private void setupImages(BaseMessageEntitiesApiEntity entitiesApiEntity, EntitiesEntity entitiesEntity) {
    ArrayList<ImageMediaEntity> imageMediaEntities = new ArrayList<>();

    if (entitiesApiEntity != null) {
      for (ImageMediaApiEntity imageMediaApiEntity : entitiesApiEntity.getImages()) {
        ImageMediaEntity imageMediaEntity = transformImages(imageMediaApiEntity);
        imageMediaEntities.add(imageMediaEntity);
      }
    }

    entitiesEntity.setImages(imageMediaEntities);

  }

  @NonNull private ImageMediaEntity transformImages(ImageMediaApiEntity imageMediaApiEntity) {
    ImageMediaEntity imageMediaEntity = new ImageMediaEntity();
    ImageSizeEntity imageSizeEntity = new ImageSizeEntity();

    SizeEntity lowSize = new SizeEntity();
    lowSize.setHeight(imageMediaApiEntity.getSizes().getLow().getHeight());
    lowSize.setWidth(imageMediaApiEntity.getSizes().getLow().getWidth());
    lowSize.setUrl(imageMediaApiEntity.getSizes().getLow().getUrl());
    imageSizeEntity.setLow(lowSize);

    SizeEntity mediumSize = new SizeEntity();
    mediumSize.setHeight(imageMediaApiEntity.getSizes().getMedium().getHeight());
    mediumSize.setWidth(imageMediaApiEntity.getSizes().getMedium().getWidth());
    mediumSize.setUrl(imageMediaApiEntity.getSizes().getMedium().getUrl());
    imageSizeEntity.setMedium(mediumSize);

    SizeEntity highSize = new SizeEntity();
    highSize.setHeight(imageMediaApiEntity.getSizes().getHigh().getHeight());
    highSize.setWidth(imageMediaApiEntity.getSizes().getHigh().getWidth());
    highSize.setUrl(imageMediaApiEntity.getSizes().getHigh().getUrl());
    imageSizeEntity.setHigh(highSize);

    imageMediaEntity.setType(imageMediaApiEntity.getType());
    imageMediaEntity.setSizes(imageSizeEntity);
    return imageMediaEntity;
  }

  private void setupMentions(BaseMessageEntitiesApiEntity entitiesApiEntity, EntitiesEntity entitiesEntity) {
    ArrayList<MentionsEntity> mentionsEntities = new ArrayList<>();
    if (entitiesApiEntity != null) {
      for (MentionsApiEntity mentionsApiEntity : entitiesApiEntity.getMentions()) {
        MentionsEntity mentionsEntity = new MentionsEntity();
        mentionsEntity.setIdUser(mentionsApiEntity.getIdUser());
        mentionsEntity.setUsername(mentionsApiEntity.getUserName());
        mentionsEntity.setIndices(mentionsApiEntity.getIndices());
        mentionsEntities.add(mentionsEntity);
      }
    }
    entitiesEntity.setMentions(mentionsEntities);
  }

  private void setupCards(BaseMessageEntitiesApiEntity entitiesApiEntity, EntitiesEntity entitiesEntity) {
    ArrayList<CardEntity> cardEntities = new ArrayList<>();
    if (entitiesApiEntity != null) {
      for (CardApiEntity cardApiEntity : entitiesApiEntity.getCards()) {
        CardEntity cardEntity = new CardEntity();

        cardEntity.setType(cardApiEntity.getType());
        cardEntity.setTitle(cardApiEntity.getTitle());
        cardEntity.setDuration(cardApiEntity.getDuration());
        cardEntity.setImage(transformImages(cardApiEntity.getImage()));
        cardEntity.setLink(transformUrls(cardApiEntity.getLink()));

        cardEntities.add(cardEntity);
      }
    }
    entitiesEntity.setCards(cardEntities);
  }

}
