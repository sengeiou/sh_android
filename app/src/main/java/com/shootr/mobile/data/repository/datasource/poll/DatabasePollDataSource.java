package com.shootr.mobile.data.repository.datasource.poll;

import com.shootr.mobile.data.entity.PollEntity;
import com.shootr.mobile.data.entity.PollOptionEntity;
import com.shootr.mobile.db.manager.PollManager;
import com.shootr.mobile.db.manager.PollOptionManager;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class DatabasePollDataSource implements PollDataSource {

  private final PollManager pollManager;
  private final PollOptionManager pollOptionManager;

  @Inject
  public DatabasePollDataSource(PollManager pollManager, PollOptionManager pollOptionManager) {
    this.pollManager = pollManager;
    this.pollOptionManager = pollOptionManager;
  }

  @Override public List<PollEntity> getPolls(String idStream) {
    PollEntity poll = pollManager.getPoll(idStream);
    if (poll != null) {
      List<PollOptionEntity> pollOptions = pollOptionManager.getPollOptions(poll.getIdPoll());
      poll.setPollOptions(pollOptions);
    }
    return Collections.singletonList(poll);
  }

  @Override public void putPoll(PollEntity poll) {
    if (pollOptionManager.putPollOptions(poll.getIdPoll(), poll.getPollOptions())) {
      pollManager.putPoll(poll);
    }
  }

  @Override public PollEntity getPollById(String idPoll) {
    PollEntity poll = pollManager.getPollbyIdPoll(idPoll);
    if (poll != null) {
      List<PollOptionEntity> pollOptions = pollOptionManager.getPollOptions(poll.getIdPoll());
      poll.setPollOptions(pollOptions);
    }
    return poll;
  }

  @Override public void removePolls(String idStream) {
    pollManager.removePolls(idStream);
  }

  @Override public PollEntity vote(String idPoll, String idPollOption, boolean isPrivateVote) {
    throw new IllegalArgumentException("method not implemented");
  }

  @Override public void sharePoll(String idPoll) {
    throw new IllegalArgumentException("method not implemented");
  }
}
