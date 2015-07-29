package com.shootr.android.db.manager;

import android.database.sqlite.SQLiteOpenHelper;
import javax.inject.Inject;

public class SuggestedPeopleManager extends AbstractManager {

    @Inject public SuggestedPeopleManager(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }
}
