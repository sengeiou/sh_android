package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.mapper.ContributorEntityMapper;
import com.shootr.mobile.data.repository.datasource.contributor.ContributorDataSource;
import com.shootr.mobile.domain.Contributor;
import com.shootr.mobile.domain.repository.ContributorRepository;
import java.util.List;
import javax.inject.Inject;

public class SyncContributorRepository implements ContributorRepository {

    private final ContributorDataSource contributorDataSource;
    private final ContributorEntityMapper mapper;

    @Inject public SyncContributorRepository(ContributorDataSource contributorDataSource, ContributorEntityMapper mapper) {
        this.contributorDataSource = contributorDataSource;
        this.mapper = mapper;
    }

    @Override public List<Contributor> getContributors(String idStream) {
        return mapper.transform(contributorDataSource.getContributors(idStream));
    }

    @Override public List<Contributor> getContributorsWithUsers(String idStream) {
        return mapper.transform(contributorDataSource.getContributorsWithUser(idStream));
    }
}
