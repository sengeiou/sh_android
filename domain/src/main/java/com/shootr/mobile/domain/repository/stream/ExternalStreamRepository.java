package com.shootr.mobile.domain.repository.stream;

import com.shootr.mobile.domain.model.stream.Stream;

public interface ExternalStreamRepository extends StreamRepository {

  Stream putStream(Stream stream, boolean notify, boolean notifyStreamMessage);

  void shareStream(String idStream);

  Stream getBlogStream(String country, String language);

  Stream getHelpStream(String country, String language);

}
