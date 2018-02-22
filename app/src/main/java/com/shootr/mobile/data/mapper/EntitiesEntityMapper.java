package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.BaseMessagePollEntity;
import com.shootr.mobile.data.entity.EntitiesEntity;
import com.shootr.mobile.data.entity.ImageMediaEntity;
import com.shootr.mobile.data.entity.ImageSizeEntity;
import com.shootr.mobile.data.entity.MentionsEntity;
import com.shootr.mobile.data.entity.SizeEntity;
import com.shootr.mobile.data.entity.StreamIndexEntity;
import com.shootr.mobile.data.entity.UrlEntity;
import com.shootr.mobile.domain.model.ImageMedia;
import com.shootr.mobile.domain.model.ImageSize;
import com.shootr.mobile.domain.model.Sizes;
import com.shootr.mobile.domain.model.shot.Entities;
import com.shootr.mobile.domain.model.shot.Mention;
import com.shootr.mobile.domain.model.shot.Poll;
import com.shootr.mobile.domain.model.shot.StreamIndex;
import com.shootr.mobile.domain.model.shot.Url;
import java.util.ArrayList;
import javax.inject.Inject;

public class EntitiesEntityMapper {

  @Inject public EntitiesEntityMapper() {
  }

  public EntitiesEntity setupEntitiesEntity(Entities entities) {
    EntitiesEntity entitiesEntity = new EntitiesEntity();
    if (entities != null) {
      setupUrls(entities, entitiesEntity);
      setupPolls(entities, entitiesEntity);
      setupStreams(entities, entitiesEntity);
      setupImages(entities, entitiesEntity);
      setupMentions(entities, entitiesEntity);
    }
    return entitiesEntity;
  }

  private void setupPolls(Entities entities, EntitiesEntity entitiesEntity) {
    ArrayList<BaseMessagePollEntity> baseMessagePollEntities = new ArrayList<>();
    for (Poll poll : entities.getPolls()) {
      BaseMessagePollEntity baseMessagePollEntity = new BaseMessagePollEntity();
      baseMessagePollEntity.setIndices(poll.getIndices());
      baseMessagePollEntity.setIdPoll(poll.getIdPoll());
      baseMessagePollEntity.setPollQuestion(poll.getPollQuestion());
      baseMessagePollEntities.add(baseMessagePollEntity);
    }
    entitiesEntity.setPolls(baseMessagePollEntities);
  }

  private void setupStreams(Entities entities, EntitiesEntity entitiesEntity) {
    ArrayList<StreamIndexEntity> streamIndexEntities = new ArrayList<>();
    for (StreamIndex stream : entities.getStreams()) {
      StreamIndexEntity streamIndexEntity = new StreamIndexEntity();
      streamIndexEntity.setIndices(stream.getIndices());
      streamIndexEntity.setIdStream(stream.getIdStream());
      streamIndexEntity.setStreamTitle(stream.getStreamTitle());
      streamIndexEntities.add(streamIndexEntity);
    }
    entitiesEntity.setStreams(streamIndexEntities);
  }

  private void setupUrls(Entities shot, EntitiesEntity entitiesEntity) {
    ArrayList<UrlEntity> urlEntities = new ArrayList<>();
    for (Url urlApiEntity : shot.getUrls()) {
      UrlEntity urlEntity = new UrlEntity();
      urlEntity.setDisplayUrl(urlApiEntity.getDisplayUrl());
      urlEntity.setUrl(urlApiEntity.getUrl());
      urlEntity.setIndices(urlApiEntity.getIndices());
      urlEntities.add(urlEntity);
    }

    entitiesEntity.setUrls(urlEntities);
  }

  private void setupImages(Entities entities, EntitiesEntity entitiesEntity) {
    ArrayList<ImageMediaEntity> imageMediaEntities = new ArrayList<>();

    if (entities != null) {
      for (ImageMedia imageMedia : entities.getImages()) {
        ImageMediaEntity imageMediaEntity = new ImageMediaEntity();
        ImageSizeEntity imageSizeEntity = new ImageSizeEntity();

        SizeEntity lowSize = new SizeEntity();
        lowSize.setHeight(imageMedia.getSizes().getLow().getHeight());
        lowSize.setWidth(imageMedia.getSizes().getLow().getWidth());
        lowSize.setUrl(imageMedia.getSizes().getLow().getUrl());
        imageSizeEntity.setLow(lowSize);

        SizeEntity mediumSize = new SizeEntity();
        mediumSize.setHeight(imageMedia.getSizes().getMedium().getHeight());
        mediumSize.setWidth(imageMedia.getSizes().getMedium().getWidth());
        mediumSize.setUrl(imageMedia.getSizes().getMedium().getUrl());
        imageSizeEntity.setMedium(mediumSize);

        SizeEntity highSize = new SizeEntity();
        highSize.setHeight(imageMedia.getSizes().getHigh().getHeight());
        highSize.setWidth(imageMedia.getSizes().getHigh().getWidth());
        highSize.setUrl(imageMedia.getSizes().getHigh().getUrl());
        imageSizeEntity.setHigh(highSize);

        imageMediaEntity.setType(imageMedia.getType());
        imageMediaEntity.setSizes(imageSizeEntity);
        imageMediaEntities.add(imageMediaEntity);
      }
    }

    entitiesEntity.setImages(imageMediaEntities);

  }

