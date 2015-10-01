package com.shootr.android.data.repository.datasource.user;

import com.shootr.android.data.entity.SuggestedPeopleEntity;
import com.shootr.android.db.manager.SuggestedPeopleManager;
import java.util.List;
import javax.inject.Inject;

public class DatabaseSuggestedPeopleDataSource implements SuggestedPeopleDataSource {

    private final SuggestedPeopleManager suggestedPeopleManager;

    @Inject public DatabaseSuggestedPeopleDataSource(SuggestedPeopleManager suggestedPeopleManager) {
        this.suggestedPeopleManager = suggestedPeopleManager;
    }

    @Override public List<SuggestedPeopleEntity> getSuggestedPeople() {
        return suggestedPeopleManager.getSuggestedPeople();
    }

    @Override public void putSuggestedPeople(List<SuggestedPeopleEntity> suggestedPeople) {
        suggestedPeopleManager.saveSuggestedPeople(suggestedPeople);
    }
}
