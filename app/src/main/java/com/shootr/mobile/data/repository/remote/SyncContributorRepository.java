package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.ContributorEntity;
import com.shootr.mobile.data.mapper.ContributorEntityMapper;
import com.shootr.mobile.data.repository.datasource.contributor.ContributorDataSource;
import com.shootr.mobile.domain.Contributor;
import com.shootr.mobile.domain.exception.ContributorNumberStreamException;
import com.shootr.mobile.domain.repository.ContributorRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import java.util.List;
import javax.inject.Inject;

public class SyncContributorRepository implements ContributorRepository {

    private final ContributorDataSource remoteContributorDataSource;
    private final ContributorDataSource localContributorDataSource;
    private final ContributorEntityMapper mapper;

    @Inject
    public SyncContributorRepository(@Remote ContributorDataSource remoteContributorDataSource,
        @Local ContributorDataSource localContributorDataSource, ContributorEntityMapper mapper) {
        this.remoteContributorDataSource = remoteContributorDataSource;
        this.localContributorDataSource = localContributorDataSource;
        this.mapper = mapper;
    }

    @Override public List<Contributor> getContributors(String idStream) {
        List<ContributorEntity> contributors = remoteContributorDataSource.getContributors(idStream);
        localContributorDataSource.clearContributors(idStream);
        localContributorDataSource.putContributors(contributors);
        return mapper.transform(contributors);
    }

    @Override public List<Contributor> getContributorsWithUsers(String idStream) {
        List<ContributorEntity> contributorsWithUser =
            remoteContributorDataSource.getContributorsWithUser(idStream);
        localContributorDataSource.clearContributors(idStream);
        localContributorDataSource.putContributors(contributorsWithUser);
        return mapper.transform(contributorsWithUser);
    }

    @Override public void addContributor(String idStream, String idUser) throws ContributorNumberStreamException {
        remoteContributorDataSource.addContributor(idStream, idUser);
    }

    @Override public void removeContributor(String idStream, String idUser) {
        remoteContributorDataSource.removeContributor(idStream, idUser);
    }
}
