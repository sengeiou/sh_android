package com.shootr.mobile.data.repository.datasource.stream;

import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.data.entity.LandingStreamsEntity;
import com.shootr.mobile.data.entity.socket.BootstrapingEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.repository.datasource.SyncableDataSource;
import com.shootr.mobile.domain.exception.InvalidYoutubeVideoUrlException;
import com.shootr.mobile.domain.model.stream.StreamUpdateParameters;
import java.util.List;

public interface StreamDataSource extends SyncableDataSource<FollowEntity> {

  StreamEntity getStreamById(String idStream, String[] types);

  List<StreamEntity> getStreamByIds(List<String> streamIds, String[] types);

  StreamEntity putStream(StreamEntity streamEntity, Boolean notifyStreamMessage);

  StreamEntity putStream(StreamEntity streamEntity);

  StreamEntity createStream(StreamEntity streamEntity) throws InvalidYoutubeVideoUrlException;

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

  void unmute(String idStream);

  void hide(String idStream);

  void follow(String idStream);

  void unfollow(String idStream);

  void putFailedFollow(FollowEntity followEntity);

  void deleteFailedFollows();

  long getConnectionTimes(String idStream);

  void storeConnection(String idStream, long connections);

  LandingStreamsEntity getLandingStreams();

  BootstrapingEntity getSocket();
}
