package com.shootr.android.task.jobs.info;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.info.SearchMatchResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.MatchModel;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SearchMatchJob extends ShootrBaseJob<SearchMatchResultEvent> {

    public static final int PRIORITY = 7;
    private ShootrService service;

    @Inject protected SearchMatchJob(Application application, Bus bus, NetworkUtil networkUtil, ShootrService service) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
    }

    @Override protected void run() throws SQLException, IOException, Exception {
        Thread.sleep(2000);
        postSuccessfulEvent(new SearchMatchResultEvent(getMockResults()));
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

    public List<MatchModel> getMockResults() {
        List<MatchModel> mockResults = new ArrayList<>();
        MatchModel mm1 = new MatchModel();
        mm1.setIdMatch(1L);
        mm1.setTitle("Barcelona-Sevilla");
        mm1.setDatetime("20/11");
        MatchModel mm2 = new MatchModel();
        mm2.setIdMatch(2L);
        mm2.setTitle("Sevruposki-Palatinesko");
        mm2.setDatetime("29/11");
        MatchModel mm3 = new MatchModel();
        mm3.setIdMatch(3L);
        mm3.setTitle("La Palma-Sevilla");
        mm3.setDatetime("11/12");
        mockResults.add(mm1);
        mockResults.add(mm2);
        mockResults.add(mm3);
        return mockResults;
    }
}
