package com.shootr.mobile.domain.repository.poll;

import com.shootr.mobile.domain.Poll;

public interface InternalPollRepository extends PollRepository {

  void putPoll(Poll poll);

}
