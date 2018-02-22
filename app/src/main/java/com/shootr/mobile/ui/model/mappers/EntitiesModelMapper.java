package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.ImageMedia;
import com.shootr.mobile.domain.model.shot.Entities;
import com.shootr.mobile.domain.model.shot.Mention;
import com.shootr.mobile.domain.model.shot.Poll;
import com.shootr.mobile.domain.model.shot.StreamIndex;
import com.shootr.mobile.domain.model.shot.Url;
import com.shootr.mobile.ui.model.BaseMessagePollModel;
import com.shootr.mobile.ui.model.EntitiesModel;
import com.shootr.mobile.ui.model.ImageMediaModel;
import com.shootr.mobile.ui.model.ImageSizeModel;
import com.shootr.mobile.ui.model.MentionModel;
import com.shootr.mobile.ui.model.SizesModel;
import com.shootr.mobile.ui.model.StreamIndexModel;
import com.shootr.mobile.ui.model.UrlModel;
import java.util.ArrayList;
import javax.inject.Inject;

public class EntitiesModelMapper {

  @Inject public EntitiesModelMapper() {
  }

  public EntitiesModel setupEntities(Entities entities) {
    EntitiesModel entitiesModel = new EntitiesModel();
    if (entities != null) {
      setupUrls(entities, entitiesModel);
      setupPolls(entities, entitiesModel);
      setupStreams(entities, entitiesModel);
      setupImages(entities, entitiesModel);
      setupMentions(entities, entitiesModel);
    }
    return entitiesModel;
  }

  private void setupStreams(Entities entities, EntitiesModel entitiesModel) {
    ArrayList<StreamIndexModel> streamsIndexModels = new ArrayList<>();
    for (StreamIndex stream : entities.getStreams()) {
      StreamIndexModel streamIndexModel = new StreamIndexModel();
      streamIndexModel.setStreamTitle(stream.getStreamTitle());
      streamIndexModel.setIdStream(stream.getIdStream());
      streamIndexModel.setIndices(stream.getIndices());
      streamsIndexModels.add(streamIndexModel);
    }

    entitiesModel.setStreams(streamsIndexModels);
  }

  private void setupPolls(Entities entities, EntitiesModel entitiesModel) {
    ArrayList<BaseMessagePollModel> baseMessagePollModels = new ArrayList<>();
    for (Poll poll : entities.getPolls()) {
      BaseMessagePollModel baseMessagePollModel = new BaseMessagePollModel();
      baseMessagePollModel.setPollQuestion(poll.getPollQuestion());
      baseMessagePollModel.setIdPoll(poll.getIdPoll());
      baseMessagePollModel.setIndices(poll.getIndices());
      baseMessagePollModels.add(baseMessagePollModel);
    }

    entitiesModel.setPolls(baseMessagePollModels);
  }

  private void setupUrls(Entities entities, EntitiesModel entitiesModel) {
    ArrayList<UrlModel> urlModels = new ArrayList<>();
    for (Url url : entities.getUrls()) {
      UrlModel urlModel = new UrlModel();
      urlModel.setDisplayUrl(url.getDisplayUrl());
      urlModel.setUrl(url.getUrl());
      urlModel.setIndices(url.getIndices());
      urlModels.add(urlModel);
    }

    entitiesModel.setUrls(urlModels);
  }

  private void setupImages(Entities entities, EntitiesModel entitiesModel) {
    ArrayList<ImageMediaModel> imageMediaModels = new ArrayList<>();

    if (entities != null && entities.getImages() != null) {
      for (ImageMedia imageMedia : entities.getImages()) {
        ImageMediaModel imageMediaModel = new ImageMediaModel();
        ImageSizeModel imageSizeModel = new ImageSizeModel();

        SizesModel lowSize = new SizesModel();
        lowSize.setHeight(imageMedia.getSizes().getLow().getHeight());
        lowSize.setWidth(imageMedia.getSizes().getLow().getWidth());
        lowSize.setUrl(imageMedia.getSizes().getLow().getUrl());
        imageSizeModel.setLow(lowSize);

        SizesModel mediumSize = new SizesModel();
        mediumSize.setHeight(imageMedia.getSizes().getMedium().getHeight());
        mediumSize.setWidth(imageMedia.getSizes().getMedium().getWidth());
        mediumSize.setUrl(imageMedia.getSizes().getMedium().getUrl());
        imageSizeModel.setMedium(mediumSize);

        SizesModel highSize = new SizesModel();
        highSize.setHeight(imageMedia.getSizes().getHigh().getHeight());
        highSize.setWidth(imageMedia.getSizes().getHigh().getWidth());
        highSize.setUrl(imageMedia.getSizes().getHigh().getUrl());
        imageSizeModel.setHigh(highSize);

        imageMediaModel.setType(imageMedia.getType());
        imageMediaModel.setSizes(imageSizeModel);
        imageMediaModels.add(imageMediaModel);
      }
    }

    entitiesModel.setImages(imageMediaModels);
  }

  private void setupMentions(Entities entities, EntitiesModel entitiesModel) {
    ArrayList<MentionModel> mentionModels = new ArrayList<>();
    if (entities != null && entities.getMentions() != null) {
      for (Mention mention : entities.getMentions()) {
        MentionModel mentionModel = new MentionModel();
        mentionModel.setIdUser(mention.getIdUser());
        mentionModel.setUsername(mention.getUsername());
        mentionModel.setIndices(mention.getIndices());
        mentionModels.add(mentionModel);
      }
    }
    entitiesModel.setMentions(mentionModels);
  }
}
