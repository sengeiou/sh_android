package com.shootr.mobile.data.repository.datasource.contributor;

import com.shootr.mobile.data.entity.ContributorEntity;
import com.shootr.mobile.db.manager.ContributorManager;
import com.shootr.mobile.domain.exception.ContributorNumberStreamException;
import java.util.List;
import javax.inject.Inject;

public class DatabaseContributorDatasource implements ContributorDataSource {

  private final ContributorManager contributorManager;

  @Inject public DatabaseContributorDatasource(ContributorManager contributorManager) {
    this.contributorManager = contributorManager;
  }

  @Override public List<ContributorEntity> getContributors(String idStream) {
    return contributorManager.getContributorsByStream(idStream);
  }

  @Override public List<ContributorEntity> getContributorsWithUser(String idStream) {
    throw new IllegalArgumentException("no implementation");
  }

  @Override public void addContributor(String idStream, String idUser)
      throws ContributorNumberStreamException {
    throw new IllegalArgumentException("no implementation");
  }

  @Override public void removeContributor(String idStream, String idUser) {
    throw new IllegalArgumentException("no implementation");
  }

  @Override public void putContributors(List<ContributorEntity> contributors) {
    contributorManager.putContributors(contributors);
  }

  @Override public void clearContributors(String idStream) {
    contributorManager.clearContributors(idStream);
  }
}
