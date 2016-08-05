package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.model.stream.StreamSearchResultList;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.ShootrValidationException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.StreamsListInteractor;
import com.shootr.mobile.ui.views.WelcomePageView;
import com.shootr.mobile.util.ErrorMessageFactory;
import javax.inject.Inject;

public class WelcomePagePresenter implements Presenter {

    private final StreamsListInteractor streamsListInteractor;
    private final ErrorMessageFactory errorMessageFactory;

    private WelcomePageView welcomePageView;
    private boolean streamsLoaded = false;
    private boolean getStartedClicked = false;

    @Inject
    public WelcomePagePresenter(StreamsListInteractor streamsListInteractor, ErrorMessageFactory errorMessageFactory) {
        this.streamsListInteractor = streamsListInteractor;
        this.errorMessageFactory = errorMessageFactory;
    }

    protected void setView(WelcomePageView welcomePageView) {
        this.welcomePageView = welcomePageView;
    }

    public void initialize(WelcomePageView welcomePageView) {
        setView(welcomePageView);
        loadDefaultStreams();
    }

    private void loadDefaultStreams() {
        streamsListInteractor.loadStreams(new Interactor.Callback<StreamSearchResultList>() {
            @Override public void onLoaded(StreamSearchResultList streamSearchResultList) {
                streamsLoaded = true;
                if (getStartedClicked) {
                    welcomePageView.goToStreamList();
                }
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                showViewError(error);
            }
        });
    }

    public void getStartedClicked() {
        getStartedClicked = true;
        welcomePageView.hideGetStarted();
        if (streamsLoaded) {
            welcomePageView.goToStreamList();
        } else {
            welcomePageView.showSpinner();
        }
    }

    private void showViewError(ShootrException error) {
        String errorMessage;
        if (error instanceof ShootrValidationException) {
            String errorCode = ((ShootrValidationException) error).getErrorCode();
            errorMessage = errorMessageFactory.getMessageForCode(errorCode);
        } else {
            errorMessage = errorMessageFactory.getMessageForError(error);
        }
        welcomePageView.showError(errorMessage);
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
