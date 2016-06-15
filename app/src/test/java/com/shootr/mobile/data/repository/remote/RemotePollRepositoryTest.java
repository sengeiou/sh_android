package com.shootr.mobile.data.repository.remote;


import android.support.annotation.NonNull;
import com.shootr.mobile.data.entity.PollEntity;
import com.shootr.mobile.data.entity.PollOptionEntity;
import com.shootr.mobile.data.mapper.PollEntityMapper;
import com.shootr.mobile.data.repository.datasource.poll.PollDataSource;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RemotePollRepositoryTest {

  public static final String ID_STREAM = "id_stream";
  public static final String ID_POLL = "id_poll";
  public static final String ID_POLL_OPTION = "id_poll_option";

  private RemotePollRepository repository;
  @Mock PollDataSource servicePollDataSource;
  @Mock PollDataSource databasePollDataSource;
  @Mock PollEntityMapper pollEntityMapper;
  @Captor ArgumentCaptor<List<PollEntity>> pollsCaptor;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    repository = new RemotePollRepository(servicePollDataSource, databasePollDataSource, pollEntityMapper);
  }

  @Test
  public void shouldGetPollsFromRemoteDataSourceWhenGetPollByIdStream() throws Exception {
    repository.getPollByIdStream(ID_STREAM);

    verify(servicePollDataSource).getPolls(ID_STREAM);
  }

  @Test
  public void shouldReturnEmptyListIfNoPollsReceivedWhenGetPollByIdStream() throws Exception {
    repository.getPollByIdStream(ID_STREAM);

    verify(pollEntityMapper).transform(pollsCaptor.capture());
    assertTrue(pollsCaptor.getValue().isEmpty());
  }

  @Test
  public void shouldGetPollFromLocalWhenGetPollByIdStream() throws Exception {
    when(servicePollDataSource.getPolls(ID_STREAM)).thenReturn(polls());

    repository.getPollByIdStream(ID_STREAM);

    verify(databasePollDataSource).getPollById(ID_POLL);
  }

  @Test
  public void shouldRemoveLocalPollsByIdStreamWhenGetPollByIdStream() throws Exception {
    when(servicePollDataSource.getPolls(ID_STREAM)).thenReturn(polls());

    repository.getPollByIdStream(ID_STREAM);

    verify(databasePollDataSource).removePolls(ID_STREAM);
  }

  @Test
  public void shouldPutPollWhenGetPollByIdStream() throws Exception {
    when(servicePollDataSource.getPolls(ID_STREAM)).thenReturn(polls());

    repository.getPollByIdStream(ID_STREAM);

    verify(databasePollDataSource).putPoll(any(PollEntity.class));
  }

  @Test
  public void shouldSavePollInLocalWhenVote() throws Exception {
    PollEntity pollEntity = pollEntity();
    when(servicePollDataSource.vote(ID_POLL, ID_POLL_OPTION)).thenReturn(pollEntity);

    repository.vote(ID_POLL, ID_POLL_OPTION);

    verify(databasePollDataSource).putPoll(pollEntity);
  }

  private List<PollEntity> polls() {
    PollEntity pollEntity = pollEntity();
    return Collections.singletonList(pollEntity);
  }

  @NonNull private PollEntity pollEntity() {
    PollEntity pollEntity = new PollEntity();
    pollEntity.setIdPoll(ID_POLL);
    pollEntity.setHasVoted(1L);
    pollEntity.setIdStream(ID_STREAM);
    pollEntity.setPollOptions(Collections.<PollOptionEntity>emptyList());
    pollEntity.setPublished(1L);
    pollEntity.setQuestion("question");
    pollEntity.setStatus("status");
    return pollEntity;
  }
}
