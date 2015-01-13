package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.MatchEntity;
import com.shootr.android.domain.Event;
import javax.inject.Inject;

public class EventEntityMapper {

    @Inject public EventEntityMapper() {
    }

    public Event transform(MatchEntity matchEntity) {
        Event event = new Event();
        event.setId(matchEntity.getIdMatch());
        event.setTitle(matchEntity.getLocalTeamName()+"-"+matchEntity.getVisitorTeamName());
        event.setStartDate(matchEntity.getMatchDate());
        //TODO endDate
        return event;
    }

}
