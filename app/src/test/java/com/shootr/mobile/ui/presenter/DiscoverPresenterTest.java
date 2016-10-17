package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.discover.GetDiscoveredInteractor;
import com.shootr.mobile.domain.interactor.discover.GetLocalDiscoveredInteractor;
import com.shootr.mobile.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.GetFavoriteStreamsInteractor;
import com.shootr.mobile.domain.interactor.stream.RemoveFromFavoritesInteractor;
import com.shootr.mobile.domain.model.discover.Discovered;
import com.shootr.mobile.domain.model.discover.DiscoveredType;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.DiscoveredModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.mappers.DiscoveredModelMapper;
import com.shootr.mobile.ui.model.mappers.ShotModelMapper;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.views.DiscoverView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DiscoverPresenterTest {

  public static final String ID_STREAM = "idStream";
  private static final String CURRENT_USER_ID = "currentUserId";

  @Mock DiscoverView discoverView;
  @Mock GetDiscoveredInteractor getDiscoveredInteractor;
  @Mock GetLocalDiscoveredInteractor getLocalDiscoveredInteractor;
  @Mock AddToFavoritesInteractor addToFavoritesInteractor;
  @Mock RemoveFromFavoritesInteractor removeFromFavoritesInteractor;
  @Mock MarkNiceShotInteractor markNiceShotInteractor;
  @Mock UnmarkNiceShotInteractor unmarkNiceShotInteractor;
  @Mock ErrorMessageFactory errorMessageFactory;
  @Mock SessionRepository sessionRepository;
  @Mock GetFavoriteStreamsInteractor getFavoriteStreamsInteractor;

  private DiscoveredModelMapper discoveredModelMapper;
  private DiscoverPresenter presenter;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    discoveredModelMapper = new DiscoveredModelMapper(new StreamModelMapper(sessionRepository),
        new ShotModelMapper());
    presenter = new DiscoverPresenter(getDiscoveredInteractor, getLocalDiscoveredInteractor,
        getFavoriteStreamsInteractor, addToFavoritesInteractor,
        removeFromFavoritesInteractor, markNiceShotInteractor, unmarkNiceShotInteractor,
        discoveredModelMapper, errorMessageFactory);
    when(sessionRepository.getCurrentUserId()).thenReturn(CURRENT_USER_ID);
  }

  @Test public void shouldRenderDiscoverListWhenInitializes() throws Exception {
    setupGetDiscoverInteractor();

    presenter.initialize(discoverView);

    verify(discoverView).renderDiscover(discoveredModels());
  }

  private void setupGetDiscoverInteractor() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<List<Discovered>> callback =
            (Interactor.Callback<List<Discovered>>) invocation.getArguments()[0];
        callback.onLoaded(discovereds());
        return null;
      }
    }).when(getDiscoveredInteractor)
        .getDiscovered(any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
  }

  private List<Discovered> discovereds() {
    return Collections.singletonList(discovered());
  }

  private Discovered discovered() {
    Discovered discovered = new Discovered();
    discovered.setStream(stream());
    discovered.setFaved(false);
    discovered.setIdDiscover("idDiscover");
    discovered.setRelevance(1L);
    discovered.setType(DiscoveredType.STREAM);
    return discovered;
  }

  private Stream stream() {
    Stream stream = new Stream();
    stream.setId(ID_STREAM);
    stream.setTitle("title");
    stream.setAuthorId("idAuthor");
    stream.setAuthorUsername("usernameAuthor");
    stream.setDescription("description");
    stream.setHistoricWatchers(1L);
    stream.setMediaCount(0);
    return stream;
  }

  private List<DiscoveredModel> discoveredModels() {
    return Collections.singletonList(discoveredModel());
  }

  private DiscoveredModel discoveredModel() {
    DiscoveredModel discovered = new DiscoveredModel();
    discovered.setStreamModel(streamModel());
    discovered.setHasBeenFaved(false);
    discovered.setIdDiscover("idDiscover");
    discovered.setRelevance(1L);
    discovered.setType(DiscoveredType.STREAM);
    return discovered;
  }

  private StreamModel streamModel() {
    StreamModel streamModel = new StreamModel();
    streamModel.setIdStream(ID_STREAM);
    streamModel.setTitle("title");
    streamModel.setAuthorId("idAuthor");
    streamModel.setAuthorUsername("usernameAuthor");
    streamModel.setDescription("description");
    streamModel.setHistoricWatchers(1L);
    streamModel.setMediaCount(0);
    streamModel.setReadWriteMode(0);
    return streamModel;
  }
}
