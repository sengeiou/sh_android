package com.shootr.mobile.data.repository.datasource.user;

import com.shootr.mobile.data.entity.SuggestedPeopleEntity;
import java.util.List;

public interface SuggestedPeopleDataSource {

    List<SuggestedPeopleEntity> getSuggestedPeople();

    void putSuggestedPeople(List<SuggestedPeopleEntity> suggestedPeople);
}
