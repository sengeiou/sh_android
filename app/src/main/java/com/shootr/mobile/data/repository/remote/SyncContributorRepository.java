package com.shootr.mobile.data.repository.remote;

import android.support.annotation.NonNull;
import com.shootr.mobile.data.entity.ContributorEntity;
import com.shootr.mobile.data.mapper.ContributorEntityMapper;
import com.shootr.mobile.data.repository.datasource.contributor.ContributorDataSource;
import com.shootr.mobile.data.repository.datasource.user.UserDataSource;
import com.shootr.mobile.domain.exception.ContributorNumberStreamException;
import com.shootr.mobile.domain.model.user.Contributor;
import com.shootr.mobile.domain.repository.ContributorRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SyncContributorRepository implements ContributorRepository {

    private final ContributorDataSource remoteContributorDataSource;
    private final ContributorDataSource localContributorDataSource;
    private final UserDataSource localUserDataSource;
    private final SessionRepository sessionRepository;
    private final ContributorEntityMapper mapper;


    @Inject
    public SyncContributorRepository(@Remote ContributorDataSource remoteContributorDataSource,
        @Local ContributorDataSource localContributorDataSource, @Local UserDataSource localUserDataSource,
        SessionRepository sessionRepository, ContributorEntityMapper mapper) {
        this.remoteContributorDataSource = remoteContributorDataSource;
        this.localContributorDataSource = localContributorDataSource;
        this.localUserDataSource = localUserDataSource;
        this.sessionRepository = sessionRepository;
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
        return storeUserRelationship(contributorsWithUser);
    }

    @NonNull private ArrayList<Contributor> storeUserRelationship(
        List<ContributorEntity> contributorsWithUser) {
        ArrayList<Contributor> contributors = new ArrayList<>(mapper.transform(contributorsWithUser));
        for (Contributor contributor : contributors) {
            contributor.getUser().setFollower(isFollower(contributor.getIdUser()));
            contributor.getUser().setFollowing(isFollowing(contributor.getIdUser()));
            contributor.getUser().setMe(isMe(contributor));
        }
        return contributors;
    }

    @Override public void addContributor(String idStream, String idUser) throws ContributorNumberStreamException {
        remoteContributorDataSource.addContributor(idStream, idUser);
    }

    @Override public void removeContributor(String idStream, String idUser) {
        remoteContributorDataSource.removeContributor(idStream, idUser);
    }

    private boolean isFollower(String userId) {
        return localUserDataSource.isFollower(sessionRepository.getCurrentUserId(), userId);
    }

    private boolean isFollowing(String userId) {
        return localUserDataSource.isFollowing(sessionRepository.getCurrentUserId(), userId);
    }

    private boolean isMe(Contributor contributor) {
        return sessionRepository.getCurrentUserId().equals(contributor.getIdUser());
    }
}
