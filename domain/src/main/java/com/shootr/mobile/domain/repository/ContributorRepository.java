package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.Contributor;
import java.util.List;

public interface ContributorRepository {

    List<Contributor> getContributors(String idStream);

    List<Contributor> getContributorsWithUsers(String idStream);

    void addContributor(String idStream, String idUser);
}
