package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.api.service.ShotApiService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

public class RemoteNiceShotRepositoryTest {

  private static final String SHOT_ID = "idShot";
  @Mock ShotApiService shotApiService;

  private RemoteNiceShotRepository repository;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    repository = new RemoteNiceShotRepository(shotApiService);
  }

  @Test public void shouldMarkNiceInApiWhenCallMark() throws Exception {
    repository.mark(SHOT_ID);

    verify(shotApiService).markNice(anyString());
  }

  @Test public void shouldUnmarkNiceInApiWhenCallUnmark() throws Exception {
    repository.unmark(SHOT_ID);

    verify(shotApiService).unmarkNice(anyString());
  }
}
