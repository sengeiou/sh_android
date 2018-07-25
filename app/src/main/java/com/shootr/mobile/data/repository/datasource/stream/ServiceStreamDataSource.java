package com.shootr.mobile.data.repository.datasource.stream;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.exception.ErrorInfo;
import com.shootr.mobile.data.api.service.StreamApiService;
import com.shootr.mobile.data.api.service.UtilsApiService;
import com.shootr.mobile.data.entity.socket.BootstrapingEntity;
import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.data.entity.LandingStreamsEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.domain.exception.InvalidYoutubeVideoUrlException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.model.stream.StreamUpdateParameters;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceStreamDataSource implements StreamDataSource {

  public static final int MAX_NUMBER_OF_LISTING_STREAMS = 100;
  private final StreamApiService streamApiService;
  private final UtilsApiService utilsApiService;

  @Inject public ServiceStreamDataSource(StreamApiService streamApiService,
      UtilsApiService utilsApiService) {
    this.streamApiService = streamApiService;
    this.utilsApiService = utilsApiService;
  }

  @Override public StreamEntity getStreamById(String idStream, String[] types) {
    try {
      return streamApiService.getStream(idStream, types);
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public List<StreamEntity> getStreamByIds(List<String> streamIds, String[] types) {
    try {
      return streamApiService.getStreams(streamIds, types);
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public StreamEntity putStream(StreamEntity streamEntity, Boolean notifyMessage) {
    throw new RuntimeException("Method not implemented yet!");
  }

  @Override public StreamEntity putStream(StreamEntity streamEntity) {
    return putStream(streamEntity, false);
  }

  @Override public StreamEntity createStream(StreamEntity streamEntity)
      throws InvalidYoutubeVideoUrlException {
    try {
      return streamApiService.createStream(streamEntity);
    } catch (ApiException apiException) {
      if (ErrorInfo.InvalidYoutubeVideoUrlException == apiException.getErrorInfo()) {
        throw new InvalidYoutubeVideoUrlException(apiException);
      } else {
        throw new ServerCommunicationException(apiException);
      }
    } catch (IOException error) {
      throw new ServerCommunicationException(error);
    }
  }

  @Override public StreamEntity updateStream(StreamUpdateParameters streamUpdateParameters) {
    try {
      return streamApiService.updateStream(streamUpdateParameters);
    } catch (ApiException apiException) {
      if (ErrorInfo.InvalidYoutubeVideoUrlException == apiException.getErrorInfo()) {
        throw new InvalidYoutubeVideoUrlException(apiException);
      } else {
        throw new ServerCommunicationException(apiException);
      }
    } catch (IOException error) {
      throw new ServerCommunicationException(error);
    }
  }

  @Override public List<StreamEntity> putStreams(List<StreamEntity> streams) {
    throw new RuntimeException("Method not implemented yet!");
  }

  @Override public List<StreamEntity> getStreamsListing(String idUser, String[] types) {
    try {
      return streamApiService.getStreamListing(idUser, MAX_NUMBER_OF_LISTING_STREAMS, types);
    } catch (ApiException | IOException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public void shareStream(String idStream) {
    try {
      streamApiService.shareStream(idStream);
    } catch (ApiException | IOException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public void removeStream(String idStream) {
    try {
      streamApiService.removeStream(idStream);
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public void restoreStream(String idStream) {
    try {
      streamApiService.restoreStream(idStream);
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public StreamEntity getBlogStream(String country, String language) {
    try {
      return streamApiService.getBlogStream(country, language);
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public StreamEntity getHelpStream(String country, String language) {
    try {
      return streamApiService.getHelpStream(country, language);
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public String getLastTimeFilteredStream(String idStream) {
    throw new RuntimeException("Method not implemented yet!");
  }

  @Override public void putLastTimeFiltered(String idStream, String lastTimeFiltered) {
    throw new RuntimeException("Method not implemented yet!");
  }

  @Override public void mute(String idStream) {
    try {
      streamApiService.mute(idStream);
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public void unmute(String idStream) {
    try {
      streamApiService.unMute(idStream);
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public void hide(String idStream) {
    try {
      streamApiService.hide(idStream);
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public void follow(String idStream) {
    try {
      streamApiService.followStream(idStream);
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public void unfollow(String idStream) {
    try {
      streamApiService.unFollowStream(idStream);
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public void putFailedFollow(FollowEntity followEntity) {
    throw new RuntimeException("Method not implemented yet!");
  }

  @Override public void deleteFailedFollows() {
    throw new RuntimeException("Method not implemented yet!");
  }

  @Override public long getConnectionTimes(String idStream) {
    throw new RuntimeException("Method not implemented yet!");
  }

  @Override public void storeConnection(String idStream, long connections) {
    throw new RuntimeException("Method not implemented yet!");
  }

  @Override public LandingStreamsEntity getLandingStreams() {
    try {
      return streamApiService.getLandingStreams();
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public BootstrapingEntity getSocket() {
    try {
      BootstrapingEntity socketEntity = utilsApiService.getBootSocket();
      return socketEntity;
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public List<FollowEntity> getEntitiesNotSynchronized() {
    throw new RuntimeException("Method not implemented yet!");
  }
}
