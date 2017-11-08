package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.stream.OnBoarding;
import com.shootr.mobile.domain.repository.onBoarding.ExternalOnBoardingRepository;
import com.shootr.mobile.domain.utils.LocaleProvider;
import java.util.List;
import javax.inject.Inject;

public class GetOnBoardingStreamInteractor implements Interactor {

  private final ExternalOnBoardingRepository externalOnBoardingRepository;
  private final LocaleProvider localeProvider;
  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;

  private ErrorCallback errorCallback;
  private Callback<List<OnBoarding>> callback;

  private String type;

  @Inject public GetOnBoardingStreamInteractor(ExternalOnBoardingRepository externalOnBoardingRepository,
      LocaleProvider localeProvider, InteractorHandler interactorHandler, PostExecutionThread postExecutionThread) {
    this.externalOnBoardingRepository = externalOnBoardingRepository;
    this.localeProvider = localeProvider;
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
  }

  public void loadOnBoardingStreams(String type, Callback<List<OnBoarding>> callback, ErrorCallback errorCallback) {
    this.callback = callback;
    this.errorCallback = errorCallback;
    this.type = type;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    loadOnBoardingStreams();
  }

  private void loadOnBoardingStreams() {
    try {
      String locale = getLocale();
      notifyResult(externalOnBoardingRepository.getOnBoardingStreams(type, locale));
    } catch (ServerCommunicationException error) {
            notifyError(error);
    }
  }

  private String getLocale() {
    String locale = localeProvider.getLocale();
    if (locale == null || locale.isEmpty()) {
      locale = localeProvider.getCountry();
    }
    return locale;
  }

  private void notifyResult(final List<OnBoarding> suggestedStream) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(suggestedStream);
      }
    });
  }

  private void notifyError(final ShootrException error) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        errorCallback.onError(error);
      }
    });
  }
}
