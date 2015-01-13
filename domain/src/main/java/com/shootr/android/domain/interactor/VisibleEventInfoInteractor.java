package com.shootr.android.domain.interactor;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventInfo;
import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.repository.SessionRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class VisibleEventInfoInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final SessionRepository sessionRepository;

    @Inject public VisibleEventInfoInteractor(InteractorHandler interactorHandler, SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.sessionRepository = sessionRepository;
    }

    public void obtainEventInfo() {
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        //TODO callbacks :'(
        //TODO ¡¡¡Cómo llamo al repositorio de forma asíncrona varias veces para pillar con y sin conexión??? OMG! #panic
        Watch visibleEventWatch = currentEventWatch();
        Event currentVisibleEvent = eventForWatch(visibleEventWatch);
        List<Watch> followingWatches = followingWatching(currentVisibleEvent);

        EventInfo eventInfo = buildEventInfo(currentVisibleEvent, visibleEventWatch, followingWatches);
        interactorHandler.sendUiMessage(eventInfo);
    }

    private Watch currentEventWatch() {
        //TODO
        Watch watch = new Watch();
        watch.setUser(sessionRepository.getCurrentUser());
        watch.setWatching(true);
        watch.setUserStatus("Watching, dude");
        return watch;
    }

    private Event eventForWatch(Watch currentEventWatch) {
        //TODO
        Event event = new Event();
        event.setStartDate(new Date(1421524533));
        event.setTitle("Sevilla-Beti");
        return event;
    }

    private List<Watch> followingWatching(Event currentVisibleEvent) {
        //TODO
        User dummyUser = new User();
        dummyUser.setUsername("Dummy");
        dummyUser.setPhoto("https://camo.githubusercontent.com/9e39276ad39fe3cda7ac61dd0f1560dc5ad1ab95/68747470733a2f2f646c2e64726f70626f7875736572636f6e74656e742e636f6d2f752f3737343835392f4769744875622d5265706f732f7465737464756d6d792f63726173687465737464756d6d792e6a7067");

        Watch watch1 = new Watch();
        watch1.setUser(dummyUser);
        watch1.setWatching(true);
        watch1.setUserStatus("Watching, dude");

        List<Watch> watches = new ArrayList<>();
        watches.add(watch1);
        watches.add(watch1);
        watches.add(watch1);
        return watches;
    }

    private EventInfo buildEventInfo(Event currentVisibleEvent, Watch visibleEventWatch, List<Watch> followingWatches) {
        return new EventInfo.Builder().event(currentVisibleEvent)
          .currentUserWatch(visibleEventWatch)
          .watchers(followingWatches)
          .build();
    }
}
