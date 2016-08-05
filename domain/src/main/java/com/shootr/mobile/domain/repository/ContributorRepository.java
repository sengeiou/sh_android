package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.model.user.Contributor;
import com.shootr.mobile.domain.exception.ContributorNumberStreamException;
import java.util.List;

public interface ContributorRepository {

    List<Contributor> getContributors(String idStream);

    List<Contributor> getContributorsWithUsers(String idStream);

    void addContributor(String idStream, String idUser) throws ContributorNumberStreamException;

    void removeContributor(String idStream, String idUser);
}
