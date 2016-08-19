package com.shootr.mobile.domain.service.stream;

import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class WatchingStreamServiceTest {

  public static final String CURRENT_USER_ID = "current_user_id";
  public static final String ID_CURRENT_WATCHING_STREAM = "id_current_watching_stream";
  public static final String ID_STREAM = "id_stream";
  @Mock UserRepository localUserRepository;
  @Mock SessionRepository sessionRepository;
  private WatchingStreamService watchingStreamService;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    watchingStreamService =
        new WatchingStreamService(localUserRepository, sessionRepository);
    when(sessionRepository.getCurrentUserId()).thenReturn(CURRENT_USER_ID);
    when(localUserRepository.getUserById(anyString())).thenReturn(user());
  }

  @Test public void shouldSetWatchingTrueStreamWhenUserIsWatchingStream() throws Exception {
    List<StreamSearchResult> streamSearchResults = watchingStreamSearchResults();
    List<StreamSearchResult> spyList = spy(streamSearchResults);

    watchingStreamService.markWatchingStream(streamSearchResults);

    assertTrue(spyList.get(0).isWatching());
  }

  @Test public void shouldSetWatchingFalseStreamWhenUserIsWatchingStream() throws Exception {
    List<StreamSearchResult> streamSearchResults = noWatchingStreamSearchResults();
    List<StreamSearchResult> spyList = spy(streamSearchResults);

    watchingStreamService.markWatchingStream(streamSearchResults);

    assertFalse(spyList.get(0).isWatching());
  }

  private User user() {
    User user = new User();
    user.setIdWatchingStream(ID_CURRENT_WATCHING_STREAM);
    return user;
  }

  private List<StreamSearchResult> watchingStreamSearchResults() {
    List<StreamSearchResult> streams = new ArrayList<>();
    StreamSearchResult streamSearchResult = new StreamSearchResult();
    Stream stream = new Stream();
    stream.setId(ID_CURRENT_WATCHING_STREAM);
    streamSearchResult.setStream(stream);
    streams.add(streamSearchResult);
    return streams;
  }

  private List<StreamSearchResult> noWatchingStreamSearchResults() {
    List<StreamSearchResult> streams = new ArrayList<>();
    StreamSearchResult streamSearchResult = new StreamSearchResult();
    Stream stream = new Stream();
    stream.setId(ID_STREAM);
    streamSearchResult.setStream(stream);
    streams.add(streamSearchResult);
    return streams;
  }
}