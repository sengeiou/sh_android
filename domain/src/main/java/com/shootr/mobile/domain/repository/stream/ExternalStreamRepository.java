package com.shootr.mobile.domain.repository.stream;

import com.shootr.mobile.domain.model.Bootstrapping;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamUpdateParameters;

public interface ExternalStreamRepository extends StreamRepository {

  Stream putStream(Stream stream, boolean notify);

  void shareStream(String idStream);

  Stream getBlogStream(String country, String language);

  Stream getHelpStream(String country, String language);

  Stream updateStream(StreamUpdateParameters streamUpdateParameters);

  Bootstrapping getSocket();

}
