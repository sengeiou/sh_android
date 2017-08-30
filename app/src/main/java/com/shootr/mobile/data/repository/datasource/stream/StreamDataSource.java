package com.shootr.mobile.data.repository.datasource.stream;

import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.domain.model.stream.StreamUpdateParameters;
import java.util.List;

public interface StreamDataSource {

  StreamEntity getStreamById(String idStream, String[] types);

  List<StreamEntity> getStreamByIds(List<String> streamIds, String[] types);

  StreamEntity putStream(StreamEntity streamEntity, Boolean notifyStreamMessage);

  StreamEntity putStream(StreamEntity streamEntity);

  StreamEntity createStream(StreamEntity streamEntity);

  StreamEntity updateStream(StreamUpdateParameters streamUpdateParameters);

  List<StreamEntity> putStreams(List<StreamEntity> streams);

  List<StreamEntity> getStreamsListing(String idUser, String[] types);

  void shareStream(String idStream);

  void removeStream(String idStream);

  void restoreStream(String idStream);

  StreamEntity getBlogStream(String country, String language);

  StreamEntity getHelpStream(String country, String language);

  String getLastTimeFilteredStream(String idStream);

  void putLastTimeFiltered(String idStream, String lastTimeFiltered);

  void mute(String idStream);

  void unMute(String idStream);
}
