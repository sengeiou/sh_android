package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.entity.SuggestedPeopleEntity;
import java.util.List;

public interface SuggestedPeopleDataSource {

    List<SuggestedPeopleEntity> getSuggestedPeople(String currentUserId);

    void putSuggestedPeople(List<SuggestedPeopleEntity> suggestedPeople);
}
