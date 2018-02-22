package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.executor.RxPostExecutionThread;
import com.shootr.mobile.domain.executor.ThreadExecutor;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SocketRepository;
import io.reactivex.Observable;
import javax.inject.Inject;

public class ConnectSocketInteractor extends UseCase {

  private final SocketRepository socketRepository;

  @Inject ConnectSocketInteractor(ThreadExecutor threadExecutor,
      RxPostExecutionThread postExecutionThread, @Remote SocketRepository socketRepository) {
    super(threadExecutor, postExecutionThread);
    this.socketRepository = socketRepository;
  }

  @Override Observable buildUseCaseObservable(Object socketAddress) {
    return socketRepository.connect((String) socketAddress);
  }
}
