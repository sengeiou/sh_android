package com.shootr.mobile.data.repository.datasource.user;

import com.shootr.mobile.data.entity.SuggestedPeopleEntity;
import com.shootr.mobile.db.manager.SuggestedPeopleManager;
import java.util.List;
import javax.inject.Inject;

public class DatabaseSuggestedPeopleDataSource implements SuggestedPeopleDataSource {

    private final SuggestedPeopleManager suggestedPeopleManager;

    @Inject public DatabaseSuggestedPeopleDataSource(SuggestedPeopleManager suggestedPeopleManager) {
        this.suggestedPeopleManager = suggestedPeopleManager;
    }

    @Override public List<SuggestedPeopleEntity> getSuggestedPeople(String locale) {
        return suggestedPeopleManager.getSuggestedPeople();
    }

    @Override public void putSuggestedPeople(List<SuggestedPeopleEntity> suggestedPeople) {
        suggestedPeopleManager.saveSuggestedPeople(suggestedPeople);
    }
}
