package com.shootr.mobile.data.repository.datasource.poll;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.exception.ErrorInfo;
import com.shootr.mobile.data.api.service.PollApiService;
import com.shootr.mobile.data.entity.PollEntity;
import com.shootr.mobile.domain.exception.PollDeletedException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.UserCannotVoteRequestException;
import com.shootr.mobile.domain.exception.UserHasVotedRequestException;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServicePollDataSource implements PollDataSource {

  private final PollApiService pollApiService;

  @Inject public ServicePollDataSource(PollApiService pollApiService) {
    this.pollApiService = pollApiService;
  }

  @Override public List<PollEntity> getPolls(String idStream)
      throws UserCannotVoteRequestException, UserHasVotedRequestException,
      PollDeletedException {
    try {
      return pollApiService.getPollByIdStream(idStream);
    } catch (IOException e) {
      throw new ServerCommunicationException(e);
    } catch (ApiException e) {
      if (e.getErrorInfo().code() == 7001) {
        throw new UserCannotVoteRequestException(e);
      } else if (e.getErrorInfo().code() == 7002) {
        throw new UserHasVotedRequestException(e);
      } else if (ErrorInfo.ResourceNotFoundException == e.getErrorInfo()) {
        throw new PollDeletedException(e);
      } else {
        throw new ServerCommunicationException(e);
      }
    }
  }

  @Override public void putPoll(PollEntity poll) {
    throw new IllegalArgumentException("method not implemented");
  }

  @Override public PollEntity getPollById(String idPoll)
      throws UserCannotVoteRequestException, UserHasVotedRequestException,
      PollDeletedException {
    try {
      return pollApiService.getPollById(idPoll);
    } catch (IOException e) {
      throw new ServerCommunicationException(e);
    } catch (ApiException e) {
      if (e.getErrorInfo().code() == 7001) {
        throw new UserCannotVoteRequestException(e);
      } else if (e.getErrorInfo().code() == 7002) {
        throw new UserHasVotedRequestException(e);
      } else if (ErrorInfo.ResourceNotFoundException == e.getErrorInfo()) {
        throw new PollDeletedException(e);
      } else {
        throw new ServerCommunicationException(e);
      }
    }
  }

  @Override public void removePolls(String idStream) {
    throw new IllegalArgumentException("method not implemented");
  }

  @Override public PollEntity vote(String idPoll, String idPollOption)
      throws UserCannotVoteRequestException, UserHasVotedRequestException {
    try {
      return pollApiService.vote(idPoll, idPollOption);
    } catch (IOException e) {
      throw new ServerCommunicationException(e);
    } catch (ApiException e) {
      if (e.getErrorInfo().code() == 7001) {
        throw new UserCannotVoteRequestException(e);
      } else if (e.getErrorInfo().code() == 7002) {
        throw new UserHasVotedRequestException(e);
      } else {
        throw new ServerCommunicationException(e);
      }
    }
  }
}