  private void setupMentions(Entities entities, EntitiesEntity entitiesEntity) {
    ArrayList<MentionsEntity> mentionsEntities = new ArrayList<>();
    if (entities != null) {
      for (Mention entity : entities.getMentions()) {
        MentionsEntity mentionsEntity = new MentionsEntity();
        mentionsEntity.setIdUser(entity.getIdUser());
        mentionsEntity.setUsername(entity.getUsername());
        mentionsEntity.setIndices(entity.getIndices());
        mentionsEntities.add(mentionsEntity);
      }
    }
    entitiesEntity.setMentions(mentionsEntities);
  }

  public Entities setupEntities(EntitiesEntity entitiesEntity) {
    Entities entities = new Entities();
    if (entitiesEntity != null) {
      setupUrls(entitiesEntity, entities);
      setupPolls(entitiesEntity, entities);
      setupStreams(entitiesEntity, entities);
      setupImages(entitiesEntity, entities);
      setupMentions(entitiesEntity, entities);
    }
    return entities;
  }

  private void setupStreams(EntitiesEntity entitiesEntity, Entities entities) {
    ArrayList<StreamIndex> streams = new ArrayList<>();
    for (StreamIndexEntity streamApiEntity : entitiesEntity.getStreams()) {
      StreamIndex streamIndex = new StreamIndex();
      streamIndex.setIdStream(streamApiEntity.getIdStream());
      streamIndex.setStreamTitle(streamApiEntity.getStreamTitle());
      streamIndex.setIndices(streamApiEntity.getIndices());
      streams.add(streamIndex);
    }

    entities.setStreams(streams);
  }

  private void setupPolls(EntitiesEntity entitiesEntity, Entities entities) {
    ArrayList<Poll> polls = new ArrayList<>();

    for (BaseMessagePollEntity baseMessagePollEntity : entitiesEntity.getPolls()) {
      Poll poll = new Poll();
      poll.setIdPoll(baseMessagePollEntity.getIdPoll());
      poll.setIndices(baseMessagePollEntity.getIndices());
      poll.setPollQuestion(baseMessagePollEntity.getPollQuestion());
      polls.add(poll);
    }
    entities.setPolls(polls);
  }

  private void setupUrls(EntitiesEntity entitiesEntity, Entities entities) {
    ArrayList<Url> urls = new ArrayList<>();
    for (UrlEntity urlApiEntity : entitiesEntity.getUrls()) {
      Url url = new Url();
      url.setDisplayUrl(urlApiEntity.getDisplayUrl());
      url.setUrl(urlApiEntity.getUrl());
      url.setIndices(urlApiEntity.getIndices());
      urls.add(url);
    }

    entities.setUrls(urls);
  }

  private void setupImages(EntitiesEntity entitiesEntity, Entities entities) {
    ArrayList<ImageMedia> imageMedias = new ArrayList<>();

    if (entitiesEntity != null) {
      for (ImageMediaEntity imageMediaEntity : entitiesEntity.getImages()) {
        ImageMedia imageMedia = new ImageMedia();
        ImageSize imageSize = new ImageSize();

        Sizes lowSize = new Sizes();
        lowSize.setHeight(imageMediaEntity.getSizes().getLow().getHeight());
        lowSize.setWidth(imageMediaEntity.getSizes().getLow().getWidth());
        lowSize.setUrl(imageMediaEntity.getSizes().getLow().getUrl());
        imageSize.setLow(lowSize);

        Sizes mediumSize = new Sizes();
        mediumSize.setHeight(imageMediaEntity.getSizes().getMedium().getHeight());
        mediumSize.setWidth(imageMediaEntity.getSizes().getMedium().getWidth());
        mediumSize.setUrl(imageMediaEntity.getSizes().getMedium().getUrl());
        imageSize.setMedium(mediumSize);

        Sizes highSize = new Sizes();
        highSize.setHeight(imageMediaEntity.getSizes().getHigh().getHeight());
        highSize.setWidth(imageMediaEntity.getSizes().getHigh().getWidth());
        highSize.setUrl(imageMediaEntity.getSizes().getHigh().getUrl());
        imageSize.setHigh(highSize);

        imageMedia.setType(imageMediaEntity.getType());
        imageMedia.setSizes(imageSize);
        imageMedias.add(imageMedia);
      }
    }

    entities.setImages(imageMedias);

  }

  private void setupMentions(EntitiesEntity entitiesEntity, Entities entities) {
    ArrayList<Mention> mentions = new ArrayList<>();
    if (entitiesEntity != null) {
      for (MentionsEntity entity : entitiesEntity.getMentions()) {
        Mention mention = new Mention();
        mention.setIdUser(entity.getIdUser());
        mention.setUsername(entity.getUsername());
        mention.setIndices(entity.getIndices());
        mentions.add(mention);
      }
    }
    entities.setMentions(mentions);
  }

}
