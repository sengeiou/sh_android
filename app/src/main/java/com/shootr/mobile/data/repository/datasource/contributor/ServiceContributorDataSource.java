package com.shootr.mobile.data.repository.datasource.contributor;

import com.shootr.mobile.data.api.exception.ApiException;
import com.shootr.mobile.data.api.exception.ErrorInfo;
import com.shootr.mobile.data.api.service.ContributorApiService;
import com.shootr.mobile.data.entity.ContributorEntity;
import com.shootr.mobile.domain.exception.ContributorNumberStreamException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;

public class ServiceContributorDataSource implements ContributorDataSource {

    private final ContributorApiService contributorApiService;

    @Inject public ServiceContributorDataSource(ContributorApiService contributorApiService) {
        this.contributorApiService = contributorApiService;
    }

    @Override public List<ContributorEntity> getContributors(String idStream) {
        try {
            return contributorApiService.getContributors(idStream);
        } catch (ApiException | IOException cause) {
            throw new ServerCommunicationException(cause);
        }
    }

    @Override public List<ContributorEntity> getContributorsWithUser(String idStream) {
        try {
            return contributorApiService.getContributorsWithUser(idStream);
        } catch (ApiException | IOException cause) {
            throw new ServerCommunicationException(cause);
        }
    }

    @Override public void addContributor(String idStream, String idUser) throws ContributorNumberStreamException {
        try {
            contributorApiService.addContributor(idStream, idUser);
        } catch (ApiException apiException) {
            if (ErrorInfo.ContributorNumberStreamException == apiException.getErrorInfo()) {
                throw new ContributorNumberStreamException(apiException);
            } else {
                throw new ServerCommunicationException(apiException);
            }
        } catch (IOException networkError) {
            throw new ServerCommunicationException(networkError);
        }
    }

    @Override public void removeContributor(String idStream, String idUser) {
        try {
            contributorApiService.removeContributor(idStream, idUser);
        } catch (ApiException | IOException cause) {
            throw new ServerCommunicationException(cause);
        }
    }

    @Override public void putContributors(List<ContributorEntity> contributors) {
        throw new IllegalArgumentException("no implementation");
    }

    @Override public void clearContributors(String idStream) {
        throw new IllegalArgumentException("no implementation");
    }
}
