package com.shootr.mobile.data.repository.datasource.contributor;

import com.shootr.mobile.data.entity.ContributorEntity;
import com.shootr.mobile.domain.exception.ContributorNumberStreamException;
import java.util.List;

public interface ContributorDataSource {

    List<ContributorEntity> getContributors(String idStream);

    List<ContributorEntity> getContributorsWithUser(String idStream);

    void addContributor(String idStream, String idUser) throws ContributorNumberStreamException;

    void removeContributor(String idStream, String idUser);
}
