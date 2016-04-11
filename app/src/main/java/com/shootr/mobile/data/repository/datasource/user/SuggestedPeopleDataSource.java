package com.shootr.mobile.data.repository.datasource.user;

import com.shootr.mobile.data.entity.SuggestedPeopleEntity;
import java.util.List;

public interface SuggestedPeopleDataSource {

    List<SuggestedPeopleEntity> getSuggestedPeople(String locale);

    void putSuggestedPeople(List<SuggestedPeopleEntity> suggestedPeople);
}
