package com.shootr.mobile.data.repository.datasource.contributor;

import com.shootr.mobile.data.entity.ContributorEntity;
import java.util.List;

public interface ContributorDataSource {

    List<ContributorEntity> getContributors(String idStream);

    List<ContributorEntity> getContributorsWithUser(String idStream);
}
