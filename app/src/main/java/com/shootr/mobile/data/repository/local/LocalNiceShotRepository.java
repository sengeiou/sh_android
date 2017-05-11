package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.domain.exception.NiceAlreadyMarkedException;
import com.shootr.mobile.domain.exception.NiceNotMarkedException;
import com.shootr.mobile.domain.repository.nice.NiceShotRepository;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class LocalNiceShotRepository implements NiceShotRepository {


  @Inject public LocalNiceShotRepository() {
  }

  @Override public void mark(String idShot) throws NiceAlreadyMarkedException {
    throw new IllegalArgumentException("no implementation");
  }

  @Override public void unmark(String idShot) throws NiceNotMarkedException {
    throw new IllegalArgumentException("no implementation");
  }

}
