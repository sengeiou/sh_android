package com.shootr.mobile.data.repository.datasource.stream;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.service.StreamApiService;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceStreamDataSource implements StreamDataSource {

  public static final int MAX_NUMBER_OF_LISTING_STREAMS = 100;
  private final StreamApiService streamApiService;

  @Inject public ServiceStreamDataSource(StreamApiService streamApiService) {
    this.streamApiService = streamApiService;
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
    try {
      if (streamEntity.getIdStream() == null) {
        return streamApiService.createStream(streamEntity);
      } else {
        return streamApiService.updateStream(streamEntity, notifyMessage);
      }
    } catch (IOException | ApiException e) {
      throw new ServerCommunicationException(e);
    }
  }

  @Override public StreamEntity putStream(StreamEntity streamEntity) {
    return putStream(streamEntity, false);
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
}
