package com.shootr.android.task.jobs.info;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.db.mappers.MatchMapper;
import com.shootr.android.db.objects.MatchEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.info.SearchMatchResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.model.mappers.MatchModelMapper;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SearchMatchJob extends ShootrBaseJob<SearchMatchResultEvent> {

    public static final int PRIORITY = 7;
    private ShootrService service;
    private MatchModelMapper matchModelMapper;
    private String queryText;

    @Inject protected SearchMatchJob(Application application, Bus bus, NetworkUtil networkUtil, ShootrService service, MatchModelMapper matchModelMapper) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
        this.matchModelMapper = matchModelMapper;
    }

    public void init(String queryText) {
        this.queryText = queryText;
    }

    @Override protected void run() throws IOException {
        List<MatchEntity> resultMatches = service.searchMatches(queryText);
        List<MatchModel> matchModels = matchModelMapper.toMatchModel(resultMatches);
        postSuccessfulEvent(new SearchMatchResultEvent(matchModels));
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }

    @Override protected void createDatabase() {
        /* no-op */
    }

    @Override protected void setDatabaseToManagers(SQLiteDatabase db) {
        /* no-op */
    }
}
