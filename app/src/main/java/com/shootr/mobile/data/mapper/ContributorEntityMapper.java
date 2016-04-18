package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.ContributorEntity;
import com.shootr.mobile.domain.Contributor;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ContributorEntityMapper {

    private final UserEntityMapper userEntityMapper;

    @Inject public ContributorEntityMapper(UserEntityMapper userEntityMapper) {
        this.userEntityMapper = userEntityMapper;
    }

    public Contributor transform(ContributorEntity contributorEntity) {
        Contributor contributor = new Contributor();

        contributor.setIdUser(contributorEntity.getIdUser());
        contributor.setIdContributor(contributorEntity.getIdContributor());
        contributor.setIdStream(contributorEntity.getIdStream());
        if (contributorEntity.getUser() != null) {
            contributor.setUser(userEntityMapper.transform(contributorEntity.getUser()));
        }
        return contributor;
    }

    public List<Contributor> transform(List<ContributorEntity> contributorEntityList) {
        List<Contributor> contributors = new ArrayList<>(contributorEntityList.size());

        for (ContributorEntity contributorEntity : contributorEntityList) {
            contributors.add(transform(contributorEntity));
        }

        return contributors;
    }
}
