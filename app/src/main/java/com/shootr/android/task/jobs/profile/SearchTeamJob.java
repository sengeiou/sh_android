package com.shootr.android.task.jobs.profile;

import android.app.Application;
import com.path.android.jobqueue.Params;
import com.path.android.jobqueue.network.NetworkUtil;
import com.shootr.android.data.entity.TeamEntity;
import com.shootr.android.service.ShootrService;
import com.shootr.android.task.events.profile.SearchTeamResultEvent;
import com.shootr.android.task.jobs.ShootrBaseJob;
import com.shootr.android.ui.model.TeamModel;
import com.shootr.android.ui.model.mappers.TeamModelMapper;
import com.squareup.otto.Bus;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;

public class SearchTeamJob extends ShootrBaseJob<SearchTeamResultEvent> {

    public static final int PRIORITY = 7;
    private final ShootrService service;
    private final TeamModelMapper teamModelMapper;

    private String queryText;

    @Inject protected SearchTeamJob(Application application, Bus bus, NetworkUtil networkUtil, ShootrService service,
      TeamModelMapper teamModelMapper) {
        super(new Params(PRIORITY), application, bus, networkUtil);
        this.service = service;
        this.teamModelMapper = teamModelMapper;
    }

    public void init(String queryText) {
        this.queryText = queryText;
    }

    @Override protected void run() throws IOException, SQLException {
        List<TeamEntity> resultTeams = service.searchTeams(queryText);

        List<TeamModel> teamModels = teamModelMapper.transform(resultTeams);

        postSuccessfulEvent(new SearchTeamResultEvent(teamModels));
    }

    @Override protected boolean isNetworkRequired() {
        return true;
    }
}
