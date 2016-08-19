package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.mapper.ContributorEntityMapper;
import com.shootr.mobile.data.repository.datasource.contributor.ContributorDataSource;
import com.shootr.mobile.domain.model.user.Contributor;
import com.shootr.mobile.domain.exception.ContributorNumberStreamException;
import com.shootr.mobile.domain.repository.ContributorRepository;
import com.shootr.mobile.domain.repository.Local;
import java.util.List;
import javax.inject.Inject;

public class LocalContributorRepository implements ContributorRepository {

  private final ContributorDataSource databaseContributorDatasource;
  private final ContributorEntityMapper contributorEntityMapper;

  @Inject
  public LocalContributorRepository(@Local ContributorDataSource databaseContributorDatasource,
      ContributorEntityMapper contributorEntityMapper) {
    this.databaseContributorDatasource = databaseContributorDatasource;
    this.contributorEntityMapper = contributorEntityMapper;
  }

  @Override public List<Contributor> getContributors(String idStream) {
    return contributorEntityMapper.transform(
        databaseContributorDatasource.getContributors(idStream));
  }

  @Override public List<Contributor> getContributorsWithUsers(String idStream) {
    throw new IllegalArgumentException("no implementation");
  }

  @Override public void addContributor(String idStream, String idUser)
      throws ContributorNumberStreamException {
    throw new IllegalArgumentException("no implementation");
  }

  @Override public void removeContributor(String idStream, String idUser) {
    throw new IllegalArgumentException("no implementation");
  }
}
