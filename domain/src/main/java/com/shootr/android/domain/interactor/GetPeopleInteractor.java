package com.shootr.android.domain.interactor;

import com.shootr.android.domain.UserList;

public interface GetPeopleInteractor extends Interactor<UserList> {

    void obtainPeople();

}
