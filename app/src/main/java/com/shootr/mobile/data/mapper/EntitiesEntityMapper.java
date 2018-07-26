package com.shootr.mobile.data.mapper;

import android.support.annotation.NonNull;
import com.shootr.mobile.data.entity.BackgroundEntity;
import com.shootr.mobile.data.entity.BaseMessagePollEntity;
import com.shootr.mobile.data.entity.CardEntity;
import com.shootr.mobile.data.entity.EntitiesEntity;
import com.shootr.mobile.data.entity.ImageMediaEntity;
import com.shootr.mobile.data.entity.ImageSizeEntity;
import com.shootr.mobile.data.entity.MentionsEntity;
import com.shootr.mobile.data.entity.PromotedEntity;
import com.shootr.mobile.data.entity.SizeEntity;
import com.shootr.mobile.data.entity.StreamIndexEntity;
import com.shootr.mobile.data.entity.UrlEntity;
import com.shootr.mobile.domain.model.ImageMedia;
import com.shootr.mobile.domain.model.ImageSize;
import com.shootr.mobile.domain.model.Sizes;
import com.shootr.mobile.domain.model.shot.Background;
import com.shootr.mobile.domain.model.shot.Card;
import com.shootr.mobile.domain.model.shot.Entities;
import com.shootr.mobile.domain.model.shot.Mention;
import com.shootr.mobile.domain.model.shot.Poll;
import com.shootr.mobile.domain.model.shot.Promoted;
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
      setupCards(entities, entitiesEntity);
      setupPromoted(entities, entitiesEntity);
    }
    return entitiesEntity;
  }

  private void setupPolls(Entities entities, EntitiesEntity entitiesEntity) {
    ArrayList<BaseMessagePollEntity> baseMessagePollEntities = new ArrayList<>();
    if (entities != null && entities.getPolls() != null) {
      for (Poll poll : entities.getPolls()) {
        BaseMessagePollEntity baseMessagePollEntity = new BaseMessagePollEntity();
        baseMessagePollEntity.setIndices(poll.getIndices());
        baseMessagePollEntity.setIdPoll(poll.getIdPoll());
        baseMessagePollEntity.setPollQuestion(poll.getPollQuestion());
        baseMessagePollEntities.add(baseMessagePollEntity);
      }
    }
    entitiesEntity.setPolls(baseMessagePollEntities);
  }

  private void setupStreams(Entities entities, EntitiesEntity entitiesEntity) {
    ArrayList<StreamIndexEntity> streamIndexEntities = new ArrayList<>();
    if (entities != null && entities.getStreams() != null) {
      for (StreamIndex stream : entities.getStreams()) {
        StreamIndexEntity streamIndexEntity = new StreamIndexEntity();
        streamIndexEntity.setIndices(stream.getIndices());
        streamIndexEntity.setIdStream(stream.getIdStream());
        streamIndexEntity.setStreamTitle(stream.getStreamTitle());
        streamIndexEntities.add(streamIndexEntity);
      }
    }
    entitiesEntity.setStreams(streamIndexEntities);
  }

  private void setupUrls(Entities shot, EntitiesEntity entitiesEntity) {
    ArrayList<UrlEntity> urlEntities = new ArrayList<>();
    if (shot != null && shot.getUrls() != null) {
      for (Url urlApiEntity : shot.getUrls()) {
        UrlEntity urlEntity = transformUrl(urlApiEntity);
        urlEntities.add(urlEntity);
      }
    }

    entitiesEntity.setUrls(urlEntities);
  }

  @NonNull private UrlEntity transformUrl(Url urlApiEntity) {
    UrlEntity urlEntity = new UrlEntity();
    urlEntity.setDisplayUrl(urlApiEntity.getDisplayUrl());
    urlEntity.setUrl(urlApiEntity.getUrl());
    urlEntity.setIndices(urlApiEntity.getIndices());
    return urlEntity;
  }

  private void setupImages(Entities entities, EntitiesEntity entitiesEntity) {
    ArrayList<ImageMediaEntity> imageMediaEntities = new ArrayList<>();

    if (entities != null && entities.getImages() != null) {
      for (ImageMedia imageMedia : entities.getImages()) {
        ImageMediaEntity imageMediaEntity = tranformImages(imageMedia);
        imageMediaEntities.add(imageMediaEntity);
      }
    }

    entitiesEntity.setImages(imageMediaEntities);

  }

  @NonNull private ImageMediaEntity tranformImages(ImageMedia imageMedia) {
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
    return imageMediaEntity;
  }

  private void setupMentions(Entities entities, EntitiesEntity entitiesEntity) {
    ArrayList<MentionsEntity> mentionsEntities = new ArrayList<>();
    if (entities != null && entities.getMentions() != null) {
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

  private void setupCards(Entities entities, EntitiesEntity entitiesEntity) {
    ArrayList<CardEntity> cardEntities = new ArrayList<>();
    if (entities != null && entities.getCards() != null) {
      for (Card card : entities.getCards()) {
        CardEntity cardEntity = new CardEntity();

        cardEntity.setType(card.getType());
        cardEntity.setTitle(card.getTitle());
        cardEntity.setDuration(card.getDuration());
        cardEntity.setImage(tranformImages(card.getImage()));
        cardEntity.setLink(transformUrl(card.getLink()));

        cardEntities.add(cardEntity);
      }
    }
    entitiesEntity.setCards(cardEntities);
  }

  private void setupPromoted(Entities entities, EntitiesEntity entitiesEntity) {
    if (entities.getPromoted() != null) {
      PromotedEntity promotedEntity = new PromotedEntity();
      promotedEntity.setCurrency(entities.getPromoted().getCurrency());
      promotedEntity.setDisplayPrice(entities.getPromoted().getDisplayPrice());
      promotedEntity.setPrice(entities.getPromoted().getPrice());

      BackgroundEntity backgroundEntity = new BackgroundEntity();
      backgroundEntity.setAngle(entities.getPromoted().getBackground().getAngle());
      backgroundEntity.setColors(entities.getPromoted().getBackground().getColors());
      backgroundEntity.setType(entities.getPromoted().getBackground().getType());
      promotedEntity.setBackground(backgroundEntity);

      entitiesEntity.setPromotedEntity(promotedEntity);
    }
  }

  public Entities setupEntities(EntitiesEntity entitiesEntity) {
    Entities entities = new Entities();
    if (entitiesEntity != null) {
      setupUrls(entitiesEntity, entities);
      setupPolls(entitiesEntity, entities);
      setupStreams(entitiesEntity, entities);
      setupImages(entitiesEntity, entities);
      setupMentions(entitiesEntity, entities);
      setupCards(entitiesEntity, entities);
      setupPromoted(entitiesEntity, entities);
    }
    return entities;
  }

  private void setupStreams(EntitiesEntity entitiesEntity, Entities entities) {
    ArrayList<StreamIndex> streams = new ArrayList<>();
    if (entitiesEntity != null && entitiesEntity.getStreams() != null) {
      for (StreamIndexEntity streamApiEntity : entitiesEntity.getStreams()) {
        StreamIndex streamIndex = new StreamIndex();
        streamIndex.setIdStream(streamApiEntity.getIdStream());
        streamIndex.setStreamTitle(streamApiEntity.getStreamTitle());
        streamIndex.setIndices(streamApiEntity.getIndices());
        streams.add(streamIndex);
      }
    }

    entities.setStreams(streams);
  }

  private void setupPolls(EntitiesEntity entitiesEntity, Entities entities) {
    ArrayList<Poll> polls = new ArrayList<>();
    if (entitiesEntity != null && entitiesEntity.getPolls() != null) {
      for (BaseMessagePollEntity baseMessagePollEntity : entitiesEntity.getPolls()) {
        Poll poll = new Poll();
        poll.setIdPoll(baseMessagePollEntity.getIdPoll());
        poll.setIndices(baseMessagePollEntity.getIndices());
        poll.setPollQuestion(baseMessagePollEntity.getPollQuestion());
        polls.add(poll);
      }
    }
    entities.setPolls(polls);
  }

  private void setupUrls(EntitiesEntity entitiesEntity, Entities entities) {
    ArrayList<Url> urls = new ArrayList<>();
    if (entitiesEntity.getUrls() != null) {
      for (UrlEntity urlApiEntity : entitiesEntity.getUrls()) {
        Url url = transformUrl(urlApiEntity);
        urls.add(url);
      }
    }

    entities.setUrls(urls);
  }

  @NonNull private Url transformUrl(UrlEntity urlApiEntity) {
    Url url = new Url();
    url.setDisplayUrl(urlApiEntity.getDisplayUrl());
    url.setUrl(urlApiEntity.getUrl());
    url.setIndices(urlApiEntity.getIndices());
    return url;
  }

  private void setupImages(EntitiesEntity entitiesEntity, Entities entities) {
    ArrayList<ImageMedia> imageMedias = new ArrayList<>();

    if (entitiesEntity != null && entitiesEntity.getImages() != null) {
      for (ImageMediaEntity imageMediaEntity : entitiesEntity.getImages()) {
        ImageMedia imageMedia = transformImage(imageMediaEntity);
        imageMedias.add(imageMedia);
      }
    }

    entities.setImages(imageMedias);

  }

  @NonNull private ImageMedia transformImage(ImageMediaEntity imageMediaEntity) {
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
    return imageMedia;
  }

  private void setupMentions(EntitiesEntity entitiesEntity, Entities entities) {
    ArrayList<Mention> mentions = new ArrayList<>();
    if (entitiesEntity != null && entitiesEntity.getMentions() != null) {
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

  private void setupCards(EntitiesEntity entitiesEntity, Entities entities) {
    ArrayList<Card> cards = new ArrayList<>();
    if (entitiesEntity != null && entitiesEntity.getCards() != null) {
      for (CardEntity cardEntity : entitiesEntity.getCards()) {
        Card card = new Card();

        card.setType(cardEntity.getType());
        card.setTitle(cardEntity.getTitle());
        card.setDuration(cardEntity.getDuration());
        card.setImage(transformImage(cardEntity.getImage()));
        card.setLink(transformUrl(cardEntity.getLink()));

        cards.add(card);
      }
    }
    entities.setCards(cards);
  }

  private void setupPromoted(EntitiesEntity entitiesEntity, Entities entities) {
    if (entitiesEntity.getPromotedEntity() != null) {
      Promoted promoted = new Promoted();
      promoted.setCurrency(entitiesEntity.getPromotedEntity().getCurrency());
      promoted.setDisplayPrice(entitiesEntity.getPromotedEntity().getDisplayPrice());
      promoted.setPrice(entitiesEntity.getPromotedEntity().getPrice());

      Background backgroundEntity = new Background();
      backgroundEntity.setAngle(entitiesEntity.getPromotedEntity().getBackground().getAngle());
      backgroundEntity.setColors(entitiesEntity.getPromotedEntity().getBackground().getColors());
      backgroundEntity.setType(entitiesEntity.getPromotedEntity().getBackground().getType());

      promoted.setBackground(backgroundEntity);

      entities.setPromoted(promoted);
    }
  }
}
